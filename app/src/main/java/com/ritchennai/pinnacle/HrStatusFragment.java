package com.ritchennai.pinnacle;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HrStatusFragment extends Fragment {


    public HrStatusFragment() {
        // Required empty public constructor
    }

    RecyclerView hr_status_RV;
    Spinner hr_status_spinner;
    String staffid;
    FirebaseFirestore fStore;
    ProgressBar progressBar;

    FirebaseUser user;
    FirebaseAuth fauth;
    TextView textView;

    String[] applicationType = {"Leave", "Permission", "OD"};
    String type;

    List<HRStatusModel> hrStatusModelList = new ArrayList<>();
    HRStatusAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        View view = inflater.inflate(R.layout.fragment_hr_status, container, false);
        hr_status_RV = view.findViewById(R.id.hr_status_RV);
        textView = view.findViewById(R.id.textView3);
        hr_status_spinner = view.findViewById(R.id.spinner_hr_status);
        fStore = FirebaseFirestore.getInstance();
        fauth = FirebaseAuth.getInstance();
        user = fauth.getCurrentUser();
        progressBar = view.findViewById(R.id.progressBarHrStatus);
        DocumentReference documentReference = fStore.collection("users").document(user.getUid());
        documentReference.addSnapshotListener((value, error) -> {
            if (value != null) {
                staffid = value.getString("StaffID");
                System.out.println(staffid);

            }
        });

        showLoading();

       /* hrStatusModelList.add(new HRStatusModel("1","KISHORE","OD","12-1-2022","13-1-2022","APPROVED"));
        hrStatusModelList.add(new HRStatusModel("1","KISHORE","OD","12-1-2022","13-1-2022","APPROVED"));
        hrStatusModelList.add(new HRStatusModel("1","KISHORE","OD","12-1-2022","13-1-2022","APPROVED"));
        hrStatusModelList.add(new HRStatusModel("1","KISHORE","OD","12-1-2022","13-1-2022","APPROVED"));
        hrStatusModelList.add(new HRStatusModel("1","KISHORE","OD","12-1-2022","13-1-2022","APPROVED"));
        hrStatusModelList.add(new HRStatusModel("1","KISHORE","OD","12-1-2022","13-1-2022","APPROVED"));
        */
        //getHrstatus("CS70","Leave");
        adapter = new HRStatusAdapter(hrStatusModelList);
        adapter.notifyDataSetChanged();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        hr_status_RV.setLayoutManager(linearLayoutManager);
        hr_status_RV.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, applicationType);
        hr_status_spinner.setAdapter(adapter1);
        hr_status_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = applicationType[position];
                hrStatusModelList.clear();
                textView.setVisibility(View.GONE);
                getHrstatus(staffid, type);
                showLoading();
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                type = applicationType[0];
            }
        });


        return view;
    }

    public void getHrstatus(String staffid, String type) {

        String url = "https://script.google.com/macros/s/AKfycbzRB7z_kIyxlu0zpVcidBjsMQZuQgE7uEZWFl7sgeUfBt6Cb23QGxoCt4762XnGNj4/exec?action=hrstatus&userId=" + staffid + "&type=" + type;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                this::parseItems1,

                error -> {
                    Toast.makeText(HrStatusFragment.this.getContext(), "Error 404 Data Not Found", Toast.LENGTH_SHORT).show();
                    dismissLoading();
                }

        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        queue.add(stringRequest);


    }

    @SuppressLint({"DefaultLocale", "NotifyDataSetChanged", "SetTextI18n"})
    private void parseItems1(String response) {


        try {


            JSONObject jobj = new JSONObject(response);
            JSONArray jarray = jobj.getJSONArray("items");
            System.out.println(jobj);
            if (jarray.isNull(0)) {
                adapter.notifyDataSetChanged();
                textView.setVisibility(View.VISIBLE);
                dismissLoading();
            }


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);
                String Uid = jo.getString("UID");
                String type = jo.getString("Type");
                if (type.equals("Leave")) {
                    if (jo.getString("Half Day Leave").equals("yes")) {
                        type = type + "(Half day)";
                    }
                }
                String staffname = jo.getString("Staff Name");
                String fromdate;
                String todate;
                if (type.equals("Permission")) {
                    todate = jo.getString("Start Time");
                    fromdate = jo.getString("Date");
                } else {
                    todate = jo.getString("To Date");
                    fromdate = jo.getString("From Date");
                }
                String status = jo.getString("Status");
                hrStatusModelList.add(new HRStatusModel(Uid, staffname.toUpperCase(), type, fromdate, todate, status));
                adapter.notifyDataSetChanged();


            }
            dismissLoading();

        } catch (JSONException e) {
            e.printStackTrace();
            dismissLoading();
        }
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