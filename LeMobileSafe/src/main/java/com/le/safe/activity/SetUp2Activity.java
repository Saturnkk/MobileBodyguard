package com.le.safe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dell.myapplication.R;
import com.le.safe.utils.Contants;
import com.le.safe.utils.SharedPreferencesUtil;


public class SetUp2Activity extends SetUpBaseActivity {

	private RelativeLayout mSim;
	private ImageView mSimIcon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		initView();
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		mSim = (RelativeLayout) findViewById(R.id.rla_setup2_sim);
		mSimIcon = (ImageView) findViewById(R.id.iv_setup2_simicon);
		
		//在进入界面的时候，进行绑定/解绑的初始化操作
		String sp_sim = SharedPreferencesUtil.getString(getApplicationContext(), Contants.SIM, "");
		if (TextUtils.isEmpty(sp_sim)) {
			//没有绑定
			mSimIcon.setImageResource(R.drawable.unlock);
		}else{
			//绑定
			mSimIcon.setImageResource(R.drawable.lock);
		}
		
		mSim.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//绑定和解绑sim卡
				//绑定 -> 解绑   解绑 ->绑定
				String sp_sim = SharedPreferencesUtil.getString(getApplicationContext(), Contants.SIM, "");
				//如果sp_sim为空，表示没有绑定，执行绑定操作，如果有，解绑操作
				if (TextUtils.isEmpty(sp_sim)) {
					//绑定SIM卡：将SIM卡的序列号保存sp中
					//获取SIM卡的序列号
					TelephonyManager tel = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					//获取SIM卡绑定的手机号，Line1：多卡多待，在中国不太好用，运营商一般不会将手机号和SIM卡绑定
					//String line1Number = tel.getLine1Number();
					String sim = tel.getSimSerialNumber();//获取SIM卡的序列号,sim卡唯一标示
					SharedPreferencesUtil.saveString(getApplicationContext(), Contants.SIM, sim);
					//改变图标
					mSimIcon.setImageResource(R.drawable.lock);
				}else{
					//解绑操作
					SharedPreferencesUtil.saveString(getApplicationContext(), Contants.SIM, "");
					mSimIcon.setImageResource(R.drawable.unlock);
				}
				
			}
		});
	}
	@Override
	public boolean next_activity() {
		
		//判断SIM卡是否绑定，绑定才能进行跳转操作
		String sp_sim = SharedPreferencesUtil.getString(getApplicationContext(), Contants.SIM, "");
		if (TextUtils.isEmpty(sp_sim)) {
			Toast.makeText(getApplicationContext(), "请先绑定SIM卡...", 0).show();
			return true;
		}
		Intent intent = new Intent(this,SetUp3Activity.class);
		startActivity(intent);
		return false;
	}
	@Override
	public boolean pre_activity() {
		Intent intent = new Intent(this,SetUp1Activity.class);
		startActivity(intent);
		return false;
	}
	
}
