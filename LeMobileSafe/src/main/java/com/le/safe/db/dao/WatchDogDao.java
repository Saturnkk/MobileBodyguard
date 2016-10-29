package com.le.safe.db.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.le.safe.db.WatchdogContants;
import com.le.safe.db.WatchdogOpenHelper;

import java.util.ArrayList;
import java.util.List;



/**
 * 程序锁数据库操作
 * 
 * @author Administrator
 * 
 */
public class WatchDogDao {
	private WatchdogOpenHelper watchdogOpenHelper;
	private Context mContext;

	// 数据库的增删改查操作
	// 1.获取数据库
	public WatchDogDao(Context context) {
		this.mContext = context;
		watchdogOpenHelper = new WatchdogOpenHelper(context);
	}

	// 2.增删改查
	// 添加
	/**
	 * 添加应用程序包名，加锁操作
	 */
	public boolean addLockApp(String packageName) {
		// 1.获取数据库
		SQLiteDatabase database = watchdogOpenHelper.getWritableDatabase();
		// 2.添加操作
		// 类似map，key：数据库表中字段名称
		ContentValues values = new ContentValues();
		values.put(WatchdogContants.WATCHDOG_PACKAGENAME, packageName);
		// nullColumnHack : sqlite数据库不允许添加null这种数据，
		// 如果添加的列为null,就把nullColumnHack数据添加到数据库，nullColumnHack可以为null,表示数据库自己添加为null数据
		// values :就是添加的数据
		long insert = database.insert(WatchdogContants.WATCHDOG_TABLENAME,
				null, values);
		// 4.关闭数据库
		database.close();

		// 通知内容观察者，更新数据
		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = Uri
				.parse("content://cn.itcast.mobliesafexian05.watchdog.change");
		resolver.notifyChange(uri, null);// 通知内容观察者数据发生变化了

		// 3.判断是否添加成功
		return insert != -1;
	}

	// 删除
	/**
	 * 根据应用程序的包名删除相应的记录，解锁操作
	 * 
	 * @return
	 */
	public boolean deleteLockApp(String packageName) {
		// 1.获取数据库
		SQLiteDatabase database = watchdogOpenHelper.getWritableDatabase();
		// 2.删除
		// whereClause : 删除条件 where balcknumber="110" balcknumber=?
		// whereArgs : 查询条件参数
		int delete = database.delete(WatchdogContants.WATCHDOG_TABLENAME,
				WatchdogContants.WATCHDOG_PACKAGENAME + "=?",
				new String[] { packageName });
		database.close();

		// 通知内容观察者，更新数据
		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = Uri
				.parse("content://cn.itcast.mobliesafexian05.watchdog.change");
		resolver.notifyChange(uri, null);// 通知内容观察者数据发生变化了

		return delete != 0;
	}

	// 查询：查询一条数据，查询多条数据
	/**
	 * 查询应用是否加锁，查询包名是否在数据库中
	 * 
	 * @return
	 */
	public boolean queryLockApp(String packageName) {
		boolean isHave = false;
		SQLiteDatabase database = watchdogOpenHelper.getReadableDatabase();
		// columns : 查询的数据列名
		// selection : 查询条件
		// selectionArgs : 查询条件的参数
		// groupBy : 分组
		// having : 去重
		// orderBy :　排序
		// 因为只是查询数据库中是否存在该包名，不需要查询出具体数据，所以查询列可以为null
		Cursor cursor = database.query(WatchdogContants.WATCHDOG_TABLENAME,
				null, WatchdogContants.WATCHDOG_PACKAGENAME + "=?",
				new String[] { packageName }, null, null, null);
		// 解析cursor获取数据
		if (cursor != null) {
			// 如果查询的cursor中只有一条数据，用if就可以了
			if (cursor.moveToNext()) {
				isHave = true;
			}
			cursor.close();
			database.close();
		}
		return isHave;
	}

	// 查询多条数据
	/**
	 * 查询所有加锁的应用程序的包名
	 */
	public List<String> queryAllLockApp() {
		List<String> list = new ArrayList<String>();
		SQLiteDatabase database = watchdogOpenHelper.getReadableDatabase();
		Cursor cursor = database.query(WatchdogContants.WATCHDOG_TABLENAME,
				new String[] { WatchdogContants.WATCHDOG_PACKAGENAME }, null,
				null, null, null, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String packageName = cursor.getString(0);
				// 将bean类保存到集合中，方便使用
				list.add(packageName);
			}
			cursor.close();
			database.close();
		}
		return list;
	}

}
