package com.zte.handring.bluetooth;

import java.util.List;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.monitorcar.TCP.DataTransfer;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */

public class BluetoothLeService extends Service {

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;
	private int mConnectionState = STATE_DISCONNECTED;

	public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
	public final static String STEP_DATA = "com.example.bluetooth.le.STEP_DATA";
	public final static String DIS_DATA = "com.example.bluetooth.le.DIS_DATA";
	public final static String CALORY_DATA = "com.example.bluetooth.le.CALORY_DATA";
	public final static String TM_DATA = "com.example.bluetooth.le.TM_DATA";

	private BluetoothGatt mBluetoothGatt;
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;

	public final static UUID UUID_MYMCU_WATCH = UUID
			.fromString(SampleGattAttribute.MCU_WATCH_UUID);

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		close();
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	private final BluetoothGattCallback mGattCallBack = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String intentAction;
			System.out.println("=======status:" + status);
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				broadcastUpdate(intentAction);
				Log.i("BlueTooth", "Connected to GATT server.");
				Log.i("BlueTooth", "Attempting to start service discovery:"
						+ mBluetoothGatt.discoverServices());
				// if (characteristic.getUuid().toString()
				// .equals("0000fff1-0000-1000-8000-00805f9b34fb")) {
				//
				// mBluetoothLeService.setCharacteristicNotification(
				// characteristic, true);
				// }

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				broadcastUpdate(intentAction);
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
			} else {
				Log.w("BlueTooth", "onServicesDiscovered received: " + status);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			System.out.println("onCharacteristicRead");
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			System.out.println("--------write success----- status:" + status);
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			System.out.print("==============" + characteristic.getValue());
			broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
			if (characteristic.getValue() != null) {

				System.out.println(characteristic.getStringValue(0));
			}
			System.out.println("--------onCharacteristicChanged-----");
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			System.out.println("onDescriptorWriteonDescriptorWrite = " + status
					+ ", descriptor =" + descriptor.getUuid().toString());
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			System.out.println("rssi = " + rssi);
		}

	};

	private void broadcastUpdate(String intentAction) {
		final Intent intent = new Intent(intentAction);
		sendBroadcast(intent);
	}

	protected void broadcastUpdate(String action,
			BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);

		if (UUID_MYMCU_WATCH.equals(characteristic.getUuid())) {
			int flag = characteristic.getProperties();
			int format = -1;
			if ((flag & 0x01) != 0) {
				format = BluetoothGattCharacteristic.FORMAT_UINT16;
			} else {
				format = BluetoothGattCharacteristic.FORMAT_UINT8;
			}
			final int heartRate = characteristic.getIntValue(format, 1);
			System.out.println("Received heart rate: %d" + heartRate);
			intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
		} else {
			// For all other profiles, writes the data formatted in HEX.
			final byte[] data = characteristic.getValue();
			if (data != null && data.length > 0) {

				boolean test = data[0] == 0xF7;

				final StringBuilder stringBuilder = new StringBuilder(
						data.length);
				for (byte byteChar : data)
					stringBuilder.append(String.format("%02X ", byteChar));

				System.out.println("ppp" + new String(data) + "\n"
						+ stringBuilder.toString());

				StringBuilder builder = new StringBuilder(data.length);
				builder.append(String.format("%02X", data[0]));
				String tmp = builder.toString();

				if (tmp.equals("F7")) { // 记步相关
					String steps = null, dis = null, calory = null, tm = null;

					byte[] tmpByte = new byte[4];
					tmpByte[0] = data[1];
					tmpByte[1] = data[2];
					tmpByte[2] = data[3];
					tmpByte[3] = data[4];
					steps = DataTransfer.byte2int(tmpByte) + "";

					tmpByte[0] = data[5];
					tmpByte[1] = data[6];
					tmpByte[2] = data[7];
					tmpByte[3] = data[8];
					dis = DataTransfer.byte2int(tmpByte) + "";

					tmpByte[0] = data[9];
					tmpByte[1] = data[10];
					tmpByte[2] = data[11];
					tmpByte[3] = data[12];
					calory = DataTransfer.byte2int(tmpByte) + "";

					tmpByte[0] = 0;
					tmpByte[1] = data[13];
					tmpByte[2] = data[14];
					tmpByte[3] = data[15];
					tm = DataTransfer.byte2int(tmpByte) + "";

					intent.putExtra(STEP_DATA, steps);
					intent.putExtra(DIS_DATA, dis);
					intent.putExtra(CALORY_DATA, calory);
					intent.putExtra(TM_DATA, tm);

					sendBroadcast(intent);
				}
				if (tmp.equals("FD")) {

				}
				if (tmp.equals("FE")) { // 历史数相关
					StringBuilder builder2 = new StringBuilder(data.length);
					builder.append(String.format("%02X", data[1]));
					String tmp2 = builder.toString();
					switch (tmp2) {
					case "01":

						break;
					case "02":

						break;
					case "03":

						break;
					case "04":

						break;
					case "05":

						break;
					case "06":

						break;

					default:
						break;
					}

				}

			}
		}

	}

	public class LocalBinder extends Binder {
		public BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	public boolean initialize() {
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				return false;
			}
		}
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			return false;
		}
		return true;
	}

	public boolean connect(final String address) {
		if (mBluetoothAdapter == null || address == null) {
			return false;
		}
		// connect to the last GATT
		if (address != null && address.equals(mBluetoothDeviceAddress)
				&& mBluetoothGatt != null) {
			if (mBluetoothGatt.connect()) {
				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				return false;
			}
		}
		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		if (device == null) {
			return false;
		}
		mBluetoothGatt = device.connectGatt(this, false, mGattCallBack);
		mBluetoothDeviceAddress = address;
		mConnectionState = STATE_CONNECTING;
		return true;
	}

	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.disconnect();
	}

	private void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothGatt == null || mBluetoothAdapter == null) {
			return;
		}
		mBluetoothGatt.writeCharacteristic(characteristic);
	}

	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothGatt == null || mBluetoothAdapter == null) {
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	public void setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enable) {
		if (mBluetoothGatt == null || mBluetoothAdapter == null) {
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enable);
		BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
				.fromString(SampleGattAttribute.CLIENT_CHARACTERISTIC_CONFIG));
		if (descriptor != null) {
			descriptor
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			mBluetoothGatt.writeDescriptor(descriptor);
		}
	}

	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null)
			return null;

		return mBluetoothGatt.getServices();
	}

	// public void write2Ring() {
	//
	// if (mGattCharacteristics != null) {
	// final BluetoothGattCharacteristic characteristic = mGattCharacteristics
	// .get(groupPosition).get(childPosition);
	// final int charaProp = characteristic.getProperties();
	// System.out.println("charaProp = " + charaProp + ",UUID = "
	// + characteristic.getUuid().toString());
	// Random r = new Random();
	//
	// if (characteristic.getUuid().toString()
	// .equals("0000fff2-0000-1000-8000-00805f9b34fb")) {
	// byte[] sended = new byte[8];
	// sended[0] = (byte) 0xf4;
	// for (int i = 1; i < sended.length; i++) {
	// sended[i] = 0x01;
	// }
	// characteristic.setValue(sended);
	// mBluetoothLeService.wirteCharacteristic(characteristic);
	// }
	// }
	// }
}
