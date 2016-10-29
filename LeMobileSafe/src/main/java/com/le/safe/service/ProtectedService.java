package com.le.safe.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.example.dell.myapplication.R;
import com.le.safe.activity.SplashActivity;


public class ProtectedService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		//获取消息
		Notification notification = new Notification();
		//设置消息到来的时候显示图标
		notification.icon = R.drawable.ic_default;
		//设置消息到来的时候显示文本
		notification.tickerText = "乐乐手机卫士时刻保护您的安全";
		//设置消息在通知栏中的布局
		//RemoteViews : 远程布局
		notification.contentView = new RemoteViews(getPackageName(), R.layout.protected_item);
		
		//PendingIntent : 延迟意图，只有执行了某个操作，意图才会执行
		Intent intent= new Intent(this,SplashActivity.class);
		//this :上下文
		//requestCode :请求码
		//intent : Intent意图
		//flags : 指定信息的标签
		//options : 设置开启的方式,在16版本之上才有
		notification.contentIntent = PendingIntent.getActivity(this, 101, intent,PendingIntent.FLAG_UPDATE_CURRENT);
		
		//开启前台进程
		//100 : 表示消息的id，不能是0
		startForeground(100, notification);
	}

}
