package tw.com.irons.try_case2.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
	private final String createTableSQL = 
		"CREATE TABLE IF NOT EXISTS notebook " +
		"(_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		"title TEXT, " + 
		"time TEXT, " + 
		"content TEXT, " + 
		"del TEXT, " + 
		"imagePath TEXT, " +
		"realImagePath TEXT)";
	
	private final String createScheduleTableSQL = 
			"CREATE TABLE IF NOT EXISTS schedule " +
			"(_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
			"title TEXT, " + 
			"priority INTEGER, " + 
			"content TEXT, " + 
			"del TEXT, " + 
			"imagePath TEXT)";
	
	public MyDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createTableSQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS notebook");
		onCreate(db);
	}

	public Cursor onClickDate(SQLiteDatabase db,String time){
		String[] args=new String[1];
		args[0] = time;
		String sql="select * from notebook where time= ?";
		Cursor cursor = db.rawQuery(sql, args);
		return cursor;
	}
	
	public Cursor onSearchDate(SQLiteDatabase db,String time,String time2){
		String[] args=new String[2];
		args[0] = time;
		args[1] = time2;
		String sql="select * from notebook where time between ? and ? order by time desc";
		Cursor cursor = db.rawQuery(sql, args);
		return cursor;
	}
}
