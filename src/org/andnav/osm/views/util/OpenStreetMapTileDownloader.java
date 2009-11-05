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
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.andnav.osm.util.constants.OpenStreetMapConstants;
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

	// ===========================================================
	// Fields
	// ===========================================================

	protected Set<String> mPending = Collections.synchronizedSet(new HashSet<String>());
	protected Context mCtx;
	protected OpenStreetMapTileFilesystemProvider mMapTileFSProvider;
	protected ExecutorService mThreadPool = Executors.newFixedThreadPool(5);

	// ===========================================================
	// Constructors
	// ===========================================================

	public OpenStreetMapTileDownloader(final Context ctx, final OpenStreetMapTileFilesystemProvider aMapTileFSProvider){
		this.mCtx = ctx;
		this.mMapTileFSProvider = aMapTileFSProvider;
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
	public void getRemoteImageAsync(final String aURLString, final Handler callback) {
		this.mThreadPool.execute(new Runnable(){
			@Override
			public void run() {
				InputStream in = null;
				OutputStream out = null;

				try {
					if(DEBUGMODE)
						Log.i(DEBUGTAG, "Downloading Maptile from url: " + aURLString);


					in = new BufferedInputStream(new URL(aURLString).openStream(), StreamUtils.IO_BUFFER_SIZE);

					final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
					out = new BufferedOutputStream(dataStream, StreamUtils.IO_BUFFER_SIZE);
					StreamUtils.copy(in, out);
					out.flush();

					final byte[] data = dataStream.toByteArray();
						OpenStreetMapTileDownloader.this.mMapTileFSProvider.saveFile(aURLString, data);
						if (DEBUGMODE)
							Log.i(DEBUGTAG, "Maptile saved to: " + aURLString);

					final Message successMessage = Message.obtain(callback, MAPTILEDOWNLOADER_SUCCESS_ID);
					successMessage.sendToTarget();
				} catch (Exception e) {
					final Message failMessage = Message.obtain(callback, MAPTILEDOWNLOADER_FAIL_ID);
					failMessage.sendToTarget();
					if(DEBUGMODE)
						Log.e(DEBUGTAG, "Error Downloading MapTile. Exception: " + e.getClass().getSimpleName(), e);
				} finally {
					StreamUtils.closeStream(in);
					StreamUtils.closeStream(out);
					OpenStreetMapTileDownloader.this.mPending.remove(aURLString);
				}
			}
		});
	}

	public void requestMapTileAsync(final String aURLString, final Handler callback) {
		if(this.mPending.contains(aURLString))
			return;

		this.mPending.add(aURLString);
		getRemoteImageAsync(aURLString, callback);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
