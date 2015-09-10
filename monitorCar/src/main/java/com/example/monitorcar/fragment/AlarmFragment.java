package com.example.monitorcar.fragment;

import java.util.ArrayList;
import java.util.List;

import com.example.monitorcar.R;
import com.example.monitorcar.ui.MainActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class AlarmFragment extends BaseFragment implements OnClickListener {

	private View view;
	private ViewPager pager;
	private Button togglebtn_now, togglebtn_history;
	private List<Fragment> fragments;
	private Fragment fragmentCurrent, fragmentHistory;
	private int currenIndex = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.alarmfragment, null);

		init();

		return view;
	}

	private void init() {
		togglebtn_history = (Button) view.findViewById(R.id.togglebtn_history);
		togglebtn_now = (Button) view.findViewById(R.id.togglebtn_now);
		togglebtn_now.setOnClickListener(this);
		togglebtn_history.setOnClickListener(this);

		pager = (ViewPager) view.findViewById(R.id.vPager);
		fragments = new ArrayList<Fragment>();
		fragmentCurrent = new AlarmCurrentFragment();
		fragmentHistory = new AlarmHistoryFragment();
		fragments.add(fragmentCurrent);
		fragments.add(fragmentHistory);
		pager.setAdapter(new MyViewPagerAdapter(getChildFragmentManager(),
				fragments));
		pager.setOnPageChangeListener(new MyPagerChangeListener());
	}

	private class MyViewPagerAdapter extends FragmentPagerAdapter {

		private List<Fragment> list;

		public MyViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public MyViewPagerAdapter(FragmentManager fm, List<Fragment> list) {
			super(fm);
			this.list = list;
		}

		@Override
		public Fragment getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public int getCount() {
			return list.size();
		}

	}

	private class MyPagerChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				if (currenIndex == 1) {
					togglebtn_now.setTextColor(getResources().getColor(
							R.color.qqblue));
					togglebtn_history.setTextColor(getResources().getColor(
							R.color.lightgrey));
					MainActivity atyMain = (MainActivity) getActivity();
					atyMain.setSearchStatus(false);
				}
				break;
			case 1:
				if (currenIndex == 0) {
					togglebtn_now.setTextColor(getResources().getColor(
							R.color.lightgrey));
					togglebtn_history.setTextColor(getResources().getColor(
							R.color.qqblue));
					MainActivity atyMain = (MainActivity) getActivity();
					atyMain.setSearchStatus(true);
				}
				break;
			default:
				break;
			}
			currenIndex = arg0;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.togglebtn_now:
			pager.setCurrentItem(0);
			break;

		case R.id.togglebtn_history:
			pager.setCurrentItem(1);
			break;
		}
	}

}
