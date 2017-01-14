package com.way.chat.activity;

import java.util.ArrayList;
import java.util.List;

import tw.com.irons.calendar.CalendarActivity;
import tw.com.irons.try_case2.R;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.way.chat.common.bean.TextMessage;
import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;
import com.way.chat.common.util.Constants;
import com.way.client.Client;
import com.way.client.ClientOutputThread;
import com.way.util.DialogFactory;
import com.way.util.GroupFriend;
import com.way.util.MessageDB;
import com.way.util.MyDate;
import com.way.util.SharePreferenceUtil;
import com.way.util.UserDB;

public class AddFriend extends MyActivity{
	private int newNum = 0;
	private MyApplication application;
	private MessageDB messageDB;// 消息資料庫物件
	private UserDB userDB;// 保存好友列表資料庫物件
	private ListView listView;
	private TextView textView;
	private AddAdapter myExAdapter;// 好
	private List<GroupFriend> group;// 需要傳遞給適配器的資料
	private String[] groupName = { "我的好友", "我的同學", "我的家人" };// 大組成員名
	
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_friend);
		
		textView=(TextView)findViewById(R.id.textView1);
		listView=(ListView)findViewById(R.id.addFriend);
		
		userDB = new UserDB(this);// 本地使用者資料庫
		messageDB = new MessageDB(this);// 本地消息資料庫
		getAllUser();
		
		//myExAdapter = new AddAdapter(this, group);
		//listView.setAdapter(myExAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
	
				final int addID = group.get(0).getChild(arg2)
						.getId();
				Builder builder = new Builder(AddFriend.this);
				builder.setTitle("新增好友");
				builder.setMessage("是否確定將帳號:"+addID+"，加入您的好友?");
				builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						sendAddFriend(addID);
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				builder.show();

			}
		});
	}

	@Override
	public void getMessage(TranObject msg) {
		Log.e("tag", "get1");
		// TODO Auto-generated method stub
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
			
			String imagePath = "/mnt/sdcard/"+fromUser+dailyTime+".bmp";
			
			ChatMsgEntity entity = new ChatMsgEntity("", MyDate.getDateEN(),
					message, -1, true, imagePath);// 收到的消息
			messageDB.saveMsg(msg.getFromUser(), entity);// 保存到資料庫
			Toast.makeText(AddFriend.this,
					"親！新消息哦 " + msg.getFromUser() + ":" + message, 0).show();// 提示用戶
			MediaPlayer.create(this, R.raw.msg).start();// 聲音提示
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
			Toast.makeText(AddFriend.this,
					"親！" + loginUser.getId() + "上線了哦", 0).show();
			MediaPlayer.create(this, R.raw.msg).start();
			loginUser.setIsOnline(1);
			//meAdapter.notifyDataSetChanged();
			break;
		case LOGOUT:
			User logoutUser = (User) msg.getObject();
			Toast.makeText(AddFriend.this,
					"親！" + logoutUser.getId() + "下線了哦", 0).show();
			MediaPlayer.create(this, R.raw.msg).start();
			logoutUser.setIsOnline(0);
			//meAdapter.notifyDataSetChanged();
			break;
		case FRIEND:
			//List<User> list = (List<User>) msg.getObject();
			ArrayList<User> list = (ArrayList) msg.getObject();
			if(list == null){
				DialogFactory.ToastDialog(AddFriend.this, "QQ登錄",
						"親！您的帳號或密碼錯誤哦");
			}
			Log.e("tag", "get2");
			if (list.size() > 0) {
				// 保存使用者資訊
				//SharePreferenceUtil util = new SharePreferenceUtil(
						//AddFriend.this, Constants.SAVE_USER);
				//util.setId(mAccounts.getText().toString());
				//util.setPasswd(mPassword.getText().toString());
				//util.setEmail(list.get(0).getEmail());
				//util.setName(list.get(0).getName());
				//util.setImg(list.get(0).getImg());

				//UserDB db = new UserDB(AddFriend.this);
				//db.addUser(list);
				
				Log.e("tag", "getalluser");
				
				initListViewData(list);
				myExAdapter = new AddAdapter(this, group);
				listView.setAdapter(myExAdapter);
				/*
				Intent i = new Intent(LoginActivity.this,
						FriendListActivity.class);
				i.putExtra(Constants.MSGKEY, msg);
				startActivity(i);
				*/
				//finish();
				Toast.makeText(getApplicationContext(), "登錄成功", 0).show();
			} 
			
		
			break;
			
		case ADDFRIEND:
			//List<User> list = (List<User>) msg.getObject();
			List<User> newFriendList = (List<User>) msg.getObject();
	
			UserDB userDB = new UserDB(this);
			userDB.delete();
			//newFriendList.remove(0);
			userDB.addUser(newFriendList);
			
			Log.e("tag", "add");
				
				//Toast.makeText(getApplicationContext(), "登錄成功", 0).show();
				//Toast.makeText(AddFriend.this,
						//"已成功加入好友", 0).show();
				
			MediaPlayer.create(this, R.raw.inn).start();
			Intent intent = new Intent("tw.com.irons.try_case2.addFriend");
			sendBroadcast(intent);
			
			FriendListSendActivity.isAddFriend = true;
			
				Builder builder = new Builder(AddFriend.this);
				builder.setTitle("已成功加入好友");
				builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						finish();
					}
				});
				builder.show();
				
				//Intent intent = new Intent(this, FriendListSendActivity.class);
				//startActivity(intent);
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

			Builder builder2 = new Builder(AddFriend.this);
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
		default:
			break;
		}
	}
	

	public void getAllUser(){
		send();
	}
	
	private void send() {
		//String contString = mEditTextContent.getText().toString();
		
		//String imgPath="/mnt/sdcard/test1.bmp";
		
		SharePreferenceUtil util;
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		
		//if (dailyContent.length() >= 0) {
			/*
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setName(util.getName());
			entity.setDate(MyDate.getDateEN());
			entity.setMessage(contString);
			entity.setImg(util.getImg());
			entity.setMsgType(false);
			entity.setDailyImg(imagePath);
					
			messageDB.saveMsg(user.getId(), entity);
			
			mEditTextContent.setText("");// 清空編輯方塊數據
			 */
			//dailyContent = mEditTextContent.getText().toString();
			

			
			MyApplication application = (MyApplication) this
					.getApplicationContext();
			Client client = application.getClient();
			ClientOutputThread out = client.getClientOutputThread();
			if (out != null) {
				TranObject<User> o = new TranObject<User>(
						TranObjectType.FRIEND);
				//TextMessage message = new TextMessage();
				//message.setMessage(dailyContent);
				
				
				Log.e("date", "123");
				
				User u = new User();
				o.setObject(u);
				o.setFromUser(Integer.parseInt(util.getId()));
				//o.setToUser(user.getId());
				//o.setImage(imagePath);
				//o.setDailyTime(dailyTime);
				out.setMsg(o);
				
				//finish();
			//}
			/*
			// 下面是添加到最近會話列表的處理，在按發送鍵之後
			RecentChatEntity entity1 = new RecentChatEntity(user.getId(),
					user.getImg(), 0, user.getName(), MyDate.getDate(),
					contString);
			application.getmRecentList().remove(entity1);
			application.getmRecentList().addFirst(entity1);
			application.getmRecentAdapter().notifyDataSetChanged();*/
		}
	}
	
	private void initListViewData(List<User> list) {
		User me = new User();
		SharePreferenceUtil util;
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		me.setId(Integer.parseInt(util.getId()));
		list.add(me);
		
		List<User> friend = new ArrayList<User>();
		UserDB userDB;
		userDB = new UserDB(AddFriend.this);
		friend = userDB.getUser();
		friend.add(me);
		List<User> notfriend = new ArrayList<User>();
		
		boolean isFriend = false;
		
		for (User u : list) {
			//if (u.getGroup() == i)// 判斷一下是屬於哪個大組
			for(User uFriend : friend){
				if(uFriend.getId() == u.getId()){
					isFriend=true;
					break;
				}
			}
			if(!isFriend){
				notfriend.add(u);
			}else {
				isFriend=false;
			}
		}
		
		//list.remove(0);//刪除自己
		
		group = new ArrayList<GroupFriend>();// 產生實體
		for (int i = 0; i < groupName.length; ++i) {// 根據大組的數量，迴圈給各大組分配成員
			List<User> child = new ArrayList<User>();// 裝小組成員的list
			GroupFriend groupInfo = new GroupFriend(groupName[i], child);// 我們自訂的大組成員物件
			for (User u : notfriend) {
				if (u.getGroup() == i)// 判斷一下是屬於哪個大組
					child.add(u);
			}
			group.add(groupInfo);// 把自訂大組成員物件放入一個list中，傳遞給適配器
		}
	}
	
	
	private void sendAddFriend(int addId) {
		//String contString = mEditTextContent.getText().toString();
		
		//String imgPath="/mnt/sdcard/test1.bmp";
		
		SharePreferenceUtil util;
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		
		//if (dailyContent.length() >= 0) {
	
			//dailyContent = mEditTextContent.getText().toString();
			

			
			MyApplication application = (MyApplication) this
					.getApplicationContext();
			Client client = application.getClient();
			ClientOutputThread out = client.getClientOutputThread();
			if (out != null) {
				TranObject<User> o = new TranObject<User>(
						TranObjectType.ADDFRIEND);
				//TextMessage message = new TextMessage();
				//message.setMessage(dailyContent);
				
				
				Log.e("date", "123");
				
				
				User u = new User();
				o.setObject(u);
				o.setFromUser(Integer.parseInt(util.getId()));
				o.setToUser(addId);
				out.setMsg(o);
				
				//finish();
			//}
		}
	}
}
