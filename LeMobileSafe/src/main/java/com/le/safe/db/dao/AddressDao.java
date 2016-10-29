package com.le.safe.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

public class AddressDao {
	/**
	 * 根据号码查询号码归属地
	 * 
	 * @param number
	 * @return
	 */
	public static String getAddress(Context context, String number) {
		String location = "";
		File file = new File(context.getFilesDir(), "address.db");
		// 1.打开已有的数据
		// path : 数据库的路径
		// factory : 游标工厂
		// flags : 读写权限
		// getAbsolutePath : 绝对路径
		// NO_LOCALIZED_COLLATORS. ： 只能写，不能读
		SQLiteDatabase database = SQLiteDatabase.openDatabase(
				file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
		// 2.查询数据库
		// 判断号码长度，因为号码是有多个数字随意组成一个数字段，所以号码必须都是数字，判断是否是11位，还要考虑110,10086等特殊号码
		// 正则表达式
		// ^1[34578]\d{9}$   11位
		if (number.matches("^1[34578]\\d{9}$")) {
			// substring : 包含头不包含尾
			Cursor cursor = database
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?)",
							new String[] { number.substring(0, 7) });
			if (cursor != null) {
				if (cursor.moveToNext()) {
					location = cursor.getString(0);
				}
				cursor.close();
			}
		}else{
			switch (number.length()) {
			case 3://110 120  119 911
				//紧急号码
				location = "紧急号码";
				break;
			case 4://5554    5556
				location = "模拟电话";
				break;
			case 5://10086  10010  100000 12306
				location = "客服电话";
				break;
			case 6:
				location = "火星电话";
				break;
			case 7: //座机电话，本地电话
			case 8://座机电话
				location = "本地电话";
				break;
			default:// 010 1234567  10位     010 12345678  11位      0372 12345678  12位
				//长途电话
				if (number.length() >=10 && number.startsWith("0")) {
					//长途电话，根据长途电话查询号码归属地
					//根据区号查询号码归属地
					//获取区号  3位和4位
					//3位
					String area = number.substring(1, 3);//010->10
					//根据区号查询数据
					Cursor cursor = database.rawQuery("select location from data2 where area=?", new String[]{area});
					//cursor没有查询数据也是不是空，有内存地址
						if (cursor.moveToNext()) {
							location = cursor.getString(0);
							//将归属地的后两位截掉
							//云南临沧电信   location.length()-2 :电信不要了  只要云南临沧
							location = location.substring(0, location.length()-2);
							//云南临沧
							cursor.close();
						}else{
							//如果3位没有查询出来，表示号码区号不是三位，查询4位区号
							String area4 = number.substring(1, 4);//0883   883
							Cursor cursor2 = database.rawQuery("select location from data2 where area=?", new String[]{area4});
							if (cursor2!=null) {
								if (cursor2.moveToNext()) {
									location = cursor2.getString(0);
									location = location.substring(0, location.length()-2);
									cursor2.close();
								}
							}
						}
				}
				break;
			}
		}
		database.close();
		return location;
	}

}
