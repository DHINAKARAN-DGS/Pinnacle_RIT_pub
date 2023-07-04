package com.ritchennai.pinnacle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        int SPLASH_DISPLAY_LENGTH = 2000;
        new Handler().postDelayed(() -> {
            /* Create an Intent that will start the MainActivity. */
            if (user != null) {
                if (user.isEmailVerified()) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                } else {
                    Intent mainIntent = new Intent(SplashScreen.this, VerifyUser.class);
                    startActivity(mainIntent);
                }
            } else {
                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(mainIntent);
            }
            finish();
        }, SPLASH_DISPLAY_LENGTH);
    }
}