package com.le.safe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.myapplication.R;
import com.le.safe.db.dao.AddressDao;

/**
 * 地址
 */
public class AddressActivity extends BaseActivity {
	
	private EditText mPhone;
	private TextView mLocation;


	@Override
	protected View createXMLView() {
		View view = View.inflate(this,R.layout.activity_address,null);
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
		mPhone = (EditText) findViewById(R.id.et_address_phone);
		mLocation = (TextView) findViewById(R.id.tv_address_location);
		
		//即时显示效果
		//监听输入框文本变化的监听事件
		mPhone.addTextChangedListener(new TextWatcher() {
			//当文本变化的时候调用的方法
			//只要输入文本就会调用
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//获取输入的文本
				String number = s.toString();
				String address = AddressDao.getAddress(getApplicationContext(), number);
				if (!TextUtils.isEmpty(address)) {
					mLocation.setText("归属地:"+address);
				}
			}
			//变化之前
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			//变化之后
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	/**
	 * 查询的点击事件
	 * @param v
	 */
	public void address(View v){
		//获取输入的查询的号码
		String number = mPhone.getText().toString().trim();
		if (!TextUtils.isEmpty(number)) {
			//查询号码归属地
			String address = AddressDao.getAddress(getApplicationContext(), number);
			if (!TextUtils.isEmpty(address)) {
				mLocation.setText("归属地:"+address);
			}
		}else{
			//抖动动画的实现
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			mPhone.startAnimation(shake);
			Toast.makeText(getApplicationContext(), "请输入要查询的号码",Toast.LENGTH_SHORT).show();
		}
	}
}
