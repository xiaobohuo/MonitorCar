package com.example.monitorcar.utils;

import java.io.UnsupportedEncodingException;

public class EnDeCodeUtil {
	public static byte[] encode(String str) {
		try {
			byte[] data = str.getBytes("UTF-8");
			return data;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
	
	public static String decodeStr(byte[] data) {
		String ret = null;
		try {
			ret = new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			ret = new String(data);
		}
		return ret;
	}

}
