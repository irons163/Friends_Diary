package tw.com.irons.try_case2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import tw.com.irons.try_case2.db.MyDBHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CreateDaily extends Activity {
	ImageView imageView;
	Button button, button2, button3, button4;
	EditText editText, editText2;
	private MyDBHelper dbHelper;
	private SQLiteDatabase db;
	private Button myButton01;
	private ImageView myImageView01;
	String imagePath;
	Uri uri;
	Bitmap bitmap;
	String fileName;
	String extStorage;
	String realImagePath;
	String time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_daily);

		imageView = (ImageView) findViewById(R.id.imageView1);
		editText = (EditText) findViewById(R.id.editText1);
		editText2 = (EditText) findViewById(R.id.editText2);
		button4 = (Button) findViewById(R.id.button4);
		button = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);

		myImageView01 = (ImageView) findViewById(R.id.dailyImage);

		button4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent();
				/* 開啟Pictures畫面Type設定為image */
				intent.setType("image/*");
				/* 使用Intent.ACTION_GET_CONTENT這個Action */
				intent.setAction(Intent.ACTION_GET_CONTENT);
				/* 取得相片後返回本畫面 */
				startActivityForResult(intent, 1);
			}
		});

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dbHelper = new MyDBHelper(CreateDaily.this, "irons", null, 1);
				db = dbHelper.getReadableDatabase();

				String title = editText.getText().toString().trim();
				String content = editText2.getText().toString();

				if (extStorage != null) {
					File dir = new File(extStorage);
					if (!dir.exists())
						dir.mkdirs();

					File file = new File(extStorage, fileName);

					try {
						// Bitmap bitmap = getBitmap(uri);
						OutputStream outStream = new FileOutputStream(file);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
								outStream);
						outStream.flush();
						outStream.close();
					} catch (Exception e) {

					}

					Log.e("uri", uri.toString());
					Log.e("file", file.toString());

					imagePath = file.toString();
				}

				time = MyNote.strClickDate;

				db.execSQL("INSERT INTO notebook "
						+ "(title,time,content,imagePath,realImagePath) VALUES "
						+ "('" + title + "'," + time + ",'" + content + "','"
						+ imagePath + "'" + ",'" + realImagePath + "'" + ")");

				Intent intent = new Intent("tw.com.irons.try_case2");
				sendBroadcast(intent);
				editText.setText("");
			}
		});

		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		button3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog alert = null;
				LayoutInflater dialog = LayoutInflater.from(CreateDaily.this);
				View dview = dialog.inflate(R.layout.symbol_insert, null);

				final TextView textView = (TextView) dview
						.findViewById(R.id.textView1);
				final TextView textView2 = (TextView) dview
						.findViewById(R.id.textView2);
				final TextView textView3 = (TextView) dview
						.findViewById(R.id.textView3);
				final TextView textView4 = (TextView) dview
						.findViewById(R.id.textView4);
				TextView textView5 = (TextView) dview
						.findViewById(R.id.textView5);
				TextView textView6 = (TextView) dview
						.findViewById(R.id.textView6);
				TextView textView7 = (TextView) dview
						.findViewById(R.id.textView7);
				TextView textView8 = (TextView) dview
						.findViewById(R.id.textView8);
				TextView textView9 = (TextView) dview
						.findViewById(R.id.textView9);
				TextView textView10 = (TextView) dview
						.findViewById(R.id.textView10);
				TextView textView11 = (TextView) dview
						.findViewById(R.id.textView11);
				TextView textView12 = (TextView) dview
						.findViewById(R.id.textView12);
				TextView textView13 = (TextView) dview
						.findViewById(R.id.textView13);
				TextView textView14 = (TextView) dview
						.findViewById(R.id.textView14);
				TextView textView15 = (TextView) dview
						.findViewById(R.id.textView15);
				TextView textView16 = (TextView) dview
						.findViewById(R.id.textView16);
				TextView textView17 = (TextView) dview
						.findViewById(R.id.textView17);
				TextView textView18 = (TextView) dview
						.findViewById(R.id.textView18);
				TextView textView19 = (TextView) dview
						.findViewById(R.id.textView19);
				TextView textView20 = (TextView) dview
						.findViewById(R.id.textView20);

				final TextView[] textViews = new TextView[20];

				// 簡單的方法暫時想不出來
				/*
				 * for(int i=0;i<20;i++){
				 * 
				 * textViews[i].setOnClickListener(new OnClickListener() {
				 * 
				 * @Override public void onClick(View v) { // TODO
				 * Auto-generated method stub v.getTag(); String str =
				 * textViews[j].getText().toString(); //cont.setText(str); } });
				 * }
				 */

				textView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String str = textView.getText().toString();
						editText2.setText(str);
					}
				});

				textView2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String str = textView2.getText().toString();
						editText2.setText(str);
					}
				});

				textView3.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String str = textView3.getText().toString();
						editText2.setText(str);
					}
				});

				textView4.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String str = textView4.getText().toString();
						editText2.setText(str);
					}
				});

				AlertDialog.Builder builder = new AlertDialog.Builder(
						CreateDaily.this);
				builder.setTitle("請選擇欲插入的表情符號");
				builder.setView(dview);
				builder.setCancelable(true);

				builder.setPositiveButton("確定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								// editText2.setText("")
							}

						});

				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});

				alert = builder.create();
				alert.show();
			}
		});
	}

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
				myImageView01.setImageBitmap(bitmap);
				myImageView01.setAdjustViewBounds(true);
				myImageView01.setMaxWidth(100);
				myImageView01.setMaxHeight(100);
				// myImageView01.setBackground(null);

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
	public void finish() {
		super.finish();
		db.close();
		dbHelper.close();
	}
}
