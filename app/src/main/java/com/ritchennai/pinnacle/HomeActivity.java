package com.ritchennai.pinnacle;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class HomeActivity extends AppCompatActivity {
    private BroadcastReceiver mNetworkReceiver;
    static Fragment attendancefragment = new AttendanceFragment();
    static Fragment profilefragment = new ProfileFragment();
    static Fragment parameterfragment = new ParameterFragment();
    static Fragment informationfragment = new InformationFragment();
    static Fragment leaderboardfragment = new LeaderboardFragment();
    static Fragment hr_requestfragment = new HrRequestFragment();
    static Fragment hr_statusfragment = new HrStatusFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    static Fragment active = parameterfragment;
    ImageView homerit;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mNetworkReceiver = new NetworkChangeReceiver();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

      //  fragmentManager.beginTransaction().add(R.id.frameLayout, hr_requestfragment, "6").hide(hr_requestfragment).commit();
        //fragmentManager.beginTransaction().add(R.id.frameLayout, attendancefragment, "5").hide(attendancefragment).commit();
       // fragmentManager.beginTransaction().add(R.id.frameLayout, leaderboardfragment, "4").hide(leaderboardfragment).commit();
        //fragmentManager.beginTransaction().add(R.id.frameLayout, profilefragment, "3").hide(profilefragment).commit();
       // fragmentManager.beginTransaction().add(R.id.frameLayout, informationfragment, "2").hide(informationfragment).commit();
        fragmentManager.beginTransaction().add(R.id.frameLayout, parameterfragment, "1").commit();

        homerit = findViewById(R.id.rithome);

        homerit.setOnClickListener(v -> fragmentManager.beginTransaction()
                .replace(R.id.frameLayout, parameterfragment)
                .commit());

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkReceiver);
    }

    public static void changeToLeaderboardFragment(String parameter) {
        FragmentManager fragmentManager1 = parameterfragment.getParentFragmentManager();

        Bundle bundle = new Bundle();
        bundle.putString("parameter", parameter);

        //Passing bundle as argument while making a call to pass data between fragments.
        fragmentManager1.beginTransaction()
                .replace(R.id.frameLayout, LeaderboardFragment.class, bundle)
                .addToBackStack("1").commit();

        active = leaderboardfragment;
    }

    public static void changeToProfileFragment() {
        FragmentManager fragmentManager1 = parameterfragment.getParentFragmentManager();

        //Passing bundle as argument while making a call to pass data between fragments.
        fragmentManager1.beginTransaction()
                .replace(R.id.frameLayout, ProfileFragment.class, null)
                .addToBackStack("1").commit();

        active = profilefragment;
    }

    public static void changeToHrStatusFragment() {
        FragmentManager fragmentManager1 = parameterfragment.getParentFragmentManager();

        //Passing bundle as argument while making a call to pass data between fragments.
        fragmentManager1.beginTransaction()
                .replace(R.id.frameLayout, HrStatusFragment.class, null)
                .addToBackStack("1").commit();

        active = hr_statusfragment;
    }

    public static void changeToInfoFragment() {
        FragmentManager fragmentManager1 = parameterfragment.getParentFragmentManager();

        //Passing bundle as argument while making a call to pass data between fragments.
        fragmentManager1.beginTransaction()
                .replace(R.id.frameLayout, InformationFragment.class, null)
                .addToBackStack("1").commit();

        active = informationfragment;
    }

    public static void changeToAttendanceFragment() {
        FragmentManager fragmentManager1 = parameterfragment.getParentFragmentManager();

        //Passing bundle as argument while making a call to pass data between fragments.
        fragmentManager1.beginTransaction()
                .replace(R.id.frameLayout, AttendanceFragment.class, null)
                .addToBackStack("1").commit();

        active = attendancefragment;
    }

    public static void changeToHRrequestFragment() {
        FragmentManager fragmentManager1 = parameterfragment.getParentFragmentManager();

        //Passing bundle as argument while making a call to pass data between fragments.
        fragmentManager1.beginTransaction()
                .replace(R.id.frameLayout, HrRequestFragment.class, null)
                .addToBackStack("1").commit();

        active = hr_requestfragment;
    }

}