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

import java.util.HashMap;
import java.util.LinkedList;

import android.graphics.Bitmap;

/**
 * Simple LRU cache for any type of object. Implemented as an extended
 * <code>HashMap</code> with a maximum size and an aggregated <code>List</code>
 * as LRU queue.
 * @author Nicolas Gramlich
 *
 */
public class LRUMapTileCache extends HashMap<String, Bitmap> {

	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final long serialVersionUID = 3345124753192560741L;

	// ===========================================================
	// Fields
	// ===========================================================
	
	/** Maximum cache size. */
	private final int maxCacheSize;
	/** LRU list. */
	private final LinkedList<String> list;

	// ===========================================================
	// Constructors
	// ===========================================================
	
	/**
	 * Constructs a new LRU cache instance.
	 * 
	 * @param maxCacheSize the maximum number of entries in this cache before entries are aged off.
	 */
	public LRUMapTileCache(final int maxCacheSize) {
		super(maxCacheSize);
		this.maxCacheSize = Math.max(0, maxCacheSize);
		this.list = new LinkedList<String>();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================
	
	/**
	 * Overrides clear() to also clear the LRU list.
	 */
	public synchronized void clear() {
		super.clear();
		list.clear();
	}

	/**
	 * Overrides <code>put()</code> so that it also updates the LRU list.
	 * 
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the key
	 * @return previous value associated with key or <code>null</code> if there
	 *         was no mapping for key; a <code>null</code> return can also
	 *         indicate that the cache previously associated <code>null</code>
	 *         with the specified key
	 */
	public synchronized Bitmap put(final String key, final Bitmap value) {
		if (maxCacheSize == 0){
			return null;
		}

		// if the key isn't in the cache and the cache is full...
		if (!super.containsKey(key) && !list.isEmpty() && list.size() + 1 > maxCacheSize) {
			final Object deadKey = list.removeLast();
			super.remove(deadKey);
		}

		updateKey(key);
		return super.put(key, value);
	}

	/**
	 * Overrides <code>get()</code> so that it also updates the LRU list.
	 * 
	 * @param key
	 *            key with which the expected value is associated
	 * @return the value to which the cache maps the specified key, or
	 *         <code>null</code> if the map contains no mapping for this key
	 */
	public synchronized Bitmap get(final String key) {
		final Bitmap value = super.get(key);
		if (value != null) {
			updateKey(key);
		}
		return value;
	}

	public synchronized Bitmap remove(final String key) {
		list.remove(key);
		return super.remove(key);
	}

	/**
	 * Moves the specified value to the top of the LRU list (the bottom of the
	 * list is where least recently used items live).
	 * 
	 * @param key of the value to move to the top of the list
	 */
	private void updateKey(final String key) {
		list.remove(key);
		list.addFirst(key);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
