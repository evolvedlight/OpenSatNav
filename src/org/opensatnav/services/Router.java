package org.opensatnav.services;

import java.util.ArrayList;

import org.andnav.osm.util.GeoPoint;

import android.content.Context;

public interface Router {

	public static final String CAR = "motorcar";
	public static final String BICYCLE = "bicycle";
	public static final String WALKING = "foot";

	public abstract ArrayList<String> getRoute(GeoPoint from, GeoPoint to,
			String vehicle, Context context);

}