package com.way.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 用戶端
 * 
 * @author way
 * 
 */
public class Client {

	private Socket client;
	private ClientThread clientThread;
	private String ip;
	private int port;

	boolean waitboolean;
	boolean clientboolean;
	
	public Client(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public boolean start() {
		try {
			client = new Socket();
			// client.connect(new InetSocketAddress(Constants.SERVER_IP,
			// Constants.SERVER_PORT), 3000);
			client.connect(new InetSocketAddress(ip, port), 3000);
			if (client.isConnected()) {
				// System.out.println("Connected..");
				clientThread = new ClientThread(client);
				clientThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
		//new Thread(downloadRun).start();
		
		/*
		int i=0;
		while(waitboolean){
		
		}
		waitboolean = true;
		return clientboolean;*/
	}
/*
	  Runnable downloadRun = new Runnable(){  
		  
		  @Override  
		  public void run() {  
		      // TODO Auto-generated method stub  
				try {
					
					
					client = new Socket();
					// client.connect(new InetSocketAddress(Constants.SERVER_IP,
					// Constants.SERVER_PORT), 3000);
					client.connect(new InetSocketAddress(ip, port), 3000);
					if (client.isConnected()) {
						// System.out.println("Connected..");
						clientThread = new ClientThread(client);
						clientThread.start();
					}
				} catch (IOException e) {
					e.printStackTrace();
					clientboolean = false;
					waitboolean = false;
					
				}
				clientboolean = true;
				waitboolean = false;
		  }  
		    };  
		*/    

	
	
	// 直接通過client得到讀執行緒
	public ClientInputThread getClientInputThread() {
		return clientThread.getIn();
	}

	// 直接通過client得到寫執行緒
	public ClientOutputThread getClientOutputThread() {
		return clientThread.getOut();
	}

	// 直接通過client停止讀寫消息
	public void setIsStart(boolean isStart) {
		clientThread.getIn().setStart(isStart);
		clientThread.getOut().setStart(isStart);
	}
	
	public class ClientThread extends Thread {

		private ClientInputThread in;
		private ClientOutputThread out;

		public ClientThread(Socket socket) {
			in = new ClientInputThread(socket);
			out = new ClientOutputThread(socket);
		}

		public void run() {
			in.setStart(true);
			out.setStart(true);
			in.start();
			out.start();
		}

		// 得到讀消息執行緒
		public ClientInputThread getIn() {
			return in;
		}

		// 得到寫消息執行緒
		public ClientOutputThread getOut() {
			return out;
		}
	}
}
