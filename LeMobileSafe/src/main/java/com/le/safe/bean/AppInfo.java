package com.le.safe.bean;

import android.graphics.drawable.Drawable;

public class AppInfo {

	public String name;
	public String packageName;
	public Drawable icon;
	public boolean isSD;
	public long memorySize;
	public boolean isSystem;
	public int uid;
	public AppInfo(String name, String packageName, Drawable icon,
			boolean isSD, long memorySize, boolean isSystem, int uid) {
		super();
		this.name = name;
		this.packageName = packageName;
		this.icon = icon;
		this.isSD = isSD;
		this.memorySize = memorySize;
		this.isSystem = isSystem;
		this.uid = uid;
	}
	
}
