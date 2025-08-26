package com.diwen.liliao.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;


import com.diwen.liliao.R;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * Created By  tian on 2020/4/2
 * Describe:   点击效果的imageview
 */
public class ReplaceSelectorImageView extends AppCompatImageView {

    private int norResId = 0;
    private int pivot = 0;
    private boolean superb = false;

    public ReplaceSelectorImageView(Context context) {
        super(context);
    }

    public ReplaceSelectorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReplaceSelectorImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.ReplaceSelectorImageView);
        norResId = typeArray.getResourceId(R.styleable.ReplaceSelectorImageView_norBackGround, 0);
        typeArray.recycle();
      /*  if (norResId != 0) {
            //改变图片颜色值 只有Src有用
            setImageResource(norResId);
        }*/
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
              BamUI.froBig_ToSmall(this,0.978f );
             changeLight(0.8f);
                break;

            // 触摸动作取消
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                 BamUI.startAnimUp(this, pivot);
                 changeLight(1f);
                  break;
        }

        return super.onTouchEvent(event);


    }

    //改变图片的亮度方法 1--原样  >1---调亮  <1---调暗  0-2
    private void changeLight(Float brightness) {
        ColorMatrix colorFilter = new ColorMatrix();
        colorFilter.setScale(brightness, brightness, brightness, 1f);
        this.setColorFilter(new ColorMatrixColorFilter(colorFilter));
    }

}