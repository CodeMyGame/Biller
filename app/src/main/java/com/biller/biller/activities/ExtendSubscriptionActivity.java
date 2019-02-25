package com.biller.biller.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.biller.biller.R;
import com.biller.biller.common.CommonMethods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ExtendSubscriptionActivity extends AppCompatActivity {

    private TextView textViewEndDate, textViewStartDate;
    private DatabaseReference myRef;
    private ProgressBar progressBar;
    String end_date;
    Button extendButton;
    SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extend_subscription);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("biller");
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        textViewEndDate = (TextView) findViewById(R.id.textViewEndDate);
        textViewStartDate = (TextView) findViewById(R.id.textViewStartDate);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        extendButton = (Button)findViewById(R.id.extendSubs);
        myRef.child("vendors").child(CommonMethods.sharedPrefrences(this).getString("key_id", "biller")).child("end_date_of_subsscription").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    end_date = dataSnapshot.getValue().toString();
                    textViewEndDate.setText("Subscription end date : " + end_date);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ExtendSubscriptionActivity.this, "Connect to internet", Toast.LENGTH_SHORT).show();
            }
        });
        myRef.child("vendors").child(CommonMethods.sharedPrefrences(this).getString("key_id", "biller")).child("date_of_subsscription").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    textViewStartDate.setText("Subscription start date : " + dataSnapshot.getValue().toString());
                }
                progressBar.setVisibility(View.GONE);
                extendButton.setEnabled(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ExtendSubscriptionActivity.this, "Connect to internet", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.slide_enter, R.transition.slide_exit);
    }

    public void extendSubscription(View view) {
        if(CommonMethods.isNetworkAvailable(ExtendSubscriptionActivity.this)){
            CommonMethods.vibrator(ExtendSubscriptionActivity.this);
            try {
                if(sdf.parse(end_date).before(sdf.parse(CommonMethods.getCurrentDate()))){
                    myRef.child("vendors").child(CommonMethods.sharedPrefrences(this).getString("key_id", "biller")).child("date_of_subsscription")
                            .setValue(CommonMethods.getCurrentDate());
                    myRef.child("vendors").child(CommonMethods.sharedPrefrences(this).getString("key_id", "biller")).child("end_date_of_subsscription")
                            .setValue(CommonMethods.addMonthsToCurrentDate(1), new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        Toast.makeText(ExtendSubscriptionActivity.this, "Successfully extended your subscription!!!!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ExtendSubscriptionActivity.this, "Failed to extend your subscription!!!!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(this, "Your subscription is still running!!!", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "Check your network connection!!!", Toast.LENGTH_SHORT).show();
        }


    }
}
