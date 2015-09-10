package com.example.monitorcar.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.monitorcar.R;
import com.example.monitorcar.TCP.AES;
import com.example.monitorcar.TCP.CommService;
import com.example.monitorcar.TCP.DataTransfer;
import com.example.monitorcar.TCP.Event;
import com.example.monitorcar.TCP.IMessage;
import com.example.monitorcar.TCP.Pdu;
import com.example.monitorcar.bin.UserLoginBin;
import com.example.monitorcar.dao.UserLoginDao;
import com.example.monitorcar.dao.UserLoginDaoImpl;
import com.example.monitorcar.listeners.MyOnTouchListener;
import com.example.monitorcar.utils.EnDeCodeUtil;
import com.example.monitorcar.utils.SaveUtil;
import com.example.monitorcar.utils.TCPUtil;
import com.example.monitorcar.utils.ThreadPoolManager;

public class RegisterActivity extends BaseActivity implements OnClickListener {

	private static final int REGISTERTIMEOUT = 0x00;
	private static final int REGISTERFAIL = 0x01;
	private static final int REGISTERSUCCESS = 0x02;
	private boolean received = false;

	private EditText userName;
	private EditText passWord;
	private EditText confirmPassword;

	private Button btnRegister;
	private Button btnBack;

	private String mUserName = "";
	private String mPassWord = "";
	private String mConfirmPassword = "";

	private Pdu pdu;
	private byte[] encrytPWD;
	private IMessage msg;

	private long insideId = -1;
	private UserLoginDao dao;

	private MyOnTouchListener myOnTouchListener = new MyOnTouchListener();

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			closeLoadingDialog();
			switch (msg.what) {
			case REGISTERTIMEOUT:
				showShortToast("超时");
				break;
			case REGISTERFAIL:
				showShortToast("注册失败");
				break;
			case REGISTERSUCCESS:
				showShortToast("注册成功");
				insideId = (Long) msg.obj;
				UserLoginBin bin = new UserLoginBin();
				bin.insideID = insideId;
				bin.userName = mUserName;
				bin.passWord = mPassWord;
				bin.loginStatus = true;
				// 存入数据库
				SaveUtil.getInstance(RegisterActivity.this).savePreference(bin);
				SaveUtil.getInstance(RegisterActivity.this).saveDataBase(bin);
				Intent intent = new Intent(RegisterActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
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
		setContentView(R.layout.register);

		init();
	}

	private void init() {
		dao = new UserLoginDaoImpl(this);

		userName = (EditText) findViewById(R.id.main_register_edittext_username);
		passWord = (EditText) findViewById(R.id.main_register_edittext_password);
		confirmPassword = (EditText) findViewById(R.id.main_register_edittext_confirm_password);

		passWord.setTransformationMethod(new PasswordTransformationMethod());
		passWord.setTypeface(Typeface.DEFAULT);
		confirmPassword
				.setTransformationMethod(new PasswordTransformationMethod());

		btnRegister = (Button) findViewById(R.id.main_register_btn_register);
		btnRegister.setOnTouchListener(myOnTouchListener);
		btnRegister.setOnClickListener(this);

		btnBack = (Button) findViewById(R.id.register_btn_back);
		btnBack.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_btn_back:
			onBackPressed();
			break;
		case R.id.main_register_btn_register:
			if (checkData()) {
				ThreadPoolManager.getInstance().addTask(new Runnable() {

					@Override
					public void run() {
						register();
					}
				});
				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						if (!received) {
							Message message = Message.obtain();
							message.what = REGISTERTIMEOUT;

						}
					}
				}, 20 * 1000);
				// showLoadingDialog
				showLoadingDialog("注册中");
			}
			break;

		default:
			break;
		}
	}

	protected void register() {
		pdu = new Pdu();
		pdu.msgId = Event.USER_PERSONAL_REG_REQ;
		pdu.sender = Event.CLIENT_SERVER;
		pdu.receiver = TCPUtil.getServerId();

		pdu.sendProcess = Event.CLIENT_USER_PROCESS;
		pdu.recvRrocess = Event.USER_MAN_PROCESS;
		pdu.prototype = Event.PROTO_TCP;
		pdu.routerType = Event.ROUTE_APPOINT;
		pdu.sericeType = Event.COMMON_SERVICE;

		pdu.addDataUnit(Event.KEY_USERNAME, EnDeCodeUtil.encode(mUserName));
		encrytPWD = new AES().encrypt(mPassWord, mUserName + "SG");
		pdu.addDataUnit(Event.KEY_PASSWORD, encrytPWD);

		msg = CommService.getInstance().sendMessage(pdu,
				Event.USER_PERSONAL_REG_RSP);

		if (msg != null) {
			received = true;
			int res = DataTransfer.ByteArrayToInt(msg.getValue(Event.RSP_KEY));
			if (res == 0) {
				insideId = DataTransfer.bytesToLong(msg
						.getValue(Event.INSIDE_USER_ID_KEY));

				Message message = Message.obtain();
				message.obj = insideId;
				message.what = REGISTERSUCCESS;
				handler.sendMessage(message);
			} else {
				Message message = Message.obtain();
				message.what = REGISTERFAIL;
				handler.sendMessage(message);
			}
		}
	}

	private boolean checkData() {
		mUserName = userName.getText().toString().trim();
		mPassWord = passWord.getText().toString().trim();
		mConfirmPassword = confirmPassword.getText().toString().trim();
		if (mUserName.equals("")) {
			showShortToast("用户名不能空");
			return false;
		} else if (mPassWord.length() < 6) {
			showShortToast("密码至少6位");
			return false;
		} else if (!mPassWord.equals(mConfirmPassword)) {
			showShortToast("两次密码不一致");
			return false;
		}
		return true;
	}
}
