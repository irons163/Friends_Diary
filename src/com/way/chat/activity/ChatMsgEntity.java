package com.way.chat.activity;

/**
 * 一個聊天消息的JavaBean
 * 
 * @author way
 * 
 */
public class ChatMsgEntity {
	private String name;// 消息來自
	private String date;// 消息日期
	private String message;// 消息內容
	private int img;
	private boolean isComMeg = true;// 是否為收到的消息
	private String dailyImg;
	private String dailyTime;
	
	public ChatMsgEntity() {

	}

	public ChatMsgEntity(String name, String date, String text, int img,
			boolean isComMsg, String dailyImg) {
		super();
		this.name = name;
		this.date = date;
		this.message = text;
		this.img = img;
		this.isComMeg = isComMsg;
		this.dailyImg = dailyImg;
		this.dailyTime = dailyTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean getMsgType() {
		return isComMeg;
	}

	public void setMsgType(boolean isComMsg) {
		isComMeg = isComMsg;
	}

	public int getImg() {
		return img;
	}

	public void setImg(int img) {
		this.img = img;
	}
	
	public String getDailyImg() {
		return dailyImg;
	}

	public void setDailyImg(String dailyImg) {
		this.dailyImg = dailyImg;
	}
	
	public String getDailyTime() {
		return dailyImg;
	}

	public void setDailyTime(String dailyTime) {
		this.dailyTime = dailyTime;
	}
	
}

