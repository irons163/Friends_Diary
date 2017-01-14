package tw.com.irons.try_case2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

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
		/* ���o�ɮ׸̪��]�w�� */
		getFileDate(arg0);
		/* ����o�]�w�Ȥ~�i���� */
		if (isExit) {
			/* �w���U��MC��� */
			String nDate = DateUtil.getNextDate(mcdate_value,
					Integer.parseInt(period_value), "yyyy/MM/dd");
			/* �Z���{�b�٦�N�� */
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
				/* ���� */
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
		 * �s�WNotification �ǤJ��ܪ�icon�b���A�P��ܪ��T���H����ܪ��ɶ�
		 */
		Notification notification = new Notification(R.drawable.icon,
				"mc date remind", System.currentTimeMillis());
		/* �I��T���}�Ҫ��e�� */
		PendingIntent contentIntent = PendingIntent.getActivity(arg0, 0,
				new Intent(arg0, MC.class), 0);
		/* �ǤJ��ܪ��T�� */
		notification.setLatestEventInfo(arg0, "you next mc date!!", msg,
				contentIntent);
		/* ��o NotificationManager */
		NotificationManager mNM = (NotificationManager) arg0
				.getSystemService(Context.NOTIFICATION_SERVICE);
		/* ��R�����e��Notification */
		mNM.cancel(0);
		/* notify�s��Notification */
		mNM.notify(0, notification);
	}
}
