package com.example.monitorcar.utils;

import android.content.Context;

import com.example.monitorcar.TCP.CommService;
import com.example.monitorcar.TCP.DataTransfer;
import com.example.monitorcar.TCP.Event;
import com.example.monitorcar.TCP.IMessage;
import com.example.monitorcar.TCP.Pdu;

public class TCPUtil {
	private static TCPUtil instance;
	private static Context context;

	public TCPUtil(Context context) {
		this.context = context;
	}

	public static TCPUtil getInstance(Context context) {
		if (instance == null) {
			instance = new TCPUtil(context);
		}
		return instance;
	}
	
	public byte[] getVehicleMgnSvrId(long insideId) {
		Pdu pdu = new Pdu();
		pdu.msgId = Event.CAR_GET_CAR_MANAGE_SERVER_ID_REQ;
		pdu.sender = Event.CLIENT_SERVER;
		pdu.receiver = DataTransfer.IntToByteArray(Event.LOCAL_SERVER);
		pdu.sendProcess = Event.CLIENT_USER_PROCESS;
		pdu.recvRrocess = Event.LOGIN_MAN_PROCESS;
		pdu.prototype = Event.PROTO_TCP;
		pdu.routerType = Event.ROUTE_APPOINT;
		pdu.sericeType = Event.COMMON_SERVICE;
		pdu.addDataUnit(Event.INSIDE_USER_ID_KEY, insideId);

		try {
			IMessage msg = CommService.getInstance().sendMessage(pdu,
					Event.CAR_GET_CAR_MANAGE_SERVER_ID_RSP);
			if (msg == null)
				return null;
			int code = DataTransfer.ByteArrayToInt(msg.getValue(Event.RSP_KEY));
			if (code == Event.RSP_OK) {
				return msg.getValue(Event.SERVERID_KEY);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public byte[] getUserMgnSvrId() {
		Pdu pdu = new Pdu();
		pdu.msgId = Event.USER_GET_USER_MANAGE_SERVER_ID_REQ;
		pdu.sender = Event.CLIENT_SERVER;
		pdu.receiver = DataTransfer.IntToByteArray(Event.LOCAL_SERVER);
		pdu.sendProcess = Event.CLIENT_USER_PROCESS;
		pdu.recvRrocess = Event.LOGIN_MAN_PROCESS;
		pdu.prototype = Event.PROTO_TCP;
		pdu.routerType = Event.ROUTE_APPOINT;
		pdu.sericeType = Event.COMMON_SERVICE;

		try {
			IMessage msg = CommService.getInstance().sendMessage(pdu,
					Event.USER_GET_USER_MANAGE_SERVER_ID_RSP);
			if (msg == null)
				return null;
			int code = DataTransfer.ByteArrayToInt(msg.getValue(Event.RSP_KEY));
			if (code == Event.RSP_OK) {
				return msg.getValue(Event.SERVERID_KEY);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public static void saveServerId(byte[] bytes) {
		try {
			PreferenceUtil.getInstance(context).setString(
					PreferenceUtil.SERVERID, new String(bytes, "ISO-8859-1"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] getServerId() {
		byte[] ret = null;
		String serverid = PreferenceUtil.getInstance(context).getString(
				PreferenceUtil.SERVERID, "");
		if (!serverid.equals("")) {
			try {
				ret = serverid.getBytes("ISO-8859-1");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
}
