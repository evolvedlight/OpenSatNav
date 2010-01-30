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
		float averSpeed =  getAverageTripSpeed() * speedConvFactor(unitSystem);
		formatter.applyLocalizedPattern("###.##");
		return formatter.format(averSpeed);
	}

	private float speedConvFactor(int unitSystem) {
		if( unitSystem == METRIC ) {
			return 3600 / 1000f;
		} else {
			return -1;  // TODO do it
		}
	}

	public String getInstantSpeedString(int unitSystem) {
		float instSpeed =  getInstantSpeed() * speedConvFactor(unitSystem);
		formatter.applyLocalizedPattern("###.##");
		return formatter.format(instSpeed);
	}

	public String getTripTimeString(int unitSystem) {
		float tripTime = getTripTime() / 1000f / 60;
		formatter.applyLocalizedPattern("###.#");
		return formatter.format(tripTime);
	}

	public String getTripDistanceString(int unitSystem) {
		float dist = getTripDistance() * distanceConvFactor(unitSystem);
		formatter.applyLocalizedPattern("##0.000");
		return formatter.format(dist);
	}

	private float distanceConvFactor(int unitSystem) {
		if( unitSystem == METRIC ) {
			return 1 / 1000f;
		} else {
			return -1;  // TODO do it
		}
	}

	public CharSequence getSpeedUnits(int unitSystem) {
		if( unitSystem == METRIC ) {
			return "km/hr";
		} else {
			return "mph";
		}
	}

	public CharSequence getDistanceUnits(int unitSystem) {
		if( unitSystem == METRIC ) {
			return "km";
		} else {
			return "mi";
		}
	}

	public CharSequence getElapsedTimeUnits(int unitSystem) {
		return "min";
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
