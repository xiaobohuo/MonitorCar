package com.example.monitorcar.TCP;

import java.net.Socket;

public class AccessSession extends Session {
	public AccessSession(Socket socket, IMessage msg) {
		super(socket, msg);
	}

	public AccessSession(Socket socket, IMessage msg, IResponseListener adapter) {
		super(socket, msg, adapter);
	}

	public void sendMessage(Pdu pdu) {
		super.sendMessage(pdu);
	}

	@Override
	public void handleMessage(IMessage msg) {
		this.rspListener.onResponseRecv(msg);
	}

}
