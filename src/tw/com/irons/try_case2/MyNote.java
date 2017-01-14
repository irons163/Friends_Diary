package tw.com.irons.try_case2;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import tw.com.irons.try_case2.db.MyDBHelper;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.irons.HelloFacebookSampleActivity;
import com.way.chat.activity.FriendListSendActivity;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.util.Constants;

public class MyNote extends Activity {
	// private TableLayout table;
	private TextView priority, title;
	private MyDBHelper dbHelper;
	private SQLiteDatabase db;
	private Resources res;
	ListView listView;
	String[] clickDate;
	static String strClickDate;
	// ArrayList<String> list;
	public static ArrayList<HashMap<String, String>> list;
	myResetReceiver receiver;
	String realImagePath;
	String imagePath;
	Uri uri;
	Bitmap bitmap;
	String fileName;
	String extStorage;
	ImageView dailyImage;
	// Button changeImage;
	String rangeFrom;
	String rangeTo;
	Cursor cursor2;
	Intent intent2;
	Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		res = getResources();

		// 搜尋目前的資料表
		dbHelper = new MyDBHelper(MyNote.this, "irons", null, 1);
		db = dbHelper.getReadableDatabase();
		resetTable();

		IntentFilter filter = new IntentFilter("tw.com.irons.try_case2");
		receiver = new myResetReceiver();
		registerReceiver(receiver, filter);

