package com.diwen.liliao.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseFragment<T extends ViewBinding> extends Fragment {

    protected T binding;
    protected Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        try {
            // 通过泛型获取对应的 ViewBinding class
            Type superclass = getClass().getGenericSuperclass();
            Class<T> aClass = (Class<T>) ((ParameterizedType) superclass).getActualTypeArguments()[0];

            // 反射调用 ViewBinding.inflate(inflater, container, false)
            Method method = aClass.getMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            binding = (T) method.invoke(null, inflater, container, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 防止内存泄漏
    }

    /**
     * 初始化界面
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();
}