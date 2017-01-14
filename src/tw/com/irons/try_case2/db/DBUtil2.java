package tw.com.irons.try_case2.db;

import java.util.ArrayList;

import tw.com.irons.try_case2.MySchedule;
import tw.com.irons.try_case2.Schedule;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
import static tw.com.irons.try_case2.Constant.*;
import static tw.com.irons.try_case2.MySchedule.*;

public class DBUtil2 {
	static SQLiteDatabase sld;

	// ============================所有處理類型資料庫的方法start==============================
	public static void loadType(MySchedule father)// 從類型資料庫中讀取資料
	{
		try {
			sld = SQLiteDatabase.openDatabase(
					"/data/data/tw.com.irons.try_case2/myDb", null,
					SQLiteDatabase.OPEN_READWRITE
							| SQLiteDatabase.CREATE_IF_NECESSARY);
			String sql = "create table if not exists type(tno integer primary key,tname varchar2(20));";
			sld.execSQL(sql);
			Cursor cursor = sld.query("type", null, null, null, null, null,
					"tno");
			int count = cursor.getCount();
			if (count == 0)// 如果是第一次運行程式，自動創建3個預設類型
			{
				for (int i = 0; i < father.defultType.length; i++) {
					sql = "insert into type values(" + i + ",'"
							+ father.defultType[i] + "')";
					sld.execSQL(sql);
				}

				cursor = sld.query("type", null, null, null, null, null, "tno");
				count = cursor.getCount();
			}
			alType.clear();
			while (cursor.moveToNext()) {
				alType.add(cursor.getString(1));
			}
			sld.close();
			cursor.close();
		} catch (Exception e) {
			Toast.makeText(father, "類型資料庫打開創建錯誤：" + e.toString(),
					Toast.LENGTH_LONG).show();
		}
	}

