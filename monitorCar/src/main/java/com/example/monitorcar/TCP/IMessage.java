package com.example.monitorcar.TCP;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public abstract class IMessage implements Cloneable {
	protected Map msgBody = new HashMap();
    public int eventNo = 0;
	public abstract boolean encode();

	public byte[] getValue(int key){
		return (byte[])msgBody.get(key);
	}
	
	public byte[] getValue(String key){
		return (byte[])msgBody.get(key);
	}
	
	
	public abstract boolean decode(Vector<Byte> buf) ;

	public Object clone() {
		Object o = null;
		try {
			o = (IMessage) super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println(e.toString());
		}
		return o;
	}

	public int getMsgID() {
		return eventNo;
	}

	public void setMsgID(int msgID) {
		this.eventNo = msgID;
	}
}
