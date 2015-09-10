package com.example.monitorcar.listeners;

import android.view.MotionEvent;
import android.view.View;

public class MyOnTouchListener implements View.OnTouchListener{

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			v.getBackground().setAlpha(80);
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			v.getBackground().setAlpha(255);
		}
		return false;
	}

}
