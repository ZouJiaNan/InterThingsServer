package com.casit.bootsystem.util;

import java.util.Random;

public class BootSystemTools {

	//生成随即字符串
	public static String getRandomString(int length) { //length表示生成字符串的长度
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789";   
	    Random random = new Random();   
	    StringBuffer sb = new StringBuffer();   
	    for (int i = 0; i < length; i++) {   
	        int number = random.nextInt(base.length());   
	        sb.append(base.charAt(number));   
	    }   
	    return sb.toString();   
	 }  
	//获取文件扩展名
	public static String getFileExtention(String fileName){
		return fileName.substring(fileName.lastIndexOf("."));
	}
	//获取文件名 不包含扩展名
	public static String getFileNameWithoutExtention(String fileName){
		return fileName.substring(0,fileName.lastIndexOf("."));
	}
}
