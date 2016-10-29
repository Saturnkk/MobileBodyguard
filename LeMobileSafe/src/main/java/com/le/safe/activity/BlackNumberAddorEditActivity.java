package com.le.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.myapplication.R;
import com.le.safe.db.BlackNumberContants;
import com.le.safe.db.dao.BlackNumberDao;


public class BlackNumberAddorEditActivity extends AppCompatActivity implements OnClickListener{
	
	private EditText mAddorEdit;
	private RadioGroup mModes;
	private Button mOk;
	private Button mCancle;
	private BlackNumberDao blackNumberDao;
	private Intent intent;
	private int position;
	private TextView mTitle;
	private String action;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacknumberaddoredit);
		blackNumberDao = new BlackNumberDao(getApplicationContext());
		initView();
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		mAddorEdit = (EditText) findViewById(R.id.et_blacknumberaddoredit_addoredit);
		mModes = (RadioGroup) findViewById(R.id.rg_blacknumber_modes);
		mOk = (Button) findViewById(R.id.btn_blacknumberaddoredit_ok);
		mCancle = (Button) findViewById(R.id.btn_blacknumberaddoredit_cancle);
		mTitle = (TextView) findViewById(R.id.tv_blacknumberaddroedit_title);
		
		//流程衔接BlackNumerActivity.java中的updateBlackNumber方法
		//3.接受数据，更改标题，按钮的文本
		//3.1获取传递的数据，判断是更新还是添加操作
		intent = getIntent();
		action = intent.getAction();
		if ("update".equals(action)) {//"update".equals（）避免空指针异常
			//更新操作
			//3.2接受数据，更改标题，按钮的文本
			update();
		}else{
			//添加操作
		}
		
		
		
		
		
		mOk.setOnClickListener(this);
		mCancle.setOnClickListener(this);
	}
	/**
	 * 更新初始化操作
	 */
	private void update() {
		
		//3.2.1.接受数据
		String number = intent.getStringExtra("number");
		int mode = intent.getIntExtra("mode", 0);
		position = intent.getIntExtra("position", 0);
		
		//3.2.2更改标题，标题，按钮的文本
		mTitle.setText("更新黑名单");
		mOk.setText("更新");
		
		//4.回显黑名单号码和拦截类型
		mAddorEdit.setText(number);
		//设置控件是否可用，true:可用，false:不可用
		mAddorEdit.setEnabled(false);
		//回显拦截类型，拦截类型：0,1,2,但是回显的时候是设置RadioButton是否选中
		//选中哪个RadioButton，id:表示选中RadioButton的id
		//根据传递的拦截类型，设置相应的RadioButton的id
		//选中的RadioButton的id
		int checkid = -1;
		switch (mode) {
		case BlackNumberContants.BLACKNUMBER_CALL:
			//电话拦截
			checkid = R.id.rb_blacknumber_call;
			break;
		case BlackNumberContants.BLACKNUMBER_SMS:
			//短信拦截
			checkid = R.id.rb_blacknumber_sms;
			break;
		case BlackNumberContants.BLACKNUMBER_ALL:
			//全部拦截
			checkid = R.id.rb_blacknumber_all;
			break;
		default:
			checkid = R.id.rb_blacknumber_call;
			break;
		}
		mModes.check(checkid);
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_blacknumberaddoredit_ok:
			//添加操作
			//1.获取数据
			//1.1获取黑名单号码
			String number = mAddorEdit.getText().toString().trim();
			if (TextUtils.isEmpty(number)) {//null   ""
				Toast.makeText(getApplicationContext(), "请输入黑名单号码", 0).show();
				return;
			}
			//1.2获取拦截类型
			//获取选中的RadioButton的id,返回的是RadioButton的id
			int mode = -1;
			int checkedRadioButtonId = mModes.getCheckedRadioButtonId();
			switch (checkedRadioButtonId) {
			case R.id.rb_blacknumber_call:
				//电话拦截
				mode = BlackNumberContants.BLACKNUMBER_CALL;
				break;
			case R.id.rb_blacknumber_sms:
				//短信拦截
				mode = BlackNumberContants.BLACKNUMBER_SMS;
				break;
			case R.id.rb_blacknumber_all:
				//全部拦截
				mode = BlackNumberContants.BLACKNUMBER_ALL;
				break;
			default:
				Toast.makeText(getApplicationContext(), "请选择拦截模式", 0).show();
				return;
			}
			//2.判断是添加到数据库中还是更新操作
			if ("update".equals(action)) {
				//5.获取数据，更新数据库，数据库更新成功，跳转更新界面
				//更新操作
				boolean isupdate = blackNumberDao.updateBalckNumber(number, mode);
				if (isupdate) {
					//更新界面,数据传递操作
					Intent intent = new Intent();
					intent.putExtra("mode", mode);
					//告诉黑名单管理界面，更新的是哪个条目，以便黑名单管理界面更新相应的条目
					intent.putExtra("position", position);
					setResult(Activity.RESULT_OK, intent);
					//关闭界面
					finish();
				}else{
					Toast.makeText(getApplicationContext(), "系统繁忙，请稍后再试", 0).show();
				}
			}else{
				//添加
				//2.1判断数据库中是否存在要添加的黑名单号码
				//如果根据黑名单查询黑名单号码的拦截模式是-1，表示数据库中没有该黑名单号码
				if (blackNumberDao.queryBlackNumberMode(number) == -1) {
					//添加黑名单号码到数据库操作
					boolean isadd = blackNumberDao.addBlacknumber(number, mode);
					//如果添加到数据库添加成功，传递给黑名单管理界面展示，如果添加失败，提醒用户
					if (isadd) {
						//传递黑名单管理界面展示
						//3.传递给黑名单管理界面展示，两个activity之间的数据传递操作
						//3.1传递数据
						Intent intent = new Intent();
						intent.putExtra("number", number);
						intent.putExtra("mode", mode);
						setResult(Activity.RESULT_OK, intent);
						//3.2关闭界面
						finish();
					}else{
						Toast.makeText(getApplicationContext(), "系统繁忙，请稍后再试", 0).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "号码已添加", 0).show();
				}
			}
			break;
		case R.id.btn_blacknumberaddoredit_cancle:
			//关闭界面
			finish();
			break;
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
