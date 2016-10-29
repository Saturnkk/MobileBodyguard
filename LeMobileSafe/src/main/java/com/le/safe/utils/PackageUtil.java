package com.le.safe.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageUtil {
	/**
	 * 获取应用程序的版本号
	 * @return
	 */
	public static int getVersionCode(Context context){
		//获取包的管理者
		PackageManager pm = context.getPackageManager();
		//根据包名获取应用程序清单文件的信息
		//packageName : 包名
		//flags : 指定信息的标签，如果要获取acitivity，service或者是权限等信息，就需要设置成相应的标签才能去获取
		//默认0，表示获取包名、版本号、版本名称等基本信息，如果设置成指定信息标签，比如GET_ACTIVITYS：获取除了包名、版本号、版本名称之外会额外的获取activity的信息
		try {
			//.getPackageName() : 获取应用程序的包名
			PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;//获取版本号
			//packageInfo.versionName//获取版本名称
		} catch (NameNotFoundException e) {
			//找不到包名的异常
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 获取应用程序的版本名称
	 * @return
	 */
	public static String getVersionName(Context context){
		//获取包的管理者
		PackageManager pm = context.getPackageManager();
		//根据包名获取应用程序清单文件的信息
		//packageName : 包名
		//flags : 指定信息的标签，如果要获取acitivity，service或者是权限等信息，就需要设置成相应的标签才能去获取
		//默认0，表示获取包名、版本号、版本名称等基本信息，如果设置成指定信息标签，比如GET_ACTIVITYS：获取除了包名、版本号、版本名称之外会额外的获取activity的信息
		try {
			//.getPackageName() : 获取应用程序的包名
			PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;//获取版本号
			//packageInfo.versionName//获取版本名称
		} catch (NameNotFoundException e) {
			//找不到包名的异常
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
