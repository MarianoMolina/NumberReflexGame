package com.mariano.numberreflexgame;

import android.os.Bundle;

import java.util.Random;

public class RulesGame extends basicGameFunctionality {

    private String[] ruleChoice;
    private String[] primeNumbers;
    private String[] nonPrimeNumbers;
    private String[] numberOptions;
    private String[] wordOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules_game);

        gameName = getResources().getString(R.string.rules_game);;
        instanceLenght = 4000;

        ruleChoice = getResources().getStringArray(R.array.ruleString);
        primeNumbers = getResources().getStringArray(R.array.primeNumberString);
        nonPrimeNumbers = getResources().getStringArray(R.array.nonPrimeNumberString);
        numberOptions = getResources().getStringArray(R.array.numbersInText);
        wordOptions = getResources().getStringArray(R.array.colorsInText);

        runOnCreate();
    }

    @Override
    public void resetPreferences() {
        super.resetPreferences();

        int randomRule = new Random().nextInt(6);
        String rightAnswer="";
        String wrongAnswer="";
        String randomRuleString = ruleChoice[randomRule];
        switch (randomRule) {
            case 0:
                rightAnswer = primeNumbers[new Random().nextInt(26)];
                wrongAnswer = nonPrimeNumbers[new Random().nextInt(26)];
                break;
            case 1:
                wrongAnswer = primeNumbers[new Random().nextInt(26)];
                rightAnswer = nonPrimeNumbers[new Random().nextInt(26)];
                break;
            case 2:
                rightAnswer = (new Random().nextInt(50) + 1) * 2 + "";
                wrongAnswer = ((new Random().nextInt(50) + 1) * 2) - 1 + "";
                break;
            case 3:
                wrongAnswer = (new Random().nextInt(50) + 1) * 2 + "";
                rightAnswer = ((new Random().nextInt(50) + 1) * 2) - 1 + "";
                break;
            case 4:
                wrongAnswer = numberOptions[new Random().nextInt(10)];
                rightAnswer = wordOptions[new Random().nextInt(10)];
                break;
            case 5:
                rightAnswer = numberOptions[new Random().nextInt(10)];
                wrongAnswer = wordOptions[new Random().nextInt(10)];
                break;
        }
        centerRule.setText(randomRuleString);
        int randomizeAnswer = new Random().nextInt(2);
        switch (randomizeAnswer) {
            case 0:
                answerTop.setText(rightAnswer);
                answerBottom.setText(wrongAnswer);
                correctView = R.id.answerTop;
                break;
            case 1:
                answerBottom.setText(rightAnswer);
                answerTop.setText(wrongAnswer);
                correctView = R.id.answerBottom;
                break;
        }
    }
}
