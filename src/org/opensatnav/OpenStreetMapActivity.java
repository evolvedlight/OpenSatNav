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
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;

/**
 * Baseclass for Activities who want to contribute to the OpenStreetMap Project.
 * @author Nicolas Gramlich
 *
 */
public abstract class OpenStreetMapActivity extends Activity implements OpenStreetMapConstants {
	// ===========================================================
	// Constants
	// ===========================================================
	
	protected static final String PROVIDER_NAME = LocationManager.GPS_PROVIDER;

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
	 * Calls <code>onCreate(final Bundle savedInstanceState, final boolean pDoGPSRecordingAndContributing)</code> with <code>pDoGPSRecordingAndContributing == true</code>.<br/>
	 * That means it automatically contributes to the OpenStreetMap Project in the background.
	 * @param savedInstanceState
	 */
	public void onCreate(final Bundle savedInstanceState) {
		onCreate(savedInstanceState, true);
	}
	/**
	 * Called when the activity is first created. Registers LocationListener.
	 * @param savedInstanceState
	 * @param pDoGPSRecordingAndContributing If <code>true</code>, it automatically contributes to the OpenStreetMap Project in the background.
	 */
	public void onCreate(final Bundle savedInstanceState, final boolean pDoGPSRecordingAndContributing) {
		super.onCreate(savedInstanceState);
		// register location listener
		initLocation();
		
		//get screen to stay on
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		 wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK , "My Tag");
		 wl.acquire();

	}

	private LocationManager getLocationManager() {
		if(this.mLocationManager == null)
			this.mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		return this.mLocationManager; 
	}

	private void initLocation() {
		this.mLocationListener = new SampleLocationListener();
		temporaryLocation = getLocationManager().getLastKnownLocation(LocationManager.GPS_PROVIDER);
		getLocationManager().requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this.mLocationListener);
		getLocationManager().requestLocationUpdates(PROVIDER_NAME, 0, 0, this.mLocationListener);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================
	
	public abstract void onLocationLost();

	public abstract void onLocationChanged(final Location pLoc);
	
	/**
	 * Called when activity is destroyed. Unregisters LocationListener.
	 */
	@Override
	protected void onDestroy() {
		//allow the screen to turn off again
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
	 * @author plusminus
	 */
	private class SampleLocationListener implements LocationListener {
		public void onLocationChanged(final Location loc) {
			if (loc != null){
				currentLocation = loc;
				if(loc.getProvider()==LocationManager.GPS_PROVIDER) {
					getLocationManager().removeUpdates(mLocationListener);
					getLocationManager().requestLocationUpdates(PROVIDER_NAME, 0, 0, mLocationListener);
				}
				OpenStreetMapActivity.this.onLocationChanged(loc);
			}else{
				OpenStreetMapActivity.this.onLocationLost();
			}
		}

		public void onStatusChanged(String a, int i, Bundle b) {
			OpenStreetMapActivity.this.mNumSatellites = b.getInt("satellites", NOT_SET); // TODO Check on an actual device
		}
		
		public void onProviderEnabled(String a) { /* ignore  */ }
		public void onProviderDisabled(String a) { /* ignore  */ }
	}
}
