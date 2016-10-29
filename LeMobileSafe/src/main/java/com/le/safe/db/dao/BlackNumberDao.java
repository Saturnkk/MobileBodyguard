package com.le.safe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.le.safe.bean.BlackNumberInfo;
import com.le.safe.db.BlackNumberContants;
import com.le.safe.db.BlackNumberOpenHelper;

import java.util.ArrayList;
import java.util.List;



/**
 * 数据库操作
 * 
 * @author Administrator
 * 
 */
public class BlackNumberDao {
	private BlackNumberOpenHelper blackNumberOpenHelper;

	// 数据库的增删改查操作
	// 1.获取数据库
	public BlackNumberDao(Context context) {
		blackNumberOpenHelper = new BlackNumberOpenHelper(context);
	}

	// 2.增删改查
	// 添加
	/**
	 * 添加黑名单数据操作
	 */
	public boolean addBlacknumber(String blacknumber, int mode) {
		// 1.获取数据库
		SQLiteDatabase database = blackNumberOpenHelper.getWritableDatabase();
		// 2.添加操作
		// 类似map，key：数据库表中字段名称
		ContentValues values = new ContentValues();
		values.put(BlackNumberContants.BLACKNUMBER_NUMBER, blacknumber);
		values.put(BlackNumberContants.BLACKNUMBER_MODE, mode);
		// nullColumnHack : sqlite数据库不允许添加null这种数据，
		// 如果添加的列为null,就把nullColumnHack数据添加到数据库，nullColumnHack可以为null,表示数据库自己添加为null数据
		// values :就是添加的数据
		long insert = database.insert(
				BlackNumberContants.BLACKNUMBER_TABLENAME, null, values);
		// 4.关闭数据库
		database.close();
		// 3.判断是否添加成功
		return insert != -1;
	}

	// 删除
	/**
	 * 删除黑名单号码 根据黑名单号码删除相应的数据库中的记录 delete为0表示删除失败，否则成功
	 * 
	 * @return
	 */
	public boolean deleteBlackNumber(String blacknumber) {
		// 1.获取数据库
		SQLiteDatabase database = blackNumberOpenHelper.getWritableDatabase();
		// 2.删除
		// whereClause : 删除条件 where balcknumber="110" balcknumber=?
		// whereArgs : 查询条件参数
		int delete = database.delete(BlackNumberContants.BLACKNUMBER_TABLENAME,
				BlackNumberContants.BLACKNUMBER_NUMBER + "=?",
				new String[] { blacknumber });
		database.close();
		return delete != 0;
	}

	// 更新
	/**
	 * 更新黑名单数据 根据黑名单号码，更新黑名单号码的拦截模式 update为0表示更新失败，否则成功
	 * 
	 * @return
	 */
	public boolean updateBalckNumber(String blackNumber, int mode) {
		// 1.获取数据库
		SQLiteDatabase database = blackNumberOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BlackNumberContants.BLACKNUMBER_MODE, mode);
		// ContentValues : 要更新的数据
		// whereClause : 查询条件
		// whereArgs : 查询条件的参数
		int update = database.update(BlackNumberContants.BLACKNUMBER_TABLENAME,
				values, BlackNumberContants.BLACKNUMBER_NUMBER + "=?",
				new String[] { blackNumber });
		database.close();
		return update != 0;
	}

	// 查询：查询一条数据，查询多条数据
	/**
	 * 根据黑名单号码，查询黑名单拦截模式
	 * 
	 * @return
	 */
	public int queryBlackNumberMode(String blackNumber) {
		int mode = -1;
		SQLiteDatabase database = blackNumberOpenHelper.getReadableDatabase();
		// columns : 查询的数据列名
		// selection : 查询条件
		// selectionArgs : 查询条件的参数
		// groupBy : 分组
		// having : 去重
		// orderBy :　排序
		Cursor cursor = database.query(
				BlackNumberContants.BLACKNUMBER_TABLENAME,
				new String[] { BlackNumberContants.BLACKNUMBER_MODE },
				BlackNumberContants.BLACKNUMBER_NUMBER + "=?",
				new String[] { blackNumber }, null, null, null);
		// 解析cursor获取数据
		if (cursor != null) {
			// 如果查询的cursor中只有一条数据，用if就可以了
			if (cursor.moveToNext()) {
				mode = cursor.getInt(0);
			}
			cursor.close();
			database.close();
		}
		return mode;
	}

	// 查询多条数据
	/**
	 * 查询全部数据 查询所有的黑名单号码和拦截模式
	 */
	public List<BlackNumberInfo> queryAllBlackNumber() {
		SystemClock.sleep(3000);
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase database = blackNumberOpenHelper.getReadableDatabase();
		Cursor cursor = database.query(
				BlackNumberContants.BLACKNUMBER_TABLENAME, new String[] {
						BlackNumberContants.BLACKNUMBER_NUMBER,
						BlackNumberContants.BLACKNUMBER_MODE }, null, null,
				null, null, "_id desc");//desc : 倒序  	 默认：正序
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String blacknumber = cursor.getString(0);
				int mode = cursor.getInt(1);
				// 将获取的数据保存到bean类中
				BlackNumberInfo blackNumberInfo = new BlackNumberInfo(
						blacknumber, mode);
				// 将bean类保存到集合中，方便使用
				list.add(blackNumberInfo);
			}
			cursor.close();
			database.close();
		}
		return list;
	}
	//查询20条目的方式
	/**
	 * 查询部分数据
	 * @param maxnum  ：   查询的最大条目
	 * @param startindex ： 查询的起始位置
	 * @return
	 */
	public List<BlackNumberInfo> queryPartBlackNumber(int maxnum,int startindex) {
		SystemClock.sleep(3000);
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase database = blackNumberOpenHelper.getReadableDatabase();
		/*Cursor cursor = database.query(
				BlackNumberContants.BLACKNUMBER_TABLENAME, new String[] {
						BlackNumberContants.BLACKNUMBER_NUMBER,
						BlackNumberContants.BLACKNUMBER_MODE }, null, null,
				null, null, "_id desc");//desc : 倒序  	 默认：正序*/	
		//rawQuery : 运行sql语句的方法，复杂的sql语句，系统提供的方法是无法执行，系统提供方法只能简单的查询查询操作
		//sql : sql语句     
		//selectionArgs : sql语句中所需的参数数据
		Cursor cursor = database.rawQuery("select blacknumber,mode from info order by _id desc limit ? offset ?", new String[]{maxnum+"",startindex+""});
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String blacknumber = cursor.getString(0);
				int mode = cursor.getInt(1);
				// 将获取的数据保存到bean类中
				BlackNumberInfo blackNumberInfo = new BlackNumberInfo(
						blacknumber, mode);
				// 将bean类保存到集合中，方便使用
				list.add(blackNumberInfo);
			}
			cursor.close();
			database.close();
		}
		return list;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
