package com.example.monitorcar.TCP;

import com.example.monitorcar.bin.Alarm;

public interface IAlarmListener {
	public void onRecvAlarm(Alarm alarm);
}
