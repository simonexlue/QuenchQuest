package com.example.quenchquest.Interface;

public interface FragmentToActivity {
    void createDrink(String name, int volume, long time);
    void onGoalChanged(int newGoal);
}
