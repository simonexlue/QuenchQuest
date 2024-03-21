package com.example.quenchquest.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quenchquest.Adapter.CardAdapter;
import com.example.quenchquest.Model.Drink;
import com.example.quenchquest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatsFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    CardAdapter adapter;
    int totalVolume = 0;
    private boolean streakIncrementedToday = false; // Flag to track if streak has already been incremented today


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CardAdapter();
        recyclerView.setAdapter(adapter);

        calculateTotalVolume();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userDocRef = db.collection("users").document(userId);

        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());

        // Update dailyCompleted if totalVolume > goal
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                int goal = documentSnapshot.getLong("goal").intValue();
                int currentStreak = documentSnapshot.getLong("achievements.currentStreak").intValue();
                adapter.setCurrentStreak(currentStreak);

                boolean dailyCompleted = totalVolume >= goal;
                boolean isToday = today.equals(documentSnapshot.getString("lastUpdated"));

                Map<String, Object> updates = new HashMap<>();
                updates.put("achievements.dailyCompleted", dailyCompleted);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.set(Calendar.HOUR_OF_DAY, 23); // 11 PM
                calendar.set(Calendar.MINUTE, 59); // 59 minutes
                calendar.set(Calendar.SECOND, 59); // 59 seconds
                Date thresholdTime = calendar.getTime();

                if(isToday) {
                    if (dailyCompleted && !streakIncrementedToday) {
                        updates.put("achievements.currentStreak", currentStreak + 1);
                        adapter.updateAchievementStatus(true);
                        streakIncrementedToday = true;
                    } else {
                        if (new Date().after(thresholdTime)) {
                            // Reset the streak to 0 if today's goal is not completed and past the threshold time
                            updates.put("achievements.currentStreak", 0);
                            adapter.updateAchievementStatus(false);
                        }
                    }
                } else {
                    streakIncrementedToday = false;
                }
                updates.put("lastUpdated", today);
                Log.d("TAG", "dailycompleted" + dailyCompleted);
                Log.d("TAG", "isToday" + isToday);
                Log.d("TAG", "streakIncrementedToday" + streakIncrementedToday);
                Log.d("TAG", "currentstreak" + currentStreak);

                // Update achievements in Firestore
                userDocRef.update(updates)
                        .addOnSuccessListener(aVoid -> Log.d("StatsFragment", "Achievements updated successfully"))
                        .addOnFailureListener(e -> Log.e("StatsFragment", "Error updating achievements", e));

                adapter.setCurrentStreak(currentStreak);
            } else {
                Log.e("StatsFragment", "User document not found");
            }
        }).addOnFailureListener(e -> Log.e("StatsFragment", "Error retrieving user document", e));

        return view;
    }

    public void calculateTotalVolume() {
        executorService.execute(() -> {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String today = format.format(new Date());
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            CollectionReference drinkHistoryRef = db.collection("DrinksHistory").document(userId).collection(today);
            drinkHistoryRef.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    totalVolume = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        int volume = document.getLong("volume").intValue();
                        totalVolume += volume;
                    }
                    Log.d("TAG", "Total volume for user " + totalVolume);
                } else {
                    Log.e("StatsFragment", "Error getting documents: ", task.getException());
                }
            });
        });
    }

    public void setCurrentStreak(int streak) {
        if (adapter != null) {
            adapter.setCurrentStreak(streak);
        } else {
            Log.e("StatsFragment", "Adapter is null");
        }
    }
}