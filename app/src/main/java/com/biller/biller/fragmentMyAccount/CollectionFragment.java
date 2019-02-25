package com.biller.biller.fragmentMyAccount;


import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.biller.biller.R;
import com.biller.biller.adapter.CollectionAdapter;
import com.biller.biller.beans.CollectionBean;
import com.biller.biller.common.CommonMethods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionFragment extends Fragment implements View.OnClickListener {

    private EditText from, to;
    private RelativeLayout relativeLayoutFrom, relativeLayoutTo;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    Calendar newCalendar;
    private DatabaseReference myRef;
    private Button buttonTodays, buttonCollectionbyDate;
    private SharedPreferences pref;
    private ProgressBar progressBar;
    SimpleDateFormat sdf;
    private List<CollectionBean> coll = new ArrayList<>();
    private CollectionAdapter collectionAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_collection, container, false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("biller");
        pref = CommonMethods.sharedPrefrences(getActivity());
        init(v);
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        newCalendar = Calendar.getInstance();
        myRef.child("vendors").child(pref.getString("key_id", "biller")).child("customers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HashMap<String, HashMap<String, String>> hashMap = (HashMap) dataSnapshot.getValue();
                    Set mapSet = hashMap.entrySet();
                    Iterator mapIterator = mapSet.iterator();
                    int i=1;
                    coll.add(new CollectionBean("SNo","Particular","Amount"));
                    while (mapIterator.hasNext()) {
                        Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                        HashMap<String, String> getData = (HashMap<String, String>) mapEntry.getValue();
                        String getPaymentStatus = getData.get("paymentStatus");
                        double cost = Double.parseDouble( getData.get("totalMoney"));
                        double roundOff = Math.round(cost * 100.0) / 100.0;
                        String getInvoice = getData.get("invoiceNo");
                        if (getPaymentStatus.equals("yes")) {
                            coll.add(new CollectionBean(""+i,getInvoice,""+roundOff));
                            i++;
                        }

                    }
                    if(coll.isEmpty()){
                        Toast.makeText(getActivity(), "No collection record!!!", Toast.LENGTH_SHORT).show();
                    }else{
                        collectionAdapter.notifyDataSetChanged();
                    }

                } else {
                    Toast.makeText(getActivity(), "No customers!!!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    public void init(View v) {
        from =  v.findViewById(R.id.editTextFrom);
        to =  v.findViewById(R.id.editTextTo);
        progressBar = v.findViewById(R.id.progressBar);
        buttonTodays =  v.findViewById(R.id.btn_todayCollection);
        buttonCollectionbyDate =  v.findViewById(R.id.buttonCollectionbyDate);
        relativeLayoutFrom =  v.findViewById(R.id.relativeLayoutFrom);
        relativeLayoutTo =v.findViewById(R.id.relativeLayoutTo);
        buttonTodays.setOnClickListener(this);
        buttonCollectionbyDate.setOnClickListener(this);
        relativeLayoutFrom.setOnClickListener(this);
        relativeLayoutTo.setOnClickListener(this);
        RecyclerView mRecyclerView = v.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        collectionAdapter= new CollectionAdapter(coll, getActivity());
        mRecyclerView.setAdapter(collectionAdapter);
    }
    @Override
    public void onClick(View view) {
        CommonMethods.vibrator(getActivity());
        if (view.getId() == R.id.relativeLayoutFrom) {
           fromDatePickerDialog = CommonMethods.datePicker(getActivity(),from);
            fromDatePickerDialog.show();
        }
        if (view.getId() == R.id.relativeLayoutTo) {
            toDatePickerDialog = CommonMethods.datePicker(getActivity(),to);
            toDatePickerDialog.show();
        }
        if (view.getId() == R.id.btn_todayCollection) {
            coll.clear();
            collectionAdapter.notifyDataSetChanged();
            if(CommonMethods.isNetworkAvailable(getActivity())) {
                progressBar.setVisibility(View.VISIBLE);
                myRef.child("vendors").child(pref.getString("key_id", "biller")).child("customers").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            coll.clear();
                            HashMap<String, HashMap<String, String>> hashMap = (HashMap) dataSnapshot.getValue();
                            Set mapSet = hashMap.entrySet();
                            Iterator mapIterator = mapSet.iterator();
                            int i=1;
                            coll.add(new CollectionBean("SNo","Particular","Amount"));
                            while (mapIterator.hasNext()) {
                                Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                                HashMap<String, String> getData = (HashMap<String, String>) mapEntry.getValue();
                                String getDate = getData.get("date_of_delivery");
                                if(getDate==null){
                                    getDate = getData.get("date_of_order");
                                }

                                String getPaymentStatus = getData.get("paymentStatus");
                                double cost = Double.parseDouble( getData.get("totalMoney"));
                                double roundOff = Math.round(cost * 100.0) / 100.0;
                                String getInvoice = getData.get("invoiceNo");
                                if (CommonMethods.getCurrentDate().equals(getDate) && getPaymentStatus.equals("yes")) {
                                    coll.add(new CollectionBean(""+i,getInvoice,""+roundOff));
                                    i++;
                                }

                            }
                            if(coll.size()==1){
                                Toast.makeText(getActivity(), "No collection today!!!", Toast.LENGTH_SHORT).show();
                            }else{
                                collectionAdapter.notifyDataSetChanged();
                            }

                        } else {
                            Toast.makeText(getActivity(), "No customers!!!", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(getActivity(), "Check your internet connection!!", Toast.LENGTH_SHORT).show();
            }
        }
        if (view.getId() == R.id.buttonCollectionbyDate) {
            coll.clear();
            collectionAdapter.notifyDataSetChanged();
            if (CommonMethods.isNetworkAvailable(getActivity())) {
                if (to.getText().length() != 0 && from.getText().length() != 0) {
                    sdf = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        boolean status = sdf.parse(from.getText().toString()).before(sdf.parse(to.getText().toString()));
                        boolean toStatus = sdf.parse(to.getText().toString()).before(sdf.parse(CommonMethods.getCurrentDate()));
                        if (status && (toStatus || to.getText().toString().equals(CommonMethods.getCurrentDate()))) {
                            progressBar.setVisibility(View.VISIBLE);
                            myRef.child("vendors").child(pref.getString("key_id", "biller")).child("customers").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        coll.clear();
                                        HashMap<String, HashMap<String, String>> hashMap = (HashMap) dataSnapshot.getValue();
                                        Set mapSet = hashMap.entrySet();
                                        Iterator mapIterator = mapSet.iterator();
                                        int i = 1;
                                        coll.add(new CollectionBean("SNo", "Particular", "Amount"));
                                        while (mapIterator.hasNext()) {
                                            Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                                            HashMap<String, String> getData = (HashMap<String, String>) mapEntry.getValue();
                                            String getDate = getData.get("date_of_delivery");
                                            if(getDate==null){
                                                getDate = getData.get("date_of_order");
                                            }
                                            try {
                                                boolean fromStatus = sdf.parse(from.getText().toString()).before(sdf.parse(getDate));
                                                boolean toStatus = sdf.parse(to.getText().toString()).after(sdf.parse(getDate));
                                                String getPaymentStatus = getData.get("paymentStatus");
                                                double cost = Double.parseDouble( getData.get("totalMoney"));
                                                double roundOff = Math.round(cost * 100.0) / 100.0;
                                                String getInvoice = getData.get("invoiceNo");
                                                if ((fromStatus || from.getText().toString().equals(getDate)) && (toStatus || to.getText().toString().equals(getDate)) && getPaymentStatus.equals("yes")) {
                                                    coll.add(new CollectionBean("" + i, getInvoice, ""+roundOff));
                                                    i++;
                                                }
                                            } catch (ParseException e) {
                                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                        if (coll.size() == 1) {
                                            Toast.makeText(getActivity(), "No collection!!!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            collectionAdapter.notifyDataSetChanged();
                                        }

                                    } else {
                                        Toast.makeText(getActivity(), "No customers!!!", Toast.LENGTH_SHORT).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "Select date combination correctly!!!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (ParseException e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "Select date!!!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getActivity(), "Check your internet connection!!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
