package com.ritchennai.pinnacle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {

    FirebaseFirestore fStore;
    FirebaseUser user;
    FirebaseAuth fauth;
    String Userid, dept, staffid;
    Button logoutBtn;
    TextView tName, pointsView, tStaffId;
    FloatingActionButton Fab;
    ImageView profile;
    StorageReference storageReference;
    ProgressBar logoutprogress;
    View codingClProgress, codingDlProgress, codingMeProgress,
            certificationClProgress, certificationDlProgress, certificationMeProgress,
            conferenceClProgress, conferenceDlProgress, conferenceMeProgress,
            fundingClProgress, fundingDlProgress, fundingMeProgress,
            consultancyClProgress, consultancyDlProgress, consultancyMeProgress,
            overallClProgress, overallDlProgress, overallMeProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), callback);

        tName = view.findViewById(R.id.username);
        tStaffId = view.findViewById(R.id.staffId_TextView);
        Fab = view.findViewById(R.id.floatingActionButton);
        profile = view.findViewById(R.id.profile_Image);
        logoutprogress = view.findViewById(R.id.logout_progress);
        logoutBtn = view.findViewById(R.id.logout_Button);
        pointsView = view.findViewById(R.id.point_EditTextView);

        overallClProgress = view.findViewById(R.id.overall_cl_ProgressBar);
        overallDlProgress = view.findViewById(R.id.overall_dl_ProgressBar);
        overallMeProgress = view.findViewById(R.id.overall_me_ProgressBar);

        codingClProgress = view.findViewById(R.id.coding_cl_ProgressBar);
        codingDlProgress = view.findViewById(R.id.coding_dl_ProgressBar);
        codingMeProgress = view.findViewById(R.id.coding_me_ProgressBar);

        certificationClProgress = view.findViewById(R.id.certification_cl_ProgressBar);
        certificationDlProgress = view.findViewById(R.id.certification_dl_ProgressBar);
        certificationMeProgress = view.findViewById(R.id.certification_me_ProgressBar);

        conferenceClProgress = view.findViewById(R.id.conference_cl_ProgressBar);
        conferenceDlProgress = view.findViewById(R.id.conference_dl_ProgressBar);
        conferenceMeProgress = view.findViewById(R.id.conference_me_ProgressBar);

        fundingClProgress = view.findViewById(R.id.funding_cl_ProgressBar);
        fundingDlProgress = view.findViewById(R.id.funding_dl_ProgressBar);
        fundingMeProgress = view.findViewById(R.id.funding_me_ProgressBar);

        consultancyClProgress = view.findViewById(R.id.consultancy_cl_ProgressBar);
        consultancyDlProgress = view.findViewById(R.id.consultancy_dl_ProgressBar);
        consultancyMeProgress = view.findViewById(R.id.consultancy_me_ProgressBar);

        fStore = FirebaseFirestore.getInstance();
        fauth = FirebaseAuth.getInstance();
        user = fauth.getCurrentUser();

        assert user != null;
        Userid = user.getUid();

        storageReference = FirebaseStorage.getInstance().getReference();

        logoutBtn.setOnClickListener(v -> {
            fauth.signOut();
            showLoading();
            startActivity(new Intent(view.getContext(), MainActivity.class));
            requireActivity().finish();
            Toast.makeText(view.getContext(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();
        });

        Fab.setOnClickListener(v -> {
            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(openGalleryIntent, 1000);
        });

        final DocumentReference documentReference = fStore.collection("users").document(Userid);
        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                tName.setText(documentSnapshot.getString("Name"));
                staffid = documentSnapshot.getString("StaffID");
                assert staffid != null;
                StorageReference profileRef = storageReference.child(staffid);
                profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(profile));
                tStaffId.setText(staffid);
                dept = documentSnapshot.getString("Department");
                readData(staffid);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Uri imageUri = data.getData();
                //profile.setImageURI(imageUri);
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        showLoading();
        StorageReference fileRef = storageReference.child(staffid);
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(requireContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Picasso.get().load(uri).into(profile);
                dismissLoading();
            });
        }).addOnFailureListener(e -> {
            System.out.println("failed");
            dismissLoading();
            Toast.makeText(requireContext(), "Server error!! Try to upload image less than 500kb", Toast.LENGTH_SHORT).show();
        });
    }

    private void readData(String id) {
        showLoading();

        String url = "https://script.google.com/macros/s/AKfycbzBbE831-5b9jO6KuCvfKAe4xIysIQTm_AIFAV9NDLOk54Db1avMOtaCsiF_qERsos/exec?action=getProfile&userid=";
        url = url + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> parseItems(response),

                error -> Toast.makeText(getActivity(), "Error 404 Data Not Found", Toast.LENGTH_SHORT).show()
        );

        int socketTimeOut = 100000;
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

            int overall_cl, overall_me, codmen_cl, codmen_me,
                    certify_cl, certify_me, conf_cl, conf_me,
                    fund_cl, fund_me, consult_cl, consult_me;

            JSONObject jo = jarray.getJSONObject(2);
            if (dept.equals("H&S"))
                codmen_cl = Integer.parseInt(jo.getString("5"));
            else
                codmen_cl = Integer.parseInt(jo.getString("4"));
            certify_cl = Integer.parseInt(jo.getString("6"));
            conf_cl = Integer.parseInt(jo.getString("7"));
            fund_cl = Integer.parseInt(jo.getString("8"));
            consult_cl = Integer.parseInt(jo.getString("9"));
            overall_cl = Integer.parseInt(jo.getString("10"));

            ViewGroup.LayoutParams layoutParamsOverall = overallMeProgress.getLayoutParams();
            ViewGroup.LayoutParams layoutParamsCoding = codingMeProgress.getLayoutParams();
            ViewGroup.LayoutParams layoutParamsCertification = certificationMeProgress.getLayoutParams();
            ViewGroup.LayoutParams layoutParamsConference = conferenceMeProgress.getLayoutParams();
            ViewGroup.LayoutParams layoutParamsFunding = fundingMeProgress.getLayoutParams();
            ViewGroup.LayoutParams layoutParamsConsultancy = consultancyMeProgress.getLayoutParams();

            jo = jarray.getJSONObject(0);
            if (dept.equals("H&S"))
                codmen_me = Integer.parseInt(jo.getString("5"));
            else
                codmen_me = Integer.parseInt(jo.getString("4"));
            certify_me = Integer.parseInt(jo.getString("6"));
            conf_me = Integer.parseInt(jo.getString("7"));
            fund_me = Integer.parseInt(jo.getString("8"));
            consult_me = Integer.parseInt(jo.getString("9"));
            overall_me = Integer.parseInt(jo.getString("10"));

            if (overall_cl == 0)
                overall_cl = 250;
            if (overall_me == 0)
                layoutParamsOverall.width = 1;
            else
                layoutParamsOverall.width = overall_me * overallClProgress.getWidth() / overall_cl;
            overallMeProgress.setLayoutParams(layoutParamsOverall);

            if (codmen_cl == 0)
                codmen_cl = 50;
            if (codmen_me == 0)
                layoutParamsCoding.width = 1;
            else
                layoutParamsCoding.width = codmen_me * codingClProgress.getWidth() / codmen_cl;
            codingMeProgress.setLayoutParams(layoutParamsCoding);

            if (certify_cl == 0)
                certify_cl = 50;
            if (certify_me == 0)
                layoutParamsCertification.width = 1;
            else
                layoutParamsCertification.width = certify_me * certificationClProgress.getWidth() / certify_cl;
            certificationMeProgress.setLayoutParams(layoutParamsCertification);

            if (conf_cl == 0)
                conf_cl = 50;
            if (conf_me == 0)
                layoutParamsConference.width = 1;
            else
                layoutParamsConference.width = conf_me * conferenceClProgress.getWidth() / conf_cl;
            conferenceMeProgress.setLayoutParams(layoutParamsConference);

            if (fund_cl == 0)
                fund_cl = 50;
            if (fund_me == 0)
                layoutParamsFunding.width = 1;
            else
                layoutParamsFunding.width = fund_me * fundingClProgress.getWidth() / fund_cl;
            fundingMeProgress.setLayoutParams(layoutParamsFunding);

            if (consult_cl == 0)
                consult_cl = 50;
            if (consult_me == 0)
                layoutParamsConsultancy.width = 1;
            else
                layoutParamsConsultancy.width = consult_me * consultancyClProgress.getWidth() / consult_cl;
            consultancyMeProgress.setLayoutParams(layoutParamsConsultancy);

            pointsView.setText(String.format("%.2f/50", overall_me / 5.00));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        dismissLoading();
    }

    void showLoading() {
        logoutprogress.setVisibility(View.VISIBLE);
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    void dismissLoading() {
        logoutprogress.setVisibility(View.GONE);
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}