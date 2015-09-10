package com.example.monitorcar.ui;

import android.annotation.SuppressLint;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.monitorcar.app.MyApplication;
import com.example.monitorcar.widgets.LoadingDialog;

public class BaseActivity extends FragmentActivity implements OnClickListener {

	private LoadingDialog dialog;

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyApplication.getInstance().removeActivity(this);
	}

	public void showShortToast(String toast) {
		Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
	}

	public void showLongToast(String toast) {
		Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
	}

	@SuppressLint("NewApi")
	public void showLoadingDialog() {
		if (dialog == null) {
			dialog = new LoadingDialog();
		}
		dialog.setCancelable(true);
		dialog.setMsg("");
		dialog.show(getFragmentManager(), "Loading");
	}

	@SuppressLint("NewApi")
	public void showLoadingDialog(String msg) {
		if (dialog == null) {
			dialog = new LoadingDialog();
		}
		dialog.setCancelable(true);
		dialog.setMsg(msg);
		dialog.show(getFragmentManager(), "Loading");
	}

	@SuppressLint("NewApi")
	public void closeLoadingDialog() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	@Override
	public void onClick(View v) {

	}
	
	public void sleepSomeTime(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
