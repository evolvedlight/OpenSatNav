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
// Created by plusminus on 17:58:57 - 25.09.2008
package org.andnav.osm.views.util;

import org.andnav.osm.views.util.constants.OpenStreetMapViewConstants;

import android.graphics.Bitmap;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class OpenStreetMapTileCache implements OpenStreetMapViewConstants{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	
	protected LRUMapTileCache<String, Bitmap> mCachedTiles;

	// ===========================================================
	// Constructors
	// ===========================================================
	
	public OpenStreetMapTileCache(){
		this(CACHE_MAPTILECOUNT_DEFAULT);
	}
	
	/**
	 * @param aMaximumCacheSize Maximum amount of MapTiles to be hold within.
	 */
	public OpenStreetMapTileCache(final int aMaximumCacheSize){
		this.mCachedTiles = new LRUMapTileCache<String, Bitmap>(aMaximumCacheSize);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public synchronized Bitmap getMapTile(final String aTileURLString) {
		return this.mCachedTiles.get(aTileURLString);
	}

	public synchronized void putTile(final String aTileURLString, final Bitmap aTile) {
		this.mCachedTiles.put(aTileURLString, aTile);
	}

	public synchronized void clearTile(final String aTileURLString) {
		this.mCachedTiles.remove(aTileURLString);
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void ensureCacheSize(int newSize) {
		mCachedTiles.setMaxCacheSize(Math.max(newSize, CACHE_MAPTILECOUNT_DEFAULT));
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
