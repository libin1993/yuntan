package com.doit.net.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.doit.net.event.AddToLocalBlackListener;
import com.doit.net.utils.BlackBoxManger;
import com.doit.net.event.EventAdapter;
import com.doit.net.R;
import com.doit.net.utils.ToastUtils;

/**
 * Created by wiker on 2016/4/29.
 */
public class NameListEditDialog extends Dialog {

    private View mView;

    public NameListEditDialog(Context context) {
        super(context,R.style.Theme_dialog);
        initView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mView);

    }

    private void initView(){
        LayoutInflater inflater= LayoutInflater.from(getContext());
        mView = inflater.inflate(R.layout.doit_layout_add_name_list, null);

        EditText etIMSI = mView.findViewById(R.id.id_imsi);
        EditText etName = mView.findViewById(R.id.id_name);
        EditText etRemark = mView.findViewById(R.id.etRemark);
        Button btnSave = mView.findViewById(R.id.button_save);
        Button btnCancel= mView.findViewById(R.id.button_cancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imsi = etIMSI.getText().toString();
                String name = etName.getText().toString();
                String remake = etRemark.getText().toString();

                if (TextUtils.isEmpty(imsi) || imsi.length() <15){
                    ToastUtils.showMessage("请输入15位IMSI");
                    return;
                }


                EventAdapter.call(EventAdapter.ADD_BLACKBOX, BlackBoxManger.ADD_NAMELIST+name+"+"+imsi+"+"+remake);
                AddToLocalBlackListener listener = new AddToLocalBlackListener(getContext(),name,imsi,remake);
                listener.onClick(v);
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
