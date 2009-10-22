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
// Created by plusminus on 21:46:41 - 25.09.2008
package org.andnav.osm.views.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.andnav.osm.exceptions.EmptyCacheException;
import org.andnav.osm.util.constants.OpenStreetMapConstants;
import org.andnav.osm.views.util.constants.OpenStreetMapViewConstants;
import org.opensatnav.OpenSatNavConstants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author Nicolas Gramlich
 * 
 */
public class OpenStreetMapTileFilesystemProvider implements OpenStreetMapConstants, OpenStreetMapViewConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final int MAPTILEFSLOADER_SUCCESS_ID = 1000;
	public static final int MAPTILEFSLOADER_FAIL_ID = MAPTILEFSLOADER_SUCCESS_ID + 1;

	// public static final Options BITMAPLOADOPTIONS = new Options(){
	// {
	// inPreferredConfig = Config.RGB_565;
	// }
	// };

	// ===========================================================
	// Fields
	// ===========================================================

	protected final Context mCtx;
	protected final OpenStreetMapTileFilesystemProviderDataBase mDatabase;
	protected final int mMaxFSCacheByteSize;
	protected int mCurrentFSCacheByteSize;
	protected ExecutorService mThreadPool = Executors.newFixedThreadPool(2);
	protected final OpenStreetMapTileCache mCache;
	protected final String tileFolder = OpenSatNavConstants.TILE_CACHE_PATH;
	protected Set<String> mPending = Collections.synchronizedSet(new HashSet<String>());


	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * @param ctx
	 * @param aMaxFSCacheByteSize
	 *            the size of the cached MapTiles will not exceed this size.
	 * @param aCache
	 *            to load fs-tiles to.
	 */
	public OpenStreetMapTileFilesystemProvider(final Context ctx, final int aMaxFSCacheByteSize,
			final OpenStreetMapTileCache aCache) {
		this.mCtx = ctx;
		this.mMaxFSCacheByteSize = aMaxFSCacheByteSize;
		this.mDatabase = new OpenStreetMapTileFilesystemProviderDataBase(ctx);
		this.mCurrentFSCacheByteSize = this.mDatabase.getCurrentFSCacheByteSize();
		this.mCache = aCache;

		if (DEBUGMODE)
			Log.i(DEBUGTAG, "Currently used cache-size is: " + this.mCurrentFSCacheByteSize + " of "
					+ this.mMaxFSCacheByteSize + " Bytes");
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getCurrentFSCacheByteSize() {
		return this.mCurrentFSCacheByteSize;
	}

	public void loadMapTileToMemCacheAsync(final String aTileURLString, final Handler callback)
			throws FileNotFoundException {
		if (this.mPending.contains(aTileURLString))
			return;

		final String formattedTileURLString = OpenStreetMapTileNameFormatter.format(aTileURLString);
		File root = OpenSatNavConstants.DATA_ROOT_DEVICE;
		File tileFile = new File(root, tileFolder+aTileURLString.substring(7)+".osn");
		FileInputStream fis = null;
		if (root.canRead()) {
			fis = new FileInputStream(tileFile);
		} else {
			fis = this.mCtx.openFileInput(formattedTileURLString);
		}
		final BufferedInputStream bis = new BufferedInputStream(fis,4096);
		

		this.mPending.add(aTileURLString);

		this.mThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				OutputStream out = null;
				try {
					// File exists, otherwise a FileNotFoundException would have
					// been thrown
					OpenStreetMapTileFilesystemProvider.this.mDatabase.incrementUse(formattedTileURLString);

					final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
					out = new BufferedOutputStream(dataStream, StreamUtils.IO_BUFFER_SIZE);
					StreamUtils.copy(bis, out);
					out.flush();

					final byte[] data = dataStream.toByteArray();
					final Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length); // ,
					// BITMAPLOADOPTIONS);

					OpenStreetMapTileFilesystemProvider.this.mCache.putTile(aTileURLString, bmp);

					final Message successMessage = Message.obtain(callback, MAPTILEFSLOADER_SUCCESS_ID);
					successMessage.sendToTarget();

					if (DEBUGMODE)
						Log.d(DEBUGTAG, "Loaded: " + aTileURLString + " to MemCache.");
				} catch (IOException e) {
					final Message failMessage = Message.obtain(callback, MAPTILEFSLOADER_FAIL_ID);
					failMessage.sendToTarget();
					if (DEBUGMODE)
						Log.e(DEBUGTAG, "Error Loading MapTile from FS. Exception: " + e.getClass().getSimpleName(), e);
				} finally {
					StreamUtils.closeStream(bis);
					StreamUtils.closeStream(out);
				}

				OpenStreetMapTileFilesystemProvider.this.mPending.remove(aTileURLString);
			}
		});
	}

	public void saveFile(final String aURLString, final byte[] someData) throws IOException {
		final String fullName = OpenStreetMapTileNameFormatter.format(aURLString);
		File sdCard = OpenSatNavConstants.DATA_ROOT_DEVICE;
		File folderPath = new File(sdCard, tileFolder+aURLString.substring(7,aURLString.lastIndexOf('/')));
		File tileFile = new File(sdCard, tileFolder+aURLString.substring(7)+".osn");
		FileOutputStream fos = null;
		if (sdCard.canWrite()) {
			folderPath.mkdirs();
			fos = new FileOutputStream(tileFile);
		} else {
			fos = this.mCtx.openFileOutput(fullName, Context.MODE_WORLD_READABLE);
		}
		final BufferedOutputStream bos = new BufferedOutputStream(fos, 4096);
		bos.write(someData);
		bos.flush();
		bos.close();

		synchronized (this) {
			final int bytesGrown = this.mDatabase.addTileOrIncrement(fullName, someData.length);
			this.mCurrentFSCacheByteSize += bytesGrown;

			if (DEBUGMODE)
				Log.i(DEBUGTAG, "FSCache Size is now: " + this.mCurrentFSCacheByteSize + " Bytes");

			/* If Cache is full... */
			try {

				if (this.mCurrentFSCacheByteSize > this.mMaxFSCacheByteSize) {
					if (DEBUGMODE)
						Log.d(DEBUGTAG, "Freeing FS cache...");
					this.mCurrentFSCacheByteSize -= this.mDatabase
							.deleteOldest((int) (this.mMaxFSCacheByteSize * 0.05f)); // Free
					// 5%
					// of
					// cache
				}
			} catch (EmptyCacheException e) {
				if (DEBUGMODE)
					Log.e(DEBUGTAG, "Cache empty", e);
			}
		}
	}

	public void clearCurrentFSCache() {
		cutCurrentFSCacheBy(Integer.MAX_VALUE); // Delete all
	}

	public void cutCurrentFSCacheBy(final int bytesToCut) {
		try {
			this.mDatabase.deleteOldest(Integer.MAX_VALUE); // Delete all
			this.mCurrentFSCacheByteSize = 0;
		} catch (EmptyCacheException e) {
			if (DEBUGMODE)
				Log.e(DEBUGTAG, "Cache empty", e);
		}
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private interface OpenStreetMapTileFilesystemProviderDataBaseConstants {
		public static final String DATABASE_NAME = "osmaptilefscache_db";
		public static final int DATABASE_VERSION = 2;

		public static final String T_FSCACHE = "t_fscache";
		public static final String T_FSCACHE_NAME = "name_id";
		public static final String T_FSCACHE_TIMESTAMP = "timestamp";
		public static final String T_FSCACHE_USAGECOUNT = "countused";
		public static final String T_FSCACHE_FILESIZE = "filesize";

		public static final String T_FSCACHE_CREATE_COMMAND = "CREATE TABLE IF NOT EXISTS " + T_FSCACHE + " ("
				+ T_FSCACHE_NAME + " VARCHAR(255)," + T_FSCACHE_TIMESTAMP + " DATE NOT NULL," + T_FSCACHE_USAGECOUNT
				+ " INTEGER NOT NULL DEFAULT 1," + T_FSCACHE_FILESIZE + " INTEGER NOT NULL," + " PRIMARY KEY("
				+ T_FSCACHE_NAME + ")" + ");";

		public static final String T_FSCACHE_SELECT_LEAST_USED = "SELECT " + T_FSCACHE_NAME + "," + T_FSCACHE_FILESIZE
				+ " FROM " + T_FSCACHE + " WHERE " + T_FSCACHE_USAGECOUNT + " = (SELECT MIN(" + T_FSCACHE_USAGECOUNT
				+ ") FROM " + T_FSCACHE + ")";
		public static final String T_FSCACHE_SELECT_OLDEST = "SELECT " + T_FSCACHE_NAME + "," + T_FSCACHE_FILESIZE
				+ " FROM " + T_FSCACHE + " ORDER BY " + T_FSCACHE_TIMESTAMP + " ASC";
	}

	private class OpenStreetMapTileFilesystemProviderDataBase implements
			OpenStreetMapTileFilesystemProviderDataBaseConstants, OpenStreetMapViewConstants {
		// ===========================================================
		// Fields
		// ===========================================================

		protected final Context mCtx;
		protected final SQLiteDatabase mDatabase;
		protected final SimpleDateFormat DATE_FORMAT_ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

		// ===========================================================
		// Constructors
		// ===========================================================

		public OpenStreetMapTileFilesystemProviderDataBase(final Context context) {
			this.mCtx = context;
			this.mDatabase = new AndNavDatabaseHelper(context).getWritableDatabase();
		}
		@Override
		protected void finalize() throws Throwable {
			super.finalize();
			// ensure the SqlLite db is closed 
			if (this.mDatabase != null)
				this.mDatabase.close();
		}		
		
		public void incrementUse(final String aFormattedTileURLString) {
			final Cursor c = this.mDatabase.rawQuery("UPDATE " + T_FSCACHE + " SET " + T_FSCACHE_USAGECOUNT + " = "
					+ T_FSCACHE_USAGECOUNT + " + 1 , " + T_FSCACHE_TIMESTAMP + " = '" + getNowAsIso8601() + "' WHERE "
					+ T_FSCACHE_NAME + " = '" + aFormattedTileURLString + "'", null);
			c.close();
		}

		public int addTileOrIncrement(final String aFormattedTileURLString, final int aByteFilesize) {
			final Cursor c = this.mDatabase.rawQuery("SELECT * FROM " + T_FSCACHE + " WHERE " + T_FSCACHE_NAME + " = '"
					+ aFormattedTileURLString + "'", null);
			final boolean existed = c.getCount() > 0;
			c.close();
			if (DEBUGMODE)
				Log.d(DEBUGTAG, "Tile existed=" + existed);
			if (existed) {
				incrementUse(aFormattedTileURLString);
				return 0;
			} else {
				insertNewTileInfo(aFormattedTileURLString, aByteFilesize);
				return aByteFilesize;
			}
		}

		private void insertNewTileInfo(final String aFormattedTileURLString, final int aByteFilesize) {
			final ContentValues cv = new ContentValues();
			cv.put(T_FSCACHE_NAME, aFormattedTileURLString);
			cv.put(T_FSCACHE_TIMESTAMP, getNowAsIso8601());
			cv.put(T_FSCACHE_FILESIZE, aByteFilesize);
			this.mDatabase.insert(T_FSCACHE, null, cv);
		}

		private int deleteOldest(final int pSizeNeeded) throws EmptyCacheException {
			final Cursor c = this.mDatabase.rawQuery(T_FSCACHE_SELECT_OLDEST, null);
			final ArrayList<String> deleteFromDB = new ArrayList<String>();
			int sizeGained = 0;
			if (c != null) {
				String fileNameOfDeleted;
				if (c.moveToFirst()) {
					do {
						final int sizeItem = c.getInt(c.getColumnIndexOrThrow(T_FSCACHE_FILESIZE));
						sizeGained += sizeItem;
						fileNameOfDeleted = c.getString(c.getColumnIndexOrThrow(T_FSCACHE_NAME));

						deleteFromDB.add(fileNameOfDeleted);
						this.mCtx.deleteFile(fileNameOfDeleted);

						if (DEBUGMODE)
							Log.i(DEBUGTAG, "Deleted from FS: " + fileNameOfDeleted + " for " + sizeItem + " Bytes");
					} while (c.moveToNext() && sizeGained < pSizeNeeded);
				} else {
					c.close();
					throw new EmptyCacheException("Cache seems to be empty.");
				}
				c.close();

				for (String fn : deleteFromDB)
					this.mDatabase.delete(T_FSCACHE, T_FSCACHE_NAME + "='" + fn + "'", null);
			}
			return sizeGained;
		}

		// ===========================================================
		// Methods
		// ===========================================================
		private String TMP_COLUMN = "tmp";

		public int getCurrentFSCacheByteSize() {
			final Cursor c = this.mDatabase.rawQuery("SELECT SUM(" + T_FSCACHE_FILESIZE + ") AS " + TMP_COLUMN
					+ " FROM " + T_FSCACHE, null);
			final int ret;
			if (c != null) {
				if (c.moveToFirst()) {
					ret = c.getInt(c.getColumnIndexOrThrow(TMP_COLUMN));
				} else {
					ret = 0;
				}
			} else {
				ret = 0;
			}
			c.close();

			return ret;
		}

		/**
		 * Get at the moment within ISO8601 format.
		 * 
		 * @return Date and time in ISO8601 format.
		 */
		private String getNowAsIso8601() {
			return DATE_FORMAT_ISO8601.format(new Date(System.currentTimeMillis()));
		}

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================

		private class AndNavDatabaseHelper extends SQLiteOpenHelper {
			AndNavDatabaseHelper(final Context context) {
				super(context, DATABASE_NAME, null, DATABASE_VERSION);
			}

			@Override
			public void onCreate(SQLiteDatabase db) {
				db.execSQL(T_FSCACHE_CREATE_COMMAND);
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				if (DEBUGMODE)
					Log.w(DEBUGTAG, "Upgrading database from version " + oldVersion + " to " + newVersion
							+ ", which will destroy all old data");

				db.execSQL("DROP TABLE IF EXISTS " + T_FSCACHE);

				onCreate(db);
			}
		}
	}
}
