package com.way.chat.activity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import tw.com.irons.try_case2.R;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.way.chat.common.bean.TextMessage;
import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.util.Constants;
import com.way.util.GroupFriend;
import com.way.util.MessageDB;
import com.way.util.MyDate;
import com.way.util.SharePreferenceUtil;
import com.way.util.UserDB;

/**
 * 好友列表的Activity
 * 
 * @author way
 * 
 */
public class FriendListSendActivity extends MyActivity implements
		OnClickListener {

	private static final int PAGE1 = 0;// 頁面1
	private static final int PAGE2 = 1;// 頁面2
	private static final int PAGE3 = 2;// 頁面3
	private List<GroupFriend> group;// 需要傳遞給適配器的資料
	private String[] groupName = { "我的好友", "我的同學", "我的家人" };// 大組成員名
	private SharePreferenceUtil util;
	private UserDB userDB;// 保存好友列表資料庫物件
	private MessageDB messageDB;// 消息資料庫物件

	private ListView myListView;// 好友列表自訂listView
	private MyExAdapter myExAdapter;// 好

	private ListView mRecentListView;// 最近會話的listView
	private int newNum = 0;

	private ListView mGroupListView;// 群組listView

	private ViewPager mPager;
	private List<View> mListViews;// Tab頁面
	private LinearLayout layout_body_activity;

	private ImageView img_group_friend;// 群組

	private ImageView myHeadImage;// 頭像
	private TextView myName;// 名字

	private ImageView cursor;// 標題背景圖片

	private int currentIndex = PAGE2; // 預設選中第2個，可以動態的改變此參數值
	private int offset = 0;// 動畫圖片偏移量
	private int bmpW;// 動畫圖片寬度

	private TranObject msg;
	private List<User> list;
	private MenuInflater mi;// 菜單
	private int[] imgs = { R.drawable.icon, R.drawable.f1, R.drawable.f2,
			R.drawable.f3, R.drawable.f4, R.drawable.f5, R.drawable.f6,
			R.drawable.f7, R.drawable.f8, R.drawable.f9 };// 頭像資源
	private MyApplication application;

	MyExAdapter meAdapter;
	Intent intent2;
	private Button mBtnBack;// 返回btn
	private Button mAddFriend;
	myResetReceiver receiver;
    static boolean isAddFriend = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉標題列
		setContentView(R.layout.friend_list_send);
		application = (MyApplication) this.getApplicationContext();

		Intent intent = getIntent();
		Bundle bundle = new Bundle();
		bundle = intent.getExtras();

		intent2 = new Intent(FriendListSendActivity.this, SendOnly.class);
		intent2.putExtras(bundle);

		IntentFilter filter = new IntentFilter(
				"tw.com.irons.try_case2.addFriend");
		receiver = new myResetReceiver();
		registerReceiver(receiver, filter);

		initData();// 初始化數據
		initUI();// 初始化介面
	}

	@Override
	protected void onResume() {// 如果從後臺恢復，服務被系統幹掉，就重啟一下服務
		// TODO Auto-generated method stub
		newNum = application.getRecentNum();// 從新獲取一下全域變數
		if (!application.isClientStart()) {
			Intent service = new Intent(this, GetMsgService.class);
			startService(service);
		}
		new SharePreferenceUtil(this, Constants.SAVE_USER).setIsStart(false);
		NotificationManager manager = application.getmNotificationManager();
		if (manager != null) {
			manager.cancel(Constants.NOTIFY_ID);
			application.setNewMsgNum(0);// 把消息數目置0
			application.getmRecentAdapter().notifyDataSetChanged();
		}
		super.onResume();
	}

	/**
	 * 初始化系統資料
	 */
	private void initData() {
		userDB = new UserDB(FriendListSendActivity.this);// 本地使用者資料庫
		messageDB = new MessageDB(this);// 本地消息資料庫
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);

		// userDB.deleteUser();

		msg = (TranObject) getIntent().getSerializableExtra(Constants.MSGKEY);// 從intent中取出消息物件
		if (isAddFriend) {
			list = userDB.getUser();
		} else {
			if (msg == null) {// 如果為空，說明是從後臺切換過來的，需要從資料庫中讀取好友清單資訊
				Log.e("aa", "aa");
				list = userDB.getUser();
			} else {// 如果是登錄介面切換過來的，就把好友清單資訊保存到資料庫
				Log.e("bb", msg.toString() + "11");
				list = (List<User>) msg.getObject();
				userDB.updateUser(list);
			}
		}
		initListViewData(list);
	}

	/**
	 * 處理伺服器傳遞過來的使用者陣列資料，
	 * 
	 * @param list
	 *            從伺服器獲取的使用者陣列
	 */
	private void initListViewData(List<User> list) {
		if(!isAddFriend){
		list.remove(0);// 刪除自己
		}
		group = new ArrayList<GroupFriend>();// 產生實體
		for (int i = 0; i < groupName.length; ++i) {// 根據大組的數量，迴圈給各大組分配成員
			List<User> child = new ArrayList<User>();// 裝小組成員的list
			GroupFriend groupInfo = new GroupFriend(groupName[i], child);// 我們自訂的大組成員物件
			for (User u : list) {
				if (u.getGroup() == i)// 判斷一下是屬於哪個大組
					child.add(u);
			}
			group.add(groupInfo);// 把自訂大組成員物件放入一個list中，傳遞給適配器
		}
	}

	private void initUI() {
		mi = new MenuInflater(this);
		layout_body_activity = (LinearLayout) findViewById(R.id.bodylayout);

		mBtnBack = (Button) findViewById(R.id.chat_back);
		mBtnBack.setOnClickListener(this);

		layout_body_activity.setFocusable(true);

		mPager = (ViewPager) findViewById(R.id.viewPager);
		mListViews = new ArrayList<View>();
		LayoutInflater inflater = LayoutInflater.from(this);
		View lay1 = inflater.inflate(R.layout.tab1, null);
		View lay2 = inflater.inflate(R.layout.tab2, null);

		mListViews.add(lay1);
		mListViews.add(lay2);

		mPager.setAdapter(new MyPagerAdapter(mListViews));
		mPager.setCurrentItem(PAGE2);
		// mPager.setOnPageChangeListener(new MyOnPageChangeListener());

		// 下面是最近會話介面處理
		mRecentListView = (ListView) lay1.findViewById(R.id.tab1_listView);
		// mRecentAdapter = new RecentChatAdapter(FriendListActivity.this,
		// application.getmRecentList());// 從全域變數中獲取最近聊天物件陣列
		mRecentListView.setAdapter(application.getmRecentAdapter());// 先設置空物件，要麼從資料庫中讀出

		// 下面是處理好友清單介面處理
		myListView = (ListView) lay2.findViewById(R.id.tab2_listView);
		Constants.WhoCall = "SEND";
		myExAdapter = new MyExAdapter(this, group, intent2);
		myListView.setAdapter(myExAdapter);
		// myListView.setGroupIndicator(null);// 不設置大組指示器圖示，因為我們自訂設置了
		myListView.setDivider(null);// 設置圖片可拉伸的
		myListView.setFocusable(true);// 聚焦才可以下拉刷新
		// myListView.setonRefreshListener(new MyRefreshListener());

		mAddFriend = (Button) findViewById(R.id.button1);
		mAddFriend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(FriendListSendActivity.this,
						AddFriend.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.chat_back:// 返回按鈕點擊事件
			finish();// 結束,實際開發中，可以返回主介面
			break;
		case R.id.button1:// 返回按鈕點擊事件
			Intent intent = new Intent(FriendListSendActivity.this,
					AddFriend.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mi.inflate(R.menu.friend_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (messageDB != null)
			messageDB.close();
	}

	@Override
	// 功能表選項添加事件處理
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.friend_menu_add:
			Toast.makeText(getApplicationContext(), "親！此功能暫未實現哦", 0).show();
			break;
		case R.id.friend_menu_exit:
			exitDialog(FriendListSendActivity.this, "QQ提示", "親！您真的要退出嗎？");
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 完全退出提示窗
	private void exitDialog(Context context, String title, String msg) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
				.setPositiveButton("確定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 關閉服務
						if (application.isClientStart()) {
							Intent service = new Intent(
									FriendListSendActivity.this,
									GetMsgService.class);
							stopService(service);
						}
						close();// 父類關閉方法
					}
				}).setNegativeButton("取消", null).create().show();
	}

	@Override
	public void onBackPressed() {// 捕獲返回按鍵事件，進入後臺運行
		// TODO Auto-generated method stub
		// 發送廣播，通知服務，已進入後臺運行
		Intent i = new Intent();
		i.setAction(Constants.BACKKEY_ACTION);
		sendBroadcast(i);

		util.setIsStart(true);// 設置後臺運行標誌，正在運行
		finish();// 再結束自己
	}

	@Override
	public void getMessage(TranObject msg) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		switch (msg.getType()) {
		case MESSAGE:
			newNum++;
			application.setRecentNum(newNum);// 保存到全域變數
			TextMessage tm = (TextMessage) msg.getObject();
			String message = tm.getMessage();

			int fromUser = msg.getFromUser();
			String dailyTime = msg.getDailyTime();

			String imagePath = "/mnt/sdcard/" + fromUser + dailyTime + ".bmp";

			ChatMsgEntity entity = new ChatMsgEntity("", MyDate.getDateEN(),
					message, -1, true, imagePath);// 收到的消息
			messageDB.saveMsg(msg.getFromUser(), entity);// 保存到資料庫
			Toast.makeText(FriendListSendActivity.this,
					"親！新消息哦 " + msg.getFromUser() + ":" + message, 0).show();// 提示用戶
			MediaPlayer.create(this, R.raw.msg).start();// 聲音提示
			
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
			
			User user2 = userDB.selectInfo(msg.getFromUser());// 通過id查詢對應資料庫該好友資訊
			RecentChatEntity entity2 = new RecentChatEntity(msg.getFromUser(),
					user2.getImg(), 1, user2.getName(), MyDate.getDate(),
					message);
			application.getmRecentAdapter().remove(entity2);// 先移除該物件，目的是添加到首部
			application.getmRecentList().addFirst(entity2);// 再添加到首部
			application.getmRecentAdapter().notifyDataSetChanged();
			break;
		case LOGIN:
			User loginUser = (User) msg.getObject();
			Toast.makeText(FriendListSendActivity.this,
					"親！" + loginUser.getId() + "上線了哦", 0).show();
			MediaPlayer.create(this, R.raw.msg).start();
			loginUser.setIsOnline(1);
			// meAdapter.notifyDataSetChanged();
			break;
		case LOGOUT:
			User logoutUser = (User) msg.getObject();
			Toast.makeText(FriendListSendActivity.this,
					"親！" + logoutUser.getId() + "下線了哦", 0).show();
			MediaPlayer.create(this, R.raw.msg).start();
			logoutUser.setIsOnline(0);
			// meAdapter.notifyDataSetChanged();
			break;
			
		case DELETEFRIEND:

			List<User> newFriendList = (List<User>) msg.getObject();

			UserDB userDB = new UserDB(this);
			userDB.delete();

			userDB.addUser(newFriendList);

			Log.e("tag", "add");

			//Toast.makeText(getApplicationContext(), "登錄成功", 0).show();
			//Toast.makeText(FriendListSendActivity.this, "已成功刪除好友", 0).show();
			// MediaPlayer.create(this, R.raw.inn).start();

			Intent intent = new Intent("tw.com.irons.try_case2.addFriend");
			sendBroadcast(intent);
			
			FriendListSendActivity.isAddFriend = true;
			
			Builder builder = new Builder(FriendListSendActivity.this);
			builder.setTitle("您已成功刪除好友");
			builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

					//finish();
				}
			});
			builder.show();

			break;
		case RECADDFRIEND:
			//List<User> list = (List<User>) msg.getObject();
			List<User> newFriendList2 = (List<User>) msg.getObject();
	
			UserDB userDB2 = new UserDB(this);
			userDB2.delete();
			//newFriendList.remove(0);
			userDB2.addUser(newFriendList2);
			
			Log.e("tag", "add");
			
			MediaPlayer.create(this, R.raw.inn).start();
			
			Intent intent2 = new Intent("tw.com.irons.try_case2.addFriend");
			sendBroadcast(intent2);

			FriendListSendActivity.isAddFriend = true;
			
			Builder builder2 = new Builder(FriendListSendActivity.this);
			builder2.setTitle("您已被加入好友");
			builder2.setPositiveButton("確定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

					//finish();
				}
			});
			builder2.show();
	
				//Intent intent = new Intent(this, FriendListSendActivity.class);
				//startActivity(intent);
			break;
			
		case RECDELETEFRIEND:

			List<User> newFriendList3 = (List<User>) msg.getObject();

			UserDB userDB3 = new UserDB(this);
			userDB3.delete();

			userDB3.addUser(newFriendList3);

			Log.e("tag", "add");

			//Toast.makeText(getApplicationContext(), "登錄成功", 0).show();
			//Toast.makeText(FriendListActivity.this, "已成功刪除好友", 0).show();
			// MediaPlayer.create(this, R.raw.inn).start();

			Intent intent3 = new Intent("tw.com.irons.try_case2.addFriend");
			sendBroadcast(intent3);

			FriendListSendActivity.isAddFriend = true;
			
			Builder builder3 = new Builder(FriendListSendActivity.this);
			builder3.setTitle("您已被好友刪除");
			builder3.setPositiveButton("確定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

					//finish();
				}
			});
			builder3.show();
			
			break;
		default:
			break;
		}
	}

	private class myResetReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("FriendSend", "ok");
			list = userDB.getUser();
			isAddFriend=true;
			initListViewData(list);
			myExAdapter = new MyExAdapter(FriendListSendActivity.this, group,
					intent2);
			myListView.setAdapter(myExAdapter);
			// myExAdapter.notifyDataSetChanged();
			
		}
	}
}
