package com.mariano.numberreflexgame;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import java.util.Random;

public class MemoryGame extends basicGameFunctionality {
    int[] randomArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);

        gameName = getResources().getString(R.string.memory_game);
        instanceLenght = 4500;

        runOnCreate();
    }

    @Override
    public void resetPreferences() {
        super.resetPreferences();

        adjustClickListeners(false);

        int random1 = new Random().nextInt(9) + 1;
        int random2 = new Random().nextInt(10);
        int random3 = new Random().nextInt(10);
        int random4 = new Random().nextInt(10);
        int random5 = new Random().nextInt(10);
        int random6 = new Random().nextInt(10);
        int random7 = new Random().nextInt(10);
        int random8 = new Random().nextInt(10);
        int random10 = new Random().nextInt(8);//Position
        int random11 = new Random().nextInt(10);//New random
        int random12 = new Random().nextInt(2);//Top or bottom
        randomArray = new int[]{random1, random2, random3, random4, random5, random6, random7, random8, random10, random11, random12};

        random11 = randomWithExclusion(random11, randomArray[random10], 10);
        String correctNumber = createNumber();
        randomArray[random10] = random11;
        String wrongNumber = createNumber();

        answerBottom.setVisibility(View.GONE);
        answerTop.setVisibility(View.GONE);
        centerRule.setText(correctNumber);
        centerRule.setVisibility(View.VISIBLE);
        if (random12 == 1) {
            answerTop.setText(correctNumber);
            answerBottom.setText(wrongNumber);
            correctView = R.id.answerTop;
        } else {
            answerBottom.setText(correctNumber);
            answerTop.setText(wrongNumber);
            correctView = R.id.answerBottom;
        }
        CountDownTimer timeToViewNewNumber = new CountDownTimer(1300,100) {
            @Override
            public void onTick(long l) {
            }
            @Override
            public void onFinish() {
                answerBottom.setVisibility(View.VISIBLE);
                answerTop.setVisibility(View.VISIBLE);
                adjustClickListeners(true);
                centerRule.setVisibility(View.GONE);
            }
        }.start();
    }
    private String createNumber() {
        String loopingString ="";
        for (int i = 0; i < 8;i++) {
            if (i == 2 || i == 5) {
                loopingString = loopingString + "." + randomArray[i];
            }else {
                loopingString = loopingString + randomArray[i];
            }
        }
        return loopingString;
    };
}
