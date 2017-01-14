package com.way.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encode {
	/**
	 * 靜態加密方法
	 * 
	 * @param codeType
	 *            傳入加密方式
	 * @param content
	 *            傳入加密的內容
	 * @return 返回加密結果
	 */
	public static String getEncode(String codeType, String content) {
		try {
			MessageDigest digest = MessageDigest.getInstance(codeType);// 獲取一個實例，並傳入加密方式
			digest.reset();// 清空一下
			digest.update(content.getBytes());// 寫入內容,可以指定編碼方式content.getBytes("utf-8");
			StringBuilder builder = new StringBuilder();
			for (byte b : digest.digest()) {
				builder.append(Integer.toHexString((b >> 4) & 0xf));
				builder.append(Integer.toHexString(b & 0xf));
			}
			return builder.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
//
//	public static void main(String[] args) {
//		String str = Encode.getEncode("MD5", "hello world!");// 用MD5方式加密
//		System.out.println(str);
//		// fc3ff98e8c6a0d3087d515c0473f8677
//		String str1 = Encode.getEncode("SHA", "hello world!");// 用SHA方式加密
//		System.out.println(str1);
//		// 430ce34d020724ed75a196dfc2ad67c77772d169
//	}
}
