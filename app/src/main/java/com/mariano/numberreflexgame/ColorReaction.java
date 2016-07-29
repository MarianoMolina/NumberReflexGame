package com.mariano.numberreflexgame;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;

import java.util.Random;

public class ColorReaction extends basicGameFunctionality {

    String[] availableColors;
    String[] availableColorNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_reaction);

        gameName = getResources().getString(R.string.color_reaction_game);
        instanceLenght = 1000;
        gameLenght = 60000;
        scoreMultiplier = 7;

        //Numbers game-specific logic variables
        availableColors = getResources().getStringArray(R.array.colorsInHex);
        availableColorNames = getResources().getStringArray(R.array.colorsInText);

        runOnCreate();
    }
    @Override
    public void cleanTextInterface () {
        centerRule.setText("");
        scoreDisplayed.setText(getResources().getString(R.string.starting_score));
    }

    @Override
    public void adjustClickListeners(boolean onOff){
        if (onOff) {
            centerRule.setOnClickListener(this);
            findViewById(R.id.scoreGroup).setOnClickListener(null);
            findViewById(R.id.streakGroup).setOnClickListener(null);
        }
        else {
            centerRule.setOnClickListener(null);
            findViewById(R.id.scoreGroup).setOnClickListener(this);
            findViewById(R.id.streakGroup).setOnClickListener(this);
        }
    }

    @SuppressWarnings("Range")
    @Override
    public void resetPreferences(){
        int random1 = new Random().nextInt(3);
        int random2 = new Random().nextInt(10);
        int random3 = new Random().nextInt(10);

        random3 = randomWithExclusion(random3,random2,10);

        String randomColor = "";
        String randomColorName;

        if (random1 == 0) {
            randomColor = "#" + availableColors[random2];
            randomColorName = availableColorNames[random2];
            correctView = R.id.centerRule;
        } else {
            randomColor = "#" +  availableColors[random2];
            randomColorName = availableColorNames[random3];
            correctView = 0;
        }
        centerRule.setText(randomColorName);
        centerRule.setTextColor(Color.parseColor(randomColor));
    }

    @Override
    public void mCountDownTimerInstance(long milliseconds, long interval) {
        if (instanceTimeIsRunning) {
            countDownTimer2.cancel();
        }
        instanceTimeIsRunning = true;
        countDownTimer2 = new CountDownTimer(milliseconds, interval) {
            public void onTick(long millisUntilFinished) {
                countDown2.setText(millisUntilFinished / 10 + "");
                timeLeftInstance = (int) (long) millisUntilFinished;
            }

            public void onFinish() {
                resetPreferences();
                long timeLeftTotalLong = (long) (int) (timeLeftTotal - 1000);
                mCountDownTimerTotal(timeLeftTotalLong, 10);

                /*Restarts this countdown*/
                if (timeIsRunning) {
                    countDownTimer2.start();
                } else {
                    instanceTimeIsRunning = false;
                }
                ;
            }
        }.start();
    }
}
