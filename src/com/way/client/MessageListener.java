package com.way.client;

import com.way.chat.common.tran.bean.TranObject;

/**
 * 消息監聽介面
 * 
 * @author way
 * 
 */
public interface MessageListener {
	public void Message(TranObject msg);
}

