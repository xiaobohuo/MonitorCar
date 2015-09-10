package com.example.monitorcar.widgets;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.platform.comjni.tools.JNITools;
import com.example.monitorcar.R;

@SuppressLint("NewApi")
public class LoadingDialog extends DialogFragment {

	String msg = "Loading";

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_loading, null);
		TextView title = (TextView) view
				.findViewById(R.id.id_dialog_loading_msg);
		title.setText(msg);
		Dialog dialog = new Dialog(getActivity(), R.style.Theme_Dialog);
		dialog.setContentView(view);
		return dialog;
	}
//	JNITools
}
