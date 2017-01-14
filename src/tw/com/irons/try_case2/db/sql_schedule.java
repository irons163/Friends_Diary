package tw.com.irons.try_case2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class sql_schedule extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "irons.db"; // ��Ʈw�W��
	private static final int DATABASE_VERSION = 1; // ��Ʈw����
	public SQLiteDatabase db = this.getReadableDatabase();

	public sql_schedule(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// �W�s �ק� �R��
	
	public Cursor getAll() {
		return db.rawQuery("SELECT * FROM Schedule_table", null); // ��o�Ҧ��O��
	}

	public long insert(String className, long id) {
		ContentValues cv = new ContentValues(); // �إ�ContentValues����
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
		return db.delete("Schedule_table", "_id=" + id, null); // null�OWHERE���Ѽ�
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String DATABASE_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS Schedule_table(_id INTEGER primary key, className TEXT)";
		// �إ�Cuestom_table��O��
		db.execSQL(DATABASE_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS Schedule_table"); // �R���¦�����ƪ�
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
