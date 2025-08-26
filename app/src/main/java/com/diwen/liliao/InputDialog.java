package com.diwen.liliao;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;


import com.diwen.liliao.base.BaseDialog;
import com.diwen.liliao.utils.AtyUtils;
import com.diwen.liliao.utils.CustomEditText;
import com.diwen.liliao.utils.OnItemClicke;

/**
 * Created By  tian on 2018/12/22
 * Describe:
 */
public class InputDialog extends BaseDialog implements View.OnClickListener {
    Context context;

    public InputDialog(Context context
    ) {
        super(context);
        this.context = context;
    }

    public OnItemClicke onitemchildClicke;


    public void setOnitemchildClicke(OnItemClicke onitemchildClicke) {
        this.onitemchildClicke = onitemchildClicke;
    }

    public void setshow(String isshow) {
        evInput.setText(isshow);
    }

    private CustomEditText evInput;

    @Override
    public Dialog initDialog(Context context) {
        View view = View.inflate(context, R.layout.dialog_ingptu, null);
        evInput = view.findViewById(R.id.evInput);


        view.findViewById(R.id.tvOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onitemchildClicke != null) {

                    onitemchildClicke.Onview(null, 0, AtyUtils.getText(evInput));
                }

                dismissDialog();

            }
        });
        view.findViewById(R.id.tvNO).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();

            }
        });
        return initMatchDialog(view, context, Gravity.TOP);
    }
 
 public void  setMaxInputValue  (int maxInputValue){
     evInput.setMaxInputValue(maxInputValue);
    }
    public void show() {

    }

    @Override
    public void onClick(View v) {

    }
}
