package com.example.monitorcar.bin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
	private String userName;
	private String realName;
	private String password;
	private String userAlias;
	private String email;
	private String phone;
	private String phone1 = "";
	private String identifyNum;
	private short roleID;
	private long innerID;
	private String companyIdentify;
	private int companyId;
	private String homeAddr = "";
	private String companyAddr = "";
	private String ESN = "";
	private int indentifyType;
	private String IMSI = "";
	private List operation = new ArrayList();

	public User(String UserName, String PassWord, String RealName,
			String Email, String Alias, String PhoneNumb, String HomeNumb,
			String HomeAddr, String CompAddr, String CompName) {
		this.userName = UserName;
		this.password = PassWord;
		this.realName = RealName;
		this.email = Email;
		this.userAlias = Alias;
		this.phone = PhoneNumb;
		this.phone1 = HomeNumb;
		this.homeAddr = HomeAddr;
		this.companyAddr = CompAddr;
		this.companyIdentify = CompName;
	}

	public User() {

	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserAlias() {
		return userAlias;
	}

	public void setUserAlias(String userAlias) {
		this.userAlias = userAlias;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIdentifyNum() {
		return identifyNum;
	}

	public void setIdentifyNum(String identifyNum) {
		this.identifyNum = identifyNum;
	}

	public short getRoleID() {
		return roleID;
	}

	public void setRoleID(short roleID) {
		this.roleID = roleID;
	}

	public long getInnerID() {
		return innerID;
	}

	public void setInnerID(long innerID) {
		this.innerID = innerID;
	}

	public String getHomeAddr() {
		return this.homeAddr;
	}

	public void setHomeAddr(String address) {
		this.homeAddr = address;
	}

	public String getCompanyIdentify() {
		return companyIdentify;
	}

	public void setCompanyIdentify(String companyIdentify) {
		this.companyIdentify = companyIdentify;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public String getCompanyAddr() {
		return companyAddr;
	}

	public void setCompanyAddr(String companyAddr) {
		this.companyAddr = companyAddr;
	}

	public String getEsn() {
		return ESN;
	}

	public void setEsn(String esn) {
		this.ESN = esn;
	}

	public int getIndentifyType() {
		return indentifyType;
	}

	public void setIndentifyType(int indentifyType) {
		this.indentifyType = indentifyType;
	}

	public String getIMSI() {
		return IMSI;
	}

	public void setIMSI(String iMSI) {
		IMSI = iMSI;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public List getOperation() {
		return operation;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}
}