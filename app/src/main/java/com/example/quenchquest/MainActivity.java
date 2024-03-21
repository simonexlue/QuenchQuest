package com.example.quenchquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import com.example.quenchquest.Fragments.AddFragment;
import com.example.quenchquest.Fragments.LearnFragment;
import com.example.quenchquest.Fragments.TodayFragment;
import com.example.quenchquest.Fragments.SettingsFragment;
import com.example.quenchquest.Fragments.StatsFragment;
import com.example.quenchquest.Interface.FragmentToActivity;
import com.example.quenchquest.LoginAndRegister.Login;
import com.example.quenchquest.Model.Drink;
import com.example.quenchquest.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements FragmentToActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth;
    FirebaseUser user;
    Button logout;
    ActivityMainBinding binding;
    AddFragment addFragment = new AddFragment();
    LearnFragment learnFragment = new LearnFragment();
    TodayFragment todayFragment = new TodayFragment();
    SettingsFragment settingsFragment = new SettingsFragment();
    StatsFragment statsFragment = new StatsFragment();
    Drink drink;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(getApplicationContext());

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        //logout = findViewById(R.id.button);

        if (user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        storeUserDataInFirestore();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,addFragment).commit();
        binding.bottomNavigationView.setSelectedItemId(R.id.add);

        //Fragment switching for Bottom Navigation Menu
        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.add){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, addFragment).commit();
                    return true;
                }
                else if (item.getItemId() == R.id.learn){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, learnFragment).commit();
                    return true;
                }
                else if (item.getItemId() == R.id.today){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, todayFragment).commit();
                    return true;
                }
                else if (item.getItemId() == R.id.settings){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, settingsFragment).commit();
                    return true;
                }
                else if (item.getItemId() == R.id.stats){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, statsFragment).commit();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void createDrink(String name, int volume, long time) {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDateString = dateFormat.format(today);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> drinkData = new HashMap<>();
        drinkData.put("name", name);
        drinkData.put("volume", volume);
        drinkData.put("time", FieldValue.serverTimestamp());
        db.collection("DrinksHistory").document(userId).collection(todayDateString)
                .add(drinkData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("TAG", "Drink added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("TAG", "Error adding drink", e);
                });
    }

    @Override
    public void onGoalChanged(int newGoal) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userDocRef = db.collection("users").document(userId);

        userDocRef.update("goal", newGoal).addOnSuccessListener(aVoid -> Log.d("TAG", "Goal updated successfully"))
                .addOnFailureListener(e -> Log.e("TAG", "Error updating goal", e));
    }

    private void storeUserDataInFirestore() {
        DocumentReference userDocRef = db.collection("users").document(user.getUid());

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d("MainActivity", "User data already exists in Firestore");
                // Skip initialization since data already exists
                return;
            }

            // Data doesn't exist, proceed with initialization
            Map<String, Object> userData = new HashMap<>();
            userData.put("email", user.getEmail());
            userData.put("displayName", user.getDisplayName());

            if (user.getPhotoUrl() != null) {
                userData.put("profilePictureUrl", user.getPhotoUrl().toString());
            }

            Map<String, Object> achievementsData = new HashMap<>();
            achievementsData.put("dailyCompleted", false);
            achievementsData.put("currentStreak", 0);
            // Add other achievement fields here

            userData.put("achievements", achievementsData);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = dateFormat.format(new Date());
            userData.put("lastUpdated", today);

            userDocRef.set(userData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Log.d("MainActivity", "User data stored successfully");
                        // Retrieve and set current streak
                        userDocRef.get().addOnSuccessListener(snapshot -> {
                            Map<String, Object> achievementsFromFirestore = (Map<String, Object>) snapshot.get("achievements");
                            if (achievementsFromFirestore != null) {
                                int currentStreak = (int) achievementsFromFirestore.get("currentStreak");
                                statsFragment.setCurrentStreak(currentStreak);
                            }
                        }).addOnFailureListener(e -> Log.e("MainActivity", "Error retrieving current streak", e));
                    })
                    .addOnFailureListener(e -> Log.e("MainActivity", "Error storing user data", e));
        }).addOnFailureListener(e -> Log.e("MainActivity", "Error checking user data existence", e));
    }
}