package com.le.safe.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;

/**
 * 锁屏清理进程
 * @author Administrator
 *
 */
public class LockScreenClearService extends Service {

	private LockScreenOffReceiver lockScreenOffReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 锁屏广播接受者
	 * @author Administrator
	 *
	 */
	private class LockScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//清理进程的操作
			killProcess();
		}
		
	}
	/**
	 * 清理进程操作
	 */
	public void killProcess() {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			//屏蔽不能清理当前应用程序
			if (!runningAppProcessInfo.processName.equals(getPackageName())) {
				am.killBackgroundProcesses(runningAppProcessInfo.processName);
			}
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		//锁屏清理进程，注册锁屏的广播接受者
		//1.广播接受者
		lockScreenOffReceiver = new LockScreenOffReceiver();
		//2.设置过滤条件
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		//3.注册广播接受者
		registerReceiver(lockScreenOffReceiver, intentFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//注销广播接受者
		unregisterReceiver(lockScreenOffReceiver);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
