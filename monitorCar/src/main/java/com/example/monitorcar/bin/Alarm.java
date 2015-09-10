package com.example.monitorcar.bin;

public class Alarm {
	

	private String happenTime;
	private String alarmDesc;
	private int relatedRule;
	private String alarmType;
	private String target;
	private int alarmCode;
	private String alarmTitle;
	private String alarmDetail;
	private long alarmTime;
	private String addTxt;

	public String getAddTxt() {
		return addTxt;
	}

	public void setAddTxt(String addTxt) {
		this.addTxt = addTxt;
	}	
	
	public String getHappenTime() {
		return happenTime;
	}

	public void setHappenTime(String happenTime) {
		this.happenTime = happenTime;
	}

	public String getAlarmDesc() {
		return alarmDesc;
	}

	public void setAlarmDesc(String alarmDesc) {
		this.alarmDesc = alarmDesc;
	}

	public int getRelatedRule() {
		return relatedRule;
	}

	public void setRelatedRule(int relatedRule) {
		this.relatedRule = relatedRule;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public int getAlarmCode() {
		return alarmCode;
	}

	public void setAlarmCode(int alarmCode) {
		this.alarmCode = alarmCode;
	}

	public String getAlarmTitle() {
		return alarmTitle;
	}

	public void setAlarmTitle(String alarmTitle) {
		this.alarmTitle = alarmTitle;
	}

	public String getAlarmDetail() {
		return alarmDetail;
	}

	public void setAlarmDetail(String alarmDetail) {
		this.alarmDetail = alarmDetail;
	}

	public long getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(long alarmTime) {
		this.alarmTime = alarmTime;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.alarmTitle + "|" + this.alarmCode);
		return sb.toString();
	}

}

