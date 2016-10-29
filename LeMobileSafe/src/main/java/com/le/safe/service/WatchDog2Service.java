package com.le.safe.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.view.accessibility.AccessibilityEvent;

import com.le.safe.activity.WatchDogActivity;
import com.le.safe.db.dao.WatchDogDao;

import java.util.List;



public class WatchDog2Service extends AccessibilityService {

	private List<String> allApps;
	private WatchDogDao watchDogDao;
	private UnlockCurrentAppReceiver unlockCurrentAppReceiver;
	/**
	 * 解锁的应用程序的包名
	 */
	private String unlockcurrentapppackagename;
	private ScreenOffReceiver screenOffReceiver;

	// 必须先连接AccessibilityService的服务，才能和系统的服务保持通讯，才能进行监听操作
	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();// 设置连接的属性
		accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;// 设置监听事件属性是监听窗口的状态操作
		accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;// 设置反馈信息的类型是一般默认的反馈类型
		setServiceInfo(accessibilityServiceInfo);
	}

	private class UnlockCurrentAppReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 接受传递的数据
			unlockcurrentapppackagename = intent.getStringExtra("packagename");
		}

	}

	/**
	 * 锁屏的广播接受者
	 * 
	 * @author Administrator
	 * 
	 */
	private class ScreenOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			unlockcurrentapppackagename = null;
		}

	}

	@Override
	public void onCreate() {
		super.onCreate();
		watchDogDao = new WatchDogDao(getApplicationContext());
		allApps = watchDogDao.queryAllLockApp();

		// 解锁的广播接受者
		unlockCurrentAppReceiver = new UnlockCurrentAppReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("cn.itcast.mobliesafexian05.watchdog.unlock");// 接受广播事件
		registerReceiver(unlockCurrentAppReceiver, intentFilter);

		// 设置锁屏，重新给解锁的应用重新加锁
		screenOffReceiver = new ScreenOffReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);// 接受锁屏的广播事件
		registerReceiver(screenOffReceiver, filter);
		
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
	}

	// 监听服务的事件
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// 监听窗口操作
		// 获取事件的类型
		int action = event.getEventType();
		// 判断事件类型是否是监听窗口变化的类型
		if (action == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
			// 窗口状态变化，只要窗口内容发生变化，窗口的状态就会发生变化
			String packagename = event.getPackageName().toString();

			boolean b = allApps.contains(packagename);// 集合中是否包含包名
			// 判断应用是否是加锁应用，是，弹出解锁界面
			if (b) {
				// 是否执行解锁操作
				if (!packagename.equals(unlockcurrentapppackagename)) {
					// 弹出解锁界面
					Intent intent = new Intent(WatchDog2Service.this,
							WatchDogActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 给跳转的activity设置一个任务栈
					// 将加锁应用程序的包名传递给解锁界面
					intent.putExtra("packageName", packagename);
					startActivity(intent);
				}
			}
		}
	}

	// 获取反馈信息
	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub

	}

}
