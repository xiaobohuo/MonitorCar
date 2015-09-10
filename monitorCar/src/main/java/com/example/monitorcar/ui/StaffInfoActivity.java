package com.example.monitorcar.ui;


import com.example.monitorcar.R;
import com.example.monitorcar.bin.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class StaffInfoActivity extends BaseActivity{
	
	public static final String USEBIN = "userbin";
	
	private TextView userName;
	private TextView realName;
	private TextView passWord;
	private TextView userAlias;
	private TextView role_desc;
	private TextView email;
	private TextView phonenum;
	private TextView homenum;
	private TextView identifyNum;
	private TextView homeAddr;
	private TextView companyAddr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myuserinfo);
		init();
	}

	private void init() {
		Intent intent = getIntent();
		User user = (User) intent.getSerializableExtra(USEBIN);
		userName = (TextView) findViewById(R.id.userinfo_usrname);
		userName.setText(user.getUserName());
		realName = (TextView) findViewById(R.id.userinfo_realusr);
		realName.setText(user.getRealName());
		userAlias = (TextView) findViewById(R.id.userinfo_alias);
		userAlias.setText(user.getUserAlias());
		role_desc = (TextView) findViewById(R.id.userinfo_role);
		if(user.getRoleID()<387)
		 role_desc.setText("司机用户");
		else 
		 role_desc.setText("公司管理员");
		email = (TextView) findViewById(R.id.userinfo_email);
		email.setText(user.getEmail());
		phonenum = (TextView) findViewById(R.id.userinfo_phonenum);
		phonenum.setText(user.getPhone());
		homenum = (TextView) findViewById(R.id.userinfo_homenum);
		homenum.setText(user.getPhone1());
		homeAddr = (TextView) findViewById(R.id.userinfo_homeaddr);
		homeAddr.setText(user.getHomeAddr());
		companyAddr = (TextView) findViewById(R.id.userinfo_compaddr);
		companyAddr.setText(user.getCompanyAddr());
	}
	
	public void back_call(View view) {
		onBackPressed();
	}
	
}
