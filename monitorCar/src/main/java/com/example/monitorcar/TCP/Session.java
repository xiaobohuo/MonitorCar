package com.example.monitorcar.TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public abstract class Session extends Thread implements ISession {
	private Socket sock = null;
	private DataInputStream rdr = null;
	private Vector<Byte> buffer = new Vector<Byte>();
	private Queue<IMessage> queue = new LinkedList<IMessage>();
	private IMessage msg = null;
	protected IResponseListener rspListener = null;
	private DataOutputStream wdr = null;

	public Session(Socket socket, IMessage message, IResponseListener rspLis) {
		this.msg = message;
		sock = socket;

		try {
			rdr = new DataInputStream(sock.getInputStream());
			wdr = new DataOutputStream(sock.getOutputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.rspListener = rspLis;
		new Thread() {
			@Override
			public void run() {

				while (true) {
					if (queue.size() <= 0) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					IMessage msg = (IMessage) queue.poll();
					if (msg != null) {
						handleMessage(msg);
					}
				}
			}
		}.start();
	}

	public Session(Socket socket, IMessage message) {
		this.msg = message;
		sock = socket;

		// try {
		// rdr = new DataInputStream(sock.getInputStream());
		// wdr = new DataOutputStream(sock.getOutputStream());
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		new Thread() {
			@Override
			public void run() {

				while (true) {
					if (queue.size() <= 0) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					IMessage msg = (IMessage) queue.poll();
					if (msg != null) {
						handleMessage(msg);
					}
				}
			}
		}.start();
	}

	public void sendMessage(Pdu pdu) {
		try {
			wdr.write(pdu.toByte());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				int length = rdr.available();
				byte[] msg = new byte[length];
				if (length == 0) {
					try {
						Thread.sleep(5);
						continue;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				rdr.read(msg);

				byte2Packet(msg);
				IMessage message = createMessage();
				while (message.decode(buffer)) {
					queue.add(message);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private IMessage createMessage() {
		return (IMessage) msg.clone();
	}

	private void byte2Packet(byte[] msg) {
		for (int i = 0; i < msg.length; i++) {
			buffer.add(msg[i]);
		}
	}

	@Override
	public abstract void handleMessage(IMessage msg);

	@Override
	public void closeSession() {

	}
}
