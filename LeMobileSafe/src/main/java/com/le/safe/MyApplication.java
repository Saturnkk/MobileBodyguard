package com.le.safe;

import android.app.Application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

//Application : 当前的应用程序，程序打开的，先运行application,然后才去运行activity
public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("Application启动了");
		//设置捕获程序出现的异常
//		Thread.currentThread().setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
	}
	
	private class MyUncaughtExceptionHandler implements UncaughtExceptionHandler{
		//当有未捕获的异常的时候调用
		//Throwable : 错误或者异常的父类，表示异常或者错误
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			System.out.println("哥捕获了异常..呵呵...");
			try {
				ex.printStackTrace();//打印出异常信息
				ex.printStackTrace(new PrintStream(new File("mnt/sdcard/error.log")));//将异常信息保存到文件中
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			//杀死进程，闪退
			//自己杀死自己，自杀
			//android.os.Process.myPid() : 获取当前应用程序的进程的pid
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		
	}
}
