package com.way.chat.common.tran.bean;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 传输的对象,直接通过Socket传输的最大对象
 * 
 * @author way
 */
public class TranObject<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TranObjectType type;// 发送的消息类型

	private int fromUser;// 来自哪个用户
	private int toUser;// 发往哪个用户

	private T object;// 传输的对象
	
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