		TranObject msg = (TranObject) getIntent().getSerializableExtra(
				Constants.MSGKEY);
		intent2 = new Intent(MyNote.this, FriendListSendActivity.class);
		intent2.putExtra(Constants.MSGKEY, msg);

	}

	@Override
	public void finish() {
		super.finish();
		db.close();
		dbHelper.close();
		unregisterReceiver(receiver);
	}

	private class myResetReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			resetTable();
		}
	}

	private void resetTable() {

		Cursor cursor = db.query("notebook", null, null, null, null, null,
				"time desc");
		Toast.makeText(this, "搜索到" + cursor.getCount() + "条日程",
				Toast.LENGTH_SHORT).show();
		while (cursor.moveToNext()) {
			addRecord(cursor.getString(2), cursor.getString(1),
					cursor.getString(5), cursor.getInt(0), cursor.getString(3),
					cursor.getString(6));
		}
		cursor.close();

		SharedPreferences preferences = getSharedPreferences("clickDate", 0);

		clickDate = preferences.getString("clickDate", "").split("-");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < clickDate.length; i++) {

			sb.append(clickDate[i]);
		}
		strClickDate = sb.toString();
		Cursor cursor2 = dbHelper.onClickDate(db, strClickDate);
		Toast.makeText(this, "搜1索到" + cursor2.getCount() + "条日程",
				Toast.LENGTH_SHORT).show();

		if (cursor2.getCount() == 0) {
			showDaily(0, null, null, null, null, null, true);

		} else {
		}
		while (cursor2.moveToNext()) {
			showDaily(cursor2.getInt(0), cursor2.getString(2),
					cursor2.getString(3), cursor2.getString(1),
					cursor2.getString(5), cursor2.getString(6), false);
		}

	}

	private void addRecord(String time, String t, String i, int id, String c,
			String r) {

		cursor = db
				.query("notebook", null, null, null, null, null, "time desc");
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.listview, cursor, new String[] { "imagePath", "time",
						"title" }, new int[] { R.id.imageView1, R.id.t1,
						R.id.t2 });

	}

	@TargetApi(16)
	private void editRecord(int id, String dataTime, String c, String t,
			final String imagePath, final String realImagePath) {
		// 顯示修改視窗
		AlertDialog alert = null;

		LayoutInflater dialog = LayoutInflater.from(this);
		View dview = dialog.inflate(R.layout.edit, null);

		final int did = id;
		final EditText et = (EditText) dview.findViewById(R.id.editTitle);
		final EditText cont = (EditText) dview.findViewById(R.id.editCont);
		dailyImage = (ImageView) dview.findViewById(R.id.dailyImage);
		Button changeImage = (Button) dview.findViewById(R.id.changeImage);

		Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
		if (bitmap != null)
			dailyImage.setImageBitmap(bitmap);

		// dailyImage.setBackground(null);

		TextView textView = (TextView) dview.findViewById(R.id.timeText);
		textView.setText(dataTime);

		et.setText(t);
		cont.setText(c);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("日記資訊");
		builder.setView(dview);
		builder.setCancelable(true);

		builder.setPositiveButton(res.getString(R.string.edit_update),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String sql = "UPDATE notebook SET " + "title = '"
								+ et.getText() + "', " + "content = '"
								+ cont.getText() + "', " + "imagePath = '"
								+ imagePath + "', " + "realImagePath = '"
								+ realImagePath + "'" + " WHERE " + "_id = "
								+ did;

						db.execSQL(sql);
						resetTable();
					}

				});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		changeImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				// 開啟Pictures畫面Type設定為image
				intent.setType("image/*");
				// * 使用Intent.ACTION_GET_CONTENT這個Action
				intent.setAction(Intent.ACTION_GET_CONTENT);
				// * 取得相片後返回本畫面
				startActivityForResult(intent, 1);
			}
		});

		alert = builder.create();
		alert.show();
	}

	@SuppressLint("NewApi")
	private void showDaily(final int id, final String dataTime, final String c,
			final String t, final String imagePath, final String realImagePath,
			boolean isEmpty) {
		setContentView(R.layout.show_daily);

		SharedPreferences preferences = getSharedPreferences("clickDate", 0);
		int bg = preferences.getInt("background", 0);
		if (bg != 0) {
			View layout = (View) findViewById(R.id.layout1);
			layout.setBackgroundDrawable(getResources().getDrawable(bg));
		}

		final int did = id;
		final EditText et = (EditText) findViewById(R.id.editTitle);
		final EditText cont = (EditText) findViewById(R.id.editCont);
		ImageView dailyImage = (ImageView) findViewById(R.id.dailyImage);
		Button button, button2, button4, button5, button6, button7;

		button = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		// button3 = (Button) findViewById(R.id.button3);
		button4 = (Button) findViewById(R.id.button4);
		button5 = (Button) findViewById(R.id.button5);
		button6 = (Button) findViewById(R.id.button6);
		button7 = (Button) findViewById(R.id.button7);

		TextView textView = (TextView) findViewById(R.id.timeText);

		if (isEmpty) {
			SharedPreferences preferences2 = getSharedPreferences("clickDate",
					0);
			textView.setText(preferences2.getString("clickDate", ""));
			// dailyImage.setBackground(null);
			et.setText("無標題");
			cont.setText("無內容");
			button.setEnabled(false);
			button2.setEnabled(false);
			// button3.setEnabled(false);
			button4.setEnabled(true);
			button5.setEnabled(false);
			button6.setEnabled(false);
		} else {
			button.setEnabled(true);
			button2.setEnabled(true);
			// button3.setEnabled(true);
			button4.setEnabled(false);
			button5.setEnabled(true);
			button6.setEnabled(true);

			Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
			if (bitmap != null)
				dailyImage.setImageBitmap(bitmap);

			textView.setText(dataTime);

			et.setText(t);
			cont.setText(c);
			MyNote.this.realImagePath = realImagePath;
		}

		dailyImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Dialog dialog = new Dialog(MyNote.this);
				ImageView imageView = new ImageView(MyNote.this);
				Bitmap bitmap = BitmapFactory
						.decodeFile(MyNote.this.realImagePath);

				imageView.setImageBitmap(bitmap);
				dialog.setContentView(imageView);
				dialog.show();

			}
		});

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(MyNote.this,
						HelloFacebookSampleActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("title", t);
				bundle.putString("content", c);
				bundle.putString("time", dataTime);
				bundle.putString("imagePath", imagePath);
				bundle.putString("realImagePath", realImagePath);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String date = strClickDate;
				Bundle bundle = new Bundle();
				bundle.putString("imagePath", imagePath);
				bundle.putString("dailyTime", date);
				bundle.putString("dailyContent", c);
				intent2.putExtras(bundle);
				startActivity(intent2);

				cursor.close();

				// Intent intent3 = new Intent(MyNote.this,
				// FriendListSendActivity.class);
				// startActivity(intent3);
			}
		});

		button4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MyNote.this, CreateDaily.class);
				startActivity(intent);
			}
		});

		button5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editRecord(id, dataTime, c, t, imagePath, realImagePath);
			}
		});

		button6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				delDaily();
			}
		});

		button7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listDaily(true);
			}
		});

	}

	public void delDaily() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("刪除日記");
		builder.setMessage("是否確定刪除記錄時間為" + strClickDate + "的日記");
		builder.setCancelable(true);

		builder.setPositiveButton(res.getString(R.string.edit_update),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String sql = "delete from notebook where time="
								+ strClickDate;

						db.execSQL(sql);
						resetTable();
					}

				});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.show();
	}

	public void listDaily(boolean callFromDailyList) {
		setContentView(R.layout.dailylist);

		SharedPreferences preferences = getSharedPreferences("clickDate", 0);
		int bg = preferences.getInt("background", 0);
		if (bg != 0) {
			View layout = (View) findViewById(R.id.layout1);
			layout.setBackgroundDrawable(getResources().getDrawable(bg));
		}

		listView = (ListView) findViewById(R.id.listView1);
		// final Cursor cursor = db.query("notebook", null, null, null, null,
		// null, "time desc");

		// cursor.moveToFirst();

		if (callFromDailyList) {
			cursor2 = db.query("notebook", null, null, null, null, null,
					"time desc");
		} else {
			cursor2 = dbHelper.onSearchDate(db, rangeFrom, rangeTo);
			Toast.makeText(this, "搜2索到" + cursor2.getCount() + "条日程",
					Toast.LENGTH_SHORT).show();
		}

		Toast.makeText(this, "搜索到" + cursor2.getCount() + "条日程",
				Toast.LENGTH_SHORT).show();

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.listview, cursor2, new String[] { "imagePath", "time",
						"title" }, new int[] { R.id.imageView1, R.id.t1,
						R.id.t2 });
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				cursor2.moveToPosition(arg2);

				int id = cursor2.getInt(0);
				String time = cursor2.getString(2);
				String c = cursor2.getString(3);
				String t = cursor2.getString(1);
				String i = cursor2.getString(5);
				String ri = cursor2.getString(6);

				editRecord(id, time, c, t, i, ri);
			}
		});

		// cursor2.close(); 注意!千萬不能關閉cursor2，因為 SimpleCursorAdapter 會用到。
	}

	/*
	 * public void searchDialy() {
	 * 
	 * setContentView(R.layout.dailysearch_fromdate);
	 * 
	 * SharedPreferences preferences = getSharedPreferences("clickDate", 0); int
	 * bg = preferences.getInt("background", 0); if (bg != 0) { View layout =
	 * (View) findViewById(R.id.layout1);
	 * layout.setBackgroundDrawable(getResources().getDrawable(bg)); }
	 * 
	 * DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker1);
	 * DatePicker datePicker2 = (DatePicker) findViewById(R.id.datePicker2);
	 * Button button = (Button) findViewById(R.id.button1); Button button2 =
	 * (Button) findViewById(R.id.button2);
	 * 
	 * Calendar c; c = Calendar.getInstance();
	 * 
	 * String y = c.get(Calendar.YEAR) + ""; String m = (c.get(Calendar.MONTH) +
	 * 1) + ""; String n = c.get(Calendar.DAY_OF_MONTH) + "";
	 * 
	 * rangeFrom = y + m + n; rangeTo = rangeFrom;
	 * datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
	 * c.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
	 * 
	 * @Override public void onDateChanged(DatePicker view, int year, int
	 * monthOfYear, int dayOfMonth) { // TODO Auto-generated method stub String
	 * y = year + ""; String m = (monthOfYear + 1) + ""; String n = dayOfMonth +
	 * ""; rangeFrom = y + m + n; } });
	 * 
	 * datePicker2.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
	 * c.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
	 * 
	 * @Override public void onDateChanged(DatePicker view, int year, int
	 * monthOfYear, int dayOfMonth) { // TODO Auto-generated method stub String
	 * y = year + ""; String m = (monthOfYear + 1) + ""; String n = dayOfMonth +
	 * ""; rangeTo = y + m + n; } });
	 * 
	 * button.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub if (rangeFrom.compareTo(rangeTo) > 0) { Toast.makeText(MyNote.this,
	 * "起始日起不能大於中止日期", Toast.LENGTH_SHORT).show(); return; }
	 * 
	 * listDaily(false); } });
	 * 
	 * button2.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub return; } });
	 * 
	 * }
	 */

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			uri = data.getData();
			ContentResolver cr = this.getContentResolver();
			// imagePath = uri.toString();
			Log.e("uri", uri.toString());
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				options.inSampleSize = 20;
				bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri),
						null, options);

				Drawable drawable = new BitmapDrawable(bitmap);

				/* 將Bitmap設定到ImageView */
				dailyImage.setImageBitmap(bitmap);
				dailyImage.setAdjustViewBounds(true);
				dailyImage.setMaxWidth(100);
				dailyImage.setMaxHeight(100);
				// dailyImage.setBackground(null);

				// 取得原始圖檔名稱
				fileName = uri.getPath().substring(
						uri.getPath().lastIndexOf("/"));

				// SD Card 目的資料夾
				extStorage = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/thumbnail";

				realImagePath = uri.getPath();

				if (bitmap.isRecycled()) {
					bitmap.recycle();
					System.gc();
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		// cursor.close();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (cursor != null)
			cursor.close();
	}
}
