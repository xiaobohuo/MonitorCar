package com.example.monitorcar.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.monitorcar.R;
import com.example.monitorcar.TCP.CommService;
import com.example.monitorcar.TCP.DataTransfer;
import com.example.monitorcar.TCP.Event;
import com.example.monitorcar.TCP.IMessage;
import com.example.monitorcar.TCP.Pdu;
import com.example.monitorcar.bin.User;
import com.example.monitorcar.utils.EnDeCodeUtil;
import com.example.monitorcar.utils.PreferenceUtil;
import com.example.monitorcar.utils.TCPUtil;
import com.example.monitorcar.utils.ThreadPoolManager;

public class UserDetailActivity extends BaseActivity {

	private static final int USERINFORECEIVED = 0x00;
	private static final int MODIFYSUCCESS = 0x01;
	private static final int MODIFYFAIL = 0x02;

	private EditText userName;
	private EditText realName;
	private EditText passWord;
	private EditText userAlias;
	private TextView role_desc;
	private EditText email;
	private EditText phonenum;
	private EditText homenum;
	private EditText identifyNum;
	private EditText homeAddr;
	private EditText companyAddr;

	private String mUserName = "";
	private String mTrueName = "";
	private String mPassWord = "";
	private String mUserAlias = "";
	private String mRole_desc;
	private String mEmail = "";
	private String mPhoneNumb = "";
	private String mHomeNumb = "";
	private String mHomeAddr = "";
	private String mCompAddr = "";
	private Short mRoleID;

	private User bin = new User();
	private long insideId = 0;
	private byte[] usrMgnSvrId = null;

	private IMessage msg;
	private Pdu pdu;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case USERINFORECEIVED:
				User tmp = (User) msg.obj;
				setEditText(tmp);
				break;

