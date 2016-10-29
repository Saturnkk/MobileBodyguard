package com.le.safe.bean;

import android.graphics.drawable.Drawable;

public class ProcessInfo {

	public String name;
	public String packageName;
	public Drawable icon;
	public long memorySize;
	public boolean isSystem;
	//因为checkbox不可以点击不可选中，所以需要使用变量来保存checkbox的状态
	public boolean isChecked=false;
	
}
