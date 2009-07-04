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
// Created by plusminus on 00:47:05 - 02.10.2008
package org.andnav.osm.util;


import android.location.Location;

/**
 * Converts some usual types from one to another.
 * @author Nicolas Gramlich
 *
 */
public class TypeConverter {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	
	public static GeoPoint locationToGeoPoint(final Location aLoc){
		return new GeoPoint((int)(aLoc.getLatitude() * 1E6), (int)(aLoc.getLongitude() * 1E6));
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
