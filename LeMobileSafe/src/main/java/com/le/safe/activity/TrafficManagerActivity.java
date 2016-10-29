package com.le.safe.activity;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dell.myapplication.R;
import com.le.safe.bean.AppInfo;
import com.le.safe.engine.AppEngine;

import java.util.List;


public class TrafficManagerActivity extends AppCompatActivity {

	private ListView mApplication;
	private List<AppInfo> list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);
		
		/*TrafficStats.getUidRxBytes(uid);//获取应用程序下载流量，uid：应用程序的标示,返回的结果就是以b为单位的流量
		TrafficStats.getUidTxBytes(uid);//获取应用程序上传流量
		//TrafficStasts:获取的手机中proc中存储流量的目录来进行，proc目录手机重启，数据清空
		 */	
		initView();
		fillData();
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		mApplication = (ListView) findViewById(R.id.lv_traffic_application);
	}
	/**
	 * 获取系统中安装的应用程序信息
	 */
	private void fillData() {
		new Thread(){
			public void run() {
				list = AppEngine.getAPPMessage(getApplicationContext());
				
				runOnUiThread(new Runnable() {
					public void run() {
						mApplication.setAdapter(new Myadapter());
					}
				});
			};
		}.start();
	}
	
	
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
			convertView = View.inflate(getApplicationContext(), R.layout.appmanager_listview_item, null);
			ImageView mIcon = (ImageView) convertView.findViewById(R.id.iv_homegridviewitem_icon);
			TextView mTitle = (TextView) convertView.findViewById(R.id.tv_homegridviewitem_title);
			TextView mDesc = (TextView) convertView.findViewById(R.id.tv_homegridviewitem_desc);
			TextView mSize = (TextView) convertView.findViewById(R.id.tv_appmanager_memroysize);
			
			//获取数据
			AppInfo appInfo = list.get(position);
			mIcon.setImageDrawable(appInfo.icon);
			mTitle.setText(appInfo.name);
			//获取应用程序上传下载流量
			long uidRxBytes = TrafficStats.getUidRxBytes(appInfo.uid);
			long uidTxBytes = TrafficStats.getUidTxBytes(appInfo.uid);
			mDesc.setText("上传:"+Formatter.formatFileSize(getApplicationContext(), uidTxBytes));
			mSize.setText("下载:"+Formatter.formatFileSize(getApplicationContext(), uidRxBytes));
			
			return convertView;
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
