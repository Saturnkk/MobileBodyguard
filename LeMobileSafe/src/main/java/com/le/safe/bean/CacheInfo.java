package com.le.safe.bean;

import android.graphics.drawable.Drawable;

public class CacheInfo {

	/**
	 * 名称
	 */
	public String name;
	/**
	 * 包名
	 */
	public String packageName;
	/**
	 * 图标
	 */
	public Drawable icon;
	/**
	 * 缓存大小
	 */
	public long cacheSize;
	public CacheInfo(String name, String packageName, Drawable icon,
			long cacheSize) {
		super();
		this.name = name;
		this.packageName = packageName;
		this.icon = icon;
		this.cacheSize = cacheSize;
	}
	
}
