package org.anddev.openstreetmap.contributor.util;


public class RecordedWayPoint extends RecordedGeoPoint {
	private String wayPointName;
	private String wayPointDescription;
	public RecordedWayPoint(int latitudeE6, int longitudeE6, long aTimeStamp, String wayPointName, String wayPointDescription) {
		super(latitudeE6, longitudeE6, aTimeStamp);
		this.wayPointName = wayPointName;
		this.wayPointDescription = wayPointDescription;
		// TODO Auto-generated constructor stub
	}
	
	public RecordedWayPoint(RecordedGeoPoint recordedGeoPoint, String name,	String description) {
		super(recordedGeoPoint.getLatitudeE6(), recordedGeoPoint.getLongitudeE6(), recordedGeoPoint.getTimeStamp());
		this.setWayPointName(name);
		this.setWayPointDescription(description);
		
	}

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