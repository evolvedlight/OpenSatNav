// Created by plusminus on 12:28:16 - 21.09.2008
package org.anddev.openstreetmap.contributor.util;

import java.util.ArrayList;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import org.andnav.osm.util.GeoPoint;

public class RouteRecorder {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private final String TAG = "RouteRecorder";
	protected final ArrayList<RecordedGeoPoint> mRecords = new ArrayList<RecordedGeoPoint>();
	protected final ArrayList<RecordedWayPoint> mWayPoints = new ArrayList<RecordedWayPoint>();

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ArrayList<RecordedGeoPoint> getRecordedGeoPoints() {
		return this.mRecords;
	}

	public ArrayList<RecordedWayPoint> getRecordedWayPoints() {
		return this.mWayPoints;
	}


	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void add(final Location aLocation){

		if (aLocation.hasAccuracy() && aLocation.getAccuracy() < 10.0f)
		{
			this.mRecords.add(new RecordedGeoPoint(
					(int)(aLocation.getLatitude() * 1E6), 
					(int)(aLocation.getLongitude() * 1E6),
					System.currentTimeMillis()));
		}
	}
	
	public void addWayPoint(final String name) {
		this.mWayPoints.add(new RecordedWayPoint(mRecords.get(mRecords.size() - 1), name, "Waypoint recorded by OpenSatNav"));
		
	}
	
	public void addWayPoint(final RecordedWayPoint wayPoint) {
		this.mWayPoints.add(wayPoint);
	}

	public void add(final GeoPoint aGeoPoint){
		this.mRecords.add(new RecordedGeoPoint(
				aGeoPoint.getLatitudeE6(), 
				aGeoPoint.getLongitudeE6(),
				System.currentTimeMillis()));
	}

	public void add(final RecordedGeoPoint rGeoPoint) {
		this.mRecords.add(rGeoPoint);
	}
	
	public void add(final RecordedWayPoint rWayPoint) {
		this.mWayPoints.add(rWayPoint);
	}
	
	

	public Bundle getBundle() {
		Bundle data = new Bundle();
		int arraySize = this.mRecords.size();
		ArrayList<Integer> lats = new ArrayList<Integer>();
		ArrayList<Integer> lons = new ArrayList<Integer>();
		long[] timestamps = new long[arraySize];

		for(int i = 0; i < arraySize; i++) {
			lats.add(this.mRecords.get(i).getLatitudeE6());
			lons.add(this.mRecords.get(i).getLongitudeE6());
			timestamps[i] = this.mRecords.get(i).getTimeStamp();
			//Log.v(TAG, "Added GPS: " + this.mRecords.get(i).getLatitudeE6() + "," + this.mRecords.get(i).getLongitudeE6() + " at " + this.mRecords.get(i).getTimeStamp());
		}
		data.putIntegerArrayList("lats", lats);
		data.putIntegerArrayList("lons", lons);
		data.putLongArray("timestamps", timestamps);

		
		int wayPointArraySize = this.mWayPoints.size();
		ArrayList<Integer> wayPointLats = new ArrayList<Integer>();
		ArrayList<Integer> wayPointLons = new ArrayList<Integer>();
		ArrayList<String> wayPointNames = new ArrayList<String>();
		ArrayList<String> wayPointDescriptions = new ArrayList<String>();
		long[] wayPointTimestamps = new long[arraySize];

		for(int i = 0; i < wayPointArraySize; i++) {
			wayPointLats.add(this.mWayPoints.get(i).getLatitudeE6());
			wayPointLons.add(this.mWayPoints.get(i).getLongitudeE6());
			wayPointNames.add(this.mWayPoints.get(i).getWayPointName());
			wayPointDescriptions.add(this.mWayPoints.get(i).getWayPointDescription());
			
			wayPointTimestamps[i] = this.mRecords.get(i).getTimeStamp();
			
			//Log.v(TAG, "Added GPS: " + this.mRecords.get(i).getLatitudeE6() + "," + this.mRecords.get(i).getLongitudeE6() + " at " + this.mRecords.get(i).getTimeStamp());
		}
		data.putIntegerArrayList("wayPointLats", wayPointLats);
		data.putIntegerArrayList("wayPointLons", wayPointLons);
		data.putStringArrayList("wayPointNames", wayPointNames);
		data.putStringArrayList("wayPointDescriptions", wayPointDescriptions);
		data.putLongArray("wayPointTimestamps", wayPointTimestamps);
		return data;
	}

	public RouteRecorder(Bundle data) {
		int arraySize = data.getIntegerArrayList("lats").size();
		ArrayList<Integer> lats = data.getIntegerArrayList("lats");
		ArrayList<Integer> lons = data.getIntegerArrayList("lons");
		long[] timestamps = data.getLongArray("timestamps");
		for (int i = 0; i < arraySize; i++) {
			add(new RecordedGeoPoint(lats.get(i), lons.get(i), timestamps[i]));
			//Log.v(TAG, "Recovered GPS: " + lats.get(i) + "," + lons.get(i) + " at " + timestamps[i]);

		}
		
		int wayPointArraySize = data.getIntegerArrayList("wayPointLats").size();
		ArrayList<Integer> wayPointLats = data.getIntegerArrayList("wayPointLats");
		ArrayList<Integer> wayPointLons = data.getIntegerArrayList("wayPointLons");
		ArrayList<String> wayPointNames = data.getStringArrayList("wayPointNames");
		ArrayList<String> wayPointDescriptions = data.getStringArrayList("wayPointDescriptions");
		long[] wayPointTimestamps = data.getLongArray("wayPointTimestamps");
		for (int i = 0; i < wayPointArraySize; i++) {
			add(new RecordedWayPoint(wayPointLats.get(i), wayPointLons.get(i), wayPointTimestamps[i], wayPointNames.get(i), wayPointDescriptions.get(i)));
			//Log.v(TAG, "Recovered GPS: " + lats.get(i) + "," + lons.get(i) + " at " + timestamps[i]);

		}
	}

	public RouteRecorder() {

	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
