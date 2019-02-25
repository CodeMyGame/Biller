package com.biller.biller.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.biller.biller.R;
import com.biller.biller.adapter.ViewPagerAdapter;
import com.biller.biller.fragmentMyAccount.BalanceFragment;
import com.biller.biller.fragmentMyAccount.CollectionFragment;
import com.biller.biller.fragmentMyAccount.ExpendatureFragment;

public class MyAccountActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.slide_enter, R.transition.slide_exit);
        finish();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CollectionFragment(), "Collection");
        adapter.addFragment(new ExpendatureFragment(), "Expenditure");
        adapter.addFragment(new BalanceFragment(), "Balance");
        viewPager.setAdapter(adapter);
    }

}
