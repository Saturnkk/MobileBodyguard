package com.le.safe.engine;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.ContactsContract;

import com.le.safe.bean.ContactsInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ContactsEngine {

	//获取联系人信息的操作
	//1.获取联系人的姓名、电话、唯一标示，把数据封装到bean类进行操作
	/**
	 * 获取系统所有联系人
	 */
	public static List<ContactsInfo> getALLContacts(Context context){
		//Thread.sleep(3000);//大约的值
		SystemClock.sleep(3000);
		List<ContactsInfo> list = new ArrayList<ContactsInfo>();
		//获取内容解析者
		ContentResolver resolver = context.getContentResolver();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[]{
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID
		};
		//uri:查询地址
		//projection : String[] 查询的字段
		//selection : 查询条件
		//selectionArgs : String[] 查询条件的参数
		//sortOrder : 排序
		Cursor cursor = resolver.query(uri, projection, null, null, null);
		//解析cursor
		if (cursor!=null) {
			while(cursor.moveToNext()){
				String name = cursor.getString(0);
				String number = cursor.getString(1);
				String contactID = cursor.getString(2);
				//保存bean类中
				/*ContactsInfo contactsInfo = new ContactsInfo();
				contactsInfo.name = name;*/
				ContactsInfo contactsInfo = new ContactsInfo(name, number, contactID);
				//将bean类添加到集合中
				list.add(contactsInfo);
			}
			//释放资源
			cursor.close();
		}
		return list;
	}
	//2.根据联系人唯一标示，获取联系人头像
	/**
	 * 根据联系人的id获取联系人的头像
	 * @param context
	 * @param contactID
	 * @return
	 */
	public static Bitmap getContactIcon(Context context,String contactID){
		//1.获取内容解析者
		ContentResolver resolver = context.getContentResolver();
		//2.获取uri地址
		//withAppendedPath : 将原有的路径上进行拼接路径
		//content://uri/contactid
		//http://www.baidu.com/jdk
		//baseUri : 基本的路径，类似http://www.baidu.com
		//pathSegment : 拼接路径，类似于jdk
		Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactID);
		//3.根据uri地址获取联系人的头像
		//openContactPhotoInputStream : 根据查询路径，获取联系人的图片
		//cr : 内容解析者
		//contacturi : uri地址
		InputStream in = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
		//4.将流转化成bitmap
		Bitmap bitmap = BitmapFactory.decodeStream(in);
		
		//5.关流操作
		if (in!= null) {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return bitmap;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
