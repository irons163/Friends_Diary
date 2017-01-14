package com.way.chat.common.tran.bean;

/**
 * 傳輸對象類型
 * 
 * @author way
 * 
 */
public enum TranObjectType {
	REGISTER, // 註冊
	LOGIN, // 用戶登錄
	LOGOUT, // 用戶退出登錄
	FRIENDLOGIN, // 好友上線
	FRIENDLOGOUT, // 好友下線
	MESSAGE, // 使用者發送消息
	UNCONNECTED, // 無法連接
	FILE, // 傳輸文件
	REFRESH, // 刷新
	FRIEND,
	ADDFRIEND,
	DELETEFRIEND,
	RECADDFRIEND,
	RECDELETEFRIEND,
}

