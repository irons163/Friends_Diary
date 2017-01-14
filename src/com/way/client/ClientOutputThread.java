package com.way.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;

/**
 * 用戶端寫消息執行緒
 * 
 * @author way
 * 
 */
public class ClientOutputThread extends Thread {
	private Socket socket;
	private ObjectOutputStream oos;
	private boolean isStart = true;
	private TranObject msg;

	public ClientOutputThread(Socket socket) {
		this.socket = socket;
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	// 這裡處理跟伺服器是一樣的
	public void setMsg(TranObject msg) {
		this.msg = msg;
		synchronized (this) {
			notify();
		}
	}

	@Override
	public void run() {
		try {
			while (isStart) {
				if (msg != null) {
					oos.writeObject(msg);
					oos.flush();
					if (msg.getType() == TranObjectType.LOGOUT) {// 如果是發送下線的消息，就直接跳出迴圈
						break;
					}
					synchronized (this) {
						wait();// 發送完消息後，執行緒進入等候狀態
					}
				}
			}
			oos.close();// 迴圈結束後，關閉輸出流和socket
			if (socket != null)
				socket.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
