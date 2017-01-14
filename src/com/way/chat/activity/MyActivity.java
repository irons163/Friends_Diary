package com.way.chat.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.util.Constants;

/**
 * 自訂一個抽象的MyActivity類，每個Activity都繼承他，實現消息的接收（優化性能，減少代碼重複）
 * 
 * @author way
 * 
 */
public abstract class MyActivity extends Activity {
	/**
	 * 廣播接收者，接收GetMsgService發送過來的消息
	 */
	private BroadcastReceiver MsgReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			TranObject msg = (TranObject) intent
					.getSerializableExtra(Constants.MSGKEY);
			
			Log.e("a", "null11");
			
			if (msg != null) {//如果不是空，說明是消息廣播
				// System.out.println("MyActivity:" + msg);
				Log.e("myactivity", "true");
				getMessage(msg);// 把收到的消息傳遞給子類
			} else {//如果是空消息，說明是關閉應用的廣播
				close();
			}
		}
	};

	/**
	 * 抽象方法，用於子類處理消息，
	 * 
	 * @param msg
	 *            傳遞給子類的消息物件
	 */
	public abstract void getMessage(TranObject msg);

	/**
	 * 子類直接調用這個方法關閉應用
	 */
	public void close() {
		Intent i = new Intent();
		i.setAction(Constants.ACTION);
		sendBroadcast(i);
		finish();
	}

	@Override
	public void onStart() {// 在start方法中註冊廣播接收者
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.ACTION);
		registerReceiver(MsgReceiver, intentFilter);// 註冊接受消息廣播

	}

	@Override
	protected void onStop() {// 在stop方法中註銷廣播接收者
		super.onStop();
		unregisterReceiver(MsgReceiver);// 登出接受消息廣播
	}
}
