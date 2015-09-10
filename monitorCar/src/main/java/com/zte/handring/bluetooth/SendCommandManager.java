package com.zte.handring.bluetooth;

import android.text.format.Time;

public class SendCommandManager {

	// 同步时间
	public static byte[] syncNowTime() {
		byte[] tmp = new byte[8];

		Time t = new Time();
		t.setToNow();
		int year = t.year;
		int month = t.month + 1;
		int date = t.monthDay;
		int hour = t.hour; // 0-23
		int minute = t.minute;
		int second = t.second;

		tmp[0] = (byte) 0xF4;
		tmp[1] = (byte) (year - 1900);
		tmp[2] = (byte) month;
		tmp[3] = (byte) date;
		tmp[4] = (byte) hour;
		tmp[5] = (byte) minute;
		tmp[6] = (byte) second;
		tmp[7] = 0x00;

		return tmp;
	}

	// 请求当天睡眠质量
	public static byte[] fetchTodaySleepQuality() {
		byte[] tmp = new byte[8];
		tmp[0] = (byte) 0xF8;
		tmp[1] = (byte) 0xAA;
		tmp[2] = 0x00;
		tmp[3] = 0x00;
		tmp[4] = 0x00;
		tmp[5] = 0x00;
		tmp[6] = 0x00;
		tmp[7] = 0x00;
		return tmp;
	}
	
	public static byte[] setClock(int hour, int minute){
		byte[] tmp = new byte[8];
		tmp[0] = (byte) 0xe0;
		tmp[1] = 0x00;
		tmp[2] = 0x00; // 手机检测闹钟时间关
//		tmp[2] = 0x01;  //手机检测闹钟时间开
		tmp[3] = 0x00; // 闹钟时间由手环检测
//		tmp[3] = 0x01; //闹钟时间由手机检测
//		tmp[3] = 0x11; //闹钟时间由手机检测且闹钟时间到
		tmp[4] = (byte) hour;  // 闹钟小时数
		tmp[5] = (byte) minute;  //闹钟分钟数
		tmp[6] = 0x00;
		tmp[7] = 0x00;
		
		return tmp;
	}
}
