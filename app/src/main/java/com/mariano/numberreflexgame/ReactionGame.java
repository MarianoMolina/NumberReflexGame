package com.mariano.numberreflexgame;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class ReactionGame extends basicGameFunctionality {

    View root;
    View alternateColor;
    CountDownTimer timerDummy;
    CountDownTimer timerDummy2;
    int scoreToAdd;
    int instancesLeft;
    TextView countDown1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reaction_game);

        gameName = getResources().getString(R.string.reaction_game);
        instanceLenght = 3000;

        runOnCreate();
    }

    @Override
    public void runOnCreateGameVariables() {
        //Define id for variables
        countDown1 = (TextView) findViewById(R.id.countDown1);
        root = findViewById(R.id.rootView);
        alternateColor = findViewById(R.id.alternateColor);
        centerRule = (TextView) findViewById(R.id.centerRule);
        backToMenuButton = findViewById(R.id.backToMenuButton);
        playAgainButton = findViewById(R.id.playAgainButton);
        inviteFriends = findViewById(R.id.inviteFriends);

        //Onclicklisteners
        alternateColor.setOnClickListener(this);

        //Variables and clicklisteners
        scoreDisplayed = (TextView) findViewById(R.id.scoreDisplayed);
        streakText = (TextView) findViewById(R.id.streakText);
        scoreDisplayer = findViewById(R.id.displayScoreOverlay);
        gameStartCountDown = (TextView) findViewById(R.id.gameStartCountDown);
        totalGameLayout = findViewById(R.id.totalGameLayout);
        gameStartCountDownText = (TextView) findViewById(R.id.gameStartCountDownText);
        gameStartCountDownWrap = findViewById(R.id.gameStartCountDownWrap);
        backToMenuButton.setOnClickListener(this);
        playAgainButton.setOnClickListener(this);
        inviteFriends.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rootView:
                instancesLeft = instancesLeft - 1;
                correctView = R.id.alternateColor;
                updateScore(R.id.rootView);
                displayResult(false);
                scoreToAdd = 0;
                resetPreferences();
                break;
            case R.id.alternateColor:
                instancesLeft = instancesLeft - 1;
                correctView = R.id.alternateColor;
                updateScore(R.id.alternateColor);
                displayResult(true);
                scoreToAdd = 0;
                resetPreferences();
                break;
            case R.id.playAgainButton:
                nativeAdViewLarge.pause();
                playerWantsBack = false;
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    restartGamePlay();
                }
                break;

            case R.id.backToMenuButton:
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    playerWantsBack = true;
                } else {
                    goBackToMainMenu();
                }
                this.finish();
                break;
            case R.id.inviteFriends:
                onInviteClicked();
                break;
        }
    }
    private void displayResult(boolean correct) {
        if (correct) {
            String timeToDisplay = (5000 - scoreToAdd) + "ms";
            centerRule.setText(getResources().getString(R.string.reaction_display_time,timeToDisplay));
        }
        else {
            centerRule.setText(getResources().getString(R.string.reaction_too_soon));
        }
    }

    public void resetPreferences(){
        if (timerDummy != null ){
            timerDummy.cancel();
        }
        if (timerDummy2 != null) {
            timerDummy2.cancel();
        }

        if (instancesLeft == 0 || instancesLeft < 0) {
            displayGameOver();
            countDown1.setText(getResources().getString(R.string.reaction_chances_left,0));
        }
        else {
            adjustVisibilityOfViews("GameLayout");
            timerToColorChange();
            countDown1.setText(getResources().getString(R.string.reaction_chances_left,instancesLeft));
        }
    }
    @Override
    public void adjustClickListeners (boolean onOff){
        if (onOff) {
            findViewById(R.id.scoreGroup).setOnClickListener(null);
            findViewById(R.id.streakGroup).setOnClickListener(null);
        }
        else {
            findViewById(R.id.scoreGroup).setOnClickListener(this);
            findViewById(R.id.streakGroup).setOnClickListener(this);
        }
    }

    @Override
    public void updateScore(int clickedView) {
        if (clickedView == correctView) {
            successSound.start();
            streakLong = streakLong + 1;
            displayStreak(streakLong);
            if (longestStreak < streakLong) {
                longestStreak = streakLong;
            }
            score = score + scoreToAdd / 2 + streakLong * 100;
            scoreDisplayed.setText(getResources().getString(R.string.points, score / 100));
        } else {
            failSound.start();
            streakLong = 0;
            displayStreak(streakLong);
            score = score - 2000;
            scoreDisplayed.setText(getResources().getString(R.string.points, score / 100));
        }
    }

    public void timerToColorChange (){

        long random1 = (long) (int) new Random().nextInt(4000);
        random1 = random1 + 1000;
        CountDownTimer timerDummy3 = new CountDownTimer(1000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (instancesLeft > 0) {
                    centerRule.setText(getResources().getString(R.string.ready_to_start));
                }
            }
        }.start();

        timerDummy = new CountDownTimer(random1, 100) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                if (instancesLeft > 0) {
                    //Change background color
                    adjustVisibilityOfViews("AlternateColor");
                    centerRule.setText("");
                    timerFromColorChange();
                }
            }
        }.start();
    }

    public void timerFromColorChange (){
        timerDummy2 = new CountDownTimer(5000, 10) {

            public void onTick(long millisUntilFinished) {
                scoreToAdd = (int) (long) millisUntilFinished;
            }

            public void onFinish() {
            }
        }.start();
    }

    @Override
    public void resetScore () {
        score = 0;
        streakLong = 0;
        streakRecord = false;
        scoreRecord = false;
        instancesLeft = 10;
    }

    @Override
    public void restartGameplayReal (){
        adjustVisibilityOfViews("GameLayout");
        resetScore();
        scoreDisplayed.setText(getResources().getString(R.string.points, score / 100));
        resetPreferences();
        correctView = R.id.alternateColor;
        root.setOnClickListener(this);
    }

    @Override
    public void adjustVisibilityOfViews(String ViewToPutForward) {
        switch (ViewToPutForward) {
            case "GameLayout":
                totalGameLayout.setVisibility(View.VISIBLE);
                root.setVisibility(View.VISIBLE);
                alternateColor.setVisibility(View.GONE);
                gameStartCountDownWrap.setVisibility(View.GONE);
                scoreDisplayer.setVisibility(View.GONE);
                break;
            case "CountDown":
                alternateColor.setVisibility(View.GONE);
                totalGameLayout.setVisibility(View.GONE);
                root.setVisibility(View.VISIBLE);
                gameStartCountDownWrap.setVisibility(View.VISIBLE);
                scoreDisplayer.setVisibility(View.GONE);
                break;
            case "GameOver":
                alternateColor.setVisibility(View.GONE);
                totalGameLayout.setVisibility(View.GONE);
                root.setVisibility(View.GONE);
                gameStartCountDownWrap.setVisibility(View.GONE);
                scoreDisplayer.setVisibility(View.VISIBLE);
                break;
            case "AlternateColor":
                alternateColor.setVisibility(View.VISIBLE);
                totalGameLayout.setVisibility(View.GONE);
                root.setVisibility(View.GONE);
                gameStartCountDownWrap.setVisibility(View.GONE);
                scoreDisplayer.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void cleanTextInterface(){
        root.setOnClickListener(null);
        scoreDisplayed.setText("");
        centerRule.setText("");
    }
}
