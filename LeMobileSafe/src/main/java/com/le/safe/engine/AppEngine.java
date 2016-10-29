package com.le.safe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.le.safe.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AppEngine {

	/**
	 * 获取系统安装应用程序
	 * @return
	 */
	public static List<AppInfo> getAPPMessage(Context context){
		List<AppInfo> list = new ArrayList<AppInfo>();
		//获取包的管理者
		PackageManager pm = context.getPackageManager();
		//获取系统中所有安装软件清单文件中的基本信息
		List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
		for (PackageInfo packageInfo : installedPackages) {
			int uid = packageInfo.applicationInfo.uid;
			//包名
			String packageName = packageInfo.packageName;
			//获取应用程序的application信息
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			//名称
			String name = applicationInfo.loadLabel(pm).toString();
			//图片
			Drawable icon = applicationInfo.loadIcon(pm);
			//获取data\app\xxx.apk,占用空间
			String sourceDir = applicationInfo.sourceDir;
			long memorySize = new File(sourceDir).length();
			//是否系统应用,获取应用的所有的标签信息
			int flags = applicationInfo.flags;
			boolean isSystem;
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				//系统程序
				isSystem = true;
			}else{
				//用户程序
				isSystem = false;
			}
			//是否安装在SD卡
			boolean isSD;
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
				//SD卡
				isSD = true;
			}else{
				//内存
				isSD = false;
			}
			//保存到bean类
			AppInfo appInfo = new AppInfo(name, packageName, icon, isSD, memorySize, isSystem, uid);
			//将bean类保存到集合中
			list.add(appInfo);
		}
		return list;
	}
	
}
