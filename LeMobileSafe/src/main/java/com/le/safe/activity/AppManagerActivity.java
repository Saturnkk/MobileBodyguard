package com.le.safe.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.myapplication.R;
import com.le.safe.bean.AppInfo;
import com.le.safe.engine.AppEngine;
import com.le.safe.ui.CustomProgressbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class AppManagerActivity extends AppCompatActivity implements OnClickListener {

	private static final int UNINSTALL_CODE = 10;
	private CustomProgressbar mMemory;
	private CustomProgressbar mSD;
	private ListView mApplication;
	private List<AppInfo> appInfos;
	private List<AppInfo> userappInfos;
	private List<AppInfo> systemappInfos;
	private TextView mCount;
	private PopupWindow popupWindow;
	private AppInfo appInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appmanager);
		initView();
		setMemory();
		fillData();
		setListViewScrollListener();
		setListViewItemClickListener();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mMemory = (CustomProgressbar) findViewById(R.id.cp_appmanager_memory);
		mSD = (CustomProgressbar) findViewById(R.id.cp_appmanager_sd);
		mApplication = (ListView) findViewById(R.id.lv_appmanager_applications);
		mCount = (TextView) findViewById(R.id.tv_appmanger_count);
	}

	/**
	 * 设置内存信息
	 */
	private void setMemory() {
		// 内存
		File memoryDirectory = Environment.getDataDirectory();
		// 获取内存可用的空间,b -> kb -> mb
		long mfreeSpace = memoryDirectory.getFreeSpace();
		// 获取内存总空间
		long mtotalSpace = memoryDirectory.getTotalSpace();
		// 获取已用空间
		long mUsedSpace = mtotalSpace - mfreeSpace;
		// 设置数据展示
		mMemory.setText("内存:  ");
		// 将b -> mb
		String freesize = Formatter.formatFileSize(getApplicationContext(),
				mfreeSpace);
		mMemory.setFree(freesize + "可用");
		String usedSize = Formatter.formatFileSize(getApplicationContext(),
				mUsedSpace);
		mMemory.setUsed(usedSize + "已用");
		// 程序中 3.4 3 3.7 3 数学 3.4 3 3.7 4 3.7+0.5 = 4.2
		int mProgress = (int) (mUsedSpace * 100f / mtotalSpace + 0.5f);
		mMemory.setProgress(mProgress);

		// SD卡
		File storageDirectory = Environment.getExternalStorageDirectory();
		long mSDfreeSpace = storageDirectory.getFreeSpace();
		long mSDtotalSpace = storageDirectory.getTotalSpace();
		long mSDUsedSpace = mSDtotalSpace - mSDfreeSpace;
		mSD.setText("SD:     ");
		String mSDfreesize = Formatter.formatFileSize(getApplicationContext(),
				mSDfreeSpace);
		mSD.setFree(mSDfreesize + "可用");
		String mSDusedSize = Formatter.formatFileSize(getApplicationContext(),
				mSDUsedSpace);
		mSD.setUsed(mSDusedSize + "已用");
		// 程序中 3.4 3 3.7 3 数学 3.4 3 3.7 4 3.7+0.5 = 4.2
		int mSDProgress = (int) (mSDUsedSpace * 100f / mSDtotalSpace + 0.5f);
		mSD.setProgress(mSDProgress);
	}

	/**
	 * 获取展示数据
	 */
	private void fillData() {
		new Thread() {

			public void run() {
				appInfos = AppEngine.getAPPMessage(getApplicationContext());
				// 1.将所有的应用程序，拆分成用户程序和系统程序吗，分别保存
				userappInfos = new ArrayList<AppInfo>();
				systemappInfos = new ArrayList<AppInfo>();
				for (AppInfo appInfo : appInfos) {
					if (appInfo.isSystem) {
						systemappInfos.add(appInfo);
					} else {
						userappInfos.add(appInfo);
					}
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						mCount.setText("用户程序(" + userappInfos.size() + "个)");

						mApplication.setAdapter(new Myadapter());
					}
				});
			};
		}.start();
	}

	/**
	 * 设置listview的滑动监听
	 */
	private void setListViewScrollListener() {
		mApplication.setOnScrollListener(new OnScrollListener() {
			// 滑动状态改变
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			// 滑动的操作
			// view : listview
			// firstVisibleItem : 第一个可见条目
			// visibleItemCount : 可见条目的总数
			// totalItemCount : listview条目的总数
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// 判断第一个可见条目是否是系统程序多少个，或者大于系统程序个数的位置，显示系统程序多杀个
				// 在listview初始化的时候，就会调用onScroll方法，每对listview操作就会调用onscroll方法
				hidePopuwindow();
				System.out.println("listview调用onscroll方法了");
				if (userappInfos != null && systemappInfos != null) {
					if (firstVisibleItem >= userappInfos.size() + 1) {
						mCount.setText("系统程序(" + systemappInfos.size() + "个)");
					} else {
						mCount.setText("用户程序(" + userappInfos.size() + "个)");
					}
				}
			}
		});
	}

	/**
	 * listView的条目点击事件
	 */
	private void setListViewItemClickListener() {
		mApplication.setOnItemClickListener(new OnItemClickListener() {
			// view : 点击条目view对象
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 显示气泡
				// 1.屏蔽用户程序和系统程序个数不能显示气泡
				if (position == 0 || position == userappInfos.size() + 1) {
					return;
				}
				// 2.之后需要对数据进行操作，所以需要获取条目对应程序的数据
				if (position <= userappInfos.size()) {
					// 从用户程序中获取数据
					appInfo = userappInfos.get(position - 1);
				} else {
					appInfo = systemappInfos.get(position - userappInfos.size()
							- 2);
				}
				// 3.显示气泡
				// 判断，如果气泡显示了，先取消显示，在重新显示
				hidePopuwindow();
				View contentView = View.inflate(getApplicationContext(),
						R.layout.popuwindow_item, null);
				// 初始化控件
				// 只有点击事件，没有其他操作
				contentView.findViewById(R.id.ll_popuwindow_uninstall)
						.setOnClickListener(AppManagerActivity.this);
				contentView.findViewById(R.id.ll_popuwindow_open)
						.setOnClickListener(AppManagerActivity.this);
				contentView.findViewById(R.id.ll_popuwindow_share)
						.setOnClickListener(AppManagerActivity.this);
				contentView.findViewById(R.id.ll_popuwindow_info)
						.setOnClickListener(AppManagerActivity.this);

				popupWindow = new PopupWindow(contentView,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//				popupWindow.setAnimationStyle(R.style.popuwindowAnimation);

				// popupWindow.showAsDropDown(view);//要对那个view对象展示气泡，默认显示在点击条目的下方
				// xoff : 距离条目的左边框的距离
				// yoff : 距离条目上部边框的距离
				popupWindow.showAsDropDown(view, 60, -view.getHeight());
			}
		});
	}

	// 异常popuwindow
	private void hidePopuwindow() {
		if (popupWindow != null) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 消除popuwindow
		hidePopuwindow();
	}

	private class Myadapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			// return appInfos.size();
			// 2.设置条目的个数
			return userappInfos.size() + systemappInfos.size() + 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 3.根据不同的条目显示不同的样式
			if (position == 0) {
				// 用户程序
				TextView textView = new TextView(getApplicationContext());
				textView.setText("用户程序(" + userappInfos.size() + "个)");
				textView.setTextSize(15);
				// color.black,在android内部十六进制实现方式展示不一样
				textView.setTextColor(Color.BLACK);
				textView.setBackgroundColor(Color.parseColor("#D6D3CE"));
				textView.setPadding(8, 8, 8, 8);
				return textView;
			} else if (position == userappInfos.size() + 1) {
				TextView textView = new TextView(getApplicationContext());
				textView.setText("系统程序(" + systemappInfos.size() + "个)");
				textView.setTextSize(15);
				textView.setTextColor(Color.BLACK);
				textView.setBackgroundColor(Color.parseColor("#D6D3CE"));
				textView.setPadding(8, 8, 8, 8);
				return textView;
			}
			ViewHolder viewHolder;
			// 5.判断是否需要复用textview
			// instanceof : convertview是否是textview类型
			if (convertView == null || convertView instanceof TextView) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.appmanager_listview_item, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_homegridviewitem_icon = (ImageView) convertView
						.findViewById(R.id.iv_homegridviewitem_icon);
				viewHolder.tv_homegridviewitem_title = (TextView) convertView
						.findViewById(R.id.tv_homegridviewitem_title);
				viewHolder.tv_homegridviewitem_desc = (TextView) convertView
						.findViewById(R.id.tv_homegridviewitem_desc);
				viewHolder.tv_appmanager_memroysize = (TextView) convertView
						.findViewById(R.id.tv_appmanager_memroysize);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			// 4.获取显示条目的bean类
			// AppInfo appInfo = appInfos.get(position);
			AppInfo appInfo;
			if (position <= userappInfos.size()) {
				// 从用户程序中获取数据
				appInfo = userappInfos.get(position - 1);
			} else {
				appInfo = systemappInfos
						.get(position - userappInfos.size() - 2);
			}
			viewHolder.iv_homegridviewitem_icon.setImageDrawable(appInfo.icon); // null.方法
																				// 参数为null
			viewHolder.tv_homegridviewitem_title.setText(appInfo.name);
			viewHolder.tv_homegridviewitem_desc.setText(appInfo.isSD ? "SD卡"
					: "手机内存");

			String size = Formatter.formatFileSize(getApplicationContext(),
					appInfo.memorySize);
			viewHolder.tv_appmanager_memroysize.setText(size);
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

	}

	static class ViewHolder {
		ImageView iv_homegridviewitem_icon;
		TextView tv_homegridviewitem_title, tv_homegridviewitem_desc,
				tv_appmanager_memroysize;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_popuwindow_uninstall:
			uninstall();
			break;
		case R.id.ll_popuwindow_open:
			open();
			break;
		case R.id.ll_popuwindow_share:
			share();
			break;
		case R.id.ll_popuwindow_info:
			info();
			break;
		}
		// 隐藏popuwindow
		hidePopuwindow();
	}
	/**
	 * 分享    shareSDK:第三方的分享SDK
	 */
	private void share() {
		/**
		 * <intent-filter>
               <action android:name="android.intent.action.SEND" />
               <category android:name="android.intent.category.DEFAULT" />
               <data android:mimeType="text/plain" />
           </intent-filter>
		 */
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "发现一个牛逼的软件："+appInfo.name+",下载路径：www.baidu.com,自己去搜...");
		startActivity(intent);
	}

	/**
	 * 详情
	 */
	private void info() {
		/**
		 * START {
		 * 
		 * act=android.settings.APPLICATION_DETAILS_SETTINGS action
		 * 
		 * cat=[android.intent.category.DEFAULT] categroy
		 * 
		 * dat=package:com.example.android.apis data
		 * 
		 * cmp=com.android.settings/.applications.InstalledAppDetails u=0 } from
		 * pid 1810
		 */
		Intent intent = new Intent();
		intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:"+appInfo.packageName));
		startActivity(intent);
	}

	/**
	 * 打开应用程序
	 */
	private void open() {
		PackageManager pm = getPackageManager();
		// 获取应用的启动意图
		Intent intent = pm.getLaunchIntentForPackage(appInfo.packageName);
		if (intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(getApplicationContext(), "系统核心程序，无法打开", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 卸载操作
	 */
	private void uninstall() {
		/**
		 * <intent-filter> <action android:name="android.intent.action.VIEW" />
		 * <action android:name="android.intent.action.DELETE" /> <category
		 * android:name="android.intent.category.DEFAULT" /> <data
		 * android:scheme="package" />// tel:+电话号码 </intent-filter>
		 */
		// 屏蔽不能卸载自己
		if (!appInfo.packageName.equals(getPackageName())) {
			if (!appInfo.isSystem) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:" + appInfo.packageName));
				startActivityForResult(intent, UNINSTALL_CODE);
			} else {
				Toast.makeText(getApplicationContext(), "系统程序，想要卸载，请Root先", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			Toast.makeText(getApplicationContext(), "文明社会，杜绝自杀..", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fillData();
	}

}
