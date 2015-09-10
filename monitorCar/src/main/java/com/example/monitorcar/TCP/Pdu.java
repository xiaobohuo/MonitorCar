package com.example.monitorcar.TCP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

public class Pdu {
	public int sender = 1;
	public byte[] receiver = null;
	public short sendProcess = 3;
	public short recvRrocess = 4;
	public char prototype = 'x';
	public char routerType = 'y';
	public short sericeType = 5;
	public int msgId = 0;

	private Map<Integer, Object> body = new HashMap<Integer, Object>();
	public boolean setHeader = true;

	public void addDataUnit(int key, String value) {
		body.put(key, value);
	}

	public void addDataUnit(int key, char[] chars) {
		body.put(key, chars);
	}

	public void addDataUnit(int key, byte[] value) {
		body.put(key, value);
	}

	public void addDataUnit(int key, int value) {
		body.put(key, value);
	}

	public void addDataUnit(int key, long value) {
		body.put(key, value);
	}

	public void addDataUnit(int key, Object value) {
		body.put(key, value);
	}

	public void addDataUnit(int key, Vector value) {
		body.put(key, value);
	}
	
//	public void addDataUnit(int key, List value){
//		body.put(key, value);
//	}

	/**
	 * ��ͷת��Ϊbyte����i
	 * 
	 * @return
	 */
	public byte[] head2Byte() {
		List<byte[]> parts = new ArrayList<byte[]>();
		// msgLen
		byte[] tmp = new byte[4];
		parts.add(tmp);

		// sender
		tmp = DataTransfer.IntToByteArray(sender);
		parts.add(tmp);

		// receiver

		parts.add(receiver);

		// sendproc
		tmp = DataTransfer.shortToByte(sendProcess);
		parts.add(tmp);

		// sendproc
		tmp = DataTransfer.shortToByte(recvRrocess);
		parts.add(tmp);

		// prototype
		tmp = DataTransfer.charToByte(prototype);
		parts.add(tmp);

		// routertype
		tmp = DataTransfer.charToByte(routerType);
		parts.add(tmp);

		// servicetype
		tmp = DataTransfer.shortToByte(sericeType);
		parts.add(tmp);

		// msgId
		tmp = DataTransfer.IntToByteArray(msgId);
		parts.add(tmp);
		byte[] data = DataTransfer.join(parts);
		return data;
	}

	public byte[] bodyToByte() {
		List<byte[]> parts = new ArrayList<byte[]>();
		Iterator it = body.entrySet().iterator();
		byte[] tmp = null;
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			int key = ((Integer) entry.getKey()).intValue();
			Object value = entry.getValue();

			// add key
			tmp = DataTransfer.IntToByteArray(key);
			parts.add(tmp);
			// value
			byte[] vbyte = null;
			if (value instanceof byte[]) {
				vbyte = (byte[]) value;
			} else {
				vbyte = valueToByteArray(value);
			}

			// add length
			tmp = DataTransfer.IntToByteArray(vbyte.length);
			parts.add(tmp);

			// add value
			parts.add(vbyte);
		}

		byte[] data = DataTransfer.join(parts);

		return data;
	}

	public byte[] toByte() {
		List<byte[]> parts = new ArrayList<byte[]>();
		if (setHeader) {
			parts.add(this.head2Byte());
		}
		parts.add(this.bodyToByte());

		byte[] data = DataTransfer.join(parts);
		if (setHeader) {
			byte[] byteLen = DataTransfer.IntToByteArray(data.length);
			System.arraycopy(byteLen, 0, data, 0, 4);// xie length
		}
		return data;
	}

	private byte[] valueToByteArray(Object value) {
		byte[] temp = null;
		if (value instanceof String) {
			String str = (String) value;
			temp = DataTransfer.StringToByteArray(str);
		} else if (value instanceof char[]) {
			char[] chs = (char[]) value;
			temp = DataTransfer.charsToByte(chs);
		} else if (value instanceof Integer) {
			int i = ((Integer) value).intValue();
			temp = DataTransfer.IntToByteArray(i);
		} else if (value instanceof byte[]) {
			temp = (byte[]) value;
		} else if (value instanceof Long) {
			long i = ((Long) value).longValue();
			temp = DataTransfer.long2Byte(i);
		} else if (value instanceof Pdu) {
			Pdu tmp = (Pdu) value;
			temp = tmp.toByte();
		} else if (value instanceof Vector) {
			Vector vec = (Vector) value;
			int size = vec.size();
			Vector vecByte = new Vector();
			for (int i = 0; i < size; i++) {
				Object obj = vec.get(i);
				byte[] data = valueToByteArray(obj);
				vecByte.add(data);
			}
			temp = DataTransfer.join(vecByte);
		}
		return temp;
	}

	public static void main(String[] args) {
		Vector vec = new Vector();
		vec.add(1000L);
		vec.add("abc");
		Pdu pdu = new Pdu();
		pdu.valueToByteArray(vec);
		byte[] data = pdu.toByte();
		for (byte b : data) {
			System.out.print(b);
			System.out.print(" ");
		}

	}

	private static Pdu user2Pdu() {
		Pdu pdu = new Pdu();
		pdu.setHeader = false;
		pdu.addDataUnit(Event.KEY_USERNAME, "tanhui");
		pdu.addDataUnit(Event.KEY_PASSWORD, "8888");
		return pdu;
	}

	public void addDataUnit(int key, Pdu pdu) {
		body.put(key, pdu);
	}

}
