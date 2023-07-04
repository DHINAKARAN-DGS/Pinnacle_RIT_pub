package com.ritchennai.pinnacle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InformationFragment extends Fragment {

    CardView codingCard, mentorshipCard;
    TextView codingStatus, mentorshipStatus, certificationStatus, conferenceStatus, fundingStatus, consultancyStatus;
    ProgressBar screenProgress,
            codingProgress, mentorshipProgress, certificationProgress,
            conferenceProgress, fundingProgress, consultancyProgress;
    EditText codingScoreEditText, fundingScoreEditText, consultancyEditText;
    Button codingSaveButton, codingDocButton,
            mentorshipSaveButton, mentorshipDocButton,
            certificationSaveButton, certificationDocButton,
            conferenceSaveButton, conferenceDocButton,
            fundingSaveButton, fundingDocButton,
            consultancySaveButton, consultancyDocButton;
    RadioGroup mentorshipRadioGroup,
            activeOrganizerRadioGroup,
            invitedCredentialRadioGroup,
            ongoingCertificationRadioGroup;
    int coding_score, mentorship_score, certification_score,
            conference_score, funding_score, consultancy_score;
    String staffid;
    FirebaseFirestore fStore;
    String dept, codingImgUrl, fundingImgUrl, mentorImgUrl, certificateImgUrl, conferenceImgUrl, consultancyImgUrl;
    FirebaseUser user;
    FirebaseAuth fauth;

    Uri codeingImageUri = Uri.parse("");
    Uri certificationImageUri = Uri.parse("");
    Uri mentorshipImageUri = Uri.parse("");
    Uri fundingImageUri = Uri.parse("");
    Uri conferenceImageUri = Uri.parse("");
    Uri consultancyImageUri = Uri.parse("");

    ImageView codingTrash, mentorshipTrash, certificationTrash, conferenceTrash, fundingTrash, consultancyTrash;

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_information, container, false);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), callback);

        fStore = FirebaseFirestore.getInstance();
        fauth = FirebaseAuth.getInstance();
        user = fauth.getCurrentUser();

        screenProgress = view.findViewById(R.id.screen_Progress);

        codingScoreEditText = view.findViewById(R.id.codingScore_EditText);
        codingSaveButton = view.findViewById(R.id.coding_save_Button);
        codingDocButton = view.findViewById(R.id.codingAddDoc_Button);
        codingStatus = view.findViewById(R.id.codingStatus_TextView);
        codingStatus.setVisibility(View.GONE);
        codingCard = view.findViewById(R.id.coding_Card);
        codingTrash = view.findViewById(R.id.coding_Trash);
        codingTrash.setVisibility(View.GONE);
        codingProgress = view.findViewById(R.id.coding_Progress);

        mentorshipSaveButton = view.findViewById(R.id.mentorship_save_Button);
        mentorshipDocButton = view.findViewById(R.id.mentorshipAddDoc_Button);
        mentorshipRadioGroup = view.findViewById(R.id.mentorship_RadioGroup);
        mentorshipCard = view.findViewById(R.id.mentorship_Card);
        mentorshipCard.setVisibility(View.VISIBLE);
        mentorshipStatus = view.findViewById(R.id.mentorStatus_TextView);
        mentorshipStatus.setVisibility(View.GONE);
        mentorshipTrash = view.findViewById(R.id.mentorship_Trash);
        mentorshipTrash.setVisibility(View.GONE);
        mentorshipProgress = view.findViewById(R.id.mentorship_Progress);

        certificationSaveButton = view.findViewById(R.id.certification_save_Button);
        certificationDocButton = view.findViewById(R.id.certificationAddDoc_Button);
        ongoingCertificationRadioGroup = view.findViewById(R.id.ongoingCertification_RadioGroup);
        certificationStatus = view.findViewById(R.id.certificationStatus_TextView);
        certificationStatus.setVisibility(View.GONE);
        certificationTrash = view.findViewById(R.id.certification_Trash);
        certificationTrash.setVisibility(View.GONE);
        certificationProgress = view.findViewById(R.id.certification_Progress);

        activeOrganizerRadioGroup = view.findViewById(R.id.activeOrganizer_RadioGroup);
        invitedCredentialRadioGroup = view.findViewById(R.id.credentialInvited_RadioGroup);
        conferenceSaveButton = view.findViewById(R.id.conference_save_Button);
        conferenceDocButton = view.findViewById(R.id.conferenceAddDoc_Button);
        conferenceStatus = view.findViewById(R.id.conferenceStatus_TextView);
        conferenceStatus.setVisibility(View.GONE);
        conferenceTrash = view.findViewById(R.id.conference_Trash);
        conferenceTrash.setVisibility(View.GONE);
        conferenceProgress = view.findViewById(R.id.conference_Progress);

        fundingSaveButton = view.findViewById(R.id.funding_save_Button);
        fundingDocButton = view.findViewById(R.id.fundingAddDoc_Button);
        fundingStatus = view.findViewById(R.id.fundingStatus_TextView);
        fundingStatus.setVisibility(View.GONE);
        fundingTrash = view.findViewById(R.id.funding_Trash);
        fundingScoreEditText = view.findViewById(R.id.funding_EditText);
        fundingTrash.setVisibility(View.GONE);
        fundingProgress = view.findViewById(R.id.funding_Progress);

        consultancySaveButton = view.findViewById(R.id.consultancy_save_Button);
        consultancyDocButton = view.findViewById(R.id.consultancy_add_Button);
        consultancyStatus = view.findViewById(R.id.consultancyStatus_TextView);
        consultancyStatus.setVisibility(View.GONE);
        consultancyTrash = view.findViewById(R.id.consultancy_Trash);
        consultancyEditText = view.findViewById(R.id.consultancy_EditText);
        consultancyTrash.setVisibility(View.GONE);
        consultancyProgress = view.findViewById(R.id.consultancy_Progress);

        DocumentReference documentReference = fStore.collection("users").document(user.getUid());
        documentReference.addSnapshotListener((value, error) -> {
            if (value != null) {
                dept = value.getString("Department");
                staffid = value.getString("StaffID");
                if (dept != null && dept.equals("H&S")) {
                    codingCard.setVisibility(View.GONE);
                    mentorshipCard.setVisibility(View.VISIBLE);
                } else {
                    mentorshipCard.setVisibility(View.GONE);
                    codingCard.setVisibility(View.VISIBLE);
                }
                readStatus();
            }
        });

        codingSaveButton.setOnClickListener(v -> {

            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if (TextUtils.isEmpty(codingScoreEditText.getText().toString()))
                codingScoreEditText.setError("Enter Score");
            else {
                coding_score = Integer.parseInt(codingScoreEditText.getText().toString().trim());
                if (!codeingImageUri.equals(Uri.parse(""))) {
                    codingProgress.setVisibility(View.VISIBLE);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference();
                    StorageReference reference = storageReference.child("CODING_IMAGES/" + staffid);

                    reference.putFile(codeingImageUri).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {
                        codingImgUrl = String.valueOf(uri);
                        updateItemToSheet("coding_leaderboard", codingImgUrl, coding_score);
                    })).addOnFailureListener(e -> {
                        if (!codeingImageUri.equals(Uri.parse(""))) {
                            Toast.makeText(getContext(), "Please select the document", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Server error!! Try to upload image less than 500kb", Toast.LENGTH_SHORT).show();
                        }
                        codingProgress.setVisibility(View.GONE);
                    });

                } else {
                    Toast.makeText(getContext(), "Select an proof to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });

        certificationSaveButton.setOnClickListener(v -> {
            int sel = ongoingCertificationRadioGroup.getCheckedRadioButtonId();
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if (sel <= 0) {
                Toast.makeText(getContext(), "Select the number of ongoing certifications", Toast.LENGTH_SHORT).show();
            } else {
                RadioButton btn = view.findViewById(sel);
                certification_score = Integer.parseInt(btn.getText().toString());
                if (!certificationImageUri.equals(Uri.parse(""))) {
                    certificationProgress.setVisibility(View.VISIBLE);
                    System.out.println("Progress Bar started by Theeraj");
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference();
                    StorageReference reference = storageReference.child("CERTIFICATION_IMAGES/" + staffid);

                    reference.putFile(certificationImageUri).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {
                        certificateImgUrl = String.valueOf(uri);
                        updateItemToSheet("certification_leaderboard", certificateImgUrl, certification_score * 10);

                    })).addOnFailureListener(e -> {

                        if (!certificationImageUri.equals(Uri.parse(""))) {
                            Toast.makeText(getContext(), "Please select the document", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Server error!! Try to upload image less than 500kb", Toast.LENGTH_SHORT).show();
                        }
                        certificationProgress.setVisibility(View.GONE);
                    });

                } else {
                    Toast.makeText(getContext(), "Select an proof to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });

        conferenceSaveButton.setOnClickListener(v -> {
            int sel1 = activeOrganizerRadioGroup.getCheckedRadioButtonId(),
                    sel2 = invitedCredentialRadioGroup.getCheckedRadioButtonId();
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if (sel1 <= 0 || sel2 <= 0)
                Toast.makeText(getContext(), "Check the respective options", Toast.LENGTH_SHORT).show();
            else {
                if (!conferenceImageUri.equals(Uri.parse(""))) {
                    conferenceProgress.setVisibility(View.VISIBLE);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference();
                    StorageReference reference = storageReference.child("CONFERENCE_IMAGES/" + staffid);

                    reference.putFile(conferenceImageUri).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {
                        conferenceImgUrl = String.valueOf(uri);
                        conference_score = 0;
                        if (activeOrganizerRadioGroup.getCheckedRadioButtonId() == view.findViewById(R.id.activeOrganizer_Yes).getId())
                            conference_score += 25;
                        if (invitedCredentialRadioGroup.getCheckedRadioButtonId() == view.findViewById(R.id.credentialInvited_Yes).getId())
                            conference_score += 25;
                        updateItemToSheet("conference_leaderboard", conferenceImgUrl, conference_score);
                    })).addOnFailureListener(e -> {
                        if (!conferenceImageUri.equals(Uri.parse(""))) {
                            Toast.makeText(getContext(), "Please select the document", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Server error!! Try to upload image less than 500kb", Toast.LENGTH_SHORT).show();
                        }
                        conferenceProgress.setVisibility(View.GONE);
                    });


                } else {
                    Toast.makeText(getContext(), "Select an proof to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fundingSaveButton.setOnClickListener(v -> {
            funding_score = 0;
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if (TextUtils.isEmpty(fundingScoreEditText.getText().toString()))
                fundingScoreEditText.setError("Enter the amount");
            else {
                if (!fundingImageUri.equals(Uri.parse(""))) {
                    fundingProgress.setVisibility(View.VISIBLE);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference();
                    StorageReference reference = storageReference.child("FUNDING_IMAGES/" + staffid);

                    reference.putFile(fundingImageUri).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {
                        fundingImgUrl = String.valueOf(uri);
                        int amount = Integer.parseInt(fundingScoreEditText.getText().toString().trim());
                        if (dept.equals("MECH")) {
                            if (amount > 0 && amount <= 50000)
                                funding_score = 10;
                            else if (amount > 50000 && amount <= 75000)
                                funding_score = 20;
                            else if (amount > 75000 && amount <= 100000)
                                funding_score = 30;
                            else if (amount > 100000 && amount <= 300000)
                                funding_score = 40;
                            else if (amount > 300000)
                                funding_score = 50;
                        } else {
                            if (amount > 0 && amount <= 10000)
                                funding_score = 10;
                            else if (amount > 10000 && amount <= 25000)
                                funding_score = 20;
                            else if (amount > 25000 && amount <= 100000)
                                funding_score = 30;
                            else if (amount > 100000 && amount <= 300000)
                                funding_score = 40;
                            else if (amount > 300000)
                                funding_score = 50;
                        }
                        updateItemToSheet("funding_trial_blazers", fundingImgUrl, funding_score);
                    })).addOnFailureListener(e -> {
                        if (!fundingImageUri.equals(Uri.parse(""))) {
                            Toast.makeText(getContext(), "Please select the document", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Server error!! Try to upload image less than 500kb", Toast.LENGTH_SHORT).show();
                        }
                        fundingProgress.setVisibility(View.GONE);
                    });

                } else {
                    Toast.makeText(getContext(), "Select an proof to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mentorshipSaveButton.setOnClickListener(v -> {
            int sel = mentorshipRadioGroup.getCheckedRadioButtonId();
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if (sel <= 0)
                Toast.makeText(getContext(), "Select the respective options", Toast.LENGTH_LONG).show();
            else {
                if (!mentorshipImageUri.equals(Uri.parse(""))) {
                    mentorshipProgress.setVisibility(View.VISIBLE);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference();
                    StorageReference reference = storageReference.child("MENTORSHIP_IMAGES/" + staffid);

                    reference.putFile(mentorshipImageUri).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {
                        mentorImgUrl = String.valueOf(uri);
                        if (sel == view.findViewById(R.id.mentorship_criteria_one).getId())
                            mentorship_score = 10;
                        else if (sel == view.findViewById(R.id.mentorship_criteria_two).getId())
                            mentorship_score = 20;
                        else if (sel == view.findViewById(R.id.mentorship_criteria_three).getId())
                            mentorship_score = 30;
                        else if (sel == view.findViewById(R.id.mentorship_criteria_four).getId())
                            mentorship_score = 40;
                        else if (sel == view.findViewById(R.id.mentorship_criteria_five).getId())
                            mentorship_score = 50;
                        updateItemToSheet("mentorship_leaderboard", mentorImgUrl, mentorship_score);
                    })).addOnFailureListener(e -> {
                        if (!mentorshipImageUri.equals(Uri.parse(""))) {
                            Toast.makeText(getContext(), "Please select the document", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Server error!! Try to upload image less than 500kb", Toast.LENGTH_SHORT).show();
                        }
                        mentorshipProgress.setVisibility(View.GONE);
                    });

                } else {
                    Toast.makeText(getContext(), "Select an proof to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });

        consultancySaveButton.setOnClickListener(v -> {
            consultancy_score = 0;
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if (TextUtils.isEmpty(consultancyEditText.getText().toString()))
                consultancyEditText.setError("Enter the amount");
            else {
                if (!consultancyImageUri.equals(Uri.parse(""))) {
                    consultancyProgress.setVisibility(View.VISIBLE);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference();
                    StorageReference reference = storageReference.child("CONSULTANCY_IMAGES/" + staffid);

                    reference.putFile(consultancyImageUri).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {
                        consultancyImgUrl = String.valueOf(uri);
                        int amount = Integer.parseInt(consultancyEditText.getText().toString().trim());
                        if (dept.equals("MECH")) {
                            if (amount > 0 && amount <= 50000)
                                consultancy_score = 10;
                            else if (amount > 50000 && amount <= 75000)
                                consultancy_score = 20;
                            else if (amount > 75000 && amount <= 100000)
                                consultancy_score = 30;
                            else if (amount > 100000 && amount <= 300000)
                                consultancy_score = 40;
                            else if (amount > 300000)
                                consultancy_score = 50;
                        } else {
                            if (amount > 0 && amount <= 10000)
                                consultancy_score = 10;
                            else if (amount > 10000 && amount <= 25000)
                                consultancy_score = 20;
                            else if (amount > 25000 && amount <= 100000)
                                consultancy_score = 30;
                            else if (amount > 100000 && amount <= 300000)
                                consultancy_score = 40;
                            else if (amount > 300000)
                                consultancy_score = 50;
                        }
                        updateItemToSheet("consultancy_victors", consultancyImgUrl, consultancy_score);
                    })).addOnFailureListener(e -> {
                        if (!consultancyImageUri.equals(Uri.parse(""))) {
                            Toast.makeText(getContext(), "Please select the document", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Server error!! Try to upload image less than 500kb", Toast.LENGTH_SHORT).show();
                        }
                        consultancyProgress.setVisibility(View.GONE);
                    });
                } else {
                    Toast.makeText(getContext(), "Select an proof to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });

        codingTrash.setOnClickListener(v -> {
            codeingImageUri = Uri.parse("");
            hide(codingTrash, codingStatus);
        });

        certificationTrash.setOnClickListener(v -> {
            certificationImageUri = Uri.parse("");
            hide(certificationTrash, certificationStatus);
        });

        conferenceTrash.setOnClickListener(v -> {
            conferenceImageUri = Uri.parse("");
            hide(conferenceTrash, conferenceStatus);
        });

        fundingTrash.setOnClickListener(v -> {
            fundingImageUri = Uri.parse("");
            hide(fundingTrash, fundingStatus);
        });

        mentorshipTrash.setOnClickListener(v -> {
            mentorshipImageUri = Uri.parse("");
            hide(mentorshipTrash, mentorshipStatus);
        });

        consultancyTrash.setOnClickListener(v -> {
            consultancyImageUri = Uri.parse("");
            hide(consultancyTrash, consultancyStatus);
        });

        codingDocButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        });

        certificationDocButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
        });

        conferenceDocButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 3);
        });

        fundingDocButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 4);
        });

        mentorshipDocButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 5);
        });

        consultancyDocButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 6);
        });
        return view;
    }

    void showLoading() {
        screenProgress.setVisibility(View.VISIBLE);
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    void dismissLoading() {
        screenProgress.setVisibility(View.GONE);
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    void hide(ImageView imageView, TextView textView) {
        imageView.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            codeingImageUri = data.getData();
            if (!String.valueOf(codeingImageUri).equals("")) {
                codingTrash.setVisibility(View.VISIBLE);
                codingStatus.setText(R.string.addimage);
                codingStatus.setTextColor(Color.parseColor("#000000"));
                codingStatus.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == 2 && data != null) {
            certificationImageUri = data.getData();
            if (!String.valueOf(certificationImageUri).equals("")) {
                certificationTrash.setVisibility(View.VISIBLE);
                certificationStatus.setText(R.string.addimage);
                certificationStatus.setTextColor(Color.parseColor("#000000"));
                certificationStatus.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == 3 && data != null) {
            conferenceImageUri = data.getData();
            if (!String.valueOf(conferenceImageUri).equals("")) {
                conferenceTrash.setVisibility(View.VISIBLE);
                conferenceStatus.setText(R.string.addimage);
                conferenceStatus.setTextColor(Color.parseColor("#000000"));
                conferenceStatus.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == 4 && data != null) {
            fundingImageUri = data.getData();
            if (!String.valueOf(fundingImageUri).equals("")) {
                fundingTrash.setVisibility(View.VISIBLE);
                fundingStatus.setText(R.string.addimage);
                fundingStatus.setTextColor(Color.parseColor("#000000"));
                fundingStatus.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == 5 && data != null) {
            mentorshipImageUri = data.getData();
            if (!String.valueOf(mentorshipImageUri).equals("")) {
                mentorshipTrash.setVisibility(View.VISIBLE);
                mentorshipStatus.setText(R.string.addimage);
                mentorshipStatus.setTextColor(Color.parseColor("#000000"));
                mentorshipStatus.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == 6 && data != null) {
            consultancyImageUri = data.getData();
            if (!String.valueOf(consultancyImageUri).equals("")) {
                consultancyTrash.setVisibility(View.VISIBLE);
                consultancyStatus.setText(R.string.addimage);
                consultancyStatus.setTextColor(Color.parseColor("#000000"));
                consultancyStatus.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateItemToSheet(String Sheet, String doclink, int score) {

        String url = "https://script.google.com/macros/s/AKfycbwKkdOMQgg2J6HjRjA6_k2aL4ZGbvEuXJDh2MqYkj-7Kbc1KhET7DCXvszBQQ6C1r4/exec";
        int temp = 0;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    switch (Sheet) {
                        case "coding_leaderboard":
                            codingChanges(temp);
                            break;
                        case "mentorship_leaderboard":
                            mentorshipChanges(temp);
                            break;
                        case "certification_leaderboard":
                            certificationChanges(temp);
                            break;
                        case "conference_leaderboard":
                            conferenceChanges(temp);
                            break;
                        case "funding_trial_blazers":
                            fundingChanges(temp);
                            break;
                        case "consultancy_victors":
                            consultancyChanges(temp);
                            break;
                    }
                    Toast.makeText(getContext(), "Successfully sent for verification", Toast.LENGTH_SHORT).show();
                },
                error -> Toast.makeText(getContext(), "Please try again", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();
                //here we pass params
                parmas.put("action", "updateItem");
                parmas.put("Sheet", Sheet);
                parmas.put("userid", staffid);
                parmas.put("doclink", doclink);
                parmas.put("score", String.valueOf(score));
                return parmas;
            }
        };

        int socketTimeOut = 30000;

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        queue.add(stringRequest);

        codingProgress.setVisibility(View.GONE);
        mentorshipProgress.setVisibility(View.GONE);
        certificationProgress.setVisibility(View.GONE);
        conferenceProgress.setVisibility(View.GONE);
        fundingProgress.setVisibility(View.GONE);
        consultancyProgress.setVisibility(View.GONE);
    }

    private void readStatus() {
       showLoading();
        if (dept.equals("H&S"))
            dept = "H%26S";
        String url = "https://script.google.com/macros/s/AKfycbwGVGmvfNOiO8URBCf6Kuse3JoCPQIZpDPx_H2fDC7VheeEyWSaozuzPZ6HYYm6H8c/exec?action=getStatus&userid=" + staffid + "&dept=" + dept;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> parseItems(response),

                error -> {
                    Toast.makeText(getContext(), "Error 404 Data Not Found", Toast.LENGTH_SHORT).show();
                    dismissLoading();
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(stringRequest);
    }

    @SuppressLint("DefaultLocale")
    private void parseItems(String response) {

        try {
            JSONObject jobj = new JSONObject(response);
            JSONArray jarray = jobj.getJSONArray("items");

            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);
                String Sheetname = jo.getString("0");
                String Status = jo.getString("1");
                String score = jo.getString("2");
                int score1 = Integer.parseInt(score);

                if (Sheetname.equals("coding_leaderboard") && Status.equals("PENDING"))
                    codingChanges(0);
                else if (Sheetname.equals("coding_leaderboard") && Status.equals("APPROVED"))
                    codingChanges(score1);
                else if (Sheetname.equals("coding_leaderboard") && Status.equals("DECLINED"))
                    codingChanges(-1);

                if (Sheetname.equals("certification_leaderboard") && Status.equals("PENDING"))
                    certificationChanges(0);
                else if (Sheetname.equals("certification_leaderboard") && Status.equals("APPROVED"))
                    certificationChanges(score1);
                else if (Sheetname.equals("certification_leaderboard") && Status.equals("DECLINED"))
                    certificationChanges(-1);

                if (Sheetname.equals("conference_leaderboard") && Status.equals("PENDING"))
                    conferenceChanges(0);
                else if (Sheetname.equals("conference_leaderboard") && Status.equals("APPROVED"))
                    conferenceChanges(score1);
                else if (Sheetname.equals("conference_leaderboard") && Status.equals("DECLINED"))
                    certificationChanges(-1);

                if (Sheetname.equals("funding_trial_blazers") && Status.equals("PENDING"))
                    fundingChanges(0);
                else if (Sheetname.equals("funding_trial_blazers") && Status.equals("APPROVED"))
                    fundingChanges(score1);
                else if (Sheetname.equals("funding_trial_blazers") && Status.equals("DECLINED"))
                    certificationChanges(-1);

                if (Sheetname.equals("consultancy_victors") && Status.equals("PENDING"))
                    consultancyChanges(0);
                else if (Sheetname.equals("consultancy_victors") && Status.equals("APPROVED"))
                    consultancyChanges(score1);
                else if (Sheetname.equals("consultancy_victors") && Status.equals("DECLINED"))
                    certificationChanges(-1);

                if (Sheetname.equals("mentorship_leaderboard") && Status.equals("PENDING"))
                    mentorshipChanges(0);
                else if (Sheetname.equals("mentorship_leaderboard") && Status.equals("APPROVED"))
                    mentorshipChanges(score1);
                else if (Sheetname.equals("mentorship_leaderboard") && Status.equals("DECLINED"))
                    certificationChanges(-1);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dismissLoading();
    }

    @SuppressLint("SetTextI18n")
    void codingChanges(int score) {
        codingTrash.setVisibility(View.GONE);
        codingStatus.setVisibility(View.VISIBLE);
        if (score == 0) {
            codingStatus.setText(R.string.request_sent);
            codingStatus.setTextColor(Color.parseColor("#006AFC"));
        } else if (score == -1) {
            codingStatus.setText("Sorry your submission was not approved !");
            codingStatus.setTextColor(Color.parseColor("#F44336"));
        } else {
            codingStatus.setText("Approved! Coding Score : " + score);
            codingStatus.setTextColor(Color.parseColor("#4CAF50"));
        }
        codingScoreEditText.setText(null);
        dismissLoading();
    }

    @SuppressLint("SetTextI18n")
    void mentorshipChanges(int score) {
        mentorshipTrash.setVisibility(View.GONE);
        mentorshipStatus.setVisibility(View.VISIBLE);
        if (score == 0) {
            mentorshipStatus.setText(R.string.request_sent);
            mentorshipStatus.setTextColor(Color.parseColor("#006AFC"));
        } else if (score == -1) {
            codingStatus.setText("Sorry your submission was not approved !");
            codingStatus.setTextColor(Color.parseColor("#F44336"));
        } else {
            mentorshipStatus.setText("Approved! Mentorship Score : " + score);
            mentorshipStatus.setTextColor(Color.parseColor("#4CAF50"));
        }
        mentorshipRadioGroup.clearCheck();
        dismissLoading();
    }

    @SuppressLint("SetTextI18n")
    void consultancyChanges(int score) {
        consultancyTrash.setVisibility(View.GONE);
        consultancyStatus.setVisibility(View.VISIBLE);
        if (score == 0) {
            consultancyStatus.setText(R.string.request_sent);
            consultancyStatus.setTextColor(Color.parseColor("#006AFC"));
        } else if (score == -1) {
            codingStatus.setText("Sorry your submission was not approved !");
            codingStatus.setTextColor(Color.parseColor("#F44336"));
        } else {
            consultancyStatus.setText("Approved! Consultancy Score : " + score);
            consultancyStatus.setTextColor(Color.parseColor("#4CAF50"));
        }
        consultancyEditText.setText(null);
        dismissLoading();
    }

    @SuppressLint("SetTextI18n")
    void certificationChanges(int score) {
        certificationTrash.setVisibility(View.GONE);
        certificationStatus.setVisibility(View.VISIBLE);
        if (score == 0) {
            certificationStatus.setText(R.string.request_sent);
            certificationStatus.setTextColor(Color.parseColor("#006AFC"));
        } else if (score == -1) {
            codingStatus.setText("Sorry your submission was not approved !");
            codingStatus.setTextColor(Color.parseColor("#F44336"));
        } else {
            certificationStatus.setText("Approved! Certification Score : " + score);
            certificationStatus.setTextColor(Color.parseColor("#4CAF50"));
        }
        ongoingCertificationRadioGroup.clearCheck();
        dismissLoading();
    }

    @SuppressLint("SetTextI18n")
    void conferenceChanges(int score) {
        conferenceTrash.setVisibility(View.GONE);
        conferenceStatus.setVisibility(View.VISIBLE);
        if (score == 0) {
            conferenceStatus.setText(R.string.request_sent);
            conferenceStatus.setTextColor(Color.parseColor("#006AFC"));
        } else if (score == -1) {
            codingStatus.setText("Sorry your submission was not approved !");
            codingStatus.setTextColor(Color.parseColor("#F44336"));
        } else {
            conferenceStatus.setText("Approved! Conference Score : " + score);
            conferenceStatus.setTextColor(Color.parseColor("#4CAF50"));
        }
        activeOrganizerRadioGroup.clearCheck();
        invitedCredentialRadioGroup.clearCheck();
        dismissLoading();
    }

    @SuppressLint("SetTextI18n")
    void fundingChanges(int score) {
        fundingTrash.setVisibility(View.GONE);
        fundingStatus.setVisibility(View.VISIBLE);
        if (score == 0) {
            fundingStatus.setText(R.string.request_sent);
            fundingStatus.setTextColor(Color.parseColor("#006AFC"));
        } else if (score == -1) {
            codingStatus.setText("Sorry your submission was not approved !");
            codingStatus.setTextColor(Color.parseColor("#F44336"));
        } else {
            fundingStatus.setText("Approved! Funding Score : " + score);
            fundingStatus.setTextColor(Color.parseColor("#4CAF50"));
        }
        fundingScoreEditText.setText("");
        dismissLoading();
    }

}