package com.example.monitorcar.TCP;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataTransfer {
    public static final String coding = "GB2312"; // 全局定义，以适应系统其他部分@

    public static int toInt(byte[] bRefArr) {
        int iOutcome = 0;
        byte bLoop;

        for (int i = 0; i < bRefArr.length; i++) {
            bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }

    /**
     * 将int转为低字节在前，高字节在后的byte数组;
     */
    public static byte[] IntToByteArray(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    public static byte[] IntTo8ByteArray(long n) {
        byte[] b = new byte[8];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        b[4] = (byte) (n >> 32 & 0xff);
        b[5] = (byte) (n >> 40 & 0xff);
        b[6] = (byte) (n >> 48 & 0xff);
        b[7] = (byte) (n >> 56 & 0xff);

        return b;
    }

    /**
     * byte数组转化为int 将低字节在前转为int，高字节在后的byte数组?
     */
    public static int ByteArrayToInt(byte[] bArr) {
        if (bArr.length != 4) {
            return -1;
        }
        return (int) ((((bArr[3] & 0xff) << 24) | ((bArr[2] & 0xff) << 16)
                | ((bArr[1] & 0xff) << 8) | ((bArr[0] & 0xff) << 0)));
    }

    public static long bytesToLong(byte[] array) {
        return ((((long) array[7] & 0xff) << 56)
                | (((long) array[6] & 0xff) << 48)
                | (((long) array[5] & 0xff) << 40)
                | (((long) array[4] & 0xff) << 32)
                | (((long) array[3] & 0xff) << 24)
                | (((long) array[2] & 0xff) << 16)
                | (((long) array[1] & 0xff) << 8) | (((long) array[0] & 0xff) << 0));
    }

    /**
     * 将byte数组转化成String,为了支持中文，转化时用GBK编码方式]
     */
    public static String ByteArraytoString(byte[] valArr, int maxLen) {
        String result = null;
        int index = 0;
        while (index < valArr.length && index < maxLen) {
            if (valArr[index] == 0) {
                break;
            }
            index++;
        }
        byte[] temp = new byte[index];
        System.arraycopy(valArr, 0, temp, 0, index);
        try {
            result = new String(temp, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将String转化为byte,为了支持中文，转化时用GBK编码方式n
     */
    public static byte[] StringToByteArray(String str) {
        byte[] temp = null;
        try {
            temp = str.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public static byte[] shortToByte(short number) {
        int temp = number;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();//
            temp = temp >> 8; // 向右移8位)
        }
        return b;
    }

    public static byte[] charToByte(char ch) {
        byte[] targets = new byte[2];
        targets[0] = (byte) (ch & 0xff);// 低位)
        targets[1] = (byte) ((ch >> 8) & 0xff);// 高位
        return new byte[]{targets[0]};
    }

    public static byte[] join(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    public static byte[] charsToByte(char[] chars) {
        List<byte[]> parts = new ArrayList<byte[]>();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            parts.add(charToByte(c));
        }

        byte[] ret = join(parts);
        return ret;
    }

    public static byte[] long2Byte(long x) {
        byte[] bb = new byte[8];
        bb[7] = (byte) (x >> 56);
        bb[6] = (byte) (x >> 48);
        bb[5] = (byte) (x >> 40);
        bb[4] = (byte) (x >> 32);
        bb[3] = (byte) (x >> 24);
        bb[2] = (byte) (x >> 16);
        bb[1] = (byte) (x >> 8);
        bb[0] = (byte) (x >> 0);
        return bb;
    }

    public static short bytesToShort(byte[] b) {
        return (short) (b[0] & 0xff | (b[1] & 0xff) << 8);
    }

    public static Map bufToMap(byte[] body) {
        Map map = new HashMap();
        int pos = 0;
        int bodyLength = body.length;
        int KEY_SIZE = 4;
        int MSG_SIZE = 4;
        while (pos < bodyLength && (bodyLength - pos) >= (KEY_SIZE + MSG_SIZE)) {
            byte[] kByte = new byte[KEY_SIZE];
            System.arraycopy(body, pos, kByte, 0, KEY_SIZE);
            int key = toInt(kByte);
            pos += KEY_SIZE;

            byte[] sByte = new byte[MSG_SIZE];
            System.arraycopy(body, pos, sByte, 0, MSG_SIZE);
            int msgLen = toInt(sByte);
            pos += MSG_SIZE;

            byte[] msg = new byte[msgLen];
            System.arraycopy(body, pos, msg, 0, msgLen);
            pos += msgLen;
            map.put(key, msg);
        }

        return map;
    }

    public static void printByte(byte[] buffer) {
        for (int i = 0; i < buffer.length; i++) {
            System.out.print(buffer[i]);
            System.out.print(" ");
        }
        System.out.println();
    }

    public static void main(String[] args) {

        byte[] t = IntToByteArray(20150);
        byte[] t1 = new byte[]{3, 0, 0, 0, 0, 0, 0, 1};

        // System.out.println(bytesToLong(t1));
        // printByte(t);

    }

    // added

    public static int byte2int(byte[] res) {
        // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000

        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for (int i = 0; i < 4; i++) {
            n <<= 8;
            temp = res[i] & mask;
            n |= temp;
        }
        return n;
    }

    public static double convertDouble(double value) {
        long l1 = Math.round(value * 100);
        double ret = l1 / 100.0;
        return ret;
    }

}