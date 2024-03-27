package com.example.quenchquest.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quenchquest.R;
import com.example.quenchquest.WebViewActivity;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LearnFragment extends Fragment {
    private AsyncHttpClient client;
    TextView bmiTextView;
    Button btnCalculate;
    EditText editHeight;
    EditText editWeight;
    Button btnWaterArticle, btnWaterArticle2;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn, container, false);
        bmiTextView = view.findViewById(R.id.bmi);
        btnCalculate = view.findViewById(R.id.button);
        editHeight = view.findViewById(R.id.editTextHeight);
        editWeight = view.findViewById(R.id.editTextWeight);
        btnWaterArticle = view.findViewById(R.id.buttonWaterArticle);
        btnWaterArticle2 = view.findViewById(R.id.buttonWaterArticle2);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String weight = editWeight.getText().toString();
                String height = editHeight.getText().toString();
                Log.d("TAG", "weight: " + weight);
                Log.d("TAG", "height: " + height);

                // Check if weight and height are not empty
                if (!weight.isEmpty() && !height.isEmpty()) {
                    makeBMICalculationRequest(weight, height);
                } else {
                    Toast.makeText(getContext(), "Please enter weight and height", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnWaterArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.mayoclinic.org/healthy-lifestyle/nutrition-and-healthy-eating/in-depth/water/art-20044256";
                openWebPage(url);
            }
        });
        btnWaterArticle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.healthlinkbc.ca/healthy-eating-physical-activity/food-and-nutrition/eating-habits/drinking-enough-water#:~:text=A%20common%20recommendation%20is%20to,and%20dry%20the%20climate%20is.";
                openWebPage(url);
            }
        });
        client = new DefaultAsyncHttpClient();
        return view;
    }

    private void makeBMICalculationRequest(String weight, String height) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    org.asynchttpclient.Response response = client.prepare("GET", "https://body-mass-index-bmi-calculator.p.rapidapi.com/imperial?weight=" + weight + "&height=" + height)
                            .setHeader("X-RapidAPI-Key", "a2ad5007e9mshacc365f72da766ep141459jsn3457b3ee9116")
                            .setHeader("X-RapidAPI-Host", "body-mass-index-bmi-calculator.p.rapidapi.com")
                            .execute()
                            .toCompletableFuture()
                            .join();

                    // Handle the response
                    handleBMIResponse(response);
                    Log.d("Response", "Response Body: " + response.getResponseBody());
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
                    String formattedBmi = String.format("%.2f", bmi);
                    bmiTextView.setText("BMI: " + formattedBmi);
                }
            });
        } catch (Exception e) {
            Log.e("LearnFragment", "Error handling BMI response: " + e.getMessage());
        }
    }
    private void openWebPage(String url) {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
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
