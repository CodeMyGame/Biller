package com.biller.biller.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.biller.biller.R;
import com.biller.biller.common.CommonMethods;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class RegisterActivity extends Activity {

    EditText firm,address,contact,business;
    String getFirm,getAddress,getContact,getBusiness,getFullName,getPin,getCity,getEmail,getPassword;
    EditText fullName,pin,city,password,email;
    public static Activity reActivity;
    ImageView getLocationImageView;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,LoginActivity.class));
        overridePendingTransition(R.transition.slide_enter, R.transition.slide_exit);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reActivity = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        firm = findViewById(R.id.editTextFirmName);
        address = findViewById(R.id.editTextFirmAddress);
        contact = findViewById(R.id.editTextContact);
        business = findViewById(R.id.editTextBusiness);
        fullName = findViewById(R.id.editTextFullName);
        pin = findViewById(R.id.editTextPINCode);
        city = findViewById(R.id.editTextCity);
        password = findViewById(R.id.editTextPassword);
        email = findViewById(R.id.editTextEmail);
        getLocationImageView = findViewById(R.id.getLocation);
        PlaceAutocompleteFragment places= (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                address.setText(place.getAddress());
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

    }



    public boolean validation() {
        boolean valid = true;
        getFirm = firm.getText().toString().trim();
        getAddress = address.getText().toString().trim();
        getContact = contact.getText().toString().trim();
        getBusiness = business.getText().toString().trim();
        getFullName = fullName.getText().toString().trim();
        getPin = pin.getText().toString().trim();
        getCity = city.getText().toString().trim();
        getEmail = email.getText().toString().trim();
        getPassword = password.getText().toString().trim();
        if (getFirm.isEmpty() || getFirm.length()<5) {
            firm.setError("Enter your correct firm name of length 6 or more");
            valid = false;
        } else {
            firm.setError(null);
        }
        if (getAddress.isEmpty()) {
            address.setError("Enter your Address");
            valid = false;
        } else {
            address.setError(null);
        }
        if (getContact.isEmpty() ||getContact.length()!=10) {
            contact.setError("Enter your contact");
            valid = false;
        } else {
            contact.setError(null);
        }
        if (getBusiness.isEmpty()) {
            business.setError("Enter you bussiness name");
            valid = false;
        } else {
            business.setError(null);
        }
        if (getFullName.isEmpty()) { // here we check single word
            fullName.setError("Enter your Full name");
            valid = false;
        } else {
            fullName.setError(null);
        }
        if (getPin.isEmpty() || getPin.length()!=6) {  //6digits
            pin.setError("Enter 6 digit PIN-CODE");
            valid = false;
        } else {
            pin.setError(null);
        }
        if (getCity.isEmpty()) {
            city.setError("Enter your city");
            valid = false;
        } else {
            city.setError(null);
        }
        if (getEmail.isEmpty()||!android.util.Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()) {  //6digits
            email.setError("Enter your correct mail id");
            valid = false;
        } else {
            email.setError(null);
        }
        if (getPassword.isEmpty()) {
            password.setError("Enter your password");
            valid = false;
        } else {
            password.setError(null);
        }
        if(!CommonMethods.isNetworkAvailable(RegisterActivity.this)){
            valid = false;
            Toast.makeText(this, "Check your internet connection!!!", Toast.LENGTH_SHORT).show();
        }
        return valid;
    }

    public void chooseSubscription(View view) {
        CommonMethods.vibrator(RegisterActivity.this);
        if(validation()) {
            try {
                Intent intent = new Intent(RegisterActivity.this, SubscriptionActivity.class);
                intent.putExtra("firmName", getFirm);
                intent.putExtra("firmAddress", getAddress);
                intent.putExtra("contact", getContact);
                intent.putExtra("bussiness", getBusiness);
                intent.putExtra("fullname", getFullName);
                intent.putExtra("city", getCity);
                intent.putExtra("pin", getPin);
                intent.putExtra("email", getEmail);
                intent.putExtra("password", getPassword);
                startActivity(intent);
                overridePendingTransition(R.transition.slide_in, R.transition.slide_out);
            }catch (Exception e){e.printStackTrace();}
        }
    }
}
