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
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;
import android.net.NetworkInfo;

/**
 * Baseclass for Activities who want to contribute to the OpenStreetMap Project.
 * 
 * @author Nicolas Gramlich
 * 
 */
public abstract class OpenStreetMapActivity extends Activity implements OpenStreetMapConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected SampleLocationListener mLocationListener;

	protected boolean mDoGPSRecordingAndContributing;

	protected LocationManager mLocationManager;

	public int mNumSatellites = NOT_SET;

	protected PowerManager.WakeLock wl;

	protected Location temporaryLocation;

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
	public void onCreate(final Bundle savedInstanceState, final boolean pDoGPSRecordingAndContributing) {
		super.onCreate(savedInstanceState);

		// check for network
		ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// get state for both phone network and wifi
		if ((connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED)
				&& (connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED)) {
			Toast.makeText(this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
		}

		// get screen to stay on
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
		wl.acquire();

		// register location listener
		initLocation();

	}

	private LocationManager getLocationManager() {
		if (this.mLocationManager == null)
			this.mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		return this.mLocationManager;
	}

	private void initLocation() {
		this.mLocationListener = new SampleLocationListener();
		// last known location using network seems to be more recent generally
		temporaryLocation = getLocationManager().getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (temporaryLocation == null)
			temporaryLocation = getLocationManager().getLastKnownLocation(LocationManager.GPS_PROVIDER);

		// test to see if location services are available
		if (getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
			if (getLocationManager().isProviderEnabled(LocationManager.NETWORK_PROVIDER) == false) {
				//no location providers are available, ask the user if they want to go and change the setting
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setCancelable(true);
				builder.setMessage(R.string.location_services_disabled).setCancelable(false).setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
								startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();

			}

			else
				// we have network location but no GPS, tell the user that
				// accuracy is bad because of this
				Toast.makeText(this, R.string.gps_disabled, Toast.LENGTH_LONG).show();
		}
		// we have GPS but no network, this tells the user that they might have
		// to wait for a fix
		else if (getLocationManager().isProviderEnabled(LocationManager.NETWORK_PROVIDER) == false) {
			Toast.makeText(this, R.string.getting_gps_fix, Toast.LENGTH_LONG).show();
		}

		getLocationManager().requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this.mLocationListener);
		getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.mLocationListener);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	public void onLocationLost() {
		// try to get the location back (not sure if this actually works)
		getLocationManager().requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this.mLocationListener);
		getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.mLocationListener);
	}

	public abstract void onLocationChanged(final Location pLoc);

	/**
	 * Called when activity is destroyed. Unregisters LocationListener.
	 */
	@Override
	protected void onDestroy() {
		// allow the screen to turn off again
		wl.release();
		super.onDestroy();
		getLocationManager().removeUpdates(mLocationListener);
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
			if (loc != null) {
				currentLocation = loc;
				// if(loc.getProvider()==LocationManager.GPS_PROVIDER) {
				// getLocationManager().removeUpdates(mLocationListener);
				// getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER,
				// 0, 0, mLocationListener);
				// }
				OpenStreetMapActivity.this.onLocationChanged(loc);
			} else {
				OpenStreetMapActivity.this.onLocationLost();
			}
		}

		public void onStatusChanged(String a, int i, Bundle b) {
			OpenStreetMapActivity.this.mNumSatellites = b.getInt("satellites", NOT_SET); // TODO
			// Check
			// on
			// an
			// actual
			// device
		}

		public void onProviderEnabled(String a) { /* ignore */
		}

		public void onProviderDisabled(String a) { /* ignore */
		}
	}
}
