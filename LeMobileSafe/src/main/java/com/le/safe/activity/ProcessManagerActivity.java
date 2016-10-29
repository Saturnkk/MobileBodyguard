package com.le.safe.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.myapplication.R;
import com.le.safe.bean.ProcessInfo;
import com.le.safe.engine.ProcessEngine;
import com.le.safe.service.LockScreenClearService;
import com.le.safe.ui.CustomProgressbar;
import com.le.safe.ui.SettingView;
import com.le.safe.utils.Contants;
import com.le.safe.utils.ServiceUtil;
import com.le.safe.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProcessManagerActivity extends AppCompatActivity {

	private CustomProgressbar mProcessCount;
	private CustomProgressbar mMemory;
	private List<ProcessInfo> processInfos;
	private List<ProcessInfo> userprocessInfos;
	private List<ProcessInfo> systemprocessInfos;
	private StickyListHeadersListView mProcess;
	private Myadapter myadapter;
	private int runningProcess;
	private int totalProcess;
	private int freeProcess;
	private SlidingDrawer mSllidingDrawer;
	private ImageView mDrawer1;
	private ImageView mDrawer2;
	private SettingView mIsShowSystem;
	private SettingView mLockScreenClear;
	/**
	 * 是否显示系统进程
	 */
	private boolean isshowsystem = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_processmanager);
		initView();
		setHeaderMessage();
		fillData();
		setListViewOnItemClickListener();
	}

	/**
	 * ListView的条目点击事件
	 */
	private void setListViewOnItemClickListener() {
		mProcess.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 获取数据
				ProcessInfo processInfo;
				if (position < userprocessInfos.size()) {
					// 从用户程序中获取数据
					processInfo = userprocessInfos.get(position);
				} else {
					processInfo = systemprocessInfos.get(position
							- userprocessInfos.size());
				}
				// 更新checkboxd状态
				// 选中 -> 取消选中 取消选中 -> 选中
				if (processInfo.isChecked) {
					processInfo.isChecked = false;
				} else {
					if (!processInfo.packageName.equals(getPackageName())) {
						processInfo.isChecked = true;
					}
				}
				myadapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * 获取进程信息操作
	 */
	private void fillData() {
		new Thread() {

			public void run() {
				processInfos = ProcessEngine
						.getRunningProcessInfo(getApplicationContext());
				// 1.将所有的应用程序，拆分成用户程序和系统程序吗，分别保存
				userprocessInfos = new ArrayList<ProcessInfo>();
				systemprocessInfos = new ArrayList<ProcessInfo>();
				for (ProcessInfo info : processInfos) {
					if (info.isSystem) {
						systemprocessInfos.add(info);
					} else {
						userprocessInfos.add(info);
					}
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						myadapter = new Myadapter();
						mProcess.setAdapter(myadapter);
					}
				});
			};
		}.start();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mProcessCount = (CustomProgressbar) findViewById(R.id.cp_processmanager_processcount);
		mMemory = (CustomProgressbar) findViewById(R.id.cp_processmanager_memory);
		mProcess = (StickyListHeadersListView) findViewById(R.id.lv_processmanager_process);
		// slidingmenu第三方框
		mSllidingDrawer = (SlidingDrawer) findViewById(R.id.sd_processmanger_slidingdrawer);
		mDrawer1 = (ImageView) findViewById(R.id.ll_processmanger_drawer1);
		mDrawer2 = (ImageView) findViewById(R.id.ll_processmanger_drawer2);
		mIsShowSystem = (SettingView) findViewById(R.id.sv_processmanager_isshowsystem);
		mLockScreenClear = (SettingView) findViewById(R.id.sv_processmanager_lockscreenclear);

		openAnimation();
		setSlidingDrawerListener();
		setIsShowSystemListener();
		setLockScreenClearListener();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if (ServiceUtil.isServiceRunning(getApplicationContext(),
				"cn.itcast.mobliesafexian05.service.LockScreenClearService")) {
			// 开启服务
			mLockScreenClear.setToggle(true);
		} else {
			// 关闭服务
			mLockScreenClear.setToggle(false);
		}
	}
	
	/**
	 * 锁屏自定清理点击事件
	 */
	private void setLockScreenClearListener() {
		mLockScreenClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ProcessManagerActivity.this,
						LockScreenClearService.class);
				// 开启服务 -> 关闭服务 关闭服务 -> 开启服务
				// 动态获取服务是否开启操作
				if (ServiceUtil
						.isServiceRunning(getApplicationContext(),
								"cn.itcast.mobliesafexian05.service.LockScreenClearService")) {
					// 开启服务 -> 关闭服务
					stopService(intent);
				} else {
					// 关闭服务 -> 开启服务
					startService(intent);
				}
				mLockScreenClear.toggle();
			}
		});
	}

	/**
	 * 显示系统进程监听操作
	 */
	private void setIsShowSystemListener() {

		// 1.获取保存的按钮样式的值
		boolean mIsUpdate = SharedPreferencesUtil.getBoolean(
				ProcessManagerActivity.this,
				Contants.PROCESSMANAGER_ISSHOWSYSTEM, true);
		// 2.根据保存的值设置按钮的样式
		if (mIsUpdate) {
			// 打开
			mIsShowSystem.setToggle(true);
			isshowsystem = true;
		} else {
			// 关闭
			mIsShowSystem.setToggle(false);
			isshowsystem = false;
		}

		mIsShowSystem.setOnClickListener(new OnClickListener() {

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
				mIsShowSystem.toggle();

				// 根据开关的状态设置显示系统进程的标示
				// 获取开关的状态
				boolean toggle = mIsShowSystem.getToggle();
				// 将开关的状态设置给显示系统进程表示
				isshowsystem = toggle;
				// 更新界面
				myadapter.notifyDataSetChanged();
				// 保存状态
				SharedPreferencesUtil.saveBoolean(ProcessManagerActivity.this,
						Contants.PROCESSMANAGER_ISSHOWSYSTEM,
						mIsShowSystem.getToggle());
			}
		});
	}

	/**
	 * 抽屉打开关闭的监听
	 */
	private void setSlidingDrawerListener() {
		// 监听抽屉打开的事件
		mSllidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				// 停止动画
				closeAnimation();
			}
		});
		// 抽屉关闭的监听
		mSllidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				// 开始动画
				openAnimation();
			}
		});
	}

	/**
	 * 关闭动画
	 */
	protected void closeAnimation() {
		mDrawer1.clearAnimation();// 清除动画
		mDrawer2.clearAnimation();

		// 更改图标
		mDrawer1.setImageResource(R.drawable.drawer_arrow_down);
		mDrawer2.setImageResource(R.drawable.drawer_arrow_down);
	}

	/**
	 * 渐变动画
	 */
	private void openAnimation() {

		// 将图标设置为默认向上图标
		mDrawer1.setImageResource(R.drawable.drawer_arrow_up);
		mDrawer2.setImageResource(R.drawable.drawer_arrow_up);

		// fromAlpha, toAlpha : 从透明到不透明/从不透明到透明
		AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.2f, 1.0f);
		alphaAnimation1.setDuration(500);
		alphaAnimation1.setRepeatCount(Animation.INFINITE);// 一直执行
		alphaAnimation1.setRepeatMode(Animation.REVERSE);
		mDrawer1.startAnimation(alphaAnimation1);

		AlphaAnimation alphaAnimation2 = new AlphaAnimation(1.0f, 0.2f);
		alphaAnimation2.setDuration(500);
		alphaAnimation2.setRepeatCount(Animation.INFINITE);// 一直执行
		alphaAnimation2.setRepeatMode(Animation.REVERSE);
		mDrawer2.startAnimation(alphaAnimation2);
	}

	/**
	 * 设置头部信息
	 */
	private void setHeaderMessage() {
		setProcessCount();
		setMemory();
	}

	/**
	 * 设置进程个数
	 */
	private void setProcessCount() {
		runningProcess = ProcessEngine
				.getRunningProcess(getApplicationContext());
		totalProcess = ProcessEngine.getTotalProcess(getApplicationContext());
		// 获取可用进程
		freeProcess = totalProcess - runningProcess;
		mProcessCount.setText("进程数：");
		mProcessCount.setUsed("正在运行" + runningProcess + "个");
		mProcessCount.setFree("可有进程:" + freeProcess + "个");

		int processProgress = (int) (runningProcess * 100f / totalProcess + 0.5f);
		mProcessCount.setProgress(processProgress);
	}

	/**
	 * 设置内存信息
	 */
	private void setMemory() {
		long freeMemory = ProcessEngine.getFreeMemory(getApplicationContext());
		long allMemory = ProcessEngine.getAllMemory(getApplicationContext());
		long usedMemroy = allMemory - freeMemory;

		mMemory.setText("内存：    ");
		mMemory.setUsed("占用内存:"
				+ Formatter.formatFileSize(getApplicationContext(), usedMemroy));
		mMemory.setFree("可用内存:"
				+ Formatter.formatFileSize(getApplicationContext(), freeMemory));

		int mMemoryProgress = (int) (usedMemroy * 100f / allMemory + 0.5f);
		mMemory.setProgress(mMemoryProgress);

	}

	private class Myadapter extends BaseAdapter implements
			StickyListHeadersAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			// return appInfos.size();
			// 2.设置条目的个数
			// 如果标示是true，表示显示所有数据，返回用户进程+系统进程个数，如果是false，表示只显示用户进程，只返回用户进程的个数
			return isshowsystem ? userprocessInfos.size()
					+ systemprocessInfos.size() : userprocessInfos.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			// 5.判断是否需要复用textview
			// instanceof : convertview是否是textview类型
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.processmanager_listview_item, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_homegridviewitem_icon = (ImageView) convertView
						.findViewById(R.id.iv_homegridviewitem_icon);
				viewHolder.tv_homegridviewitem_title = (TextView) convertView
						.findViewById(R.id.tv_homegridviewitem_title);
				viewHolder.tv_homegridviewitem_desc = (TextView) convertView
						.findViewById(R.id.tv_homegridviewitem_desc);
				viewHolder.cb_porcessmangeitem_isselect = (CheckBox) convertView
						.findViewById(R.id.cb_porcessmangeitem_isselect);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			// 4.获取显示条目的bean类
			// AppInfo appInfo = appInfos.get(position);
			ProcessInfo processInfo;
			if (position < userprocessInfos.size()) {
				// 从用户程序中获取数据
				processInfo = userprocessInfos.get(position);
			} else {
				processInfo = systemprocessInfos.get(position
						- userprocessInfos.size());
			}
			// 显示数据
			viewHolder.iv_homegridviewitem_icon
					.setImageDrawable(processInfo.icon);
			viewHolder.tv_homegridviewitem_title.setText(processInfo.name);
			viewHolder.tv_homegridviewitem_desc.setText("内存占用:"
					+ Formatter.formatFileSize(getApplicationContext(),
							processInfo.memorySize));

			// 根据标示设置checkbox是否选中
			viewHolder.cb_porcessmangeitem_isselect
					.setChecked(processInfo.isChecked);

			// 如果是当前应用程序，隐藏checkbox,不是显示checkbox
			// 为了复用缓存时候，checkbox应该显示，但是复用对象checkbox是隐藏的现象，在getview中，有if一定有else
			if (processInfo.packageName.equals(getPackageName())) {
				viewHolder.cb_porcessmangeitem_isselect
						.setVisibility(View.GONE);
			} else {
				viewHolder.cb_porcessmangeitem_isselect
						.setVisibility(View.VISIBLE);
			}

			return convertView;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		// 设置头部的样式
		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			TextView textView;
			if (convertView == null || !(convertView instanceof TextView)) {
				textView = new TextView(getApplicationContext());
				textView.setTextSize(15);
				// color.black,在android内部十六进制实现方式展示不一样
				textView.setTextColor(Color.BLACK);
				textView.setBackgroundColor(Color.parseColor("#D6D3CE"));
				textView.setPadding(8, 8, 8, 8);
			} else {
				textView = (TextView) convertView;
			}

			// 获取数据
			ProcessInfo processInfo;
			if (position < userprocessInfos.size()) {
				// 从用户程序中获取数据
				processInfo = userprocessInfos.get(position);
			} else {
				processInfo = systemprocessInfos.get(position
						- userprocessInfos.size());
			}
			// 根据条目的数据，设置显示用户进程，还是系统进程个数
			textView.setText(processInfo.isSystem ? "系统进程("
					+ systemprocessInfos.size() + "个)" : "用户进程("
					+ userprocessInfos.size() + "个)");

			return textView;
		}

		// 设置头部的条目的id
		@Override
		public long getHeaderId(int position) {
			// 获取数据
			ProcessInfo processInfo;
			if (position < userprocessInfos.size()) {
				// 从用户程序中获取数据
				processInfo = userprocessInfos.get(position);
			} else {
				processInfo = systemprocessInfos.get(position
						- userprocessInfos.size());
			}
			return processInfo.isSystem ? 0 : 1;
		}

	}

	static class ViewHolder {
		ImageView iv_homegridviewitem_icon;
		TextView tv_homegridviewitem_title, tv_homegridviewitem_desc;
		CheckBox cb_porcessmangeitem_isselect;
	}

	/**
	 * 全选
	 */
	public void all(View v) {
		// 将用户进程和系统进程的标示改为true
		for (int i = 0; i < userprocessInfos.size(); i++) {
			if (!userprocessInfos.get(i).packageName.equals(getPackageName())) {
				userprocessInfos.get(i).isChecked = true;
			}
		}
		// 如果系统进程隐藏，不行全选操作
		if (isshowsystem) {
			for (int i = 0; i < systemprocessInfos.size(); i++) {
				systemprocessInfos.get(i).isChecked = true;
			}
		}
		myadapter.notifyDataSetChanged();
	}

	/**
	 * 反选操作
	 */
	public void cancle(View v) {
		// 将用户进程和系统进程的标示取反操作
		for (int i = 0; i < userprocessInfos.size(); i++) {
			if (!userprocessInfos.get(i).packageName.equals(getPackageName())) {
				userprocessInfos.get(i).isChecked = !userprocessInfos.get(i).isChecked;
			}
		}
		// 如果系统进程隐藏，不行全选操作
		if (isshowsystem) {
			for (int i = 0; i < systemprocessInfos.size(); i++) {
				systemprocessInfos.get(i).isChecked = !systemprocessInfos
						.get(i).isChecked;
			}
		}
		myadapter.notifyDataSetChanged();
	}

	/**
	 * 清理
	 * 
	 * @param v
	 */
	public void clear(View v) {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		// 保存杀死进程的信心
		List<ProcessInfo> deleteinfo = new ArrayList<ProcessInfo>();

		// 杀死系统中进程
		for (ProcessInfo info : userprocessInfos) {
			if (info.isChecked) {
				am.killBackgroundProcesses(info.packageName);// 杀死后台进程和空进程，服务进程，前台进程，可见进程
				deleteinfo.add(info);
			}
		}
		// 如果系统进程隐藏，不进行清理操作
		if (isshowsystem) {
			for (ProcessInfo info : systemprocessInfos) {
				if (info.isChecked) {
					am.killBackgroundProcesses(info.packageName);// 杀死后台进程和空进程，服务进程，前台进程，可见进程
					deleteinfo.add(info);
				}
			}
		}
		// 更新界面
		long memory = 0;
		for (ProcessInfo processInfo : deleteinfo) {
			if (processInfo.isSystem) {
				systemprocessInfos.remove(processInfo);
			} else {
				userprocessInfos.remove(processInfo);
			}
			memory += processInfo.memorySize;
		}
		// 提醒用户杀死进程数，释放内存
		Toast.makeText(
				getApplicationContext(),
				"杀死"
						+ deleteinfo.size()
						+ "个进程,释放"
						+ Formatter.formatFileSize(getApplicationContext(),
								memory) + "内存", 0).show();
		// 更新头部信息
		// 让之前获取的进程数据 -杀死进程的进程数
		// 获取现在正在运行的进程数
		runningProcess = runningProcess - deleteinfo.size();
		mProcessCount.setUsed("正在运行" + runningProcess + "个");
		mProcessCount.setFree("可有进程:" + freeProcess + "个");

		int processProgress = (int) (runningProcess * 100f / totalProcess + 0.5f);
		mProcessCount.setProgress(processProgress);
		// 内存
		setMemory();
		myadapter.notifyDataSetChanged();
	}

}
