package com.le.safe.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

public class ServiceUtil {
	
	/**
	 * 动态获取服务是否开启
	 * @param serviceClassName ： 服务的全类名，包名+服务名称
	 * @return
	 */
	public static boolean isServiceRunning(Context context,String serviceClassName){
		//动态去获取服务开启的状态
		//进程的管理者，活动的管理者
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//getRunningServices : 获取系统中正在运行的服务
		//maxNum : 返回服务的最大个数
		List<RunningServiceInfo> runningServices = activityManager.getRunningServices(1000);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			//获取组件的标示
			ComponentName service = runningServiceInfo.service;
			String className = service.getClassName();
			//传递一个服务的全类名到工具类中，通过判断获取的正在运行的服务的全类名是否和传递过来的一致，一致的服务正在运行，不一致，没有运行
			if (serviceClassName.equals(className)) {
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
