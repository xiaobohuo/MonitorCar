package com.example.monitorcar.ui;

import static android.view.Gravity.START;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.monitorcar.R;
import com.example.monitorcar.fragment.AlarmFragment;
import com.example.monitorcar.fragment.CarFragment;
import com.example.monitorcar.fragment.SettingFragment;
import com.example.monitorcar.fragment.StaffFragment;
import com.example.monitorcar.services.AlarmService;
import com.example.monitorcar.utils.DateUtil;
import com.example.monitorcar.utils.DrawableUtil;
import com.example.monitorcar.utils.DrawableUtil.Cusor;
import com.example.monitorcar.utils.SaveUtil;
import com.example.monitorcar.widgets.DrawerArrowDrawable;
import com.example.monitorcar.widgets.SelectTimePopupWindow;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

public class MainActivity extends BaseActivity implements OnDateSetListener,
		TimePickerDialog.OnTimeSetListener {

	public static final String INSIDEID = "INSIDEID";
	public static final String DATEPICKER_TAG = "datepicker";
	public static final String TIMEPICKER_TAG = "timepicker";

	public static final String FRAGMENT_TITLE = "Fragment_title";

	private long insideID = 0;

	private DrawerArrowDrawable drawerArrowDrawable;
	private DatePickerDialog datePickerDialog;
	private TimePickerDialog timePickerDialog;
	private SelectTimePopupWindow menuWindow;
	private TextView titleText;
	private ListView drawerList;
	private Button btn_search;
	private MyAdapter mAdapter;
	private Bundle savedInstanceState;

	private float offset;
	private boolean flipped;
	private int lastSelected = 0;
	private List<HashMap<String, Object>> listData;

	private Cusor cusor = DrawableUtil.Cusor.UNSET;

	private String[] arrayTextSource = { "车辆", "员工", "告警", "设置" };
	private int[] arrayImageSource = { R.drawable.mycar, R.drawable.mypeople,
			R.drawable.myalarm, R.drawable.mysetting };

	private int myYear_begin, myMonth_begin, myDay_begin, myHour_begin,
			myMinute_begin;
	private int myYear_end, myMonth_end, myDay_end, myHour_end, myMinute_end;

	private Fragment currentFragment, lastFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.savedInstanceState = savedInstanceState;
		setContentView(R.layout.mainactivity);

		startBackgroundService();

		init();
	}

	private void init() {
		final Calendar calendar = Calendar.getInstance();

		datePickerDialog = DatePickerDialog.newInstance(this,
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), false);
		timePickerDialog = TimePickerDialog.newInstance(this,
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), false, false);

		final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		final ImageView imageView = (ImageView) findViewById(R.id.drawer_indicator);
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
				titleText.setText(getResources().getString(R.string.app_name));
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

		drawerList = (ListView) findViewById(R.id.drawer_listView);
		listData = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 4; i++) {
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
					setSearchStatus(false);

					MoveToFragment(position);
					lastSelected = position;
				}

			}
		});

		titleText = (TextView) findViewById(R.id.indicator_style);
		titleText.setText(arrayTextSource[lastSelected]);

		btn_search = (Button) findViewById(R.id.button_to_popup);
		btn_search.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				menuWindow = new SelectTimePopupWindow(MainActivity.this,
						itemsOnClick);
				menuWindow.showAtLocation(
						MainActivity.this.findViewById(R.id.main_layout),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});

		MoveToFragment(0);

		if (savedInstanceState != null) {
			DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager()
					.findFragmentByTag(DATEPICKER_TAG);
			if (dpd != null) {
				dpd.setOnDateSetListener(this);
			}

			TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager()
					.findFragmentByTag(TIMEPICKER_TAG);
			if (tpd != null) {
				tpd.setOnTimeSetListener(this);
			}
		}
	}

	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			// menuWindow.dismiss();
			switch (v.getId()) {
			case R.id.edtStartDay:
				cusor = Cusor.STARTDAY;
				datePickerDialog.setVibrate(false);
				datePickerDialog.setYearRange(1985, 2028);
				datePickerDialog.show(getSupportFragmentManager(),
						DATEPICKER_TAG);
				break;
			case R.id.edtStartHour:
				cusor = Cusor.STARTHOUR;
				timePickerDialog.setVibrate(false);
				timePickerDialog.show(getSupportFragmentManager(),
						TIMEPICKER_TAG);
				break;
			case R.id.edtEndDay:
				cusor = cusor.ENDDAY;
				datePickerDialog.setVibrate(false);
				datePickerDialog.setYearRange(1985, 2028);
				datePickerDialog.show(getSupportFragmentManager(),
						DATEPICKER_TAG);
				break;
			case R.id.edtEndHour:
				cusor = cusor.ENDHOUR;
				timePickerDialog.setVibrate(false);
				timePickerDialog.show(getSupportFragmentManager(),
						TIMEPICKER_TAG);
				break;
			case R.id.button_pop_search:
				menuWindow.dismiss();
				long beginTime = DateUtil.DateStringToLong(myYear_begin,
						myMonth_begin, myDay_begin, myHour_begin,
						myMinute_begin);
				long endTime = DateUtil.DateStringToLong(myYear_end,
						myMonth_end, myDay_end, myHour_end, myMinute_end);
				int interval = (int) (endTime - beginTime);
				if (beginTime >= endTime) {
					showShortToast("开始时间应小于结束时间");
				} else {
					// MyTask task = new MyTask(beginTime, interval);
					// task.execute();
				}
				break;
			default:
				break;
			}

		}

	};

	private void startBackgroundService() {
		Intent intent = new Intent(MainActivity.this, AlarmService.class);
		intent.putExtra("INSIDEID", insideID);
		startService(intent);

	}

	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
		if (cusor == Cusor.STARTHOUR) {
			myHour_begin = hourOfDay;
			myMinute_begin = minute;
			menuWindow.startHour.setText(hourOfDay + "时" + minute + "分");
		} else if (cusor == Cusor.ENDHOUR) {
			myHour_end = hourOfDay;
			myMinute_end = minute;
			menuWindow.endHour.setText(hourOfDay + "时" + minute + "分");
		}
	}

	@Override
	public void onDateSet(DatePickerDialog datePickerDialog, int year,
			int month, int day) {
		if (cusor == Cusor.STARTDAY) {
			myYear_begin = year;
			myMonth_begin = month + 1;
			myDay_begin = day;
			menuWindow.startDay.setText(year + "年" + (month + 1) + "月" + day
					+ "日");
		} else if (cusor == Cusor.ENDDAY) {
			myYear_end = year;
			myMonth_end = month + 1;
			myDay_end = day;
			menuWindow.endDay.setText(year + "年" + (month + 1) + "月" + day
					+ "日");
		}
	}

	public void setSearchStatus(boolean status) {
		if (status == true) {
			btn_search.setVisibility(View.VISIBLE);
		} else {
			btn_search.setVisibility(View.GONE);
		}
	}

	private void MoveToFragment(int position) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		currentFragment = fm.findFragmentByTag(arrayTextSource[position]);
		if (currentFragment == null) {
			switch (position) {
			case 0:
				currentFragment = new CarFragment();
				break;
			case 1:
				currentFragment = new StaffFragment();
				break;
			case 2:
				currentFragment = new AlarmFragment();
				break;
			case 3:
				currentFragment = new SettingFragment();
				break;
			}
			ft.add(R.id.fragment_layout, currentFragment,
					arrayTextSource[position]); // add tag
		}
		if (lastFragment != null) {
			ft.hide(lastFragment);
		}
		if (currentFragment.isDetached()) {
			ft.attach(currentFragment);
		}
		ft.show(currentFragment);
		lastFragment = currentFragment;
		ft.commit();

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
			img.setImageResource((Integer) listData.get(position).get(
					"ItemImage"));
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

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setTitle("退出程序")
				.setMessage("确定退出程序？")
				.setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						SaveUtil.getInstance(MainActivity.this)
								.saveUserStatusAfterLogout();
						MainActivity.this.finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
	}

}
