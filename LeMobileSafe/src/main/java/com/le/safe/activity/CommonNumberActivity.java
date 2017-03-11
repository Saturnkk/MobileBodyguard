package com.le.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.example.dell.myapplication.R;
import com.le.safe.db.dao.CommonNumberDao;

import java.util.List;



public class CommonNumberActivity extends BaseActivity {

	private ExpandableListView mExpandListView;
	private List<CommonNumberDao.Group> groups;
	private int currentGroup =-1;

	@Override
	protected View createXMLView() {
		View view = View.inflate(this,R.layout.activity_commonnumber,null);
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
		mExpandListView = (ExpandableListView) findViewById(R.id.eblv_commonnumber_phones);
		
		mExpandListView.setOnGroupClickListener(new OnGroupClickListener() {
			//groupPosition : 组的位置
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				//自己控制组的打开和关闭
				//关闭已经打开的组
				if (currentGroup == groupPosition) {
					//关闭操作
					mExpandListView.collapseGroup(groupPosition);
					currentGroup = -1;
				}else{
					//打开点击的组，关闭其他组
					//打开一个组
					mExpandListView.expandGroup(groupPosition);
					//关闭其他组
					//获取组的总个数
					int size = groups.size();
					for (int i = 0; i <size; i++) {
						if (i != groupPosition) {
							mExpandListView.collapseGroup(i);
						}
					}
					mExpandListView.setSelectedGroup(groupPosition);//选择一个组
					currentGroup = groupPosition;
				}
				//True if the click was handled 
				//返回true表示事件执行完成，返回false表示操作没有执行完
				return true;
			}
		});
		
		mExpandListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:"+groups.get(groupPosition).child.get(childPosition).number));
				startActivity(intent);
				return true;
			}
		});
		
		fillData();
		
		mExpandListView.setAdapter(new Myadapter());
	}

	/**
	 * 获取数据
	 */
	private void fillData() {
		groups = CommonNumberDao.getGroups(getApplicationContext());
	}

	private class Myadapter extends BaseExpandableListAdapter {
		// 获取组的个数
		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return groups.size();
		}

		// 获取孩子的个数
		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return groups.get(groupPosition).child.size();
		}

		// 根据组的位置获取组对应的数据
		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return groups.get(groupPosition);
		}

		// 根据组的位置和孩子位置获取孩子对应的数据
		// groupPosition : 组的位置
		// childPosition ： 组中的孩子 位置
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return groups.get(groupPosition).child.get(childPosition);
		}

		// 获取组的id
		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		// 获取孩子的id
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		// 判断是否设置id,如果设置了id，返回true,否则，返回false
		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		// 设置组的样式
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			textView.setBackgroundColor(Color.parseColor("#33000000"));
			textView.setTextSize(20);
			textView.setTextColor(Color.BLACK);
			textView.setText(groups.get(groupPosition).name);
			textView.setPadding(8, 8, 8, 8);
			return textView;
		}

		// 设置孩子的样式
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			textView.setBackgroundColor(Color.parseColor("#ffffff"));
			textView.setTextSize(20);
			textView.setTextColor(Color.BLACK);
			textView.setText(groups.get(groupPosition).child.get(childPosition).name
					+ "\n"
					+ groups.get(groupPosition).child.get(childPosition).number);
			textView.setPadding(8, 8, 8, 8);
			return textView;
		}

		// 设置孩子是否可以点击，true:可以，false：不可以
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

	}

}
