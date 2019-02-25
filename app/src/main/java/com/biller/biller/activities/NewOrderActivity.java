package com.biller.biller.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.biller.biller.R;
import com.biller.biller.adapter.KartAdapter;
import com.biller.biller.adapter.ViewPagerAdapter;
import com.biller.biller.beans.NewOrderBean;
import com.biller.biller.beans.ServiceAddBean;
import com.biller.biller.common.CommonMethods;
import com.biller.biller.fragmentOrder.Category1;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import cn.pedant.SweetAlert.SweetAlertDialog;

public class NewOrderActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static TextView serviceAdded, serviceTotalCost;
    public static int totalServices = 0;
    public static int totalCost = 0;
    float totalCostIfInKgs=0;
    private String getName, getContact, getAddress;
    public static HashMap<String, Integer> integerHashMap;
    private DatabaseReference myRef;
    private EditText textViewName, textViewContact, textViewAddress;
    ProgressBar progressBar;
    String invoice=null;
    private SharedPreferences pref;
    String getFirmName;
    String send;
    String details;
    Button buttonConfirm;
    Set<String> category = new HashSet<>();
    ImageView getLocationImageView;
    String choiceStatus = "quantity";
    float priceWashing=0,priceWashingPlusIroning=0,priceIroning=0;
    List<String>  categories = new ArrayList<>();
    float price = 12;
    public static List<ServiceAddBean> kartServices = new ArrayList<>();
    public static int crash = 0;
    public static Activity fa;

    @Override
    protected void onStop() {
        super.onStop();
        crash = 1;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        fa = this;
        kartServices.clear();
        totalServices = 0;
        totalCost = 0;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        pref = CommonMethods.sharedPrefrences(this);
        integerHashMap = new HashMap<>();
        integerHashMap.clear();
        progressBar =  findViewById(R.id.progressBar);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("biller");
        buttonConfirm = findViewById(R.id.buttonConfirm);
        serviceAdded =  findViewById(R.id.serviceAdded);
        serviceTotalCost = findViewById(R.id.serviceAddedMoney);
        textViewName = findViewById(R.id.editTextCname);
        textViewContact =  findViewById(R.id.editTextContact);
        textViewAddress =  findViewById(R.id.editTextAddress);
        getLocationImageView = findViewById(R.id.imageViewLocation);
        myRef.child("vendors").child(CommonMethods.sharedPrefrences(NewOrderActivity.this).getString("key_id","")).child("perkgprice").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    HashMap priceDetails = (HashMap) dataSnapshot.getValue();
                    Set mapSet =  priceDetails.entrySet();
                    //Create iterator on Set
                    Iterator mapIterator = mapSet.iterator();
                    while (mapIterator.hasNext()) {
                        Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                        String keyValue = (String) mapEntry.getKey();
                        String value =  mapEntry.getValue().toString();
                        if(keyValue.equals("washing")){
                            priceWashing =  Float.parseFloat(value);
                        }
                        if(keyValue.equals("ironing")){
                            priceIroning =  Float.parseFloat(value);
                        }
                        if(keyValue.equals("washingplusironing")){
                            priceWashingPlusIroning =  Float.parseFloat(value);
                        }
                    }

                }else{
                    Toast.makeText(NewOrderActivity.this, "Please update per Kg price in your profile!! ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        final PlaceAutocompleteFragment places= (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                textViewAddress.setText(place.getAddress());
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(getApplicationContext(),status.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        final View root = places.getView();
        getLocationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                root.post(new Runnable() {
                    @Override
                    public void run() {
                        root.findViewById(R.id.place_autocomplete_search_input)
                                .performClick();
                    }
                });
            }
        });
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()) {
                    final AlertDialog.Builder alert = new AlertDialog.Builder(NewOrderActivity.this);
                    LayoutInflater inflater = NewOrderActivity.this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.confirm_order_popup, null);
                    final Switch aSwitch = dialogView.findViewById(R.id.switchChoice);
                    final EditText editText = dialogView.findViewById(R.id.editTextTotalKgs);
                    final TextView textView = dialogView.findViewById(R.id.textViewTotalBill);
                    final Button buttonConfirmFinal = dialogView.findViewById(R.id.buttonConfirm);
                    final ProgressBar progressBarPopup = dialogView.findViewById(R.id.progressBar);
                    final TextView textViewTotalItem = dialogView.findViewById(R.id.textViewTotalItem);
                    aSwitch.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    progressBarPopup.setVisibility(View.GONE);
                    buttonConfirmFinal.setVisibility(View.VISIBLE);
                    textViewTotalItem.setVisibility(View.VISIBLE);
                    textViewTotalItem.setText("Total item = "+totalServices);
                    final Spinner spinner =  dialogView.findViewById(R.id.spinner);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if(editText.getText().length()!=0){
                                textView.setText("Total bill amount = ₹ 0");
                                totalCostIfInKgs =0 ;
                                editText.setText(null);
                            }
                            if(i==0){
                                price = priceWashing;
                            }else if(i==1){
                                price = priceWashingPlusIroning;
                            }else{
                                price = priceIroning;
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                            price = priceWashing;
                        }
                    });

                    categories.clear();
                    categories.add("Washing("+priceWashing+")");
                    categories.add("Washing + Ironing("+priceWashingPlusIroning+")");
                    categories.add("Ironing("+priceIroning+")");
                    final ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(NewOrderActivity.this, android.R.layout.simple_spinner_item, categories);

                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // attaching data adapter to spinner
                    spinner.setAdapter(dataAdapter);
                    final AlertDialog dialog = alert.create();
                    buttonConfirmFinal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(choiceStatus.equals("quantity")){
                                dialog.dismiss();
                                progressBar.setVisibility(View.VISIBLE);
                                buttonConfirm.setText("Wait...");
                                buttonConfirm.setEnabled(false);
                                new MyAsyntask().execute();

                            }else{

                                if(editText.getText().length()==0 ){
                                    editText.setError("Enter total kg's");
                                }else{
                                    if(Float.parseFloat(editText.getText().toString())==0.0){
                                        editText.setError("Zero kg not allowed!!");
                                    }else{
                                        editText.setError(null);
                                        dialog.dismiss();
                                        progressBar.setVisibility(View.VISIBLE);
                                        buttonConfirm.setText("Wait...");
                                        buttonConfirm.setEnabled(false);
                                        new MyAsyntask().execute();
                                    }

                                }
                            }

                        }
                    });
                    textView.setText("Total bill amount = ₹ "+totalCost);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if(charSequence.length()!=0 ){
                                textView.setText("Total bill amount = ₹ "+Float.parseFloat(charSequence.toString())*price);
                                totalCostIfInKgs = Float.parseFloat(charSequence.toString())*price;
                            }else{
                                textView.setText("Total bill amount = ₹ 0");
                                totalCostIfInKgs =0 ;
                            }
                        }
                        @Override
                        public void afterTextChanged(Editable editable) {
                        }
                    });
                    aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if(b){
                                choiceStatus = "kgs";
                                aSwitch.setText("Kilograms");
                                spinner.setVisibility(View.VISIBLE);
                                editText.setVisibility(View.VISIBLE);
                                textView.setText("Total bill amount = ₹ 0");
                            }else{
                                choiceStatus = "quantity";
                                aSwitch.setText("Quantity");
                                spinner.setVisibility(View.GONE);
                                editText.setVisibility(View.GONE);
                                textView.setText("Total bill amount = ₹ "+totalCost);
                            }
                        }
                    });
                    dialog.setView(dialogView);
                    dialog.setTitle("Order confirmation");
                    dialog.setIcon(getResources().getDrawable(R.drawable.ic_warning));
                    dialog.show();
                }

            }
        });

        myRef.child("vendors").child(CommonMethods.sharedPrefrences(this).getString("key_id","biller")).child("services").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList serviceList =(ArrayList) dataSnapshot.getValue();
                    for (int i = 0; i < serviceList.size(); i++) {
                        HashMap hashMap1 = (HashMap) serviceList.get(i);
                        category.add(hashMap1.get("des").toString());
                    }
                    viewPager = findViewById(R.id.viewpager);
                    viewPager.setOffscreenPageLimit(category.size());
                    setupViewPager(viewPager);
                    tabLayout =  findViewById(R.id.tabs);
                    tabLayout.setupWithViewPager(viewPager);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NewOrderActivity.this,"Error : "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = "" + mdformat.format(calendar.getTime());
        return strDate;
    }

    public boolean validation() {
        boolean valid = true;
        getName = textViewName.getText().toString().trim();
        getAddress = textViewAddress.getText().toString().trim();
        getContact = textViewContact.getText().toString().trim();

        if (getName.isEmpty()) {
            textViewName.setError("Enter your Full name");
            valid = false;
        } else {
            textViewName.setError(null);
        }
        if (getAddress.isEmpty()) {
            textViewAddress.setError("Enter your correct Address");
            valid = false;
        } else {
            textViewAddress.setError(null);
        }
        if (getContact.isEmpty() || getContact.length() != 10) {
            textViewContact.setError("Enter your correct contact");
            valid = false;
        } else {
            textViewContact.setError(null);
        }
        if (totalServices < 1) {
            valid = false;
            Toast.makeText(this, "Please add atleast 1 service to proceed", Toast.LENGTH_SHORT).show();
        }
        if (!CommonMethods.isNetworkAvailable(NewOrderActivity.this)) {
            valid = false;
            Toast.makeText(this, "Please check your network connection!!!", Toast.LENGTH_SHORT).show();
        }
        return valid;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (Iterator<String> it = category.iterator(); it.hasNext(); ) {
            Fragment fragment = new Category1();
            Bundle args = new Bundle();
            String cat = it.next();
            args.putString("key", cat);
            fragment.setArguments(args);
            adapter.addFragment(fragment, cat);
        }
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.slide_enter, R.transition.slide_exit);
        finish();
    }

    private void generateInvoice() {
        myRef.child("vendors").child(CommonMethods.sharedPrefrences(NewOrderActivity.this).getString("key_id", "biller")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String newcount = "";
                    HashMap hashMap = (HashMap) dataSnapshot.getValue();
                    String getFirm = hashMap.get("firmname").toString().toUpperCase().substring(0, 2);
                    getFirmName = hashMap.get("firmname").toString();
                    String getAlphabet = hashMap.get("alphabet").toString();
                    String count = hashMap.get("count").toString();
                    int counter = Integer.parseInt(count);
                    if (counter == 1000) {
                        int ascii = getAlphabet.charAt(0);
                        ascii++;
                        char newAlphabet = (char) ascii;
                        invoice = getFirm + newAlphabet + "0001";
                        myRef.child("vendors").child(pref.getString("key_id", "biller")).child("alphabet").setValue("" + newAlphabet);
                        myRef.child("vendors").child(pref.getString("key_id", "biller")).child("count").setValue("0001");
                        saveToDatabase(invoice);

                    } else {
                        counter++;
                        if (counter >= 0 && counter < 10) {
                            newcount = "000" + (counter);
                        } else if (counter >= 10 && counter < 100) {
                            newcount = "00" + (counter);
                        } else if (counter >= 100 && counter < 1000) {
                            newcount = "0" + (counter);
                        } else {
                            newcount = "" + (counter);
                        }
                        myRef.child("vendors").child(pref.getString("key_id", "biller")).child("count").setValue(newcount);
                        invoice = getFirm + getAlphabet + newcount;
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(invoice!=null){
                                    saveToDatabase(invoice);
                                }else{
                                    Toast.makeText(NewOrderActivity.this, "Please try again!!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }, 10);
                    }

                } else {
                    Toast.makeText(NewOrderActivity.this, "Try again!!!!", Toast.LENGTH_SHORT).show();
                    buttonConfirm.setText("Confirm");
                    buttonConfirm.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                buttonConfirm.setText("Confirm");
                buttonConfirm.setEnabled(true);
                Toast.makeText(NewOrderActivity.this, "ERROR Please try again!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void saveToDB(){
        if(choiceStatus.equals("quantity")){
            totalCostIfInKgs = totalCost;
        }
        myRef.child("vendors").child(CommonMethods.sharedPrefrences(NewOrderActivity.this).getString("key_id", "biller")).child("customers").child(invoice).setValue(
                new NewOrderBean("" + totalCostIfInKgs, "" + totalServices, getName, getContact, getAddress, integerHashMap, getCurrentDate(), invoice, "no", details,choiceStatus), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
//                            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(NewOrderActivity.this, SweetAlertDialog.SUCCESS_TYPE)
//                                    .setTitleText("Success!!!")
//                                    .setContentText("Order placed successfully!!");
//                            sweetAlertDialog.show();
//                            Toast.makeText(NewOrderActivity.this, "Order placed successfully!!", Toast.LENGTH_SHORT).show();
//                            Button btn =sweetAlertDialog.findViewById(R.id.confirm_button);
//                            btn.setBackgroundColor(ContextCompat.getColor(NewOrderActivity.this,R.color.colorPrimary));
                            buttonConfirm.setText("Confirm");
                            buttonConfirm.setEnabled(true);
                            textViewName.setText(null);
                            textViewContact.setText(null);
                            textViewAddress.setText(null);
                            progressBar.setVisibility(View.GONE);


                        }

                    }
                });
    }
    public void sendRequest(String url) {
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    send = jsonObject.getString("Status");
                    details = jsonObject.getString("Details");
                    if (send.equals("Success")) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new MyAsyntaskSaveToDB().execute();
                            }
                        }, 10);

                    } else {
                        Toast.makeText(NewOrderActivity.this, "Please try again!!!", Toast.LENGTH_SHORT).show();
                        buttonConfirm.setText("Confirm");
                        buttonConfirm.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    Toast.makeText(NewOrderActivity.this, "Error : "+e.toString(), Toast.LENGTH_SHORT).show();
                    buttonConfirm.setText("Confirm");
                    progressBar.setVisibility(View.GONE);
                    buttonConfirm.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                buttonConfirm.setText("Confirm");
                progressBar.setVisibility(View.GONE);
                buttonConfirm.setEnabled(true);
                Toast.makeText(NewOrderActivity.this, "ERROR:"+error.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(NewOrderActivity.this);
        requestQueue.add(strReq);
    }

    private void saveToDatabase(final String invoice) {
        if(choiceStatus.equals("quantity")){
            totalCostIfInKgs = totalCost;
        }
        String url = "http://api.planettechlabs.com/_OTP3/sendTSMS.php?name=" + getName.replace(" ","%20") + "&mobile=" + getContact + "&invno=" + invoice + "&amount=" + totalCostIfInKgs + "&shop=" + getFirmName.replace(" ","%20");
        sendRequest(url);

    }

    public void showKart(View view) {
        if(!kartServices.isEmpty()){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.kartdialog, null);
            RecyclerView mRecyclerView = dialogView.findViewById(R.id.recyclerView);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            KartAdapter sAdapter = new KartAdapter(kartServices, this);
            mRecyclerView.setAdapter(sAdapter);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setTitle("Your Cart");
            AlertDialog b = dialogBuilder.create();
            b.show();
        }else{
            Toast.makeText(this, "No item in the cart", Toast.LENGTH_SHORT).show();
        }

    }

    class MyAsyntask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            generateInvoice();
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    class MyAsyntaskSaveToDB extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            saveToDB();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
