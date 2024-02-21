package com.example.quenchquest.Adapter;

import android.app.ActivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quenchquest.Model.Drink;
import com.example.quenchquest.databinding.LayoutTodaysdrinkitemBinding;

import java.util.List;

public class TodayDrinksAdapter extends RecyclerView.Adapter<TodayDrinksAdapter.TodaysDrinksViewHolder> {
    List<Drink> adapterDrinksList;

    public TodayDrinksAdapter(List<Drink> adapterDrinksList) {
        this.adapterDrinksList = adapterDrinksList;
    }

    public List<Drink> getAdapterDrinksList() {
        return adapterDrinksList;
    }

    public void setAdapterDrinksList(List<Drink> adapterDrinksList) {
        this.adapterDrinksList = adapterDrinksList;
    }

    @NonNull
    @Override
    public TodaysDrinksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Create item binding object
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutTodaysdrinkitemBinding binding = LayoutTodaysdrinkitemBinding.inflate(inflater, parent, false);
        //use the object, to create drinkviewholder object
        TodaysDrinksViewHolder holder = new TodaysDrinksViewHolder(binding.getRoot(), binding);
        //return viewholder object
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TodaysDrinksViewHolder holder, int position) {
        holder.holderBinding.drinkName.setText(adapterDrinksList.get(position).getDrinkType());
        holder.holderBinding.drinkVolume.setText(String.valueOf(adapterDrinksList.get(position).getDrinkVolume()));
        holder.holderBinding.drinkTime.setText(String.valueOf(adapterDrinksList.get(position).getDrinkTime()));
    }

    @Override
    public int getItemCount() {
        return adapterDrinksList.size();
    }

    public class TodaysDrinksViewHolder extends RecyclerView.ViewHolder {
        LayoutTodaysdrinkitemBinding holderBinding;

        public TodaysDrinksViewHolder(@NonNull View itemView, LayoutTodaysdrinkitemBinding holderBinding) {
            super(itemView);
            this.holderBinding = holderBinding;
        }
    }
}
