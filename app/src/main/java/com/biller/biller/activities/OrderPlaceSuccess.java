package com.biller.biller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.biller.biller.R;
import com.biller.biller.common.CommonMethods;

public class OrderPlaceSuccess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_place_success);
    }

    public void backToHome(View view) {
        CommonMethods.vibrator(OrderPlaceSuccess.this);
        startActivity(new Intent(this, BillerHomeActivity.class));
        overridePendingTransition(R.transition.slide_enter, R.transition.slide_exit);
        finish();
    }
}
