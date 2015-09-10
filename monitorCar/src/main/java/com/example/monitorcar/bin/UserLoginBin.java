package com.example.monitorcar.bin;

public class UserLoginBin {
	public String userName;
	public long insideID;
	public int companyID;
	public String passWord;
	public boolean loginStatus;

	public int getCompanyID() {
		return companyID;
	}

	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String outID) {
		this.userName = outID;
	}

	public long getInsideID() {
		return insideID;
	}

	public void setInsideID(long insideID) {
		this.insideID = insideID;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public boolean isLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(boolean loginStatus) {
		this.loginStatus = loginStatus;
	}
}
