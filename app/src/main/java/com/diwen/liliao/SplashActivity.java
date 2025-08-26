package com.diwen.liliao;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.view.WindowManager;

import com.diwen.liliao.activity.MainActivity;
import com.diwen.liliao.base.MqttBaseActivity;
import com.diwen.liliao.databinding.ActivitySplashBinding;
import com.diwen.liliao.utils.ActivityUtils;

/**
 * Created By  tian on 2024/8/21
 * Describe:
 */
public class SplashActivity extends MqttBaseActivity<ActivitySplashBinding> {
    @Override
    protected void handleIntent(Intent intent) {

    }

    @Override
    protected void setUiText() {

    }

    @Override
    protected void config() {
        binding.lottieView.setAnimation("haha.json");
        binding.lottieView.playAnimation();
        binding.lottieView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ActivityUtils.startActivity(MainActivity.class);
                ActivityUtils.finishActivity(SplashActivity.class);
            }
        });
    
    }
    private void full(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
    @Override
    protected void setListener() {

    }
} 
