package com.example.quenchquest.Model;

import com.google.firebase.Timestamp;

import java.sql.Time;

public class Drink {

    private String DrinkType;
    private String DrinkVolume;
    private String DrinkTime;


    public Drink(String drinkType, String drinkVolume) {
        DrinkType = drinkType;
        DrinkVolume = drinkVolume;
    }

    public Drink(String drinkType, String drinkVolume, String drinkTime) {
        DrinkType = drinkType;
        DrinkVolume = drinkVolume;
        DrinkTime = drinkTime;
    }

    public String getDrinkType() {
        return DrinkType;
    }

    public void setDrinkType(String drinkType) {
        DrinkType = drinkType;
    }

    public String getDrinkVolume() {
        return DrinkVolume;
    }

    public void setDrinkVolume(String drinkVolume) {
        DrinkVolume = drinkVolume;
    }

    public String getDrinkTime() {
        return DrinkTime;
    }

    public void setDrinkTime(String drinkTime) {
        DrinkTime = drinkTime;
    }
}
