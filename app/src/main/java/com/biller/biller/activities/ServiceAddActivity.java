package com.biller.biller.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biller.biller.R;
import com.biller.biller.adapter.ServiceAddAdapter;
import com.biller.biller.beans.NewServiceBean;
import com.biller.biller.beans.RegisterBean;
import com.biller.biller.beans.ServiceAddBean;
import com.biller.biller.common.CommonMethods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceAddActivity extends Activity {

    private List<ServiceAddBean> services = new ArrayList<>();
    private ServiceAddAdapter sAdapter;
    private EditText service, cost;
    private AutoCompleteTextView des;
    private String getService, getDes, getCost;
    private ProgressBar progressBar;
    public static  RelativeLayout relativeLayout;
    private TextView serviceAdded;
    private DatabaseReference myRef;
    private String getFirm, getAddress, getContact, getBusiness, getFullName, getPin, getCity, getEmail, getPassword;
    private String getSerEdit,getDesEdit,getCosEdit;
    private Button buttonConfirm;
    ArrayList<String> category = new ArrayList<>();
    ArrayAdapter<String> adapter;
    AutoCompleteTextView getDescription;
    AlertDialog alertDialog;
    EditText serviceNameEditText,costEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_add);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("biller");
        relativeLayout = findViewById(R.id.rl);
        progressBar = findViewById(R.id.progressBar);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        serviceAdded =  findViewById(R.id.serviceAdded);
        service =  findViewById(R.id.editTextService);
        des =  findViewById(R.id.editTextServiceDes);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, category);
        des.setAdapter(adapter);
        cost =  findViewById(R.id.editTextServiceCost);
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sAdapter = new ServiceAddAdapter(services, this,serviceAdded);
        mRecyclerView.setAdapter(sAdapter);
        myRef.child("vendors").child("category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    category.clear();
                    HashMap<String,HashMap<String, String>> list = (HashMap<String,HashMap<String, String>>) dataSnapshot.getValue();
                    for ( Map.Entry<String, HashMap<String, String>> entry :list.entrySet()) {
                        String key = entry.getKey();
                        category.add(key);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonMethods.vibrator(ServiceAddActivity.this);
                if (services.size() > 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    buttonConfirm.setText("Wait...");
                    buttonConfirm.setEnabled(false);
                    getAddress = getIntent().getStringExtra("firmAddress");
                    getBusiness = getIntent().getStringExtra("bussiness");
                    getFullName = getIntent().getStringExtra("fullname");
                    getFirm = getIntent().getStringExtra("firmName");
                    getCity = getIntent().getStringExtra("city");
                    getPin = getIntent().getStringExtra("pin");
                    getContact = getIntent().getStringExtra("contact");
                    getEmail = getIntent().getStringExtra("email");
                    getPassword = getIntent().getStringExtra("password");
                    String subscription_month = getIntent().getStringExtra("subscription");
                    RegisterBean registerBean = new RegisterBean(getEmail, getPassword, getFullName, getContact, getAddress,
                            getFirm, getCity, getPin, getBusiness, getCurrentDate(),
                            CommonMethods.addMonthsToCurrentDate(Integer.parseInt(subscription_month)), subscription_month, services, "A", "0000");
                    myRef.child("vendors").child(getContact).setValue(registerBean, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(ServiceAddActivity.this, "Error!!!"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                buttonConfirm.setText("Confirm");
                                buttonConfirm.setEnabled(true);
                            } else {
                                Toast.makeText(ServiceAddActivity.this, "Register successfully!!!", Toast.LENGTH_SHORT).show();
                                RegisterActivity.reActivity.finish();
                                SubscriptionActivity.suActivity.finish();
                                startActivity(new Intent(ServiceAddActivity.this, RegisterSuccess.class));
                                overridePendingTransition(R.transition.slide_enter, R.transition.slide_exit);
                                finish();
                            }
                        }
                    });
                }else{
                    Toast.makeText(ServiceAddActivity.this, "Please add atleast one service!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean validation() {
        boolean valid = true;
        getService = service.getText().toString().trim();
        getDes = des.getText().toString().toLowerCase().trim();
        getCost = cost.getText().toString().trim();
        String newDes = getDes.replace(" ","");
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
        Matcher matcher = pattern.matcher(newDes);
        if (getService.isEmpty()) {
            service.setError("Enter Service name");
            valid = false;
        } else {
            service.setError(null);
        }
        if (!matcher.matches() || getDes.isEmpty()) {
            valid = false;
            des.setError("Enter correct category name");
        } else {
            des.setError(null);
        }
        if (getCost.isEmpty()) {
            cost.setError("Enter Service cost");
            valid = false;
        } else {
            cost.setError(null);
        }
        if(!CommonMethods.isNetworkAvailable(ServiceAddActivity.this)){
            valid = false;
            Toast.makeText(this, "Check your network connection!!!", Toast.LENGTH_SHORT).show();
        }
        return valid;
    }

    public void addService(View view) {
        CommonMethods.vibrator(ServiceAddActivity.this);
        if (validation()) {
            services.add(0, new ServiceAddBean(getService, getDes, getCost));
            DatabaseReference databaseReference = myRef.child("vendors").child("category").child(getDes);
            databaseReference.setValue(new NewServiceBean(getDes));
            sAdapter.notifyDataSetChanged();
            sAdapter.notifyItemChanged(0);
            serviceAdded.setText("" + services.size());
            cost.setText("");
            des.setText("");
            service.setText("");
        }
    }


    @Override
    public void onBackPressed() {
            super.onBackPressed();
            overridePendingTransition(R.transition.slide_enter, R.transition.slide_exit);

    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = "" + mdformat.format(calendar.getTime());
        return strDate;
    }

    public boolean validationEdit(View v) {
        boolean valid = true;
        getSerEdit = serviceNameEditText.getText().toString().trim();
        getDesEdit = getDescription.getText().toString().toLowerCase().trim();
        getCosEdit = costEditText.getText().toString().trim();
        String newDes = getDesEdit.replace(" ","");
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
        Matcher matcher = pattern.matcher(newDes);
        if (getSerEdit.isEmpty()) {
            serviceNameEditText.setError("Enter Service name");
            valid = false;
        } else {
            serviceNameEditText.setError(null);
        }
        if (!matcher.matches() || getDesEdit.isEmpty()) {
            valid = false;
            getDescription.setError("Enter correct category name");
        } else {
            getDescription.setError(null);
        }
        if (getCosEdit.isEmpty()) {
            costEditText.setError("Enter Service cost");
            valid = false;
        } else {
            costEditText.setError(null);
        }
        return valid;
    }
    public void showEditPopup(final int position){
        if (CommonMethods.isNetworkAvailable(ServiceAddActivity.this)) {
            CommonMethods.vibrator(ServiceAddActivity.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View v = inflater.inflate(R.layout.edit_service_popup, null);
            ServiceAddBean serviceAddBean = services.get(position);
            serviceNameEditText = v.findViewById(R.id.editTextService);
            getDescription = v.findViewById(R.id.editTextServiceDes);
            costEditText = v.findViewById(R.id.editTextServiceCost);
            serviceNameEditText.setText(serviceAddBean.getTitle());
            getDescription.setText(serviceAddBean.getDes());
            costEditText.setText(serviceAddBean.getCost());
            getDescription.setAdapter(adapter);
            Button edit =  v.findViewById(R.id.buttonedit);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validationEdit(v)) {
                        int length = services.size();
                        int seq = position;
                        if (seq >= 0 && seq <= length) {
                            services.set(seq, new ServiceAddBean(getSerEdit,
                                    getDesEdit, getCosEdit));
                            sAdapter.notifyDataSetChanged();
                            DatabaseReference databaseReference = myRef.child("vendors").child("category").child(getDesEdit);
                            databaseReference.setValue(new NewServiceBean(getDesEdit));
//                            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(ServiceAddActivity.this, SweetAlertDialog.SUCCESS_TYPE)
//                                    .setTitleText("Success!!!")
//                                    .setContentText("Services edited successfully!!");
//                            sweetAlertDialog.show();
//                            Button btn =sweetAlertDialog.findViewById(R.id.confirm_button);
//                            btn.setBackgroundColor(ContextCompat.getColor(ServiceAddActivity.this,R.color.colorPrimary));
                           // alertDialog.dismiss();
                        } else {
                            Toast.makeText(ServiceAddActivity.this, "Enter correct sequence!!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
            builder.setView(v);
            alertDialog = builder.create();
            alertDialog.show();
        } else {
            Toast.makeText(this, "Please check your internet connection!!!", Toast.LENGTH_SHORT).show();
        }

    }


}
