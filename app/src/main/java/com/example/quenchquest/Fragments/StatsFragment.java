package com.example.quenchquest.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    RecyclerView.Adapter adapter;
    int totalVolume = 0;

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
                   DocumentReference userDocRef = db.collection("users").document(userId);
                   int finalTotalVolume = totalVolume;
                   userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                       if (documentSnapshot.exists()) {
                           int goal = documentSnapshot.getLong("goal").intValue();
                           updateAchievements(finalTotalVolume, goal);
                       } else {
                           Log.e("StatsFragment", "User document not found");
                       }
                   }).addOnFailureListener(e -> Log.e("StatsFragment", "Error retrieving user document", e));
               } else {
                   Log.e("StatsFragment", "Error getting documents: ", task.getException());
               }
            });
        });
    }

    private void updateAchievements(int finalTotalVolume, int goal) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userDocRef = db.collection("users").document(userId);

        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());

        // Update dailyCompleted if totalVolume > goal
        boolean dailyCompleted = finalTotalVolume >= goal;
        Map<String, Object> updates = new HashMap<>();
        updates.put("achievements.dailyCompleted", dailyCompleted);

        // Update currentStreak if totalVolume > goal and it's a new day
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> userData = documentSnapshot.getData();
            if (userData != null) {
                Map<String, Object> achievementsData = (Map<String, Object>) userData.get("achievements");
                if (achievementsData != null) {
                    boolean lastDailyCompleted = (boolean) achievementsData.get("dailyCompleted");
                    boolean isToday = today.equals((String) userData.get("lastUpdated"));

                    if (dailyCompleted && !lastDailyCompleted && isToday) {
                        int currentStreak = ((Long) achievementsData.get("currentStreak")).intValue();
                        updates.put("achievements.currentStreak", currentStreak + 1);
                        updates.put("lastUpdated", today);
                    }
                    Log.d("StatsFragment", "dailyCompleted: " + dailyCompleted);
                    Log.d("StatsFragment", "lastDailyCompleted: " + lastDailyCompleted);
                    Log.d("StatsFragment", "isToday: " + isToday);
                }
            }

            // Update achievements in Firestore
            userDocRef.update(updates)
                    .addOnSuccessListener(aVoid -> Log.d("StatsFragment", "Achievements updated successfully"))
                    .addOnFailureListener(e -> Log.e("StatsFragment", "Error updating achievements", e));
        });
    }
}