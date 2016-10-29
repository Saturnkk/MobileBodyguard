package com.le.safe.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dell.myapplication.R;
import com.le.safe.receiver.AdminReceiver;


public class SetUp4Activity extends SetUpBaseActivity {

	protected static final int REQUEST_CODE_ENABLE_ADMIN = 20;
	private RelativeLayout mAdmin;
	private ComponentName componentName;
	private DevicePolicyManager devicePolicyManager;
	private ImageView mIsActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		initView();
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		mAdmin = (RelativeLayout) findViewById(R.id.rel_setup4_admin);
		mIsActivity = (ImageView) findViewById(R.id.iv_setup4_isactivity);
		
		devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		componentName = new ComponentName(this, AdminReceiver.class);
		
		//4.回显操作
		if (devicePolicyManager.isAdminActive(componentName)) {
			mIsActivity.setImageResource(R.drawable.admin_activated);
		}else{
			mIsActivity.setImageResource(R.drawable.admin_inactivated);
		}
		
		
		mAdmin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//激活 -> 取消激活   取消激活 -> 激活
				//判断超级管理员是否激活
				if (devicePolicyManager.isAdminActive(componentName)) {
					//3.取消激活
					//取消密码
					devicePolicyManager.resetPassword("", 0);
					//取消激活
					devicePolicyManager.removeActiveAdmin(componentName);
					//设置图标
					mIsActivity.setImageResource(R.drawable.admin_inactivated);
				}else{
					//激活
					 //1.打开系统的激活设备管理器的界面
					 //表示打开一个超级管理员
					 Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					 //打开哪一个超级管理员
					 //参数2：组件的标示
	                 intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
	                 //添加描述信息
	                 intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"传智播客手机安全卫士");
	                 startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//2.判断超级管理员是否激活，激活设置激活的图标，没有设置没有的图标
		if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
			if (devicePolicyManager.isAdminActive(componentName)) {
				//激活
				mIsActivity.setImageResource(R.drawable.admin_activated);
			}else{
				//没有激活
				mIsActivity.setImageResource(R.drawable.admin_inactivated);
			}
		}
	}
	
	@Override
	public boolean next_activity() {
		
		//5.判断是否激活超级管理员，激活跳转，没有，提醒用户
		if (!devicePolicyManager.isAdminActive(componentName)) {
			Toast.makeText(getApplicationContext(), "请先激活超级管理员", Toast.LENGTH_SHORT).show();
			return true;
		}
		
		Intent intent = new Intent(this,SetUp5Activity.class);
		startActivity(intent);
		return false;
	}

	@Override
	public boolean pre_activity() {
		Intent intent = new Intent(this,SetUp3Activity.class);
		startActivity(intent);
		return false;
	}
	
}
