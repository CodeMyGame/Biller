package com.biller.biller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biller.biller.R;
import com.biller.biller.beans.ServiceAddBean;
import com.biller.biller.common.CommonMethods;

import java.util.List;

/**
 * Created by Kapil Gehlot on 9/10/2017.
 */

public class KartAdapter extends RecyclerView.Adapter<KartAdapter.MyViewHolder>  {
    private List<ServiceAddBean> serviceList;
    private Context context;
    int previousPosition = 0;
    public KartAdapter(List<ServiceAddBean> serviceList, Context c) {
        this.serviceList = serviceList;
        this.context = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kart_single_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ServiceAddBean service = serviceList.get(position);
        holder.title.setText(service.getTitle());
        holder.des.setText(service.getDes());
        holder.cost.setText(service.getCost());
        holder.singlechar.setText(""+service.getTitle().toUpperCase().charAt(0));
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, des, cost,singlechar;

        MyViewHolder(View view) {
            super(view);
            title =  view.findViewById(R.id.textViewTitle);
            des =  view.findViewById(R.id.textViewDes);
            cost =  view.findViewById(R.id.textViewCost);
            singlechar = view.findViewById(R.id.TextViewID);
            title.setTypeface(CommonMethods.getFont(context));
            des.setTypeface(CommonMethods.getFont(context));
            title.setTypeface(CommonMethods.getFont(context));
        }


    }
}
