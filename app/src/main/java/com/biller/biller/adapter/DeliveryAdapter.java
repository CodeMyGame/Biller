package com.biller.biller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biller.biller.R;
import com.biller.biller.animation.AnimationUtils;
import com.biller.biller.beans.DeliveryBean;
import com.biller.biller.common.CommonMethods;

import java.util.List;

/**
 * Created by Kapil Gehlot on 9/23/2017.
 */

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.MyViewHolder>  {
    private List<DeliveryBean> deliveryList;
    private Context context;
    int previousPosition = 0;
    public DeliveryAdapter(List<DeliveryBean> deliveryList, Context c) {
        this.deliveryList = deliveryList;
        this.context = c;
    }

    @Override
    public DeliveryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.delivery_single_row, parent, false);

        return new DeliveryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DeliveryAdapter.MyViewHolder holder, int position) {
        DeliveryBean service = deliveryList.get(position);
        holder.title.setText(service.getTitle());
        holder.cost.setText(service.getCount());
        holder.singlechar.setText(""+service.getTitle().toUpperCase().charAt(0));
        if(position>previousPosition){
            AnimationUtils.animate(holder,true);
        }else{
            AnimationUtils.animate(holder,false);
        }
        previousPosition = position;

    }

    @Override
    public int getItemCount() {
        return deliveryList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, des, cost,singlechar;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.textViewTitle);
            cost = (TextView) view.findViewById(R.id.textViewCount);
            singlechar = (TextView) view.findViewById(R.id.TextViewID);
            title.setTypeface(CommonMethods.getFont(context));
            singlechar.setTypeface(CommonMethods.getFont(context));
        }


    }
}