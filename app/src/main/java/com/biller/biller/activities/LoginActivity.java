package com.biller.biller.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.biller.biller.R;
import com.biller.biller.common.CommonMethods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends Activity {

    private EditText username,password;
    private String getUsername,getPassword;
    private DatabaseReference myRef;
    private ProgressDialog progressDialog;
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        pref = CommonMethods.sharedPrefrences(this);
        if(pref.contains("key_id")){
            startActivity(new Intent(this,BillerHomeActivity.class));
            finish();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("biller");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        username = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
    }


    public boolean validation() {
        boolean valid = true;
        getUsername = username.getText().toString().trim();
        getPassword= password.getText().toString().trim();
        if (getUsername.isEmpty() || getUsername.length()!=10) {
            username.setError("Enter correct username");
            valid = false;
        } else {
            username.setError(null);
        }
        if (getPassword.isEmpty()) {
            password.setError("Enter correct password");
            valid = false;
        } else {
            password.setError(null);
        }
        if(!CommonMethods.isNetworkAvailable(LoginActivity.this)){
            valid = false;
            Toast.makeText(this, "Check your internet connection!!!", Toast.LENGTH_SHORT).show();
        }
        return valid;
    }
    public void login(View view) {
        CommonMethods.vibrator(LoginActivity.this);
        if(validation()){
            progressDialog.show();
            myRef.child("vendors").child(getUsername).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String getPass = (String)dataSnapshot.child("password").getValue();
                        if(getPass.equals(getPassword)){
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("key_id",getUsername);
                            editor.apply();
                            startActivity(new Intent(LoginActivity.this,BillerHomeActivity.class));
                            overridePendingTransition(R.transition.slide_in, R.transition.slide_out);
                            finish();
                        }else{
                            Toast.makeText(LoginActivity.this, "password incorrect!!", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.hide();
                    }else{
                        Toast.makeText(LoginActivity.this, "Username incorrect", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(LoginActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void registerActivity(View view) {
        CommonMethods.vibrator(LoginActivity.this);
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
        overridePendingTransition(R.transition.slide_enter, R.transition.slide_exit);
        finish();
    }
}
