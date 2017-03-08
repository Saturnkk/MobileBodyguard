package com.le.safe.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dell.myapplication.R;
import com.le.safe.bean.BlackNumberInfo;
import com.le.safe.db.BlackNumberContants;
import com.le.safe.db.dao.BlackNumberDao;

import java.util.List;


public class BlackNumberActivity extends BaseActivity {

	protected static final int BLACKNUMBER_ADD_CODE = 100;
	protected static final int BLACKNUMBER_UPDATE_CODE = 101;
	private ListView mBlacknumbers;
	private ImageView mEmpty;
	private BlackNumberDao blackNumberDao;
	private List<BlackNumberInfo> blackNumbers;
	private ImageView mAdd;
	private Myadapter myadapter;
	private LinearLayout mLoading;
	//查询的最大条数
	private final int MAXNUM=20;
	//查询的起始位置
	private int startIndex=0;

	@Override
	protected View createXMLView() {
		View view = View.inflate(this,R.layout.activity_blacknumber,null);
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
		mBlacknumbers = (ListView) findViewById(R.id.lv_blacknumber_blacknumbers);
		mEmpty = (ImageView) findViewById(R.id.iv_blacknumber_empty);
		mAdd = (ImageView) findViewById(R.id.iv_blacknumber_add);
		mLoading = (LinearLayout) findViewById(R.id.ll_blacknumber_loading);
		
		fillData();
		//添加数据
		addBlackNumber();
		//更新操作
		updateBlackNumber();
		//设置listview的滑动监听
		setOnScrollListenter();
	}
	/**
	 * 监听listview滑动操作
	 */
	private void setOnScrollListenter() {
		//listview的滑动监听事件
		mBlacknumbers.setOnScrollListener(new OnScrollListener() {
			//当滑动状态改变的时候调用的方法
			//scrollState : 滑动的状态
			//SCROLL_STATE_IDLE : 空闲的状态
			//SCROLL_STATE_FLING : 快速滑动的状态，惯性滑动
			//SCROLL_STATE_TOUCH_SCROLL : 触摸滑动，缓慢滑动
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//当listview静止的时候，判断当前的可见的最后一条数据是否是listview的最后一条数据，是，加载下一波数据，不是不管了
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					//获取当前可见的最后一个条目
					int position = mBlacknumbers.getLastVisiblePosition();
					//判断是否是listview最后一条数据,因为listview是通过list集合加载数据的
					if (position == blackNumbers.size()-1) {
						//加载下一波数据
						//更新查询的起始位置
						startIndex+=MAXNUM;
						fillData();
					}
				}
			}
			//滑动的时候调用的方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	/**
	 * 更新黑名单号码
	 */
	private void updateBlackNumber() {
		//1.条目点击事件
		mBlacknumbers.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//2.传递黑名单号码和拦截类型，传递表示更新操作的标示，告诉更新界面更新的时候哪条数据，方便回显
				Intent intent = new Intent(BlackNumberActivity.this,BlackNumberAddorEditActivity.class);
				intent.putExtra("number", blackNumbers.get(position).blackNumber);
				intent.putExtra("mode", blackNumbers.get(position).mode);
				intent.setAction("update");//设置intent的动作是update操作，如果不设置，就是空的
				intent.putExtra("position", position);
				startActivityForResult(intent, BLACKNUMBER_UPDATE_CODE);
			}
		});
	}
	/**
	 * 添加黑名单，跳转到添加黑名单界面
	 */
	private void addBlackNumber() {
		mAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BlackNumberActivity.this,BlackNumberAddorEditActivity.class);
				//startActivity(intent);
				startActivityForResult(intent, BLACKNUMBER_ADD_CODE);
			}
		});
	}
	//resultCode : 返回的结果码
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//接受添加成功，要展示的数据
		//因为添加和更新是使用同一个activity了，所以接收数据要判断是添加的数据还是更新的数据
		//如果请求码是添加的请求码，表示添加的操作，如果是更新的请求码，表示更新的操作
		if (requestCode == BLACKNUMBER_ADD_CODE) {
			//接受添加传递过来的数据
			//判断数据是否传递成功，成功，接受数据
			switch (resultCode) {
			case Activity.RESULT_OK:
				//数据传递成功，
				//因为传递数据的时候是通过intent传递的，在onActivityResult方法中有一个intent类型的参数来接受数据
				if (data != null ) {
					//获取传递的数据
					String blacknumber = data.getStringExtra("number");
					int mode = data.getIntExtra("mode", 0);
					//将数据保存bean类中
					BlackNumberInfo blackNumberInfo = new BlackNumberInfo(blacknumber, mode);
					//将bean类保存到集合中
					//blackNumbers.add(blackNumberInfo);
					blackNumbers.add(0, blackNumberInfo);//location : 将object添加到集合的那个位置
					//更新界面展示数据
					myadapter.notifyDataSetChanged();//更新界面，重新执行adapter的getcount方法和getview方法
				}
				break;

			default:
				break;
			}
		}else if(requestCode == BLACKNUMBER_UPDATE_CODE){//判断请求码是否是更新的请求码
			//判断数据是否传递成功
			switch (resultCode) {
			case Activity.RESULT_OK:
				//传递成功
				//更新条目数据，更新界面
				//获取传递的数据
				if (data!=null) {
					int mode = data.getIntExtra("mode", 0);
					int position = data.getIntExtra("position", 0);
					//根据条目的位置，更新相应条目的数据
					blackNumbers.get(position).mode = mode;
					//更新界面
					myadapter.notifyDataSetChanged();
				}
				
				break;

			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 获取数据
	 */
	private void fillData() {
		//加载数据显示进度条
		mLoading.setVisibility(View.VISIBLE);
		//查询数据库，耗时操作，子线程中
		blackNumberDao = new BlackNumberDao(getApplicationContext());
		new Thread(){
			public void run() {
				//查询全部数据
				//blackNumbers = blackNumberDao.queryAllBlackNumber();
				//原因：因为最新的数据将之前的数据覆盖
				if (blackNumbers == null) {
					blackNumbers = blackNumberDao.queryPartBlackNumber(MAXNUM, startIndex);
				}else{
					//Collection  ： list的父类
					//将一个集合和另一个集合合并，取并集
					//A[0,1,2]  B[2,4,5]  A.addAll(B)    A[0,1,2,4,5]
					blackNumbers.addAll(blackNumberDao.queryPartBlackNumber(MAXNUM, startIndex));
				}
				//通过listview展示数据，主线程
				runOnUiThread(new Runnable() {

					public void run() {
						//如果listview加载过来adapter，不需要加载adapter，直接更新adapter就可以
						if (myadapter == null) {
							myadapter = new Myadapter();
							//给listview设置adapter表示重新个listview加载显示界面，listview会将数据定位在第一个界面
							mBlacknumbers.setAdapter(myadapter);
						}else{
							myadapter.notifyDataSetChanged();//更新界面
						}
						//当listview没有数据展示的显示空白页面的图片
						//setEmptyView : 当listview没有数据的时候，显示什么布局样式
						mBlacknumbers.setEmptyView(mEmpty);
						//数据显示完成隐藏进度条
						mLoading.setVisibility(View.GONE);
					}
				});
			};
		}.start();
	}
	
	private class Myadapter extends BaseAdapter{

		@Override
		public int getCount() {
			System.out.println("getCount");
			return blackNumbers.size();
		}
		//根据条目的位置，获取条目对应的数据
		@Override
		public Object getItem(int position) {
			System.out.println("getItem");
			return blackNumbers.get(position);
		}
		//获取条目的id
		@Override
		public long getItemId(int position) {
			System.out.println("getItemId");
			return position;
		}
		//设置条目的样式
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			System.out.println("getView");
			//1.创建盒子
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.blacknumber_listview_item, null);
				viewHolder = new ViewHolder();
				//2.将view.findviewbyid添加到盒子中
				viewHolder.tv_blacknumberitem_blacknumber = (TextView) convertView.findViewById(R.id.tv_blacknumberitem_blacknumber);
				viewHolder.tv_blacknumberitem_mode = (TextView) convertView.findViewById(R.id.tv_blacknumberitem_mode);
				viewHolder.iv_blacknumberitem_delete = (ImageView) convertView.findViewById(R.id.iv_blacknumberitem_delete);
				//3.将盒子添加要复用的view对象中，进行一起复用
				convertView.setTag(viewHolder);
			}else{
				//4.将复用缓存中的盒子取出来
				viewHolder = (ViewHolder) convertView.getTag();
			}
			//5.使用盒子中view.findviewbyid出来的控件
			//根据条目的位置获取对应的数据
			final BlackNumberInfo blackNumberInfo = blackNumbers.get(position);
			viewHolder.tv_blacknumberitem_blacknumber.setText(blackNumberInfo.blackNumber);
			//设置拦截模式
			int mode = blackNumberInfo.mode;
			//根据mode设置相应的文字显示
			switch (mode) {
			case BlackNumberContants.BLACKNUMBER_CALL:
				viewHolder.tv_blacknumberitem_mode.setText("电话拦截");
				break;
			case BlackNumberContants.BLACKNUMBER_SMS:
				viewHolder.tv_blacknumberitem_mode.setText("短信拦截");
				break;
			case BlackNumberContants.BLACKNUMBER_ALL:
				viewHolder.tv_blacknumberitem_mode.setText("全部拦截");
				break;
			}
			//删除操作
			viewHolder.iv_blacknumberitem_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Builder builder = new Builder(BlackNumberActivity.this);
					builder.setMessage("您是否删除黑名单"+blackNumberInfo.blackNumber);
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//删除黑名单
							//1.删除数据库中的数据
							boolean isdelete = blackNumberDao.deleteBlackNumber(blackNumberInfo.blackNumber);
							//2.数据库删除成功，操作界面
							if (isdelete) {
								//删除界面数据，更新界面
								blackNumbers.remove(position);//根据条目的位置，删除相应的条目
								//更新界面
								myadapter.notifyDataSetChanged();
							}
						}
					});
					builder.setNegativeButton("取消", null);//当取消操作，除了隐藏对话框之外什么都没有做，可以使用null作为参数，表示隐藏对话框
					builder.show();
				}
			});
			
			return convertView;
		}
		
	}
	
	static class ViewHolder{
		TextView tv_blacknumberitem_blacknumber,tv_blacknumberitem_mode;
		ImageView iv_blacknumberitem_delete;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
