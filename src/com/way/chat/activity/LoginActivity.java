package com.way.chat.activity;

import java.util.List;

import tw.com.irons.calendar.CalendarActivity;
import tw.com.irons.try_case2.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;
import com.way.chat.common.util.Constants;
import com.way.client.Client;
import com.way.client.ClientOutputThread;
import com.way.util.DialogFactory;
import com.way.util.Encode;
import com.way.util.SharePreferenceUtil;
import com.way.util.UserDB;

/**
 * 登錄
 * 
 * @author way
 * 
 */
public class LoginActivity extends MyActivity implements OnClickListener {
	private Button mBtnRegister;
	private Button mBtnLogin;
	private EditText mAccounts, mPassword;
	private CheckBox mAutoSavePassword;
	private MyApplication application;

	private View mMoreView;// “更多登錄選項”的view
	private ImageView mMoreImage;// “更多登錄選項”的箭頭圖片
	private View mMoreMenuView;// “更多登錄選項”中的內容view
	private MenuInflater mi;// 菜單
	private boolean mShowMenu = false;// “更多登錄選項”的內容是否顯示

	public void onCreate(Bundle savedInstanceState) {
		/*
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		.detectNetwork() // 這裡可以替換為detectAll() 就包括了磁片讀寫和網路I/O
		.penaltyLog() //列印logcat，當然也可以定位到dropbox，通過檔保存相應的log
		.build());*/
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginpage);
		application = (MyApplication) this.getApplicationContext();
		initView();
		mi = new MenuInflater(this);
	}

	@Override
	protected void onResume() {// 在onResume方法裡面先判斷網路是否可用，再啟動服務,這樣在打開網路連接之後返回當前Activity時，會重新開機服務聯網，
		super.onResume();
		
		Intent service2 = new Intent(this, GetMsgService.class);
		startService(service2);
		
		if (isNetworkAvailable()) {
			int newNum = application.getRecentNum();// 從新獲取一下全域變數
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
			
			//Intent service = new Intent(this, GetMsgService.class);
			//startService(service);
		} else {
			toast(this);
		}
	}


	public void initView() {
		mAutoSavePassword = (CheckBox) findViewById(R.id.auto_save_password);
		mMoreView = findViewById(R.id.more);
		mMoreMenuView = findViewById(R.id.moremenu);
		mMoreImage = (ImageView) findViewById(R.id.more_image);
		mMoreView.setOnClickListener(this);

		mBtnRegister = (Button) findViewById(R.id.regist_btn);
		mBtnRegister.setOnClickListener(this);

		mBtnLogin = (Button) findViewById(R.id.login_btn);
		mBtnLogin.setOnClickListener(this);

		mAccounts = (EditText) findViewById(R.id.lgoin_accounts);
		mPassword = (EditText) findViewById(R.id.login_password);
		if (mAutoSavePassword.isChecked()) {
			SharePreferenceUtil util = new SharePreferenceUtil(
					LoginActivity.this, Constants.SAVE_USER);
			mAccounts.setText(util.getId());
			mPassword.setText(util.getPasswd());
		}
	}

	/**
	 * “更多登錄選項”內容的顯示方法
	 * 
	 * @param bShow
	 *            是否顯示
	 */
	public void showMoreView(boolean bShow) {
		if (bShow) {
			mMoreMenuView.setVisibility(View.GONE);
			mMoreImage.setImageResource(R.drawable.login_more_up);
			mShowMenu = true;
		} else {
			mMoreMenuView.setVisibility(View.VISIBLE);
			mMoreImage.setImageResource(R.drawable.login_more);
			mShowMenu = false;
		}
	}

	/**
	 * 處理點擊事件
	 */
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.more:
			showMoreView(!mShowMenu);
			break;
		case R.id.regist_btn:
			goRegisterActivity();
			break;
		case R.id.login_btn:
			submit();
			break;
		default:
			break;
		}
	}

	/**
	 * 進入註冊介面
	 */
	public void goRegisterActivity() {
		Intent intent = new Intent();
		intent.setClass(this, RegisterActivity.class);
		startActivity(intent);
	}

	/**
	 * 點擊登錄按鈕後，彈出驗證對話方塊
	 */
	private Dialog mDialog = null;

	private void showRequestDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(this, "正在驗證帳號...");
		mDialog.show();
	}

	/**
	 * 提交帳號密碼資訊到伺服器
	 */
	private void submit() {
		String accounts = mAccounts.getText().toString();
		String password = mPassword.getText().toString();
		if (accounts.length() == 0 || password.length() == 0) {
			DialogFactory.ToastDialog(this, "QQ登錄", "親！帳號或密碼不能為空哦");
		} else {
			showRequestDialog();
			// 通過Socket驗證資訊
			if (application.isClientStart()) {
				Client client = application.getClient();
				ClientOutputThread out = client.getClientOutputThread();
				TranObject<User> o = new TranObject<User>(TranObjectType.LOGIN);
				User u = new User();
				u.setId(Integer.parseInt(accounts));
				u.setPassword(Encode.getEncode("MD5", password));
				o.setObject(u);
				out.setMsg(o);
			} else {
				if (mDialog.isShowing())
					mDialog.dismiss();
				DialogFactory.ToastDialog(LoginActivity.this, "QQ登錄",
						"親！伺服器暫未開放哦");
			}
		}
	}

	@Override
	// 依據自己需求處理父類廣播接收者收取到的消息
	public void getMessage(TranObject msg) {
		if (msg != null) {
			// System.out.println("Login:" + msg);
			switch (msg.getType()) {
			case LOGIN:// LoginActivity只處理登錄的消息
				List<User> list = (List<User>) msg.getObject();
				Log.e("login", "ok1");
				if(list == null){
					DialogFactory.ToastDialog(LoginActivity.this, "QQ登錄",
							"親！您的帳號或密碼錯誤哦");
					if (mDialog.isShowing())
						mDialog.dismiss();
				}
				
				if (list.size() > 0) {
					Log.e("login", "ok2");
					// 保存使用者資訊
					SharePreferenceUtil util = new SharePreferenceUtil(
							LoginActivity.this, Constants.SAVE_USER);
					util.setId(mAccounts.getText().toString());
					util.setPasswd(mPassword.getText().toString());
					util.setEmail(list.get(0).getEmail());
					util.setName(list.get(0).getName());
					util.setImg(list.get(0).getImg());

					UserDB db = new UserDB(LoginActivity.this);
					db.addUser(list);

					/*
					Intent i = new Intent(LoginActivity.this,
							FriendListActivity.class);
					i.putExtra(Constants.MSGKEY, msg);
					startActivity(i);
					*/
					FriendListSendActivity.isAddFriend=false;
					FriendListActivity.isAddFriend=false;
					
					//開啟服務
					Intent i = new Intent();
					i.setAction(Constants.BACKKEY_ACTION);
					sendBroadcast(i);

					util.setIsStart(true);// 設置後臺運行標誌，正在運行
					
					Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(
							null, CalendarActivity.MIME_TYPE);
					intent.putExtra(Constants.MSGKEY, msg);
					startActivity(intent);
					
					if (mDialog.isShowing())
						mDialog.dismiss();
					finish();
					Toast.makeText(getApplicationContext(), "登錄成功", 0).show();
				} else {
					DialogFactory.ToastDialog(LoginActivity.this, "QQ登錄",
							"親！您的帳號或密碼錯誤哦");
					if (mDialog.isShowing())
						mDialog.dismiss();
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	// 添加菜單
	public boolean onCreateOptionsMenu(Menu menu) {
		mi.inflate(R.menu.login_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	// 功能表選項添加事件處理
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.login_menu_setting:
			//setDialog();
			break;
		case R.id.login_menu_exit:
			exitDialog(LoginActivity.this, "QQ提示", "親！您真的要退出嗎？");
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {// 捕獲返回按鍵
		exitDialog(LoginActivity.this, "QQ提示", "親！您真的要退出嗎？");
	}

	/**
	 * 退出時的提示框
	 * 
	 * @param context
	 *            上下文對象
	 * @param title
	 *            標題
	 * @param msg
	 *            內容
	 */
	private void exitDialog(Context context, String title, String msg) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
				.setPositiveButton("確定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (application.isClientStart()) {// 如果連接還在，說明服務還在運行
							// 關閉服務
							Intent service = new Intent(LoginActivity.this,
									GetMsgService.class);
							stopService(service);
						}
						close();// 調用父類自訂的迴圈關閉方法
					}
				}).setNegativeButton("取消", null).create().show();
	}

	/**
	 * “設置”功能表選項的功能實現
	 */
	/*
	private void setDialog() {
		final View view = LayoutInflater.from(LoginActivity.this).inflate(
				R.layout.setting_view, null);
		new AlertDialog.Builder(LoginActivity.this).setTitle("設置伺服器ip、port")
				.setView(view)
				.setPositiveButton("確定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 把ip和port保存到檔中
						EditText ipEditText = (EditText) view
								.findViewById(R.id.setting_ip);
						EditText portEditText = (EditText) view
								.findViewById(R.id.setting_port);
						String ip = ipEditText.getText().toString();
						String port = portEditText.getText().toString();
						SharePreferenceUtil util = new SharePreferenceUtil(
								LoginActivity.this, Constants.IP_PORT);
						if (ip.length() > 0 && port.length() > 0) {
							util.setIp(ip);
							util.setPort(Integer.valueOf(port));
							Toast.makeText(getApplicationContext(),
									"親！保存成功，重啟生效哦", 0).show();
							finish();
						}else{
							Toast.makeText(getApplicationContext(),
									"親！ip和port都不能為空哦", 0).show();
						}
					}
				}).setNegativeButton("取消", null).create().show();
	}*/

	/**
	 * 判斷手機網路是否可用
	 * 
	 * @param context
	 * @return
	 */
	private boolean isNetworkAvailable() {
		ConnectivityManager mgr = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = mgr.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	private void toast(Context context) {
		new AlertDialog.Builder(context)
				.setTitle("溫馨提示")
				.setMessage("親！您的網路連接未打開哦")
				.setPositiveButton("前往打開",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										android.provider.Settings.ACTION_WIRELESS_SETTINGS);
								startActivity(intent);
							}
						}).setNegativeButton("取消", null).create().show();
	}
}

