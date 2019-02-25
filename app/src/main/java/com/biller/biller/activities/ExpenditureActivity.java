package com.biller.biller.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.biller.biller.R;
import com.biller.biller.beans.ExpenditureBean;
import com.biller.biller.common.CommonMethods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

//import cn.pedant.SweetAlert.SweetAlertDialog;

public class ExpenditureActivity extends AppCompatActivity {

    private EditText editTextAmount,editTextDesc;
    private Button buttonSave;
    private String getAmount,getDesc;
    private DatabaseReference myRef;
    private SharedPreferences pref;
    private ProgressBar progressBar;
    public boolean validation(){
        boolean status = true;
        getAmount = editTextAmount.getText().toString().trim();
        getDesc = editTextDesc.getText().toString().trim();
        if(getAmount.isEmpty()){
            status = false;
            editTextAmount.setError("Enter Amount");
        }else{
            editTextAmount.setError(null);
        }
        if(getDesc.isEmpty()){
            status = false;
            editTextDesc.setError("Enter Description");
        }else{
            editTextDesc.setError(null);
        }
        if(!CommonMethods.isNetworkAvailable(this)){
            status = false;
            Toast.makeText(this, "Check your internet connection!!", Toast.LENGTH_SHORT).show();
        }
        return status;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenditure);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Expenditure");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pref = CommonMethods.sharedPrefrences(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("biller");
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextDesc =  findViewById(R.id.editTextDescrip);
        buttonSave =  findViewById(R.id.buttonSave);
        progressBar  =  findViewById(R.id.progressBar);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonMethods.vibrator(ExpenditureActivity.this);
                if(validation()){
                    progressBar.setVisibility(View.VISIBLE);
                    myRef.child("vendors").child(pref.getString("key_id","biller")).child("expenditure").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                ArrayList hashMap = (ArrayList) dataSnapshot.getValue();
                                int size = hashMap.size();
                                myRef.child("vendors").child(pref.getString("key_id","biller")).child("expenditure").child(""+size)
                                        .setValue(new ExpenditureBean(getAmount,getDesc,CommonMethods.getCurrentDate()));
                            }else{
                                myRef.child("vendors").child(pref.getString("key_id","biller")).child("expenditure").child("0")
                                        .setValue(new ExpenditureBean(getAmount,getDesc,CommonMethods.getCurrentDate()));
                            }
                            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(ExpenditureActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success!!!")
                                    .setContentText("Expenditure saved successfully!!");
                            sweetAlertDialog.show();
                            Button btn =sweetAlertDialog.findViewById(R.id.confirm_button);
                            btn.setBackgroundColor(ContextCompat.getColor(ExpenditureActivity.this,R.color.colorPrimary));
                            Toast.makeText(ExpenditureActivity.this, "Scuss", Toast.LENGTH_SHORT).show();
                            editTextAmount.setText("");
                            editTextDesc.setText("");
                            progressBar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(ExpenditureActivity.this, "ERROR: Slow internet connection!!!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }
}
