package com.example.monitorcar.TCP;

import java.util.Iterator;
import java.util.Vector;

public class MyMessage extends IMessage {
    private int KEY_SIZE = 4;
    private int MSG_SIZE = 4;
    private int HEAD_SIZE = 24;
    private int MSGID_SIZE = 4;

    @Override
    public boolean encode() {
        return false;
    }

    @Override
    public boolean decode(Vector<Byte> buffer) {
        if (buffer.size() < HEAD_SIZE) { // 小于頭部返回
            return false;
        }

        byte[] data = new byte[buffer.size()];
        for (int i = 0; i < buffer.size(); i++) {
            data[i] = ((Byte) buffer.get(i)).byteValue();
        }

        byte[] sizeByte = new byte[MSG_SIZE];
        System.arraycopy(data, 0, sizeByte, 0, MSG_SIZE);
        int totalSize = DataTransfer.toInt(sizeByte);
        if (buffer.size() < totalSize) {
            return false;// 不是一個完整的消息也返回繼續接收
        }

        byte[] fMsg = getFullMsg(totalSize, buffer);
        // 消息頭部
        byte[] header = new byte[HEAD_SIZE];
        System.arraycopy(fMsg, 0, header, 0, HEAD_SIZE);
        byte[] msgIDBytes = new byte[4];
        System.arraycopy(header, 20, msgIDBytes, 0, MSGID_SIZE);
        setMsgID(DataTransfer.ByteArrayToInt(msgIDBytes));

        // 解析消息body
        byte[] body = new byte[totalSize - HEAD_SIZE];
        System.arraycopy(fMsg, HEAD_SIZE, body, 0, body.length);
        int bodyLength = body.length;
        int pos = 0;
        while (pos < bodyLength && (bodyLength - pos) >= (KEY_SIZE + MSG_SIZE)) {
            byte[] kByte = new byte[KEY_SIZE];
            System.arraycopy(body, pos, kByte, 0, KEY_SIZE);
            int key = DataTransfer.toInt(kByte);
            pos += KEY_SIZE;

            byte[] sByte = new byte[MSG_SIZE];
            System.arraycopy(body, pos, sByte, 0, MSG_SIZE);
            int msgLen = DataTransfer.toInt(sByte);
            pos += MSG_SIZE;

            byte[] msg = new byte[msgLen];
            System.arraycopy(body, pos, msg, 0, msgLen);
            // printByte(msg);
            pos += msgLen;
            msgBody.put(key, msg);
        }
        return true;
    }

    private void parseHeader(byte[] header) {
        int point = 0;
        byte[] sizeByte = new byte[MSG_SIZE];
        System.arraycopy(header, point, sizeByte, 0, MSG_SIZE);
        int nMsgLen = DataTransfer.toInt(sizeByte);
        point += 4;

        byte[] senderByte = new byte[4];
        System.arraycopy(header, point, senderByte, 0, 4);
        int nSender = DataTransfer.toInt(senderByte);
        point += 4;

        byte[] recvByte = new byte[4];
        System.arraycopy(header, point, recvByte, 0, 4);
        int nRecver = DataTransfer.toInt(recvByte);
        point += 4;

        byte[] sendProcByte = new byte[2];
        System.arraycopy(header, point, sendProcByte, 0, 2);
        short nSendProcess = DataTransfer.bytesToShort(sendProcByte);
        point += 2;

        byte[] recvProcByte = new byte[2];
        System.arraycopy(header, point, recvProcByte, 0, 2);
        short nRecvProcess = DataTransfer.bytesToShort(recvProcByte);
        point += 2;

    }

    private byte[] getFullMsg(int totalSize, Vector<Byte> packet) {
        byte[] data = new byte[totalSize];

        Iterator<Byte> it = packet.iterator();
        int i = 0;
        while (it.hasNext()) {
            Byte elements = (Byte) it.next();
            data[i] = ((Byte) elements).byteValue();
            it.remove();
            i++;
            if (i == totalSize) {
                break;
            }
        }
        return data;
    }

    public void printByte(byte[] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            System.out.print(buffer[i]);
            System.out.print(" ");
        }
        System.out.println();
    }
}
