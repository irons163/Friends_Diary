package tw.com.irons.try_case2;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class NoticeSet extends Activity {
	/* 放置設定值的檔案 */
	public static String fileName = "mc.ini";
	/* 上次MC第一天的日期 */
	public static String mcdate_key = "mcdate";
	private String mcdate_value = "";
	/* MC週期 */
	public static String period_key = "period";
	private String period_value = "28";
	/* 每日提醒時間 */
	public static String remind_key = "remind";
	private String remind_value = "1200";

	TextView textView, textView2, textView3;
	private TimePicker TimePicker01;
	FileOutputStream fos;
	private Button Button02;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notice_set);

		TimePicker01 = (TimePicker) findViewById(R.id.TimePicker01);
		Button02 = (Button) this.findViewById(R.id.Button02);

		Button02.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/* 取得TimePicker的時間 */
				int h = TimePicker01.getCurrentHour();
				String strH = h >= 10 ? "" + h : "0" + h;
				int mu = TimePicker01.getCurrentMinute();
				String strMu = mu >= 10 ? "" + mu : "0" + mu;
				remind_value = strH + strMu;

				try {
					fos = openFileOutput(MC.fileName, MODE_WORLD_WRITEABLE);
					BufferedOutputStream bos = new BufferedOutputStream(fos);

					String txt = MC.mcdate_key + "=" + mcdate_value;
					bos.write(txt.getBytes());

					bos.write(new String("\n").getBytes());
					txt = MC.period_key + "=" + period_value;
					bos.write(txt.getBytes());

					bos.write(new String("\n").getBytes());
					txt = MC.remind_key + "=" + remind_value;
					bos.write(txt.getBytes());
					bos.close();
					fos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Intent receiverIntent = new Intent(NoticeSet.this,
						AlarmReceiver.class);
				PendingIntent sender = PendingIntent.getBroadcast(
						NoticeSet.this, 1, receiverIntent, 0);
				/* 取得AlarmManager服務 */
				AlarmManager am;
				am = (AlarmManager) getSystemService(ALARM_SERVICE);
				/* 先將AlarmManager停止 */
				am.cancel(sender);

				/* 1天的毫秒 */
				int times = 24 * 60 * 60 * 1000;

				/* 取得TimePicker的值當作驅動的時間 */
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY,
						TimePicker01.getCurrentHour());
				calendar.set(Calendar.MINUTE, TimePicker01.getCurrentMinute());
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);

				long triggerTime = calendar.getTimeInMillis();

				/* 每日同一時間執行一次 */
				am.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, times,
						sender);

				SharedPreferences preferences = getSharedPreferences(
						"clickDate", 0);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putLong("mcNoticTime", triggerTime);
				editor.commit();

				Intent intent = new Intent("tw.com.irons.try_case2.mcNoticTime");
				sendBroadcast(intent);

				finish();
			}
		});

		boolean isExit = true;

		FileOutputStream fos = null;
		try {
			openFileInput(fileName);
		} catch (FileNotFoundException e) {
			isExit = false;
		}
		if (!isExit) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("請先前往設定生理期日期").setPositiveButton("OK", null);
			builder.show();
			finish();
		}

		/* 將檔案mc.ini裡的值取出 */
		Properties p = new Properties();
		try {
			p.load(openFileInput(fileName));
			mcdate_value = p.getProperty(mcdate_key);
			period_value = p.getProperty(period_key);
			remind_value = p.getProperty(remind_key);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		textView = (TextView) this.findViewById(R.id.textView1);
		textView2 = (TextView) this.findViewById(R.id.textView2);
		textView3 = (TextView) this.findViewById(R.id.textView3);

		textView.setText(mcdate_value);
		textView2.setText(period_value);
		textView3.setText(remind_value);

		/* 每日提醒時間設定於TimePicker */
		if (remind_value != null && remind_value.length() == 4) {
			TimePicker01.setCurrentHour(Integer.parseInt(remind_value
					.substring(0, 2)));
			TimePicker01.setCurrentMinute(Integer.parseInt(remind_value
					.substring(2, 4)));
		}

	}
}
