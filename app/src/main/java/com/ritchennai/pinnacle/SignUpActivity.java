package com.ritchennai.pinnacle;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button login, signup_Button;
    FirebaseAuth fauth;
    EditText mname, memail, mpassword, mstaffid;
    Spinner deptSpinner;
    String userid, department, name, staffid;
    FirebaseFirestore mFireStore;
    ProgressBar signupprogress;
    boolean isEmailValid, isChecked;
    String emailPattern = "[a-z.]*+[a-z.]*+[0-9.]*+[a-z]+@ritchennai.edu.in$",
            emailPatternTeachers = "[a-z.]*+[a-z.]*+@ritchennai.edu.in$";
    String[] list = new String[]{"", "CSE", "CCE", "AI&DS", "ECE", "MECH", "H&S"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        login = findViewById(R.id.login_Button);
        mname = findViewById(R.id.name_EditText);
        memail = findViewById(R.id.email_EditText);
        mpassword = findViewById(R.id.password_EditText);
        deptSpinner = findViewById(R.id.dept_spinner);
        signup_Button = findViewById(R.id.signup_Button);
        signupprogress = findViewById(R.id.signup_progress);
        mstaffid = findViewById(R.id.staffId_EditText);

        fauth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, list);

        deptSpinner.setOnItemSelectedListener(this);

        deptSpinner.setAdapter(adapter);

        signup_Button.setOnClickListener(view -> {
            name = mname.getText().toString();
            String email = memail.getText().toString();
            String password = mpassword.getText().toString();
            staffid = mstaffid.getText().toString();


            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);


            if (TextUtils.isEmpty(name)) {
                mname.setError("Name is required.");
                return;
            }
            if (TextUtils.isEmpty(staffid)) {
                mstaffid.setError("Staff Id is Required.");
                return;
            }
            if (memail.getText().toString().isEmpty()) {
                memail.setError("Enter email Address");
                isEmailValid = false;
            } else {
                if (memail.getText().toString().trim().matches(emailPattern) || memail.getText().toString().trim().matches(emailPatternTeachers)) {
                    isEmailValid = true;
                } else {
                    isEmailValid = false;
                    memail.setError("Enter College Mail ID");
                    Toast.makeText(getApplicationContext(), "Invalid Email Id", Toast.LENGTH_SHORT).show();
                }

            }
            if (TextUtils.isEmpty(password)) {
                mpassword.setError("Password is Required.");
                return;
            }
            if (password.length() < 6) {
                mpassword.setError("Password must be greater than 6 characters.");
                return;
            }


            if (isEmailValid && isChecked) {
                showLoading();
                fauth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = fauth.getCurrentUser();
                        if (user != null) {
                            userid = user.getUid();

                            Map<String, Object> usermap = new HashMap<>();
                            usermap.put("Id", userid);
                            usermap.put("Name", name);
                            usermap.put("Department", department);
                            usermap.put("Email", email);
                            usermap.put("StaffID", staffid);
                            DocumentReference documentReference = mFireStore.collection("users").document(userid);

                            documentReference.set(usermap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SignUpActivity.this, "User Created", Toast.LENGTH_SHORT).show();

                                    dismissLoading();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dismissLoading();
                                    Toast.makeText(SignUpActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            Toast.makeText(SignUpActivity.this, "SignUp Successful.", Toast.LENGTH_SHORT).show();
                            //send verification link
                            user = fauth.getCurrentUser();
                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SignUpActivity.this, "Verification Email has been sent successfully", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUpActivity.this, "Error!!" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                            Intent intent = new Intent(this, VerifyUser.class);
                            intent.putExtra("userid", userid);
                            intent.putExtra("staffname", name);
                            intent.putExtra("department", department);
                            intent.putExtra("StaffID", staffid);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            dismissLoading();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        dismissLoading();
                    }
                });
            } else {
                if (!isEmailValid)
                    memail.setError("Enter College Mail ID Only");
                else
                    Toast.makeText(this, "Select a department", Toast.LENGTH_SHORT).show();
            }
        });


        login.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        isChecked = true;
        department = list[position];
        if (position == 0)
            isChecked = false;
    }

    void showLoading() {
        signupprogress.setVisibility(View.VISIBLE);
    }

    void dismissLoading() {
        signupprogress.setVisibility(View.GONE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        isChecked = false;
    }

}