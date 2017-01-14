package tw.com.irons.try_case2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import tw.com.irons.try_case2.R;
import tw.com.irons.try_case2.utils.DateUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
	private String mcdate_value = "";
	private String period_value = "";
	private boolean isExit = true;

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		/* 先取得檔案裡的設定值 */
		getFileDate(arg0);
		/* 有取得設定值才可執行 */
		if (isExit) {
			/* 預估下次MC日期 */
			String nDate = DateUtil.getNextDate(mcdate_value,
					Integer.parseInt(period_value), "yyyy/MM/dd");
			/* 距離現在還有N天 */
			int days = DateUtil.computerDiffDate(DateUtil.getDateTime(DateUtil
					.getNextDate(mcdate_value, Integer.parseInt(period_value),
							"yyyyMMdd")), System.currentTimeMillis());
			String msg = arg0.getResources().getString(R.string.strMessage2);
			msg += nDate;
			msg += "\n";
			if (days >= 0) {
				msg += arg0.getResources().getString(R.string.strMessage5);
				msg += days;
				msg += arg0.getResources().getString(R.string.strMessage7);
			} else {
				msg += arg0.getResources().getString(R.string.strMessage8);
				/* 取絕對值 */
				msg += Math.abs(days);
				msg += arg0.getResources().getString(R.string.strMessage7);
			}

			showNotification(arg0, msg);
		}
	}

	private void getFileDate(Context arg0) {
		String msg = "";
		Properties p = new Properties();
		try {
			p.load(arg0.openFileInput(MC.fileName));
			mcdate_value = p.getProperty(MC.mcdate_key);
			period_value = p.getProperty(MC.period_key);
		} catch (FileNotFoundException e) {
			isExit = false;
			msg = e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			isExit = false;
			msg = e.getMessage();
			e.printStackTrace();
		}
		if (msg.length() > 0) {
			showNotification(arg0, msg);
		}
	}

	/*
	 * private void showToast(Context arg0, String msg) { Toast.makeText(arg0,
	 * msg, Toast.LENGTH_LONG).show(); }
	 */

	private void showNotification(Context arg0, String msg) {
		/*
		 * 新增Notification 傳入顯示的icon在狀態烈顯示的訊息以及顯示的時間
		 */
		Notification notification = new Notification(R.drawable.icon,
				"mc date remind", System.currentTimeMillis());
		/* 點選訊息開啟的畫面 */
		PendingIntent contentIntent = PendingIntent.getActivity(arg0, 0,
				new Intent(arg0, MC.class), 0);
		/* 傳入顯示的訊息 */
		notification.setLatestEventInfo(arg0, "you next mc date!!", msg,
				contentIntent);
		/* 取得 NotificationManager */
		NotificationManager mNM = (NotificationManager) arg0
				.getSystemService(Context.NOTIFICATION_SERVICE);
		/* 先刪除之前的Notification */
		mNM.cancel(0);
		/* notify新的Notification */
		mNM.notify(0, notification);
	}
}
