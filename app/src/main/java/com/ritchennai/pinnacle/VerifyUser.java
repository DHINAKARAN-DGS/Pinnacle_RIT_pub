package com.ritchennai.pinnacle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class VerifyUser extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore fStore;
    String Userid, staffid;
    Task<Void> usertask;
    Button verifyButton, resendMailButton;
    ImageView backButton;
    boolean email_verified_status = false;
    ProgressBar verifyprogress;
    String userid, department, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_user);

        verifyprogress = findViewById(R.id.verify_progress);
        verifyButton = findViewById(R.id.verify_button);
        resendMailButton = findViewById(R.id.resendMail_Button);
        backButton = findViewById(R.id.back_button);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        Userid = user.getUid();


        verifyButton.setOnClickListener(v -> {
            showLoading();

            if (user != null) {
                final DocumentReference documentReference = fStore.collection("users").document(Userid);
                documentReference.addSnapshotListener((documentSnapshot, e) -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        userid = documentSnapshot.getString("Id");
                        staffid = documentSnapshot.getString("StaffID");
                        name = documentSnapshot.getString("Name");
                        department = documentSnapshot.getString("Department");

                    }
                });

                usertask = user.reload();
                usertask.addOnSuccessListener(unused -> {

                    email_verified_status = user.isEmailVerified();

                    if (email_verified_status) {
                        addItemToSheet();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        dismissLoading();
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "Check your inbox, Email Not Verified", Toast.LENGTH_SHORT).show();
                        dismissLoading();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Account doesn't exist, Sign up try again!", Toast.LENGTH_SHORT).show();
                dismissLoading();
            }
        });
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(VerifyUser.this, MainActivity.class));
            finish();
        });

        resendMailButton.setOnClickListener(v -> {
            showLoading();
            user.sendEmailVerification();
            Toast.makeText(this, "Verification email has been sent!", Toast.LENGTH_SHORT).show();
            dismissLoading();
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Are you sure you want close this app?");
        builder.setPositiveButton("Exit", (dialog, id) -> VerifyUser.super.onBackPressed());
        builder.setNegativeButton("Cancel", (dialog, id) -> {

        });
        builder.show();
    }


    private void addItemToSheet() {

        String url = "https://script.google.com/macros/s/AKfycbwqpRgb9T8WItqb89U2rATO8keAprO93J7Z5D0cq2Gi-b_m7zNCeCxnNMJXtnHIJjU/exec";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action", "addItem");
                parmas.put("userId", staffid);
                parmas.put("staffname", name);
                parmas.put("dept", department);

                return parmas;
            }
        };

        int socketTimeOut = 5000;// u can change this .. here it is 5 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);

    }

    void showLoading() {
        verifyprogress.setVisibility(View.VISIBLE);
    }

    void dismissLoading() {
        verifyprogress.setVisibility(View.GONE);
    }
}