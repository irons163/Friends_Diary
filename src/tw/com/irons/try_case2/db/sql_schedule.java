package tw.com.irons.try_case2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class sql_schedule extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "irons.db"; // 資料庫名稱
	private static final int DATABASE_VERSION = 1; // 資料庫版本
	public SQLiteDatabase db = this.getReadableDatabase();

	public sql_schedule(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// 增新 修改 刪除
	public Cursor getAll() {
		return db.rawQuery("SELECT * FROM Schedule_table", null); // 取得所有記錄
	}

	public long insert(String className, long id) {
		ContentValues cv = new ContentValues(); // 建立ContentValues物件
		cv.put("_id", id);
		cv.put("className", className);

		return db.insert("Schedule_table", null, cv);
	}

	public long update(String className, long id) {
		ContentValues cv = new ContentValues();
		cv.put("className", className);
		return db.update("Schedule_table", cv, "_id=" + id, null);
	}

	public int delete(long id) {
		return db.delete("Schedule_table", "_id=" + id, null); // null是WHERE的參數
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String DATABASE_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Schedule_table(_id INTEGER primary key, className TEXT)";
		// 建立Cuestom_table資烙表
		db.execSQL(DATABASE_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS Schedule_table"); // 刪除舊有的資料表
		onCreate(db);
	}

	public Cursor onScheduleSearchDate(String time, String time2) {
		String[] args = new String[2];
		args[0] = time;
		args[1] = time2;
		String sql = "select * from notebook where time between ? and ? order by time desc";
		Cursor cursor = db.rawQuery(sql, args);
		return cursor;
	}

	public Cursor onSearchDate(SQLiteDatabase db, String time, String time2) {
		String[] args = new String[2];
		args[0] = time;
		args[1] = time2;
		String sql = "select * from notebook where time between ? and ? order by time desc";
		Cursor cursor = db.rawQuery(sql, args);
		return cursor;
	}
}
