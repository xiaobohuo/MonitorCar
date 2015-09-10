package com.example.monitorcar.fragment;

import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.monitorcar.R;
import com.example.monitorcar.ui.UserDetailActivity;
import com.example.monitorcar.utils.PreferenceUtil;

public class SettingFragment extends BaseFragment {

	private View view;
	private LinearLayout ll_settings, ll_fankui, ll_guanyu, ll_qingchuhuancun,
			ll_tuichu;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.settingfragment, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ll_settings = (LinearLayout) view.findViewById(R.id.ll_shezhi);
		ll_fankui = (LinearLayout) view.findViewById(R.id.ll_fankui);
		ll_guanyu = (LinearLayout) view.findViewById(R.id.ll_guanyu);
		ll_qingchuhuancun = (LinearLayout) view
				.findViewById(R.id.ll_qingchuhuancun);
		ll_tuichu = (LinearLayout) view.findViewById(R.id.ll_logout);

		MyOnClickListener myOnclickListener = new MyOnClickListener();
		ll_settings.setOnClickListener(myOnclickListener);
		ll_fankui.setOnClickListener(myOnclickListener);
		ll_guanyu.setOnClickListener(myOnclickListener);
		ll_qingchuhuancun.setOnClickListener(myOnclickListener);
		ll_tuichu.setOnClickListener(myOnclickListener);
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ll_shezhi:
				 Intent intent = new Intent();
				 intent.setClass(getActivity(), UserDetailActivity.class);
				 startActivity(intent);
				break;

			case R.id.ll_fankui:
				showShortToast("待添加");
				break;
			case R.id.ll_guanyu:
				showShortToast("待添加");
				break;
			case R.id.ll_qingchuhuancun:
				new AlertDialog.Builder(getActivity())
						.setTitle("清除应用缓存")
						.setMessage("确定清除应用缓存？")
						.setCancelable(false)
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								})
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										new DataCleanManager()
												.cleanApplicationData(getActivity());
										showShortToast("清理成功");
									}
								}).show();
				break;
			case R.id.ll_logout:
				new AlertDialog.Builder(getActivity())
						.setTitle("退出当前账号")
						.setMessage("确定要退出当前账号？")
						.setCancelable(false)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										PreferenceUtil.getInstance(
												getActivity()).setBoolean(
												PreferenceUtil.HASLOGIN, false);
										// Intent intent = new Intent(
										// getActivity(), AtyLogin.class);
										// startActivity(intent);
										//
										// Intent intent2 = new Intent(
										// getActivity(),
										// AlarmService.class);
										// getActivity().stopService(intent2);
										getActivity().finish();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
				break;
			}
		}

	}

	public class DataCleanManager {
		public void cleanInternalCache(Context context) {
			deleteFilesByDirectory(context.getCacheDir());
		}

		public void cleanDatabases(Context context) {
			deleteFilesByDirectory(new File("/data/data/"
					+ context.getPackageName() + "/databases"));
		}

		public void cleanSharedPreference(Context context) {
			deleteFilesByDirectory(new File("/data/data/"
					+ context.getPackageName() + "/shared_prefs"));
		}

		public void cleanDatabaseByName(Context context, String dbName) {
			context.deleteDatabase(dbName);
		}

		public void cleanFiles(Context context) {
			deleteFilesByDirectory(context.getFilesDir());
		}

		public void cleanExternalCache(Context context) {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				deleteFilesByDirectory(context.getExternalCacheDir());
			}
		}

		public void cleanApplicationData(Context context) {
			cleanInternalCache(context);
			cleanExternalCache(context);
			cleanDatabases(context);
			cleanSharedPreference(context);
			cleanFiles(context);
		}

		private void deleteFilesByDirectory(File directory) {
			if (directory != null && directory.exists()
					&& directory.isDirectory()) {
				for (File item : directory.listFiles()) {
					item.delete();
				}
			}
		}
	}

}
