package com.le.safe.db;

public interface BlackNumberContants {

	// 数据库名称
	public static final String BLACKNUMBER_DBNAME = "blacknumber.db";
	// 数据库的版本号
	public static final int BLACKNUMBER_DBVERSION = 1;
	// 表名
	public static final String BLACKNUMBER_TABLENAME = "info";
	// id
	public static final String BLACKNUMBER_ID = "_id";
	// 黑名单号码字段
	public static final String BLACKNUMBER_NUMBER = "blacknumber";
	// 黑名单拦截模式字段
	public static final String BLACKNUMBER_MODE = "mode";
	// 抽取sql,方便修改sql，避免在实际的操作类中进行操作
	public static final String BLACKNUMBER_SQL = "create table "
			+ BLACKNUMBER_TABLENAME + "(" + BLACKNUMBER_ID
			+ " integer primary key autoincrement," + BLACKNUMBER_NUMBER
			+ " varchar(20)," + BLACKNUMBER_MODE + " varchar(2))";
	/**
	 * 电话拦截
	 */
	public static final int BLACKNUMBER_CALL=0;
	/**
	 * 短信拦截
	 */
	public static final int BLACKNUMBER_SMS=1;
	/**
	 * 全部拦截
	 */
	public static final int BLACKNUMBER_ALL=2;
}
