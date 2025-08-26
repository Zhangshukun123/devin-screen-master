package com.diwen.liliao.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.hjq.shape.view.ShapeEditText;


public class CustomEditText extends ShapeEditText {

    private int maxInputValue;

    public CustomEditText(Context context) {
        super(context);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInput();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void setMaxInputValue(int maxValue) {
        this.maxInputValue = maxValue;
    }

    private void validateInput() {
        try {
            String input = getText().toString();
            if (!input.isEmpty()) {
                int inputValue = Integer.parseInt(input);
                if (inputValue > maxInputValue) {
                    setText(String.valueOf(maxInputValue));
                    setSelection(getText().length());
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            setText(String.valueOf(maxInputValue));
            setSelection(getText().length());
        }
    }
}