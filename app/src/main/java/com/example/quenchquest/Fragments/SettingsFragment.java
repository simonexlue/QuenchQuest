package com.example.quenchquest.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quenchquest.LoginAndRegister.Login;
import com.example.quenchquest.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    private BottomSheetDialog dialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        dialog = new BottomSheetDialog(this.getContext());
        createDialog();

        return view;
    }

    private void createDialog() {
        View view = getLayoutInflater().inflate(R.layout.notification_settingsdialog, null, false);

        Switch fifteen = view.findViewById(R.id.switch_15);
        Switch thirty = view.findViewById(R.id.switch_30);
        Switch hour = view. findViewById(R.id.switch_60);

        dialog.setContentView(view);
    }
}
