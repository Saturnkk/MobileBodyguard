package com.le.safe.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.dell.myapplication.R;


public class AddressDialog extends Dialog {

	private ListView mListView;

	public AddressDialog(Context context) {
		super(context,R.style.AddressDialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_addressdialog);
		mListView = (ListView) findViewById(R.id.lv_customaddrssdialog_bgs);
		
		
		//获取dialog所在的窗口
		Window window = getWindow();
		LayoutParams params = window.getAttributes();//获取activity在窗口中属性
		params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		//将属性重新设置给window，这样activity才能使用最新的属性
		window.setAttributes(params);
	}
	//为了避免出现，其他地方使用自定义dialog的时候出现数据更改样式，另一个同样使用自定义dialog的地方样式也发生更改
	//一般，哪个activity使用自定义dialog,就把数据传递给自定义的dialog,由自定义的dialog来显示
	/**
	 * 将使用自定义dialog的activity中的adapter传递到dialog中，并设置给listview
	 * @param adapter
	 */
	public void setadapter(BaseAdapter adapter){
		mListView.setAdapter(adapter);
	}
	/**
	 * 避免出现使用自定义dialog的activity出现点击listview条目出现重复操作
	 * @param itemClickListener
	 */
	public void setItemClickListener(OnItemClickListener itemClickListener){
		mListView.setOnItemClickListener(itemClickListener);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
