// Created by plusminus on 12:29:23 - 21.09.2008
package org.anddev.openstreetmap.contributor.util;

import org.andnav.osm.util.GeoPoint;

//import com.google.android.maps.GeoPoint;


public class RecordedGeoPoint extends GeoPoint {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final long mTimeStamp;
	

	// ===========================================================
	// Constructors
	// ===========================================================

	public RecordedGeoPoint(final int latitudeE6, final int longitudeE6, final long aTimeStamp) {
		super(latitudeE6, longitudeE6);
		this.mTimeStamp = aTimeStamp;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public long getTimeStamp() {
		return this.mTimeStamp;
	}
	
	public double getLatitudeAsDouble(){
		return this.getLatitudeE6() / 1E6;
	}
	
	public double getLongitudeAsDouble(){
		return this.getLongitudeE6() / 1E6;
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
}
