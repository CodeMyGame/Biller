package com.biller.biller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.biller.biller.R;
import com.biller.biller.animation.AnimationUtils;
import com.biller.biller.beans.MyOrderBean;
import com.biller.biller.common.CommonMethods;

import java.util.List;

/**
 * Created by Kapil Gehlot on 9/29/2017.
 */

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyViewHolder> {
    private List<MyOrderBean> orderList;
    private Context context;
    int previousPosition = 0;
    private ClickListener clickListener;
    public MyOrderAdapter(List<MyOrderBean> orderList, Context c) {
        this.orderList = orderList;
        this.context = c;
    }
    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
    @Override
    public MyOrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myorder_single_row, parent, false);

        return new MyOrderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyOrderAdapter.MyViewHolder holder, int position) {
        MyOrderBean order = orderList.get(position);
        holder.customerName.setText(order.getCustomerName());
        holder.orderNo.setText(order.getOrderNo());
        double cost = Double.parseDouble(order.getTotalCost());
        double roundOff = Math.round(cost * 100.0) / 100.0;
        holder.cost.setText(""+roundOff);
        holder.singlechar.setText("" + order.getCustomerName().toUpperCase().charAt(0));
        holder.invoiceNo.setText(order.getInvoiceNo());
        holder.mobile.setText(order.getMobile());
        if(order.getStatus().equals("yes")){
            holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check));
        }else{
            holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cancel));
        }
        if (position > previousPosition) {
            AnimationUtils.animate(holder, true);
        } else {
            AnimationUtils.animate(holder, false);
        }
        previousPosition = position;

    }
//    public void filterList(List<MyOrderBean> filterdNames) {
//        this.orderList = filterdNames;
//        notifyDataSetChanged();
//    }
    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public interface ClickListener {
        void itemClicked(View v, int position);
    }
    class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnLongClickListener{
        TextView customerName, orderNo, cost, singlechar, invoiceNo,mobile;
        ImageView imageView;

        MyViewHolder(View view) {
            super(view);
            customerName =  view.findViewById(R.id.textViewCustomerName);
            orderNo =  view.findViewById(R.id.textViewOrderNo);
            cost = view.findViewById(R.id.textViewCost);
            singlechar =  view.findViewById(R.id.TextViewID);
            invoiceNo = view.findViewById(R.id.textViewInvoiceno);
            imageView = view.findViewById(R.id.status);
            mobile = view.findViewById(R.id.textViewCustomerMobile);
            customerName.setTypeface(CommonMethods.getFont(context));
            orderNo.setTypeface(CommonMethods.getFont(context));
            singlechar.setTypeface(CommonMethods.getFont(context));
            invoiceNo.setTypeface(CommonMethods.getFont(context));
            mobile.setTypeface(CommonMethods.getFont(context));
            view.setOnLongClickListener(this);
        }


        @Override
        public boolean onLongClick(View view) {
            if (clickListener != null) {
                clickListener.itemClicked(view, getPosition());
            }
            return true;
        }

    }
}