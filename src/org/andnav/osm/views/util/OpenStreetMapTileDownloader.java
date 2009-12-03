/* 
This file is part of OpenSatNav.

    OpenSatNav is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    OpenSatNav is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with OpenSatNav.  If not, see <http://www.gnu.org/licenses/>.
*/
// Created by plusminus on 21:31:36 - 25.09.2008
package org.andnav.osm.views.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.andnav.osm.util.constants.OpenStreetMapConstants;
import org.andnav.osm.views.util.OpenStreetMapTileFilesystemProvider.TileMetaData;
import org.andnav.osm.views.util.constants.OpenStreetMapViewConstants;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class OpenStreetMapTileDownloader implements OpenStreetMapConstants, OpenStreetMapViewConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final int MAPTILEDOWNLOADER_SUCCESS_ID = 0;
	public static final int MAPTILEDOWNLOADER_FAIL_ID = MAPTILEDOWNLOADER_SUCCESS_ID + 1;

	private static final String ETAG_HEADER = "ETag";
	private static final String IF_NONE_MATCH_HEADER = "If-None-Match";
 	// ===========================================================
	// Fields
	// ===========================================================

	protected Set<String> mPending = Collections.synchronizedSet(new HashSet<String>());
	protected Context mCtx;
	protected OpenStreetMapTileFilesystemProvider mMapTileFSProvider;
	protected OpenStreetMapTileCache mMapTileCache;
	protected ExecutorService mThreadPool = Executors.newFixedThreadPool(5);

	// ===========================================================
	// Constructors
	// ===========================================================

	public OpenStreetMapTileDownloader(final Context ctx, final OpenStreetMapTileFilesystemProvider aMapTileFSProvider,
						final OpenStreetMapTileCache aMapTileCache){
		this.mCtx = ctx;
		this.mMapTileFSProvider = aMapTileFSProvider;
		this.mMapTileCache = aMapTileCache;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	/** Sets the Child-ImageView of this to the URL passed. */
	public void getRemoteImageAsync(final String aURLString, final Handler callback, final TileMetaData tileMetaData) {
		this.mThreadPool.execute(new Runnable(){
			@Override
			public void run() {
				InputStream in = null;
				OutputStream out = null;

				try {
					if(DEBUGMODE)
						Log.i(DEBUGTAG, "Downloading Maptile from url: " + aURLString);

					URL url = new URL(aURLString);

					HttpURLConnection conn = (HttpURLConnection)url.openConnection();	// doesn't actually open connection?
					if (tileMetaData != null) {
						Date ifModifiedSince = tileMetaData.getDateAdded();
						if (ifModifiedSince != null) {
							if (DEBUGMODE)
								Log.d(DEBUGTAG, "Adding ifModifiedSince header: " + ifModifiedSince);
							conn.setIfModifiedSince(ifModifiedSince.getTime());							
						}
						String etag = tileMetaData.getEtag();
						if (etag != null) {
							if (DEBUGMODE)
								Log.d(DEBUGTAG, "Adding If-None-Match header: " + etag);
							conn.addRequestProperty(IF_NONE_MATCH_HEADER, etag);							
						}
					}
					
					in = new BufferedInputStream(conn.getInputStream(), StreamUtils.IO_BUFFER_SIZE);

					final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
					out = new BufferedOutputStream(dataStream, StreamUtils.IO_BUFFER_SIZE);
					StreamUtils.copy(in, out);
					out.flush();

					final byte[] data = dataStream.toByteArray();
					
					if (DEBUGMODE)
						Log.d(DEBUGTAG, "Maptile " + aURLString + " got response: " + conn.getResponseMessage());
					
					switch (conn.getResponseCode()) {
						case HttpURLConnection.HTTP_OK:
							final TileMetaData metadata = new TileMetaData(conn.getHeaderField(ETAG_HEADER));
							OpenStreetMapTileDownloader.this.mMapTileFSProvider.saveFile(aURLString, metadata, data);
							OpenStreetMapTileDownloader.this.mMapTileCache.clearTile(aURLString);
							if (DEBUGMODE)
								Log.d(DEBUGTAG, "Maptile saved to: " + aURLString);

							final Message successMessage = Message.obtain(callback, MAPTILEDOWNLOADER_SUCCESS_ID);
							successMessage.sendToTarget();
							break;
						case HttpURLConnection.HTTP_NOT_MODIFIED:
							if (DEBUGMODE)
								Log.d(DEBUGTAG, "Maptile " + aURLString + " not modified from stored version");
							OpenStreetMapTileDownloader.this.mMapTileFSProvider.updateFile(aURLString);
							break;
						default:
							throw new RuntimeException("Bad HTTP response code: " + conn.getResponseCode() +
									" msg: " + conn.getResponseMessage() + " getting tile: " + aURLString);
					}
				} catch (Exception e) {
					final Message failMessage = Message.obtain(callback, MAPTILEDOWNLOADER_FAIL_ID);
					failMessage.sendToTarget();
					Log.e(DEBUGTAG, "Error Downloading MapTile. Exception: " + e.getClass().getSimpleName(), e);
				} finally {
					StreamUtils.closeStream(in);
					StreamUtils.closeStream(out);
				}
				OpenStreetMapTileDownloader.this.mPending.remove(aURLString);
			}
		});
	}

	public void requestMapTileAsync(final String aURLString, final Handler callback, final TileMetaData metadata) {
		if(this.mPending.contains(aURLString))
			return;

		this.mPending.add(aURLString);
		getRemoteImageAsync(aURLString, callback, metadata);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
