package com.example.monitorcar.bin;

import java.util.ArrayList;
import java.util.List;

import com.example.monitorcar.TCP.DataTransfer;
import com.example.monitorcar.utils.DateUtil;

public class GPSInfo {

	private double longtitude;
	private double latitude;
	private long time;

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public double getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public static GPSInfo splitGPSByteArray(byte[] gpsByte) {
		GPSInfo tmp = new GPSInfo();
		byte[] longtitudeByte = new byte[8];
		for (int i = 0; i < 8; i++) {
			longtitudeByte[i] = gpsByte[i];
		}
		long tmp_long = DataTransfer.bytesToLong(longtitudeByte);
		double final_long = Double.parseDouble(DateUtil
				.bigDecimalToString(tmp_long));
		tmp.setLongtitude(final_long);

		byte[] latitudeByte = new byte[8];
		for (int i = 0; i < 8; i++) {
			latitudeByte[i] = gpsByte[8 + i];
		}
		long tmp_lati = DataTransfer.bytesToLong(latitudeByte);
		double final_lati = Double.parseDouble(DateUtil
				.bigDecimalToString(tmp_lati));
		tmp.setLatitude(final_lati);

		byte[] timeByte = new byte[8];
		for (int i = 0; i < 8; i++) {
			timeByte[i] = gpsByte[16 + i];
		}
		long tmp_time = DataTransfer.bytesToLong(timeByte);
		tmp.setTime(tmp_time);
		return tmp;
	}
	
	public static List<GPSInfo> byteArray2GPSInfoList(byte[] op) {

		List<GPSInfo> tmpList = new ArrayList<GPSInfo>();
		if (op.length == 0) {
			return null;
		}
		int size = op.length / 24;
		for (int i = 0; i < size; i++) {
			GPSInfo tmpGpsInfo = new GPSInfo();

			byte[] tmpByte = new byte[24];
			tmpByte[0] = op[i * 24];
			tmpByte[1] = op[i * 24 + 1];
			tmpByte[2] = op[i * 24 + 2];
			tmpByte[3] = op[i * 24 + 3];
			tmpByte[4] = op[i * 24 + 4];
			tmpByte[5] = op[i * 24 + 5];
			tmpByte[6] = op[i * 24 + 6];
			tmpByte[7] = op[i * 24 + 7];
			tmpByte[8] = op[i * 24 + 8];
			tmpByte[9] = op[i * 24 + 9];
			tmpByte[10] = op[i * 24 + 10];
			tmpByte[11] = op[i * 24 + 11];
			tmpByte[12] = op[i * 24 + 12];
			tmpByte[13] = op[i * 24 + 13];
			tmpByte[14] = op[i * 24 + 14];
			tmpByte[15] = op[i * 24 + 15];
			tmpByte[16] = op[i * 24 + 16];
			tmpByte[17] = op[i * 24 + 17];
			tmpByte[18] = op[i * 24 + 18];
			tmpByte[19] = op[i * 24 + 19];
			tmpByte[20] = op[i * 24 + 20];
			tmpByte[21] = op[i * 24 + 21];
			tmpByte[22] = op[i * 24 + 22];
			tmpByte[23] = op[i * 24 + 23];

			tmpGpsInfo = splitGPSByteArray(tmpByte);
			tmpList.add(tmpGpsInfo);
		}

		return tmpList;
	}
}
