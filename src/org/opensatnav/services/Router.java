package org.opensatnav.services;

import java.util.ArrayList;

import org.andnav.osm.util.GeoPoint;

import android.content.Context;

public interface Router {

	public static final String CAR = "motorcar";
	public static final String BICYCLE = "bicycle";
	public static final String WALKING = "foot";

	/**
	 * 
	 * @param from
	 *            where the user is
	 * @param to
	 *            where the user wants to go
	 * @param vehicle
	 *            one of the vehicle constants (CAR, BICYCLE or WALKING)
	 * @param context
	 *            reference to caller (used to get the name and version
	 *            number of the program to add the user agent in network ops)
	 * @return an ArrayList containing Strings of the format "latE6,longE6" (E6
	 *         means times by 1000000 so we are dealing with ints, not floats as
	 *         floats run slowly on phones)
	 */
	public abstract ArrayList<String> getRoute(GeoPoint from, GeoPoint to, String vehicle, Context context);

}