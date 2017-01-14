package tw.com.irons.try_case2;

import tw.com.irons.try_case2.utils.ImageAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;

public class BackgroundChange extends Activity {
	Button button, button2;
	public Resources res;
	myResetReceiver receiver;
	int bgColor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.set_bg_main);

		SharedPreferences preferences = getSharedPreferences("clickDate", 0);
		int bg = preferences.getInt("background", 0);
		if (bg != 0) {
			View layout = (View) findViewById(R.id.layout1);
			layout.setBackgroundDrawable(getResources().getDrawable(bg));
		}

		button = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setContentView(R.layout.set_bg_menu);

				final Integer[] mImageIds = { R.drawable.onef1,
						R.drawable.onef2, R.drawable.one2finish };

				// res = getResources();

				GridView gridView01 = (GridView) findViewById(R.id.GridView01);
				gridView01.setAdapter(new ImageAdapter(BackgroundChange.this,
						85, 85, mImageIds, res)); // �]�w image ���f
				gridView01.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							final int position, long id) {
						// TODO Auto-generated method stub
						final PopupWindow popWin = new PopupWindow(
								BackgroundChange.this);
						// �w�q PopupWindow ���t�� ImageView
						ImageView iv = new ImageView(BackgroundChange.this);
						// �b ImageView ���]�w�ϼ�
						iv.setImageDrawable(getResources().getDrawable(
								mImageIds[position]));

						AlertDialog alert = null;

						AlertDialog.Builder builder = new AlertDialog.Builder(
								BackgroundChange.this);
						builder.setTitle("�D���I���˦�");
						builder.setView(iv);
						builder.setCancelable(true);

						builder.setPositiveButton("�T�w",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										SharedPreferences preferences = getSharedPreferences(
												"clickDate", 0);
										SharedPreferences.Editor editor = preferences
												.edit();
										editor.putInt("backgroundMain",
												mImageIds[position]);
										editor.commit();

										// bgColor = mImageIds[position];

									}
								});

						builder.setNegativeButton("���",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								});

						alert = builder.create();
						alert.show();

						View layout = (View) findViewById(R.id.layout1);
						layout.setBackgroundDrawable(getResources()
								.getDrawable(mImageIds[position]));
					}
				});

			}
		});

		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setContentView(R.layout.set_bg_menu);

				final Integer[] mImageIds = { R.drawable.dot_blue,
						R.drawable.dot_orange, R.drawable.dot_pb,
						R.drawable.dot_pink, R.drawable.dot_purple,
						R.drawable.dot_yellow, R.drawable.line_blue,
						R.drawable.line_green, R.drawable.line_pink,
						R.drawable.line_purple };

				res = getResources();

				View layout = (View) findViewById(R.id.layout2);
				layout.setBackgroundColor(Color.BLACK);
				GridView gridView01 = (GridView) findViewById(R.id.GridView01);
				gridView01.setAdapter(new ImageAdapter(BackgroundChange.this,
						120, 120, mImageIds, res)); // �]�w image ���f
				gridView01.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							final int position, long id) {
						// TODO Auto-generated method stub
						final PopupWindow popWin = new PopupWindow(
								BackgroundChange.this);

						ImageView iv = new ImageView(BackgroundChange.this);
						// �b ImageView ���]�w�ϼ�
						iv.setImageDrawable(getResources().getDrawable(
								mImageIds[position]));

						AlertDialog alert = null;

						AlertDialog.Builder builder = new AlertDialog.Builder(
								BackgroundChange.this);
						builder.setTitle("�I���˦�");
						builder.setView(iv);
						builder.setCancelable(true);

						builder.setPositiveButton("�T�w",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										SharedPreferences preferences = getSharedPreferences(
												"clickDate", 0);
										SharedPreferences.Editor editor = preferences
												.edit();
										editor.putInt("background",
												mImageIds[position]);
										editor.commit();

										bgColor = mImageIds[position];

										Intent intent = new Intent(
												"tw.com.irons.try_case2.BG");
										sendBroadcast(intent);
										setContentView(R.layout.set_bg_main);
									}
								});

						builder.setNegativeButton("���",
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
		});

		IntentFilter filter = new IntentFilter("tw.com.irons.try_case2.BG");
		receiver = new myResetReceiver();
		registerReceiver(receiver, filter);

	}

	private class myResetReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			View layout = (View) findViewById(R.id.layout1);
			layout.setBackgroundDrawable(getResources().getDrawable(bgColor));
		}
	}
}
