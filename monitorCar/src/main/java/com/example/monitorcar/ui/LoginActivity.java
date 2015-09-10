package com.example.monitorcar.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.monitorcar.utils.PreferenceUtil;
import com.example.monitorcar.utils.SaveUtil;
import com.example.monitorcar.utils.ThreadPoolManager;

public class LoginActivity extends BaseActivity {

	public static final String ACCESSINFO = "info_of_acess";
	private static final int BEFORELOGINRESPONSE = 0x00;
	private static final int LOGINRESPONSE = 0x01;

	private UserLoginDao dao;
	private Pdu pdu;
	private IMessage msg;

	private Drawable mIconPerson;
	private Drawable mIconLock;

	private EditText username;
	private EditText password;
	private EditText verifycode;

	private TextView topText;
	private Button loginbtn;
	private Button registerbtn;

	private LinearLayout ll_verify;

	private String uName;
	private String pwd;
	private String verifyCode = "";
	private long inId = -1;
	private int companyID = -1;
	private int roleIDint;

	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);

		String toast = getIntent().getExtras().getString(ACCESSINFO);
		if(!toast.equals("")){
			showShortToast(toast);
		}
		
		init();

		// 获取上次登录用户信息
		username.setText(PreferenceUtil.getInstance(this).getString(
				PreferenceUtil.USERNAME, ""));
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message message) {
			switch (message.what) {
			case BEFORELOGINRESPONSE:
				int code = (Integer) message.obj;
				if (code == Event.RSP_OK) {
					byte[] imgdata = msg.getValue(Event.VERIFY_CODE_VER_KEY);
					if (imgdata != null) {
						ImageView vcode = (ImageView) findViewById(R.id.img_verifycode);
						ll_verify.setVisibility(View.VISIBLE);
						vcode.setImageBitmap(Bytes2Bimap(imgdata));
					}
				}
				if (code == Event.RSP_lOGIN_NONEXSIT_USERID) {
					showShortToast("用户名不存在");
				}
				break;
			case LOGINRESPONSE:
				int result = (Integer) message.obj;
				switch (result) {
				case Event.RSP_OK:
					companyID = DataTransfer.bytesToShort(msg
							.getValue(Event.USER_COMPANY_ID_KEY));
					byte[] roleID = msg.getValue(Event.KEY_ROLE_ID);
					roleIDint = DataTransfer.bytesToShort(roleID);
					// 页面跳转
					showShortToast("登录成功");

					UserLoginBin bin = new UserLoginBin();
					bin.insideID = inId;
					bin.userName = uName;
					bin.passWord = pwd;
					bin.companyID = companyID;
					bin.loginStatus = true;

					SaveUtil.getInstance(LoginActivity.this)
							.savePreference(bin);

					SaveUtil.getInstance(LoginActivity.this).saveDataBase(bin);

					Intent intent = new Intent();
					if (roleIDint > 395) {
						intent.setClass(LoginActivity.this, MainActivity.class);
						intent.putExtra("GOT_USER_INSIDEID", inId);
						startActivity(intent);
						password.setText("");
						ll_verify.setVisibility(View.GONE);
					} else {
						//跳转至手环主界面
//						intent.setClass(LoginActivity.this, cls)
					}

					break;
				case Event.RSP_FAIL:
					showShortToast("无响应");
					break;
				case Event.RSP_PARA_ERROR:
					showShortToast("参数错误");
					break;
				case Event.RSP_lOGIN_NONEXSIT_USERID:
					showShortToast("用户名不存在");
					break;
				case Event.RSP_lOGIN_PASSWORD_ERROR:
					showShortToast("密码错误");
					password.setText("");
					byte[] imgdata = msg.getValue(Event.VERIFY_CODE_VER_KEY);
					if (imgdata != null) {
						ImageView vcode = (ImageView) findViewById(R.id.img_verifycode);
						ll_verify.setVisibility(View.VISIBLE);
						vcode.setImageBitmap(Bytes2Bimap(imgdata));
					}
					break;
				case Event.RSP_USER_IN_LOGIN:
					showShortToast("用户已在登录过程中");
					byte[] imgdata1 = msg.getValue(Event.VERIFY_CODE_VER_KEY);
					if (imgdata1 != null) {
						ImageView vcode = (ImageView) findViewById(R.id.img_verifycode);
						ll_verify.setVisibility(View.VISIBLE);
						vcode.setImageBitmap(Bytes2Bimap(imgdata1));
					}
				default:
					break;
				}
				break;
			default:
				break;
			}
		}

	};

	private void init() {
		dao = new UserLoginDaoImpl(this);

		ll_verify = (LinearLayout) findViewById(R.id.ll_verifycode);
		verifycode = (EditText) findViewById(R.id.verifycode);

		mIconPerson = getResources().getDrawable(R.drawable.txt_person_icon);
		mIconPerson.setBounds(10, 5, 100, 100);
		mIconLock = getResources().getDrawable(R.drawable.txt_lock_icon);
		mIconLock.setBounds(10, 5, 100, 100);

		username = (EditText) findViewById(R.id.username);
		username.setCompoundDrawables(mIconPerson, null, null, null);
		password = (EditText) findViewById(R.id.password);
		password.setCompoundDrawables(mIconLock, null, null, null);
		password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					uName = username.getText().toString();
					if (!uName.equals("")) {
						UserLoginBin bin = dao.getUserLogin(inId);
						inId = bin == null ? -1 : bin.insideID;
						if (inId != -1) {
							beforeLogin(inId);
						} else {
							beforeLogin(uName);
						}

					}
				}
			}
		});

		topText = (TextView) findViewById(R.id.topname);
		topText.setTextSize(24.0f);

		loginbtn = (Button) findViewById(R.id.loginbtn);
		registerbtn = (Button) findViewById(R.id.registerbtn);
		registerbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			}
		});

		loginbtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.getBackground().setAlpha(80);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					v.getBackground().setAlpha(255);
				}
				return false;
			}

		});

		registerbtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.getBackground().setAlpha(80);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					v.getBackground().setAlpha(255);
				}
				return false;
			}

		});
	}

	public void login_login(View v) {
		uName = username.getText().toString();
		pwd = password.getText().toString();
		if (ll_verify.getVisibility() == View.VISIBLE) {
			verifyCode = verifycode.getText().toString();
		}

		if (uName.equals("") || pwd.equals("")) {
			showShortToast("请输入用户名密码");
			return;
		}

//		 Intent intent = new Intent();
//		 intent.setClass(LoginActivity.this, MainActivity.class);
//		 startActivity(intent);

		loginWithInsideId(inId, pwd, verifyCode, 1, Event.INSIDE_ID_LOGIN_TYPE);

	}

	public void loginWithInsideId(long usrID, String pwd, String verifyCode,
			int iTeminalType, int loginType) {
		pdu = new Pdu();
		pdu.msgId = Event.USER_LOGIN_WITH_INSIDE_USER_ID_REQ;// Event.USER_LOGIN_WITH_OUTSIDE_USER_ID_REQ;
		pdu.sender = Event.CLIENT_SERVER;
		pdu.receiver = DataTransfer.IntToByteArray(Event.LOCAL_SERVER);
		pdu.sendProcess = Event.CLIENT_USER_PROCESS;
		pdu.recvRrocess = Event.LOGIN_MAN_PROCESS;
		pdu.prototype = Event.PROTO_TCP;
		pdu.routerType = Event.ROUTE_APPOINT;
		pdu.sericeType = Event.COMMON_SERVICE;

		pdu.addDataUnit(Event.INSIDE_USER_ID_KEY, usrID);
		pdu.addDataUnit(Event.USER_CLIENT_PASSWORD_KEY,
				new AES().encrypt(pwd, pwd));
		if (!verifyCode.trim().equals("")) {
			pdu.addDataUnit(Event.VERIFY_CODE_STR_KEY, verifyCode);
		}
		pdu.addDataUnit(Event.USER_CLIENT_TYPE_KEY, 1);

		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				msg = CommService.getInstance().sendMessage(pdu,
						Event.USER_LOGIN_RSP);
				int result = DataTransfer.ByteArrayToInt(msg
						.getValue(Event.RSP_KEY));
				Message message = Message.obtain();
				message.obj = result;
				message.what = LOGINRESPONSE;
				handler.sendMessage(message);
			}
		});

	}

	public void beforeLogin(long insideId) {
		pdu = new Pdu();
		pdu.msgId = Event.PRE_LOGIN_WITH_INSIDE_USER_ID_REQ;
		pdu.sender = Event.CLIENT_SERVER;
		pdu.receiver = DataTransfer.IntToByteArray(Event.LOCAL_SERVER);
		pdu.sendProcess = Event.CLIENT_USER_PROCESS;
		pdu.recvRrocess = Event.LOGIN_MAN_PROCESS;
		pdu.prototype = Event.PROTO_TCP;
		pdu.routerType = Event.ROUTE_APPOINT;
		pdu.sericeType = Event.COMMON_SERVICE;

		pdu.addDataUnit(Event.INSIDE_USER_ID_KEY, insideId);

		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				msg = CommService.getInstance().sendMessage(pdu,
						Event.PRE_LOGIN_WITH_INSIDE_USER_ID_RSP);
				int code = DataTransfer.ByteArrayToInt(msg
						.getValue(Event.RSP_KEY));
				Message tmpMessage = Message.obtain();
				tmpMessage.what = BEFORELOGINRESPONSE;
				tmpMessage.obj = code;
				handler.sendMessage(tmpMessage);
			}
		});
	}

	public void beforeLogin(String userName) {
		pdu = new Pdu();
		pdu.msgId = Event.PRE_LOGIN_WITH_OUTSIDE_USER_ID_REQ;
		pdu.sender = Event.CLIENT_SERVER;
		pdu.receiver = DataTransfer.IntToByteArray(Event.LOCAL_SERVER);
		pdu.sendProcess = Event.CLIENT_USER_PROCESS;
		pdu.recvRrocess = Event.LOGIN_MAN_PROCESS;
		pdu.prototype = Event.PROTO_TCP;
		pdu.routerType = Event.ROUTE_APPOINT;
		pdu.sericeType = Event.COMMON_SERVICE;

		pdu.addDataUnit(Event.OUTSIDE_USER_ID_KEY, userName);

		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				msg = CommService.getInstance().sendMessage(pdu,
						Event.PRE_LOGIN_WITH_OUTSIDE_USER_ID_RSP);
				int code = DataTransfer.ByteArrayToInt(msg
						.getValue(Event.RSP_KEY));
				inId = DataTransfer.bytesToLong(msg
						.getValue(Event.INSIDE_USER_ID_KEY));
				Message tmpMessage = Message.obtain();
				tmpMessage.what = BEFORELOGINRESPONSE;
				tmpMessage.obj = code;
				handler.sendMessage(tmpMessage);
			}
		});

	}

	public Bitmap Bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	@Override
	public void onBackPressed() {

		if ((System.currentTimeMillis() - exitTime) > 2000) {
			showShortToast("再按一次退出程序");
			exitTime = System.currentTimeMillis();
		} else {
			finish();
		}

	}

}
