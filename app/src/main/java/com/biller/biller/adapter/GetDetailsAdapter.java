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
import com.biller.biller.beans.CollectionBean;
import com.biller.biller.common.CommonMethods;

import java.util.List;

/**
 * Created by Kapil Gehlot on 1/7/2018.
 */

public class GetDetailsAdapter extends RecyclerView.Adapter<GetDetailsAdapter.MyViewHolder> {
    private List<CollectionBean> orderList;
    private Context context;
    int previousPosition = 0;

    public GetDetailsAdapter(List<CollectionBean> orderList, Context c) {
        this.orderList = orderList;
        this.context = c;
    }

    @Override
    public GetDetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_test, parent, false);

        return new GetDetailsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GetDetailsAdapter.MyViewHolder holder, int position) {
        CollectionBean order = orderList.get(position);
        holder.particuler.setText(order.getcName());
        holder.cost.setText(order.getCollection());
        holder.sno.setText(order.getsNo());

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
        TextView sno, particuler, cost;

        MyViewHolder(View view) {
            super(view);
            sno=  view.findViewById(R.id.sno);
            particuler=  view.findViewById(R.id.particuler);
            cost = view.findViewById(R.id.amount);
            sno.setTypeface(CommonMethods.getFont(context));
            particuler.setTypeface(CommonMethods.getFont(context));
            cost.setTypeface(CommonMethods.getFont(context));
            int widthPixels= BillerHomeActivity.displayMetrics.widthPixels;
            sno.getLayoutParams().width = widthPixels/4;
            particuler.getLayoutParams().width = widthPixels/3;
            cost.getLayoutParams().width = widthPixels/3;
        }


    }
}
