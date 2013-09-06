package com.example.upcomingmovies;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class MoviesReceiver extends ResultReceiver {

	private Receiver mReceiver;
	
	public MoviesReceiver(Handler handler) {
		super(handler);
	}
	
	public interface Receiver {
		public void onReceiverResult(int resultCode, Bundle resultData);
	}
	
	public void setReceiver(Receiver receiver) {
		mReceiver = receiver;
	}
	
	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {
		if(mReceiver != null) {
			mReceiver.onReceiverResult(resultCode, resultData);
		}
	}

}
