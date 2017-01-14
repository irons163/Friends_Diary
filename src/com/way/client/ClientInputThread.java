package com.way.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import android.util.Log;

import com.way.chat.common.tran.bean.TranObject;

/**
 * 用戶端讀消息執行緒
 * 
 * @author way
 * 
 */
public class ClientInputThread extends Thread {
	private Socket socket;
	private TranObject msg;
	private boolean isStart = true;
	private ObjectInputStream ois;
	private MessageListener messageListener;// 消息監聽介面物件

	public ClientInputThread(Socket socket) {
		this.socket = socket;
		try {
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 提供給外部的消息監聽方法
	 * 
	 * @param messageListener
	 *            消息監聽介面物件
	 */
	public void setMessageListener(MessageListener messageListener) {
		this.messageListener = messageListener;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	@Override
	public void run() {
		try {
			while (isStart) {
				msg = (TranObject) ois.readObject();
				// 每收到一條消息，就調用介面的方法，並傳入該消息物件，外部在實現介面的方法時，就可以及時處理傳入的消息物件了
				// 我不知道我有說明白沒有？
				Log.e("c", msg.toString());
				messageListener.Message(msg);
			}
			ois.close();
			if (socket != null)
				socket.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
