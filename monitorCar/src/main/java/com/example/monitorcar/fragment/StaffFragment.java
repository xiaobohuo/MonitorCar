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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.monitorcar.R;
import com.example.monitorcar.TCP.CommService;
import com.example.monitorcar.TCP.DataTransfer;
import com.example.monitorcar.TCP.Event;
import com.example.monitorcar.TCP.IMessage;
import com.example.monitorcar.TCP.Pdu;
import com.example.monitorcar.bin.User;
import com.example.monitorcar.ui.StaffInfoActivity;
import com.example.monitorcar.utils.EnDeCodeUtil;
import com.example.monitorcar.utils.PreferenceUtil;
import com.example.monitorcar.utils.TCPUtil;
import com.example.monitorcar.utils.ThreadPoolManager;
import com.example.monitorcar.widgets.XListView;
import com.example.monitorcar.widgets.XListView.IXListViewListener;

public class StaffFragment extends BaseFragment implements IXListViewListener {

	private static final int FETCH_STAFF_SUCCESS = 0x00;
	private static final int FETCH_STAFF_FAIL = 0x01;

	private View view;
	private XListView listView;
	private MyAdapter adapter = new MyAdapter();
	private List<User> data = new ArrayList<User>();

	private long inId;
	private int companyId;
	private int offset = 0;
	private static final int count = 20;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.funcfragmentpeople, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		inId = PreferenceUtil.getInstance(getActivity()).getLong(
				PreferenceUtil.INID, -1);
		companyId = PreferenceUtil.getInstance(getActivity()).getInt(
				PreferenceUtil.COMPANYID, -1);

		listView = (XListView) view.findViewById(R.id.RefreshList_people);
		listView.setPullLoadEnable(true);
		listView.setPullRefreshEnable(false);
		listView.setAdapter(adapter);
		listView.setXListViewListener(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), StaffInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(StaffInfoActivity.USEBIN, data.get(position-1));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		ThreadPoolManager.getInstance().addTask(fetchAllUserRunnable);
		showLoadingDialog("正在加载...");
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			closeLoadingDialog();
			switch (msg.what) {
			case FETCH_STAFF_SUCCESS:
				List<User> tmp = (List<User>) msg.obj;
				data.addAll(tmp);
				adapter.notifyDataSetChanged();
				offset++;
				break;

			case FETCH_STAFF_FAIL:
				showShortToast("无数据");
				break;
			}
		}
	};

	private Runnable fetchAllUserRunnable = new Runnable() {

		@Override
		public void run() {
			List<User> tmp = queryAllUserInfo(Event.KEY_COMPANY_ID, companyId,
					offset, count);
			Message message = Message.obtain();
			if (tmp.size() > 0) {
				message.what = FETCH_STAFF_SUCCESS;
			} else {
				message.what = FETCH_STAFF_FAIL;
			}
			message.obj = tmp;
			handler.sendMessage(message);
		}
	};

	private List<User> queryAllUserInfo(int key, Object value, int offset,
			int count) {

		Pdu pdu = new Pdu();
		pdu.msgId = Event.USER_INFO_QUERY_REQ;
		pdu.sender = Event.CLIENT_SERVER;
		pdu.receiver = TCPUtil.getInstance(getActivity()).getUserMgnSvrId();
		pdu.sendProcess = Event.CLIENT_USER_PROCESS;
		pdu.recvRrocess = Event.USER_MAN_PROCESS;
		pdu.prototype = Event.PROTO_TCP;
		pdu.routerType = Event.ROUTE_APPOINT;
		pdu.sericeType = Event.COMMON_SERVICE;

		if (key != Event.KEY_COMPANY_ID) {
			pdu.addDataUnit(Event.KEY_COMPANY_ID, companyId);
		}
		if (value instanceof String) {
			String str = (String) value;
			pdu.addDataUnit(key, EnDeCodeUtil.encode(str));
		} else {
			pdu.addDataUnit(key, value);
		}

		pdu.addDataUnit(Event.SRC_INSIDE_USER_ID_KEY, inId);
		pdu.addDataUnit(Event.KEY_QUERY_OFFSET, offset);
		pdu.addDataUnit(Event.KEY_QUERY_COUNT, count);

		List<User> ret = new ArrayList<User>();
		try {

			IMessage msg = CommService.getInstance().sendMessage(pdu,
					Event.USER_INFO_QUERY_RSP);
			int result = DataTransfer.ByteArrayToInt(msg
					.getValue(Event.RSP_KEY));
			if (result == Event.RSP_OK) {
				int totalNum = DataTransfer.ByteArrayToInt(msg
						.getValue(Event.KEY_QUERY_COUNT));
				int usrNum = DataTransfer.ByteArrayToInt(msg
						.getValue(Event.KEY_NUMBER));

				for (int i = 0; i < usrNum; i++) {
					User tmp = new User();
					Map usrInfoMap = DataTransfer.bufToMap(msg
							.getValue(Event.KEY_USER_INFO + i));
					long userInsideId = DataTransfer
							.bytesToLong((byte[]) usrInfoMap
									.get(Event.KEY_USER_ID));
					if (userInsideId == inId) {
						continue;
					}
					String userName = EnDeCodeUtil
							.decodeStr((byte[]) usrInfoMap
									.get(Event.KEY_USERNAME));
					tmp.setUserName(userName);
					String email = new String(
							(byte[]) usrInfoMap.get(Event.KEY_EMAIL));
					tmp.setEmail(email);
					String alias = EnDeCodeUtil.decodeStr((byte[]) usrInfoMap
							.get(Event.KEY_USERALIAS));
					tmp.setUserAlias(alias);
					String phone1 = new String(
							(byte[]) usrInfoMap.get(Event.KEY_PHONE1));
					tmp.setPhone(phone1);
					String phone2 = new String(
							(byte[]) usrInfoMap.get(Event.KEY_PHONE2));
					tmp.setPhone1(phone2);
					String addrHome = EnDeCodeUtil
							.decodeStr((byte[]) usrInfoMap
									.get(Event.KEY_ADDRESS_HOME));
					tmp.setHomeAddr(addrHome);
					String addrOffice = EnDeCodeUtil
							.decodeStr((byte[]) usrInfoMap
									.get(Event.KEY_ADDRESS_OFFICE));
					tmp.setCompanyAddr(addrOffice);
					String identifyNum = new String(
							(byte[]) usrInfoMap.get(Event.KEY_IDENTIFY_NUM));
					tmp.setIdentifyNum(identifyNum);
					String realName = EnDeCodeUtil
							.decodeStr((byte[]) usrInfoMap
									.get(Event.KEY_REALNAME));
					tmp.setRealName(realName);
					ret.add(tmp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public User getItem(int position) {
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
				convertView = inflater
						.inflate(R.layout.car_item, parent, false);
			}
			String itemData = getItem(position).getUserName();
			ImageView GroupImage = (ImageView) convertView
					.findViewById(R.id.GroupImg);
			TextView GroupNub = (TextView) convertView
					.findViewById(R.id.GroupNub);
			GroupNub.setText(itemData);
			GroupImage.setBackground(getResources().getDrawable(
					R.drawable.people_img));
			return convertView;
		}

	}

	@Override
	public void onLoadMore() {

	}

	@Override
	public void onRefresh() {

	}

}
