package tw.com.irons.try_case2;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import tw.com.irons.try_case2.utils.DateUtil;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;

public class MC extends Activity {
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

	private TextView TextView02;
	private TextView TextView04;
	private TextView TextView05;
	private TextView TextView08;
	private Button Button01;
	private DatePicker DatePicker01;
	private EditText EditText01;
	private int mYear;
	private int mMonth;
	private int mDay;

	static Calendar calendar1 = Calendar.getInstance();
	static Calendar calendar2 = Calendar.getInstance();
	static Calendar calendar3 = Calendar.getInstance();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mc_msg);

		SharedPreferences preferences = getSharedPreferences("clickDate", 0);
		int bg = preferences.getInt("background", 0);
		if (bg == 0) {
			View layout = (View) findViewById(R.id.layout1);
			layout.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.dot_blue));
		}
		if (bg != 0) {
			View layout = (View) findViewById(R.id.layout1);
			layout.setBackgroundDrawable(getResources().getDrawable(bg));
		}

		TextView02 = (TextView) this.findViewById(R.id.TextView02);
		TextView04 = (TextView) this.findViewById(R.id.TextView04);
		TextView05 = (TextView) this.findViewById(R.id.TextView05);
		TextView08 = (TextView) this.findViewById(R.id.TextView08);
		DatePicker01 = (DatePicker) findViewById(R.id.DatePicker01);
		EditText01 = (EditText) this.findViewById(R.id.EditText01);
		Button01 = (Button) this.findViewById(R.id.Button01);

		/* MC週期 */
		EditText01.setText(period_value);

		/* 檢查檔案mc.ini是否存在 */
		checkFile();
		/* 算有關日期 */
		calDate();

		/* 上次MC第一天的日期設定於DatePicker */
		Calendar calendar = Calendar.getInstance();
		if (mcdate_value != null) {
			mYear = Integer.parseInt(mcdate_value.substring(0, 4));
			mMonth = Integer.parseInt(mcdate_value.substring(4, 6)) - 1;
			mDay = Integer.parseInt(mcdate_value.substring(6, 8));
		} else {
			mYear = calendar.get(Calendar.YEAR);
			mMonth = calendar.get(Calendar.MONTH);
			mDay = calendar.get(Calendar.DAY_OF_MONTH);
		}
		DatePicker01.init(mYear, mMonth, mDay, null);

		/* 儲存設定 */
		Button01.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				FileOutputStream fos;
				try {
					/* 取得DatePicker的日期 */
					int m = DatePicker01.getMonth() + 1;
					String strM = m >= 10 ? "" + m : "0" + m;
					int d = DatePicker01.getDayOfMonth();
					String strD = d >= 10 ? "" + d : "0" + d;
					mcdate_value = "" + DatePicker01.getYear() + "" + strM + ""
							+ strD;

					/* 取得EditText的值 */
					period_value = EditText01.getText().toString();

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

				finish();
			}
		});

	}

	/* 檢查檔案mc.ini是否存在 */
	private void checkFile() {
		boolean isExit = true;

		FileOutputStream fos = null;
		try {
			openFileInput(fileName);
		} catch (FileNotFoundException e) {
			isExit = false;
		}
		if (!isExit) {
			try {
				fos = openFileOutput(fileName, MODE_WORLD_WRITEABLE);
				BufferedOutputStream bos = new BufferedOutputStream(fos);

				/* 系統日期為上次MC第一天的日期 */
				mcdate_value = DateUtil.getDateTime("yyyyMMdd",
						System.currentTimeMillis());
				String txt = mcdate_key + "=" + mcdate_value;
				bos.write(txt.getBytes());
				/* 週期為28天 */
				bos.write(new String("\n").getBytes());
				txt = period_key + "=" + period_value;
				bos.write(txt.getBytes());
				/* 提醒時間為中午12點 */
				bos.write(new String("\n").getBytes());
				txt = remind_key + "=" + remind_value;
				bos.write(txt.getBytes());

				bos.close();
				fos.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
	}

	private void calDate() {

		String format = "yyyy.MM.dd";

		/* 上次MC第一天的日期 */
		/*
		 * TextView02.setText(DateUtil .getNextDate(mcdate_value, 0, format));
		 */
		DateUtil.getNextDate(mcdate_value, 0, format);
		calendar1 = DateUtil.calendar;

		/* 預估下次MC日期 */
		TextView04.setText(DateUtil.getNextDate(mcdate_value,
				Integer.parseInt(period_value), format));

		calendar2 = DateUtil.calendar;
		int mcDay = MC.calendar2.get(Calendar.DAY_OF_MONTH);
		int mcMonth = MC.calendar2.get(Calendar.MONTH) + 1;
		int mcYear = MC.calendar2.get(Calendar.YEAR);

		SharedPreferences preferences = getSharedPreferences("clickDate", 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("mcDay", mcDay);
		editor.putInt("mcMonth", mcMonth);
		editor.putInt("mcYear", mcYear);
		editor.commit();

		/* 距離現在還有N天 */
		String nDate = DateUtil.getNextDate(mcdate_value,
				Integer.parseInt(period_value), "yyyyMMdd");
		int days = DateUtil.computerDiffDate(DateUtil.getDateTime(nDate),
				System.currentTimeMillis());
		String text = "";
		if (days >= 0) {
			text += getResources().getString(R.string.strMessage5);
			text += days;
			text += getResources().getString(R.string.strMessage7);
		} else {
			text += getResources().getString(R.string.strMessage8);
			text += Math.abs(days);
			text += getResources().getString(R.string.strMessage7);
		}
		TextView05.setText(text);

		/* 排卵日，公式=即從下次月經來潮的第一天起，倒數14天就是排卵期 */
		TextView08.setText(DateUtil.getNextDate(nDate, -14, format));
		calendar3 = DateUtil.calendar;

	}
}