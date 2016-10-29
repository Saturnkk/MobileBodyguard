package com.le.safe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.example.dell.myapplication.R;
import com.le.safe.utils.Contants;
import com.le.safe.utils.SharedPreferencesUtil;


public class SetUp5Activity extends SetUpBaseActivity {

	private CheckBox mProtected;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup5);
		initView();
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		mProtected = (CheckBox) findViewById(R.id.cb_setup5_protected);
		
		//3.初始化界面，进行回显操作
		//获取保存的防盗保护是否开启的状态
		boolean isProtected = SharedPreferencesUtil.getBoolean(getApplicationContext(), Contants.PROTECTED, false);
		/*if (isProtected) {
			//setChecked : 设置checkbox是否选中
			mProtected.setChecked(true);
		}else{
			mProtected.setChecked(false);
		}*/
		mProtected.setChecked(isProtected);
		
		
		//1.设置checkbox监听操作,checkbox选中的监听
		mProtected.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			//是checkbox选中或者取消执行方法
			//buttonView : checkbox
			//isChecked : 选中状态
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//2.保存选中的状态，方便回显操作
				SharedPreferencesUtil.saveBoolean(getApplicationContext(), Contants.PROTECTED, isChecked);
			}
		});
	}
	@Override
	public boolean next_activity() {
		
		//4.判断是否开启防盗保护，如果开启了，进行跳转操作，如果没有开启，不允许跳转
		//判断checkbox是否选中
		//isChecked : 获取checkbox的状态
		if (!mProtected.isChecked()) {
			Toast.makeText(getApplicationContext(), "请先开启防盗保护", Toast.LENGTH_SHORT).show();
			return true;
		}
		//设置用户不是第一次进入手机卫士，再次进入的时候，跳转到手机防盗页面
		SharedPreferencesUtil.saveBoolean(getApplicationContext(), Contants.ISFIRSTENTER, false);
		//跳转到手机防盗页面
		Intent intent = new Intent(this,LostFindActivity.class);
		startActivity(intent);
		return false;
	}
	@Override
	public boolean pre_activity() {
		Intent intent = new Intent(this,SetUp4Activity.class);
		startActivity(intent);
		return false;
	}
	
}
