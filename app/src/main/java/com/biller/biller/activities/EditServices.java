package com.biller.biller.activities;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.biller.biller.adapter.EditServiceAdapter;
import com.biller.biller.beans.NewServiceBean;
import com.biller.biller.beans.ServiceAddBean;
import com.biller.biller.common.CommonMethods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditServices extends AppCompatActivity {

    private EditText service, cost;
    private TextView serviceAdded;
    private AutoCompleteTextView des;
    private String getService, getDes, getCost;
    private DatabaseReference myRef;
    private  List<ServiceAddBean> services = new ArrayList<>();
    private EditServiceAdapter sAdapter;
    private ProgressBar progressBar;
    private String getSerEdit, getDesEdit, getCosEdit;
    ArrayList<String> category = new ArrayList<>();
    ArrayAdapter<String> adapter;
    AutoCompleteTextView getDescription;
    public static RelativeLayout relativeLayout;
    AlertDialog alertDialog;
    EditText serviceNameEditText,costEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_services);
        relativeLayout = findViewById(R.id.rl);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("biller");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        progressBar =  findViewById(R.id.progressBar);
        serviceAdded =  findViewById(R.id.serviceAdded);
        service = findViewById(R.id.editTextService);
        des =  findViewById(R.id.editTextServiceDes);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, category);
        des.setAdapter(adapter);
        cost = findViewById(R.id.editTextServiceCost);
        RecyclerView mRecyclerView =  findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sAdapter = new EditServiceAdapter(services, this,serviceAdded);
        mRecyclerView.setAdapter(sAdapter);
        getDataFromDatabase();

    }

    public boolean validation() {
        boolean valid = true;
        getService = service.getText().toString().trim();
        getDes = des.getText().toString().trim();
        getCost = cost.getText().toString().trim();
        String newDes = getDes.replace(" ","");
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
        Matcher matcher = pattern.matcher(newDes);
        if (!matcher.matches() || getDes.isEmpty()) {
            valid = false;
            des.setError("Enter correct category name");
        } else {
           des.setError(null);
        }
        if (getService.isEmpty()) {
            service.setError("Enter Service name");
            valid = false;
        } else {
            service.setError(null);
        }
        if (getCost.isEmpty()) {
            cost.setError("Enter Service cost");
            valid = false;
        } else {
            cost.setError(null);
        }
        if (!CommonMethods.isNetworkAvailable(EditServices.this)) {
            valid = false;
            Toast.makeText(this, "Check your internet connection!!", Toast.LENGTH_SHORT).show();
        }
        return valid;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.slide_enter, R.transition.slide_exit);
    }

    public void getDataFromDatabase() {
        myRef.child("vendors").child(CommonMethods.sharedPrefrences(EditServices.this).getString("key_id", "biller")).child("services")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ArrayList<HashMap<String, String>> hashMaps = (ArrayList) dataSnapshot.getValue();
                            for (HashMap<String, String> stringHashMap : hashMaps) {
                                services.add(0, new ServiceAddBean(stringHashMap.get("title"),
                                        stringHashMap.get("des"), stringHashMap.get("cost")));
                            }
                            sAdapter.notifyDataSetChanged();
                        }
                        progressBar.setVisibility(View.GONE);
                        serviceAdded.setText("" + services.size());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(EditServices.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
    }

    public boolean validationEdit(View v) {
        boolean valid = true;
        getSerEdit = serviceNameEditText.getText().toString().trim();
        getDesEdit = getDescription.getText().toString().trim();
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
        if (!CommonMethods.isNetworkAvailable(EditServices.this)) {
            valid = false;
            Toast.makeText(this, "Please check your internet connection!!!", Toast.LENGTH_SHORT).show();
        }

        return valid;
    }

    public void addServiceAfterLogin(View view) {
        CommonMethods.vibrator(EditServices.this);
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
            Toast.makeText(this, "Service added to list!!", Toast.LENGTH_SHORT).show();
        }
    }


    public void EditServicesConfirm(View view) {
        if (CommonMethods.isNetworkAvailable(EditServices.this)) {
            if(services.size()==0){
                Toast.makeText(this, "Please add atleast 1 service to proceed", Toast.LENGTH_SHORT).show();
                return;
            }
            CommonMethods.vibrator(EditServices.this);
            progressBar.setVisibility(View.VISIBLE);
            myRef.child("vendors").child(CommonMethods.sharedPrefrences(EditServices.this).getString("key_id", "biller"))
                    .child("services").setValue(services, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Toast.makeText(EditServices.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditServices.this, "Services Edited successfully!!!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        //alertDialog.dismiss();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Please check your network connection!!!", Toast.LENGTH_SHORT).show();
        }

    }

    public void showEditPopup(final int position){
        if (CommonMethods.isNetworkAvailable(EditServices.this)) {
            CommonMethods.vibrator(EditServices.this);
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
//                            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(EditServices.this, SweetAlertDialog.SUCCESS_TYPE)
//                                    .setTitleText("Success!!!")
//                                    .setContentText("Services edited successfully!!");
//                            sweetAlertDialog.show();
//                            alertDialog.dismiss();
//                            Button btn =sweetAlertDialog.findViewById(R.id.confirm_button);
//                            btn.setBackgroundColor(ContextCompat.getColor(EditServices.this,R.color.colorPrimary));
                        } else {
                            Toast.makeText(EditServices.this, "Enter correct sequence!!!", Toast.LENGTH_SHORT).show();
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
