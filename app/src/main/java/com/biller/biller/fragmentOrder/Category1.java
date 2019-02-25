package com.biller.biller.fragmentOrder;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.biller.biller.R;
import com.biller.biller.activities.NewOrderActivity;
import com.biller.biller.adapter.Category1Adapter;
import com.biller.biller.beans.Category1Bean;
import com.biller.biller.common.CommonMethods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Category1 extends Fragment {
    private List<Category1Bean> services = new ArrayList<>();
    private Category1Adapter category1Adapter;
    private DatabaseReference myRef;
    private ProgressBar  progressBar;
    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category1, container, false);
        progressBar = v.findViewById(R.id.progressBar);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("biller");
        RecyclerView mRecyclerView = v.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        category1Adapter= new Category1Adapter(services, getActivity());
        mRecyclerView.setAdapter(category1Adapter);
        addService();
        bundle = this.getArguments();
        return v;
    }
    public void addService(){
        myRef.child("vendors").child(CommonMethods.sharedPrefrences(getActivity()).getString("key_id","biller")).child("services").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList serviceList =(ArrayList) dataSnapshot.getValue();
                    for (int i = 0; i < serviceList.size(); i++) {
                        HashMap hashMap1 = (HashMap) serviceList.get(i);
                        NewOrderActivity.integerHashMap.put(hashMap1.get("title").toString(),0);
                        if(hashMap1.get("des").equals(bundle.getString("key"))) {
                            Category1Bean stu = new Category1Bean(hashMap1.get("title").toString(),
                                    hashMap1.get("cost").toString(),hashMap1.get("des").toString());
                            services.add(0,stu);
                        }

                    }

                    category1Adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }else{
                    Toast.makeText(getActivity(), "No services added!!!", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Network problem!!", Toast.LENGTH_SHORT).show();
            }
        });
        category1Adapter.notifyDataSetChanged();
    }

}
