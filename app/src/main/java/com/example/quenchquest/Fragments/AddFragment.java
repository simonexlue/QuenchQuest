package com.example.quenchquest.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quenchquest.Interface.FragmentToActivity;
import com.example.quenchquest.Model.Drink;
import com.example.quenchquest.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class AddFragment extends Fragment {

    private BottomSheetDialog dialog;
    private Button btnShowDialog;
    private int volume = 400;
    private EditText drinkNameInput;
    private EditText drinkVolumeInput;
    private String drinkName;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        btnShowDialog = view.findViewById(R.id.addDrink);
        dialog = new BottomSheetDialog(this.getContext());
        createDialog();

        btnShowDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        return view;
    }
    public void Display() {
        drinkVolumeInput.setText(volume + " mL");
    }
    private void createDialog() {
        View view = getLayoutInflater().inflate(R.layout.drink_dialog, null, false);
        drinkNameInput = view.findViewById(R.id.editTextDrinkName);
        drinkVolumeInput = view.findViewById(R.id.editTextDrinkVolume);
        Button plus = view.findViewById(R.id.plus);
        Button minus = view.findViewById(R.id.minus);
        Button submit = view.findViewById(R.id.btnSubmit);

        drinkVolumeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();
                if (!input.isEmpty()){
                    volume = Integer.parseInt(input.replaceAll("[^\\d]", ""));
                }
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volume = volume + 50;
                Display();
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volume = volume - 50;
                Display();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drinkName = drinkNameInput.getText().toString();

                if(drinkName.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a drink name", Toast.LENGTH_SHORT).show();
                } else {
                    FragmentToActivity transfer = (FragmentToActivity) getActivity();
                    transfer.createDrink(drinkName,volume,System.currentTimeMillis());
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Drink Added", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setContentView(view);
    }
}