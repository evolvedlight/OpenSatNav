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
// Created by plusminus on 20:36:01 - 26.09.2008
package org.andnav.osm.views.util;

import org.andnav.osm.views.util.constants.MathConstants;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class MyMath implements MathConstants {
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
	 * Calculates i.e. the increase of zoomlevel needed when the visible latitude needs to be bigger by <code>factor</code>.  
	 * 
	 * Assert.assertEquals(1, getNextSquareNumberAbove(1.1f));
	 * Assert.assertEquals(2, getNextSquareNumberAbove(2.1f));
	 * Assert.assertEquals(2, getNextSquareNumberAbove(3.9f));
	 * Assert.assertEquals(3, getNextSquareNumberAbove(4.1f));
	 * Assert.assertEquals(3, getNextSquareNumberAbove(7.9f));
	 * Assert.assertEquals(4, getNextSquareNumberAbove(8.1f));
	 * Assert.assertEquals(5, getNextSquareNumberAbove(16.1f));
	 * 
	 * Assert.assertEquals(-1, - getNextSquareNumberAbove(1 / 0.4f) + 1);
	 * Assert.assertEquals(-2, - getNextSquareNumberAbove(1 / 0.24f) + 1);
	 * 
	 * @param factor
	 * @return
	 */
	public static int getNextSquareNumberAbove(final float factor){
		int out = 0;
		int cur = 1;
		int i = 1;
		while(true){
			if(cur > factor)
				return out;
			
			out = i;
			cur *= 2;
			i++;
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
