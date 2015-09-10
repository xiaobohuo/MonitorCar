package com.example.monitorcar.utils;

import android.content.Context;

import com.example.monitorcar.bin.UserLoginBin;
import com.example.monitorcar.dao.UserLoginDao;
import com.example.monitorcar.dao.UserLoginDaoImpl;

public class SaveUtil {
	private static SaveUtil instance = null;

	private static Context context;

	private UserLoginDao dao;

	public SaveUtil(Context context) {
		this.context = context;
		dao = new UserLoginDaoImpl(context);
	}

	public static SaveUtil getInstance(Context context) {

		if (instance == null) {
			instance = new SaveUtil(context);
		}
		return instance;
	}

	public void savePreference(UserLoginBin bin) {
		PreferenceUtil.getInstance(context).setString(PreferenceUtil.USERNAME,
				bin.userName);
		PreferenceUtil.getInstance(context).setString(PreferenceUtil.PASSWORD,
				bin.passWord);
		PreferenceUtil.getInstance(context).setLong(PreferenceUtil.INID,
				bin.insideID);
		PreferenceUtil.getInstance(context).setBoolean(PreferenceUtil.HASLOGIN,
				bin.loginStatus);
		PreferenceUtil.getInstance(context).setInt(PreferenceUtil.COMPANYID,
				bin.companyID);
	}

	public void saveDataBase(UserLoginBin bin) {
		if (dao.getUserLogin(bin.insideID) == null) {
			dao.insertUserLogin(bin);
		} else {
			dao.updateUserLogin(bin.insideID, bin.passWord, bin.loginStatus);
		}
	}

	public void saveUserStatusAfterLogout() {
		long inID = PreferenceUtil.getInstance(context).getLong(
				PreferenceUtil.INID, -1);
		UserLoginBin bin = dao.getUserLogin(inID);
		if (bin != null) {
			dao.updateUserLogin(bin.insideID, bin.passWord, false);
			PreferenceUtil.getInstance(context).setBoolean(
					PreferenceUtil.HASLOGIN, false);
		}
	}

}
