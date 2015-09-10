package com.example.monitorcar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceUtil {
	private static PreferenceUtil instance;
	private SharedPreferences sharedPreferences;
	private String packageName = "";

	public static final String INID = "inid";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String COMPANYID = "companyid";
	public static final String SERVERID = "serverid";
	public static final String HASLOGIN = "haslogin";

	public PreferenceUtil(Context context) {
		packageName = context.getPackageName() + "_sharedpreference";
		sharedPreferences = context.getSharedPreferences(packageName,
				Context.MODE_PRIVATE);
	}

	public static PreferenceUtil getInstance(Context context) {
		if (instance == null) {
			instance = new PreferenceUtil(context);
		}
		return instance;
	}

	public void setInt(String key, int value) {
		Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public int getInt(String key, int defValue) {
		return sharedPreferences.getInt(key, defValue);
	}

	public void setString(String key, String value) {
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String getString(String key, String defValue) {
		return sharedPreferences.getString(key, defValue);
	}

	public void setBoolean(String key, boolean value) {
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public boolean getBoolean(String key, boolean defValue) {
		return sharedPreferences.getBoolean(key, defValue);
	}

	public void setLong(String key, long value) {
		Editor editor = sharedPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public long getLong(String key, long defValue) {
		return sharedPreferences.getLong(key, defValue);
	}
	
}
