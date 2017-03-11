package com.le.safe.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.dell.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.le.safe.engine.SMSEngine;
import com.le.safe.service.WatchDog1Service;
import com.le.safe.ui.SettingView;
import com.le.safe.utils.ServiceUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;


/**
 * 常用工具
 */
public class CommonToolsActivity extends BaseActivity implements OnClickListener {

	private SettingView mAddress;
	private SettingView mCommonnumber;
	private SettingView mSMSBackup;
	private SettingView mSMSRecovery;
	private SettingView mAppLock;
	private SettingView mWatchDog1;
	private SettingView mWatchDog2;

	@Override
	protected View createXMLView() {
		View view = View.inflate(this,R.layout.activity_commontools,null);
		return view;
	}

	@Override
	protected void initData() {
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mAddress = (SettingView) findViewById(R.id.sv_commontools_address);
		mCommonnumber = (SettingView) findViewById(R.id.sv_commontools_commonnumber);
		mSMSBackup = (SettingView) findViewById(R.id.sv_commontools_smsbackup);
		mSMSRecovery = (SettingView) findViewById(R.id.sv_commontools_smsrecovery);
		mAppLock = (SettingView) findViewById(R.id.sv_commontools_applock);
		mWatchDog1 = (SettingView) findViewById(R.id.sv_commontools_watchdog1);
		mWatchDog2 = (SettingView) findViewById(R.id.sv_commontools_watchdog2);

		mAddress.setOnClickListener(this);
		mCommonnumber.setOnClickListener(this);
		mSMSBackup.setOnClickListener(this);
		mSMSRecovery.setOnClickListener(this);
		mAppLock.setOnClickListener(this);
		mWatchDog1.setOnClickListener(this);
		mWatchDog2.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sv_commontools_address:
			// 跳转到号码归属地查询界面
			startActivity(new Intent(CommonToolsActivity.this,
					AddressActivity.class));
			break;
		case R.id.sv_commontools_commonnumber:
			startActivity(new Intent(CommonToolsActivity.this,
					CommonNumberActivity.class));
			break;
		case R.id.sv_commontools_smsbackup:
			final ProgressDialog progressDialog = new ProgressDialog(
					CommonToolsActivity.this);
			progressDialog.setCancelable(false);// 设置对话框不能取消
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.show();
			// 短信备份
			new Thread() {
				public void run() {
					SMSEngine
							.getAllSMS(getApplicationContext(), progressDialog);
					// progressbar或者progressDialog都是可以在子线程中更新UI
					progressDialog.dismiss();
				};
			}.start();
			break;
		case R.id.sv_commontools_smsrecovery:
			// 短信还原
			try {
				BufferedReader br = new BufferedReader(new FileReader(new File(
						"mnt/sdcard/sms.txt")));
				String json = br.readLine();
				// 将json字符串转化成list集合
				Gson gson = new Gson();
				// typeOfT : 转化的类型,new
				// TypeToken<List<SMSinfo>>(){}.getType()提示不出来，List<SMSinfo>：表示将json字符串转化成的对象
				List<SMSEngine.SMSinfo> list = gson.fromJson(json,
						new TypeToken<List<SMSEngine.SMSinfo>>() {
						}.getType());
				for (SMSEngine.SMSinfo smSinfo : list) {
					// 添加到系统的数据库中
					ContentResolver resolver = getContentResolver();
					Uri uri = Uri.parse("content://sms");
					ContentValues values = new ContentValues();
					values.put("address", smSinfo.address);
					values.put("date", smSinfo.date);
					values.put("type", smSinfo.type);
					values.put("body", smSinfo.body);
					resolver.insert(uri, values);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.sv_commontools_applock:
			// 跳转到程序锁界面
			startActivity(new Intent(this, AppLockActivity.class));
			break;
		case R.id.sv_commontools_watchdog1:
			// 开启和关闭监听服务

			Intent intent = new Intent(CommonToolsActivity.this,
					WatchDog1Service.class);
			// 开启服务 -> 关闭服务 关闭服务 -> 开启服务
			// 动态获取服务是否开启操作
			if (ServiceUtil.isServiceRunning(getApplicationContext(),
					"cn.itcast.mobliesafexian05.service.WatchDog1Service")) {
				// 开启服务 -> 关闭服务
				stopService(intent);
			} else {
				// 关闭服务 -> 开启服务
				startService(intent);
			}
			mWatchDog1.toggle();

			break;
		case R.id.sv_commontools_watchdog2:
			//跳转到系统界面
			/**
			 * START
			 * {
			 * act=android.settings.ACCESSIBILITY_SETTINGS 
			 * cmp=com.android.settings/.Settings$AccessibilitySettingsActivity u=0
			 * } from pid 2334
			 */
			Intent accessibiltyIntent =new Intent();
			accessibiltyIntent.setAction("android.settings.ACCESSIBILITY_SETTINGS");
			startActivity(accessibiltyIntent);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 回显操作
		if (ServiceUtil.isServiceRunning(getApplicationContext(),
				"cn.itcast.mobliesafexian05.service.WatchDog1Service")) {
			// 开启服务
			mWatchDog1.setToggle(true);
		} else {
			// 关闭服务
			mWatchDog1.setToggle(false);
		}
		if (ServiceUtil.isServiceRunning(getApplicationContext(),
				"cn.itcast.mobliesafexian05.service.WatchDog2Service")) {
			// 开启服务
			mWatchDog2.setToggle(true);
		} else {
			// 关闭服务
			mWatchDog2.setToggle(false);
		}
	}

}
