package org.opensatnav.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.andnav.osm.util.GeoPoint;
import org.opensatnav.OpenSatNavConstants;
import org.opensatnav.util.OSNHttpAgent;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class NominatimGeoCoder implements GeoCoder {
	private URL url;
	private GeoPoint location;
	private ArrayList<String> locationNames;
	private ArrayList locationLatitudes;
	private ArrayList locationLongitudes;
	private ArrayList<String> locationInfo;

	@Override
	public Bundle getFromLocationName(String locationName, int maxResults,
			Context context) {
		locationNames = new ArrayList<String>();
		locationLatitudes = new ArrayList<int[]>();
		locationLongitudes = new ArrayList<int[]>();
		locationInfo = new ArrayList<String>();
		try {			
			// see http://wiki.openstreetmap.org/wiki/Nominatim for URL available parameters
			
			//String language = Locale.getDefault().getLanguage();
			// "&accept-language=" + language
			url = new URL("http://nominatim.openstreetmap.org/search?q="
					+ URLEncoder.encode(locationName) + "&format=xml");
		} catch (MalformedURLException e) {
			Log.e(OpenSatNavConstants.LOG_TAG, e.getMessage(), e);
		}
		try {
			URLConnection urlConn = url.openConnection();
			String userAgent = OSNHttpAgent.getUserAgent(context);
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
				NodeList places = xml.item(1).getChildNodes();
				for (int i = 1; i < places.getLength(); i++) {

					NamedNodeMap attributes = places.item(i).getAttributes();
					Log.d("NOMINATIMGEOCODER", "found location: "
							+ attributes.getNamedItem("display_name").getNodeValue());
					locationNames.add(attributes.getNamedItem("display_name")
							.getNodeValue());
					// convert to integer (E6 format)
					locationLatitudes.add((int) (Float.parseFloat(attributes
							.getNamedItem("lat").getNodeValue()) * 1000000));
					locationLongitudes.add((int) (Float.parseFloat(attributes
							.getNamedItem("lon").getNodeValue()) * 1000000));
					if (attributes.getNamedItem("type") != null)
						locationInfo.add(attributes.getNamedItem("type")
								.getNodeValue());
					else
						locationInfo.add(" ");
				}
			}
			// no results
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("OSMGEOCODER", "Network timeout");
			return null;
		}
		Bundle bundle = new Bundle();
		// should have done this better - didn't know Java had issues like this!
		int[] latArray = new int[locationLatitudes.size()];
		int[] lonArray = new int[locationLatitudes.size()];
		String[] nameArray = new String[locationNames.size()];
		String[] infoArray = new String[locationInfo.size()];

		System.arraycopy(locationNames.toArray(), 0, nameArray, 0,
				locationNames.size());
		System.arraycopy(locationInfo.toArray(), 0, infoArray, 0, locationInfo
				.size());
		for (int i = 0; i < locationLatitudes.size(); i++)
			latArray[i] = (Integer) locationLatitudes.get(i);
		for (int i = 0; i < locationLatitudes.size(); i++)
			lonArray[i] = (Integer) locationLongitudes.get(i);

		bundle.putStringArray("names", nameArray);
		bundle.putIntArray("latitudes", latArray);
		bundle.putIntArray("longitudes", lonArray);
		bundle.putStringArray("info", infoArray);
		return bundle;

	}

}
