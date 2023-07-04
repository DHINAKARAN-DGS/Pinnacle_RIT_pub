package com.ritchennai.pinnacle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LeaderboardFragment extends Fragment {

    RecyclerView leaderboardRecyclerView;
    LeaderboardAdapter leaderboardAdapter;
    String parameter = "coding_leaderboard";
    StorageReference storageReference;
    TextView leaderboardTextView;
    ProgressBar loadingData;
    TextView firstName, firstScore, secondName, secondScore, thirdName, thirdScore, fourthName, fourthScore;
    ImageView firstPlaceImage, secondPlaceImage, thirdPlaceImage, fourthPlaceImage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            parameter = bundle.getString("parameter");
        }

        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().popBackStack();
            }
        };



        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), callback);

        leaderboardTextView = view.findViewById(R.id.heading_TextView);
        leaderboardTextView.setText(parameter.replace("_", " "));

        firstName = view.findViewById(R.id.firstplace_Name);
        secondName = view.findViewById(R.id.secondplace_Name);
        thirdName = view.findViewById(R.id.thirdplace_Name);
        fourthName = view.findViewById(R.id.fourthplace_Name);

        firstScore = view.findViewById(R.id.firstplace_Score);
        secondScore = view.findViewById(R.id.secondplace_Score);
        thirdScore = view.findViewById(R.id.thirdplace_Score);
        fourthScore = view.findViewById(R.id.fourthplace_Score);

        firstPlaceImage = view.findViewById(R.id.firstplace_Image);
        secondPlaceImage = view.findViewById(R.id.secondplace_Image);
        thirdPlaceImage = view.findViewById(R.id.thirdplace_Image);
        fourthPlaceImage = view.findViewById(R.id.fourthplace_Image);

        loadingData = view.findViewById(R.id.loading_ProgressBar);
        loadingData.setVisibility(View.GONE);
        leaderboardRecyclerView = view.findViewById(R.id.leaderboard_RecyclerView);

        readData(parameter);

        return view;
    }

    private void readData(String leaderboard) {
       showLoading();

        String url = "https://script.google.com/macros/s/AKfycbwgMAIp3Om3u2a25FcFPw-nLeVA6eW-Z4WVl2UNVOYKx_KiZvFttZJeXTvq3glDxcg/exec?action=getItems&Sheet=";
        url = url + leaderboard;
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

        User.sno.clear();
        User.userId.clear();
        User.staffName.clear();
        User.documentLink.clear();
        User.status.clear();
        User.score.clear();

        try {
            JSONObject jobj = new JSONObject(response);
            JSONArray jarray = jobj.getJSONArray("items");
            int temp = 1;

            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String user_id = jo.getString("0");
                String staff_name = jo.getString("1");
                String score;
                String document_link = "link";
                String status = "APPROVED";
                storageReference = FirebaseStorage.getInstance().getReference();
                if (parameter.equals("overall_leaderboard")) {
                    score = jo.getString("9");
                } else {
                    document_link = jo.getString("3");
                    status = jo.getString("4");
                    score = jo.getString("5");
                }

                if (status.equals("APPROVED") && temp == 1 && !score.equals("0")) {
                    firstName.setText(String.format("%d. %s", temp, staff_name));
                    firstScore.setText(String.format("%s points", score));
                    StorageReference profileRef = storageReference.child(user_id);
                    profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(firstPlaceImage));
                    temp += 1;
                } else if (status.equals("APPROVED") && temp == 2 && !score.equals("0")) {
                    secondName.setText(String.format("%d. %s", temp, staff_name));
                    secondScore.setText(String.format("%s points", score));
                    StorageReference profileRef = storageReference.child(user_id);
                    profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(secondPlaceImage));
                    temp += 1;
                } else if (status.equals("APPROVED") && temp == 3 && !score.equals("0")) {
                    thirdName.setText(String.format("%d. %s", temp, staff_name));
                    thirdScore.setText(String.format("%s points", score));
                    StorageReference profileRef = storageReference.child(user_id);
                    profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(thirdPlaceImage));
                    temp += 1;
                } else if (status.equals("APPROVED") && temp == 4 && !score.equals("0")) {
                    fourthName.setText(String.format("%d. %s", temp, staff_name));
                    fourthScore.setText(String.format("%s points", score));
                    StorageReference profileRef = storageReference.child(user_id);
                    profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(fourthPlaceImage));
                    temp += 1;
                } else if (status.equals("APPROVED") && !score.equals("0")) {
                    User.sno.add(String.valueOf(temp));
                    temp += 1;
                    User.userId.add(user_id);
                    User.staffName.add(staff_name);
                    User.documentLink.add(document_link);
                    User.status.add(status);
                    User.score.add(score);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        leaderboardRecyclerView.setLayoutManager(linearLayoutManager);

        leaderboardAdapter = new LeaderboardAdapter(getContext(), User.sno, User.staffName, User.score, User.userId, User.documentLink);
        leaderboardRecyclerView.setAdapter(leaderboardAdapter);

        dismissLoading();
    }

    void showLoading() {
        loadingData.setVisibility(View.VISIBLE);
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    void dismissLoading() {
        loadingData.setVisibility(View.GONE);
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}