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

package org.opensatnav.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.andnav.osm.util.GeoPoint;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 
 * @author Kieran Fleming
 * 
 */

public class YOURSRouter implements Router {
	private URL url;
	private ArrayList<String> route;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opensatnav.services.Router#getRoute(org.andnav.osm.util.GeoPoint,
	 * org.andnav.osm.util.GeoPoint, java.lang.String, android.content.Context)
	 */
	public ArrayList<String> getRoute(GeoPoint from, GeoPoint to, String vehicle, Context context) {
		route = new ArrayList<String>();
		try {
			url = new URL("http://www.yournavigation.org/gosmore.php?" + "flat=" + (from.getLatitudeE6() / 1000000.0)
					+ "&" + "flon=" + (from.getLongitudeE6() / 1000000.0) + "&" + "tlat="
					+ (to.getLatitudeE6() / 1000000.0) + "&" + "tlon=" + (to.getLongitudeE6() / 1000000.0) + "&" + "v="
					+ vehicle + "&" + "fast=1&layer=mapnik");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			URLConnection conn = url.openConnection();
			String userAgent = getUserAgent(context);
			if (userAgent != null)
				conn.setRequestProperty("User-Agent", userAgent);
			conn.setReadTimeout(30000);
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8192);
			StringBuilder kmlBuilder = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				kmlBuilder.append(line + "\n");
			}
			in.close();
			String kml = kmlBuilder.toString();
			String coords = null;
			if (kml.indexOf("<coordinates>") != -1) {
				coords = kml.substring(kml.indexOf("<coordinates>") + 14, kml.indexOf("</coordinates>"));
				StringTokenizer tokenizer = new StringTokenizer(coords, "\n");
				while (tokenizer.hasMoreTokens()) {
					String coord = tokenizer.nextToken();
					if ((coord != null) && (coord.indexOf(',') != -1)) {
						// yes, the data returned from the server is long, lat
						float lonRegular = Float.parseFloat(coord.substring(0, coord.indexOf(',')));
						float latRegular = Float.parseFloat(coord.substring(coord.indexOf(',') + 1));
						// convert to int format to avoid too much float
						// processing
						route.add(new String((int) (latRegular * 1000000) + "," + (int) (lonRegular * 1000000)));
					}
				}
			} else {
				throw new IOException();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		// Log.d("OSMROUTER", "Route created");
		return route;

	}

	public static String getUserAgent(Context context) {
		try {
			// Read package name and version number from manifest
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.packageName + " " + info.versionName;

		} catch (NameNotFoundException e) {
			return null;
		}
	}
}
