package com.example.monitorcar.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.monitorcar.R;
import com.example.monitorcar.TCP.CommService;
import com.example.monitorcar.TCP.DataTransfer;
import com.example.monitorcar.TCP.Event;
import com.example.monitorcar.TCP.IMessage;
import com.example.monitorcar.TCP.Pdu;
import com.example.monitorcar.bin.Vehicle;
import com.example.monitorcar.ui.MapTraceActivity;
import com.example.monitorcar.utils.EnDeCodeUtil;
import com.example.monitorcar.utils.PreferenceUtil;
import com.example.monitorcar.utils.TCPUtil;
import com.example.monitorcar.utils.ThreadPoolManager;
import com.example.monitorcar.widgets.XListView;
import com.example.monitorcar.widgets.XListView.IXListViewListener;

public class CarFragment extends BaseFragment implements IXListViewListener {

	private View view;
	private XListView carListView;
	private Button togglebtn_nowtrace, togglebtn_historytrace;
	private List<Vehicle> carData = new ArrayList<Vehicle>();
	private CarListAdapter adapter = new CarListAdapter();

	private long insideId;
	private int companyId;
	private int offset = 0;
	private static int count = 15;
	private int traceKind = MapTraceActivity.NOWTRACE;

	private static final int FETCH_CAR_SUCCESS = 0x00;
	private static final int FETCH_CAR_FAIL = 0x01;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			closeLoadingDialog();
			switch (msg.what) {
			case FETCH_CAR_SUCCESS:
				List<Vehicle> tmp = (List<Vehicle>) msg.obj;
				carData.addAll(tmp);
				adapter.notifyDataSetChanged();
				offset++;
				break;

			case FETCH_CAR_FAIL:
				showShortToast("无数据");
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.funcfragmentcar, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		init();

		ThreadPoolManager.getInstance().addTask(fetchCarsRunnable);

		showLoadingDialog("正在加载...");

	}

