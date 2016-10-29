package com.le.safe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dell.myapplication.R;
import com.le.safe.utils.Contants;
import com.le.safe.utils.SharedPreferencesUtil;


public class SetUp3Activity extends SetUpBaseActivity {

	private static final int GETCONTACTSCODE = 10;
	private EditText mSafeNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		initView();
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		mSafeNumber = (EditText) findViewById(R.id.et_setup3_safenumber);
		
		//初始化界面，回显设置的安全号码
		String sp_safenumber = SharedPreferencesUtil.getString(getApplicationContext(), Contants.SAFENUMBER, "");
		if (!TextUtils.isEmpty(sp_safenumber)) {
			mSafeNumber.setText(sp_safenumber);
		}
	}

	@Override
	public boolean next_activity() {
		
		//获取输入的安全号码，判断安全号码是否为空，为空提醒用户，不为空，保存安全号码
		//获取安全号码
		String safenumber = mSafeNumber.getText().toString().trim();
		//判断安全号码是否为空
		if(TextUtils.isEmpty(safenumber)){
			Toast.makeText(getApplicationContext(), "请输入安全号码", Toast.LENGTH_SHORT).show();
			return true;
		}
		//保存安全号码
		SharedPreferencesUtil.saveString(getApplicationContext(), Contants.SAFENUMBER, safenumber);
		
		Intent intent = new Intent(this,SetUp4Activity.class);
		startActivity(intent);
		return false;
	}

	@Override
	public boolean pre_activity() {
		Intent intent = new Intent(this,SetUp2Activity.class);
		startActivity(intent);
		return false;
	}
	/**
	 * 选择联系人按钮的点击事件
	 * @param v
	 */
	public void selectcontacts(View v){
		Intent intent = new Intent(this,ContactsActivity.class);
		//startActivity(intent);
		//当当前界面退出的时候，会调用之前界面的onActivityResult方法
		startActivityForResult(intent, GETCONTACTSCODE);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//获取contactsactivity传递过来的数据，并设置给安全号码输入框
		if (data!= null) {
			String number = data.getStringExtra("number");
			mSafeNumber.setText(number);
		}
	}
	
	
}
