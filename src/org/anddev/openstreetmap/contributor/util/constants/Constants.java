// Created by plusminus on 23:11:31 - 22.09.2008
package org.anddev.openstreetmap.contributor.util.constants;

import java.text.SimpleDateFormat;


public interface Constants {
	// ===========================================================
	// Final Fields
	// ===========================================================
	
	public static final String DEBUGTAG = "OSMCONTRIBUTOR";
	public static Boolean DEBUGMODE = false;
	
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "OSNContribute";
	public static final String T_ROUTERECORDER_NODE_TABLE = "t_routerecorder";
	public static final String T_ROUTERECORDER_LATITUDE = "latitude";
	public static final String T_ROUTERECORDER_LONGITUDE = "longitude";
	public static final String T_ROUTERECORDER_ALTITUDE = "altitude";
	public static final String T_ROUTERECORDER_ACCURACY = "accuracy";
	public static final String T_ROUTERECORDER_BEARING = "bearing";
	public static final String T_ROUTERECORDER_SPEED = "speed";
	public static final String T_ROUTERECORDER_TIMESTAMP = "timestamp";
	public static final String T_ROUTERECORDER_ID = "_id";
	public static final String T_ROUTERECORDER_ROUTE_ID = "route_id";

	public static final String T_ROUTERECORDER_JOURNEY_TABLE = "t_journeys";
	public static final String T_ROUTERECORDER_JOURNEY_NAME = "journey_name";
	public static final String T_ROUTERECORDER_MAX_SPEED = "max_speed";
	public static final String T_ROUTERECORDER_DISTANCE = "distance";
	public static final String T_ROUTERECORDER_TOTAL_TIME = "total_time";
	public static final String T_ROUTERECORDER_MOVING_TIME = "moving_time";
	public static final String T_ROUTERECORDER_AVERAGE_SPEED = "average_speed";
	public static final String T_ROUTERECORDER_AVERAGE_MOVING_SPEED = "average_moving_speed";
	public static final String T_ROUTERECORDER_MIN_ALTITUDE = "minimum_elevation";
	public static final String T_ROUTERECORDER_MAX_ALTITUDE = "maximum_elevation";
	public static final String T_ROUTERECORDER_ALTITUDE_GAIN = "elevation_gain";
	public static final String T_ROUTERECORDER_MIN_GRADE = "minimum_grade";
	public static final String T_ROUTERECORDER_MAX_GRADE = "maximum_grade";
	public static final SimpleDateFormat DATE_FORMAT_ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	public static final String T_ROUTERECORDER_CREATE_JOURNEY_TABLE = "CREATE TABLE IF NOT EXISTS " 
		+ T_ROUTERECORDER_JOURNEY_TABLE
		+ " ("
		+ T_ROUTERECORDER_ID + " INTEGER, "
		+ T_ROUTERECORDER_JOURNEY_NAME + " VARCHAR(255),"
		+ T_ROUTERECORDER_MAX_SPEED + " FLOAT,"
		+ T_ROUTERECORDER_DISTANCE + " FLOAT,"
		+ T_ROUTERECORDER_TOTAL_TIME + " DOUBLE," //stored as seconds in journey
		+ T_ROUTERECORDER_MOVING_TIME + " DOUBLE,"
		+ T_ROUTERECORDER_AVERAGE_SPEED + " FLOAT,"
		+ T_ROUTERECORDER_AVERAGE_MOVING_SPEED + " FLOAT,"
		+ T_ROUTERECORDER_MIN_ALTITUDE + " DOUBLE," 
		+ T_ROUTERECORDER_MAX_ALTITUDE + " DOUBLE,"
		+ T_ROUTERECORDER_ALTITUDE_GAIN + " DOUBLE,"
		+ T_ROUTERECORDER_MIN_GRADE + " FLOAT," 
		+ T_ROUTERECORDER_MAX_GRADE + " FLOAT,"
		+ " PRIMARY KEY(" + T_ROUTERECORDER_ID + "));"; 
	
	public static final String T_ROUTERECORDER_CREATE_NODE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ T_ROUTERECORDER_NODE_TABLE
			+ " ("
			+ T_ROUTERECORDER_ID + " INTEGER, "
			+ T_ROUTERECORDER_ROUTE_ID + " INTEGER,"
			+ T_ROUTERECORDER_LATITUDE + " DOUBLE,"
			+ T_ROUTERECORDER_LONGITUDE	+ " DOUBLE,"
			+ T_ROUTERECORDER_ALTITUDE	+ " DOUBLE,"
			+ T_ROUTERECORDER_ACCURACY	+ " FLOAT,"
			+ T_ROUTERECORDER_BEARING + " FLOAT,"
			+ T_ROUTERECORDER_SPEED	+ " FLOAT,"
			+ T_ROUTERECORDER_TIMESTAMP + " DATE, "
			+ " PRIMARY KEY(" + T_ROUTERECORDER_ID + "));"; 
	// ===========================================================
	// Methods
	// ===========================================================
	
}
