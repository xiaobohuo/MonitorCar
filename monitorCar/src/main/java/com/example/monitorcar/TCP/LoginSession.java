package com.example.monitorcar.TCP;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LoginSession extends Session {
	private List<IResponseListener> listeners = new ArrayList<IResponseListener>();

	public LoginSession(Socket socket, IMessage msg) {
		super(socket, msg);
	}

	public LoginSession(Socket sock, IMessage msg, InnerRspListener listener,
			List<IResponseListener> lisList) {
		super(sock, msg, listener);
		this.listeners = lisList;
	}

	public void sendMessage(Pdu pdu) {
		super.sendMessage(pdu);
	}

	@Override
	public void handleMessage(IMessage msg) {
		notifyAdapter(msg);
		this.rspListener.onResponseRecv(msg);
	}

	private void notifyAdapter(IMessage event) {
		Iterator<IResponseListener> it = listeners.iterator();
		while (it.hasNext()) {
			IResponseListener adapter = (IResponseListener) it.next();
			adapter.onResponseRecv(event);
		}
	}
}
