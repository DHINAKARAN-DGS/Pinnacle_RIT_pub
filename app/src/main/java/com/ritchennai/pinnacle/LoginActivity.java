package com.ritchennai.pinnacle;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button signup, login;
    EditText memail, mpassword;
    FirebaseAuth fAuth;
    FirebaseUser user;
    ProgressBar loginprogress;
    boolean isEmailValid;
    String emailPattern = "[a-z.]*+[a-z.]*+[0-9.]*+[a-z]+@ritchennai.edu.in$",
            emailPatternTeachers = "[a-z.]*+[a-z.]*+@ritchennai.edu.in$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signup = findViewById(R.id.signup_Button);
        login = findViewById(R.id.login_Button);
        memail = findViewById(R.id.email_EditText);
        loginprogress = findViewById(R.id.login_progress);
        mpassword = findViewById(R.id.password_EditText);

        fAuth = FirebaseAuth.getInstance();


        signup.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });

        login.setOnClickListener(v -> {
            final String email = memail.getText().toString().trim();
            final String password = mpassword.getText().toString().trim();

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);


            SetValidation();
            if (TextUtils.isEmpty(password)) {
                mpassword.setError("Password is Required.");
                return;
            }
            if (password.length() < 6) {
                mpassword.setError("Password must be greater than 6 characters.");
                return;
            }


            showLoading();

            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                user = fAuth.getCurrentUser();

                if (task.isSuccessful()) {
                    if (user != null && user.isEmailVerified()) {
                        Toast.makeText(LoginActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(LoginActivity.this, "" + "Email Not Verified", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, VerifyUser.class));
                        dismissLoading();
                    }
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Error!" + task.getException(), Toast.LENGTH_SHORT).show();
                    dismissLoading();
                }
            });

        });

    }

    public void SetValidation() {
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

    }

    public void forgot(View view) {
        final EditText resetmail1 = new EditText(view.getContext());
        final AlertDialog.Builder passworddialog = new AlertDialog.Builder(view.getContext());
        passworddialog.setTitle("Reset Password");
        passworddialog.setMessage("Enter your Email ID to Receive Reset Link :-");
        passworddialog.setView(resetmail1);

        passworddialog.setPositiveButton("SEND", (dialog, which) -> {
            //get email and send link
            if (resetmail1.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Enter Your College Mail ID", Toast.LENGTH_SHORT).show();
            } else {
                String mail = resetmail1.getText().toString();
                fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(aVoid -> {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Reset link as been sent", Snackbar.LENGTH_LONG);
                    snackbar.show();


                }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Error!!" + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
        passworddialog.setNegativeButton("CANCEL", (dialog, which) -> {

        });
        passworddialog.show();
    }
    void showLoading() {
        loginprogress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    void dismissLoading() {
        loginprogress.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


}
