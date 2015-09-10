package com.zte.handring.ui;

import static android.view.Gravity.START;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.monitorcar.R;
import com.example.monitorcar.ui.BaseActivity;
import com.example.monitorcar.widgets.DrawerArrowDrawable;
import com.zte.handring.bluetooth.BluetoothLeService;
import com.zte.handring.bluetooth.SendCommandManager;

public class HRMainActivity extends BaseActivity {
	
	private DrawerArrowDrawable drawerArrowDrawable;
	private TextView titleText;
	private ListView drawerList;
	private Button test_btn;
	private MyAdapter mAdapter;
	private Fragment fragment = null;
	
	private float offset;
	private boolean flipped;
	private int lastSelected = 0;
	private String userName;
	
	private String[] arrayTextSource = { "运动睡眠", "活动记录", "设置" };
	private int[] arrayImageSource = { R.drawable.mycar, R.drawable.mypeople,
			R.drawable.mysetting };
	private List<HashMap<String, Object>> listData;
	private BluetoothGattCharacteristic characteristic_of_send;
	private BluetoothLeService mBluetoothLeService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hr_aty_mainview);
		
		Intent intent = getIntent();
		userName = intent.getStringExtra("UserName");
		
		final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.hr_drawer_layout);
		final ImageView imageView = (ImageView) findViewById(R.id.hr_drawer_indicator);
		final Resources resources = getResources();

		drawerArrowDrawable = new DrawerArrowDrawable(resources);
		drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.white));
		imageView.setImageDrawable(drawerArrowDrawable);

		drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				offset = slideOffset;

				if (slideOffset >= .995) {
					flipped = true;
					drawerArrowDrawable.setFlip(flipped);
				} else if (slideOffset <= .005) {
					flipped = false;
					drawerArrowDrawable.setFlip(flipped);
				}

				drawerArrowDrawable.setParameter(offset);
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				titleText.setText(arrayTextSource[lastSelected]);
				super.onDrawerClosed(drawerView);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				titleText.setText(getResources()
						.getString(R.string.hr_app_name));
				super.onDrawerOpened(drawerView);
			}

		});

		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (drawer.isDrawerVisible(START)) {
					drawer.closeDrawer(START);
				} else {
					drawer.openDrawer(START);
				}
			}
		});

		drawerList = (ListView) findViewById(R.id.hr_drawer_listView);
		listData = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 3; i++) {
			HashMap<String, Object> tmp = new HashMap<String, Object>();
			tmp.put("ItemImage", arrayImageSource[i]);
			tmp.put("ItemText", arrayTextSource[i]);
			listData.add(tmp);
		}

		mAdapter = new MyAdapter();
		drawerList.setAdapter(mAdapter);
		drawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == lastSelected) {
					return;
				} else {
					view.setBackgroundColor(getResources().getColor(
							R.color.Packetblue));
					drawerList.getChildAt(lastSelected).setBackgroundColor(
							getResources().getColor(R.color.white));

					titleText.setText(arrayTextSource[position]);
					drawer.closeDrawer(START);

					MoveToFragment(position);
					lastSelected = position;
				}

			}
		});

		titleText = (TextView) findViewById(R.id.hr_indicator_style);
		titleText.setText(arrayTextSource[lastSelected]);

		// test zhi ling
		test_btn = (Button) findViewById(R.id.hr_button_test);
		test_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (characteristic_of_send != null
						&& mBluetoothLeService != null) {

					byte[] sended = new byte[8];

					// sended = SendCommandManager.syncNowTime(); // 同步时间

					sended = SendCommandManager.fetchTodaySleepQuality();
					// //请求当天睡眠质量

					characteristic_of_send.setValue(sended);
					mBluetoothLeService
							.writeCharacteristic(characteristic_of_send);
				}
			}
		});

		initFirstFragment();

//		Intent intentService = new Intent(HRMainActivity.this,
//				LocationReportService.class);
//		startService(intentService);
	}
	
	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listData.size();
		}

		@Override
		public Object getItem(int position) {
			return listData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater layoutInflater = getLayoutInflater();
				convertView = layoutInflater.inflate(
						R.layout.drawer_list_item_view, parent, false);
			}
			ImageView img = (ImageView) convertView
					.findViewById(R.id.drawer_list_img_item);
			img.setImageResource((int) listData.get(position).get("ItemImage"));
			TextView txt = (TextView) convertView
					.findViewById(R.id.drawer_list_txt_item);
			txt.setText((String) listData.get(position).get("ItemText"));
			if (position == 0) {
				convertView.setBackgroundColor(getResources().getColor(
						R.color.Packetblue));
			}

			return convertView;
		}

	}
	
	private void initFirstFragment() {
//		Fragment fragment = new SportSleepFragment();
//		FragmentManager fragmentManager = getSupportFragmentManager();
//		fragmentManager.beginTransaction()
//				.replace(R.id.hr_fragment_layout, fragment).commit();
		
	}
	
	private void MoveToFragment(int position) {

//		switch (position) {
//		case 0:
//			fragment = new SportSleepFragment();
//			break;
//
//		case 1:
//			fragment = new ActionDetailFragment();
//			break;
//
//		case 2:
//			fragment = new HRSettingFragment();
//			Bundle bundle = new Bundle();
//			bundle.putString("UserName", userName);
//			fragment.setArguments(bundle);
//			break;
//
//		default:
//			break;
//		}
//		FragmentManager fragmentManager = getSupportFragmentManager();
//		fragmentManager.beginTransaction()
//				.replace(R.id.hr_fragment_layout, fragment).commit();

	}
}
