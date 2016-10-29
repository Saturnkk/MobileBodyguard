package com.le.safe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dell.myapplication.R;


public class CustomProgressbar extends LinearLayout {

	private TextView mMemory;
	private ProgressBar mProgressbar;
	private TextView mUsed;
	private TextView mFree;

	public CustomProgressbar(Context context) {
		//super(context);
		this(context,null);
	}

	public CustomProgressbar(Context context, AttributeSet attrs) {
		//super(context, attrs);
		this(context,attrs,0);
	}
	public CustomProgressbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {
		View view = View.inflate(getContext(), R.layout.custom_progressbar, this);
		mMemory = (TextView) view.findViewById(R.id.tv_appmanager_memorytext);
		mProgressbar = (ProgressBar) view.findViewById(R.id.pb_appmanager_progressbar);
		mUsed = (TextView) view.findViewById(R.id.tv_appmanger_used);
		mFree = (TextView) view.findViewById(R.id.tv_appmanger_free);
	}
	//谁使用自定义控件，需要显示什么数据，直接调用方法将数据传递过来，进行显示操作
	/**
	 * 设置内存文本
	 * @param text
	 */
	public void setText(String text){
		mMemory.setText(text);
	}
	/**
	 * 设置已用文本
	 * @param use
	 */
	public void setUsed(String use){
		mUsed.setText(use);
	}
	/**
	 * 设置可用文本
	 * @param free
	 */
	public void setFree(String free){
		mFree.setText(free);
	}
	/**
	 * 设置progressbar的进度
	 * @param progress
	 */
	public void setProgress(int progress){
		mProgressbar.setProgress(progress);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
