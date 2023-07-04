package com.ritchennai.pinnacle;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ParameterFragment extends Fragment {

    LinearLayout l1, l2, l3, l4, l5, l6, l7, l8, l9, l10,l11;

    StorageReference storageReference;

    ImageView profile;
    String staffid;
    FirebaseFirestore fStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parameter, container, false);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                builder.setTitle("Do you want to close this app?");
                builder.setPositiveButton("Yes", (dialog, id) -> requireActivity().finish());
                builder.setNegativeButton("No", (dialog, id) -> {

                });
                builder.show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), callback);

        l1 = view.findViewById(R.id.l1);
        l2 = view.findViewById(R.id.l2);
        l3 = view.findViewById(R.id.l3);
        l4 = view.findViewById(R.id.l4);
        l5 = view.findViewById(R.id.l5);
        l6 = view.findViewById(R.id.l6);
        l7 = view.findViewById(R.id.l7);
        l8 = view.findViewById(R.id.l8);
        l9 = view.findViewById(R.id.l9);
        l10 = view.findViewById(R.id.l10);
        l11 = view.findViewById(R.id.l11);

        profile = view.findViewById(R.id.profileImage);

        storageReference = FirebaseStorage.getInstance().getReference();

        fStore = FirebaseFirestore.getInstance();
        final DocumentReference documentReference = fStore.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                staffid = documentSnapshot.getString("StaffID");
                assert staffid != null;
                StorageReference profileRef = storageReference.child(staffid);
                profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(profile));
            }
        });

        profile.setOnClickListener(v -> HomeActivity.changeToProfileFragment());

        l1.setOnClickListener(v -> HomeActivity.changeToLeaderboardFragment("overall_leaderboard"));

        l2.setOnClickListener(v -> HomeActivity.changeToAttendanceFragment());

        l3.setOnClickListener(v -> HomeActivity.changeToHRrequestFragment());

        l4.setOnClickListener(v -> HomeActivity.changeToLeaderboardFragment("coding_leaderboard"));

        l5.setOnClickListener(v -> HomeActivity.changeToLeaderboardFragment("mentorship_leaderboard"));

        l6.setOnClickListener(v -> HomeActivity.changeToLeaderboardFragment("certification_leaderboard"));

        l7.setOnClickListener(v -> HomeActivity.changeToLeaderboardFragment("conference_leaderboard"));

        l8.setOnClickListener(v -> HomeActivity.changeToLeaderboardFragment("funding_trial_blazers"));

        l9.setOnClickListener(v -> HomeActivity.changeToLeaderboardFragment("consultancy_victors"));

        l10.setOnClickListener(v -> HomeActivity.changeToInfoFragment());

        l11.setOnClickListener(v -> HomeActivity.changeToHrStatusFragment());

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}