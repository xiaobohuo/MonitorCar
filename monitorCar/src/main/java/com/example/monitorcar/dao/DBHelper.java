package com.example.monitorcar.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public static String DB_NAME = "userlogininfo.db";
	public static int DB_VERSION = 1;

	public static String CREATE_TABLE = "create table if not exists userlogin (_id integer primary key autoincrement,"
			+ "inid text,username text,password text,companyid integer,loginstatus text) ";
	public static String DROP_TABLE = "drop table if exists userlogin";

	public DBHelper(Context context) {
		this(context, DB_NAME, null, DB_VERSION);
	}

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_TABLE);
		db.execSQL(CREATE_TABLE);
	}

	public static String booleanToString(boolean value) {
		return value == true ? "true" : "false";
	}

	public static boolean StringToBoolean(String value) {
		return value.equals("true") ? true : false;
	}

}
