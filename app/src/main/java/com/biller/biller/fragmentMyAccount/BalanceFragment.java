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
import android.widget.TextView;
import android.widget.Toast;

import com.biller.biller.R;
import com.biller.biller.adapter.BalanceAdapter;
import com.biller.biller.beans.BalanceBean;
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
public class BalanceFragment extends Fragment implements View.OnClickListener {
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
    double collection = 0;
    double expenditure = 0;
    double dateCollection = 0;
    double dateExpendature = 0;
    int dateCounter = 1;
    int todayCounter = 1;
    int universalCounter = 1;
    TextView totalBalanceTextView;
    private List<BalanceBean> coll = new ArrayList<>();
    private BalanceAdapter balanceAdapter;
    int debit = 0;
    int credit = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_balance, container, false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        pref = CommonMethods.sharedPrefrences(getActivity());
        myRef = database.getReference("biller");
        init(v);
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        newCalendar = Calendar.getInstance();
        coll.add(new BalanceBean("SNo","Particular","Debit","Credit"));
        myRef.child("vendors").child(pref.getString("key_id", "biller")).child("customers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HashMap<String, HashMap<String, String>> hashMap = (HashMap) dataSnapshot.getValue();
                    Set mapSet = hashMap.entrySet();
                    Iterator mapIterator = mapSet.iterator();
                    while (mapIterator.hasNext()) {
                        Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                        HashMap<String, String> getData = (HashMap<String, String>) mapEntry.getValue();
                        String getPaymentStatus = getData.get("paymentStatus");
                        double cost = Double.parseDouble( getData.get("totalMoney"));
                        double roundOff = Math.round(cost * 100.0) / 100.0;
                        String getInvoice = getData.get("invoiceNo");
                        if (getPaymentStatus.equals("yes")) {
                            coll.add(new BalanceBean(""+universalCounter, getInvoice, "-",""+roundOff));
                            collection = collection + roundOff;
                            universalCounter++;
                        }
                    }
                        credit = 1;
                        balanceAdapter.notifyDataSetChanged();
                        if( debit == 1){
                            totalBalanceTextView.setVisibility(View.VISIBLE);
                            totalBalanceTextView.setText("₹"+(collection-expenditure));
                            credit  = 0;
                            debit = 0;
                            collection =0;
                            expenditure =0;
                        }

                } else {
                    collection = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Slow internet connection", Toast.LENGTH_SHORT).show();
            }
        });
        myRef.child("vendors").child(pref.getString("key_id", "biller")).child("expenditure").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList arrayList = (ArrayList) dataSnapshot.getValue();
                    for (int i = 0; i < arrayList.size(); i++) {
                        HashMap hashMap = (HashMap) arrayList.get(i);
                        double cost = Double.parseDouble(hashMap.get("amount").toString());
                        double roundOff = Math.round(cost * 100.0) / 100.0;
                        coll.add(new BalanceBean(""+universalCounter, hashMap.get("description").toString(),""+roundOff,"-"));
                        expenditure = expenditure + roundOff;
                        universalCounter++;
                    }

                } else {
                    expenditure = 0;
                }
                progressBar.setVisibility(View.GONE);
                debit = 1;
                balanceAdapter.notifyDataSetChanged();
                if( credit == 1){
                    totalBalanceTextView.setVisibility(View.VISIBLE);
                    totalBalanceTextView.setText("₹"+(collection-expenditure));
                    credit  = 0;
                    debit = 0;
                    collection =0;
                    expenditure =0;
                }
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
        from = v.findViewById(R.id.editTextFrom);
        to = v.findViewById(R.id.editTextTo);
        progressBar = v.findViewById(R.id.progressBar);
        buttonTodays = v.findViewById(R.id.btn_todayCollection);
        buttonCollectionbyDate = v.findViewById(R.id.buttonCollectionbyDate);
        relativeLayoutFrom = v.findViewById(R.id.relativeLayoutFrom);
        relativeLayoutTo = v.findViewById(R.id.relativeLayoutTo);
        buttonTodays.setOnClickListener(this);
        buttonCollectionbyDate.setOnClickListener(this);
        relativeLayoutFrom.setOnClickListener(this);
        relativeLayoutTo.setOnClickListener(this);
        RecyclerView mRecyclerView = v.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        balanceAdapter = new BalanceAdapter(coll, getActivity());
        mRecyclerView.setAdapter(balanceAdapter);
        totalBalanceTextView = v.findViewById(R.id.textViewTotal);
    }

    @Override
    public void onClick(View view) {
        CommonMethods.vibrator(getActivity());
        if (view.getId() == R.id.relativeLayoutFrom) {
            fromDatePickerDialog = CommonMethods.datePicker(getActivity(), from);
            fromDatePickerDialog.show();
        }
        if (view.getId() == R.id.relativeLayoutTo) {
            toDatePickerDialog = CommonMethods.datePicker(getActivity(), to);
            toDatePickerDialog.show();
        }
        if (view.getId() == R.id.btn_todayCollection) {
            todayCounter = 1;
            coll.clear();
            balanceAdapter.notifyDataSetChanged();
            if (CommonMethods.isNetworkAvailable(getActivity())) {
                progressBar.setVisibility(View.VISIBLE);
                myRef.child("vendors").child(pref.getString("key_id", "biller")).child("customers").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            coll.clear();
                            HashMap<String, HashMap<String, String>> hashMap = (HashMap) dataSnapshot.getValue();
                            Set mapSet = hashMap.entrySet();
                            Iterator mapIterator = mapSet.iterator();
                            coll.add(new BalanceBean("SNo","Particular","Debit","Credit"));
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
                                    coll.add(new BalanceBean(""+todayCounter, getInvoice, "-",""+roundOff));
                                    collection = collection +roundOff;
                                    todayCounter++;
                                }
                            }
                                credit = 1;
                                balanceAdapter.notifyDataSetChanged();
                                if( debit == 1){
                                    totalBalanceTextView.setVisibility(View.VISIBLE);
                                    totalBalanceTextView.setText("₹"+(collection-expenditure));
                                    credit  = 0;
                                    debit = 0;
                                    collection = 0;
                                    expenditure = 0;
                                }
                        } else {
                            collection = 0;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),"Slow internet connection!!", Toast.LENGTH_SHORT).show();
                    }
                });
                myRef.child("vendors").child(pref.getString("key_id", "biller")).child("expenditure").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ArrayList arrayList = (ArrayList) dataSnapshot.getValue();
                            for (int i = 0; i < arrayList.size(); i++) {
                                HashMap hashMap = (HashMap) arrayList.get(i);
                                if (CommonMethods.getCurrentDate().equals(hashMap.get("date_of_expenditure"))) {
                                    double cost = Double.parseDouble(hashMap.get("amount").toString());
                                    double roundOff = Math.round(cost * 100.0) / 100.0;
                                    coll.add(new BalanceBean(""+todayCounter, hashMap.get("description").toString(),""+roundOff,"-"));
                                    expenditure = expenditure +roundOff;
                                    todayCounter++;
                                }
                            }

                        } else {
                            expenditure = 0;
                        }
                        debit = 1;
                        if(credit ==1){
                            totalBalanceTextView.setText("₹"+(collection-expenditure));
                            debit = 0;
                            credit = 0;
                            collection = 0;
                            expenditure = 0;
                        }
                        progressBar.setVisibility(View.GONE);
                        balanceAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Slow internet connection!!!", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(getActivity(), "Check your internet connection!!", Toast.LENGTH_SHORT).show();
            }
        }

        if (view.getId() == R.id.buttonCollectionbyDate) {
            coll.clear();
            balanceAdapter.notifyDataSetChanged();
            if (CommonMethods.isNetworkAvailable(getActivity())) {
                dateCounter = 1;
                coll.add(new BalanceBean("SNo", "Particular", "Debit", "Credit"));
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
                                        HashMap<String, HashMap<String, String>> hashMap = (HashMap) dataSnapshot.getValue();
                                        Set mapSet = hashMap.entrySet();
                                        Iterator mapIterator = mapSet.iterator();
                                        while (mapIterator.hasNext()) {
                                            Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                                            HashMap<String, String> getData = (HashMap<String, String>) mapEntry.getValue();
                                            String getDate = getData.get("date_of_delivery");
                                            if(getData==null){
                                                getDate = getData.get("date_of_order");
                                            }
                                            String getColl = getData.get("totalMoney");
                                            String getInvoice = getData.get("invoiceNo");
                                            try {
                                                boolean fromStatus = sdf.parse(from.getText().toString()).before(sdf.parse(getDate));
                                                boolean toStatus = sdf.parse(to.getText().toString()).after(sdf.parse(getDate));
                                                String getPaymentStatus = getData.get("paymentStatus");
                                                if ((fromStatus || from.getText().toString().equals(getDate)) && (toStatus || to.getText().toString().equals(getDate)) && getPaymentStatus.equals("yes")) {
                                                    double cost = Double.parseDouble(getData.get("totalMoney"));
                                                    double roundOff = Math.round(cost * 100.0) / 100.0;
                                                    coll.add(new BalanceBean("" + dateCounter, getInvoice, "-", ""+roundOff));
                                                    dateCollection = dateCollection +roundOff;
                                                    dateCounter++;
                                                }
                                            } catch (ParseException e) {
                                                Toast.makeText(getActivity(), "ERROR:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                        credit = 1;
                                        if (debit == 1) {
                                            totalBalanceTextView.setVisibility(View.VISIBLE);
                                            totalBalanceTextView.setText("₹" + (dateCollection - dateExpendature));
                                            credit = 0;
                                            debit = 0;
                                            dateCollection = 0;
                                            dateExpendature = 0;
                                        }
                                        balanceAdapter.notifyDataSetChanged();
                                    } else {
                                        dateCollection = 0;
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(getActivity(), "Slow internet connection!!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                            myRef.child("vendors").child(pref.getString("key_id", "biller")).child("expenditure").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        ArrayList arrayList = (ArrayList) dataSnapshot.getValue();
                                        for (int i = 0; i < arrayList.size(); i++) {
                                            HashMap hashMap = (HashMap) arrayList.get(i);
                                            try {
                                                String getDate = hashMap.get("date_of_expenditure").toString();
                                                boolean fromStatus = sdf.parse(from.getText().toString()).before(sdf.parse(getDate));
                                                boolean toStatus = sdf.parse(to.getText().toString()).after(sdf.parse(getDate));
                                                if ((fromStatus || from.getText().toString().equals(getDate)) && (toStatus || to.getText().toString().equals(getDate))) {
                                                    double cost = Double.parseDouble( hashMap.get("amount").toString());
                                                    double roundOff = Math.round(cost * 100.0) / 100.0;
                                                    coll.add(new BalanceBean("" + dateCounter, hashMap.get("description").toString(), ""+roundOff, "-"));
                                                    dateExpendature = dateExpendature + roundOff;
                                                    dateCounter++;
                                                }

                                            } catch (ParseException e) {
                                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        debit = 1;
                                        if (credit == 1) {
                                            totalBalanceTextView.setVisibility(View.VISIBLE);
                                            totalBalanceTextView.setText("₹" + (dateCollection - dateExpendature));
                                            credit = 0;
                                            debit = 0;
                                            dateCollection = 0;
                                            dateExpendature = 0;
                                        }
                                        balanceAdapter.notifyDataSetChanged();
                                    } else {
                                        dateExpendature = 0;
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    balanceAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Slow internet connection!!!", Toast.LENGTH_SHORT).show();
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