package com.le.safe.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.le.safe.utils.Contants;
import com.le.safe.utils.SharedPreferencesUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import org.json.JSONException;
import org.json.JSONObject;



public class GPSService extends Service {

	private LocationManager locationManager;
	private MyLocationListener myLocationListener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 1.位置的管理者
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		myLocationListener = new MyLocationListener();
		// provider :定位的方式
		// minTime : 定位的最小间隔时间
		// minDistance : 定位最小距离间隔
		// listener : 定位的监听
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, myLocationListener);
	}

	private class MyLocationListener implements LocationListener {
		// 当定位位置改变的是调用
		// location : 当前的位置
		@Override
		public void onLocationChanged(Location location) {
			location.getAccuracy();// 获取精确的位置
			location.getAltitude();// 获取海拔
			location.getSpeed();// 获取设备移动的速度
			location.getTime();// 获取定位的时间

			double latitude = location.getLatitude();// 获取纬度，平行赤道
			double longitude = location.getLongitude();// 获取经度
			System.out.println("经度："+longitude+" 纬度："+latitude);
			//1.通过经纬获取实际地理位置
			getLocation(longitude,latitude);
			//2.将地理位置发送给安全号码
		}

		// 当定位状体改变的调用
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		// 当定位方式可用的调用
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		// 当定位方式不可用的时候调用
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 停止定位
		locationManager.removeUpdates(myLocationListener);// 高版本中android系统要求gps必须有用户自己打开或者关闭定位
	}
	/**
	 * 通过经纬度获取地理位置
	 * @param longitude
	 * @param latitude
	 */
	public void getLocation(double longitude, double latitude) {
		/**
		 * 	https://www.juhe.cn/docs/api/id/14/aid/30
		 *  接口地址：http://lbs.juhe.cn/api/getaddressbylngb
			支持格式：json/xml
			请求方式：get
			请求示例：http://lbs.juhe.cn/api/getaddressbylngb?lngx=116.407431&lngy=39.914492
		 */
		//联网操作，耗时操作，子线程
		HttpUtils httpUtils = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("lngx", longitude+"");
		params.addQueryStringParameter("lngy", latitude+"");
		//params : 请求参数
		httpUtils.send(HttpMethod.GET, "http://lbs.juhe.cn/api/getaddressbylngb", params, new RequestCallBack<String>() {


			@Override
			public void onSuccess(ResponseInfo<String> resultinfo) {
				String json = resultinfo.result;
				System.out.println(json);
				//解析数据
				processJSON(json);
			}
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	/**
	 * 解析json数据
	 * @param json
	 */
	protected void processJSON(String json) {
		/**
		 * 	{
			    "resultcode": "1",
			    "resultinfo": "Successful",
			    "row": {
			        		"status": "OK",
			        "result": {
									            "location": {
									                "lng": 108.861213,
									                "lat": 34.188719
									            },
			            "formatted_address": "陕西省西安市雁塔区锦业二路",
			            "business": "郭杜",
			            "addressComponent": {
			                "city": "西安市",
			                "direction": "",
			                "distance": "",
			                "district": "雁塔区",
			                "province": "陕西省",
			                "street": "锦业二路",
			                "street_number": ""
			            },
			            "cityCode": 233
			        }
			    }
			}
		 */
		try {
			JSONObject jsonObject = new JSONObject(json);
			//解析row
			JSONObject rowObject = jsonObject.getJSONObject("row");
			//解析result
			JSONObject resultObject = rowObject.getJSONObject("result");
			//解析formatted_address
			String address = resultObject.getString("formatted_address");
			System.out.println("地址："+address);
			//将地址发送给安全号码
			sendMessage(address);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 发送地址给安全号码
	 */
	private void sendMessage(String address) {
		//获取安全号码
		String safenumber = SharedPreferencesUtil.getString(getApplicationContext(), Contants.SAFENUMBER, "5556");
		if (!TextUtils.isEmpty(safenumber) && !TextUtils.isEmpty(address)) {
			//发送短信
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(safenumber, null, address, null, null);
			//为了避免短信的频繁发送，发送完短信，关闭服务
			this.stopSelf();//停止服务操作
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
