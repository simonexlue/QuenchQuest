package com.example.quenchquest.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quenchquest.Adapter.TodayDrinksAdapter;
import com.example.quenchquest.Model.Drink;
import com.example.quenchquest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.api.Distribution;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TodayFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> DrinkName = new ArrayList<>();
    List<String> DrinkVolume = new ArrayList<>();
    List<String> DrinkTime = new ArrayList<>();
    CircularProgressIndicator progressIndicator;
    RecyclerView recyclerView;
    List<Drink> DrinkList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        progressIndicator = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        FirebaseApp.initializeApp(getContext());
        LoadModel();

        return view;
    }

    private void LoadModel() {
        CollectionReference drinkHistoryRef = db.collection("DrinksHistory");
        DocumentReference dateDocumentRef = drinkHistoryRef.document("2024-02-20"); //put in todays date with the right format
        CollectionReference drinksCollectionRef = dateDocumentRef.collection("drinks");

        drinksCollectionRef.orderBy("time", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            DrinkList.clear();
            DrinkName.clear();
            DrinkVolume.clear();
            DrinkTime.clear();

            for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                String drinkName = documentSnapshot.getString("name");
                long drinkVolumeLong = documentSnapshot.getLong("volume");
                int drinkVolume = (int) drinkVolumeLong;
                Timestamp drinkTime = documentSnapshot.getTimestamp("time");
                SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
                String formattedTime = dateFormat.format(drinkTime.toDate());

                DrinkName.add(drinkName);
                DrinkVolume.add(String.valueOf(drinkVolume) + " mL");
                DrinkTime.add(formattedTime);
            }
            for(int i = 0; i<DrinkName.size(); i++){
                Drink eachDrink = new Drink(DrinkName.get(i), DrinkVolume.get(i), DrinkTime.get(i));
                DrinkList.add(eachDrink);
            }

            TodayDrinksAdapter drinksAdapter = new TodayDrinksAdapter(DrinkList);
            LinearLayoutManager lm = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(lm);
            recyclerView.setAdapter(drinksAdapter);
        }).addOnFailureListener(e -> {
            Log.e("TAG", "Error", e);
        });
    }
}
