package com.le.safe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Uitl {
	/**
	 * md5加密
	 */
	public static String passwordToMD5(String passWord){
		StringBuilder sb = new StringBuilder();
		try {
			//1.获取数据摘要器
			//arg0 : 加密方式
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			//2.digest:将一个byte数组进行加密，返回加密过的byte数组，第一重加密，二进制哈希算法
			byte[] digest = messageDigest.digest(passWord.getBytes());
			//3.获取加密过来的byte数组中每个元素，进行计算加密
			for (int i = 0; i < digest.length; i++) {
				//byte : -128 - 127
				int result = digest[i] & 0xff;//0xff : 255的十六进制的写法
				//将int类值转化成十六进制的字符串
				String hexString = Integer.toHexString(result);
				if (hexString.length()<2) {
					sb.append("0");
				}
				sb.append(hexString);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
