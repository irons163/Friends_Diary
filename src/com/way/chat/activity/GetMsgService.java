package com.way.chat.activity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import tw.com.irons.try_case2.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.way.chat.common.bean.TextMessage;
import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;
import com.way.chat.common.util.Constants;
import com.way.client.Client;
import com.way.client.ClientInputThread;
import com.way.client.ClientOutputThread;
import com.way.client.MessageListener;
import com.way.util.MessageDB;
import com.way.util.MyDate;
import com.way.util.SharePreferenceUtil;
import com.way.util.UserDB;

/**
 * 收取消息服務
 * 
 * @author way
 * 
 */
public class GetMsgService extends Service {
	private static final int MSG = 0x001;
	private MyApplication application;
	private Client client;
	private NotificationManager mNotificationManager;
	private boolean isStart = false;// 是否與伺服器連接上
	private Notification mNotification;
	private Context mContext = this;
	private SharePreferenceUtil util;
	private MessageDB messageDB;
	
	private UserDB userDB;// 保存好友列表資料庫物件
	// 收到用戶按返回鍵發出的廣播，就顯示通知欄
	private BroadcastReceiver backKeyReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Toast.makeText(context, "QQ進入後臺運行", 0).show();
			setMsgNotification();
		}
	};
	// 用來更新通知欄消息的handler
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG:
				int newMsgNum = application.getNewMsgNum();// 從全域變數中獲取
				newMsgNum++;// 每收到一次消息，自增一次
				application.setNewMsgNum(newMsgNum);// 再設置為全域變數
				TranObject<TextMessage> textObject = (TranObject<TextMessage>) msg
						.getData().getSerializable("msg");
				

				
				// System.out.println(textObject);
				if (textObject != null) {
					int form = textObject.getFromUser();// 消息從哪裡來
					String content = textObject.getObject().getMessage();// 消息內容
					
					TranObject Object = (TranObject)msg.getData().getSerializable("msg");
					int fromUser = Object.getFromUser();
					String dailyTime = Object.getDailyTime();
					
					String imagePath = "/mnt/sdcard/"+fromUser+dailyTime+".bmp";
					
					userDB = new UserDB(GetMsgService.this);
					User user2 = userDB.selectInfo(Object.getFromUser());// 通過id查詢對應資料庫該好友資訊
					RecentChatEntity entity2 = new RecentChatEntity(Object.getFromUser(),
							user2.getImg(), 1, user2.getName(), MyDate.getDate(),
							content);
					application.getmRecentAdapter().remove(entity2);// 先移除該物件，目的是添加到首部
					application.getmRecentList().addFirst(entity2);// 再添加到首部
					application.getmRecentAdapter().notifyDataSetChanged();
					
					


					ChatMsgEntity entity = new ChatMsgEntity("",
							MyDate.getDateEN(), content, -1, true, imagePath);// 收到的消息
					messageDB.saveMsg(form, entity);// 保存到資料庫

					// 更新通知欄
					int icon = R.drawable.notify_newmessage;
					CharSequence tickerText = form + ":" + content;
					long when = System.currentTimeMillis();
					mNotification = new Notification(icon, tickerText, when);

					mNotification.flags = Notification.FLAG_NO_CLEAR;
					// 設置默認聲音
					mNotification.defaults |= Notification.DEFAULT_SOUND;
					// 設定震動(需加VIBRATE許可權)
					mNotification.defaults |= Notification.DEFAULT_VIBRATE;
					mNotification.contentView = null;

					Intent intent = new Intent(mContext,
							FriendListActivity.class);
					PendingIntent contentIntent = PendingIntent.getActivity(
							mContext, 0, intent, 0);
					mNotification.setLatestEventInfo(mContext, util.getName()
							+ " (" + newMsgNum + "條新消息)", content,
							contentIntent);
				}
				mNotificationManager.notify(Constants.NOTIFY_ID, mNotification);// 通知一下才會生效哦
				break;

			default:
				break;
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {// 在onCreate方法裡面註冊廣播接收者
		// TODO Auto-generated method stub
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		.detectDiskReads()
		.detectDiskWrites()
		.detectNetwork() // 這裡可以替換為detectAll() 就包括了磁片讀寫和網路I/O
		.penaltyLog() //列印logcat，當然也可以定位到dropbox，通過檔保存相應的log
		.build());
		/*
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		.detectLeakedSqlLiteObjects() //探測SQLite資料庫操作
		.penaltyLog() //列印logcat
		.penaltyDeath()
		.build()); */
		
		super.onCreate();
		messageDB = new MessageDB(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.BACKKEY_ACTION);
		registerReceiver(backKeyReceiver, filter);
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		application = (MyApplication) this.getApplicationContext();
		client = application.getClient();
		application.setmNotificationManager(mNotificationManager);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		util = new SharePreferenceUtil(getApplicationContext(),
				Constants.SAVE_USER);
		isStart = client.start();
		application.setClientStart(isStart);
		System.out.println("client start:" + isStart);
		if (isStart) {
			ClientInputThread in = client.getClientInputThread();
			in.setMessageListener(new MessageListener() {

				@Override
				public void Message(TranObject msg) {
					// System.out.println("GetMsgService:" + msg);
					if (util.getIsStart()) {// 如果 是在後臺運行，就更新通知欄，否則就發送廣播給Activity
						if (msg.getType() == TranObjectType.MESSAGE) {// 只處理文本消息類型
							// System.out.println("收到新消息");
							// 把消息物件發送到handler去處理
							
							int fromUser = msg.getFromUser();
							String dailyTime = msg.getDailyTime();

							String imagePath = "/mnt/sdcard/" + fromUser + dailyTime + ".bmp";
							
							if(msg.getImage()!=null){
								Log.i("e", msg.toString());
								String str = msg.getImage().toString();
								Log.i("e", str);
								//fos.write(msg.getImage());
								//fos.close();
								Bitmap bitmap =BitmapFactory.decodeByteArray(msg.getImage(), 0,msg.getImage().length);
								try {
									OutputStream os = new FileOutputStream(imagePath);
									bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
									os.flush();
									os.close();
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								}
							
							Message message = handler.obtainMessage();
							message.what = MSG;
							message.getData().putSerializable("msg", msg);
							handler.sendMessage(message);
						}
					} else {
						Intent broadCast = new Intent();
						broadCast.setAction(Constants.ACTION);
						broadCast.putExtra(Constants.MSGKEY, msg);
						Log.e("b", msg.toString());
						
	
						
						sendBroadcast(broadCast);// 把收到的消息已廣播的形式發送出去
					}
				}
			});
		}
	}

	@Override
	// 在服務被摧毀時，做一些事情
	public void onDestroy() {
		super.onDestroy();
		if (messageDB != null)
			messageDB.close();
		unregisterReceiver(backKeyReceiver);
		mNotificationManager.cancel(Constants.NOTIFY_ID);
		// 給伺服器發送下線消息
		if (isStart) {
			ClientOutputThread out = client.getClientOutputThread();
			TranObject<User> o = new TranObject<User>(TranObjectType.LOGOUT);
			User u = new User();
			u.setId(Integer.parseInt(util.getId()));
			o.setObject(u);
			out.setMsg(o);
			// 發送完之後，關閉client
			out.setStart(false);
			client.getClientInputThread().setStart(false);
		}
		// Intent intent = new Intent(this, GetMsgService.class);
		// startService(intent);
	}

	/**
	 * 創建通知
	 */
	private void setMsgNotification() {
		int icon = R.drawable.notify;
		CharSequence tickerText = "";
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, tickerText, when);

		// 放置在"正在運行"欄目中
		mNotification.flags = Notification.FLAG_ONGOING_EVENT;

		RemoteViews contentView = new RemoteViews(mContext.getPackageName(),
				R.layout.notify_view);
		contentView.setTextViewText(R.id.notify_name, util.getName());
		contentView.setTextViewText(R.id.notify_msg, "手機QQ正在後臺運行");
		contentView.setTextViewText(R.id.notify_time, MyDate.getDate());
		// 指定個性化視圖
		mNotification.contentView = contentView;

		Intent intent = new Intent(this, FriendListActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// 指定內容意圖
		mNotification.contentIntent = contentIntent;
		mNotificationManager.notify(Constants.NOTIFY_ID, mNotification);
	}
}

