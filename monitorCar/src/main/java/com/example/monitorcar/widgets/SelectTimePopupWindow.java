package com.example.monitorcar.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.example.monitorcar.R;


public class SelectTimePopupWindow extends PopupWindow {
	private View mMenuView;
	public Button startDay, startHour, endDay, endHour, cancel;

	public SelectTimePopupWindow(Activity context,
			OnClickListener buttonListener) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.select_time_dialog, null);
		startDay = (Button) mMenuView.findViewById(R.id.edtStartDay);
		startHour = (Button) mMenuView.findViewById(R.id.edtStartHour);
		endDay = (Button) mMenuView.findViewById(R.id.edtEndDay);
		endHour = (Button) mMenuView.findViewById(R.id.edtEndHour);
		cancel = (Button) mMenuView.findViewById(R.id.button_pop_search);

		startDay.setOnClickListener(buttonListener);
		startHour.setOnClickListener(buttonListener);
		endDay.setOnClickListener(buttonListener);
		endHour.setOnClickListener(buttonListener);
		cancel.setOnClickListener(buttonListener);

		this.setContentView(mMenuView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottom);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.ll_root).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});
	}

}
