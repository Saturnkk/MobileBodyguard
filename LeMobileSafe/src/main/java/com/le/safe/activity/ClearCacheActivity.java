package com.le.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dell.myapplication.R;
import com.le.safe.bean.CacheInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class ClearCacheActivity extends AppCompatActivity {
	
	protected static final int REQUEST_INFO_CODE = 100;

	private ImageView mScanLine;
	private PackageManager pm;
	private ProgressBar mProgressbar;
	private TextView mName;
	private ImageView mIcon;
	private TextView mCacheSize;
	private List<CacheInfo> list;
	private ListView mApplicaiton;
	private Myadapter myadapter;
	private List<PackageInfo> packages;
	/**
	 * 缓存软件的个数
	 */
	private int totalcount=0;
	/**
	 * 缓存的总大小
	 */
	private long totalCacheSize = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clearcache);
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mScanLine = (ImageView) findViewById(R.id.iv_clearcache_scanline);
		mProgressbar = (ProgressBar) findViewById(R.id.pb_clearcache_progressbar);
		mName = (TextView) findViewById(R.id.tv_clearcache_name);
		mIcon = (ImageView) findViewById(R.id.iv_clearcache_icon);
		mCacheSize = (TextView) findViewById(R.id.tv_clearcache_cachesize);
		mApplicaiton = (ListView) findViewById(R.id.lv_clearcache_application);
		mRelScan = (RelativeLayout) findViewById(R.id.rel_clearcache_scan);
		mLLProgressbar = (LinearLayout) findViewById(R.id.ll_clearcache_progressbar);
		mScanCacheText = (TextView) findViewById(R.id.tv_clearcache_scancachetext);
		mClearScan = (Button) findViewById(R.id.btn_clearcache_clear);
		mClearAll = (Button) findViewById(R.id.btn_clearcache_clearall);

		scan();
	}

	/**
	 * 扫描操作
	 */
	private void scan() {

		//扫描不能点击一键清理按钮
		mClearAll.setEnabled(false);//设置一键清理按钮不可用
		
		//隐藏快速扫描布局，显示的进度条的布局
		mRelScan.setVisibility(View.GONE);
		mLLProgressbar.setVisibility(View.VISIBLE);
		
		//重新扫描出现提示显示有两个缓存软件而实际只有一个的原因：第一次扫描的时候，实现保存的缓存软件的信息的操作，当第二次扫描的时候，没有清空在原来的基础上直接开始累加，所以
		//展示的数据是两次扫描的总和
		totalcount = 0;
		totalCacheSize = 0;
		
		list = new ArrayList<CacheInfo>();
		list.clear();//在扫描之前将原来扫描的 数据清空，重新加载存放新的数据
		
		// 1.线的动画实现
		setAnimation();
		//2.扫描应用程序，设置进度条的进度
		pm = getPackageManager();
		new Thread(){
			public void run() {
				packages = pm.getInstalledPackages(0);
				
				//2.1设置总进度
				mProgressbar.setMax(packages.size());
				int progress=0;
				for (PackageInfo packageInfo : packages) {
					SystemClock.sleep(300);
					//每循环一次，就代表扫描一个应用程序，进度就要+1
					//2.2设置当前进度
					progress++;
					mProgressbar.setProgress(progress);
					//3.每扫描一个应用程序，显示一个应用程序的名称和图标
					setNameAndIcon(packageInfo.packageName);
					//4.获取应用程序的缓存大小
					getCacheSize(packageInfo.packageName);
				}
			};
		}.start();
	}
	/**
	 * 4.获取应用程序缓存大小
	 * @param packageName
	 */
	protected void getCacheSize(String packageName) {
//		//mPm.getPackageSizeInfo(mCurComputingSizePkg, mStatsObserver);
//		try {
//			Method method = pm.getClass().getDeclaredMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
//			//方法所在类的对象，如果方法是静态的方法，设置为null,不是，设置方法所在类的对象
//			method.invoke(pm, packageName,mStatsObserver);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

//	/**
//	 * 获取缓存大小aidl
//	 */
//	IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
//        public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
//        	//获取缓存大小
//        	final long cacheSize = stats.cacheSize;
//        	String packageName = stats.packageName;
//        	//System.out.println(packageName+"   :"+Formatter.formatFileSize(getApplicationContext(), cachsize));
//        	runOnUiThread(new Runnable() {
//				public void run() {
//					mCacheSize.setText("缓存大小:"+Formatter.formatFileSize(getApplicationContext(), cacheSize));
//				}
//			});
//        	//5.通过listview展示应用程序信息
//        	//5.1获取数据，将数据存放到bean类,将bean类保存到集合
//        	try {
//				ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
//				String name = applicationInfo.loadLabel(pm).toString();
//				Drawable icon = applicationInfo.loadIcon(pm);
//				//将信息保存到bean类
//				CacheInfo cacheInfo = new CacheInfo(name, packageName, icon, cacheSize);
//				//将bean类保存到list集合
//				//5.5将有缓存的条目，放到listview第一个位置展示
//				//如何应用程序有缓存信息，保存信息的时候，将有缓存的应用程序的信息保存到list集合的第一个位置
//				//因为listview展示数据需要用到adapter，而adapter展示数据是通过getcount来获取条目，所以，adapter展示数据是根据list集合顺序展示的
//				if (cacheSize > 0) {
//					//有缓存
//					list.add(0,cacheInfo);//将cacheInfo放到list集合的第一位
//					totalcount++;
//					totalCacheSize+=cacheSize;
//				}else{
//					//没有缓存
//					list.add(cacheInfo);
//				}
//			} catch (NameNotFoundException e) {
//				e.printStackTrace();
//			}
//        	//5.2给listview设置adapter展示数据，更新UI操作
//        	runOnUiThread(new Runnable() {
//
//				public void run() {
//					//因为操作是没循环一个应用，就动态的实时添加到listview中进行显示操作，并进行自动滑动，所以不能每次都进行setadapter操作
//					//原因，setadapter操作，相当于重新加载新的界面，默认会定位到第一页数据
//					if (myadapter == null) {
//						myadapter = new Myadapter();
//						mApplicaiton.setAdapter(myadapter);
//					}else{
//						myadapter.notifyDataSetChanged();
//					}
//					//5.3listView自动滑动到最底部条目
//					//position : 如果设置的值大于listview的最大条目数，会以listview的最大条目数据为准
//					mApplicaiton.smoothScrollToPosition(myadapter.getCount()-1);
//				}
//			});
//        	//5.4扫描结束，会滚动第一个条目
//        	runOnUiThread(new Runnable() {
//				public void run() {
//					//如何判断listview加载的已将是最后一条数据
//					//因为每循环一个应用，就要把应用的信息保存到list集合中进行显示操作，所以到循环到最后一个应用的时候，list的长度是和packages(获取系统安装所有应用程序的集合)
//					if (list.size() == packages.size()) {
//						//扫描结束，回滚到第一个条目
//						mApplicaiton.smoothScrollToPosition(0);
//
//						//6.扫描完成的操作
//						//6.1动画注销
//						mScanLine.clearAnimation();
//						//6.2隐藏进度条布局，显示快速扫描布局
//						mLLProgressbar.setVisibility(View.GONE);
//						mRelScan.setVisibility(View.VISIBLE);
//						//6.3展示缓存软件的个数及缓存总大小
//						mScanCacheText.setText("总共有"+totalcount+"个缓存软件，总共"+Formatter.formatFileSize(getApplicationContext(), totalCacheSize));
//						//6.4重新快速扫描
//						mClearScan.setOnClickListener(new OnClickListener() {
//
//							@Override
//							public void onClick(View v) {
//								scan();
//							}
//						});
//						//7.扫描完成，一键清理按钮才可点击，扫描的时候不能点击
//						mClearAll.setEnabled(true);//扫描完成，设置一键清理按钮可用
//						mClearAll.setOnClickListener(new OnClickListener() {
//
//							@Override
//							public void onClick(View v) {
//								//清理所有缓存应用的缓存
//								// public abstract void freeStorageAndNotify(long freeStorageSize, IPackageDataObserver observer);
//								try {
//									Method method = pm.getClass().getDeclaredMethod("freeStorageAndNotify", Long.TYPE,IPackageDataObserver.class);
//									//方法所在类的对象，如果方法是静态的方法，设置为null,不是，设置方法所在类的对象
//									method.invoke(pm, Long.MAX_VALUE,new MyIPackageDataObserver());
//								} catch (Exception e) {
//									e.printStackTrace();
//								}
//								//重新扫描操作
//								scan();
//							}
//						});
//					}
//				}
//			});
//
//        }
//	};
//	//申请存储的aidl
//	private class MyIPackageDataObserver extends IPackageDataObserver.Stub{
//
//		@Override
//		public void onRemoveCompleted(String packageName, boolean succeeded)
//				throws RemoteException {
//			// TODO Auto-generated method stub
//
//		}
//
//	}
	
	private RelativeLayout mRelScan;
	private LinearLayout mLLProgressbar;
	private TextView mScanCacheText;
	private Button mClearScan;

	private Button mClearAll;

	private class Myadapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.clearcache_listview_item, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_clearcache_icon = (ImageView) convertView.findViewById(R.id.iv_clearcache_icon);
				viewHolder.tv_clearcache_title = (TextView) convertView.findViewById(R.id.tv_clearcache_title);
				viewHolder.tv_clearcache_desc = (TextView) convertView.findViewById(R.id.tv_clearcache_desc);
				viewHolder.iv_clearcache_clear = (ImageView) convertView.findViewById(R.id.iv_clearcache_clear);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			//根据条目的位置获取相应的数据
			final CacheInfo cacheInfo = list.get(position);
			viewHolder.iv_clearcache_icon.setImageDrawable(cacheInfo.icon);
			viewHolder.tv_clearcache_title.setText(cacheInfo.name);
			viewHolder.tv_clearcache_desc.setText("缓存大小:"+Formatter.formatFileSize(getApplicationContext(), cacheInfo.cacheSize));
			//有缓存信息的显示清理图标，没有不显示
			if (cacheInfo.cacheSize > 0) {
				viewHolder.iv_clearcache_clear.setVisibility(View.VISIBLE);
			}else{
				viewHolder.iv_clearcache_clear.setVisibility(View.GONE);
			}	
			//清理点击事件
			viewHolder.iv_clearcache_clear.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//跳转到应用详情界面
					Intent intent = new Intent();
					intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
					intent.addCategory("android.intent.category.DEFAULT");
					intent.setData(Uri.parse("package:"+cacheInfo.packageName));
					//startActivity(intent);
					startActivityForResult(intent, REQUEST_INFO_CODE);
				}
			});
			return convertView;
		}
		
	}
	static class ViewHolder{
		ImageView iv_clearcache_icon,iv_clearcache_clear;
		TextView tv_clearcache_title,tv_clearcache_desc;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//重新扫描
		scan();
	}
	
	/**
	 * 3.设置应用程序的图标和名称
	 * @param packageName
	 */
	protected void setNameAndIcon(final String packageName) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				try {
					//根据包名获取应用程序的applicationinfo信息，根据applicationinfo信息获取名称和图标
					ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
					String name = applicationInfo.loadLabel(pm).toString();
					Drawable icon = applicationInfo.loadIcon(pm);
					//更新ui的操作，在主线程中执行，不能在子线程中执行
					mName.setText(name);
					mIcon.setImageDrawable(icon);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 线的动画效果
	 */
	private void setAnimation() {
		TranslateAnimation translateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT,
				0, Animation.RELATIVE_TO_PARENT, 0,
				Animation.RELATIVE_TO_PARENT, 1);
		translateAnimation.setDuration(500);
		translateAnimation.setRepeatCount(Animation.INFINITE);
		translateAnimation.setRepeatMode(Animation.REVERSE);
		mScanLine.startAnimation(translateAnimation);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
