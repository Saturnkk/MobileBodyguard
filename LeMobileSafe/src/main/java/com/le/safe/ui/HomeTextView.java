package com.le.safe.ui;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

public class HomeTextView extends TextView {
	
	//在代码中使用控件的调用
	public HomeTextView(Context context) {
		//super(context);
		this(context,null);
	}
	//在布局文件中使用的时候调用的方法
	//布局控件最终都会通过反射转化成相应的代码来实现，控件中的属性都会反射到AttributeSet参数中保存
	//通过AttributeSet获取出布局文件中控件设置的属性
	public HomeTextView(Context context, AttributeSet attrs) {
		//super(context, attrs);
		this(context,attrs,0);
	}
	//在代码内部调用
	//defStyle : textview样式，默认样式
	public HomeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		/**
		 *  	android:singleLine="true"
                android:ellipsize="marquee"
                android:focusableInTouchMode="true"
                android:focusable="true"
                android:marqueeRepeatLimit="marquee_forever"
		 */
		setSingleLine();
		setEllipsize(TruncateAt.MARQUEE);
		setFocusableInTouchMode(true);
		setFocusable(true);
		setMarqueeRepeatLimit(-1);
	}
	//是否可获取焦点，true:可以   false:不可以
	@Override
	public boolean isFocused() {
		return true;
	}
	//当焦点改变的调用
	//focused :当前的view是否有焦点
	//direction : 焦点方向
	//previouslyFocusedRect : 焦点来自哪个控件
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		//当textview没有焦点的时候，不去调用父类的取消焦点操作
		if (focused) {
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
		}
	}
	//当窗口焦点改变调用
	//hasWindowFocus : 当前窗口是否有当前控件的焦点
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if (hasWindowFocus) {
			super.onWindowFocusChanged(hasWindowFocus);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