	public static boolean insertType(MySchedule father, String newType)// 更新類型資料庫
	{
		Cursor cursor = null;
		boolean duplicate = false;// false代表沒有類型名稱重複，true代表有重複
		try {
			sld = SQLiteDatabase.openDatabase(
					"/data/data/tw.com.irons.try_case2/myDb", null,
					SQLiteDatabase.OPEN_READWRITE);
			cursor = sld.query("type", null, null, null, null, null, "tno");
			alType.clear();
			while (cursor.moveToNext())// 存入新行程時，與資料庫中已有的行程進行比較，如果重複，則標誌位元設為true
			{
				if (newType.equals(cursor.getString(1))) {
					duplicate = true;
				}
				alType.add(cursor.getString(1));
			}
			if (!duplicate) {
				alType.add(newType);
				String sql = "delete from type";
				sld.execSQL(sql);
				for (int i = 0; i < alType.size(); i++) {
					sql = "insert into type values(" + i + ",'" + alType.get(i)
							+ "')";
					sld.execSQL(sql);
				}
				Toast.makeText(father, "成功添加類型“" + newType + "”。",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(father, "類型名稱重複！", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(father, "類型資料庫更新錯誤：" + e.toString(),
					Toast.LENGTH_LONG).show();
			return false;
		} finally {
			cursor.close();
			sld.close();
		}
		return true;
	}

	public static void deleteType(MySchedule father, String s) {
		try {
			sld = SQLiteDatabase.openDatabase(
					"/data/data/tw.com.irons.try_case2/myDb", null,
					SQLiteDatabase.OPEN_READWRITE);
			String sql = "delete from type where tname='" + s + "'";
			sld.execSQL(sql);
			Toast.makeText(father, "成功刪除類型", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(father, "類型刪除錯誤：" + e.toString(), Toast.LENGTH_LONG)
					.show();
		} finally {
			sld.close();
		}
	}

	public static ArrayList<String> getAllType(MySchedule father)// 獲得所有行程的類型，包括軟體中使用者自建的，和軟體中已經被刪但是在資料庫中存儲的行程卻還在用的類型
	{
		ArrayList<String> type = new ArrayList<String>();
		type = alType;// 把軟體中存在的類型放入type
		try {
			sld = SQLiteDatabase.openDatabase(
					"/data/data/tw.com.irons.try_case2/myDb", null,
					SQLiteDatabase.OPEN_READONLY);
			String sql = "select distinct type from schedule;";// 因為存儲在資料庫中的行程的類型可能被用戶刪除，所以要搜索一遍
			Cursor cursor = sld.rawQuery(sql, new String[] {});
			while (cursor.moveToNext()) {
				if (!type.contains(cursor.getString(0))) {
					type.add(cursor.getString(0));
				}
			}
			sld.close();
			cursor.close();
		} catch (Exception e) {
			Toast.makeText(father, "獲取類型錯誤：" + e.toString(), Toast.LENGTH_LONG)
					.show();
			Log.d("exception!!", e.toString());
		}
		return type;
	}

	// ============================所有處理類型資料庫的方法end==============================

	// ============================所有處理行程資料庫的方法start==============================
	public static void loadSchedule(MySchedule father)// 從行程資料庫讀取行程資料
	{
		try {
			sld = SQLiteDatabase.openDatabase(
					"/data/data/tw.com.irons.try_case2/myDb", null,
					SQLiteDatabase.OPEN_READWRITE
							| SQLiteDatabase.CREATE_IF_NECESSARY);
			String sql = "create table if not exists schedule("
					+ "_id integer primary key," + "date1 char(10),"
					+ "time1 char(5)," + "date2 char(10)," + "time2 char(5),"
					+ "title varchar2(40)," + "note varchar2(120),"
					+ "type varchar2(20)," + "timeset boolean,"
					+ "alarmset boolean," + "littleImage INTEGER" + ")";
			sld.execSQL(sql);
			Cursor cursor = sld.query("schedule", null, null, null, null, null,
					"date1 desc,time1 desc");// 按datetime1倒排序

			while (cursor.moveToNext()) {
				int sn = cursor.getInt(0);
				String date1 = cursor.getString(1);
				String time1 = cursor.getString(2);
				String date2 = cursor.getString(3);
				String time2 = cursor.getString(4);
				String title = cursor.getString(5);
				String note = cursor.getString(6);
				String type = cursor.getString(7);
				String timeSet = cursor.getString(8);
				String alarmSet = cursor.getString(9);
				int littleImage = cursor.getInt(10);
				Schedule schTemp = new Schedule(sn, date1, time1, date2, time2,
						title, note, type, timeSet, alarmSet, littleImage);
				alSch.add(schTemp);
				Log.d("schdata", "" + cursor.getPosition() + ":sn=" + sn + ":"
						+ date1 + "," + time1 + "," + date2 + "," + timeSet);
			}
			sld.close();
			cursor.close();
		} catch (Exception e) {
			Toast.makeText(father, "行程資料庫打開創建錯誤：" + e.toString(),
					Toast.LENGTH_LONG).show();
			Log.d("exception", e.toString());
		}
	}

	public static void insertSchedule(MySchedule father)// 插入行程
	{
		try {
			sld = SQLiteDatabase.openDatabase(
					"/data/data/tw.com.irons.try_case2/myDb", null,
					SQLiteDatabase.OPEN_READWRITE);
			String sql = father.schTemp.toInsertSql(father);
			sld.execSQL(sql);

			sld.close();

		} catch (Exception e) {
			Toast.makeText(father, "行程資料庫更新錯誤：" + e.toString(),
					Toast.LENGTH_LONG).show();
			Log.d("exception!!", e.toString());
		}
	}

	public static void updateSchedule(MySchedule father)// 更新行程
	{
		try {
			sld = SQLiteDatabase.openDatabase(
					"/data/data/tw.com.irons.try_case2/myDb", null,
					SQLiteDatabase.OPEN_READWRITE);
			String sql = father.schTemp.toUpdateSql(father);
			sld.execSQL(sql);
			sld.close();
		} catch (Exception e) {
			Toast.makeText(father, "行程資料庫更新錯誤：" + e.toString(),
					Toast.LENGTH_LONG).show();
			Log.d("exception!!", e.toString());
		}
	}

	public static void deleteSchedule(MySchedule father)// 刪除行程
	{
		try {
			sld = SQLiteDatabase.openDatabase(
					"/data/data/tw.com.irons.try_case2/myDb", null,
					SQLiteDatabase.OPEN_READWRITE);
			int sn = father.schTemp.getSn();
			String sql = "delete from schedule where _id=" + sn;
			sld.execSQL(sql);
			sld.close();
			Toast.makeText(father, "刪除成功", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(father, "行程刪除錯誤：" + e.toString(), Toast.LENGTH_LONG)
					.show();
		}
	}

	public static void deletePassedSchedule(MySchedule father)// 刪除所有過期行程
	{
		try {
			sld = SQLiteDatabase.openDatabase(
					"/data/data/tw.com.irons.try_case2/myDb", null,
					SQLiteDatabase.OPEN_READWRITE);
			String nowDate = getNowDateString();
			String nowTime = getNowTimeString();
			String sql = "date1<'" + nowDate + "' or date1='" + nowDate
					+ "' and time1<'" + nowTime + "'";
			sql = "delete from schedule where date1<'" + nowDate
					+ "' or date1='" + nowDate + "' and time1<'" + nowTime
					+ "'";
			sld.execSQL(sql);
			sld.close();
			Toast.makeText(father, "成功刪除過期行程", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(father, "行程刪除錯誤：" + e.toString(), Toast.LENGTH_LONG)
					.show();
			Log.d("error", e.toString());
		}
	}

	public static Cursor searchSchedule(MySchedule father,
			ArrayList<String> allKindsType)// 查找行程
	{
		ArrayList<Boolean> alSelectedType = father.alSelectedType;
		Cursor cursor = null;
		try {
			sld = SQLiteDatabase.openDatabase(
					"/data/data/tw.com.irons.try_case2/myDb", null,
					SQLiteDatabase.OPEN_READONLY);
			String[] args = new String[2];
			args[0] = father.rangeFrom;
			args[1] = father.rangeTo;
			String sql = "select * from schedule where date1 between ? and ?";
			StringBuffer sbtmp = new StringBuffer();
			sbtmp.append(" and (type=");
			for (int i = 0; i < alSelectedType.size(); i++) {
				if (alSelectedType.get(i)) {
					sbtmp.append("'");
					sbtmp.append(allKindsType.get(i));
					sbtmp.append("' or type=");
				}
			}
			String strSelectedType = sbtmp.toString();
			strSelectedType = strSelectedType.substring(0,
					strSelectedType.length() - 9);// 最後去掉後面的" or type="
			sql += strSelectedType + ")";

			Log.d("search sql:", sql);

			cursor = sld.rawQuery(sql, args);
			Toast.makeText(father, "搜索到" + cursor.getCount() + "條行程",
					Toast.LENGTH_SHORT).show();

			alSch.clear();
			while (cursor.moveToNext()) {
				int sn = cursor.getInt(0);
				String date1 = cursor.getString(1);
				String time1 = cursor.getString(2);
				String date2 = cursor.getString(3);
				String time2 = cursor.getString(4);
				String title = cursor.getString(5);
				String note = cursor.getString(6);
				String type = cursor.getString(7);
				String timeSet = cursor.getString(8);
				String alarmSet = cursor.getString(9);
				int littleImage = cursor.getInt(10);
				Schedule schTemp = new Schedule(sn, date1, time1, date2, time2,
						title, note, type, timeSet, alarmSet, littleImage);
				alSch.add(schTemp);
			}
			sld.close();
			// cursor.close();

		} catch (Exception e) {
			Toast.makeText(father, e.toString(), Toast.LENGTH_SHORT).show();
		}
		return cursor;
	}

	// ============================所有處理行程資料庫的方法end==============================

	public static int getSNFromPrefs(MySchedule father)// 讀取preferences裡面的行程sn
	{
		SharedPreferences sp = father.getSharedPreferences("SN", MODE_PRIVATE);
		int sn = sp.getInt("sn", 0);
		Editor editor = sp.edit();
		editor.putInt("sn", sn + 1);
		editor.commit();
		return sn;
	}
}
