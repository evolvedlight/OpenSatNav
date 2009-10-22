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
// Created by plusminus on 00:14:42 - 02.10.2008
package org.opensatnav;

import org.andnav.osm.util.constants.OpenStreetMapConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Baseclass for Activities who want to contribute to the OpenStreetMap Project.
 * 
 * @author Nicolas Gramlich
 * 
 */
public abstract class OpenStreetMapActivity extends Activity implements
		OpenStreetMapConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected SampleLocationListener mGpsLocationListener;
	protected SampleLocationListener mNetworkLocationListener;

	/** true if Gps Location is activated, false otherwise */
	protected boolean GpsLocationActivated = false;
	/** true if Network Location is activated, false otherwise */
	protected boolean NetworkLocationActivated = false;
	protected String lastLocation = "";
	/** accuracy limit fixed at 40meters */
	protected final int accuracyLimit = 40;

	protected boolean mDoGPSRecordingAndContributing;

	protected LocationManager mLocationManager;

	public int mNumSatellites = NOT_SET;

	protected PowerManager.WakeLock wl;

	protected Location firstLocation;

	protected Location currentLocation;

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * Calls
	 * <code>onCreate(final Bundle savedInstanceState, final boolean pDoGPSRecordingAndContributing)</code>
	 * with <code>pDoGPSRecordingAndContributing == true</code>.<br/>
	 * That means it automatically contributes to the OpenStreetMap Project in
	 * the background.
	 * 
	 * @param savedInstanceState
	 */
	public void onCreate(final Bundle savedInstanceState) {
		onCreate(savedInstanceState, true);
	}

	/**
	 * Called when the activity is first created. Registers LocationListener.
	 * 
	 * @param savedInstanceState
	 * @param pDoGPSRecordingAndContributing
	 *            If <code>true</code>, it automatically contributes to the
	 *            OpenStreetMap Project in the background.
	 */
	public void onCreate(final Bundle savedInstanceState,
			final boolean pDoGPSRecordingAndContributing) {
		super.onCreate(savedInstanceState);

		// check for network
		ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// get state for both phone network and wifi
		if ((connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED)
				&& (connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED)) {
			Toast.makeText(this, R.string.network_unavailable,
					Toast.LENGTH_LONG).show();
		}

		// get screen to stay on
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
		wl.acquire();

		// register location listeners
		initLocation();

	}

	private LocationManager getLocationManager() {
		if (this.mLocationManager == null)
			this.mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		return this.mLocationManager;
	}

	/**
	 * Tests if the given provider is the best among all location providers
	 * available
	 * 
	 * @param myLocation
	 * @return true if the location is the best choice, false otherwise
	 */
	private boolean isBestProvider(Location myLocation) {
		if (myLocation == null)
			return false;
		boolean isBestProvider = false;
		String myProvider = myLocation.getProvider();
		boolean gpsCall = myProvider
				.equalsIgnoreCase(LocationManager.GPS_PROVIDER);
		boolean networkCall = myProvider
				.equalsIgnoreCase(LocationManager.NETWORK_PROVIDER);
		// get all location accuracy in meter; note that less is better!
		float gpsAccuracy = Float.MAX_VALUE;
		long gpsTime = 0;
		if (GpsLocationActivated) {
			Location lastGpsLocation = getLocationManager()
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (lastGpsLocation != null) {
				gpsAccuracy = lastGpsLocation.getAccuracy();
				gpsTime = lastGpsLocation.getTime();
			}
		}
		float networkAccuracy = Float.MAX_VALUE;
		if (NetworkLocationActivated) {
			Location lastNetworkLocation = getLocationManager()
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (lastNetworkLocation != null)
				networkAccuracy = lastNetworkLocation.getAccuracy();
		}
		float currentAccuracy = myLocation.getAccuracy();
		long currentTime = myLocation.getTime();
		// Use myLocation if:
		// 1. it's a gps location & network is disabled
		// 2. it's a gps loc & network activated
		// & gps accuracy is better than network
		// 3. it's a network loc & gps is disabled
		// 4. it's a network loc, gps enabled
		// & (network accuracy is better than gps
		// OR last network fix is newer than last gps fix+30seconds)
		boolean case1 = gpsCall && !NetworkLocationActivated;
		boolean case2 = gpsCall && NetworkLocationActivated
				&& currentAccuracy < networkAccuracy;
		boolean case3 = networkCall && !GpsLocationActivated;
		boolean case4 = networkCall
				&& GpsLocationActivated
				&& (currentAccuracy < gpsAccuracy || currentTime > gpsTime + 30000);
		if (case1 || case2 || case3 || case4) {
			isBestProvider = true;
		}
		return isBestProvider;
	}

	/**
	 * Defines the best location provider using isBestProvider() test
	 * 
	 * @return LocationProvider or null if none are available
	 */
	protected String bestProvider() {
		String bestProvider = null;
		if (NetworkLocationActivated
				&& isBestProvider(getLocationManager().getLastKnownLocation(
						LocationManager.NETWORK_PROVIDER))) {
			bestProvider = LocationManager.NETWORK_PROVIDER;
		} else if (GpsLocationActivated) {
			bestProvider = LocationManager.GPS_PROVIDER;
		}
		return bestProvider;
	}

	private void initLocation() {
		// initialize state of location providers and launch location listeners
		if (getLocationManager().isProviderEnabled(
				LocationManager.NETWORK_PROVIDER) == true) {
			NetworkLocationActivated = true;
			mNetworkLocationListener = new SampleLocationListener();
			getLocationManager().requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0,
					this.mNetworkLocationListener);
		}
		if (getLocationManager()
				.isProviderEnabled(LocationManager.GPS_PROVIDER) == true) {
			GpsLocationActivated = true;
			mGpsLocationListener = new SampleLocationListener();
			getLocationManager().requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0,
					this.mGpsLocationListener);
		}
		// get the best location using bestProvider()
		try {
			firstLocation = getLocationManager().getLastKnownLocation(
					bestProvider());
		} catch (Exception e) {
			Log.d("OpenSatNav", "Error getting the first location");
		}

		// test to see which location services are available
		if (!GpsLocationActivated) {
			if (!NetworkLocationActivated) {
				// no location providers are available, ask the user if they
				// want to go and change the setting
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setCancelable(true);
				builder.setMessage(R.string.location_services_disabled)
						.setCancelable(false).setPositiveButton(
								android.R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.dismiss();
										startActivity(new Intent(
												android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
									}
								}).setNegativeButton(android.R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			} else {
				// we have network location but no GPS, tell the user that
				// accuracy is bad because of this
				Toast.makeText(this, R.string.gps_disabled, Toast.LENGTH_LONG)
						.show();
			}
		} else if (!NetworkLocationActivated) {
			// we have GPS (but no network), this tells the user
			// that they might have to wait for a fix
			Toast.makeText(this, R.string.getting_gps_fix, Toast.LENGTH_LONG)
					.show();
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public void onLocationLost() {
		// TODO what do we need to do when a location's lost?
	}

	public abstract void onLocationChanged(final Location pLoc);

	/**
	 * Called when activity is destroyed. Unregisters LocationListener.
	 */
	@Override
	protected void onDestroy() {
		wl.release(); // allow the screen to turn off again
		super.onDestroy();
		try {
			getLocationManager().removeUpdates(mGpsLocationListener);
		} catch (IllegalArgumentException e) {
			// there's no gps location listener to disable
		}
		try {
			getLocationManager().removeUpdates(mNetworkLocationListener);
		} catch (IllegalArgumentException e) {
			// there's no network location listener to disable
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	/**
	 * Logs all Location-changes to <code>mRouteRecorder</code>.
	 * 
	 * @author plusminus
	 */
	private class SampleLocationListener implements LocationListener {
		public void onLocationChanged(final Location loc) {
			if (isBestProvider(loc)) {
				currentLocation = loc;
				OpenStreetMapActivity.this.onLocationChanged(loc);
				lastLocation = loc.getProvider();
			}
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			OpenStreetMapActivity.this.mNumSatellites = extras.getInt(
					"satellites", NOT_SET); // TODO Check on an actual device
			if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
				OpenStreetMapActivity.this.onLocationLost();
			}
		}

		public void onProviderEnabled(String a) { /* ignore */
		}

		public void onProviderDisabled(String a) { /* ignore */
		}
	}
}
