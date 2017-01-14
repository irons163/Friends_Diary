package com.way.chat.activity;

import java.util.ArrayList;
import java.util.List;

import tw.com.irons.try_case2.R;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
public class FriendListSendActivity extends MyActivity implements OnClickListener {

	private static final int PAGE1 = 0;// 页面1
	private static final int PAGE2 = 1;// 页面2
	private static final int PAGE3 = 2;// 页面3
	private List<GroupFriend> group;// 需要传递给适配器的数据
	private String[] groupName = { "我的好友", "我的同学", "我的家人" };// 大组成员名
	private SharePreferenceUtil util;
	private UserDB userDB;// 保存好友列表数据库对象
	private MessageDB messageDB;// 消息数据库对象

	private ListView myListView;// 好友列表自定义listView
	private MyExAdapter myExAdapter;// 好

	private ListView mRecentListView;// 最近会话的listView
	private int newNum = 0;

	private ListView mGroupListView;// 群组listView

	private ViewPager mPager;
	private List<View> mListViews;// Tab页面
	private LinearLayout layout_body_activity;
	
	private ImageView img_group_friend;// 群组

	private ImageView myHeadImage;// 头像
	private TextView myName;// 名字

	private ImageView cursor;// 标题背景图片

	private int currentIndex = PAGE2; // 默认选中第2个，可以动态的改变此参数值
	private int offset = 0;// 动画图片偏移量
	private int bmpW;// 动画图片宽度

	private TranObject msg;
	private List<User> list;
	private MenuInflater mi;// 菜单
	private int[] imgs = { R.drawable.icon, R.drawable.f1, R.drawable.f2,
			R.drawable.f3, R.drawable.f4, R.drawable.f5, R.drawable.f6,
			R.drawable.f7, R.drawable.f8, R.drawable.f9 };// 头像资源
	private MyApplication application;

