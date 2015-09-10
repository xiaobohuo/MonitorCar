package com.example.monitorcar.TCP;

import android.content.Context;


public interface ICommService {
 
	public void sendMessage(Pdu pdu);
	public IMessage sendMessage(Pdu pdu,int resEventNo);
	public void addAsyncListener(IResponseListener adapter);
	
	public void init(Context context);
}
