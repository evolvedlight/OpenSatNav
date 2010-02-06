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
package org.opensatnav;

import org.opensatnav.services.LocationHandler;
import org.opensatnav.services.TripStatistics;
import org.opensatnav.services.TripStatisticsListener;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class TripStatisticsService extends Service implements LocationListener {

	private static final String TAG = "OpenSatNav.TripStatisticsService";
	private static TripStatisticsService mTripStatistiticsService = null;
	private TripStatistics mTripStatistics;
	protected static LocationHandler mLocationHandler;
	private static TripStatisticsListener tripStatsController;
	
	public static TripStatisticsService getService() {
		return mTripStatistiticsService;
	}
	
	/* This method is not static, so that it 
	 * can be overridden for unit testing */
	public TripStatistics getTripStatistics() {
		return mTripStatistics;
	}

	public void setTripStatistics(TripStatistics stats) {
		mTripStatistics = stats;
	}

	public void onCreate() {
		Log.v(TAG, "onCreate()");
		
		if (mLocationHandler == null) {
			mLocationHandler = new LocationHandler(
				(LocationManager) getSystemService(Context.LOCATION_SERVICE),
				this, this);
		}
		if( mTripStatistics == null) {
			mTripStatistics = new TripStatistics();
		}
	}
	
	public synchronized void onStart(Intent intent, int startId) {
		Log.v(TAG, "onStart()");
		super.onStart(intent, startId);
		setForeground(true);
		mTripStatistiticsService = this;
		mLocationHandler.start();

		// Tried to do this from where the service is started, but 
		// onCreate() doesn't run until after some time later. 
		mTripStatistics.addTripStatsListener(tripStatsController);
	}
	
	public synchronized void onDestroy() {
		Log.v(TAG, "onDestroy()");
		super.onDestroy();
		internalStop();
	}
	
	public IBinder onBind(Intent i) {
		return null;
	}
	
	public static void start(Context ctx) {
		Log.v(TAG, "static start");
		ctx.startService(new Intent(ctx, TripStatisticsService.class));
	}
	
	public static void stop(Context ctx) {
		Log.v(TAG, "static stop");
		ctx.stopService(new Intent(ctx, TripStatisticsService.class));
	}
	
	protected void internalStop() {
		Log.v(TAG, "internalStop()");
		if (mLocationHandler != null) {
			mLocationHandler.stop();
		}
		
		mTripStatistics.removeAllTripStatsListeners();
	}

	public void resetStatistics() {
		mTripStatistics.resetStatistics();
	}

	@Override
	public void onLocationChanged(Location pLoc) {
		if (pLoc != null ) {
			Log.v(TAG, "Accuracy: " + pLoc.getAccuracy());
			if( pLoc.getAccuracy() < 200 ) {
				mTripStatistics.addNewLocationPoint(pLoc);
			}
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	public static void setController(TripStatisticsListener listener) {
		tripStatsController = listener;
	}
	
	
}
