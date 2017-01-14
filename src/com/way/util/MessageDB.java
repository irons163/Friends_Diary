package com.way.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.way.chat.activity.ChatMsgEntity;
import com.way.chat.common.util.Constants;

public class MessageDB {
	private SQLiteDatabase db;

	public MessageDB(Context context) {
		db = context.openOrCreateDatabase(Constants.DBNAME,
				Context.MODE_PRIVATE, null);
	}

	public void saveMsg(int id, ChatMsgEntity entity) {
		db.execSQL("CREATE table IF NOT EXISTS _"
				+ id
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, img TEXT,date TEXT,isCome TEXT,message TEXT, imagePath TEXT)");
		int isCome = 0;
		if (entity.getMsgType()) {//如果是收到的消息，保存在資料庫的值為1
			isCome = 1;
		}
		db.execSQL(
				"insert into _" + id
						+ " (name,img,date,isCome,message,imagePath) values(?,?,?,?,?,?)",
				new Object[] { entity.getName(), entity.getImg(),
						entity.getDate(), isCome, entity.getMessage(), entity.getDailyImg() });
	}

	public List<ChatMsgEntity> getMsg(int id) {
		List<ChatMsgEntity> list = new ArrayList<ChatMsgEntity>();
		db.execSQL("CREATE table IF NOT EXISTS _"
				+ id
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, img TEXT,date TEXT,isCome TEXT,message TEXT, imagePath TEXT)");
		Cursor c = db.rawQuery("SELECT * from _" + id + " ORDER BY _id DESC ", null);
		while (c.moveToNext()) {
			String name = c.getString(c.getColumnIndex("name"));
			int img = c.getInt(c.getColumnIndex("img"));
			String date = c.getString(c.getColumnIndex("date"));
			int isCome = c.getInt(c.getColumnIndex("isCome"));
			String message = c.getString(c.getColumnIndex("message"));
			boolean isComMsg = false;
			String imagePath = c.getString(c.getColumnIndex("imagePath"));
			if (isCome == 1) {
				isComMsg = true;
			}
			ChatMsgEntity entity = new ChatMsgEntity(name, date, message, img,
					isComMsg, imagePath);
			list.add(entity);
		}
		c.close();
		return list;
	}

	public void close() {
		if (db != null)
			db.close();
	}
}
