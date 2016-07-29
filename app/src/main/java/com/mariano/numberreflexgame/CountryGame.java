package com.mariano.numberreflexgame;

import android.os.Bundle;

import java.util.Random;

public class CountryGame extends basicGameFunctionality {

    private String[] COUNTRIES;
    private int[] POPULATIONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_game);

        gameName = getResources().getString(R.string.populations_game);
        instanceLenght = 5000;

        COUNTRIES = getResources().getStringArray(R.array.countryName);
        POPULATIONS = getResources().getIntArray(R.array.countryPopulation);

        runOnCreate();
    }

    @Override
    public void resetPreferences() {
        super.resetPreferences();

        answerBottom.setText("");
        answerTop.setText("");

        int random1 = new Random().nextInt(100);
        int random2 = new Random().nextInt(100);
        int random3 = new Random().nextInt(2);

        random2 = randomWithExclusion(random2,random1,100);
        String textToDisplay;
        if (random3 == 1) {
            textToDisplay = getResources().getString(R.string.tapHighestPopulation);
            if (POPULATIONS[random1] > POPULATIONS[random2]){
                correctView = R.id.answerTop;
            }
            else {
                correctView = R.id.answerBottom;
            }
        }
        else {
            textToDisplay = getResources().getString(R.string.tapSmallestPopulation);
            if (POPULATIONS[random1] < POPULATIONS[random2]){
                correctView = R.id.answerTop;
            }
            else {
                correctView = R.id.answerBottom;
            }
        }
        answerTop.setText(COUNTRIES[random1]);
        answerBottom.setText(COUNTRIES[random2]);
        centerRule.setText(textToDisplay);
    }
}
