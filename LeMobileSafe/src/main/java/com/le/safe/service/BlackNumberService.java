package com.le.safe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;


import com.le.safe.db.BlackNumberContants;
import com.le.safe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;


public class BlackNumberService extends Service {

	private SMSReceiver receiver;
	private BlackNumberDao blackNumberDao;
	private TelephonyManager telephonyManager;
	private MyPhoneStateListener myPhoneStateListener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 监听短信到来的广播接受者
	 * 
	 * @author Administrator
	 * 
	 */
	private class SMSReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 监听拦截操作
			// 接受解析短信
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object obj : objs) {
				// 将获取的短信转化成短信对象
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				// 获取短信的内容
				String body = smsMessage.getMessageBody();
				// 获取发件人
				String sender = smsMessage.getOriginatingAddress();
				// 判断发件人是否是拦截号码
				// 查询发件人的拦截模式
				int mode = blackNumberDao.queryBlackNumberMode(sender);
				if (mode == BlackNumberContants.BLACKNUMBER_SMS
						|| mode == BlackNumberContants.BLACKNUMBER_ALL) {
					abortBroadcast();// 拦截短信
				}
			}
		}

	}

	@Override
	public void onCreate() {
		super.onCreate();
		blackNumberDao = new BlackNumberDao(getApplicationContext());
		// 短信拦截,判断发件人号码是否是拦截号码
		// 通过代码注册广播接受者
		// 1.创建广播接受者
		receiver = new SMSReceiver();
		// 2.创建intentfilter
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.setPriority(1000);
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		// 3.注册广播接受者
		registerReceiver(receiver, intentFilter);
		// 电话拦截,监听来电号码是否是拦截号码，监听电话的状态
		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();
		// PhoneStateListener : 电话状态的监听事件
		// events : 监听电话的什么状态 LISTEN_NONE : 不监听任何状态 LISTEN_CALL_STATE：监听电话的状态
		telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	private class MyPhoneStateListener extends PhoneStateListener {
		// 监听电话状态
		// state : 电话的状态
		// incomingNumber : 来电号码
		@Override
		public void onCallStateChanged(int state, final String incomingNumber) {
			// 监听当电话是来电状态的时候，判断来电号码是否是拦截号码
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:// 空闲的状态

				break;
			case TelephonyManager.CALL_STATE_RINGING:// 响铃的状态
				int mode = blackNumberDao.queryBlackNumberMode(incomingNumber);
				if (mode == BlackNumberContants.BLACKNUMBER_CALL || mode == BlackNumberContants.BLACKNUMBER_ALL) {
					//挂断电话操作
					endCall();
					//删除通话记录
					//通话记录：以日志的形式在系统展示的
					//因为系统也是要把通话记录写入数据库的，我们是在系统写入数据库之前，把数据库中的数据删除了，所以会留下最新的通话记录
					final ContentResolver resolver = getContentResolver();
					final Uri uri = Uri.parse("content://call_log/calls");//http://www.baidu.com/jdk
					//观察数据库的变化，如果变化了，删除数据库中数据，因为是内容提供者，所以要用到内容观察者，观察内容提供者数据的变化，如果变化执行相应的操作
					//notifyForDescendents : 匹配类型      true：精确匹配  false：模糊匹配
					resolver.registerContentObserver(uri, true, new ContentObserver(new Handler()) {
						//当内容提供者数据发生变化的时候调用的方法
						@Override
						public void onChange(boolean selfChange) {
							super.onChange(selfChange);
							resolver.delete(uri, "number=?", new String[]{incomingNumber});
							//注销内容观察者
							resolver.unregisterContentObserver(this);
						}
					});
				}

				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 通话的的状态

				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 注销广播接受者
		unregisterReceiver(receiver);
		//取消监听电话状态的操作
		telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
	}
	/**
	 *挂断电话
	 */
	public void endCall() {
//		try {
//		//1.5版本之前
//		//因为ServiceManager隐藏不让使用，所以通过反射去执行ServiceManager中方法
//		//1.获取字节码文件
//		//Class.forName("android.os.ServiceManager");
//		Class<?> loadClass = BlackNumberService.class.getClassLoader().loadClass("android.os.ServiceManager");
//		//2.获取执行的方法
//		//name:方法名
//		//parameterTypes : 参数类型
//		Method method = loadClass.getDeclaredMethod("getService", String.class);
//		//3.执行方法
//		//method : 方法所在的类的对象，如果方法不是静态方法，必须设置方法所在类的对象，如果是静态方法，设置为null
//		//args : 方法的参数
//		IBinder invoke = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
//		ITelephony iTelephony = ITelephony.Stub.asInterface(invoke);
//		iTelephony.endCall();//挂断电话操作
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
