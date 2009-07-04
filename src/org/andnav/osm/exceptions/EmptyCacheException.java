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
// Created by plusminus on 14:29:37 - 12.10.2008
package org.andnav.osm.exceptions;


public class EmptyCacheException extends Exception {
	// ===========================================================
	// Constants
	// ===========================================================
	
	private static final long serialVersionUID = -6096533745569312071L;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public EmptyCacheException() {
		super();
	}

	public EmptyCacheException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public EmptyCacheException(String detailMessage) {
		super(detailMessage);
	}

	public EmptyCacheException(Throwable throwable) {
		super(throwable);
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

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
