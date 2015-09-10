package com.example.monitorcar.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.monitorcar.R;
import com.example.monitorcar.TCP.CommService;
import com.example.monitorcar.TCP.DataTransfer;
import com.example.monitorcar.TCP.Event;
import com.example.monitorcar.TCP.IMessage;
import com.example.monitorcar.TCP.Pdu;
import com.example.monitorcar.bin.Alarm;
import com.example.monitorcar.utils.DateUtil;
import com.example.monitorcar.utils.EnDeCodeUtil;
import com.example.monitorcar.utils.PreferenceUtil;
import com.example.monitorcar.utils.TCPUtil;
import com.example.monitorcar.utils.ThreadPoolManager;
import com.example.monitorcar.widgets.XListView;
import com.example.monitorcar.widgets.XListView.IXListViewListener;

public class AlarmCurrentFragment extends BaseFragment implements
		IXListViewListener {

	private static final int FETCH_CURRENT_ALARM_SUCCESS = 0x00;
	private static final int FETCH_CURRENT_ALARM_FAIL = 0x01;

	private View view;
	private XListView xListView;
	private List<Alarm> data = new ArrayList<Alarm>();
	private MyAdapter adapter = new MyAdapter();

	private int offset = 0;
	private static final int count = 20;
	private long inId = -1;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			closeLoadingDialog();
			switch (msg.what) {
			case FETCH_CURRENT_ALARM_SUCCESS:
				List<Alarm> tmp = (List<Alarm>) msg.obj;
				data.addAll(tmp);
				adapter.notifyDataSetChanged();
				offset++;
				break;

			case FETCH_CURRENT_ALARM_FAIL:
				showShortToast("无数据");
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.alarmnowfragment, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		inId = PreferenceUtil.getInstance(getActivity()).getLong(
				PreferenceUtil.INID, -1);
		xListView = (XListView) view.findViewById(R.id.RefreshList_now);
		xListView.setAdapter(adapter);
		xListView.setPullLoadEnable(true);
		xListView.setPullRefreshEnable(false);
		xListView.setXListViewListener(this);

		ThreadPoolManager.getInstance().addTask(currentAlarmRunnable);
		showLoadingDialog("正在加载...");

	}

	private Runnable currentAlarmRunnable = new Runnable() {

		@Override
		public void run() {
			List<Alarm> tmp = queryCurrentAlarm(offset, count);
			Message message = Message.obtain();
			if (tmp.size() == 0) {
				message.what = FETCH_CURRENT_ALARM_FAIL;
			} else {
				message.what = FETCH_CURRENT_ALARM_SUCCESS;
			}
			message.obj = tmp;
			handler.sendMessage(message);
		}
	};

	protected List<Alarm> queryCurrentAlarm(int offset, int count) {
		Pdu pdu = new Pdu();
		pdu.msgId = Event.MONITOR_QUERY_CURRENT_ALARM_REQ;
		pdu.sender = Event.CLIENT_SERVER;
		pdu.receiver = TCPUtil.getInstance(getActivity()).getUserMgnSvrId();
		pdu.sendProcess = Event.CLIENT_USER_PROCESS;
		pdu.recvRrocess = Event.MONITOR_AGENT_PROCESS;

		pdu.prototype = Event.PROTO_TCP;
		pdu.routerType = Event.ROUTE_APPOINT;
		pdu.sericeType = Event.COMMON_SERVICE;

		pdu.addDataUnit(Event.SRC_INSIDE_USER_ID_KEY, inId);
		pdu.addDataUnit(Event.MONITOR_OPERATOR_INSIDEID_KEY, inId);
		pdu.addDataUnit(Event.MONITOR_USER_INSIDEID_KEY, inId);

		pdu.addDataUnit(Event.MONTIOR_QUERY_ROW_OFFSET_KEY, offset);
		pdu.addDataUnit(Event.MONITOR_QUERY_ROW_COUNT_KEY, count);

		List<Alarm> tmp = new ArrayList<Alarm>();

		try {

			IMessage msg = CommService.getInstance().sendMessage(pdu,
					Event.MONITOR_QUERY_CURRENT_ALARM_RSP);
			int result = DataTransfer.ByteArrayToInt(msg
					.getValue(Event.RSP_KEY));
			if (result == Event.RSP_OK) {
				int totalNum = DataTransfer.toInt(msg
						.getValue(Event.MONITOR_QUERY_RESULT_TOTAL_NUMBER_KEY));
				int retNum = DataTransfer.toInt(msg
						.getValue(Event.MONITOR_QUERY_ROW_COUNT_KEY));
				for (int i = 0; i < retNum; i++) {
					Map alarmInfoMap = DataTransfer.bufToMap(msg
							.getValue(Event.MONITOR_QUERY_RESULT_SET_KEY + i));

					String alarmDesc = EnDeCodeUtil
							.decodeStr((byte[]) alarmInfoMap
									.get(Event.MONITOR_ALARM_TITLE_KEY));
					String addTxt = EnDeCodeUtil
							.decodeStr((byte[]) alarmInfoMap
									.get(Event.MONITOR_ALARM_DETAILS_KEY));
					long time = DataTransfer.bytesToLong((byte[]) alarmInfoMap
							.get(Event.MONITOR_ALARM_TIME_KEY));
					String targetName = EnDeCodeUtil
							.decodeStr((byte[]) alarmInfoMap
									.get(Event.MONITOR_TARGET_NAME_KEY));
					Alarm alarm = new Alarm();
					alarm.setAlarmDesc(alarmDesc);
					alarm.setHappenTime(DateUtil.formatTime(time));
					alarm.setAddTxt(addTxt);
					alarm.setTarget(targetName);
					tmp.add(alarm);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tmp;
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Alarm getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
				convertView = inflater.inflate(R.layout.alarm_item, parent,
						false);
			}
			String alarm_desc = getItem(position).getAlarmDesc();
			TextView text_alarm_desc = (TextView) convertView
					.findViewById(R.id.alram_descrip);
			text_alarm_desc.setText(alarm_desc);

			String alarm_for = getItem(position).getTarget();
			TextView text_alarm_for = (TextView) convertView
					.findViewById(R.id.alram_for_who);
			text_alarm_for.setText(alarm_for);

			String alarm_time = getItem(position).getHappenTime();
			TextView text_alarm_time = (TextView) convertView
					.findViewById(R.id.alram_time);
			text_alarm_time.setText(alarm_time);

			String alarm_more = getItem(position).getAddTxt();
			TextView text_alarm_more = (TextView) convertView
					.findViewById(R.id.alram_more_message);
			text_alarm_more.setText(alarm_more);
			return convertView;
		}
	}

	@Override
	public void onLoadMore() {
		ThreadPoolManager.getInstance().addTask(currentAlarmRunnable);
	}

	@Override
	public void onRefresh() {

	}

}
