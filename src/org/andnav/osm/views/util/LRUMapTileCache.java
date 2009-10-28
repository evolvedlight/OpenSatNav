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
// Created by plusminus on 22:13:10 - 28.09.2008
package org.andnav.osm.views.util;

import java.util.LinkedHashMap;

/**
 * Simple LRU cache for any type of object. Implemented as an extended
 * <code>HashMap</code> with a maximum size and an aggregated <code>List</code>
 * as LRU queue.
 * @author Nicolas Gramlich
 *
 */
public class LRUMapTileCache<K, V> extends LinkedHashMap<K, V> {

	// ===========================================================
	// Constants
	// ===========================================================
	
	// private static final long serialVersionUID = 3345124753192560741L;

	protected static final float DEFAULT_LOAD_FACTOR = 0.75f;

	// ===========================================================
	// Fields
	// ===========================================================
	
	/** Maximum cache size. */
	private int maxCacheSize;

	// ===========================================================
	// Constructors
	// ===========================================================
	
	/**
	 * Constructs a new LRU cache instance.
	 * 
	 * @param maxCacheSize the maximum number of entries in this cache before entries are aged off.
	 */
	public LRUMapTileCache(final int maxCacheSize) {
		super(maxCacheSize, DEFAULT_LOAD_FACTOR, true);
		this.maxCacheSize = Math.max(0, maxCacheSize);
	}

	public void setMaxCacheSize(int newMax) {
		// warning: cache will not grow beyond this size, but if already beyond this size,
		// it will not shrink by itself!
		maxCacheSize = newMax;
	}

	protected boolean removeEldestEntry(Entry<K, V> eldest) {
		return (size() > maxCacheSize);
	}
}
