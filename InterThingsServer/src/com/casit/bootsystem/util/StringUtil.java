package com.casit.bootsystem.util;

import java.util.ArrayList;

import com.casit.json.JsonO;


public class StringUtil {

	/**
	 * 判断字符串是否为空,包括""和Null
	 * @param str
	 * @return true表示为空, false表示不为空
	 */
	public static boolean isEmpty(String str){
		boolean result = false;
		if("".equals(str) || str==null){
			result=true;
		} 
		return result;
	}
	
	/**
	 * 判断字符串是否不为空, 包括""和Null
	 * @param str
	 * @return true表示不为空, false表示为空
	 */
	public static boolean isNotEmpty(String str){
		boolean result = false;
		if(!"".equals(str) && str !=null){
			result=true;
		} 
		return result;
	}
	
	/**
	 * 删除Json为空的字符串
	 * @param json
	 * @param filedName
	 */
	public static void removeEmptyDate(JsonO json, String filedName){
		if(json!=null){
			if(json.containsKey(filedName) && StringUtil.isEmpty(json.getString(filedName))){
				json.remove(filedName);
			}
		}
	}
	
	/**
	 * 用于获取为空字符串的对象,处理为null
	 * @param json
	 * @param filedName
	 * @return
	 */
	public static String getObjectQuoted(JsonO json, String filedName){
		String quoted="";
		if(json!=null){
			if(json.containsKey(filedName) && StringUtil.isEmpty(json.getString(filedName))){
				quoted = (","+filedName+",");
			}
		}
		return quoted;
	}
	
	/**
	 * 用于获取为多个空字符串的对象,处理为null
	 * @param json
	 * @param filedList
	 * @return
	 */
	public static String getObjectQuoted(JsonO json, ArrayList<String> filedList){
		String quoted="";
		 for(String filedName:filedList){
			 quoted += getObjectQuoted(json, filedName);
		 }
		return quoted;
	}
	
}
