package com.le.safe.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

public class AntivirusDao {

	/**
	 * 查询是否是病毒
	 * 查询应用程序的特征码是否在数据库中
	 * @return
	 */
	public static boolean isAntivirus(Context context,String md5){
		boolean isAntivirus = false;
		//1.打开数据库
		File file = new File(context.getFilesDir(), "antivirus.db");
		SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
		//2.查询数据
		//md5 :表中字段
		Cursor cursor = database.query("datable", new String[]{"md5"}, "md5=?", new String[]{md5}, null, null, null);
		if (cursor!=null) {
			if (cursor.moveToNext()) {
				//在数据库中
				isAntivirus = true;
			}
			cursor.close();
		}
		database.close();
		return isAntivirus;
	}
	
}
