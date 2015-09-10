package com.example.monitorcar.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.example.monitorcar.widgets.LoadingDialog;

public class BaseFragment extends Fragment {

	private LoadingDialog dialog;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	public void showShortToast(String toast) {
//		Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
	}

	public void showLongToast(String toast) {
//		Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
	}

	@SuppressLint("NewApi")
	public void showLoadingDialog() {
		if (dialog == null) {
			dialog = new LoadingDialog();
		}
		dialog.setCancelable(true);
		dialog.setMsg("");
		dialog.show(getActivity().getFragmentManager(), "Loading");
	}

	@SuppressLint("NewApi")
	public void showLoadingDialog(String msg) {
		if (dialog == null) {
			dialog = new LoadingDialog();
		}
		dialog.setCancelable(true);
		dialog.setMsg(msg);
		dialog.show(getActivity().getFragmentManager(), "Loading");
	}

	@SuppressLint("NewApi")
	public void closeLoadingDialog() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}
}
