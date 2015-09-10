package com.example.monitorcar.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.monitorcar.bin.UserLoginBin;

public class UserLoginDaoImpl implements UserLoginDao {

	private DBHelper helper;

	public UserLoginDaoImpl(Context context) {
		helper = new DBHelper(context);
	}

	@Override
	public void insertUserLogin(UserLoginBin user) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(
				"insert into userlogin (inid,username,password,companyid,loginstatus) values (?,?,?,?,?)",
				new Object[] { Long.toString(user.insideID), user.userName,
						user.passWord, user.companyID,
						DBHelper.booleanToString(user.loginStatus) });
		db.close();
	}

	@Override
	public void deleteUserLogin(long inid) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from userlogin where inid = ?",
				new Object[] { Long.toString(inid) });
		db.close();
	}

	@Override
	public void updateUserLogin(long inid, String password, boolean loginstatus) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(
				"update userlogin set password=? and loginstatus=? where inid=?",
				new Object[] { password, DBHelper.booleanToString(loginstatus),
						Long.toString(inid) });
		db.close();
	}

	@Override
	public UserLoginBin getUserLogin(long inid) {
		SQLiteDatabase db = helper.getWritableDatabase();
		List<UserLoginBin> bins = new ArrayList<UserLoginBin>();
		Cursor cursor = null;
		cursor = db.rawQuery("select * from userlogin where inid=?",
				new String[] { Long.toString(inid) });
		while (cursor.moveToNext()) {
			UserLoginBin bin = new UserLoginBin();
			bin.insideID = Long.parseLong(cursor.getString(cursor
					.getColumnIndex("inid")));
			bin.userName = cursor.getString(cursor.getColumnIndex("username"));
			bin.passWord = cursor.getString(cursor.getColumnIndex("password"));
			bin.companyID = cursor.getInt(cursor.getColumnIndex("companyid"));
			bin.loginStatus = DBHelper.StringToBoolean(cursor.getString(cursor
					.getColumnIndex("loginstatus")));
			bins.add(bin);
		}
		cursor.close();
		db.close();
		if (bins.size() > 0) {
			return bins.get(0);
		}
		return null;
	}

}
