package com.le.safe.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommonNumberDao {
	/**
	 * 获取组的数据
	 * @param context
	 * @return
	 */
	public static List<Group> getGroups(Context context){
		List<Group> list = new ArrayList<Group>();
		File file = new File(context.getFilesDir(), "commonnum.db");
		// 1.打开已有的数据
		// path : 数据库的路径
		// factory : 游标工厂
		// flags : 读写权限
		// getAbsolutePath : 绝对路径
		// NO_LOCALIZED_COLLATORS. ： 只能写，不能读
		SQLiteDatabase database = SQLiteDatabase.openDatabase(
				file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = database.query("classlist", new String[]{"name","idx"}, null, null, null, null, null);
		if (cursor!=null) {
			while(cursor.moveToNext()){
				String name = cursor.getString(0);
				String idx = cursor.getString(1);
				//因为孩子的方法中需要用到idx，需求是每查询出一个组的数据，就要将对应的孩子的数据查询出来，所以将孩子获取的数据，保存到组的数据的bean类
				List<Child> child = getChild(context, idx);
				Group group = new Group(name, idx, child);
				list.add(group);
			}
			cursor.close();
			database.close();
		}
		return list;
	}
	/**
	 * 查询孩子的数据
	 * @param context
	 * @param idx
	 * @return
	 */
	public static List<Child> getChild(Context context,String idx){
		List<Child> list = new ArrayList<Child>();
		File file = new File(context.getFilesDir(), "commonnum.db");
		// 1.打开已有的数据
		// path : 数据库的路径
		// factory : 游标工厂
		// flags : 读写权限
		// getAbsolutePath : 绝对路径
		// NO_LOCALIZED_COLLATORS. ： 只能写，不能读
		SQLiteDatabase database = SQLiteDatabase.openDatabase(
				file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = database.query("table"+idx, new String[]{"number","name"}, null, null, null, null, null);
		if (cursor != null) {
			while(cursor.moveToNext()){
				String number = cursor.getString(0);
				String name = cursor.getString(1);
				Child child = new Child(number, name);
				list.add(child);
			}
			cursor.close();
			database.close();
		}
		return list;
	}
	
	public static class Group{
		public String name;
		public String idx;
		public List<Child> child;
		public Group(String name, String idx, List<Child> child) {
			super();
			this.name = name;
			this.idx = idx;
			this.child = child;
		}
		
	}
	public static class Child{
		public String number;
		public String name;
		public Child(String number, String name) {
			super();
			this.number = number;
			this.name = name;
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
