package com.biller.biller.common;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Kapil Gehlot on 10/8/2017.
 */

public class CommonMethods {
    public static SimpleDateFormat dateFormatter;
    public static Calendar newCalendar;
    public static DatePickerDialog datePickerDialog;
    public static SharedPreferences pref;
    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = "" + mdformat.format(calendar.getTime());
        return strDate;
    }
    public static Typeface getFont(Context c){
        Typeface custom_font = Typeface.createFromAsset(c.getAssets(), "biller.ttf");
        return custom_font;
    }
    public static DatePickerDialog datePicker(Context context, final EditText editText){
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                editText.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        return datePickerDialog;
    }
    public static SharedPreferences sharedPrefrences(Context c){
        pref = c.getSharedPreferences("MyPref", 0); // 0 - for private mode
        return pref;
    }
    public static String addMonthsToCurrentDate(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, month);
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = "" + mdformat.format(calendar.getTime());
        return strDate;
    }
    public static boolean isNetworkAvailable(Context c) {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager)
                c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
    public static Vibrator vibrator(Context c){
        Vibrator v = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(50);
        return v;
    }

}
