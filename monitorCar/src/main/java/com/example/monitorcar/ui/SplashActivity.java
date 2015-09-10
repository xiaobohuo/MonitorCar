package com.example.monitorcar.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

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
import com.example.monitorcar.utils.TCPUtil;
import com.example.monitorcar.utils.ThreadPoolManager;

public class SplashActivity extends BaseActivity {

	private static final int ACCESS_SUCESS = 0x00;
	private static final int ACCESS_FAIL = 0x01;
	private static final int LOGIN = 0x02;

	private Intent intent;
	private IMessage msg;
	private Pdu pdu;

	private byte[] serverId = null;
	private UserLoginDao dao = null;
	
	private long inID = -1;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message message) {
			switch (message.what) {
			case ACCESS_SUCESS: // 接入成功
				TCPUtil.saveServerId(serverId);
				// 检查sharepreference中是否有登录信息
				inID = PreferenceUtil.getInstance(SplashActivity.this)
						.getLong(PreferenceUtil.INID, -1);
				if (inID != -1) {// 存在登录信息 从数据库中获取Userloginbin 并 登录
					UserLoginBin loginBin = dao.getUserLogin(inID);

					byte[] encrytPWD = new AES().encrypt(loginBin.passWord,
							loginBin.passWord);
					pdu = new Pdu();
					pdu.sender = Event.CLIENT_SERVER;
					pdu.receiver = DataTransfer
							.IntToByteArray(Event.LOCAL_SERVER);
					pdu.sendProcess = Event.CLIENT_USER_PROCESS;
					pdu.recvRrocess = Event.LOGIN_MAN_PROCESS;
					pdu.prototype = Event.PROTO_TCP;
					pdu.routerType = Event.ROUTE_APPOINT;
					pdu.sericeType = Event.COMMON_SERVICE;
					pdu.msgId = Event.USER_LOGIN_WITH_INSIDE_USER_ID_REQ;
					pdu.addDataUnit(Event.INSIDE_USER_ID_KEY, inID);

					pdu.addDataUnit(Event.USER_CLIENT_PASSWORD_KEY, encrytPWD);
					pdu.addDataUnit(Event.USER_CLIENT_TYPE_KEY, 0);
					ThreadPoolManager.getInstance().addTask(new Runnable() {

						@Override
						public void run() {
							msg = CommService.getInstance().sendMessage(pdu,
									Event.USER_LOGIN_RSP);
							int code = DataTransfer.ByteArrayToInt(msg
									.getValue(Event.RSP_KEY));
							Message tmp = Message.obtain();
							tmp.what = LOGIN;
							tmp.obj = code;
							handler.sendMessage(tmp);
						}
					});

				} else {// 没有登录信息 直接跳转到Login
					intent = new Intent(SplashActivity.this,
							LoginActivity.class);
					intent.putExtra(LoginActivity.ACCESSINFO, "接入成功");
				}
				break;
			case ACCESS_FAIL: // 接入失败
				intent = new Intent(SplashActivity.this, LoginActivity.class);
				intent.putExtra(LoginActivity.ACCESSINFO, "接入失败");
				break;
			case LOGIN:
				int code = (Integer) message.obj;
				switch (code) {
				case Event.RSP_OK:
					byte[] roleiD = msg.getValue(Event.KEY_ROLE_ID);
					int roleID = DataTransfer.bytesToShort(roleiD);

					if (roleID > 395) {
						intent = new Intent(SplashActivity.this,
								MainActivity.class);
						intent.putExtra(MainActivity.INSIDEID,inID);
					} else {
						intent = new Intent(SplashActivity.this,
								MainActivity.class);
					}
					break;
				case Event.RSP_FAIL:
					intent = new Intent(SplashActivity.this,
							LoginActivity.class);
					intent.putExtra(LoginActivity.ACCESSINFO, "");
					break;
				default:
					intent = new Intent(SplashActivity.this,
							LoginActivity.class);
					intent.putExtra(LoginActivity.ACCESSINFO, "");
					break;
				}
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
		setContentView(R.layout.splashactivity);

//		long time = System.currentTimeMillis();
		
		init();
		

		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				CommService.getInstance().init(SplashActivity.this);
				serverId = TCPUtil.getInstance(SplashActivity.this)
						.getUserMgnSvrId();
				if (CommService.getInstance().getHasInited() == true
						&& serverId != null) {
					Message message = Message.obtain();
					message.what = ACCESS_SUCESS;
					handler.sendMessage(message);
				} else {
					Message message = Message.obtain();
					message.what = ACCESS_FAIL;
					handler.sendMessage(message);
				}
			}
		});
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (intent == null) {
					intent = new Intent(SplashActivity.this,
							LoginActivity.class);
					intent.putExtra(LoginActivity.ACCESSINFO, "接入失败");
				}
				startActivity(intent);
				finish();
			}
		}, 3000);

		// 检查版本

	}

	private void init() {
		dao = new UserLoginDaoImpl(this);
	}

}
