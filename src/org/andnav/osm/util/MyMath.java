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
package org.andnav.osm.util;

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
	
	
	public static double gudermannInverse(double aLatitude){
		return Math.log(Math.tan(PI_4 + (DEG2RAD * aLatitude / 2)));
	}
	
	public static double gudermann(double y){
		return RAD2DEG * Math.atan(Math.sinh(y));
	}
	
	
	public static int mod(int number, final int modulus){
		if(number > 0)
			return number % modulus;
		
		while(number < 0)
			number += modulus;
		
		return number;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
