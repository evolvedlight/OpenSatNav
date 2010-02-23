// Created by plusminus on 12:29:23 - 21.09.2008
package org.anddev.openstreetmap.contributor.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.anddev.openstreetmap.contributor.util.constants.Constants;
import org.andnav.osm.util.GeoPoint;

import android.content.ContentValues;

//import com.google.android.maps.GeoPoint;


public class RecordedGeoPoint extends GeoPoint implements Constants{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final Date timestamp;
	protected final double altitude;
	protected final float accuracy;
	protected final float bearing;
	protected final float speed;
	protected final long routeID;
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public RecordedGeoPoint(final long routeID, final int latitudeE6, final int longitudeE6, final Date timestamp, final double altitude, final float accuracy, final float bearing, final float speed) {
		super(latitudeE6, longitudeE6);
		this.timestamp = timestamp;
		this.altitude = altitude;
		this.accuracy = accuracy;
		this.bearing = bearing;
		this.speed = speed;
		this.routeID = routeID;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public Date getTimeStamp() {
		return this.timestamp;
	}
	
	public String getOSMTimeStamp() {
		SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return SDF.format(this.getTimeStamp());
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
	public ContentValues getContentValues() {
		//String result = "INSERT INTO t_journeys (\"latitude\", \"longitude\", \"altitude\", \"accuracy\", \"bearing\", \"speed\", \"timestamp\") VALUES (" + this.getLatitudeAsDouble() + ", " + this.getLongitudeAsDouble() + "," + this.altitude + "," + this.accuracy + "," + this.bearing + "," + this.speed + "," + this.mTimeStamp + ")";
		//sack it, constants take ages to type in
		//That was wrong! SQL not needed when we have super contentvalues.
		ContentValues cv = new ContentValues();
		cv.put(T_ROUTERECORDER_LATITUDE, this.getLatitudeAsDouble());
		cv.put(T_ROUTERECORDER_LONGITUDE, this.getLongitudeAsDouble());
		cv.put(T_ROUTERECORDER_ALTITUDE, this.altitude);
		cv.put(T_ROUTERECORDER_ACCURACY, this.accuracy);
		cv.put(T_ROUTERECORDER_BEARING, this.bearing);
		cv.put(T_ROUTERECORDER_SPEED, this.speed);
		cv.put(T_ROUTERECORDER_TIMESTAMP, DATE_FORMAT_ISO8601.format(this.timestamp));
		cv.put(T_ROUTERECORDER_ROUTE_ID, this.routeID);
		
		return cv;
		
	} 
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
