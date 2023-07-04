package com.ritchennai.pinnacle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    ArrayList<String> s_no;
    ArrayList<String> staff_name;
    ArrayList<String> score;
    ArrayList<String> user_id;
    ArrayList<String> document_link;
    StorageReference storageReference;
    Context context;

    public LeaderboardAdapter(Context context, ArrayList<String> sno, ArrayList<String> staffName, ArrayList<String> score, ArrayList<String> userId, ArrayList<String> documentLink) {
        this.context = context;
        this.s_no = sno;
        this.staff_name = staffName;
        this.user_id = userId;
        this.document_link = documentLink;
        this.score = score;
    }

    @NonNull
    @Override
    public LeaderboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.leaderboard_single_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardAdapter.ViewHolder holder, int position) {
        holder.snoTextView.setText(s_no.get(position));
        holder.nameTextView.setText(staff_name.get(position));
        holder.scoreTextView.setText(String.format("%s points", score.get(position)));

        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child(user_id.get(position));
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(holder.profileImageView));
    }

    @Override
    public int getItemCount() {
        return s_no.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView snoTextView, nameTextView, scoreTextView;
        ImageView profileImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            snoTextView = itemView.findViewById(R.id.sno_TextView);
            nameTextView = itemView.findViewById(R.id.username_TextView);
            scoreTextView = itemView.findViewById(R.id.points_TextView);
            profileImageView = itemView.findViewById(R.id.profile_user_Image);
        }
    }
}
