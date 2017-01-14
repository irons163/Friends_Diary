package com.way.chat.common.tran.bean;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 傳輸的物件,直接通過Socket傳輸的最大物件
 * 
 * @author way
 */
public class TranObject<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TranObjectType type;// 發送的消息類型

	private int fromUser;// 來自哪個用戶
	private int toUser;// 發往哪個用戶

	private T object;// 傳輸的物件
	
	byte[] image;
	
	private String dailyTime;

	public TranObject(TranObjectType type) {
		this.type = type;
	}

	public int getFromUser() {
		return fromUser;
	}

	public void setFromUser(int fromUser) {
		this.fromUser = fromUser;
	}

	public int getToUser() {
		return toUser;
	}

	public void setToUser(int toUser) {
		this.toUser = toUser;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public TranObjectType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "TranObject [type=" + type + ", fromUser=" + fromUser
				+ ", toUser=" + toUser + ", object=" + object + ", image=" + image + ", dailyTime=" + dailyTime +"]";
	}
	
	public byte[] getImage() {
		return image;
	}

	public void setImage(String imgPath) {
        FileInputStream fis;
		Bitmap defaultIcon = BitmapFactory.decodeFile(imgPath);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		defaultIcon.compress(Bitmap.CompressFormat.JPEG, 100, stream);

		image = stream.toByteArray();
        
		//this.object = object;
	}
	
	public String getDailyTime() {
		return dailyTime;
	}

	public void setDailyTime(String dailyTime) {
		this.dailyTime = dailyTime;
	}
}
