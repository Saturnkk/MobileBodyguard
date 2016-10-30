
package com.le.safe.ui;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.View;

import com.le.safe.utils.UiUtils;

import static com.le.safe.utils.UiUtils.dipToPx;

/**
 * 水波纹
 */
public class WaterRippleView extends View {

    //波纹颜色
    private static final int WAVE_PAINT_COLOR = 0x7fc3ea;
    //使用 函数 y = Asin(wx+b)+h
    //w影响周期，A影响振幅，h影响y位置，b为初相 生成静态水波纹
    //振幅
    private float STRETCH_FACTOR_A_PX;
    private static final float STRETCH_FACTOR_A = 4;
    
    private int OFFSET_Y_PX;
    private static final int OFFSET_Y = 0;

    // 第一条水波移动速度
    private final int TRANSLATE_X_SPEED_ONE = 4;
    //第二条水波移动速度
    private final int TRANSLATE_X_SPEED_TWO = 2;

    private float mCycleFactorW;

    private int mTotalWidth, mTotalHeight;

    //初始Y值
    private float[] mYPositions;
    private float[] mResetOneYPositions;
    private float[] mResetTwoYPositions;

    //校正后第一条移动速度
    private int mXOffsetSpeedOne;
    //校正后第二条移动速度
    private int mXOffsetSpeedTwo;

    private int mXOneOffset;
    private int mXTwoOffset;

    private Paint mWavePaint;
    private DrawFilter mDrawFilter;
    private int left;
    private int top;
    private int right;
    private int bottom;

    private static final int downY = 5;
    private int downY_PX;

    public WaterRippleView(Context context) {
        this(context, null);
    }

    public WaterRippleView(Context context, AttributeSet attrs) {
        this(context, attrs,0);


    }

    public WaterRippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     * @param context
     */
    private void init(Context context) {
        mXOffsetSpeedOne = dipToPx(context, TRANSLATE_X_SPEED_ONE);
        mXOffsetSpeedTwo = dipToPx(context, TRANSLATE_X_SPEED_TWO);
        STRETCH_FACTOR_A_PX = dipToPx(context, (int) STRETCH_FACTOR_A);
        OFFSET_Y_PX = dipToPx(context,OFFSET_Y);
        downY_PX = dipToPx(context,downY);
        // 初始绘制波纹的画笔
        mWavePaint = new Paint();
        // 去除画笔锯齿
        mWavePaint.setAntiAlias(true);
        // 设置风格为实线
        mWavePaint.setStyle(Style.FILL);
        // 设置画笔颜色
        mWavePaint.setColor(Color.rgb(127,195,234));
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 从canvas层面去除绘制时锯齿
        canvas.setDrawFilter(mDrawFilter);

        resetPositonY();
        for (int i = 0; i < mTotalWidth; i++) {
//             绘制第一条水波纹
            canvas.drawLine(i, 0 , i,
                    mTotalHeight - mResetOneYPositions[i] - downY_PX,
                    mWavePaint);

            // 绘制第二条水波纹
            canvas.drawLine(i, 0, i,
                    mTotalHeight - mResetTwoYPositions[i] - downY_PX,
                    mWavePaint);
        }

        // 改变两条波纹的移动点
        mXOneOffset += mXOffsetSpeedOne;
        mXTwoOffset += mXOffsetSpeedTwo;

        // 如果已经移动到结尾处，则重头记录
        if (mXOneOffset >= mTotalWidth) {
            mXOneOffset = 0;
        }
        if (mXTwoOffset > mTotalWidth) {
            mXTwoOffset = 0;
        }

        // 引发view重绘，一般可以考虑延迟20-30ms重绘，空出时间片
//        postInvalidate(left,top,right,bottom);
        postInvalidateDelayed(30,left,top,right,bottom);
//        invalidate();
    }

    private void resetPositonY() {
        // mXOneOffset代表当前第一条水波纹要移动的距离
        int yOneInterval = mYPositions.length - mXOneOffset;

        // 使用System.arraycopy方式重新填充第一条波纹的数据
        System.arraycopy(mYPositions, mXOneOffset, mResetOneYPositions, 0, yOneInterval);
        System.arraycopy(mYPositions, 0, mResetOneYPositions, yOneInterval, mXOneOffset);

        int yTwoInterval = mYPositions.length - mXTwoOffset;
        System.arraycopy(mYPositions, mXTwoOffset, mResetTwoYPositions, 0,
                yTwoInterval);
        System.arraycopy(mYPositions, 0, mResetTwoYPositions, yTwoInterval, mXTwoOffset);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 记录下view的宽高
        mTotalWidth = w;
        mTotalHeight = h;
        left = getLeft();
        top = getTop();
        right = getRight();
        bottom = getBottom();
        //用于保存原始波纹的y值
        mYPositions = new float[mTotalWidth];
        // 用于保存波纹一的y值
        mResetOneYPositions = new float[mTotalWidth];
        //用于保存波纹二的y值
        mResetTwoYPositions = new float[mTotalWidth];

        // 将周期定为view总宽度
        mCycleFactorW = (float) (2 * Math.PI / mTotalWidth);

        // 根据view总宽度得出所有对应的y值
        for (int i = 0; i < mTotalWidth; i++) {
            mYPositions[i] = (float) (STRETCH_FACTOR_A_PX * Math.sin(mCycleFactorW * i) + OFFSET_Y_PX);
        }
    }

}
