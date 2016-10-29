package com.le.safe.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;

import com.le.safe.activity.WatchDogActivity;
import com.le.safe.db.dao.WatchDogDao;

import java.util.List;



/**
 * 监听用户打开的应用程序，如果是加锁的引用，弹出解锁的界面
 * @author Administrator
 *
 */
public class WatchDog1Service extends Service {
	
	//设置循环监听是否执行的操作标示
	private boolean isWatch=false;
	private ActivityManager am;
	private WatchDogDao watchDogDao;
	private UnlockCurrentAppReceiver unlockCurrentAppReceiver;
	/**
	 * 解锁的应用程序的包名
	 */
	private String unlockcurrentapppackagename;
	private ScreenOffReceiver screenOffReceiver;
	private List<String> allApps;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private class UnlockCurrentAppReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//接受传递的数据
			unlockcurrentapppackagename = intent.getStringExtra("packagename");
		}
		
	}
	/**
	 * 锁屏的广播接受者
	 * @author Administrator
	 *
	 */
	private class ScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			unlockcurrentapppackagename = null;
		}
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		//解锁的广播接受者
		unlockCurrentAppReceiver = new UnlockCurrentAppReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("cn.itcast.mobliesafexian05.watchdog.unlock");//接受广播事件
		registerReceiver(unlockCurrentAppReceiver, intentFilter);
		
		//设置锁屏，重新给解锁的应用重新加锁
		screenOffReceiver = new ScreenOffReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);//接受锁屏的广播事件
		registerReceiver(screenOffReceiver, filter);
		
		
		
		watchDogDao = new WatchDogDao(getApplicationContext());
		isWatch = true;
		//时时刻刻监听用户打开的应用程序，如果打开的应用程序是加锁的应用，弹出解锁界面
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		new Thread(){

			public void run() {
				//将数据库中的数据，保存到内存中
				allApps = watchDogDao.queryAllLockApp();
				//但是当数据库中数据发生改变的时候，内存中的数据没有相应的进行更新操作
				//当数据库中的数据发生改变的时候，更新内存中的数据，内容观察者
				//注册内容观察者，时刻监听，数据库是否发生变化
				Uri uri = Uri.parse("content://cn.itcast.mobliesafexian05.watchdog.change");
				getContentResolver().registerContentObserver(uri, true, new ContentObserver(null) {
						//当数据库发生变化的时候进行数据更新操作
						public void onChange(boolean selfChange) {
							allApps = watchDogDao.queryAllLockApp();
						};
				});
				
				while(isWatch){
					//监听用户打开的应用程序
					//获取正在运行的任务栈，maxnum：获取正在运行的任务栈的最大个数，需要GET_TASKS权限，正在运行的应用，永远是集合的第一个元素
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					for (RunningTaskInfo runningTaskInfo : runningTasks) {
						ComponentName baseactivity = runningTaskInfo.baseActivity;//获取栈底的activity
						//runningTaskInfo.topActivity;//获取栈顶activity
						String packageName = baseactivity.getPackageName();//获取正在运行的任务栈的所对应的应用程序的包名
						System.out.println(packageName);
						boolean b = allApps.contains(packageName);//集合中是否包含包名
						//判断应用是否是加锁应用，是，弹出解锁界面
						if (b) {
							//是否执行解锁操作
							if (!packageName.equals(unlockcurrentapppackagename)) {
								//弹出解锁界面
								Intent intent = new Intent(WatchDog1Service.this,WatchDogActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//给跳转的activity设置一个任务栈
								//将加锁应用程序的包名传递给解锁界面
								intent.putExtra("packageName", packageName);
								startActivity(intent);
							}
						}
					} 
					SystemClock.sleep(300);
				}
			};
		}.start();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		isWatch = false;
		unregisterReceiver(unlockCurrentAppReceiver);
		unregisterReceiver(screenOffReceiver);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
