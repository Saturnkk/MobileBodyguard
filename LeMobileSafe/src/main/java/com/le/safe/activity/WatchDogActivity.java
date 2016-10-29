package com.le.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.myapplication.R;

public class WatchDogActivity extends AppCompatActivity {

	private ImageView mIcon;
	private TextView mName;
	private EditText mPassWord;
	private String packagename;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watchdog);
		initView();
		fillData();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mIcon = (ImageView) findViewById(R.id.iv_watchdog_icon);
		mName = (TextView) findViewById(R.id.tv_watchdog_name);
		mPassWord = (EditText) findViewById(R.id.et_watchdog_password);
	}

	// 设置显示数据
	private void fillData() {
		Intent intent = getIntent();
		packagename = intent.getStringExtra("packageName");
		//获取加锁应用程序的图标和名称
		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo applicationInfo = pm.getApplicationInfo(packagename, 0);
			//图标
			Drawable icon = applicationInfo.loadIcon(pm);
			String name = applicationInfo.loadLabel(pm).toString();
			mIcon.setImageDrawable(icon);
			mName.setText(name);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//跳转到主界面
			/**  
			 * START {
			 * act=android.intent.action.MAIN   action
			 * cat=[android.intent.category.HOME]   category
			 * cmp=com.android.launcher/com.android.launcher2.Launcher u=0
			 * } from pid 1285
			 */
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.HOME");
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 解锁操作
	 * @param v
	 */
	public void unlock(View v){
		//获取输入的密码
		String password = mPassWord.getText().toString().trim();
		if ("123".equals(password)) {
			//发送给服务一个自定义的广播，在服务中注册一个广播接受者接受自定义的广播，来获取传递的数据
			Intent intent = new Intent();
			intent.setAction("cn.itcast.mobliesafexian05.watchdog.unlock");//设置一个自定义的广播事件
			intent.putExtra("packagename", packagename);
			sendBroadcast(intent);//发送一个自定义广播
			finish();
		}else{
			Toast.makeText(getApplicationContext(), "密码错误，请重新输入...", 0).show();
		}
	}
	
	
	
	
	

}
