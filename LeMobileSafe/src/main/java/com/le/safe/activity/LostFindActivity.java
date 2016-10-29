package com.le.safe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dell.myapplication.R;
import com.le.safe.utils.Contants;
import com.le.safe.utils.SharedPreferencesUtil;


public class LostFindActivity extends AppCompatActivity implements OnClickListener{

	private TextView mSetUp;
	private ImageView mProtected;
	private TextView mSafeNumber;
	private RelativeLayout mRelProtected;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lostfind);
		initView();
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		mSetUp = (TextView) findViewById(R.id.tv_lostfind_setup);
		mProtected = (ImageView) findViewById(R.id.iv_lostfind_protected);
		mSafeNumber = (TextView) findViewById(R.id.tv_lostfind_safenumber);
		mRelProtected = (RelativeLayout) findViewById(R.id.rel_lostfind_protected);
		mSetUp.setOnClickListener(this);
		
		//1.根据保存安全号码和防盗保护是否开启的状态设置手机防盗页面中的相应的数据
		setMessage();
		//2.快速实现开始或者关闭防盗保护状态
		mRelProtected.setOnClickListener(this);
	}
	/**
	 * 设置相应展示数据
	 */
	private void setMessage() {
		mSafeNumber.setText(SharedPreferencesUtil.getString(getApplicationContext(), Contants.SAFENUMBER, ""));
		//设置防盗保护是否开启操作
		boolean sp_protected = SharedPreferencesUtil.getBoolean(getApplicationContext(), Contants.PROTECTED, false);
		if (sp_protected) {
			//开启
			mProtected.setImageResource(R.drawable.lock);
		}else{
			//关闭
			mProtected.setImageResource(R.drawable.unlock);
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_lostfind_setup:
			//跳转到第一个设置向导界面,移出手机防盗界面
			Intent intent = new Intent(this,SetUp1Activity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.rel_lostfind_protected:
			//开启 -> 关闭       关闭 -> 开启
			//获取防盗保护是否开启的状态，如果是开启，关闭，如果是关闭，开启
			boolean b = SharedPreferencesUtil.getBoolean(getApplicationContext(), Contants.PROTECTED, false);
			if (b) {
				//关闭
				//重新保存状态
				SharedPreferencesUtil.saveBoolean(getApplicationContext(), Contants.PROTECTED, false);
				//更改图标
				mProtected.setImageResource(R.drawable.unlock);
			}else{
				//开启
				//重新保存状态
				SharedPreferencesUtil.saveBoolean(getApplicationContext(), Contants.PROTECTED, true);
				//更改图标
				mProtected.setImageResource(R.drawable.lock);
			}
			break;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
