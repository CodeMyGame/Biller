package com.biller.biller.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.biller.biller.R;

/**
 * Created by Kapil Gehlot on 10/11/2017.
 */

public class NoDataAdapter extends RecyclerView.Adapter<NoDataAdapter.ViewHolder> {

    Drawable drawable;
    public NoDataAdapter(Drawable drawable) {
        this.drawable = drawable;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nodata_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.imageView.setImageDrawable(drawable);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 1;//must return one otherwise none item is shown
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        ImageView imageView;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            imageView = view.findViewById(R.id.imageView);
        }
    }
}
