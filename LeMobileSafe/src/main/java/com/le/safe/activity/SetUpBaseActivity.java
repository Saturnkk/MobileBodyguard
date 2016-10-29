package com.le.safe.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.dell.myapplication.R;

public abstract class SetUpBaseActivity extends AppCompatActivity {
	
	
	
	private GestureDetector gestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//手势识别器
		//要想手势识别器生效，必须将手势识别器注册到界面的触摸事件中
		gestureDetector = new GestureDetector(this, new MyOnGestureListener());
		super.onCreate(savedInstanceState);

	}
	//界面的触摸事件
	//MotionEvent : 界面触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
	private class MyOnGestureListener extends SimpleOnGestureListener{
		//滑动的监听事件，左右，滚动：上下
		//e1 : 按下的事件，保存有按下的坐标
		//e2 : 抬起的事件，保存有抬起的坐标
		//velocity : 速度   滑动的速率
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			//1.获取按下和抬起事件的坐标,强转成int类型，方便计算
			int downX = (int) e1.getRawX();
			int upX = (int) e2.getRawX();
			
			//获取按下和抬起的y的坐标
			int downY = (int) e1.getRawY();
			int upY = (int) e2.getRawY();
			//屏蔽侧滑
			if ((Math.abs(downY-upY)) > 50) {
				//屏蔽侧滑
				Toast.makeText(getApplicationContext(), "你小子又乱滑了....", Toast.LENGTH_SHORT).show();
				return true;
			}
			
			//上一步
			if ((upX-downX)>100) {
				dopre();
			}
			//下一步操作
			if ((downX - upX) > 100) {
				donext();
			}
			//true if the event is consumed, else false
			//返回true:事件执行，返回false:事件拦截不执行
			return false;
		}
		
	}
	
	
	
	

	// 1.抽取上一步和下一步的按钮的点击事件到父类
	// 但是，父类不知道子类应该跳转到那个activity
	// 父类创建两个抽象方法，子类根据自己的特性去实现抽象方法，来实现自己的需求
	/**
	 * 下一步按钮的点击事件
	 * 
	 * @param v
	 */
	public void next(View v) {
		donext();
	}

	/**
	 * 上一步 按钮点击事件
	 * 
	 * @param v
	 */
	public void pre(View v) {
		dopre();
	}

	// 2.因为父类不知道类具体的跳转执行操作，所以创建抽象方法，具体的操作，还是有子类完成
	/**
	 * 下一步的具体操作方法
	 * true:屏蔽跳转操作，false:执行跳转操作
	 */
	public abstract boolean next_activity();

	/**
	 * 上一步的具体操作
	 * true:屏蔽跳转操作，false:执行跳转操作
	 */
	public abstract boolean pre_activity();

	
	
	/**
	 * 下一步操作
	 */
	private void donext() {
		if (next_activity()) {
			return;
		}
		finish();
		// 执行界面切换动画
		// enterAnim : 新的界面进入的动画
		// exitAnim : 旧的界面退出的动画
		overridePendingTransition(R.anim.setup_next_enter,
				R.anim.setup_next_exit);
	}

	/**
	 * 上一步操作
	 */
	private void dopre() {
		//如果返回true，表示是屏蔽跳转操作，不会执行移出界面和界面切换动画操作
		if (pre_activity()) {
			return;
		}
		finish();
		overridePendingTransition(R.anim.setup_pre_enter, R.anim.setup_pre_exit);
	}

	/**
	 * 是对物理按钮的点击事件的监听 keyCode ： 执行事件的按钮的标示 KeyEvent ： 事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dopre();
		}
		return super.onKeyDown(keyCode, event);
	}

}
