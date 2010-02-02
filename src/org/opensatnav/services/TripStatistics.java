package org.opensatnav.services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.opensatnav.TripStatisticsController;

import android.location.Location;
import android.text.format.Formatter;

public class TripStatistics {
	
	public static final int METRIC = 0;
	public static final int ENGLISH = 1;

	private int pointsReceivedCount;
	private long tripStartTimeMilisec;
	private long lastPointTimeMilisec;
	private float tripDistanceMeters;
	
	// Previous Point
	private Location lastLocatPoint = null;
	
	private Collection<TripStatisticsListener> listeners = null;
	
	private DecimalFormat formatter;
	
	public TripStatistics() {
		initializeStats();
	}
	
	public TripStatistics(TripStatisticsListener firstListener) {
		initializeStats();
		
		// Tried to do this from where the service is started, but 
		// onCreate() doesn't run until after some time later. 
		addTripStatsListener(firstListener);
	}
	
	private void initializeStats() {
		pointsReceivedCount = 0;
		tripStartTimeMilisec = -1;
		lastPointTimeMilisec = -1;
		tripDistanceMeters = 0;
		
		formatter = new DecimalFormat();
	}
	
	public void addTripStatsListener(TripStatisticsListener listener) {
		if( listeners == null ) {
			listeners = new ArrayList<TripStatisticsListener>();
		}
		listeners.add(listener);
	}
	
	public void removeTripStatsListener(TripStatisticsListener listener) {
		if( listeners.contains(listener) ) {
			listeners.remove(listener);
		}
	}

	public void addNewLocationPoint(Location newLocatPoint) {
		pointsReceivedCount++;
		if( tripStartTimeMilisec == -1 ) {
			tripStartTimeMilisec = newLocatPoint.getTime();
		}
		lastPointTimeMilisec = newLocatPoint.getTime();

		if( lastLocatPoint != null ) {
			tripDistanceMeters += newLocatPoint.distanceTo(lastLocatPoint);
		}
		
		lastLocatPoint = newLocatPoint;
		
		if( pointsReceivedCount > 1 ) {
			callAllListeners(); // Can't deliver with just one point.
		}
	}
	
	private void callAllListeners() {
		if( listeners != null ) {
			for( TripStatisticsListener l : listeners) {
				l.tripStatisticsChanged(this);
			}
		}		
	}

	/** Returns the aver trip speed in m/s */
	public float getAverageTripSpeed() {
		return getTripDistance() / (getTripTime() / 1000);
	}

	/** Return total trip time in milisec */
	public long getTripTime() {
		return lastPointTimeMilisec - tripStartTimeMilisec;
	}

	/** Returns total trip distance in meters */
	public float getTripDistance() {
		return tripDistanceMeters;
	}

	/* Note: the instantenous speed is newPoint.getSpeed()  */
	public float getInstantSpeed() {
		return lastLocatPoint.getSpeed();
	}

	public void resetStatistics() {
		initializeStats();
	}

	public String getAverageTripSpeedString(int unitSystem) {
		return getSpeedString(getAverageTripSpeed(),unitSystem);
	}

	public String getInstantSpeedString(int unitSystem) {
		return getSpeedString(getInstantSpeed(),unitSystem);
	}

	private String getSpeedString(float speed, int unitSystem) {
		if( unitSystem == METRIC ) {
			formatter.applyLocalizedPattern("###.##");
			return formatter.format(speed * 3600f / 1000f) + " km/hr";
		} else {
			return "not implemented";  // TODO do it
		}
	}

	public String getTripTimeString(int unitSystem) {
		int tripTimeSec = Math.round(getTripTime() / 1000f);
		formatter.applyLocalizedPattern("#0");
		int hr = tripTimeSec / 3600;
		String hrStr = formatter.format(hr);
		
		formatter.applyLocalizedPattern("00");
		int min = (tripTimeSec - hr * 3600) / 60;
		String minStr = formatter.format(min);
		
		int sec = tripTimeSec % 60;
		String secStr = formatter.format(sec);
		return hrStr + ":" + minStr + ":" + secStr;
	}

	public String getTripDistanceString(int unitSystem) {
		String dist = "";
		if( unitSystem == METRIC ) {
			int distMeters = Math.round(getTripDistance());
			int km = distMeters / 1000;
			int m = distMeters % 1000;
			if( km > 0 ) {
				dist = km + " km "; 
			}
			dist += m + " m";
		} else {
			dist = "not implemented";  // TODO do it
		}
		return dist;
	}

	public static class TripStatisticsStrings {
		public String averSpeed;
		public String instSpeed;
		public String tripDistance;
		public String tripDuration;

		public String speedUnits;
		public String distanceUnits;
		public String elapsedTimeUnits;
	}
}
