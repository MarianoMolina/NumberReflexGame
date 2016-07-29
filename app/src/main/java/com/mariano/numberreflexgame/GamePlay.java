package com.mariano.numberreflexgame;

import android.graphics.Color;
import android.os.Bundle;

import java.util.Random;

public class GamePlay extends basicGameFunctionality {
    //Numbers game-specific logic variables
    String[] availableColors;
    String[] availableColorNames;
    String[] availableNumbers;
    String[] availableNumbersText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        gameName = getResources().getString(R.string.numbers_game);
        instanceLenght = 2000;

        //Numbers game-specific logic variables
        availableColors = getResources().getStringArray(R.array.colorsInHex);
        availableColorNames = getResources().getStringArray(R.array.colorsInText);
        availableNumbers = getResources().getStringArray(R.array.numbersInInt);
        availableNumbersText = getResources().getStringArray(R.array.numbersInText);

        runOnCreate();
    }
    //Numbers game-specific logic
    public void resetPreferences () {

        int random1 = new Random().nextInt(2);
        int random2 = new Random().nextInt(10);
        int random3 = new Random().nextInt(10);
        int random4 = new Random().nextInt(10);

        String randomColor;
        String randomNumber;
        String currentOptionColor;
        String currentOptionNumber;

        if (random1 != 0) {
            randomColor = availableColorNames[random2];
            randomNumber = availableNumbersText[random3];
            currentOptionColor = "#" + availableColors[random2];
            currentOptionNumber = availableNumbers[randomWithExclusion(random4,random3,10)];

            correctView = R.id.answerTop;

            answerTop.setText(randomColor);
            answerBottom.setText(randomNumber);
            centerRule.setTextColor(Color.parseColor(currentOptionColor));
            centerRule.setText(currentOptionNumber);
        }
        else {
            randomColor = availableColorNames[random2];
            randomNumber = availableNumbersText[random3];
            currentOptionColor = "#" + availableColors[randomWithExclusion(random4,random2,10)];
            currentOptionNumber = availableNumbers[random3];

            correctView = R.id.answerBottom;

            answerTop.setText(randomColor);
            answerBottom.setText(randomNumber);
            centerRule.setTextColor(Color.parseColor(currentOptionColor));
            centerRule.setText(currentOptionNumber);
        }
    }
    //Numbers game-specific logic ends

}
