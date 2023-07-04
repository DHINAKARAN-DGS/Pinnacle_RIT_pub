package com.ritchennai.pinnacle;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AttendanceFragment extends Fragment {

    FirebaseFirestore fStore;
    FirebaseUser user;
    FirebaseAuth fauth;
    String Userid, staffid;
    ProgressBar loading;
    TextView clCount, absentCount, odCount, lopCount, permissionCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), callback);

        loading = view.findViewById(R.id.attendance_ProgressBar);

        clCount = view.findViewById(R.id.cl_count);
        absentCount = view.findViewById(R.id.absent_count);
        odCount = view.findViewById(R.id.od_count);
        lopCount = view.findViewById(R.id.lop_count);
        permissionCount = view.findViewById(R.id.permissions_count);

        fStore = FirebaseFirestore.getInstance();
        fauth = FirebaseAuth.getInstance();
        user = fauth.getCurrentUser();

        assert user != null;
        Userid = user.getUid();

        final DocumentReference documentReference = fStore.collection("users").document(Userid);
        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                staffid = documentSnapshot.getString("StaffID");
                assert staffid != null;
                readData(staffid);
            }
        });

        return view;
    }

    private void readData(String id) {
        showLoading();

        //String url = "https://script.google.com/macros/s/AKfycbzp8dJlqo0q0_cqXpDFrhrueKDARUQRpm5EVL2fitxrwmQL8pa1pLrc5Ohm1_V3N4I/exec?action=getAttendance&userid=";
        String url = "https://script.google.com/macros/s/AKfycbytcxp5wkQIELrgzJUfHdV0H4QBqdizu25fIqwpw8roSUFd3FzBw5QIsVbgDK-yEyK9Ig/exec?action=getAttendance&userid=";
        url = url + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> parseItems(response),

                error -> Toast.makeText(getActivity(), "Error 404 Data Not Found", Toast.LENGTH_SHORT).show()
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(requireActivity());
        queue.add(stringRequest);
    }

    @SuppressLint("DefaultLocale")
    private void parseItems(String response) {

        try {
            JSONObject jobj = new JSONObject(response);
            JSONArray jarray = jobj.getJSONArray("items");

            clCount.setText(jarray.getString(0));
            absentCount.setText(jarray.getString(1));
            odCount.setText(jarray.getString(2));
            lopCount.setText(jarray.getString(3));
            permissionCount.setText(jarray.getString(4));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        dismissLoading();
    }
    void showLoading() {
        loading.setVisibility(View.VISIBLE);
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }
    void dismissLoading() {
        loading.setVisibility(View.GONE);
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}