package com.biller.biller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.biller.biller.R;
import com.biller.biller.activities.NewOrderActivity;
import com.biller.biller.animation.AnimationUtils;
import com.biller.biller.beans.Category1Bean;
import com.biller.biller.beans.ServiceAddBean;
import com.biller.biller.common.CommonMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kapil Gehlot on 9/17/2017.
 */

public class Category1Adapter extends RecyclerView.Adapter<Category1Adapter.MyViewHolder>{
    private List<Category1Bean> serviceList;
    private Context context;
    HashMap<String,Integer> saveCountState = new HashMap<>();
    int previousPosition = 0;
    public Category1Adapter(List<Category1Bean> serviceList, Context c) {
        this.serviceList = serviceList;
        saveCountState.clear();
        this.context = c;
    }

    @Override
    public Category1Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.neworder_single_row, parent, false);

        return new Category1Adapter.MyViewHolder(itemView);
    }

    public  List<ServiceAddBean>removeDuplicates(String t, String d){
        List<ServiceAddBean> kartServices = NewOrderActivity.kartServices;
        List<ServiceAddBean> newKartServices = new ArrayList<>();
        for(int i=0;i<kartServices.size();i++){
            ServiceAddBean serviceAddBean = kartServices.get(i);
            String title = serviceAddBean.getTitle();
            String des = serviceAddBean.getDes();
            if(!(title.equals(t) && des.equals(d))){
                newKartServices.add(serviceAddBean);
            }
        }
        return newKartServices;
    }

    @Override
    public void onBindViewHolder(final Category1Adapter.MyViewHolder holder, final int position) {
        final Category1Bean services = serviceList.get(position);
        holder.title.setText(services.getTitle());
        holder.id.setText(""+services.getTitle().toUpperCase().charAt(0));
        holder.cost.setText(services.getCost());
        holder.des.setText(services.getCategory());
        if(saveCountState.containsKey(services.getCategory()+services.getTitle())){
            holder.count.setText(""+saveCountState.get(services.getCategory()+services.getTitle()));
        }else{
            holder.count.setText("0");
        }
        holder.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int c = Integer.parseInt(holder.count.getText().toString());
                c++;
                if (c >= 0) {
                    saveCountState.put(holder.des.getText().toString()+holder.title.getText().toString(),c);
                    holder.count.setText("" +c);
                    NewOrderActivity.kartServices = removeDuplicates(holder.title.getText().toString(), holder.des.getText().toString());
                    NewOrderActivity.kartServices.add(new ServiceAddBean(holder.title.getText().toString(), holder.des.getText().toString(), "" + c));
                    int totalService = ++NewOrderActivity.totalServices;
                    int totalCost;
                    if (totalService >= 0)
                        NewOrderActivity.integerHashMap.put(services.getTitle() + "(" + services.getCategory() + ")", Integer.parseInt("" + holder.count.getText()));
                    totalCost = Integer.parseInt(services.getCost()) + NewOrderActivity.totalCost;
                    NewOrderActivity.totalCost = totalCost;
                    NewOrderActivity.serviceAdded.setText(totalService + " services added");
                    NewOrderActivity.serviceTotalCost.setText("\u20B9" + totalCost);
                }
            }
        });
        holder.buttonSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int c = Integer.parseInt(holder.count.getText().toString());
                c--;
                if(c>=0) {
                    saveCountState.put(holder.des.getText().toString()+holder.title.getText().toString(),c);
                    holder.count.setText("" +c);
                    NewOrderActivity.kartServices = removeDuplicates(holder.title.getText().toString(),holder.des.getText().toString());
                    if(c!=0){
                        NewOrderActivity.kartServices.add(new ServiceAddBean(holder.title.getText().toString(),holder.des.getText().toString(),""+c));
                    }

                    int totalService = --NewOrderActivity.totalServices;
                    int totalCost;
                    if(totalService>=0)
                        NewOrderActivity.integerHashMap.put(services.getTitle()+"("+services.getCategory()+")",Integer.parseInt(""+holder.count.getText()));
                        totalCost = NewOrderActivity.totalCost-Integer.parseInt(services.getCost());
                        NewOrderActivity.totalCost = totalCost;
                        NewOrderActivity.serviceAdded.setText(totalService+" services added");
                        NewOrderActivity.serviceTotalCost.setText("\u20B9"+totalCost);
                }
            }
        });
        if(position>previousPosition){
            AnimationUtils.animate(holder,true);
        }else{
            AnimationUtils.animate(holder,false);
        }
        previousPosition = position;
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView id;
        TextView count;
        TextView cost;
        TextView des;
        Button buttonAdd,buttonSub;
        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.textViewTitle);
            id = view.findViewById(R.id.TextViewID);
            cost = view.findViewById(R.id.textViewCost);
            buttonAdd = view.findViewById(R.id.buttonAdd);
            buttonSub = view.findViewById(R.id.buttonSub);
            des = view.findViewById(R.id.description);
            count = view.findViewById(R.id.item_count);
            title.setTypeface(CommonMethods.getFont(context));
            id.setTypeface(CommonMethods.getFont(context));
            count.setTypeface(CommonMethods.getFont(context));
        }


    }
}
