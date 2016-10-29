package com.le.safe.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dell.myapplication.R;


public class SettingView extends RelativeLayout {

	private TextView mSettingViewTitle;
	private ImageView mSettingViewToggle;
	private View view;
	/**
	 * 保存按钮的状态
	 */
	private boolean isToggle;

	public SettingView(Context context) {
		//super(context);
		// TODO Auto-generated constructor stub
		this(context,null);
	}

	public SettingView(Context context, AttributeSet attrs) {
		//super(context, attrs);
		// TODO Auto-generated constructor stub
		this(context,attrs,0);
	}
	

	public SettingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
		//AttributeSet保存有控件的所有属性的值，通过AttributeSet获取自定义属性的值
		//获取在attrs.xml中设置的相应的控件的属性的值的集合
		TypedArray mTa = context.obtainStyledAttributes(attrs, R.styleable.SettingView);
		//从集合中获取特定属性的值
		String mTitle = mTa.getString(R.styleable.SettingView_title);
		//获取自定义背景属性
		int mBackground = mTa.getInt(R.styleable.SettingView_settingviewBackground, 0);
		//获取控制按钮显示隐藏的属性的值
		boolean mIsToggle = mTa.getBoolean(R.styleable.SettingView_istoggle, true);
		//将获取的属性的值设置给相应的控件进行显示了
		mSettingViewTitle.setText(mTitle);
		//根据背景属性的值，设置整个条目的背景
		switch (mBackground) {
		case 0:
			view.setBackgroundResource(R.drawable.selector_settingview_first);
			break;
		case 1:
			view.setBackgroundResource(R.drawable.selector_settingview_middle);
			break;
		case 2:
			view.setBackgroundResource(R.drawable.selector_settingview_last);
			break;
		default:
			view.setBackgroundResource(R.drawable.selector_settingview_first);
			break;
		}
		//根据属性的值隐藏显示按钮
		mSettingViewToggle.setVisibility(mIsToggle ? View.VISIBLE : View.GONE);
		//释放资源
		mTa.recycle();
	}

	/**
	 * 将设置中心的条目添加到自定义控件
	 */
	private void initView() {
		//第一种方式
		//得到布局文件转化的view对象
		//getContext() : 一般就是在自定义控件使用
		/*View view = View.inflate(getContext(), R.layout.settingview, null);//先有爹，再有孩子，亲生的
		//将对象添加到自定义控件中
		this.addView(view);*/
		//第二种方式
		//将布局文件转化成view对象，同时给view对象设置一个父控件，this表示的就是要给view对象设置的父控件
		view = View.inflate(getContext(), R.layout.settingview, this);
		//初始化控件
		mSettingViewTitle = (TextView) view.findViewById(R.id.tv_settingview_title);
		mSettingViewToggle = (ImageView) view.findViewById(R.id.iv_settingview_toggle);
	}
	/**
	 * 设置按钮的样式
	 * @param isToggle	true:开    false:关
	 */
	public void setToggle(boolean isToggle){
		this.isToggle = isToggle;
		if (isToggle) {
			mSettingViewToggle.setImageResource(R.drawable.on);
		}else{
			mSettingViewToggle.setImageResource(R.drawable.off);
		}
	}
	/**
	 * 获取按钮的状态
	 * @return
	 */
	public boolean getToggle(){
		return isToggle;
	}
	/**
	 * 设置按钮的样式
	 */
	public void toggle(){
		setToggle(!isToggle);
		
		
		/*if (isToggle) {
			mSettingViewToggle.setImageResource(R.drawable.on);
		}else{
			mSettingViewToggle.setImageResource(R.drawable.off);
		}*/
	}
	
	
	
	
	
	
	
	
	
	
	

}
