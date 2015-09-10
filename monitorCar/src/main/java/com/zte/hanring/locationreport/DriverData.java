package com.zte.hanring.locationreport;

public class DriverData {
	private double latitude;
	private double longitude;
	private long gpsTime;
	private int heartBeat;

	public DriverData() {

	}

	public DriverData(double longitude, double latitude, long gpsTime,
			int heartBeat) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.gpsTime = gpsTime;
		this.heartBeat = heartBeat;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public long getGpsTime() {
		return gpsTime;
	}

	public void setGpsTime(long gpsTime) {
		this.gpsTime = gpsTime;
	}

	public int getHeartBeat() {
		return heartBeat;
	}

	public void setHeartBeat(int heartBeat) {
		this.heartBeat = heartBeat;
	}

}
