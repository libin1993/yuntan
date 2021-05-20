package com.doit.net.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.doit.net.bean.BlackBoxBean;
import com.doit.net.utils.DateUtils;
import com.doit.net.R;

import java.util.List;

/**
 * Created by Zxc on 2018/11/28.
 */

public class BlackBoxListAdapter extends ArrayAdapter<BlackBoxBean> {
    private int resourceId;

    public BlackBoxListAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<BlackBoxBean> objects) {
        super(context, textViewResourceId, objects);
        this.resourceId = textViewResourceId;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        BlackBoxBean blackBoxBean = getItem(i);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view =  LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tvIndex = view.findViewById(R.id.tvIndex);
            viewHolder.tvBlackBox = view.findViewById(R.id.tvBlackBox);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvIndex.setText((i + 1) + ".");
        viewHolder.tvBlackBox.setText(blackBoxBean.getOperation()+ "\n" + "账户:"+blackBoxBean.getAccount()
                         +"              " + "时间:" + DateUtils.convert2String(blackBoxBean.getTime(), DateUtils.LOCAL_DATE));

        return view;
    }

    public void updateView(){
        this.notifyDataSetChanged();
    }

    class ViewHolder{
        TextView tvIndex;
        TextView tvBlackBox;
    }
}
