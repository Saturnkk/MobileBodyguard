package com.le.safe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.myapplication.R;
import com.le.safe.service.AddressService;
import com.le.safe.service.BlackNumberService;
import com.le.safe.ui.AddressDialog;
import com.le.safe.ui.SettingView;
import com.le.safe.utils.Contants;
import com.le.safe.utils.ServiceUtil;
import com.le.safe.utils.SharedPreferencesUtil;



public class SettingActivity extends BaseActivity {

	private SettingView mUpdate;
	private SettingView mBlackNumber;
	private SettingView mAddress;
	private SettingView mChangebg;

	@Override
	protected View createXMLView() {
		View view = View.inflate(this,R.layout.activity_setting,null);
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
		// SettingView mUpdate = new SettingView()
		mUpdate = (SettingView) findViewById(R.id.sv_setting_update);
		mBlackNumber = (SettingView) findViewById(R.id.sv_setting_blacknumber);
		mAddress = (SettingView) findViewById(R.id.sv_setting_address);
		mChangebg = (SettingView) findViewById(R.id.sv_setting_changebg);

		// 设置条目的点击事件
		update();
		blacknumber();
		address();
		changebg();
	}

	/**
	 * 归属地风格设置
	 */
	private void changebg() {
		mChangebg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//弹出自定义的dialog
				final AddressDialog addressDialog = new AddressDialog(SettingActivity.this);
				addressDialog.show();
				addressDialog.setadapter(new Myadapter());
				addressDialog.setItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						//隐藏对话框
						addressDialog.dismiss();
						//保存选中条目所代表的背景
						SharedPreferencesUtil.saveInt(getApplicationContext(), Contants.ADDRESSDIALOG_BG, mBgIcons[position]);
					}
				});
			}
		});
	}

	/**
	 * 归属地显示设置
	 */
	private void address() {
		mAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this,
						AddressService.class);
				// 开启服务 -> 关闭服务 关闭服务 -> 开启服务
				// 动态获取服务是否开启操作
				if (ServiceUtil.isServiceRunning(getApplicationContext(),
						"cn.itcast.mobliesafexian05.service.AddressService")) {
					// 开启服务 -> 关闭服务
					stopService(intent);
				} else {
					// 关闭服务 -> 开启服务
					startService(intent);
				}
				mAddress.toggle();
			}
		});
	}

	// 界面可见的时候调用的方法
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// 回显操作
		if (ServiceUtil.isServiceRunning(getApplicationContext(),
				"cn.itcast.mobliesafexian05.service.BlackNumberService")) {
			// 开启服务
			mBlackNumber.setToggle(true);
		} else {
			// 关闭服务
			mBlackNumber.setToggle(false);
		}
		// 归属地设置
		if (ServiceUtil.isServiceRunning(getApplicationContext(),
				"cn.itcast.mobliesafexian05.service.AddressService")) {
			// 开启服务
			mAddress.setToggle(true);
		} else {
			// 关闭服务
			mAddress.setToggle(false);
		}
	}

	// 界面不可见的时候调用的方法
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	/**
	 * 骚扰拦截
	 */
	private void blacknumber() {

		mBlackNumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this,
						BlackNumberService.class);
				// 开启服务 -> 关闭服务 关闭服务 -> 开启服务
				// 动态获取服务是否开启操作
				if (ServiceUtil
						.isServiceRunning(getApplicationContext(),
								"cn.itcast.mobliesafexian05.service.BlackNumberService")) {
					// 开启服务 -> 关闭服务
					stopService(intent);
				} else {
					// 关闭服务 -> 开启服务
					startService(intent);
				}
				mBlackNumber.toggle();
			}
		});
	}

	/**
	 * 设置自动更新条目的点击事件
	 */
	private void update() {
		// 初始化按钮的样式操作
		// 1.获取保存的按钮样式的值
		boolean mIsUpdate = SharedPreferencesUtil.getBoolean(
				SettingActivity.this, Contants.UPDATE, true);
		// 2.根据保存的值设置按钮的样式
		if (mIsUpdate) {
			// 打开
			mUpdate.setToggle(true);
		} else {
			// 关闭
			mUpdate.setToggle(false);
		}
		mUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 更改自定义控件中的按钮的图标
				// 但是按钮是在自定义控件中的布局中的
				// 打开 -> 关闭 关闭 -> 打开
				// 如果想改变自定义控件中按钮的样式，首先要先获取按钮的状态，如果是打开，变成关闭，如果是关闭，变成打开
				// mUpdate.setToggle(true);
				/*
				 * if (mUpdate.getToggle()) { //打开 -> 关闭
				 * mUpdate.setToggle(false); }else{ //关闭 -> 打开
				 * mUpdate.setToggle(true); }
				 */
				mUpdate.toggle();
				// 保存状态
				SharedPreferencesUtil.saveBoolean(SettingActivity.this,
						Contants.UPDATE, mUpdate.getToggle());
			}
		});
	}

	private String[] mBgNames = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰",
			"帽子绿" };
	private int[] mBgIcons = new int[] {
			R.drawable.shape_customdialog_bg_normal,
			R.drawable.shape_customdialog_bg_orange,
			R.drawable.shape_customdialog_bg_blue,
			R.drawable.shape_customdialog_bg_gray,
			R.drawable.shape_customdialog_bg_green };

	/**
	 * 自定义dialog的adapter
	 *
	 * @author Administrator
	 *
	 */
	private class Myadapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mBgIcons.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mBgIcons[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = View.inflate(getApplicationContext(), R.layout.customdialog_item, null);
			ImageView mIcon = (ImageView) convertView.findViewById(R.id.iv_customdialogitem_icon);
			TextView mBgName = (TextView) convertView.findViewById(R.id.tv_customdialogitem_bgname);
			ImageView mIsSelect = (ImageView) convertView.findViewById(R.id.iv_customdialogitem_isselect);

			//设置显示数据
			mIcon.setImageResource(mBgIcons[position]);
			mBgName.setText(mBgNames[position]);

			//获取保存的背景,进行回显操作
			int mBgId = SharedPreferencesUtil.getInt(getApplicationContext(), Contants.ADDRESSDIALOG_BG, R.drawable.shape_customdialog_bg_normal);
			if (mBgId==mBgIcons[position]) {
				mIsSelect.setVisibility(View.VISIBLE);
			}else{
				mIsSelect.setVisibility(View.GONE);
			}

			return convertView;
		}

	}























}
