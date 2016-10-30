package com.le.safe.activity;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dell.myapplication.R;
import com.le.safe.bean.AntivirusInfo;
import com.le.safe.db.dao.AntivirusDao;
import com.le.safe.utils.MD5Uitl;

import java.util.ArrayList;
import java.util.List;

import circleprogress.ArcProgress;


public class AntivirusActivity extends AppCompatActivity {

	public ArcProgress mProgressBar;
	private PackageManager pm;
	private TextView mPackageName;
	private List<AntivirusInfo> list;
	private ListView mApplication;
	private Myadapter myadapter;
	private List<PackageInfo> packages;
	private LinearLayout mLLScan;
	private LinearLayout mLLPorgressbar;
	/**
	 * 病毒的个数
	 */
	private int totalCount = 0;
	private TextView mIsSafe;
	private LinearLayout mLLImageView;
	private ImageView mLeft;
	private ImageView mRight;
	private int width;
	private Button mScan;
	private UninstallReceiver uninstallReceiver;
	/**
	 * 卸载应用的标示
	 */
	private AntivirusInfo deleteAntivirusInfo;
	/**
	 * 卸载的广播接受者
	 * @author Administrator
	 *
	 */
	private class UninstallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//接受卸载的广播事件，进行处理
			//从list集合中删除卸载病毒信息
			list.remove(deleteAntivirusInfo);
			//更新界面
			myadapter.notifyDataSetChanged();
			//改变重新扫描显示的文本
			totalCount--;//卸载一个应用程序，病毒的个数少一个
			if (totalCount > 0) {
				//提示用户发现病毒
				mIsSafe.setText("发现"+totalCount+"个病毒");
			}else{
				//提示用户手机很安全
				mIsSafe.setText("您的手机很安全");
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antivirus);
		initView();
		//注册卸载的广播接受者
		uninstallReceiver = new UninstallReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.setPriority(1000);//先于系统接收到卸载应用的广播接受者
		//ACTION_PACKAGE_ADDED : 安装应用广播事件
		intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);//卸载的广播事件
		intentFilter.addDataScheme("package");
		registerReceiver(uninstallReceiver, intentFilter);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(uninstallReceiver);
	}
	//初始化控件
	private void initView() {
		mProgressBar = (ArcProgress) findViewById(R.id.arc_antivirus_progress);
		mPackageName = (TextView) findViewById(R.id.tv_antivirus_packagename);
		mApplication = (ListView) findViewById(R.id.lv_antivirus_application);
		mLLScan = (LinearLayout) findViewById(R.id.ll_antivirus_scan);
		mLLPorgressbar = (LinearLayout) findViewById(R.id.ll_antivirus_progressbar);
		mIsSafe = (TextView) findViewById(R.id.tv_antivirus_issafe);
		mLLImageView = (LinearLayout) findViewById(R.id.ll_antivrus_imageview);
		mLeft = (ImageView) findViewById(R.id.iv_antivirust_left);
		mRight = (ImageView) findViewById(R.id.iv_antivirust_right);
		mScan = (Button) findViewById(R.id.btn_antivirus_scan);
		
		scan();
	}
	/**
	 * 扫描操作
	 */
	private void scan() {
		//Ctrl+k:快速寻找选中的文本
		//Ctrl+l：快速寻找行数
		totalCount = 0;//初始化病毒的个数
		
		//重新扫描显示进度条布局，隐藏重新扫描和图片的布局
		mLLScan.setVisibility(View.GONE);
		mLLImageView.setVisibility(View.GONE);
		mLLPorgressbar.setVisibility(View.VISIBLE);
		
		
		list = new ArrayList<AntivirusInfo>();
		list.clear();
		
		pm = getPackageManager();
		new Thread(){
			public void run() {
				//1.设置进度条进度
				packages = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
				//当前应用总个数
				int total = packages.size();
				//进度条默认总进度就是100
				//mProgressBar.setMax(100);
				int progress=0;
				for (final PackageInfo packageInfo : packages) {
					SystemClock.sleep(300);
					//设置进度图条进度
					progress++;
					final int arcProgress = (int) (progress * 100f / total + 0.5f);
					runOnUiThread(new Runnable() {
						public void run() {
							//必须主线程中设置进度条的进度
							mProgressBar.setProgress(arcProgress);//当前进度百分比
							//2.设置显示扫描应用程序包名
							mPackageName.setText(packageInfo.packageName);
						}
					});
					//3.通过listview展示扫描应用程序的信息
					//3.1获取应用程序的信息
					try {
						ApplicationInfo applicationInfo = pm.getApplicationInfo(packageInfo.packageName, 0);
						String name = applicationInfo.loadLabel(pm).toString();
						Drawable icon = applicationInfo.loadIcon(pm);
						//3.2将信息保存到bean类
						AntivirusInfo antivirusInfo = new AntivirusInfo();
						antivirusInfo.name = name;
						antivirusInfo.icon = icon;
						antivirusInfo.packageName = packageInfo.packageName;
						
						//4.获取应用程序特征码，查询是否是病毒
						//获取应用程序的签名信息，返回的签名的数组，
						//比如在获取应用信息的时候添加PackageManager.GET_SIGNATURES标签信息，表示要额外获取应用的签名信息
						Signature[] singatures = packageInfo.signatures;
						String charsString = singatures[0].toCharsString();//将签名信息转化成string类型
						String md5 = MD5Uitl.passwordToMD5(charsString);
						//查询md5值是否在数据库中
						boolean b = AntivirusDao.isAntivirus(getApplicationContext(), md5);
						if (b) {
							//有病毒
							antivirusInfo.isAntivirus = true;
							list.add(0, antivirusInfo);
							totalCount++;
						}else{
							//不是病毒
							antivirusInfo.isAntivirus = false;
							//3.3将bean类保存到list集合
							list.add(antivirusInfo);
						}
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					//3.4通过listview展示数据
					runOnUiThread(new Runnable() {
						public void run() {
							if (myadapter == null) {
								myadapter = new Myadapter();
								mApplication.setAdapter(myadapter);
							}else{
								myadapter.notifyDataSetChanged();
							}
							//3.5滚动到listView最后一个条目
							mApplication.smoothScrollToPosition(myadapter.getCount()-1);
						}
					});
					//3.6扫描完成，回滚到第一个条目
					runOnUiThread(new Runnable() {
						public void run() {
							//判断是否扫描完成
							if (list.size() == packages.size()) {
								//回滚到第一个条目
								mApplication.smoothScrollToPosition(0);
								//隐藏进度条布局，显示重新扫描布局
								mLLPorgressbar.setVisibility(View.GONE);
								mLLScan.setVisibility(View.VISIBLE);
								//图片的布局显示
								mLLImageView.setVisibility(View.VISIBLE);
								//提示用户手机很安全或者手机发现几个病毒
								//判断病毒的个数，如果大于0表示有病毒，就显示发现几个病毒
								if (totalCount > 0) {
									//提示用户发现病毒
									mIsSafe.setText("发现"+totalCount+"个病毒");
								}else{
									//提示用户手机很安全
									mIsSafe.setText("您的手机很安全");
								}
								//动画
								//扫描完成，将进度条布局，拆分成两部分，一个向左一个向右，渐变+平移移出界面，同时重新扫描的布局，有透明->不透明显示
								//一个布局不能拆分成两部分，但是图片可以，所以要获取进度条布局的副本图片
								//1.获取进度条副本图片
								mLLPorgressbar.setDrawingCacheEnabled(true);//设置是否可以创建进度条布局的缓存图片，true:可以，false:不可以
								mLLPorgressbar.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);//设置图片的质量
								Bitmap drawingBitmap = mLLPorgressbar.getDrawingCache();//获取进度条布局的缓存图片
								//2.拆分图片
								Bitmap leftBitmap = getLeftBitmap(drawingBitmap);
								Bitmap rightBitmap = getRightBitmap(drawingBitmap);
								//3.将获取的图片分别存放到两个imageview中
								mLeft.setImageBitmap(leftBitmap);
								mRight.setImageBitmap(rightBitmap);
								//4.执行动画
								startAnimation();
								//5.重新扫描动画
								mScan.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										//执行返回的动画
										backAnimation();
									}
								});
							}
						}
					});
				}
			};
		}.start();
	}
	
	/**
	 * 返回的动画
	 */
	private void backAnimation() {
		//平移+透明渐变，同时重新扫描布局不透明到透明
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(
				ObjectAnimator.ofFloat(mLeft, "translationX", -width,0),
				ObjectAnimator.ofFloat(mRight, "translationX", width,0),
				ObjectAnimator.ofFloat(mLeft, "alpha", 0,1),
				ObjectAnimator.ofFloat(mRight, "alpha", 0,1),
				ObjectAnimator.ofFloat(mLLScan, "alpha", 1,0)
				);
		//属性动画的监听
		animatorSet.addListener(new AnimatorListener() {
			//动画开始的时候调用
			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			//动画重复的时候调用
			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			//动画结束的时候调用
			@Override
			public void onAnimationEnd(Animator animation) {
				//动画执行结束时,重新扫描
				scan();
			}
			//动画取消的时候调用
			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}
		});
		animatorSet.setDuration(2000);
		animatorSet.start();
		//animatorSet.cancel();//比如动画执行一半，在中途调用cancel()方法停止动画，是动画取消
	}
	
	/**
	 * 动画执行操作
	 */
	private void startAnimation() {
		//一个向左一个向右，透明渐变+平移效果，同时重新扫描布局透明 -> 不透明效果
		//属性组合动画
		AnimatorSet animatorSet = new AnimatorSet();
		//动画一起执行
		/*mLeft.setTranslationX(translationX)
		mLeft.setAlpha(alpha)*/
		//target : 执行动画的控件
		//propertyName : 控件执行动画操作
		//values : 动画执行的属性的值
		animatorSet.playTogether(
				ObjectAnimator.ofFloat(mLeft, "translationX", 0,-width),
				ObjectAnimator.ofFloat(mRight, "translationX", 0,width),
				ObjectAnimator.ofFloat(mLeft, "alpha", 1,0),
				ObjectAnimator.ofFloat(mRight, "alpha", 1,0),
				ObjectAnimator.ofFloat(mLLScan, "alpha", 0,1)
				);
		//动画的持续时间
		animatorSet.setDuration(2000);
		animatorSet.start();//开始动画
	}
	/**
	 * 获取左边图片操作
	 * @param drawingBitmap
	 * Bitmap : 图片的载体
	 */
	private Bitmap getLeftBitmap(Bitmap drawingBitmap) {
		//1.确定左边图片的宽高，宽是原图片一半，高不变
		width = (int) (drawingBitmap.getWidth()/2 + 0.5f);
		int height = drawingBitmap.getHeight();
		//2.创建图片存放的载体，创建图片存放到bitmap
		//config :bitmap的配置，因为是从原图片截取了一部分，所以配置属性是和原图片一致的
		Bitmap createBitmap = Bitmap.createBitmap(width, height, drawingBitmap.getConfig());
		//3.画图片
		Canvas canvas = new Canvas(createBitmap);
		Paint paint = new Paint();
		Matrix matrix = new Matrix();
		//bitmap : 根据那个图片画新的图片，参考图片
		//matrix : 矩阵
		//paint : 画笔
		canvas.drawBitmap(drawingBitmap, matrix, paint);
		return createBitmap;
	}
	
	/**
	 * 获取右边图片操作
	 * @param drawingBitmap
	 * Bitmap : 图片的载体
	 */
	private Bitmap getRightBitmap(Bitmap drawingBitmap) {
		//1.确定左边图片的宽高，宽是原图片一半，高不变
		int width = (int) (drawingBitmap.getWidth()/2 + 0.5f);
		int height = drawingBitmap.getHeight();
		//2.创建图片存放的载体，创建图片存放到bitmap
		//config :bitmap的配置，因为是从原图片截取了一部分，所以配置属性是和原图片一致的
		Bitmap createBitmap = Bitmap.createBitmap(width, height, drawingBitmap.getConfig());
		//3.画图片
		Canvas canvas = new Canvas(createBitmap);
		Paint paint = new Paint();
		Matrix matrix = new Matrix();
		//因为获取的是右边的图片，所有的操作都是基于右边的图片进行的，右边的图片距离，父控件原点的距离，是减少有图片的一半
		matrix.setTranslate(-width, 0);
		//bitmap : 根据那个图片画新的图片，参考图片
		//matrix : 矩阵
		//paint : 画笔
		canvas.drawBitmap(drawingBitmap, matrix, paint);
		return createBitmap;
	}
	
	private class Myadapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.clearcache_listview_item, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_clearcache_icon = (ImageView) convertView.findViewById(R.id.iv_clearcache_icon);
				viewHolder.tv_clearcache_title = (TextView) convertView.findViewById(R.id.tv_clearcache_title);
				viewHolder.tv_clearcache_desc = (TextView) convertView.findViewById(R.id.tv_clearcache_desc);
				viewHolder.iv_clearcache_clear = (ImageView) convertView.findViewById(R.id.iv_clearcache_clear);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			//获取数据
			final AntivirusInfo antivirusInfo = list.get(position);
			viewHolder.iv_clearcache_icon.setImageDrawable(antivirusInfo.icon);
			viewHolder.tv_clearcache_title.setText(antivirusInfo.name);
			
			if (antivirusInfo.isAntivirus) {
				//是病毒
				viewHolder.tv_clearcache_desc.setText("病毒");
				viewHolder.tv_clearcache_desc.setTextColor(Color.RED);
				//显示清理按钮
				viewHolder.iv_clearcache_clear.setVisibility(View.VISIBLE);
			}else{
				//不是病毒
				viewHolder.tv_clearcache_desc.setText("安全");
				viewHolder.tv_clearcache_desc.setTextColor(Color.GREEN);
				//不显示清理按钮
				viewHolder.iv_clearcache_clear.setVisibility(View.GONE);
			}
			//卸载操作
			viewHolder.iv_clearcache_clear.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//卸载操作
					Intent intent = new Intent();
					intent.setAction("android.intent.action.DELETE");
					intent.addCategory("android.intent.category.DEFAULT");
					intent.setData(Uri.parse("package:" + antivirusInfo.packageName));
					startActivity(intent);
					//执行卸载操作，将卸载应用的信息保存到标示中
					deleteAntivirusInfo = antivirusInfo;
				}
			});
			return convertView;
		}
		
	}
	static class ViewHolder{
		ImageView iv_clearcache_icon,iv_clearcache_clear;
		TextView tv_clearcache_title,tv_clearcache_desc;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
