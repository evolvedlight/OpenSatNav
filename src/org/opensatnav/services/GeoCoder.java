package org.opensatnav.services;

import android.content.Context;
import android.os.Bundle;

public interface GeoCoder {
	/**
	 * 
	 * @param locationName
	 *            where the user wants to go
	 * @param maxResults
	 *            max results required
	 * @param context
	 *            reference to caller (used to get the name and version number
	 *            of the program to add the user agent in network ops)
	 * @return a Bundle containing parallel arrays named names, latitudes,
	 *         longitudes and info where <i>name</i> is the name of the place,
	 *         where <i>latitudes</i> and <i>longitudes</i> are in integer (E6)
	 *         format and <i>info</i> can be any text that will help the user
	 *         choose the place they want
	 */
	public abstract Bundle getFromLocationName(String locationName, int maxResults, Context context);

}