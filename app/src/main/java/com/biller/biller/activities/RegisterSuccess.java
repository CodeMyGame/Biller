package com.biller.biller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.biller.biller.R;

public class RegisterSuccess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);
    }

    public void backLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.transition.slide_enter, R.transition.slide_exit);
        finish();
    }
}
