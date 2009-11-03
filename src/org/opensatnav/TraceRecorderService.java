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

import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.app.Notification;
import android.app.NotificationManager;
import android.os.Bundle;
import android.content.Intent;
import android.app.PendingIntent;
import android.os.IBinder;
import android.util.Log;

import org.andnav.osm.util.constants.OpenStreetMapConstants;
import org.anddev.openstreetmap.contributor.util.RouteRecorder;
import org.opensatnav.services.LocationHandler;

public class TraceRecorderService extends Service implements OpenStreetMapConstants,
			OpenSatNavConstants, LocationListener {

	private static final String TAG = "OpenSatNav.TraceRecorderService";

	protected static RouteRecorder mRouteRecorder;
	protected static boolean tracing = false;
	
	protected static LocationHandler mLocationHandler;
	protected static NotificationManager mNotificationManager;

	public static RouteRecorder getRouteRecorder() {
		return mRouteRecorder;
	}
	
	public static boolean isTracing() {
		return tracing;
	}
	
	public void onCreate() {
		Log.v(TAG, "onCreate()");
		if (mLocationHandler == null)
			mLocationHandler = new LocationHandler((LocationManager) getSystemService(Context.LOCATION_SERVICE), this, this);
		if (mNotificationManager == null)
			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if (mRouteRecorder == null)
			mRouteRecorder = new RouteRecorder();
	}
	
	public synchronized void onStart(Intent intent, int startId) {
		Log.v(TAG, "onStart()");
		super.onStart(intent, startId);
		if (!tracing) {
			Log.v(TAG, "onStart() internals");
			setForeground(true);
			mLocationHandler.start();
			Notification n = new Notification(R.drawable.icon,
							getString(R.string.start_trace_ticker), System.currentTimeMillis());
			Intent notificationIntent = new Intent(this, SatNavActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
			n.setLatestEventInfo(this,
							getString(R.string.app_name), getString(R.string.tracing_notification_text),
							contentIntent);
			n.flags = n.flags | n.FLAG_ONGOING_EVENT;	// | n.FLAG_NO_CLEAR;
			mNotificationManager.notify(TRACE_RECORDING_NOTIFICATION_ID, n);
			tracing = true;
		}
	}
	
	public synchronized void onDestroy() {
		super.onDestroy();
		internalStop();
		// mNotificationManager = null;
	}
	
	public IBinder onBind(Intent i) {
		return null;
	}
	
	protected static void internalStop() {
		Log.v(TAG, "internalStop()");
		if (mLocationHandler != null)
			mLocationHandler.stop();
		if (mNotificationManager != null)
			mNotificationManager.cancel(TRACE_RECORDING_NOTIFICATION_ID);
		tracing = false;
	}
	
	public static void start(Context ctx) {
		Log.v(TAG, "static start");
		ctx.startService(new Intent(ctx, TraceRecorderService.class));
	}
	
	public static void stop(Context ctx) {
		ctx.stopService(new Intent(ctx, TraceRecorderService.class));
		internalStop();
	}
	
	public static void resetTrace() {
		mRouteRecorder = new RouteRecorder();
	}
	
	public void onLocationChanged(final Location pLoc) {
		if (pLoc != null && tracing) {
			Log.v(TAG, "Accuracy: " + pLoc.getAccuracy());
			mRouteRecorder.add(pLoc);
		}
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {  /* ignore */
	}

	public void onProviderEnabled(String a) { /* ignore */
	}

	public void onProviderDisabled(String a) { /* ignore */
	}
}
