package com.biller.biller.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.biller.biller.R;
import com.biller.biller.adapter.HomeAdapter;
import com.biller.biller.beans.HomeBean;
import com.biller.biller.common.CommonMethods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class BillerHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeAdapter.ClickListener {

    private List<HomeBean> mUsers = new ArrayList<>();
    private HomeAdapter mUserAdapter;
    FloatingActionButton floatingActionButton;
    private DatabaseReference myRef;
    NavigationView navigationView;
    public static DisplayMetrics displayMetrics;
    private SharedPreferences pref;
    int popupSubmitEnableCounter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biller_home);
        if(DeliveryActivity.crash==1){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    DeliveryActivity.fa.finish();
                }
            }, 3000);
        }
        if(NewOrderActivity.crash==1){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    NewOrderActivity.fa.finish();
                }
            }, 3000);
        }
        pref = CommonMethods.sharedPrefrences(this);
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("biller");
        floatingActionButton = findViewById(R.id.floatingActionButton);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
        mUserAdapter = new HomeAdapter(mUsers, this);
        mUserAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mUserAdapter);
        setHomeIteams();
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BillerHomeActivity.this,ExpenditureActivity.class));
            }
        });
        myRef.child("vendors").child(CommonMethods.sharedPrefrences(this).getString("key_id","")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    HashMap data = (HashMap) dataSnapshot.getValue();
                    TextView view =   navigationView.getHeaderView(0).findViewById(R.id.vendorName);
                    view.setText(data.get("fullname").toString());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("key_firmname",data.get("firmname").toString().substring(0,2));
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    private void setHomeIteams() {
        mUsers.add(0,new HomeBean("New order",R.drawable.u1));
        mUsers.add(1,new HomeBean("Delivery",R.drawable.u2));
        mUsers.add(2,new HomeBean("Daily report",R.drawable.u3));
        mUsers.add(3,new HomeBean("My Account",R.drawable.u4));
        mUserAdapter.notifyDataSetChanged();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.biller_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            SharedPreferences.Editor editor = CommonMethods.sharedPrefrences(this).edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(this,LoginActivity.class));
            overridePendingTransition(R.transition.slide_in, R.transition.slide_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_myaccount) {
            startActivity(new Intent(this,MyAccountActivity.class));
            overridePendingTransition(R.transition.slide_in, R.transition.slide_out);
        } else if (id == R.id.nav_delivery) {
            startActivity(new Intent(this,DeliveryActivity.class));
            overridePendingTransition(R.transition.slide_in, R.transition.slide_out);
        } else if (id == R.id.nav_report) {
            startActivity(new Intent(this,MyOrderActivity.class));
            overridePendingTransition(R.transition.slide_in, R.transition.slide_out);
        } else if (id == R.id.nav_service) {
            startActivity(new Intent(this,EditServices.class));
            overridePendingTransition(R.transition.slide_in, R.transition.slide_out);
        } else if (id == R.id.nav_subscri) {
            startActivity(new Intent(this,ExtendSubscriptionActivity.class));
            overridePendingTransition(R.transition.slide_in, R.transition.slide_out);
        }
        else if (id == R.id.nav_neworder) {
            startActivity(new Intent(this,NewOrderActivity.class));
            overridePendingTransition(R.transition.slide_in, R.transition.slide_out);
        }
        else if (id == R.id.nav_perKg) {
            AlertDialog.Builder alert = new AlertDialog.Builder(BillerHomeActivity.this);
            LayoutInflater inflater = BillerHomeActivity.this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.perkg_popup, null);
            final EditText editTextwWas = dialogView.findViewById(R.id.editTextPerKgPrice);
            final EditText editTextIron = dialogView.findViewById(R.id.editTextPerKgPriceIroning);
            final EditText editTextWasIron = dialogView.findViewById(R.id.editTextPerKgPriceWasIron);
            final Button button = dialogView.findViewById(R.id.buttonPerKgPrice);
            CheckBox checkBoxWas = dialogView.findViewById(R.id.checkBoxWas);
            CheckBox checkBoxIron = dialogView.findViewById(R.id.checkBoxIron);
            CheckBox checkBoxWasIron = dialogView.findViewById(R.id.checkBoxWasIron);
            final ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);
            checkBoxWas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        editTextwWas.setVisibility(View.VISIBLE);
                        popupSubmitEnableCounter++;
                    }else{
                        editTextwWas.setVisibility(View.GONE);
                        popupSubmitEnableCounter--;

                    }
                    if(popupSubmitEnableCounter!=0){
                        button.setVisibility(View.VISIBLE);
                    }else{
                        button.setVisibility(View.GONE);
                    }
                }
            });
            checkBoxIron.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        editTextIron.setVisibility(View.VISIBLE);
                        popupSubmitEnableCounter++;
                    }else{
                        editTextIron.setVisibility(View.GONE);
                        popupSubmitEnableCounter--;
                    }
                    if(popupSubmitEnableCounter!=0){
                        button.setVisibility(View.VISIBLE);
                    }else{
                        button.setVisibility(View.GONE);
                    }
                }
            });
            checkBoxWasIron.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        editTextWasIron.setVisibility(View.VISIBLE);
                        popupSubmitEnableCounter++;
                    }else{
                        editTextWasIron.setVisibility(View.GONE);
                        popupSubmitEnableCounter--;
                    }
                    if(popupSubmitEnableCounter!=0){
                        button.setVisibility(View.VISIBLE);
                    }else{
                        button.setVisibility(View.GONE);
                    }
                }
            });
            final AlertDialog dialog = alert.create();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(CommonMethods.isNetworkAvailable(BillerHomeActivity.this)){
                        String priceWas;
                        String priceIron;
                        String priceWasIron;
                        boolean success = false;
                        if(editTextwWas.getVisibility()==View.VISIBLE && editTextIron.getVisibility()!=View.VISIBLE && editTextWasIron.getVisibility()!=View.VISIBLE  ){
                            priceWas = editTextwWas.getText().toString();
                            if(!priceWas.isEmpty()){
                                Float getPrice = Float.parseFloat(priceWas);
                                editTextwWas.setError(null);
                                progressBar.setVisibility(View.VISIBLE);
                                editTextwWas.setVisibility(View.GONE);
                                button.setVisibility(View.GONE);
                                myRef.child("vendors").child(CommonMethods.sharedPrefrences(BillerHomeActivity.this).getString("key_id","")).child("perkgprice").child("washing").setValue(getPrice);
                                success = true;

                            }else{
                                editTextwWas.setError("Enter price");
                            }
                        }
                        else if(editTextwWas.getVisibility()!=View.VISIBLE && editTextIron.getVisibility()==View.VISIBLE && editTextWasIron.getVisibility()!=View.VISIBLE ){
                            priceIron = editTextIron.getText().toString();
                            if(!priceIron.isEmpty()){
                                Float getPrice = Float.parseFloat(priceIron);
                                editTextIron.setError(null);
                                progressBar.setVisibility(View.VISIBLE);
                                editTextIron.setVisibility(View.GONE);
                                button.setVisibility(View.GONE);
                                myRef.child("vendors").child(CommonMethods.sharedPrefrences(BillerHomeActivity.this).getString("key_id","")).child("perkgprice").child("ironing").setValue(getPrice);
                                success = true;
                            }else{
                                editTextIron.setError("Enter price");
                            }
                        }
                        else if(editTextwWas.getVisibility()!=View.VISIBLE && editTextIron.getVisibility()!=View.VISIBLE && editTextWasIron.getVisibility()==View.VISIBLE ){
                            priceWasIron = editTextWasIron.getText().toString();
                            if(!priceWasIron.isEmpty()){
                                Float getPrice = Float.parseFloat(priceWasIron);
                                editTextWasIron.setError(null);
                                progressBar.setVisibility(View.VISIBLE);
                                editTextWasIron.setVisibility(View.GONE);
                                button.setVisibility(View.GONE);
                                myRef.child("vendors").child(CommonMethods.sharedPrefrences(BillerHomeActivity.this).getString("key_id","")).child("perkgprice").child("washingplusironing").setValue(getPrice);
                                success = true;
                            }else{
                                editTextWasIron.setError("Enter price");

                            }
                        }
                        else if(editTextwWas.getVisibility()==View.VISIBLE && editTextIron.getVisibility()==View.VISIBLE && editTextWasIron.getVisibility()!=View.VISIBLE ){
                            priceIron = editTextIron.getText().toString();
                            priceWas = editTextwWas.getText().toString();
                            if(!priceIron.isEmpty() && !priceWas.isEmpty()){
                                Float getPriceIron = Float.parseFloat(priceIron);
                                Float getPriceWas = Float.parseFloat(priceWas);
                                progressBar.setVisibility(View.VISIBLE);
                                editTextIron.setVisibility(View.GONE);
                                editTextwWas.setVisibility(View.GONE);
                                button.setVisibility(View.GONE);
                                myRef.child("vendors").child(CommonMethods.sharedPrefrences(BillerHomeActivity.this).getString("key_id","")).child("perkgprice").child("ironing").setValue(getPriceIron);
                                myRef.child("vendors").child(CommonMethods.sharedPrefrences(BillerHomeActivity.this).getString("key_id","")).child("perkgprice").child("washing").setValue(getPriceWas);
                                success = true;
                            }else{
                                Toast.makeText(BillerHomeActivity.this, "Enter price", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(editTextwWas.getVisibility()==View.VISIBLE && editTextIron.getVisibility()!=View.VISIBLE && editTextWasIron.getVisibility()==View.VISIBLE ){
                            priceWasIron = editTextWasIron.getText().toString();
                            priceWas = editTextwWas.getText().toString();
                            if(!priceWasIron.isEmpty()&& !priceWas.isEmpty()){
                                Float getPriceWas = Float.parseFloat(priceWas);
                                Float getPriceWasIron = Float.parseFloat(priceWasIron);
                                progressBar.setVisibility(View.VISIBLE);
                                editTextWasIron.setVisibility(View.GONE);
                                editTextwWas.setVisibility(View.GONE);
                                button.setVisibility(View.GONE);
                                myRef.child("vendors").child(CommonMethods.sharedPrefrences(BillerHomeActivity.this).getString("key_id","")).child("perkgprice").child("washingplusironing").setValue(getPriceWasIron);
                                myRef.child("vendors").child(CommonMethods.sharedPrefrences(BillerHomeActivity.this).getString("key_id","")).child("perkgprice").child("washing").setValue(getPriceWas);
                                success = true;
                            }else{
                                Toast.makeText(BillerHomeActivity.this, "Enter price", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(editTextwWas.getVisibility()!=View.VISIBLE && editTextIron.getVisibility()==View.VISIBLE && editTextWasIron.getVisibility()==View.VISIBLE ){
                            priceIron = editTextIron.getText().toString();
                            priceWasIron = editTextWasIron.getText().toString();
                            if(!priceIron.isEmpty() && !priceWasIron.isEmpty()){
                                Float getPriceWasIron = Float.parseFloat(priceWasIron);
                                Float getPriceIron = Float.parseFloat(priceIron);
                                progressBar.setVisibility(View.VISIBLE);
                                editTextIron.setVisibility(View.GONE);
                                editTextWasIron.setVisibility(View.GONE);
                                button.setVisibility(View.GONE);
                                myRef.child("vendors").child(CommonMethods.sharedPrefrences(BillerHomeActivity.this).getString("key_id","")).child("perkgprice").child("ironing").setValue(getPriceIron);
                                myRef.child("vendors").child(CommonMethods.sharedPrefrences(BillerHomeActivity.this).getString("key_id","")).child("perkgprice").child("washingplusironing").setValue(getPriceWasIron);
                                success = true;
                            }else{
                                Toast.makeText(BillerHomeActivity.this, "Enter price", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(editTextwWas.getVisibility()==View.VISIBLE && editTextIron.getVisibility()==View.VISIBLE && editTextWasIron.getVisibility()==View.VISIBLE ){
                            priceWasIron = editTextWasIron.getText().toString();
                            priceWas = editTextwWas.getText().toString();
                            priceIron = editTextIron.getText().toString();
                            if(!priceWasIron.isEmpty() && !priceWas.isEmpty() && !priceIron.isEmpty()){
                                Float getPriceWas = Float.parseFloat(priceWas);
                                Float getPriceWasIron = Float.parseFloat(priceWasIron);
                                Float getPriceIron = Float.parseFloat(priceIron);
                                progressBar.setVisibility(View.VISIBLE);
                                editTextWasIron.setVisibility(View.GONE);
                                editTextIron.setVisibility(View.GONE);
                                editTextwWas.setVisibility(View.GONE);
                                button.setVisibility(View.GONE);
                                myRef.child("vendors").child(CommonMethods.sharedPrefrences(BillerHomeActivity.this).getString("key_id","")).child("perkgprice").child("washingplusironing").setValue(getPriceWasIron);
                                myRef.child("vendors").child(CommonMethods.sharedPrefrences(BillerHomeActivity.this).getString("key_id","")).child("perkgprice").child("ironing").setValue(getPriceIron);
                                myRef.child("vendors").child(CommonMethods.sharedPrefrences(BillerHomeActivity.this).getString("key_id","")).child("perkgprice").child("washing").setValue(getPriceWas);
                                success = true;
                            }else{
                                Toast.makeText(BillerHomeActivity.this, "Enter price", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if(success){
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    Toast.makeText(BillerHomeActivity.this, "Per kg price setup successfully!!!", Toast.LENGTH_SHORT).show();

                                }
                            }, 3000);
                        }


                    }else{
                        Toast.makeText(BillerHomeActivity.this, "Check internet connection!!", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            dialog.setView(dialogView);
            dialog.setTitle("Per kg price setup");
            dialog.setIcon(getResources().getDrawable(R.drawable.ic_perkg));
            dialog.show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void itemClicked(View v, int position) {
        switch (position){
            case 0:
                startActivity(new Intent(BillerHomeActivity.this,NewOrderActivity.class));
                overridePendingTransition(R.transition.slide_in, R.transition.slide_out);

                break;
            case 1:
                startActivity(new Intent(BillerHomeActivity.this,DeliveryActivity.class));
                overridePendingTransition(R.transition.slide_in, R.transition.slide_out);

                break;
            case 2:
                startActivity(new Intent(BillerHomeActivity.this,MyOrderActivity.class));
                overridePendingTransition(R.transition.slide_in, R.transition.slide_out);

                break;
            case 3:
                startActivity(new Intent(BillerHomeActivity.this,MyAccountActivity.class));
                overridePendingTransition(R.transition.slide_in, R.transition.slide_out);

                break;
        }
        
    }
}
