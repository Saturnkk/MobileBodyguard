package com.le.safe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建数据
 * @author Administrator
 *
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper {
	/**
	 * name :数据库名称
	 * factory ： 游标工厂
	 * version ： 数据库版本号
	 * @param context
	 * 设置数据库的信息
	 */
	public BlackNumberOpenHelper(Context context) {
		super(context, BlackNumberContants.BLACKNUMBER_DBNAME, null, BlackNumberContants.BLACKNUMBER_DBVERSION);
	}
	//数据库创建表结构的时候使用
	@Override
	public void onCreate(SQLiteDatabase db) {
		//表的字段：_id,blacknumber(黑名单号码),mode(拦截方式)
		db.execSQL(BlackNumberContants.BLACKNUMBER_SQL);
	}
	//当数据的进行版本更新的时候调用的方法
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	
	
	
	
	
	
	

}
