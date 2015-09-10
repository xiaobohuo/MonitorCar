package com.example.monitorcar.services;

import java.io.UnsupportedEncodingException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.monitorcar.R;
import com.example.monitorcar.TCP.CommService;
import com.example.monitorcar.TCP.DataTransfer;
import com.example.monitorcar.TCP.Event;
import com.example.monitorcar.TCP.IMessage;
import com.example.monitorcar.TCP.IResponseListener;
import com.example.monitorcar.TCP.Pdu;

public class AlarmService extends Service implements IResponseListener {

	private long insideID = 0;
	private IMessage msg = null;
	private int number = 0;
	private AlarmBinder alarmBinder;

	public AlarmService() {
		CommService.getInstance().addAsyncListener(this);
		System.out.println("=======asynclistener added==========");
	}

	public class AlarmBinder extends Binder {
		AlarmService getService() {
			return AlarmService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			insideID = intent.getLongExtra("INSIDEID", 0);
		}
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onResponseRecv(IMessage event) {
		int eventNo = event.eventNo;
		switch (eventNo) {
		case Event.USER_MONITOR_ALARM_CLIENT_REQ:
			msg = event;
			String alarmTitle = decodeStr(msg
					.getValue(Event.MONITOR_ALARM_TITLE_KEY));
			String alarmDesc = decodeStr(msg
					.getValue(Event.MONITOR_ALARM_DETAILS_KEY));
			long time = DataTransfer.bytesToLong((byte[]) msg
					.getValue(Event.MONITOR_ALARM_TIME_KEY));

			showNotification(alarmTitle, alarmDesc, time);
			// Thread thd = new Thread(new Runnable() {
			//
			// @Override
			// public void run() {
			// CommService.getInstance().sendMessage(
			// recvAlarmResponse(msg));
			// }
			// });
			// thd.start();
			break;

		default:
			break;
		}

	}

	private void showNotification(String title, String descrip, long time) {
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher).setContentTitle(title)
				.setContentText(descrip).setDefaults(Notification.DEFAULT_ALL)
				.build();
		manager.notify(number++, notification);

	}

	public String decodeStr(byte[] data) {
		String ret = null;
		try {
			ret = new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@SuppressWarnings("unused")
	private Pdu recvAlarmResponse(IMessage msg) {
		Pdu pdu = new Pdu();
		pdu.msgId = Event.USER_MONITOR_ALARM_CLIENT_RSP;
		pdu.sender = DataTransfer.ByteArrayToInt(msg.getValue("nRecver"));
		pdu.receiver = msg.getValue("nSender");
		pdu.sendProcess = (short) DataTransfer.toInt(msg
				.getValue("nRecvProcess"));
		pdu.recvRrocess = (short) DataTransfer.toInt(msg
				.getValue("nSendProcess"));
		pdu.prototype = Event.PROTO_TCP;
		pdu.routerType = Event.ROUTE_APPOINT;
		pdu.sericeType = Event.COMMON_SERVICE;

		pdu.addDataUnit(Event.SRC_INSIDE_USER_ID_KEY, insideID);
		pdu.addDataUnit(Event.MONITOR_ALARM_SEQUENCE_KEY,
				msg.getValue(Event.MONITOR_ALARM_SEQUENCE_KEY));
		return pdu;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return alarmBinder;
	}

}
