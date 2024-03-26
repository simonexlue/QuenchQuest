package com.example.quenchquest.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quenchquest.R;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LearnFragment extends Fragment {
    private AsyncHttpClient client;
    TextView bmiTextView;
    Button btnCalculate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn, container, false);
        bmiTextView = view.findViewById(R.id.bmi);
        btnCalculate = view.findViewById(R.id.button);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeBMICalculationRequest();

            }
        });
        client = new DefaultAsyncHttpClient();
        return view;
    }

    private void makeBMICalculationRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    org.asynchttpclient.Response response = client.prepare("GET", "https://body-mass-index-bmi-calculator.p.rapidapi.com/metric?weight=150&height=1.83")
                            .setHeader("X-RapidAPI-Key", "a2ad5007e9mshacc365f72da766ep141459jsn3457b3ee9116")
                            .setHeader("X-RapidAPI-Host", "body-mass-index-bmi-calculator.p.rapidapi.com")
                            .execute()
                            .toCompletableFuture()
                            .join();

                    // Handle the response
                    handleBMIResponse(response);
                } catch (Exception e) {
                    Log.e("LearnFragment", "Error making BMI calculation request: " + e.getMessage());
                }
            }
        }).start();
    }

    private void handleBMIResponse(org.asynchttpclient.Response response) {
        try {
            String responseBody = response.getResponseBody();
            JSONObject jsonResponse = new JSONObject(responseBody);
            final double bmi = jsonResponse.getDouble("bmi");

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bmiTextView.setText("BMI: " + bmi);
                }
            });
        } catch (Exception e) {
            Log.e("LearnFragment", "Error handling BMI response: " + e.getMessage());
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Close the AsyncHttpClient instance when the fragment is destroyed to avoid memory leaks
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
