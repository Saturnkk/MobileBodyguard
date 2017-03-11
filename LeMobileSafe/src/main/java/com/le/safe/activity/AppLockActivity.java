package com.le.safe.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.myapplication.R;
import com.le.safe.bean.AppInfo;
import com.le.safe.db.dao.WatchDogDao;
import com.le.safe.engine.AppEngine;

import java.util.ArrayList;
import java.util.List;


public class AppLockActivity extends BaseActivity implements OnClickListener {
	private Button mUnlock;
	private Button mLock;
	private LinearLayout mllUnlock;
	private LinearLayout mllLock;
	private TextView mUnLockTitle;
	private ListView mUnLockListView;
	private TextView mLockTitle;
	private ListView mLockListView;
	private List<AppInfo> appMessages;
	private List<AppInfo> unLockApps;
	private List<AppInfo> lockApps;
	private WatchDogDao watchDogDao;
	private Myadapter unLockmyadapter;
	private Myadapter lockmyadapter;

	@Override
	protected View createXMLView() {
		View view = View.inflate(this,R.layout.activity_applock,null);
		return view;
	}

	@Override
	protected void initData() {
		initView();
		fillData();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mUnlock = (Button) findViewById(R.id.btn_applock_unlock);
		mLock = (Button) findViewById(R.id.btn_applock_lock);

		mllUnlock = (LinearLayout) findViewById(R.id.ll_applock_unlock);
		mllLock = (LinearLayout) findViewById(R.id.ll_applock_lock);

		mUnLockTitle = (TextView) findViewById(R.id.tv_applock_unlocktitle);
		mUnLockListView = (ListView) findViewById(R.id.lv_applock_unlock);

		mLockTitle = (TextView) findViewById(R.id.tv_applock_locktitle);
		mLockListView = (ListView) findViewById(R.id.lv_applock_lock);

		mUnlock.setOnClickListener(this);
		mLock.setOnClickListener(this);
	}

	/**
	 * 加载数据
	 */
	private void fillData() {
		watchDogDao = new WatchDogDao(getApplicationContext());
		new Thread() {
			public void run() {
				// 获取系统中所有的应用程序信息
				appMessages = AppEngine.getAPPMessage(getApplicationContext());
				// 将加锁的和未加锁的程序分别存放到不同的集合中中，在不同的listview展示出来
				unLockApps = new ArrayList<AppInfo>();
				lockApps = new ArrayList<AppInfo>();
				for (AppInfo appInfo : appMessages) {
					// 判断应用程序是否加锁，是否保存在数据库中
					if (watchDogDao.queryLockApp(appInfo.packageName)) {
						lockApps.add(appInfo);
					} else {
						unLockApps.add(appInfo);
					}
				}
				runOnUiThread(new Runnable() {

					public void run() {
						unLockmyadapter = new Myadapter(false);
						mUnLockListView.setAdapter(unLockmyadapter);

						lockmyadapter = new Myadapter(true);
						mLockListView.setAdapter(lockmyadapter);
					}
				});
			};
		}.start();
	}

	private class Myadapter extends BaseAdapter {
		// 当显示未加锁列表的时候，使用未加锁的集合，显示已加锁列表的时候，使用已加锁的集合
		// 是否加锁的标示，true：加锁，false:未加锁
		private boolean isLock;
		private TranslateAnimation right;
		private TranslateAnimation left;

		public Myadapter(boolean isLock) {
			this.isLock = isLock;

			// 创建平移动画
			// fromXType, fromXValue, toXType, toXValue : x轴移动规则和移动值
			// fromXValue：从哪里开始移动，toXValue：到哪里去，0表示没有，1表示整个控件
			// Animation.ABSOLUTE : 绝对的位置，没参考操作
			// Animation.RELATIVE_TO_SELF : 相对自己的进行操作
			// Animation.RELATIVE_TO_PARENT :相对于父控件进行操作
			// fromYType, fromYValue, toYType, toYValue : y轴移动规则和移动值
			right = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF,
					0, Animation.RELATIVE_TO_SELF, 0);
			right.setDuration(500);

