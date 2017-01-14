package com.way.chat.activity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tw.com.irons.try_case2.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.way.chat.common.bean.TextMessage;
import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.util.Constants;
import com.way.util.MessageDB;
import com.way.util.MyDate;
import com.way.util.SharePreferenceUtil;
import com.way.util.UserDB;

/**
 * 聊天Activity
 * 
 * @author way
 */
public class ChatActivity extends MyActivity implements OnClickListener {
	//private Button mBtnSend;// 發送btn
	private Button mBtnBack;// 返回btn
	private EditText mEditTextContent;
	private TextView mFriendName;
	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;// 消息視圖的Adapter
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();// 消息物件陣列
	private SharePreferenceUtil util;
	private User user;
	private MessageDB messageDB;
	private MyApplication application;
	
	
	private UserDB userDB;// 保存好友列表資料庫物件
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉標題列
		setContentView(R.layout.chat);
		application = (MyApplication) getApplicationContext();
		messageDB = new MessageDB(this);
		user = (User) getIntent().getSerializableExtra("user");
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		initView();// 初始化view
		initData();// 初始化數據
	}

	/**
	 * 初始化view
	 */
	public void initView() {
		mListView = (ListView) findViewById(R.id.listview);
		//mBtnSend = (Button) findViewById(R.id.chat_send);
		//mBtnSend.setOnClickListener(this);
		mBtnBack = (Button) findViewById(R.id.chat_back);
		mBtnBack.setOnClickListener(this);
		mFriendName = (TextView) findViewById(R.id.chat_name);
		mFriendName.setText(user.getName());

	}

	/**
	 * 載入消息歷史，從資料庫中讀出
	 */
	public void initData() {
		List<ChatMsgEntity> list = messageDB.getMsg(user.getId());
		if (list.size() > 0) {
			for (ChatMsgEntity entity : list) {
				if (entity.getName().equals("")) {
					entity.setName(user.getName());
				}
				if (entity.getImg() < 0) {
					entity.setImg(user.getImg());
				}
				//entity.get
				mDataArrays.add(entity);
			}
			Collections.reverse(mDataArrays);
		}
		mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);
		mListView.setSelection(mAdapter.getCount() - 1);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		messageDB.close();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chat_send:// 發送按鈕點擊事件
			//send();
			break;
		case R.id.chat_back:// 返回按鈕點擊事件
			finish();// 結束,實際開發中，可以返回主介面
			break;
		}
	}


	@Override
	public void getMessage(TranObject msg) {
		// TODO Auto-generated method stub
		switch (msg.getType()) {
		case MESSAGE:
			
			TextMessage tm = (TextMessage) msg.getObject();
			String message = tm.getMessage();
			
			int fromUser = msg.getFromUser();
			String dailyTime = msg.getDailyTime();
			
			String imagePath = "/mnt/sdcard/"+fromUser+dailyTime+".bmp";
			
			
            //FileOutputStream fos = new FileOutputStream("/mnt/sdcard/test2.bmp");
			//byte[] buffer = new byte[39923];
			//DataInputStream dis = new DataInputStream(null);
			//dis.read(msg.getImage());
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
			//dis.close();
			
			
			
			
            
			userDB = new UserDB(ChatActivity.this);
			User user2 = userDB.selectInfo(msg.getFromUser());// 通過id查詢對應資料庫該好友資訊
			RecentChatEntity entity2 = new RecentChatEntity(msg.getFromUser(),
					user2.getImg(), 1, user2.getName(), MyDate.getDate(),
					message);
			application.getmRecentAdapter().remove(entity2);// 先移除該物件，目的是添加到首部
			application.getmRecentList().addFirst(entity2);// 再添加到首部
			application.getmRecentAdapter().notifyDataSetChanged();
            

			ChatMsgEntity entity = new ChatMsgEntity(user.getName(),
					MyDate.getDateEN(), message, user.getImg(), true, imagePath);// 收到的消息
			if (msg.getFromUser() == user.getId() || msg.getFromUser() == 0) {// 如果是正在聊天的好友的消息，或者是伺服器的消息

				messageDB.saveMsg(user.getId(), entity);

				mDataArrays.add(entity);
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(mListView.getCount() - 1);
				MediaPlayer.create(this, R.raw.msg).start();
			} else {
				messageDB.saveMsg(msg.getFromUser(), entity);// 保存到資料庫
				Toast.makeText(ChatActivity.this,
						"您有新的消息來自：" + msg.getFromUser() + ":" + message, 0)
						.show();// 其他好友的消息，就先提示，並保存到資料庫
				MediaPlayer.create(this, R.raw.msg).start();
			}
			break;
		case LOGIN:
			User loginUser = (User) msg.getObject();
			Toast.makeText(ChatActivity.this, loginUser.getId() + "上線了", 0)
					.show();
			MediaPlayer.create(this, R.raw.msg).start();
			
			break;
		case LOGOUT:
			User logoutUser = (User) msg.getObject();
			Toast.makeText(ChatActivity.this, logoutUser.getId() + "下線了", 0)
					.show();
			MediaPlayer.create(this, R.raw.msg).start();
			break;
		default:
			break;
		}
	}
}
