package com.le.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dell.myapplication.R;
import com.le.safe.bean.ContactsInfo;
import com.le.safe.engine.ContactsEngine;

import java.util.List;


public class ContactsActivity extends AppCompatActivity implements OnItemClickListener{

	private List<ContactsInfo> contacts;
	private ListView mContacts;
	private ProgressBar mLoading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		//初始化控件  -> 加载数据
		initView();
		fillData();
	}
	/**
	 * 加载数据
	 */
	private void fillData() {
		//耗时操作，放到子线程中执行
		new Thread(){
			public void run() {
				contacts = ContactsEngine.getALLContacts(getApplicationContext());
				//在获取完数据之后才能设置adapter
				runOnUiThread(new Runnable() {//封装了一个Handler，在主线程执行
					public void run() {
						mContacts.setAdapter(new Myadapter());
						//显示完数据，隐藏进度条
						mLoading.setVisibility(View.GONE);
					}
				});
			};
		}.start();
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		mContacts = (ListView) findViewById(R.id.lv_contacts_contacts);
		mLoading = (ProgressBar) findViewById(R.id.loading);
		
		mContacts.setOnItemClickListener(this);
		
	}	
	
	private class Myadapter extends BaseAdapter{
		//获取条目的个数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return contacts.size();
		}
		//根据条目的位置获取条目对应的数据
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return contacts.get(position);
		}
		//获取条目的id
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		//设置条目的样式
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			/*if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.contacts_listview_item, null);
			}*/
			View view;
			//1.创建一个盒子,没有复用缓存的时候
			ViewHolder viewHolder;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(), R.layout.contacts_listview_item, null);	
				viewHolder = new ViewHolder();
				//2.将view.findviewbyid放到盒子中
				viewHolder.mIcon = (ImageView) view.findViewById(R.id.iv_homegridviewitem_icon);
				viewHolder.mName = (TextView) view.findViewById(R.id.tv_homegridviewitem_title);
				viewHolder.mNumber = (TextView) view.findViewById(R.id.tv_homegridviewitem_desc);
				//3.将盒子放到复用的view对象中进行一起复用
				view.setTag(viewHolder);//将viewholder和view对象绑定在一起
			}else{
				view = convertView;
				//4.复用缓存对象，从复用的缓存对象中获取一起复用的盒子
				viewHolder = (ViewHolder) view.getTag();
			}
			
			/*ImageView mIcon = (ImageView) view.findViewById(R.id.iv_homegridviewitem_icon);
			TextView mName = (TextView) view.findViewById(R.id.tv_homegridviewitem_title);
			TextView mNumber = (TextView) view.findViewById(R.id.tv_homegridviewitem_desc);*/
			
			//获取数据
			//5.使用盒子中存在的通过view.findviewbyid得到的控件
			ContactsInfo contactsInfo = contacts.get(position);
			viewHolder.mName.setText(contactsInfo.name);
			viewHolder.mNumber.setText(contactsInfo.number);
			
			//根据联系人的id获取联系人头像
			Bitmap bitmap = ContactsEngine.getContactIcon(getApplicationContext(), contactsInfo.contactsID);
			if (bitmap!=null) {
				viewHolder.mIcon.setImageBitmap(bitmap);
			}else{
				viewHolder.mIcon.setImageResource(R.drawable.ic_contact);
			}
			
			return view;
		}
		
	}
	//存放view.findviewbyid的盒子
	class ViewHolder{
		ImageView mIcon;
		TextView mName;
		TextView mNumber;
	}
	//position : 点击条目的位置
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
			//1.传递数据，传递给setUp3activity
			Intent intent = new Intent();
			intent.putExtra("number", contacts.get(position).number);
			//resultcode:返回码，结果码
			//设置返回结果的操作，将结果传递给之前调用当前activity的activity
			setResult(Activity.RESULT_OK, intent);
			//2.销毁页面
			finish();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
