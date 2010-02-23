package org.anddev.openstreetmap.contributor.util;

import java.util.Date;


public class RecordedWayPoint extends RecordedGeoPoint {
	private String wayPointName;
	private String wayPointDescription;
	public RecordedWayPoint(long routeID, int latitudeE6, int longitudeE6, Date timestamp, double altitude, float accuracy, float bearing, float speed, String wayPointName, String wayPointDescription) {
		super(routeID, latitudeE6, longitudeE6, timestamp, altitude, speed, speed, speed);
		this.wayPointName = wayPointName;
		this.wayPointDescription = wayPointDescription;
		// TODO Auto-generated constructor stub
	}
	
	/*public RecordedWayPoint(RecordedGeoPoint recordedGeoPoint, String name,	String description) {
		super(recordedGeoPoint.getLatitudeE6(), recordedGeoPoint.getLongitudeE6(), recordedGeoPoint.getTimeStamp());
		this.setWayPointName(name);
		this.setWayPointDescription(description);
		
	}*/

	public String getWayPointName() {
		return this.wayPointName;
	}
	
	public String getWayPointDescription() {
		return this.wayPointDescription;
	}
	
	public void setWayPointName(String wayPointName) {
		this.wayPointName = wayPointName;
	}
	
	public void setWayPointDescription(String wayPointDescription) {
		this.wayPointDescription = wayPointDescription;
	}
	
}