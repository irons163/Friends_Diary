package tw.com.irons.try_case2;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Notice extends Activity {
	Button button, button2, button3;
	CheckBox checkBox;

	boolean mcIsCheck = false;
	myResetReceiver receiver;
	AlarmManager am;
	int times;
	PendingIntent sender;
	long mcNoticTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notice);

		button = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		checkBox = (CheckBox) findViewById(R.id.checkBox1);

		SharedPreferences preferences = getSharedPreferences("clickDate", 0);
		mcIsCheck = preferences.getBoolean("mcIsCheck", false);

		checkBox.setChecked(mcIsCheck);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(Notice.this, NoticeSet.class);
				startActivity(intent);

			}
		});

		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				mcIsCheck = isChecked;
				if (isChecked) {
					/* 1天的毫秒 */
					SharedPreferences preferences = getSharedPreferences(
							"clickDate", 0);
					mcNoticTime = preferences.getInt("mcNoticTime", 0);
					if (mcNoticTime != 0) {
						times = 24 * 60 * 60 * 1000;

						am.setRepeating(AlarmManager.RTC_WAKEUP, mcNoticTime,
								times, sender);

						IntentFilter filter = new IntentFilter(
								"tw.com.irons.try_case2.mcNoticTime");
						receiver = new myResetReceiver();
						registerReceiver(receiver, filter);
					}

				} else {

					/* 先將AlarmManager停止 */
					am.cancel(sender);
				}

				SharedPreferences preferences = getSharedPreferences(
						"clickDate", 0);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putBoolean("mcIsCheck", mcIsCheck);
				editor.commit();

			}
		});
		Intent receiverIntent = new Intent(Notice.this, AlarmReceiver.class);
		sender = PendingIntent.getBroadcast(Notice.this, 1, receiverIntent, 0);

		/* 取得AlarmManager服務 */

		am = (AlarmManager) getSystemService(ALARM_SERVICE);

	}

	private class myResetReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			SharedPreferences preferences = getSharedPreferences("clickDate", 0);
			mcNoticTime = preferences.getInt("mcNoticTime", 0);

			am.setRepeating(AlarmManager.RTC_WAKEUP, mcNoticTime, times, sender);
		}
	}
}
