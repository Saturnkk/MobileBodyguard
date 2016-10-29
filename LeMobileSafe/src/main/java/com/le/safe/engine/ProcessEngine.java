package com.le.safe.engine;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.example.dell.myapplication.R;
import com.le.safe.bean.ProcessInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;



public class ProcessEngine {
	/**
	 * 获取正在运行进程数
	 * @return
	 */
	public static int getRunningProcess(Context context){
		//进程的管理者
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		return runningAppProcesses.size();
	}
	/**
	 * 总进程数据
	 * @return
	 */
	public static int getTotalProcess(Context context){
		//一个应用对应一个进程
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
		return installedPackages.size();
	}
	/**
	 * 获取可用内存
	 * @return
	 */
	public static long getFreeMemory(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);//获取系统内存的信息，并保存到MemoryInfo中
		long freememory = outInfo.availMem;//获取系统可用内存
		//outInfo.totalMem//获取总内存
		return freememory;
	}
	
	/**
	 * 获取总内存
	 * @return
	 */
	@SuppressLint("NewApi")
	public static long getAllMemory(Context context){
		long totalmemory = 0;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);//获取系统内存的信息，并保存到MemoryInfo中
		
		//获取手机系统SDK版本，根据SDK版本进行操作
		//Build.VERSION.SDK_INT :获取手机系统的SDK版本
		if (Build.VERSION.SDK_INT >= 16) {
			totalmemory = outInfo.totalMem;//获取系统可用内存
		}else{
			totalmemory = getAllMemory();
		}
		//outInfo.totalMem//获取总内存
		return totalmemory;
	}
	/**
	 * 兼容低版本获取总内存操作
	 * @return
	 */
	private static long getAllMemory() {
		File file = new File("proc/meminfo");
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String readLine = br.readLine();
			readLine = readLine.replace("MemTotal:", "");
			readLine = readLine.replace("kB", "");
			readLine = readLine.trim();
			Long totalmemory = Long.valueOf(readLine);
			//kb - > b   1kb = 1024b
			return totalmemory*1024;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 获取系统正在运行的进程信息
	 * @return
	 */
	public static List<ProcessInfo> getRunningProcessInfo(Context context){
		
		List<ProcessInfo> list = new ArrayList<ProcessInfo>();
		
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		
		PackageManager pm = context.getPackageManager();
		
		//获取正在运行进程的信心
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			
			ProcessInfo processInfo = new ProcessInfo();
			
			String packageName = runningAppProcessInfo.processName;//进程名就是包名
			
			processInfo.packageName = packageName;
			
			//int[] pids ： 进程id的数组，里面存放的是进程id，保存几个进程的id,就会返回几个进程的内存信息
			//获取进程的内存信息，保存是以kb的形式实现
			android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
			long memorysize = processMemoryInfo[0].getTotalPss()*1024;
			
			processInfo.memorySize = memorysize;
			
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
				Drawable icon = applicationInfo.loadIcon(pm);
				
				processInfo.icon = icon;
				
				String name = applicationInfo.loadLabel(pm).toString();
				
				processInfo.name = name;
				
				int flags = applicationInfo.flags;
				boolean isSystem;
				if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
					//系统进程
					isSystem = true;
				}else{
					//用户进程
					isSystem = false;
				}
				processInfo.isSystem = isSystem;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				//android是通过linux内核开发的，所以其中含有用c/c++编写的程序
				//如果拿不到application信息，设置默认信息
				processInfo.icon = context.getResources().getDrawable(R.drawable.ic_default);
				processInfo.name = packageName;
				processInfo.isSystem = true;
			}
			//将bean类保存到集合中
			list.add(processInfo);
		}
		return list;
		
	}
	
	
	
	
	
	
	
	
	
	
	
}
