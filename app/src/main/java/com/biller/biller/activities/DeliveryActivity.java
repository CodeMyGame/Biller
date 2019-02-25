package com.biller.biller.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.biller.biller.R;
import com.biller.biller.adapter.DeliveryAdapter;
import com.biller.biller.beans.DeliveryBean;
import com.biller.biller.common.CommonMethods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DeliveryActivity extends AppCompatActivity {


    private List<DeliveryBean> servicesadd = new ArrayList<>();
    private DeliveryAdapter sAdapter;
    private EditText editTextInvoice;
    private String getInvoiceNo;
    private DatabaseReference myRef;
    private ProgressBar progressBar;
    private CardView cardView;
    private TextView textViewCustomerAdd, textViewCustomerName, textViewCustomerMobile, textViewSMS, textViewDate;
    private TextView textViewTotalMoney, textViewInvoice;
    private SharedPreferences pref;
    double finalMoney;
    Button buttonConfirm;
    public static int crash=0;
    public static Activity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        fa = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        pref = CommonMethods.sharedPrefrences(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        init();
        myRef = database.getReference("biller");
        RecyclerView mRecyclerView =  findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sAdapter = new DeliveryAdapter(servicesadd, this);
        mRecyclerView.setAdapter(sAdapter);
    }


    @Override
    protected void onStop() {
        super.onStop();
        crash=1;
    }

    private void init() {
        textViewInvoice =  findViewById(R.id.invoiceText);
        editTextInvoice =  findViewById(R.id.editTextInvoiceNo);
        progressBar =  findViewById(R.id.progressBar);
        cardView =  findViewById(R.id.card_view);
        textViewCustomerName =  findViewById(R.id.textViewCustomerName);
        textViewCustomerMobile =  findViewById(R.id.textViewCustomerMobile);
        textViewCustomerAdd =  findViewById(R.id.textViewCustomerAdd);
        textViewTotalMoney =  findViewById(R.id.serviceAddedMoney);
        textViewSMS =  findViewById(R.id.textViewCustomerStatus);
        textViewDate =  findViewById(R.id.textViewCustomerDate);
        buttonConfirm = findViewById(R.id.confirm_delivery);
        textViewInvoice.setText(CommonMethods.sharedPrefrences(this).getString("key_firmname","").toUpperCase());
    }
    public boolean validation() {
        boolean valid = true;
        getInvoiceNo = editTextInvoice.getText().toString().trim();
        if (getInvoiceNo.isEmpty()) {
            editTextInvoice.setError("Enter Invoice no");
            valid = false;
        } else {
            editTextInvoice.setError(null);
        }
        if (!CommonMethods.isNetworkAvailable(DeliveryActivity.this)) {
            valid = false;
            Toast.makeText(this, "Check your internet connection!!", Toast.LENGTH_SHORT).show();
        }
        return valid;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.slide_enter, R.transition.slide_exit);
        finish();
    }

    public void sendRequest(String url) {
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    textViewSMS.setText("SMS : " + jsonObject.getString("statusDesc"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DeliveryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);
        requestQueue.add(strReq);
    }

    public void getInvoice(View view) {
        if (validation()) {
            progressBar.setVisibility(View.VISIBLE);
            new MyAsyntaskGetInvoice().execute();
        }
    }
    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = "" + mdformat.format(calendar.getTime());
        return strDate;
    }
    public void deliveredConfirmed(View view) {
        if(!CommonMethods.isNetworkAvailable(this)){
            Toast.makeText(this, "Check your network connection!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        CommonMethods.vibrator(DeliveryActivity.this);
        if (cardView.getVisibility() == View.VISIBLE) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.discount_layout);
            final TextView total = dialog.findViewById(R.id.textViewTotalAmount);
            final EditText editTextDiscount = dialog.findViewById(R.id.editTextDiscount);
            final Button buttonApply = dialog.findViewById(R.id.buttonApply);
            final String getTotal = textViewTotalMoney.getText().toString();
            double getCost = Double.parseDouble(getTotal);
            final double roundOff = Math.round(getCost * 100.0) / 100.0;
            total.setText("₹" + roundOff);
            buttonApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (buttonApply.getText().toString().toLowerCase().equals("apply")) {
                        if (!editTextDiscount.getText().toString().isEmpty()) {
                            int discountPer = Integer.parseInt(editTextDiscount.getText().toString());
                            finalMoney =roundOff - (roundOff * discountPer) / 100;
                            final double roundOffFinal = Math.round(finalMoney * 100.0) / 100.0;
                            total.setText("₹" + roundOffFinal);
                            textViewTotalMoney.setText("" + roundOffFinal);
                            buttonApply.setText("confirm");
                        }
                    } else {
                        new MyAsyntaskDelivery().execute();
                        dialog.dismiss();
                        Toast.makeText(DeliveryActivity.this, "Order confirmed successfully!!", Toast.LENGTH_SHORT).show();
//                        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(DeliveryActivity.this, SweetAlertDialog.SUCCESS_TYPE)
//                                .setTitleText("Success!!!")
//                                .setContentText("Order confirm successfully!!");
//                        sweetAlertDialog.show();
                        buttonConfirm.setEnabled(false);
                        buttonConfirm.setText("Done");
//                        Button btn =sweetAlertDialog.findViewById(R.id.confirm_button);
//                        btn.setBackgroundColor(ContextCompat.getColor(DeliveryActivity.this,R.color.colorPrimary));
                    }

                }
            });
            dialog.show();

        } else {
            Toast.makeText(this, "Please select customer!!", Toast.LENGTH_SHORT).show();
        }
    }
    class MyAsyntaskDelivery extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            myRef.child("vendors").child(pref.getString("key_id", "biller")).child("customers").child(textViewInvoice.getText()+getInvoiceNo).child("paymentStatus").setValue("yes");
            myRef.child("vendors").child(pref.getString("key_id", "biller")).child("customers").child(textViewInvoice.getText()+getInvoiceNo).child("totalMoney").setValue(""+finalMoney);
            myRef.child("vendors").child(pref.getString("key_id", "biller")).child("customers").child(textViewInvoice.getText()+getInvoiceNo).child("date_of_delivery").setValue(getCurrentDate());
            return null;
        }
    }
    class MyAsyntaskGetInvoice extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            myRef.child("vendors").child(pref.getString("key_id", "biller")).child("customers").child(textViewInvoice.getText()+getInvoiceNo).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        servicesadd.clear();
                        HashMap stringHashMap = (HashMap) dataSnapshot.getValue();
                        if(stringHashMap.get("paymentStatus").equals("yes")){
                            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(DeliveryActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Thank you!!")
                                    .setContentText("Order already delivered");
                            sweetAlertDialog.show();
                            progressBar.setVisibility(View.GONE);
                            return;
                        }
                        cardView.setVisibility(View.VISIBLE);
                        textViewCustomerName.setText("Customer name : " + stringHashMap.get("customerName"));
                        textViewCustomerMobile.setText("Customer Mobile : " + stringHashMap.get("customerMobile"));
                        textViewCustomerAdd.setText("Customer Address : " + stringHashMap.get("customerAddress"));
                        double getCost = Double.parseDouble(stringHashMap.get("totalMoney").toString());
                        final double roundOff = Math.round(getCost * 100.0) / 100.0;
                        textViewTotalMoney.setText(""+roundOff);
                        textViewDate.setText("Date of order : " + stringHashMap.get("date_of_order").toString());
                        sendRequest("http://api.planettechlabs.com/_OTP3/checkDeliveryStatus.php?sid=" + stringHashMap.get("details"));
                        HashMap services = (HashMap) stringHashMap.get("services");
                        Set mapSet = services.entrySet();
                        Iterator mapIterator = mapSet.iterator();
                        while (mapIterator.hasNext()) {
                            Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                            if(Integer.parseInt(mapEntry.getValue().toString())!=0){
                                servicesadd.add(0, new DeliveryBean(mapEntry.getKey().toString(), mapEntry.getValue().toString()));
                            }

                        }
                        sAdapter.notifyDataSetChanged();
                        buttonConfirm.setText("confirm");
                        buttonConfirm.setEnabled(true);

                    } else {
                        Toast.makeText(DeliveryActivity.this, "Please enter correct Invoice no.", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(DeliveryActivity.this,"Slow internet connection!!!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
            return null;
        }
    }
}
