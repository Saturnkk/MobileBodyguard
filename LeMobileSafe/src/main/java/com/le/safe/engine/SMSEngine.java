package com.le.safe.engine;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SMSEngine {
	
	public static class SMSinfo{
		public String address;
		public String date;
		public String type;
		public String body;
		public SMSinfo(String address, String date, String type, String body) {
			super();
			this.address = address;
			this.date = date;
			this.type = type;
			this.body = body;
		}
	}
	
	/**
	 * 获取备份短信
	 */
	public static void getAllSMS(Context context,ProgressDialog progressDialog){
		List<SMSinfo> list = new ArrayList<SMSinfo>();
		//获取短信
		//1.获取内容解析者
		ContentResolver resolver = context.getContentResolver();
		
		Uri uri = Uri.parse("content://sms");
		
		Cursor cursor = resolver.query(uri, new String[]{"address","date","type","body"}, null, null, null);
		
		if (cursor != null) {
			//设置最大进度
			//getCount() : 获取cursor中数据个数
			progressDialog.setMax(cursor.getCount());
			
			//设置当前进度
			int progress = 0;
			while(cursor.moveToNext()){
				SystemClock.sleep(1000);
				String address = cursor.getString(0);
				String date = cursor.getString(1);
				String type = cursor.getString(2);
				String body = cursor.getString(3);
				System.out.println("address:"+address+" date:"+date+" type:"+type+" body:"+body);
				//保存到bean类
				SMSinfo smsinfo = new SMSinfo(address, date, type, body);
				//将bean类添加到集合中
				list.add(smsinfo);
				
				progress++;
				progressDialog.setProgress(progress);
			}
			//获取gson对象
			Gson gson = new Gson();
			//将集合转化成json字符串
			String json = gson.toJson(list);
			try {
				FileWriter fileWriter = new FileWriter(new File("mnt/sdcard/sms.txt"));
				fileWriter.write(json);
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
