package com.example.monitorcar.TCP;

public class InnerRspListener implements IResponseListener {
	protected boolean isRspRecv = false;
	private IMessage response = null;
	private int eventNo = 0;

	public void setEventNo(int eventNo) {
		this.eventNo = eventNo;
	}

	public InnerRspListener(int resEventNo) {
		this.eventNo = resEventNo;
	}

	@Override
	public void onResponseRecv(IMessage event) {
		if (event.eventNo == this.eventNo) {
			isRspRecv = true;
			this.response = event;
		}
	}

	public IMessage getResponse() {
		return response;
	}

	public boolean isRspRecv() {
		return isRspRecv;
	}

}
