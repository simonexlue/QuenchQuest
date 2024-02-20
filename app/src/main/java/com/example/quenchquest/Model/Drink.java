package com.example.quenchquest.Model;

public class Drink {

    private String DrinkType;
    private int DrinkVolume;

    public Drink(String drinkType, int drinkVolume) {
        DrinkType = drinkType;
        DrinkVolume = drinkVolume;
    }

    public String getDrinkType() {
        return DrinkType;
    }

    public void setDrinkType(String drinkType) {
        DrinkType = drinkType;
    }

    public int getDrinkVolume() {
        return DrinkVolume;
    }

    public void setDrinkVolume(int drinkVolume) {
        DrinkVolume = drinkVolume;
    }
}
