package com.example.quenchquest.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quenchquest.R;
import com.google.android.material.snackbar.Snackbar;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private String[] titles = {"Current Streak", "Daily Achievement", "Goldfish", "Clownfish", "Piranha", "Koi Fish", "Great Barracuda", "Dolphin", "Shark"};
    private String[] texts = {"Your current daily achievement streak", "Complete your daily water intake goal to get this achievement!", "Complete 3 days consecutively to get this achievement!",
            "Complete 7 days consecutively to get this achievement!", "Complete 14 days consecutively to get this achievement!", "Complete 30 days consecutively to get this achievement!",
            "Complete 60 days consecutively to get this achievement!", "Complete 90 days consecutively to get this achievement!", "Complete 365 days consecutively to get this achievement!"};
    private int[] images = {R.drawable.trophy, R.drawable.trophy, R.drawable.goldfish, R.drawable.clownfish, R.drawable.piranha, R.drawable.koi, R.drawable.barracuda,
            R.drawable.dolphin, R.drawable.shark};
    private boolean[] achievedStatus = new boolean[titles.length];
    private int currentStreak = 0;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.itemTitle.setText(titles[i]);
        holder.itemImage.setImageResource(images[i]);
        Log.d("TAG", "achieved " + achievedStatus[1]);


        if(i == 0) {
            holder.itemText.setText("Your current streak is: " + currentStreak + " days!");
            achievedStatus[0] = true;
        } else {
            holder.itemText.setText(texts[i]);
        }

        if (achievedStatus[i]) {
            holder.overlay.setVisibility(View.GONE);
        } else {
            holder.overlay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImage;
        TextView itemTitle;
        TextView itemText;
        View overlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemText = itemView.findViewById(R.id.item_text);
            itemTitle = itemView.findViewById(R.id.item_title);
            overlay = itemView.findViewById(R.id.overlay);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Snackbar.make(v, "Click detected on item " + position, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });
        }
    }
    public void setCurrentStreak(int streak) {
        this.currentStreak = streak;
        notifyDataSetChanged();
    }
    public void setAchievedStatus(int position, boolean status) {
        achievedStatus[position] = status;
        notifyItemChanged(position); // Notify adapter of specific item change
    }
}
