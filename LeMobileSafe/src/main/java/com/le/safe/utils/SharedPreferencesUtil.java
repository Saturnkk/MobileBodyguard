package com.le.safe.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
	private static SharedPreferences sp;
	/**
	 * 保存boolean值操作
	 * key  value
	 */
	public static void saveBoolean(Context context,String key,boolean value){
		//config : 是保存boolean值的文件的名称
		//mode : 权限
		if (sp==null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, value).commit();
		
		/*Editor edit = sp.edit();
		edit.putBoolean(key, value);
		edit.commit();*/
	}
	/**
	 * 获取保存的booelan值
	 * @param context
	 * @param key  ： 保存信息的key
	 * @param defValue  : 缺省默认的值
	 * @return
	 */
	public static boolean getBoolean(Context context,String key,boolean defValue){
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		
		/*SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		sp.getBoolean(Contants.UPDATE, true);*/
		
		return sp.getBoolean(key, defValue);
	}
	
	
	/**
	 * 保存String值操作
	 * key  value
	 */
	public static void saveString(Context context,String key,String value){
		//config : 是保存boolean值的文件的名称
		//mode : 权限
		if (sp==null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).commit();
	}
	/**
	 * 获取保存的String值
	 * @param context
	 * @param key  ： 保存信息的key
	 * @param defValue  : 缺省默认的值
	 * @return
	 */
	public static String getString(Context context,String key,String defValue){
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getString(key, defValue);
	}
	/**
	 * 保存int值操作
	 * key  value
	 */
	public static void saveInt(Context context,String key,int value){
		//config : 是保存boolean值的文件的名称
		//mode : 权限
		if (sp==null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putInt(key, value).commit();
	}
	/**
	 * 获取保存的int值
	 * @param context
	 * @param key  ： 保存信息的key
	 * @param defValue  : 缺省默认的值
	 * @return
	 */
	public static int getInt(Context context,String key,int defValue){
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getInt(key, defValue);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
