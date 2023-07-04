package com.ritchennai.pinnacle;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HrRequestFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    FirebaseFirestore fStore;
    FirebaseUser user;
    FirebaseAuth fauth;
    String Userid, staffid, dept, name, designation, application;

    @SuppressLint("UseSwitchCompatOrMaterialCode")

    Switch halfDay_Leave;

    Button permissionDatePicker, permissionTimePicker,
            leaveFromDatePicker, leaveToDatePicker,
            odFromDatePicker, odToDatePicker,
            submitButton;

    Spinner typeSpinner, designationSpinner;

    String[] applicationType = {"Leave", "Permission", "OD"},
            designationType = {"Professor", "Assistant Professor", "Admin Staff", "HOD","Associate Professor","Lab Assistant"};
    CardView leaveCard, permissionCard, odCard;
    TextView staffNameTextView, staffIdTextView, deptTextView,
            leaveFromTextView, leaveToTextView;
    EditText reason;
    ImageView leaveToCalendar;
    String Date,Starttime;
    String Type = "Leave";
    String Halfday = "False";
    ProgressBar progressBar;
    //int permYear, permMonth, permDay;

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hr_request, container, false);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), callback);

        staffNameTextView = view.findViewById(R.id.name_Text);
        staffIdTextView = view.findViewById(R.id.staffid_Text);
        deptTextView = view.findViewById(R.id.dept_Text);
        fStore = FirebaseFirestore.getInstance();
        fauth = FirebaseAuth.getInstance();
        user = fauth.getCurrentUser();


        assert user != null;
        Userid = user.getUid();

        final DocumentReference documentReference = fStore.collection("users").document(Userid);
        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                name = documentSnapshot.getString("Name");
                staffid = documentSnapshot.getString("StaffID");
                dept = documentSnapshot.getString("Department");
                staffNameTextView.setText(name);
                staffIdTextView.setText(staffid);
                deptTextView.setText(dept);
            }
        });

        permissionDatePicker = view.findViewById(R.id.permission_Date);
        permissionTimePicker = view.findViewById(R.id.permission_Time);

        halfDay_Leave = view.findViewById(R.id.halfday_switch);
        leaveFromDatePicker = view.findViewById(R.id.leave_from_Date);
        leaveToDatePicker = view.findViewById(R.id.leave_to_Date);
        leaveFromTextView = view.findViewById(R.id.leave_fromdate_TextView);
        leaveToTextView = view.findViewById(R.id.leave_to_date_TextView);
        leaveToCalendar = view.findViewById(R.id.imageView2);

        odFromDatePicker = view.findViewById(R.id.od_from_Date);
        odToDatePicker = view.findViewById(R.id.od_to_Date);

        typeSpinner = view.findViewById(R.id.type_Spinner);
        designationSpinner = view.findViewById(R.id.designation_Spinner);

        leaveCard = view.findViewById(R.id.leave_card);
        permissionCard = view.findViewById(R.id.permission_card);
        odCard = view.findViewById(R.id.od_card);
        reason = view.findViewById(R.id.reason_TextMultiLine);

        submitButton = view.findViewById(R.id.submit_Button);
        progressBar = view.findViewById(R.id.progressBar2);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, applicationType);
        typeSpinner.setOnItemSelectedListener(this);
        typeSpinner.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, designationType);
        //designationSpinner.setOnItemSelectedListener(this);
        designationSpinner.setAdapter(adapter2);

        permissionDatePicker.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, year, monthOfYear, dayOfMonth) -> permissionDatePicker.setText(String.format("%02d-%02d-%d", dayOfMonth, monthOfYear + 1, year)), mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
            datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis() + (30L * 1000 * 60 * 60 * 24));
            //calendar.get(Calendar.DAY_OF_WEEK).equals(Calendar.SUNDAY)
            datePickerDialog.show();
        });

        permissionTimePicker.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (view12, hourOfDay, minute) -> permissionTimePicker.setText(hourOfDay%12 + ":" + minute + " " + ((hourOfDay>=12) ? "PM" : "AM")), mHour, mMinute, false);
            timePickerDialog.show();
        });

        halfDay_Leave.setOnClickListener(v -> {
            if (halfDay_Leave.isChecked()) {
                leaveToDatePicker.setVisibility(View.GONE);
                leaveToTextView.setVisibility(View.INVISIBLE);
                leaveToCalendar.setVisibility(View.GONE);
                leaveFromTextView.setText("Date");
                Halfday = "true";
            }
            if (!halfDay_Leave.isChecked()) {
                leaveToDatePicker.setVisibility(View.VISIBLE);
                leaveToTextView.setVisibility(View.VISIBLE);
                leaveToCalendar.setVisibility(View.VISIBLE);
                leaveFromTextView.setText("From");
                Halfday = "false";
            }
        });

        leaveFromDatePicker.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, year, monthOfYear, dayOfMonth) -> leaveFromDatePicker.setText(String.format("%02d-%02d-%d", dayOfMonth, monthOfYear + 1, year)), mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        leaveToDatePicker.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, year, monthOfYear, dayOfMonth) -> leaveToDatePicker.setText(String.format("%02d-%02d-%d", dayOfMonth, monthOfYear + 1, year)), mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        odFromDatePicker.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, year, monthOfYear, dayOfMonth) -> odFromDatePicker.setText(String.format("%02d-%02d-%d", dayOfMonth, monthOfYear + 1, year)), mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        odToDatePicker.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, year, monthOfYear, dayOfMonth) -> odToDatePicker.setText(String.format("%02d-%02d-%d", dayOfMonth, monthOfYear + 1, year)), mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        submitButton.setOnClickListener(v -> {


            designation = designationType[designationSpinner.getSelectedItemPosition()];


            if (Type.equals("Permission")) {

                if (validatepermission()) {
                    addtoSheet(staffid, name, dept, Type, designation, reason.getText().toString(), Halfday, ""+Date, Starttime);
                }

            } else if (Type.equals("Leave")) {

                if (validateleave()) {
                    addtoSheet(staffid, name, dept, Type, designation, reason.getText().toString(), Halfday, leaveFromDatePicker.getText().toString(), leaveToDatePicker.getText().toString());
                }
            } else {

                if (validateod()) {
                    addtoSheet(staffid, name, dept, Type, designation, reason.getText().toString(), Halfday, odFromDatePicker.getText().toString(), odToDatePicker.getText().toString());
                }
            }

        });

        return view;

    }

    public boolean validatepermission() {
        boolean valid = true;

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH) + 1;
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        Date = permissionDatePicker.getText().toString();
        Starttime = permissionTimePicker.getText().toString();
        Toast.makeText(getActivity(), ""+Starttime, Toast.LENGTH_SHORT).show();
        String reasonforpermission = reason.getText().toString();

        if (Date.equals("")) {
            Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (Starttime.equals("")) {
            valid = false;
        } else {
            String[] givenDate = Date.split("-");
            int givenYear = Integer.parseInt(givenDate[2]),
                    givenMonth = Integer.parseInt(givenDate[1]),
                    givenDay = Integer.parseInt(givenDate[0]);
            if (givenYear < mYear || givenMonth < mMonth || givenDay < mDay) {
                Toast.makeText(getContext(), "Select a proper date", Toast.LENGTH_SHORT).show();
                valid = false;
            }
        }

        if (TextUtils.isEmpty(reasonforpermission)) {
            reason.setError("Reason need to apply for " + Type);
            valid = false;
        }

        return valid;
    }

    public boolean validateleave() {
        boolean valid = true;


        String fromDate = leaveFromDatePicker.getText().toString();
        String toDate = leaveToDatePicker.getText().toString();
        String reasonforleave = reason.getText().toString();

        if (fromDate.equals("")) {
            Toast.makeText(getContext(), "Please select a from date", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (toDate.equals("")) {
            Toast.makeText(getContext(), "Please select a to date", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (TextUtils.isEmpty(reasonforleave)) {
            reason.setError("Reason need to apply for " + Type);
            valid = false;
        }

        return valid;
    }

    public boolean validateod() {
        boolean valid = true;


        String fromDate = odFromDatePicker.getText().toString();
        String toDate = odToDatePicker.getText().toString();
        String reasonforleave = reason.getText().toString();

        if (fromDate.equals("")) {
            Toast.makeText(getContext(), "Please select a from date", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (toDate.equals("")) {
            Toast.makeText(getContext(), "Please select a to date", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (TextUtils.isEmpty(reasonforleave)) {
            reason.setError("Reason need to apply for " + Type);
            valid = false;
        }

        return valid;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        application = applicationType[position];
        switch (application) {
            case "Leave":
                leaveCard.setVisibility(View.VISIBLE);
                permissionCard.setVisibility(View.GONE);
                odCard.setVisibility(View.GONE);
                Type = "Leave";
                reason.getText().clear();
                permissionTimePicker.setText(null);
                permissionDatePicker.setText(null);
                odFromDatePicker.setText(null);
                odToDatePicker.setText(null);
                break;
            case "Permission":
                leaveCard.setVisibility(View.GONE);
                permissionCard.setVisibility(View.VISIBLE);
                odCard.setVisibility(View.GONE);
                Type = "Permission";
                reason.getText().clear();
                leaveFromDatePicker.setText(null);
                leaveToDatePicker.setText(null);
                odFromDatePicker.setText(null);
                odToDatePicker.setText(null);
                break;
            case "OD":
                leaveCard.setVisibility(View.GONE);
                permissionCard.setVisibility(View.GONE);
                odCard.setVisibility(View.VISIBLE);
                Type = "OD";
                reason.getText().clear();
                leaveFromDatePicker.setText(null);
                leaveToDatePicker.setText(null);
                permissionDatePicker.setText(null);
                permissionTimePicker.setText(null);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void addtoSheet(String userId, String staffname, String dept, String type, String designation, String reason, String halfdayLeave, String fromDate, String toDate) {
        showLoading();

        String url = "https://script.google.com/macros/s/AKfycbzIcNYeF1Iq-8kV8Ih_0ih1gnJ6aVsC1k7Hl-BdkP11Q0J8R0eo5VzvHQnDBWsqyQ0/exec";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(getContext(), "Request sent Successfully", Toast.LENGTH_SHORT).show();
                    dismissLoading();

                },
                error -> {
                    Toast.makeText(getContext(), "Sever Error!!!Try Again", Toast.LENGTH_SHORT).show();
                    dismissLoading();

                }
        ) {
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();
                //here we pass params
                if (type.equals("Leave")) {
                    parmas.put("action", "addHRdata");
                    parmas.put("userId", userId);
                    parmas.put("type", type);
                    parmas.put("staffname", staffname);
                    parmas.put("dept", dept);
                    parmas.put("fromDate", fromDate);
                    if (halfdayLeave.equals("Yes")) {
                        parmas.put("toDate", fromDate);
                    } else {
                        parmas.put("toDate", toDate);
                    }
                    parmas.put("designation", designation);
                    parmas.put("reason", reason);
                    parmas.put("halfdayLeave", halfdayLeave);
                    return parmas;

                } else if (type.equals("OD")) {
                    parmas.put("action", "addHRdata");
                    parmas.put("userId", userId);
                    parmas.put("staffname", staffname);
                    parmas.put("type", type);
                    parmas.put("dept", dept);
                    parmas.put("fromDate", fromDate);
                    parmas.put("toDate", toDate);
                    parmas.put("designation", designation);
                    parmas.put("reason", reason);

                    return parmas;

                } else {
                    parmas.put("action", "addHRdata");
                    parmas.put("userId", userId);
                    parmas.put("staffname", staffname);
                    parmas.put("type", type);
                    parmas.put("dept", dept);
                    parmas.put("date", fromDate);
                    parmas.put("startTime", toDate);
                    parmas.put("designation", designation);
                    parmas.put("reason", reason);

                    return parmas;

                }

            }
        };
        int socketTimeOut = 50000;

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        queue.add(stringRequest);

    }

    void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    void dismissLoading() {
        progressBar.setVisibility(View.GONE);
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}