package com.mariano.numberreflexgame;

import android.os.Bundle;

import java.util.Random;

public class MathGame extends basicGameFunctionality {

    //Game specific variables
    String[] availableOperators;
    int[] availableModifiers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_game);

        gameName = getResources().getString(R.string.math_game);
        instanceLenght = 3000;

        availableOperators = getResources().getStringArray(R.array.operatorString);
        availableModifiers = getResources().getIntArray(R.array.modifiersMath);

        runOnCreate();
    }
    //Math game-specific logic begins
    public void resetPreferences() {
        String randomOperator = availableOperators[new Random().nextInt(3)];
        String randomOperator2 = availableOperators[new Random().nextInt(3)];
        int randomModifier = availableModifiers[new Random().nextInt(7)];
        int randomNumber1 = new Random().nextInt(21) + 1;
        int randomNumber2 = new Random().nextInt(21) + 1;
        int randomNumber3 = new Random().nextInt(2);
        int mathResult = 0;
        String mathToDisplay ="";

        if (randomOperator == "*"){
            mathToDisplay = randomNumber1 + " X " + randomNumber2;
        }
        else {
            mathToDisplay = randomNumber1 + " " + randomOperator + " " + randomNumber2;
        }

        switch (randomOperator) {
            case "+":
                mathResult = randomNumber1 + randomNumber2;
                break;
            case "-":
                mathResult = randomNumber1 - randomNumber2;
                break;
            case "*":
                mathResult = randomNumber1 * randomNumber2;
                break;
        }
        int randomAnswer = 0;
        switch (randomOperator2){
            case "+":
                randomAnswer = mathResult + randomModifier;
                break;
            case "-":
                randomAnswer = mathResult - randomModifier;
                break;
            case "*":
                randomAnswer = mathResult + randomNumber1;
                break;
        }
        centerRule.setText(mathToDisplay);

        if (randomNumber3 != 0) {
            answerTop.setText(mathResult + "");
            answerBottom.setText(randomAnswer + "");
            correctView = R.id.answerTop;
        }
        else {
            answerTop.setText(randomAnswer+"");
            answerBottom.setText(mathResult+"");
            correctView = R.id.answerBottom;
        }
    }
    //End math game-specific logic
}
