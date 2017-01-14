package com.way.chat.activity;

import tw.com.irons.try_case2.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;
import com.way.client.Client;
import com.way.client.ClientOutputThread;
import com.way.util.DialogFactory;
import com.way.util.Encode;

public class RegisterActivity extends MyActivity implements OnClickListener {

	private Button mBtnRegister;
	private Button mRegBack;
	private EditText mEmailEt, mNameEt, mPasswdEt, mPasswdEt2;
	
	private MyApplication application;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.register);
		application = (MyApplication) this.getApplicationContext();
		initView();

	}

	public void initView() {
		mBtnRegister = (Button) findViewById(R.id.register_btn);
		mRegBack = (Button) findViewById(R.id.reg_back_btn);
		mBtnRegister.setOnClickListener(this);
		mRegBack.setOnClickListener(this);

		mEmailEt = (EditText) findViewById(R.id.reg_email);
		mNameEt = (EditText) findViewById(R.id.reg_name);
		mPasswdEt = (EditText) findViewById(R.id.reg_password);
		mPasswdEt2 = (EditText) findViewById(R.id.reg_password2);

	}

	private Dialog mDialog = null;

	private void showRequestDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(this, "正在註冊中...");
		mDialog.show();
	}

	@Override
	public void onBackPressed() {// 捕獲返回鍵
		toast(RegisterActivity.this);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.register_btn:
			// showRequestDialog();
			estimate();
			break;
		case R.id.reg_back_btn:
			toast(RegisterActivity.this);
			break;
		default:
			break;
		}
	}

	private void toast(Context context) {
		new AlertDialog.Builder(context).setTitle("QQ註冊")
				.setMessage("親！您真的不註冊了嗎？")
				.setPositiveButton("確定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).setNegativeButton("取消", null).create().show();
	}

	private void estimate() {
		String email = mEmailEt.getText().toString();
		String name = mNameEt.getText().toString();
		String passwd = mPasswdEt.getText().toString();
		String passwd2 = mPasswdEt2.getText().toString();
		if (email.equals("") || name.equals("") || passwd.equals("")
				|| passwd2.equals("")) {
			DialogFactory.ToastDialog(RegisterActivity.this, "QQ註冊",
					"親！帶*項是不能為空的哦");
		} else {
			if (passwd.equals(passwd2)) {
				showRequestDialog();
				// 提交註冊資訊
				if (application.isClientStart()) {// 如果已連接上伺服器
					Client client = application.getClient();
//					Client client = GetMsgService.client;
					ClientOutputThread out = client.getClientOutputThread();
					TranObject<User> o = new TranObject<User>(
							TranObjectType.REGISTER);
					User u = new User();
					u.setEmail(email);
					u.setName(name);
					u.setPassword(Encode.getEncode("MD5", passwd));
					o.setObject(u);
					out.setMsg(o);
				} else {
					if (mDialog.isShowing())
						mDialog.dismiss();
					DialogFactory.ToastDialog(this, "QQ註冊", "親！伺服器暫未開放哦");
				}

			} else {
				DialogFactory.ToastDialog(RegisterActivity.this, "QQ註冊",
						"親！您兩次輸入的密碼不同哦");
			}
		}
	}

	@Override
	public void getMessage(TranObject msg) {
		// TODO Auto-generated method stub
		switch (msg.getType()) {
		case REGISTER:
			User u = (User) msg.getObject();
			int id = u.getId();
			if (id > 0) {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}
				DialogFactory.ToastDialog(RegisterActivity.this, "QQ註冊",
						"親！請牢記您的登錄QQ哦：" + id);
			} else {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}
				DialogFactory.ToastDialog(RegisterActivity.this, "QQ註冊",
						"親！很抱歉！QQ號暫時缺貨哦");
			}
			break;

		default:
			break;
		}
	}
}
