package com.example.monitorcar.dao;

import com.example.monitorcar.bin.UserLoginBin;

public interface UserLoginDao {
	public void insertUserLogin(UserLoginBin user);

	public void deleteUserLogin(long inid);

	public void updateUserLogin(long inid, String password, boolean loginstatus);

	public UserLoginBin getUserLogin(long inid);

}
