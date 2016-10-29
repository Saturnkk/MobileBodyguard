package com.le.safe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.example.dell.myapplication.R;
import com.le.safe.service.GPSService;


public class SMSReceiver extends BroadcastReceiver {
	//只能知心10秒钟
	@Override
	public void onReceive(Context context, Intent intent) {
		//获取设备的管理者
		DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		//获取指定组件的标示,组件在android系统中的使用bean类
		//cls : 组件的.class
		ComponentName componentName = new ComponentName(context, AdminReceiver.class);
		//接受解析短信
		//接受短信
		//中国  70个汉字一条短信       71个汉字  对方接受的时候两个短信
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for(Object obj:objs){
			//将获取的短信转化成短信对象
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			//获取短信的内容
			String body = smsMessage.getMessageBody();
			//获取发件人
			String sender = smsMessage.getOriginatingAddress();
			System.out.println("发件人："+sender+"短信内容："+body);
			
			//发件人比如是安全号码才可以  155521前缀
			//注意：真机测试，对发件人进行是否是安全号码的判断
			
			//判断短信内容是否是指令短信
			if ("#*location*#".equals(body)) {
				//GPS追踪,将通过经纬获取的地理位置，发送给安全号码
				System.out.println("GPS追踪");
				//开启服务
				context.startService(new Intent(context,GPSService.class));
				//拦截短信
				abortBroadcast();//原生android上没有问题，但是在深度定制的android系统中失效，比如小米
			}else if("#*wipedata*#".equals(body)){
				//远程销毁数据
				System.out.println("远程销毁数据");
				//判断超级管理员权限是否开启
				//isAdminActive : 超级管理员是否激活
				//参数：超级管理员的标示
				if (devicePolicyManager.isAdminActive(componentName)) {
					//擦除数据
					//参数：表示清除手机内存还是SD卡
					devicePolicyManager.wipeData(0);//类似恢复出厂设置，慎用
				}
				//拦截短信
				abortBroadcast();//原生android上没有问题，但是在深度定制的android系统中失效，比如小米
			}else if("#*alarm*#".equals(body)){
				//播放报警音乐
				System.out.println("播放报警音乐");
				//播放报警音乐
				MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
				//参数1：左声道
				//参数2：右声道
				mediaPlayer.setVolume(1.0f, 1.0f);//设置音量大小，1.0f音量的比例、
				mediaPlayer.setLooping(true);//设置是否循环播放
				mediaPlayer.start();//开始播放
				//拦截短信
				abortBroadcast();//原生android上没有问题，但是在深度定制的android系统中失效，比如小米
			}else if("#*lockscreen*#".equals(body)){
				//远程锁屏
				System.out.println("远程锁屏");
				if (devicePolicyManager.isAdminActive(componentName)) {
					//0:普通的设置密码
					//1:只能让当前的超级管理员使用
					devicePolicyManager.resetPassword("123", 0);
					devicePolicyManager.lockNow();//锁屏的方法
				}
				//拦截短信
				abortBroadcast();//原生android上没有问题，但是在深度定制的android系统中失效，比如小米
			}
		}
	}

}
