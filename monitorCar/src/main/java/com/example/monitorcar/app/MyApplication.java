package com.example.monitorcar.app;

import java.util.LinkedList;

import com.baidu.mapapi.SDKInitializer;

import android.app.Activity;
import android.app.Application;


public class MyApplication extends Application {

	private LinkedList<Activity> activities = new LinkedList<Activity>();

	private static MyApplication instance = null;

	public static MyApplication getInstance() {
		if (instance == null) {
			instance = new MyApplication();
		}
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SDKInitializer.initialize(this);
	}

	@Override
	public void onTerminate() {

		super.onTerminate();
	}

	public void addActivity(Activity act) {
		activities.add(act);
	}

	public void removeActivity(Activity act) {
		activities.remove(act);
	}

	public void exit() {
		for (Activity act : activities) {
			act.finish();
		}
		System.exit(0);
	}
}
