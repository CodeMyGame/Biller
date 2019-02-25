package com.biller.biller.fragmentMyOrder;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.biller.biller.adapter.CollectionAdapter;
import com.biller.biller.adapter.MyOrderAdapter;
import com.biller.biller.adapter.NoDataAdapter;
import com.biller.biller.beans.CollectionBean;
import com.biller.biller.beans.MyOrderBean;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingOrder extends Fragment implements View.OnClickListener, MyOrderAdapter.ClickListener {

    private List<MyOrderBean> orders = new ArrayList<>();
    private MyOrderAdapter sAdapter;
    private DatabaseReference myRef;
    ProgressBar progressBar;
    private EditText from, to;
    private RelativeLayout relativeLayoutFrom, relativeLayoutTo;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    Calendar newCalendar;
    SimpleDateFormat sdf;
    RecyclerView mRecyclerView;
    Button todayOrderButton;
    Button dateOrderButton;
    Dialog dialog;
    private List<CollectionBean> coll = new ArrayList<>();
    private CollectionAdapter collectionAdapter;
    ProgressBar progressBarDialog;
    TextView textViewAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_total_order, container, false);
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        newCalendar = Calendar.getInstance();
        init(view);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("biller");
        progressBar = view.findViewById(R.id.progressBar);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sAdapter = new MyOrderAdapter(orders, getActivity());
        mRecyclerView.setAdapter(sAdapter);
        sAdapter.setClickListener(this);
        addOrder();
        return view;
    }

    public void init(View v) {
        from = v.findViewById(R.id.editTextFrom);
        to = v.findViewById(R.id.editTextTo);
        progressBar = v.findViewById(R.id.progressBar);
        relativeLayoutFrom = v.findViewById(R.id.relativeLayoutFrom);
        relativeLayoutTo = v.findViewById(R.id.relativeLayoutTo);
        todayOrderButton = v.findViewById(R.id.btn_todayOrder);
        dateOrderButton = v.findViewById(R.id.btn_orderByDate);
        todayOrderButton.setOnClickListener(this);
        dateOrderButton.setOnClickListener(this);
        relativeLayoutFrom.setOnClickListener(this);
        relativeLayoutTo.setOnClickListener(this);
        dialog();
    }

    public void addOrder() {
        myRef.child("vendors").child(CommonMethods.sharedPrefrences(getActivity()).getString("key_id", "biller")).child("customers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    orders.clear();
                    HashMap<String, HashMap<String, String>> hashMap = (HashMap) dataSnapshot.getValue();
                    Set mapSet = (Set) hashMap.entrySet();
                    Iterator mapIterator = mapSet.iterator();
                    while (mapIterator.hasNext()) {
                        Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                        HashMap<String, String> values = (HashMap<String, String>) mapEntry.getValue();
                        if (values.get("paymentStatus").equals("no")) {
                            orders.add(0, new MyOrderBean(values.get("customerName"),
                                    values.get("invoiceNo"),
                                    values.get("totalMoney"),
                                    values.get("date_of_order"),
                                    values.get("paymentStatus"),
                                    values.get("customerMobile")));
                        }
                    }
                    sAdapter.notifyDataSetChanged();
                } else {
                    mRecyclerView.setAdapter(new NoDataAdapter(getResources().getDrawable(R.drawable.no_customers)));
                    Toast.makeText(getActivity(), "No orders!!!!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        sAdapter.notifyDataSetChanged();
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
        if (view.getId() == R.id.btn_todayOrder) {
            orders.clear();
            progressBar.setVisibility(View.VISIBLE);
            myRef.child("vendors").child(CommonMethods.sharedPrefrences(getActivity()).getString("key_id", "biller")).child("customers").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        HashMap<String, HashMap<String, String>> hashMap = (HashMap) dataSnapshot.getValue();
                        Set mapSet = hashMap.entrySet();
                        Iterator mapIterator = mapSet.iterator();
                        while (mapIterator.hasNext()) {
                            Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                            HashMap<String, String> values = (HashMap<String, String>) mapEntry.getValue();
                            String getDate = values.get("date_of_order");
                            String status = values.get("paymentStatus");
                            if (CommonMethods.getCurrentDate().equals(getDate) && status.equals("no")) {
                                orders.add(0, new MyOrderBean(values.get("customerName"),
                                        values.get("invoiceNo"),
                                        values.get("totalMoney"),
                                        getDate,
                                        status,
                                        values.get("customerMobile")));
                            }

                        }
                        Collections.sort(orders, new Comparator<MyOrderBean>() {
                            public int compare(MyOrderBean m1, MyOrderBean m2) {
                                return m2.invoiceNo.compareTo(m1.invoiceNo);
                            }
                        });
                        if (orders.size() == 0) {
                            Toast.makeText(getActivity(), "No order's today!!!", Toast.LENGTH_SHORT).show();

                        }else {
                            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success!!!")
                                    .setContentText(orders.size()+" orders today");
                            sweetAlertDialog.show();
                            Button btn =sweetAlertDialog.findViewById(R.id.confirm_button);
                            btn.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
                        }
                        sAdapter.notifyDataSetChanged();

                    } else {
                        mRecyclerView.setAdapter(new NoDataAdapter(getResources().getDrawable(R.drawable.no_customers)));
                        Toast.makeText(getActivity(), "No orders!!!!", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            sAdapter.notifyDataSetChanged();
        }
        if (view.getId() == R.id.btn_orderByDate) {
            orders.clear();
            if (to.getText().length() != 0 && from.getText().length() != 0) {
                sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    boolean status = sdf.parse(from.getText().toString()).before(sdf.parse(to.getText().toString()));
                    boolean toStatus = sdf.parse(to.getText().toString()).before(sdf.parse(CommonMethods.getCurrentDate()));
                    if (status && (toStatus || to.getText().toString().equals(CommonMethods.getCurrentDate()))) {
                        progressBar.setVisibility(View.VISIBLE);
                        myRef.child("vendors").child(CommonMethods.sharedPrefrences(getActivity()).getString("key_id", "biller")).child("customers").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    HashMap<String, HashMap<String, String>> hashMap = (HashMap) dataSnapshot.getValue();
                                    Set mapSet = hashMap.entrySet();
                                    Iterator mapIterator = mapSet.iterator();
                                    while (mapIterator.hasNext()) {
                                        Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                                        HashMap<String, String> values = (HashMap<String, String>) mapEntry.getValue();
                                        String getDate = values.get("date_of_order");
                                        String status = values.get("paymentStatus");
                                        try {
                                            boolean toStatus = sdf.parse(to.getText().toString()).after(sdf.parse(getDate));
                                            boolean fromStatus = sdf.parse(from.getText().toString()).before(sdf.parse(getDate));
                                            if ((fromStatus || from.getText().toString().equals(getDate)) && (toStatus || to.getText().toString().equals(getDate)) && status.equals("no")) {
                                                orders.add(0, new MyOrderBean(values.get("customerName"),
                                                        values.get("invoiceNo"),
                                                        values.get("totalMoney"),
                                                        getDate,
                                                        values.get("paymentStatus"),
                                                        values.get("customerMobile")));
                                            }

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    if (orders.size() == 0) {
                                        Toast.makeText(getActivity(), "No order's between these date!!!", Toast.LENGTH_SHORT).show();

                                    }else{
                                        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("Success!!!")
                                                .setContentText(orders.size()+" orders between these date!!!");
                                        sweetAlertDialog.show();
                                        Button btn =sweetAlertDialog.findViewById(R.id.confirm_button);
                                        btn.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
                                    }
                                    sAdapter.notifyDataSetChanged();

                                } else {
                                    mRecyclerView.setAdapter(new NoDataAdapter(getResources().getDrawable(R.drawable.no_customers)));
                                    Toast.makeText(getActivity(), "No orders!!!!", Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        sAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getActivity(), "Select date combination correctly!!!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Select date!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void dialog() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.get_details_popup);
        RecyclerView mRecyclerView = dialog.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        collectionAdapter = new CollectionAdapter(coll, getActivity());
        mRecyclerView.setAdapter(collectionAdapter);
        progressBarDialog = dialog.findViewById(R.id.progressBar);
        textViewAddress = dialog.findViewById(R.id.textViewFirmAddress);
    }

    @Override
    public void itemClicked(View v, int position) {
        dialog.show();
        coll.clear();
        MyOrderBean myOrderBean = orders.get(position);
        String invoice = myOrderBean.getInvoiceNo();
        myRef.child("vendors").child(CommonMethods.sharedPrefrences(getActivity()).getString("key_id", "")).child("customers")
                .child(invoice).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HashMap hashMap = (HashMap)dataSnapshot.getValue();
                    HashMap<String, Long> stringStringHashMap = (HashMap<String,Long>)hashMap.get("services");
                    int i = 1;
                    for (Map.Entry<String, Long> entry : stringStringHashMap.entrySet()) {
                        String key = entry.getKey();
                        long value = entry.getValue();
                        if(value!=0){
                            coll.add(new CollectionBean(""+i,key,""+value));
                            i++;
                        }
                    }
                    textViewAddress.setText(hashMap.get("customerAddress").toString());
                    collectionAdapter.notifyDataSetChanged();
                    progressBarDialog.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Slow internet connection!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

