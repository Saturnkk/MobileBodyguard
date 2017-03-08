package com.le.safe.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.le.safe.utils.StatusBarCompat;


/**
 * activity基类
 * Created by Saturn_kk
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        //初始化布局
        setContentView(initContentView());
        //设置沉浸式状态栏
        StatusBarCompat.compat(this,Color.parseColor("#429ED6"));
        initData();
    }

    /**
     * 初始化contentView
     * @return 返回contentView的layout
     */
    protected View initContentView() {
        View view = createXMLView();
        if (view != null) {
            return view;
        } else {
            throw new IllegalStateException("the method createPager() cannot return null");
        }
    }

    /**
     * 创建每一activity的布局View
     * @return
     */
    protected abstract View createXMLView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

}
