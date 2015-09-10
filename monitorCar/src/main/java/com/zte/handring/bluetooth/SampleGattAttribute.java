package com.zte.handring.bluetooth;

import java.util.HashMap;

public class SampleGattAttribute {
	private static HashMap<String, String> attrs = new HashMap();
	public static String MCU_WATCH_UUID = "0000fff0-0000-1000-8000-00805f9b34fb";
	public static String MCU_WATCH_UUID_NOTIFICATION = "0000fff1-0000-1000-8000-00805f9b34fb";
	public static String MCU_WATCH_UUID_WRITE = "0000fff2-0000-1000-8000-00805f9b34fb";
	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
	
	static {
		attrs.put("0000fff0-0000-1000-8000-00805f9b34fb", "Read Write Service");

		attrs.put("0000fff1-0000-1000-8000-00805f9b34fb",
				"Read Characteristics");
		attrs.put("0000fff2-0000-1000-8000-00805f9b34fb",
				"Write Characteristics");
	}

	public static String lookup(String uuid, String defaultUUID) {
		String ret = attrs.get(uuid);
		return ret == null ? defaultUUID : ret;
	}
}