	private void init() {
		insideId = PreferenceUtil.getInstance(getActivity()).getLong(
				PreferenceUtil.INID, -1);
		companyId = PreferenceUtil.getInstance(getActivity()).getInt(
				PreferenceUtil.COMPANYID, -1);

		carListView = (XListView) view.findViewById(R.id.carListView);
		carListView.setPullLoadEnable(true);
		carListView.setPullRefreshEnable(false);
		carListView.setAdapter(adapter);
		carListView.setXListViewListener(this);
		carListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), MapTraceActivity.class);
				long carInsideId = carData.get(position - 1).getInsideId();
				intent.putExtra(MapTraceActivity.CARPAINUM, carInsideId);
				intent.putExtra(MapTraceActivity.INSIDEID, insideId);
				intent.putExtra(MapTraceActivity.WHICHTRACE, traceKind);
				startActivity(intent);
			}
		});
		
		togglebtn_historytrace = (Button) view
				.findViewById(R.id.togglebtn_historytrace);
		togglebtn_nowtrace = (Button) view
				.findViewById(R.id.togglebtn_nowtrace);
		togglebtn_nowtrace.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (traceKind != MapTraceActivity.NOWTRACE) {
					traceKind = 0;
					togglebtn_nowtrace.setTextColor(getResources().getColor(
							R.color.qqblue));
					togglebtn_historytrace.setTextColor(getResources()
							.getColor(R.color.lightgrey));
				}

			}
		});
		togglebtn_historytrace.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (traceKind != MapTraceActivity.HISTORYTRACE) {
					traceKind = MapTraceActivity.HISTORYTRACE;
					togglebtn_nowtrace.setTextColor(getResources().getColor(
							R.color.lightgrey));
					togglebtn_historytrace.setTextColor(getResources()
							.getColor(R.color.qqblue));
				}
			}
		});
	}

	private Runnable fetchCarsRunnable = new Runnable() {

		@Override
		public void run() {
			List<Vehicle> tmp = getAllVehicle(companyId, offset, count);
			Message message = Message.obtain();
			if (tmp.size() == 0) {
				message.what = FETCH_CAR_FAIL;
			} else {
				message.what = FETCH_CAR_SUCCESS;
			}
			message.obj = tmp;
			handler.sendMessage(message);
		}
	};

	public List<Vehicle> getAllVehicle(int iCompanyID, int offset, int count) {
		Pdu pdu = setPduHead(Event.CAR_INFO_QUERY_REQ);
		pdu.addDataUnit(Event.KEY_COMPANY_ID, iCompanyID);
		pdu.addDataUnit(Event.KEY_CAR_QUERYTYPE, 2);
		pdu.addDataUnit(Event.SRC_INSIDE_USER_ID_KEY, insideId);
		pdu.addDataUnit(Event.KEY_QUERY_OFFSET, offset);
		pdu.addDataUnit(Event.KEY_QUERY_COUNT, count);
		List<Vehicle> list = new ArrayList<Vehicle>();
		try {
			IMessage msg = CommService.getInstance().sendMessage(pdu,
					Event.CAR_INFO_QUERY_RSP);
			int result = DataTransfer.ByteArrayToInt(msg
					.getValue(Event.RSP_KEY));
			if (result == Event.RSP_OK) {
				int iRecNumber = DataTransfer.ByteArrayToInt(msg
						.getValue(Event.KEY_NUMBER));
				for (int i = 0; i < iRecNumber; i++) {
					Vehicle tmp = new Vehicle();
					Map bodyVehicle = DataTransfer.bufToMap(msg
							.getValue(Event.KEY_CAR_INFO + i));
					tmp.setInsideId(DataTransfer
							.bytesToLong((byte[]) bodyVehicle
									.get(Event.KEY_CAR_ID)));
					tmp.setPlateNum(EnDeCodeUtil.decodeStr((byte[]) bodyVehicle
							.get(Event.KEY_CAR_LICENSENO)));
					tmp.setDeviceID(EnDeCodeUtil.decodeStr((byte[]) bodyVehicle
							.get(Event.KEY_CAR_SERIALNO)));
					tmp.setType(DataTransfer.toInt((byte[]) bodyVehicle
							.get(Event.KEY_CAR_TYPE)));
					tmp.setCompanyInsideId(DataTransfer
							.toInt((byte[]) bodyVehicle
									.get(Event.KEY_COMPANY_ID)));
					list.add(tmp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private Pdu setPduHead(int msgId) {
		Pdu pdu = new Pdu();
		pdu.msgId = msgId;
		pdu.sender = Event.CLIENT_SERVER;
		pdu.receiver = TCPUtil.getInstance(getActivity()).getVehicleMgnSvrId(
				insideId);
		pdu.sendProcess = Event.CLIENT_USER_PROCESS;
		pdu.recvRrocess = Event.USER_MAN_PROCESS;
		pdu.prototype = Event.PROTO_TCP;
		pdu.routerType = Event.ROUTE_APPOINT;
		pdu.sericeType = Event.COMMON_SERVICE;
		return pdu;
	}

	class CarListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return carData.size();
		}

		@Override
		public Vehicle getItem(int position) {
			return carData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
				convertView = inflater
						.inflate(R.layout.car_item, parent, false);
			}
			String itemData = getItem(position).getPlateNum();
			ImageView GroupImage = (ImageView) convertView
					.findViewById(R.id.GroupImg);
			TextView GroupNub = (TextView) convertView
					.findViewById(R.id.GroupNub);
			GroupImage.setBackground(getResources().getDrawable(
					R.drawable.car_img));
			GroupNub.setText(itemData);
			return convertView;
		}

	}

	@Override
	public void onLoadMore() {
		// 加载更多车辆信息
		ThreadPoolManager.getInstance().addTask(fetchCarsRunnable);
	}

	@Override
	public void onRefresh() {

	}
}
