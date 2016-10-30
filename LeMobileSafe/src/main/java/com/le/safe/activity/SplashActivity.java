package com.le.safe.activity;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.myapplication.R;
import com.le.safe.service.ProtectedService;
import com.le.safe.utils.Contants;
import com.le.safe.utils.PackageUtil;
import com.le.safe.utils.ServiceUtil;
import com.le.safe.utils.SharedPreferencesUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * splash欢迎界面
 */
public class SplashActivity extends BaseActivity {
	/**
	 * 请求路径
	 */
	private static final String CONNECTURL = "http://192.168.3.100:8080/updateinfo.html";
	private static final int INSTALLREQUESTCODE = 100;
	private TextView mVersionName;
	private int mNewCode;
	private String mApkurl;
	private String mMsg;
	private ProgressDialog progressDialog;

	/**
	 * 创建布局
	 * @return
     */
	@Override
	protected View createXMLView() {
		View view = View.inflate(this,R.layout.activity_splash,null);
		return view;
	}

	/**
	 * 初始化控件数据
	 */
	@Override
	protected void initData() {
		initView();
		copyDB("address.db");
		copyDB("commonnum.db");
		copyDB("antivirus.db");
		if (!ServiceUtil.isServiceRunning(getApplicationContext(), "cn.itcast.mobliesafexian05.service.ProtectedService")) {
			startService(new Intent(this,ProtectedService.class));
		}
		shortCut();
	}

