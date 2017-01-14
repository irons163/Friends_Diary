package com.way.chat.activity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import tw.com.irons.try_case2.R;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.way.chat.common.bean.TextMessage;
import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;
import com.way.chat.common.util.Constants;
import com.way.client.Client;
import com.way.client.ClientOutputThread;
import com.way.util.MessageDB;
import com.way.util.MyDate;
import com.way.util.SharePreferenceUtil;
import com.way.util.UserDB;



public class SendOnly extends MyActivity implements OnClickListener {
	private Button mBtnSend;// 發送btn
	private Button mBtnBack;// 返回btn
	private EditText mEditTextContent;
	private TextView mFriendName;

	private SharePreferenceUtil util;
	private User user;
	private MessageDB messageDB;
	private MyApplication application;

	MyExAdapter meAdapter ;
	private ImageView dailyImage;
	String imagePath;
	String dailyTime;
	String dailyContent;
	
	private UserDB userDB;// 保存好友列表資料庫物件
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉標題列
		setContentView(R.layout.send);
		application = (MyApplication) getApplicationContext();
		messageDB = new MessageDB(this);
		user = (User) getIntent().getSerializableExtra("user");
		util = new SharePreferenceUtil(this, Constants.SAVE_USER);
		
		
		initView();// 初始化view
		//initData();// 初始化數據
		
		Intent intent = getIntent();
		Bundle bundle = new Bundle();
		bundle = intent.getExtras();
		imagePath = bundle.getString("imagePath");
		dailyTime = bundle.getString("dailyTime");
		dailyContent = bundle.getString("dailyContent");
		
		mEditTextContent.setText(dailyContent);
		
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
		
		dailyImage.setImageBitmap(bitmap);
	}

	/**
	 * 初始化view
	 */
	public void initView() {
		
		mBtnSend = (Button) findViewById(R.id.chat_send);
		mBtnSend.setOnClickListener(this);
		mBtnBack = (Button) findViewById(R.id.chat_back);
		mBtnBack.setOnClickListener(this);
		mFriendName = (TextView) findViewById(R.id.chat_name);
		mFriendName.setText(user.getName());
		mEditTextContent = (EditText) findViewById(R.id.editCont);
		dailyImage = (ImageView)findViewById(R.id.dailyImage);
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
			send();
			break;
		case R.id.chat_back:// 返回按鈕點擊事件
			finish();// 結束,實際開發中，可以返回主介面
			break;
		}
	}

	/**
	 * 發送消息
	 */
	private void send() {
		//String contString = mEditTextContent.getText().toString();
		
		//String imgPath="/mnt/sdcard/test1.bmp";
		

		
		if (dailyContent.length() >= 0) {
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
			dailyContent = mEditTextContent.getText().toString();
			

			
			MyApplication application = (MyApplication) this
					.getApplicationContext();
			Client client = application.getClient();
			ClientOutputThread out = client.getClientOutputThread();
			if (out != null) {
				TranObject<TextMessage> o = new TranObject<TextMessage>(
						TranObjectType.MESSAGE);
				TextMessage message = new TextMessage();
				message.setMessage(dailyContent);
				
				
				Log.e("date", dailyTime);
				
				
				o.setObject(message);
				o.setFromUser(Integer.parseInt(util.getId()));
				o.setToUser(user.getId());
				o.setImage(imagePath);
				o.setDailyTime(dailyTime);
				out.setMsg(o);
				
				finish();
			}
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
			
			
			
			
            
			userDB = new UserDB(SendOnly.this);
			User user2 = userDB.selectInfo(msg.getFromUser());// 通過id查詢對應資料庫該好友資訊
			RecentChatEntity entity2 = new RecentChatEntity(msg.getFromUser(),
					user2.getImg(), 1, user2.getName(), MyDate.getDate(),
					message);
			application.getmRecentAdapter().remove(entity2);// 先移除該物件，目的是添加到首部
			application.getmRecentList().addFirst(entity2);// 再添加到首部
			application.getmRecentAdapter().notifyDataSetChanged();
			
			
			ChatMsgEntity entity = new ChatMsgEntity(user.getName(),
					MyDate.getDateEN(), message, user.getImg(), true, imagePath);// 收到的消息

				messageDB.saveMsg(msg.getFromUser(), entity);// 保存到資料庫
				Toast.makeText(SendOnly.this,
						"您有新的消息來自：" + msg.getFromUser() + ":" + message, 0)
						.show();// 其他好友的消息，就先提示，並保存到資料庫
				MediaPlayer.create(this, R.raw.msg).start();
			
			
			break;
		case LOGIN:
			User loginUser = (User) msg.getObject();
			Toast.makeText(SendOnly.this, loginUser.getId() + "上線了", 0)
					.show();
			MediaPlayer.create(this, R.raw.msg).start();
			
			meAdapter.notifyDataSetChanged();
			break;
		case LOGOUT:
			User logoutUser = (User) msg.getObject();
			Toast.makeText(SendOnly.this, logoutUser.getId() + "下線了", 0)
					.show();
			MediaPlayer.create(this, R.raw.msg).start();
			meAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}
}
