package com.le.safe.db;

public interface WatchdogContants {

	// 数据库名称
	public static final String WATCHDOG_DBNAME = "watchdog.db";
	// 数据库的版本号
	public static final int WATCHDOG_DBVERSION = 1;
	// 表名
	public static final String WATCHDOG_TABLENAME = "info";
	// id
	public static final String WATCHDOG_ID = "_id";
	// 黑名单号码字段
	public static final String WATCHDOG_PACKAGENAME = "packagename";
	
	// 抽取sql,方便修改sql，避免在实际的操作类中进行操作
	public static final String WATCHDOG_SQL = "create table "
			+ WATCHDOG_TABLENAME + "(" + WATCHDOG_ID
			+ " integer primary key autoincrement," + WATCHDOG_PACKAGENAME
			+ " varchar(100))";
}
