package com.example.monitorcar.TCP;


public interface ISession {
	public void sendMessage(Pdu pdu);

	// handle message
	public void handleMessage(IMessage msg);
	
	public void closeSession();
}
