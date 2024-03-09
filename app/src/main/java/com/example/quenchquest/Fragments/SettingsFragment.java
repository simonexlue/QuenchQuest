package com.example.quenchquest.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.quenchquest.LoginAndRegister.Login;
import com.example.quenchquest.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsFragment extends Fragment {

    BottomSheetDialog dialog;
    CardView cardViewNotifications;
    CardView cardViewEditProfile;
    Switch notificationsSwitch;
    SharedPreferences sharedPreferences;
    TextView userDisplayName;
    Button logout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        cardViewNotifications = view.findViewById(R.id.cardViewNotifications);
        cardViewEditProfile = view.findViewById(R.id.cardViewProfile);
        notificationsSwitch = view.findViewById(R.id.notificationSwitch);
        userDisplayName = view.findViewById(R.id.txtViewSettings);
        logout = view.findViewById(R.id.btnLogout);

        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        boolean switchState = sharedPreferences.getBoolean("notificationSwitchState", false);
        notificationsSwitch.setChecked(switchState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(user.getUid());
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String displayName = documentSnapshot.getString("displayName");
                        if (displayName != null && !displayName.isEmpty()) {
                            userDisplayName.setText("Hello, " + displayName);
                        } else {
                            userDisplayName.setText("Hello, User");
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("TAG", "Error retrieving display name from Firestore", e);
                }
            });
        }


        dialog = new BottomSheetDialog(this.getContext());
        createDialog();
        cardViewNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        cardViewEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, new EditProfileFragment())
                        .addToBackStack(null) // Optional: Add to back stack to enable back navigation
                        .commit();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
            }
        });

        return view;
    }


    private void createDialog() {
        View view = getLayoutInflater().inflate(R.layout.notification_settingsdialog, null, false);
        final int[] time = {0};
        Switch fifteen = view.findViewById(R.id.switch_15);
        Switch thirty = view.findViewById(R.id.switch_30);
        Switch hour = view. findViewById(R.id.switch_60);

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        fifteen.setChecked(sharedPreferences.getBoolean("switch_15_state", false));
        thirty.setChecked(sharedPreferences.getBoolean("switch_30_state", false));
        hour.setChecked(sharedPreferences.getBoolean("switch_60_state", false));

        fifteen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("switch_15_state", isChecked);
                editor.apply();
                if(isChecked) {
                    thirty.setChecked(false);
                    hour.setChecked(false);
                    time[0] = 15;
                    notificationsSwitch.setChecked(true);
                    Log.d("time", "time: " + time[0]);
                }
            }
        });
        thirty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("switch_30_state", isChecked);
                editor.apply();
                if(isChecked) {
                    fifteen.setChecked(false);
                    hour.setChecked(false);
                    time[0] = 30;
                    notificationsSwitch.setChecked(true);
                    Log.d("time", "time: " + time[0]);
                }
            }
        });
        hour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("switch_60_state", isChecked);
                editor.apply();
                if(isChecked) {
                    thirty.setChecked(false);
                    fifteen.setChecked(false);
                    time[0] = 60;
                    notificationsSwitch.setChecked(true);
                    Log.d("time", "time: " + time[0]);
                }
            }
        });
        dialog.setContentView(view);
    }
    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notificationSwitchState", notificationsSwitch.isChecked());
        editor.apply();
    }
}
