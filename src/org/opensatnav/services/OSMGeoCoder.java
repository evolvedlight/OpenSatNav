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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.andnav.osm.util.GeoPoint;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;

/**
 * 
 * @author Kieran Fleming
 * 
 */

public class OSMGeoCoder implements GeoCoder {
	private URL url;
	private GeoPoint location;
	private ArrayList<String> locationNames;
	private ArrayList locationLatitudes;
	private ArrayList locationLongitudes;
	private ArrayList<String> locationTypes;

	/* (non-Javadoc)
	 * @see org.opensatnav.services.GeoCoder#getFromLocationName(java.lang.String, int, android.content.Context)
	 */
	public Bundle getFromLocationName(String locationName, int maxResults, Context context) {
		locationNames = new ArrayList<String>();
		locationLatitudes = new ArrayList<int[]>();
		locationLongitudes = new ArrayList<int[]>();
		locationTypes = new ArrayList<String>();
		try {
			url = new URL("http://gazetteer.openstreetmap.org/namefinder/search.xml?find="
					+ URLEncoder.encode(locationName) + "&max=" + maxResults);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			URLConnection urlConn = url.openConnection();
			String userAgent = getUserAgent(context);
			if (userAgent != null)
				urlConn.setRequestProperty("User-Agent", userAgent);
			urlConn.setReadTimeout(60000);
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(urlConn.getInputStream());
			NodeList xml = doc.getChildNodes();
			// if we have at least 1 result
			if (xml.item(1).getChildNodes().item(1) != null) {
				NodeList locations = xml.item(1).getChildNodes();
				for (int i = 1; i < locations.getLength(); i++) {
					if (locations.item(i).getNodeName().compareTo("named") == 0) {
						NamedNodeMap locationXml = locations.item(i).getAttributes();
						Log.d("OSMGEOCODER", "found location: " + locationXml.getNamedItem("name").getNodeValue());
						locationNames.add(locationXml.getNamedItem("name").getNodeValue());
						locationLatitudes
								.add((int) (Float.parseFloat(locationXml.getNamedItem("lat").getNodeValue()) * 1000000));
						locationLongitudes
								.add((int) (Float.parseFloat(locationXml.getNamedItem("lon").getNodeValue()) * 1000000));
						locationTypes.add(locationXml.getNamedItem("info").getNodeValue());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("OSMGEOCODER", "Network timeout");
			return null;
		}
		Bundle bundle = new Bundle();
		//should have done this better - didn't know Java had issues like this!
		int[] latArray = new int[locationLatitudes.size()];
		int[] lonArray = new int[locationLatitudes.size()];
		String[] nameArray = new String[locationNames.size()];
		String[] typeArray = new String[locationTypes.size()];

		System.arraycopy(locationNames.toArray(), 0, nameArray, 0, locationNames.size());
		System.arraycopy(locationTypes.toArray(), 0, typeArray, 0, locationTypes.size());
		for(int i = 0;i<locationLatitudes.size();i++)
			latArray[i] = (Integer) locationLatitudes.get(i);
		for(int i = 0;i<locationLatitudes.size();i++)
			lonArray[i] = (Integer) locationLongitudes.get(i);
		
		bundle.putStringArray("names", nameArray);
		bundle.putIntArray("latitudes", latArray);
		bundle.putIntArray("longitudes", lonArray);
		bundle.putStringArray("types", typeArray);
		return bundle;

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
