package com.le.safe.activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.myapplication.R;
import com.le.safe.bean.HomeItemInfo;
import com.le.safe.utils.Contants;
import com.le.safe.utils.MD5Uitl;
import com.le.safe.utils.SharedPreferencesUtil;


import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements OnClickListener,
		OnItemClickListener {

	private ImageView mLogo;
	private TextView mMsg;
	private ImageView mSetting;
	private GridView mGridView;

	private final String[] TITLES = new String[] { "手机防盗", "骚扰拦截", "软件管家",
			"进程管理", "流量统计", "手机杀毒", "缓存清理", "常用工具" };
	private final String[] DESCS = new String[] { "远程定位手机", "全面拦截骚扰", "管理您的软件",
			"管理运行进程", "流量一目了然", "病毒无处藏身", "系统快如火箭", "工具大全" };
	private final int[] ICONS = new int[] { R.drawable.sjfd, R.drawable.srlj,
			R.drawable.rjgj, R.drawable.jcgl, R.drawable.lltj, R.drawable.sjsd,
			R.drawable.hcql, R.drawable.cygj };

	private List<HomeItemInfo> mList;
	private AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);// 加载布局


		//int i = 3/0;
		
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mLogo = (ImageView) findViewById(R.id.iv_home_logo);
		mMsg = (TextView) findViewById(R.id.tv_home_msg);
		mSetting = (ImageView) findViewById(R.id.iv_home_setting);
		mGridView = (GridView) findViewById(R.id.gv_home_gridview);

		// 1.设置logo旋转动画
		setLogoAnimation();
		// 获取数据
		fillData();
		mSetting.setOnClickListener(this);
		mGridView.setOnItemClickListener(this);
	}

	/**
	 * 获取数据
	 */
	private void fillData() {
		mList = new ArrayList<HomeItemInfo>();
		for (int i = 0; i < ICONS.length; i++) {
			// 保存到bean类中
			HomeItemInfo homeItemInfo = new HomeItemInfo();
			homeItemInfo.title = TITLES[i];
			homeItemInfo.desc = DESCS[i];
			homeItemInfo.iconId = ICONS[i];
			// 保存到集合中
			mList.add(homeItemInfo);
		}
		// 给gridview设置adapter显示
		mGridView.setAdapter(new Myadapter());
	}

	/**
	 * 设置logo执行旋转动画
	 */
	private void setLogoAnimation() {
		/**
		 * 面试 1.帧动画 2.补间动画 3.属性动画 3.0以后
		 */
		// target : 执行动画的控件
		// propertyName : 动画执行的方法的标示
		// values : 执行动画的属性的值
		// mLogo.setRotation(rotation)
		// setRotation : z setRotationX : x setRotationY : Y
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mLogo,
				"rotationY", 0f, 90f, 270f, 360f);
		objectAnimator.setDuration(2000);
		// INFINITE : 设置动画一致旋转
		objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
		// 动画执行的模式
		// RESTART : 动画执行完，重新执行
		// REVERSE : 动画执行完，从结束的位置开始逆向旋转
		objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
		// 开始动画
		objectAnimator.start();

	}

	private class Myadapter extends BaseAdapter {
		// 获取条目的个数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		// 根据条目的位置获取条目的对应的数据
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		// 获取条目的id
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		// 设置条目的样式
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 将布局布局文件转化成了view对象
			View view = View.inflate(HomeActivity.this,
					R.layout.home_gridview_item, null);
			// view.findViewById : 表示的去home_gridview_item布局文件中找控件
			// findViewById : 是去activity_home布局文件找控件
			ImageView mItemIcon = (ImageView) view
					.findViewById(R.id.iv_homegridviewitem_icon);
			TextView mItemTitle = (TextView) view
					.findViewById(R.id.tv_homegridviewitem_title);
			TextView mItemDesc = (TextView) view
					.findViewById(R.id.tv_homegridviewitem_desc);

			// 填充数据
			// 1.获取数据，获取bean类
			HomeItemInfo homeItemInfo = mList.get(position);
			// 2.设置显示数据
			mItemIcon.setImageResource(homeItemInfo.iconId);
			mItemTitle.setText(homeItemInfo.title);
			mItemDesc.setText(homeItemInfo.desc);

			return view;
		}

	}

	@Override
	public void onClick(View v) {
		// getId() : 获取点击控件的id
		switch (v.getId()) {
		case R.id.iv_home_setting:
			// 跳转到设置中心界面
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

	// view : 点击条目的view对象
	// position : 点击条目的位置  从0开始   0-7
	// id : 点击条目的id
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 根据点击条目的位置，判断点击是哪个条目
		switch (position) {
		case 0:
			//手机防盗
			//弹出设置密码对话框
			//如果已经设置过密码，再次点击弹出输入密码对话框
			String psw = SharedPreferencesUtil.getString(getApplicationContext(), Contants.PASSWORD, "");
			if (TextUtils.isEmpty(psw)) {// null    ""
				showSetPassWordDialog();
			}else{
				showEnterPassWordDialog();
			}
			break;
		case 1:
			//骚扰拦截
			startActivity(new Intent(HomeActivity.this,BlackNumberActivity.class));
			break;
		case 2:
			//软件管家
			startActivity(new Intent(HomeActivity.this,AppManagerActivity.class));
			break;
		case 3:
			//进程管理
			startActivity(new Intent(HomeActivity.this,ProcessManagerActivity.class));
			break;
		case 4:
			//流量统计
			startActivity(new Intent(HomeActivity.this,TrafficManagerActivity.class));
			break;
		case 5:
			//手机杀毒
			startActivity(new Intent(HomeActivity.this,AntivirusActivity.class));
			break;
		case 6:
			//缓存清理
			startActivity(new Intent(HomeActivity.this,ClearCacheActivity.class));
			break;
		case 7:
			//常用工具
			startActivity(new Intent(HomeActivity.this,CommonToolsActivity.class));
			break;
		}
	}
	/**
	 * 输入密码对话框
	 */
	private void showEnterPassWordDialog() {
		//复制一
		//dialog第二中显示方式，将布局文件转化成view对象，设置给dialog显示
		Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.home_enterpassword_dialog, null);
		//复制三
		//初始化控件
		final EditText mPsw = (EditText) view.findViewById(R.id.et_setpassword_psw);
		Button mOk = (Button) view.findViewById(R.id.btn_setpassword_ok);
		Button mCancle = (Button) view.findViewById(R.id.btn_setpassword_cancle);

		//复制四
		mOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//判断输入的密码是正确
				//1.获取输入的密码
				String psw = mPsw.getText().toString().trim();
				//2.判断密码是否为空
				if (TextUtils.isEmpty(psw)) {
					Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				//3.获取设置保存的密码
				String sp_psw = SharedPreferencesUtil.getString(getApplicationContext(), Contants.PASSWORD, "");
				//4.判断输入的密码和保存的密码是否一致
				if (MD5Uitl.passwordToMD5(psw).equals(sp_psw)) {
					//隐藏对话框
					dialog.dismiss();
					//弹出提醒信息
					Toast.makeText(getApplicationContext(), "密码正确", Toast.LENGTH_SHORT).show();
					//跳转界面
					enterLostFind();
				}else{
					Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
				}
			}
		});
		mCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//关闭对话框
				dialog.dismiss();
			}
		});

		//复制二
		builder.setView(view);

		//builder.show();
		dialog = builder.create();
		dialog.show();
	}
	/**
	 * 跳转手机防盗界面
	 */
	protected void enterLostFind() {
		//判断如果用户第一次使用，进入手机防盗设置向导界面
		//如果用户不是第一次使用，进入手机防盗界面
		//获取用户是否是第一次进入，保存的操作是在手机防盗设置向导界面的第五个界面进行实现的
		boolean isFirstEnter = SharedPreferencesUtil.getBoolean(getApplicationContext(), Contants.ISFIRSTENTER, true);
		if (isFirstEnter) {
			//第一次进入，跳转到手机防盗设置向导界面
			Intent intent = new Intent(this,SetUp1Activity.class);
			startActivity(intent);
		}else{
			//不是第一次进入，跳转到手机防盗界面
			Intent intent = new Intent(this,LostFindActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * 设置密码对话框
	 */
	private void showSetPassWordDialog() {
		//dialog第二中显示方式，将布局文件转化成view对象，设置给dialog显示
		Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.home_setpassword_dialog, null);
		
		//初始化控件
		final EditText mPsw = (EditText) view.findViewById(R.id.et_setpassword_psw);
		final EditText mConfirm = (EditText) view.findViewById(R.id.et_setpassword_confirm);
		Button mOk = (Button) view.findViewById(R.id.btn_setpassword_ok);
		Button mCancle = (Button) view.findViewById(R.id.btn_setpassword_cancle);
		
		
		//实现点击事件
		mOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//保存设置的密码
				//1.获取输入密码
				String psw = mPsw.getText().toString().trim();
				//2.判断输入的密码是否为空
				if (TextUtils.isEmpty(psw)) {
					Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				//3.获取输入的确认密码
				String confirm = mConfirm.getText().toString().trim();
				//4.判断密码是否一致
				if (psw.equals(confirm)) {
					//保存密码
					//SharedPreferences
					SharedPreferencesUtil.saveString(getApplicationContext(), Contants.PASSWORD, MD5Uitl.passwordToMD5(psw));
					Toast.makeText(getApplicationContext(), "密码设置成功", Toast.LENGTH_SHORT).show();
					//隐藏对话框
					dialog.dismiss();
				}else{
					//提醒用户
					Toast.makeText(getApplicationContext(), "两次密码不一致", Toast.LENGTH_SHORT).show();
				}
			}
		});
		mCancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//关闭对话框
				dialog.dismiss();
			}
		});
		builder.setView(view);
		
		//builder.show();
		dialog = builder.create();
		dialog.show();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
