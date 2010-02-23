// Created by plusminus on 12:28:16 - 21.09.2008
package org.anddev.openstreetmap.contributor.util;

import java.sql.Date;
import java.util.ArrayList;

import org.anddev.openstreetmap.contributor.util.constants.Constants;
import org.opensatnav.OpenSatNavConstants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

public class RouteRecorder implements Constants{
	// ===========================================================
	// Constants
	// ===========================================================
	

	

	
	// ===========================================================
	// Fields
	// ===========================================================
	private final String TAG = "RouteRecordery";
	protected final Context mCtx;
	private DatabaseAdapter mDatabaseAdapter;
	private long journeyID;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ArrayList<RecordedGeoPoint> getRecordedGeoPoints() {
		ArrayList<RecordedGeoPoint> results = new ArrayList<RecordedGeoPoint>();
		
		Cursor c = mDatabaseAdapter.getNodesForJourney(journeyID);
		int i = 0;
		if (c != null) {
            /* Check if at least one Result was returned. */
			Log.v("TAG", "C not null");
            if (c.moveToFirst()) {
            	Log.v("TAG", "C first");
                 /* Loop through all Results */
                 do {
                	 Log.v("TAG", "In loop");
                      i++;
                      /* Retrieve the values of the Entry
                       * the Cursor is pointing to. */
                      Log.v(TAG, "Adding" + (int) (c.getDouble(c.getColumnIndex(T_ROUTERECORDER_LATITUDE))*1E6));
                      results.add(new RecordedGeoPoint(c.getInt(c.getColumnIndex(T_ROUTERECORDER_ROUTE_ID)), (int) (c.getDouble(c.getColumnIndex(T_ROUTERECORDER_LATITUDE))*1E6), (int) (c.getDouble(c.getColumnIndex(T_ROUTERECORDER_LONGITUDE))*1E6), parseDate(c.getString(c.getColumnIndex(T_ROUTERECORDER_TIMESTAMP))), c.getFloat(c.getColumnIndex(T_ROUTERECORDER_ALTITUDE)), c.getFloat(c.getColumnIndex(T_ROUTERECORDER_ACCURACY)), c.getFloat(c.getColumnIndex(T_ROUTERECORDER_BEARING)), c.getFloat(c.getColumnIndex(T_ROUTERECORDER_SPEED))));
                 } while (c.moveToNext());
            }
		}
		Log.v(TAG, "Returned " + i + " results for routeID " + journeyID);
		return results;
		
	}

	public ArrayList<RecordedWayPoint> getRecordedWayPoints() {
		return new ArrayList<RecordedWayPoint>();
		//return this.mWayPoints;
	}


	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void add(final Location aLocation){

		if (aLocation.hasAccuracy() && aLocation.getAccuracy() <= OpenSatNavConstants.GPS_TRACE_MIN_ACCURACY)
		{
			RecordedGeoPoint newPoint = new RecordedGeoPoint(
					this.journeyID,
					(int)(aLocation.getLatitude() * 1E6), 
					(int)(aLocation.getLongitude() * 1E6),
					new Date(System.currentTimeMillis()),
					aLocation.getAltitude(),
					aLocation.getAccuracy(),
					aLocation.getBearing(),
					aLocation.getSpeed());
			long id = mDatabaseAdapter.insertNode(newPoint.getContentValues());
			Log.v(TAG, "Inserted a geopoint, id " + id);
		}
	}
	
	
	public void addWayPoint(final String name) {
		//this.mWayPoints.add(new RecordedWayPoint(mRecords.get(mRecords.size() - 1), name, "Waypoint recorded by OpenSatNav"));
		
	}
	/*
	public void addWayPoint(final RecordedWayPoint wayPoint) {
		//this.mWayPoints.add(wayPoint);
	}
	

	public void add(final GeoPoint aGeoPoint){
		//this.mRecords.add(new RecordedGeoPoint(
		//		aGeoPoint.getLatitudeE6(), 
		//		aGeoPoint.getLongitudeE6(),
		//		System.currentTimeMillis()));
	}

	public void add(final RecordedGeoPoint rGeoPoint) {
		//this.mRecords.add(rGeoPoint);
	}
	
	public void add(final RecordedWayPoint rWayPoint) {
		//this.mWayPoints.add(rWayPoint);
	}*/
	
	

	public Bundle getBundle() {
		
		
		Bundle data = new Bundle();
		data.putLong(T_ROUTERECORDER_ROUTE_ID, journeyID);
		return data;
	}

	public RouteRecorder(Bundle data, Context ctx) {
		journeyID = data.getLong(T_ROUTERECORDER_ROUTE_ID);
		mDatabaseAdapter = new DatabaseAdapter(ctx);
		mDatabaseAdapter.open();
		mCtx = ctx;
		
	}

	public RouteRecorder(Context ctx) {
		mDatabaseAdapter = new DatabaseAdapter(ctx);
		mDatabaseAdapter.open();
		startNewJourney();
		mCtx = ctx;
	}
	
	public void startNewJourney() {
		ContentValues cv = new ContentValues();
		cv.put(T_ROUTERECORDER_JOURNEY_NAME, "Unnamed Route");
		cv.put(T_ROUTERECORDER_MAX_SPEED, 0);
		cv.put(T_ROUTERECORDER_DISTANCE, 0);
		cv.put(T_ROUTERECORDER_TOTAL_TIME, 0);
		cv.put(T_ROUTERECORDER_MOVING_TIME, 0);
		cv.put(T_ROUTERECORDER_AVERAGE_SPEED, 0);
		cv.put(T_ROUTERECORDER_AVERAGE_MOVING_SPEED, 0);
		cv.put(T_ROUTERECORDER_MIN_ALTITUDE, 0);
		cv.put(T_ROUTERECORDER_MAX_ALTITUDE, 0);
		cv.put(T_ROUTERECORDER_ALTITUDE_GAIN, 0);
		cv.put(T_ROUTERECORDER_MIN_GRADE, 0);
		cv.put(T_ROUTERECORDER_MAX_GRADE, 0);
		journeyID = mDatabaseAdapter.createNewJourney(cv);
	}
	
	
	
	private java.util.Date parseDate(String dateString) {
		java.util.Date result;
		try {
			Log.v(TAG, dateString);
			result = DATE_FORMAT_ISO8601.parse(dateString);
		} catch (java.text.ParseException e) {
			result = null;
		}
		return result;
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	
	
	

	
}