	/**
	 * 创建快捷方式
	 */
	private void shortCut() {
		if (!SharedPreferencesUtil.getBoolean(getApplicationContext(), Contants.SHORTCUT, false)) {
			//系统允许创建多个快捷方式
			Intent intent = new Intent();
			intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "西安手机卫士05");//设置快捷方式的名称
			//设置快捷方式的图标
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
			//设置快捷方式的意图
			Intent value = new Intent(this,SplashActivity.class);
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, value);
			//发送广播
			sendBroadcast(intent);
			//如果创建了快捷方式，保存状态，根据状态去判断是否需要再次创建快捷方式
			SharedPreferencesUtil.saveBoolean(getApplicationContext(), Contants.SHORTCUT, true);
		}
	}
	/**
	 * 拷贝数据库
	 */
	private void copyDB(String name) {
		File file = new File(getFilesDir(), name);
		//判断文件是否存在，存在，不去拷贝
		if (!file.exists()) {
			//assets管理者
			AssetManager assetManager = getAssets();
			InputStream in = null;
			FileOutputStream out = null;
			try {
				//将数据读取到字节流中
				in = assetManager.open(name);
				//getFilesDir() : 手机中data目录下的应用程序包名中files目录
				//getCacheDir() : 获取缓存目录
				out = new FileOutputStream(file);
				//读写操作
				byte[] b = new byte[1024];//设置缓冲区
				int len = -1;
				while((len=in.read(b)) !=-1){
					//byteOffset : 从哪个位置开始写入
					//byteCount : 写入的多少的长度
					out.write(b, 0, len);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		mVersionName = (TextView) findViewById(R.id.tv_splash_version);
		//设置显示版本名称
		mVersionName.setText("版本："+ PackageUtil.getVersionName(this));

		//设置handler延迟任务
		//runnable:执行的操作
		//delayMillis : 延迟的时间
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				//根据保存的设置自动更新的按钮的状态来设置是否进行提示更新操作
				if (SharedPreferencesUtil.getBoolean(SplashActivity.this, Contants.UPDATE, true)) {
					//1.连接服务器，查看是否有最新版本
					update();
				}else{
					//跳转到主界面
					enterHome();
				}
			}
		}, 2000);
		
	}
	/**
	 * 连接服务器，查看是否有最新版本
	 */
	private void update() {
		enterHome();
		//1连接服务器，联网操作，在子线程中
		//权限
		//使用第三方框架进行联网操作
		/*new Thread(){
			public void run() {
				HttpURLConnection
			};
		}.start();*/
		//connTimeout : 连接超时时间
//		HttpUtils httpUtils = new HttpUtils(3000);
		//send : 发送请求
		//method :请求方式
		//url : 请求路径
		//callBack :requestCallBack  请求回调函数
		//<T> : 表示获取数据的类型
//		httpUtils.send(HttpRequest.HttpMethod.GET.GET, CONNECTURL, new RequestCallBack<String>() {
//			//请求成功调用的方法
//			//ResponseInfo : 服务器返回的数据
//			@Override
//			public void onSuccess(ResponseInfo<String> resultInfo) {
//				//1.2获取服务器返回的数据，问题：服务器应该返回什么数据  ： code:新版的版本号      apkurl：新版本下载路径      msg：新版本的描述信息
//				//服务器返回的数据都是以流的形式展示，考虑服务器是如何形式封装数据，xml  json
//				String json = resultInfo.result;
//
//				System.out.println("连接服务器成功,服务器数据："+json);
//				//1.3解析数据
//				processJSON(json);
//
//			}
//			//请求失败调用的方法
//			@Override
//			public void onFailure(com.lidroid.xutils.exception.HttpException error, String msg) {
//				System.out.println("连接服务器失败");
//				//1.4跳转到主界面
//				enterHome();
//			}
//
//
//		});
	}
	/**
	 * 解析服务器返回的数据
	 */
	protected void processJSON(String json) {
		try {
			//将json串转成json对象
			JSONObject jsonObject = new JSONObject(json);
			//name : json串中对应的key
			mNewCode = jsonObject.getInt("code");
			mApkurl = jsonObject.getString("apkurl");
			mMsg = jsonObject.getString("msg");
			System.out.println("code:"+mNewCode+"   apkurl:"+mApkurl+"  msg:"+mMsg);
			
			//2.判断是否有最新版本
			//那新版本的版本号和本地的版本进行比较，一致：没有最新版本，不一致：有最新版本，弹出对话框提示更新
			if (mNewCode == PackageUtil.getVersionCode(this)) {
				//一致，没有最新版本，跳转到主界面
				enterHome();
			}else{
				//不一致，弹出对话框，提示更新
				showUpdateDialog();
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 跳转主界面的方法
	 */
	protected void enterHome() {
		Intent intent = new Intent(this,HomeActivity.class);
		startActivity(intent);
		//跳转主界面之后，销毁splash界面
		finish();
	}
	/**
	 * 提示更新的对话框
	 */
	private void showUpdateDialog() {
		//创建对话框
		Builder builder = new Builder(this);
		//设置对话框是否能够消失，cancelable：true:可以  false:不可以
		//builder.setCancelable(false);
		//设置对话框标题
		builder.setTitle("发现新版本："+mNewCode);
		//设置对话框的图标
		builder.setIcon(R.drawable.ic_launcher);
		//设置对话框显示内容
		builder.setMessage(mMsg);
		//设置对话框消失的监听
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
			}
		});
		//设置更新按钮
		builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//下载最新app操作
				downloadAPK();
			}
		});
		//设置取消按钮
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//取消操作
				//1.隐藏对话框
				dialog.dismiss();//隐藏对话框
				//2.跳转到主界面
				enterHome();
			}
		});
		//显示对话框
		builder.show();
		//builder.create().show();
	}
	/**
	 * 下载最新的apk
	 */
	protected void downloadAPK() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			
			progressDialog = new ProgressDialog(this);
			progressDialog.setCancelable(false);//设置对话框不能取消
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			
			progressDialog.show();
			
			//下载操作
			//连接服务器，联网操作，在子线程中去实现
			HttpUtils httpUtils = new HttpUtils();
			//下载操作
			//url :　下载路径
			//target : 保存路径
			//callback ： RequestCallBack  连接请求回调
			//问题：1.将下载路径更改有效路径   2.sdk权限  3.sdk没有挂载成功 4.生成一个2.0版本的apk
			httpUtils.download(mApkurl, "/mnt/sdcard/mobliesafeXiAn05_2.0.apk", new RequestCallBack<File>() {


				@Override
				public void onSuccess(ResponseInfo arg0) {
					//隐藏进度对话框
					progressDialog.dismiss();
					
					//安装最新版本apk
					installAPK();
				}
				

				//展示下载进度
				//total : 总进度
				//current : 当前进度
				//isUploading : 是否支持回调上传
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					super.onLoading(total, current, isUploading);
					//设置进度对话框下载进度
					progressDialog.setMax((int) total);//设置最大进度
					progressDialog.setProgress((int) current);//设置当前进度
				}
				@Override
				public void onFailure(com.lidroid.xutils.exception.HttpException error, String msg) {
					//跳转到主界面
					enterHome();
				}

			});
		}else{
			Toast.makeText(this, "没有发现可用的SD卡", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * 安装最新版本apk
	 */
	protected void installAPK() {
		//问题：如何安装最新版本的apk
		/**
		 * <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="content" />//content://
                <data android:scheme="file" />//file : 从文件中获取数据
                <data android:mimeType="application/vnd.android.package-archive" />
            </intent-filter>
		 */
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		//在隐式意图中，如果同时设置data和type数据，两种会相互覆盖替换
		/*intent.setData(Uri.fromFile(new File("/mnt/sdcard/mobliesafeXiAn05_2.0.apk")));
		intent.setType("application/vnd.android.package-archive");*/
		intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/mobliesafeXiAn05_2.0.apk")), "application/vnd.android.package-archive");
		//startActivity(intent);
		//当界面退出的时候，会调用上一个界面的OnActivityResult方法，让上一个界面进行相应的处理
		startActivityForResult(intent, INSTALLREQUESTCODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//跳转主界面，因为始终只是两个界面在进行操作，不存在第三种操作
		enterHome();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