	MyExAdapter meAdapter ;
	Intent intent2;
	private Button mBtnBack;// 返回btn
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.friend_list_send);
		application = (MyApplication) this.getApplicationContext();
		
		Intent intent = getIntent();
		Bundle bundle = new Bundle();
		bundle = intent.getExtras();
		
		intent2 = new Intent(FriendListSendActivity.this, SendOnly.class);
		intent2.putExtras(bundle);
		
		initData();// 初始化数据
		initUI();// 初始化界面
	}

	@Override
	protected void onResume() {// 如果从后台恢复，服务被系统干掉，就重启一下服务
		// TODO Auto-generated method stub
		newNum = application.getRecentNum();// 从新获取一下全局变量
		if (!application.isClientStart()) {
			Intent service = new Intent(this, GetMsgService.class);
			startService(service);
		}
		new SharePreferenceUtil(this, Constants.SAVE_USER).setIsStart(false);
		NotificationManager manager = application.getmNotificationManager();
		if (manager != null) {
			manager.cancel(Constants.NOTIFY_ID);
			application.setNewMsgNum(0);// 把消息数目置0
			application.getmRecentAdapter().notifyDataSetChanged();
		}
		super.onResume();
	}

	/**
	 * 初始化系统数据
	 */
	private void initData() {
		userDB = new UserDB(FriendListSendActivity.this);// 本地用户数据库
		messageDB = new MessageDB(this);// 本地消息数据库
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);

		msg = (TranObject) getIntent().getSerializableExtra(Constants.MSGKEY);// 从intent中取出消息对象
		if (msg == null) {// 如果为空，说明是从后台切换过来的，需要从数据库中读取好友列表信息
			Log.e("aa", "aa");
			list = userDB.getUser();
		} else {// 如果是登录界面切换过来的，就把好友列表信息保存到数据库
			Log.e("bb", msg.toString()+"11");
			list = (List<User>) msg.getObject();
			userDB.updateUser(list);
		}
		initListViewData(list);
	}

	/**
	 * 处理服务器传递过来的用户数组数据，
	 * 
	 * @param list
	 *            从服务器获取的用户数组
	 */
	private void initListViewData(List<User> list) {
		group = new ArrayList<GroupFriend>();// 实例化
		for (int i = 0; i < groupName.length; ++i) {// 根据大组的数量，循环给各大组分配成员
			List<User> child = new ArrayList<User>();// 装小组成员的list
			GroupFriend groupInfo = new GroupFriend(groupName[i], child);// 我们自定义的大组成员对象
			for (User u : list) {
				if (u.getGroup() == i)// 判断一下是属于哪个大组
					child.add(u);
			}
			group.add(groupInfo);// 把自定义大组成员对象放入一个list中，传递给适配器
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
		//mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		
		// 下面是最近会话界面处理
		mRecentListView = (ListView) lay1.findViewById(R.id.tab1_listView);
		// mRecentAdapter = new RecentChatAdapter(FriendListActivity.this,
		// application.getmRecentList());// 从全局变量中获取最近聊天对象数组
		mRecentListView.setAdapter(application.getmRecentAdapter());// 先设置空对象，要么从数据库中读出

		// 下面是处理好友列表界面处理
		myListView = (ListView) lay2.findViewById(R.id.tab2_listView);
		Constants.WhoCall = "SEND";
		myExAdapter = new MyExAdapter(this, group, intent2);
		myListView.setAdapter(myExAdapter);
		//myListView.setGroupIndicator(null);// 不设置大组指示器图标，因为我们自定义设置了
		myListView.setDivider(null);// 设置图片可拉伸的
		myListView.setFocusable(true);// 聚焦才可以下拉刷新
		//myListView.setonRefreshListener(new MyRefreshListener());


	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.chat_back:// 返回按钮点击事件
			finish();// 结束,实际开发中，可以返回主界面
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
	// 菜单选项添加事件处理
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.friend_menu_add:
			Toast.makeText(getApplicationContext(), "亲！此功能暂未实现哦", 0).show();
			break;
		case R.id.friend_menu_exit:
			exitDialog(FriendListSendActivity.this, "QQ提示", "亲！您真的要退出吗？");
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 完全退出提示窗
	private void exitDialog(Context context, String title, String msg) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 关闭服务
						if (application.isClientStart()) {
							Intent service = new Intent(
									FriendListSendActivity.this,
									GetMsgService.class);
							stopService(service);
						}
						close();// 父类关闭方法
					}
				}).setNegativeButton("取消", null).create().show();
	}



	@Override
	public void onBackPressed() {// 捕获返回按键事件，进入后台运行
		// TODO Auto-generated method stub
		// 发送广播，通知服务，已进入后台运行
		Intent i = new Intent();
		i.setAction(Constants.BACKKEY_ACTION);
		sendBroadcast(i);

		util.setIsStart(true);// 设置后台运行标志，正在运行
		finish();// 再结束自己
	}

	@Override
	public void getMessage(TranObject msg) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		switch (msg.getType()) {
		case MESSAGE:
			newNum++;
			application.setRecentNum(newNum);// 保存到全局变量
			TextMessage tm = (TextMessage) msg.getObject();
			String message = tm.getMessage();
			
			int fromUser = msg.getFromUser();
			String dailyTime = msg.getDailyTime();
			
			String imagePath = "/mnt/sdcard/"+fromUser+dailyTime+".bmp";
			
			ChatMsgEntity entity = new ChatMsgEntity("", MyDate.getDateEN(),
					message, -1, true, imagePath);// 收到的消息
			messageDB.saveMsg(msg.getFromUser(), entity);// 保存到数据库
			Toast.makeText(FriendListSendActivity.this,
					"亲！新消息哦 " + msg.getFromUser() + ":" + message, 0).show();// 提示用户
			MediaPlayer.create(this, R.raw.msg).start();// 声音提示
			User user2 = userDB.selectInfo(msg.getFromUser());// 通过id查询对应数据库该好友信息
			RecentChatEntity entity2 = new RecentChatEntity(msg.getFromUser(),
					user2.getImg(), 1, user2.getName(), MyDate.getDate(),
					message);
			application.getmRecentAdapter().remove(entity2);// 先移除该对象，目的是添加到首部
			application.getmRecentList().addFirst(entity2);// 再添加到首部
			application.getmRecentAdapter().notifyDataSetChanged();
			break;
		case LOGIN:
			User loginUser = (User) msg.getObject();
			Toast.makeText(FriendListSendActivity.this,
					"亲！" + loginUser.getId() + "上线了哦", 0).show();
			MediaPlayer.create(this, R.raw.msg).start();
			loginUser.setIsOnline(1);
			//meAdapter.notifyDataSetChanged();
			break;
		case LOGOUT:
			User logoutUser = (User) msg.getObject();
			Toast.makeText(FriendListSendActivity.this,
					"亲！" + logoutUser.getId() + "下线了哦", 0).show();
			MediaPlayer.create(this, R.raw.msg).start();
			logoutUser.setIsOnline(0);
			//meAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}

}