			case MODIFYSUCCESS:
				showShortToast("修改成功");
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindow().getDecorView()
						.getWindowToken(), 0);
				break;

			case MODIFYFAIL:
				showShortToast("修改失败");
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.myinfo);

		init();

		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				bin = queryUserWithInId(insideId);
				Message message = Message.obtain();
				message.what = USERINFORECEIVED;
				message.obj = bin;
				handler.sendMessage(message);
			}
		});
	}

	private void init() {
		insideId = PreferenceUtil.getInstance(this).getLong(
				PreferenceUtil.INID, -1);

		userName = (EditText) findViewById(R.id.info_usrname);
		realName = (EditText) findViewById(R.id.info_realusr);
		userAlias = (EditText) findViewById(R.id.info_alias);
		role_desc = (TextView) findViewById(R.id.info_role);
		email = (EditText) findViewById(R.id.info_email);
		phonenum = (EditText) findViewById(R.id.info_phonenum);
		homenum = (EditText) findViewById(R.id.info_homenum);
		homeAddr = (EditText) findViewById(R.id.info_homeaddr);
		companyAddr = (EditText) findViewById(R.id.info_compaddr);
	}

	private void setEditText(User user) {
		userName.setText(user.getUserName());
		realName.setText(user.getRealName());
		userAlias.setText(user.getUserAlias());
		if (user.getRoleID() > 387)
			role_desc.setText("公司管理员");
		else
			role_desc.setText("司机用户");
		email.setText(user.getEmail());
		phonenum.setText(user.getPhone());
		homenum.setText(user.getPhone1());
		homeAddr.setText(user.getHomeAddr());
		companyAddr.setText(user.getCompanyAddr());
	}

	public void btn_change_info(View v) {
		pdu = new Pdu();
		pdu.msgId = Event.USER_MYSELF_MODIFY_REQ;
		pdu.sender = Event.CLIENT_SERVER;
		pdu.receiver = TCPUtil.getInstance(this).getUserMgnSvrId();
		pdu.sendProcess = Event.CLIENT_USER_PROCESS;
		pdu.recvRrocess = Event.USER_MAN_PROCESS;
		pdu.prototype = Event.PROTO_TCP;
		pdu.routerType = Event.ROUTE_APPOINT;
		pdu.sericeType = Event.COMMON_SERVICE;

		pdu.addDataUnit(Event.INSIDE_USER_ID_KEY, insideId);
		pdu.addDataUnit(Event.SRC_INSIDE_USER_ID_KEY, insideId);

		if (!userName.getText().toString().equals(mUserName)) {
			pdu.addDataUnit(Event.KEY_USERNAME,
					EnDeCodeUtil.encode(userName.getText().toString()));
		}
		if (!email.getText().toString().equals(mEmail)) {
			pdu.addDataUnit(Event.KEY_EMAIL,
					EnDeCodeUtil.encode(email.getText().toString()));
		}
		if (!userAlias.getText().toString().equals(mUserAlias)) {
			pdu.addDataUnit(Event.KEY_USERALIAS,
					EnDeCodeUtil.encode(userAlias.getText().toString()));
		}
		if (!phonenum.getText().toString().equals(mPhoneNumb)) {
			pdu.addDataUnit(Event.KEY_PHONE1,
					EnDeCodeUtil.encode(phonenum.getText().toString()));
		}
		if (!homenum.getText().toString().equals(mHomeNumb)) {
			pdu.addDataUnit(Event.KEY_PHONE2,
					EnDeCodeUtil.encode(homenum.getText().toString()));
		}
		if (!homeAddr.getText().toString().equals(mHomeAddr)) {
			pdu.addDataUnit(Event.KEY_ADDRESS_HOME,
					EnDeCodeUtil.encode(homeAddr.getText().toString()));
		}
		if (!companyAddr.getText().toString().equals(mCompAddr)) {
			pdu.addDataUnit(Event.KEY_ADDRESS_OFFICE,
					EnDeCodeUtil.encode(companyAddr.getText().toString()));
		}

		if (!realName.getText().toString().equals(mTrueName)) {
			pdu.addDataUnit(Event.KEY_REALNAME,
					EnDeCodeUtil.encode(realName.getText().toString()));
		}

		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				msg = CommService.getInstance().sendMessage(pdu,
						Event.USER_MYSELF_MODIFY_RSP);
				int result = DataTransfer.ByteArrayToInt(msg
						.getValue(Event.RSP_KEY));
				Message message = Message.obtain();
				if (result == Event.RSP_OK) {
					message.what = MODIFYSUCCESS;
				} else {
					message.what = MODIFYFAIL;
				}
				handler.sendMessage(message);
			}
		});


	}

	protected User queryUserWithInId(long inId) {
		Pdu pdu = new Pdu();
		pdu.msgId = Event.USER_MYSELF_QUERY_REQ;
		pdu.sender = Event.CLIENT_SERVER;
		pdu.receiver = TCPUtil.getInstance(this).getUserMgnSvrId();
		pdu.sendProcess = Event.CLIENT_USER_PROCESS;
		pdu.recvRrocess = Event.USER_MAN_PROCESS;
		pdu.prototype = Event.PROTO_TCP;
		pdu.routerType = Event.ROUTE_APPOINT;
		pdu.sericeType = Event.COMMON_SERVICE;

		pdu.addDataUnit(Event.INSIDE_USER_ID_KEY, insideId);
		pdu.addDataUnit(Event.SRC_INSIDE_USER_ID_KEY, insideId);

		try {
			IMessage msg = CommService.getInstance().sendMessage(pdu,
					Event.USER_MYSELF_QUERY_RSP);
			int result = DataTransfer.ByteArrayToInt(msg
					.getValue(Event.RSP_KEY));
			if (result == Event.RSP_OK) {
				mUserName = EnDeCodeUtil.decodeStr(msg
						.getValue(Event.KEY_USERNAME));
				bin.setUserName(mUserName);

				mEmail = new String(msg.getValue(Event.KEY_EMAIL));
				bin.setEmail(mEmail);

				mRoleID = DataTransfer.bytesToShort(msg
						.getValue(Event.KEY_ROLE_ID));
				bin.setRoleID(mRoleID);

				mUserAlias = EnDeCodeUtil.decodeStr(msg
						.getValue(Event.KEY_USERALIAS));
				bin.setUserAlias(mUserAlias);

				mPhoneNumb = new String(msg.getValue(Event.KEY_PHONE1));
				bin.setPhone(mPhoneNumb);

				mHomeNumb = new String(msg.getValue(Event.KEY_PHONE2));
				bin.setPhone1(mHomeNumb);

				mHomeAddr = EnDeCodeUtil.decodeStr(msg
						.getValue(Event.KEY_ADDRESS_HOME));
				bin.setHomeAddr(mHomeAddr);

				mCompAddr = EnDeCodeUtil.decodeStr(msg
						.getValue(Event.KEY_ADDRESS_OFFICE));
				bin.setCompanyAddr(mCompAddr);

				String identifyNum = new String(
						msg.getValue(Event.KEY_IDENTIFY_NUM));
				bin.setIdentifyNum(identifyNum);

				mTrueName = EnDeCodeUtil.decodeStr(msg
						.getValue(Event.KEY_REALNAME));
				bin.setRealName(mTrueName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bin;
	}
	
	public void back_call(View v) {
		onBackPressed();
	}

}
