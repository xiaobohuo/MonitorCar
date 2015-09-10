package com.example.monitorcar.bin;

public class Vehicle {
	private int type;
	private String plateNum;
	private String deviceID;
	private long insideId;
	private long longitude;
	private long latitude;
	private long speed;
	private String companyName;
	private int companyInsideId;

	public Vehicle() {

	}

	public Vehicle(short type, String plateNumber, String deviceID) {
		this.setDeviceID(deviceID);
		this.setPlateNum(plateNumber);
		this.setType(type);
	}

	public boolean ledTeam(int column) {
		return true;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getPlateNum() {
		return plateNum;
	}

	public void setPlateNum(String plateNum) {
		this.plateNum = plateNum;
	}

	public long getInsideId() {
		return insideId;
	}

	public void setInsideId(long insideId) {
		this.insideId = insideId;
	}

	public long getLongitude() {
		return longitude;
	}

	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}

	public long getLatitude() {
		return latitude;
	}

	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}

	public long getSpeed() {
		return speed;
	}

	public void setSpeed(long speed) {
		this.speed = speed;
	}

	public String getCompanyId() {
		return companyName;
	}

	public void setCompanyId(String companyId) {
		this.companyName = companyId;
	}

	public int getCompanyInsideId() {
		return companyInsideId;
	}

	public void setCompanyInsideId(int companyInsideId) {
		this.companyInsideId = companyInsideId;
	}

}
