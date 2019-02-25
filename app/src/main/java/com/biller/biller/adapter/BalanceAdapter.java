package com.biller.biller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biller.biller.R;
import com.biller.biller.activities.BillerHomeActivity;
import com.biller.biller.animation.AnimationUtils;
import com.biller.biller.beans.BalanceBean;
import com.biller.biller.common.CommonMethods;

import java.util.List;

/**
 * Created by Kapil Gehlot on 11/4/2017.
 */

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.MyViewHolder> {
    private List<BalanceBean> orderList;
    private Context context;
    int previousPosition = 0;

    public BalanceAdapter(List<BalanceBean> orderList, Context c) {
        this.orderList = orderList;
        this.context = c;
    }

    @Override
    public BalanceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_balance, parent, false);

        return new BalanceAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BalanceAdapter.MyViewHolder holder, int position) {
        BalanceBean order = orderList.get(position);
        holder.particuler.setText(order.getcName());
        holder.cost.setText(order.getCollection());
        holder.sno.setText(order.getsNo());
        holder.debit.setText(order.getDebit());

        if (position > previousPosition) {
            AnimationUtils.animate(holder, true);
        } else {
            AnimationUtils.animate(holder, false);
        }
        previousPosition = position;

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sno, particuler, cost,debit;

        MyViewHolder(View view) {
            super(view);
            sno=  view.findViewById(R.id.sno);
            particuler=  view.findViewById(R.id.particuler);
            cost = view.findViewById(R.id.credit);
            debit = view.findViewById(R.id.debit);
            sno.setTypeface(CommonMethods.getFont(context));
            particuler.setTypeface(CommonMethods.getFont(context));
            cost.setTypeface(CommonMethods.getFont(context));
            debit.setTypeface(CommonMethods.getFont(context));
            int widthPixels= BillerHomeActivity.displayMetrics.widthPixels;
            sno.getLayoutParams().width = widthPixels/4;
            particuler.getLayoutParams().width = widthPixels/4;
            cost.getLayoutParams().width = widthPixels/4;
            debit.getLayoutParams().width = widthPixels/4;
        }


    }
}
