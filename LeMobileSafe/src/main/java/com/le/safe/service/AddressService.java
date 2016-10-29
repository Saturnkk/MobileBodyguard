package com.le.safe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.dell.myapplication.R;
import com.le.safe.db.dao.AddressDao;
import com.le.safe.utils.Contants;
import com.le.safe.utils.SharedPreferencesUtil;


public class AddressService extends Service {

	private TelephonyManager telephonyManager;
	private MyPhoneStateListener myPhoneStateListener;
	private WindowManager windowManager;
	private View view;
	private WindowManager.LayoutParams params;
	private OutGoingCallReceiver outGoingCallReceiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 监听外拨电话的广播接受者
	 * @author Administrator
	 *
	 */
	private class OutGoingCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//获取外拨电话，根据电话获取号码归属地，显示出来
			String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			String address = AddressDao.getAddress(context, number);
			if (!TextUtils.isEmpty(address)) {
				//显示操作
				showToast(address);
			}
		}
		
	}
	@Override
	public void onCreate() {
		super.onCreate();
		//来电或者外拨电话，显示号码归属地的控件
		
		//监听外拨电话动作
		outGoingCallReceiver = new OutGoingCallReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(outGoingCallReceiver, intentFilter);
		
		
		
		//来电操作
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
			case TelephonyManager.CALL_STATE_IDLE:// 空闲的状态,挂断电话的状态、
				hideToast();

				break;
			case TelephonyManager.CALL_STATE_RINGING:// 响铃的状态
				//获取号码归属地
				String address = AddressDao.getAddress(getApplicationContext(), incomingNumber);
				if (!TextUtils.isEmpty(address)) {
					//Toast.makeText(getApplicationContext(), address, 0).show();
					showToast(address);
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
		//取消监听电话状态的操作
		telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		//注销外拨电话的广播接受者
		unregisterReceiver(outGoingCallReceiver);
	}
	/**
	 * 显示自定义toast
	 */
	public void showToast(String address) {
		//获取窗口管理器
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		view = View.inflate(getApplicationContext(), R.layout.custom_toast, null);
		
		//根据保存的控件的背景，设置归属地显示控件的背景
		int mbgId = SharedPreferencesUtil.getInt(getApplicationContext(), Contants.ADDRESSDIALOG_BG, R.drawable.shape_customdialog_bg_normal);
		view.setBackgroundResource(mbgId);
		
		TextView mAddress = (TextView) view.findViewById(R.id.tv_customtoast_address);
		mAddress.setText(address);
		
		//LayoutParams ： 设置view控件（对象）的属性
		//LayoutParams : 设置属性规则，view控件要添加到那个父控件，必须使用那个父控件的LayoutParams,保证控件的属性全部有效
		params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;//布局文件中高度包裹内容一致
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;//宽度包裹内容
        params.flags = 
        		WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// 没有焦点，设置触摸事件，控件会自动获取焦点，按下获取焦点，抬起的释放焦点
                | 
              // WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE		//禁止触摸
                //| 
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON; // 保持屏幕常亮
        params.format = PixelFormat.TRANSLUCENT;   //设置透明背景
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;   //设置控件的类型是toast类型,TYPE_TOAST:toast是天生没有触摸事件
		
        
        //设置toast的位置
        params.gravity = Gravity.LEFT | Gravity.TOP;// | 表示+，表示两种效果同时实现
        //100 像素
        params.x = 100;//距离窗口边框的距离，根据gravity属性来设置的，如果是left距离左边框距离，如果right距离有边框的距离,是坐标
        params.y = 100;//根据x一样的含义，根据gravity属性设置距离上下边框的距离
        
        
        setTouch();
        
		windowManager.addView(view, params);
	}
	/**
	 * 随着手指移动而移动
	 */
	private void setTouch() {
		view.setOnTouchListener(new OnTouchListener() {
			private int startX;
			private int startY;

			//v : 表示的触摸的控件
			//event : 触摸事件
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//event.getAction() : 获取控件触摸事件动画
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					//按下
					//1.记录手指按下的开始的坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					//移动
					//2.记录新的位置的坐标
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					//3.计算偏移量
					int dX = newX-startX;
					int dY = newY-startY;
					//4.将控件移动相应的距离，更改控件的位置
					params.x+=dX;
					params.y+=dY;
					//更新控件的位置
					//updateViewLayout : 更新控件的操作，params：控件设置的最新属性
					windowManager.updateViewLayout(view, params);
					//5.更新开始的位置
					startX = newX;
					startY = newY;
					break;
				case MotionEvent.ACTION_UP:
					//抬起
					break;
				}
				return false;
			}
		});
	}
	/**
	 * 隐藏toast
	 */
	public void hideToast() {
		if (windowManager != null && view != null) {
			windowManager.removeView(view);
			//为下一次显示做准备
			windowManager=null;
			view = null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