			left = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF,
					0, Animation.RELATIVE_TO_SELF, 0);
			left.setDuration(500);
		}

		// 初始化数据调用getCount方法，当界面更新的notifyDataSetChanged的调用getCount方法
		@Override
		public int getCount() {
			showAppCount();
			// 如果加锁，使用加锁集合，如果未加锁，使用未加锁集合
			if (isLock) {
				return lockApps.size();
			} else {
				return unLockApps.size();
			}
		}

		@Override
		public Object getItem(int position) {
			// 如果加锁，使用加锁集合，如果未加锁，使用未加锁集合
			if (isLock) {
				return lockApps.get(position);
			} else {
				return unLockApps.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			final View view;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.applock_listview_item, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_applockitem_icon = (ImageView) view
						.findViewById(R.id.iv_applockitem_icon);
				viewHolder.iv_applockitem_name = (TextView) view
						.findViewById(R.id.iv_applockitem_name);
				viewHolder.iv_applockitem_lockorunlock = (ImageView) view
						.findViewById(R.id.iv_applockitem_lockorunlock);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}
			// 获取条目对应的数据信息
			// AppInfo appInfo = appMessages.get(position);
			final AppInfo appInfo = (AppInfo) getItem(position);
			viewHolder.iv_applockitem_icon.setImageDrawable(appInfo.icon);
			viewHolder.iv_applockitem_name.setText(appInfo.name);
			// 根据是加锁列表还是未加锁列表，设置加锁解锁的图标
			if (isLock) {
				// 加锁 解锁图标
				viewHolder.iv_applockitem_lockorunlock
						.setBackgroundResource(R.drawable.selector_applock_unlock);
			} else {
				// 未加锁 加锁的图标
				viewHolder.iv_applockitem_lockorunlock
						.setBackgroundResource(R.drawable.selector_applock_lock);
			}

			// 加锁解锁
			viewHolder.iv_applockitem_lockorunlock
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 问题：自己是否能给自己加锁
							if (appInfo.packageName.equals(getPackageName())) {
								Toast.makeText(getApplicationContext(),
										"自己不能给自己加锁", Toast.LENGTH_SHORT).show();
							} else {
								// 加锁解锁操作
								if (isLock) {
									// 原因：动画执行需要一定的时间，但是在动画执行的时间范围内，把该显示动画的条目给删除了，listview会自动将之后数据往前移动，
									// 这个时候，动画不知道执行条目已经发生变化了，所以动画还是照常执行了
									// 动画执行完，再去删除数据
									// 执行动画
									view.startAnimation(left);// 每次都是重新执行动画
									// 动画执行的监听
									left.setAnimationListener(new AnimationListener() {
										// 动画开始的调用
										@Override
										public void onAnimationStart(
												Animation animation) {
											// TODO Auto-generated method stub

										}

										// 动画重复调用的方法
										@Override
										public void onAnimationRepeat(
												Animation animation) {
											// TODO Auto-generated method stub

										}

										// 动画结束调用的方法
										@Override
										public void onAnimationEnd(
												Animation animation) {
											// convertView.setAnimation(left);//设置动画，执行一次动画
											// 解锁
											// 1.将应用的包名从数据库中删除，表示应用已解锁
											// 2.从已加锁集合中将数据删除
											// 3.将数据添加到未加锁集合中
											// 4.更新界面
											boolean isDelete = watchDogDao
													.deleteLockApp(appInfo.packageName);
											if (isDelete) {
												lockApps.remove(appInfo);
												unLockApps.add(appInfo);
												unLockmyadapter
														.notifyDataSetChanged();
												lockmyadapter
														.notifyDataSetChanged();
											} else {
												Toast.makeText(
														getApplicationContext(),
														"系统繁忙，请稍后再试...", Toast.LENGTH_SHORT)
														.show();
											}
										}
									});
								} else {
									// 加锁
									view.startAnimation(right);// 每次都是重新执行动画
									right.setAnimationListener(new AnimationListener() {

										@Override
										public void onAnimationStart(
												Animation animation) {
											// TODO Auto-generated method stub

										}

										@Override
										public void onAnimationRepeat(
												Animation animation) {
											// TODO Auto-generated method stub

										}

										@Override
										public void onAnimationEnd(
												Animation animation) {
											// 1.将应用的包名保存到数据库中，表示应用已加锁
											// 2.从未加锁集合中将数据删除
											// 3.将数据添加到已加锁集合中
											// 4.更新界面
											boolean isAdd = watchDogDao
													.addLockApp(appInfo.packageName);
											if (isAdd) {
												unLockApps.remove(appInfo);
												lockApps.add(appInfo);
												unLockmyadapter
														.notifyDataSetChanged();
												lockmyadapter
														.notifyDataSetChanged();
											} else {
												Toast.makeText(
														getApplicationContext(),
														"系统繁忙，请稍后再试...", Toast.LENGTH_SHORT)
														.show();
											}
										}
									});
								}
							}
						}
					});
			return view;
		}

	}

	static class ViewHolder {
		ImageView iv_applockitem_icon, iv_applockitem_lockorunlock;
		TextView iv_applockitem_name;
	}

	/**
	 * 设置加锁和未加锁程序的显示个数
	 */
	public void showAppCount() {
		mUnLockTitle.setText("未加锁(" + unLockApps.size() + ")");
		mLockTitle.setText("已加锁(" + lockApps.size() + ")");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_applock_unlock:
			// 未加锁操作
			// button按钮样式
			mUnlock.setBackgroundResource(R.drawable.dg_btn_confirm_normal);
			mLock.setBackgroundResource(R.drawable.dg_button_cancel_normal);
			// button按钮字体颜色
			mUnlock.setTextColor(Color.parseColor("#ffffff"));
			mLock.setTextColor(Color.parseColor("#429ED6"));
			// 未加锁列表展示吗，已加锁列表隐藏
			mllUnlock.setVisibility(View.VISIBLE);
			mllLock.setVisibility(View.GONE);
			break;
		case R.id.btn_applock_lock:
			// 已加锁加锁操作
			// button按钮样式
			mLock.setBackgroundResource(R.drawable.dg_btn_confirm_normal);
			mUnlock.setBackgroundResource(R.drawable.dg_button_cancel_normal);
			// button按钮字体颜色
			mLock.setTextColor(Color.parseColor("#ffffff"));
			mUnlock.setTextColor(Color.parseColor("#429ED6"));
			// 已加锁列表展示，未加锁列表隐藏
			mllLock.setVisibility(View.VISIBLE);
			mllUnlock.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}

}
