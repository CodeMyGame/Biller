package com.biller.biller.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.biller.biller.R;
import com.biller.biller.animation.AnimationUtils;
import com.biller.biller.beans.HomeBean;
import com.biller.biller.common.CommonMethods;

import java.util.List;

/**
 * Created by Kapil Gehlot on 9/16/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>  {
    private List<HomeBean> homeList;
    private Context context;
    int previousPosition = 0;
    private ClickListener clickListener;
    public HomeAdapter(List<HomeBean> homeList, Context c) {
        this.homeList = homeList;
        this.context = c;
    }
    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
    @Override
    public HomeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_home_row, parent, false);

        return new HomeAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HomeAdapter.MyViewHolder holder, int position) {
        HomeBean home = homeList.get(position);
        holder.title.setText(home.getTitle());
        holder.imageView.setImageResource(home.getId());
        if(position>previousPosition){
            AnimationUtils.animate(holder,true);
        }else{
            AnimationUtils.animate(holder,false);
        }
        previousPosition = position;

    }

    @Override
    public int getItemCount() {
        return homeList.size();
    }

    public interface ClickListener {
        void itemClicked(View v, int position);
    }
    class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        TextView title;
        ImageView imageView;
        CardView card_view;
        @TargetApi(Build.VERSION_CODES.O)
        MyViewHolder(View view) {
            super(view);
            title =  view.findViewById(R.id.heading);
            title.setTypeface(CommonMethods.getFont(context));
            imageView =  view.findViewById(R.id.head_img);
//            card_view = view.findViewById(R.id.card_view);
//            card_view.getLayoutParams().height = BillerHomeActivity.displayMetrics.heightPixels/3;
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.itemClicked(view, getPosition());
            }
        }
    }
}
