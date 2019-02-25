package com.biller.biller.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biller.biller.R;
import com.biller.biller.activities.ServiceAddActivity;
import com.biller.biller.animation.AnimationUtils;
import com.biller.biller.beans.ServiceAddBean;
import com.biller.biller.common.CommonMethods;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.List;

/**
 * Created by Kapil Gehlot on 9/10/2017.
 */

public class ServiceAddAdapter extends RecyclerView.Adapter<ServiceAddAdapter.MyViewHolder>  {
    private List<ServiceAddBean> serviceList;
    private Context context;
    TextView serviceAdded;
    int previousPosition = 0;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    public ServiceAddAdapter(List<ServiceAddBean> serviceList, Context c, TextView serviceAdded) {
        this.serviceList = serviceList;
        this.context = c;
        this.serviceAdded = serviceAdded;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.swipe_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ServiceAddBean service = serviceList.get(position);
        holder.title.setText(service.getTitle());
        holder.des.setText(service.getDes());
        holder.cost.setText(service.getCost());
        holder.singlechar.setText(""+service.getTitle().toUpperCase().charAt(0));
        viewBinderHelper.bind(holder.swipeLayout,service.getTitle());
        if(position>previousPosition){
            AnimationUtils.animate(holder,true);
        }else{
            AnimationUtils.animate(holder,false);
        }
        previousPosition = position;

        holder.deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem(position);
            }
        });
        holder.editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ServiceAddActivity)context).showEditPopup(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public void removeItem(final int position) {
        final ServiceAddBean deletedItem = serviceList.get(position);
        serviceList.remove(position);
        notifyItemRemoved(position);
        serviceAdded.setText("" + serviceList.size());
        Snackbar snackbar = Snackbar
                .make(ServiceAddActivity.relativeLayout, deletedItem.getTitle() + " removed from list!", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               restoreItem(deletedItem,position);
               serviceAdded.setText("" + serviceList.size());
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    public void restoreItem(ServiceAddBean item, int position) {
        serviceList.add(position, item);
        notifyItemInserted(position);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, des, cost,singlechar;
        public CardView viewForeground;
        private SwipeRevealLayout swipeLayout;
        public LinearLayout deleteLayout;
        public LinearLayout editLayout;
        MyViewHolder(View view) {
            super(view);
            title =  view.findViewById(R.id.textViewTitle);
            des =  view.findViewById(R.id.textViewDes);
            cost =  view.findViewById(R.id.textViewCost);
            singlechar = view.findViewById(R.id.TextViewID);
            title.setTypeface(CommonMethods.getFont(context));
            des.setTypeface(CommonMethods.getFont(context));
            title.setTypeface(CommonMethods.getFont(context));
            swipeLayout =  itemView.findViewById(R.id.swipe_layout);
            editLayout = itemView.findViewById(R.id.editItem);
            deleteLayout = itemView.findViewById(R.id.deleteItem);
        }


    }
}
