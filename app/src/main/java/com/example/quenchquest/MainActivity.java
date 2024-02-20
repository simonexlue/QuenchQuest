package com.example.quenchquest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.example.quenchquest.Fragments.AddFragment;
import com.example.quenchquest.Fragments.LearnFragment;
import com.example.quenchquest.Fragments.TodayFragment;
import com.example.quenchquest.Fragments.SettingsFragment;
import com.example.quenchquest.Fragments.StatsFragment;
import com.example.quenchquest.LoginAndRegister.Login;
import com.example.quenchquest.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    Button logout;
    ActivityMainBinding binding;
    AddFragment addFragment = new AddFragment();
    LearnFragment learnFragment = new LearnFragment();
    TodayFragment todayFragment = new TodayFragment();
    SettingsFragment settingsFragment = new SettingsFragment();
    StatsFragment statsFragment = new StatsFragment();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        //logout = findViewById(R.id.button);

        if (user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getApplicationContext(), Login.class);
//                startActivity(intent);
//                finish();
//            }
//        });

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
}