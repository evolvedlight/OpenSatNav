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
// Created by plusminus on 08:19:56 - 26.09.2008
package org.andnav.osm.views.util;


/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class OpenStreetMapTileNameFormatter {
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
	
	/**
	 * Formats a URL to a String that it can be saved to a file, without problems of special chars.
	 * 
	 * <PRE><b>Example:</b>
	 * 
	 * <code>http://a.tile.openstreetmap.org/0/0/0.png</code>
	 * would become 
	 * <code>a.tile.openstreetmap.org_0_0_0.png</code>
	 * </PRE>
	 * @return saveable formatted URL as a String
	 */
	public static String format(final String aTileURLString){
		return aTileURLString.substring(7).replace("/", "_");
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
