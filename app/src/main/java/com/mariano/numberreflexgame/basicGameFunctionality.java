package com.mariano.numberreflexgame;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class basicGameFunctionality extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    public long instanceLenght;
    public long gameLenght = 30000;
    public String gameName;

    //AppRater
    AppRater newAppRater;
    //Database
    public DatabaseReference mDatabase;

    //Highscores variables
    public int highScore;
    public int longestStreakEver;

    //Shared Prefs variables
    SharedPreferences SharedPrefs;
    SharedPreferences.Editor editor;

    public int scoreMultiplier = 1;

    //Game TextView variables
    TextView answerTop;
    TextView answerBottom;
    TextView centerRule;

    View mainGameLayout;
    TextView gameStartCountDown;
    TextView gameStartCountDownText;
    View gameStartCountDownWrap;
    View totalGameLayout;

    TextView subCongratulatoryText;
    TextView congratulatoryText;

    //Countdown variables
    TextView countDown1;
    TextView countDown2;
    CountDownTimer countDownTimer2;
    CountDownTimer countDownTimer1;
    int timeLeftInstance;
    int timeLeftTotal;
    boolean timeIsRunning = false;
    boolean instanceTimeIsRunning;
    boolean missedLastAttemp = false;

    //Score variables
    int score;
    TextView scoreDisplayed;
    int correctView;
    int streakLong;
    TextView streakText;
    int longestStreak = 0;
    View scoreDisplayer;
    boolean scoreRecord = false;
    boolean streakRecord = false;

    //Ad variables
    NativeExpressAdView nativeAdView;
    NativeExpressAdView nativeAdViewLarge;
    InterstitialAd mInterstitialAd;
    AdRequest request;
    int remainingHeight;
    FrameLayout nativeAdContainer;

    //Firebase
    public FirebaseAnalytics mFirebaseAnalytics;

    //Sound Variables
    MediaPlayer successSound;
    MediaPlayer failSound;

    //Other button variables
    Boolean playerWantsBack = false;
    View backToMenuButton;
    View playAgainButton;
    View inviteFriends;

    //Login variables
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseUser userFirebase;
    private ProgressDialog mProgressDialog;
    public String userName;
    public String uid;

    //Invites variables
    private static final String TAG = GamePlay.class.getSimpleName();
    private static final int REQUEST_INVITE = 0;

    public GoogleApiClient mGoogleApiClient;

    public void runOnCreate() {
        //Define Sharedprefs editor
        SharedPrefs = getSharedPreferences(getString(R.string.save_file_name), 0);
        editor = SharedPrefs.edit();

        //Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Obtain Database instance
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Media variables
        successSound = MediaPlayer.create(this, R.raw.success);
        failSound = MediaPlayer.create(this, R.raw.buzzer);

        newAppRater = new AppRater(this);
        newAppRater.setDaysBeforePrompt(0);
        newAppRater.setLaunchesBeforePrompt(4);
        newAppRater.setPhrases(R.string.rate_text_title, R.string.rate_text_explanation, R.string.rate_text_now, R.string.rate_text_later, R.string.rate_text_never);

        runOnCreateGameVariables();

        //Reset record variables
        resetScore();

        //Start game
        restartGamePlay();
        //End restart game

        //Start Ads logic
        //Native ad
        nativeAdView = (NativeExpressAdView) findViewById(R.id.nativeAdView);
        request = new AdRequest.Builder()
                .addTestDevice("YOUR_DEVICE_ID")
                .build();
        nativeAdView.loadAd(request);

        //Large native ad
        nativeAdViewLarge = new NativeExpressAdView(this);
        nativeAdViewLarge.setAdSize(new AdSize(AdSize.FULL_WIDTH, 250));
        nativeAdViewLarge.setAdUnitId("ca-app-pub-5446158020626099/2248753509");
        // Add the NativeExpressAdView to the view hierarchy.
        nativeAdContainer = (FrameLayout) findViewById(R.id.nativeAdContainer);
        nativeAdContainer.addView(nativeAdViewLarge);

        //Interstitial ad
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.Interstitial_ad_unit));
        setUpInterstitialCloseListener();
        requestNewInterstitial();
        //End ads logic

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Firebase Login
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                userFirebase = firebaseAuth.getCurrentUser();
                if (userFirebase != null) {
                    // User is signed in
                    uid = userFirebase.getUid();
                    userName = userFirebase.getDisplayName();

                } else {
                    // User is signed out... DO SOMETHING HERE
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    public void runOnCreateGameVariables (){
        //Variables and clicklisteners
        mainGameLayout = findViewById(R.id.mainGameLayout);
        countDown1 = (TextView) findViewById(R.id.countDown1);
        countDown2 = (TextView) findViewById(R.id.countDown2);
        scoreDisplayed = (TextView) findViewById(R.id.scoreDisplayed);
        streakText = (TextView) findViewById(R.id.streakText);
        scoreDisplayer = findViewById(R.id.displayScoreOverlay);
        gameStartCountDown = (TextView) findViewById(R.id.gameStartCountDown);
        totalGameLayout = findViewById(R.id.totalGameLayout);
        gameStartCountDownText = (TextView) findViewById(R.id.gameStartCountDownText);
        gameStartCountDownWrap = findViewById(R.id.gameStartCountDownWrap);

        backToMenuButton = findViewById(R.id.backToMenuButton);
        playAgainButton = findViewById(R.id.playAgainButton);
        inviteFriends = findViewById(R.id.inviteFriends);
        answerTop = (TextView) findViewById(R.id.answerTop);
        answerBottom = (TextView) findViewById(R.id.answerBottom);
        centerRule = (TextView) findViewById(R.id.centerRule);

        backToMenuButton.setOnClickListener(this);
        playAgainButton.setOnClickListener(this);
        inviteFriends.setOnClickListener(this);
        //End Variables and clicklisteners
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    //Finds if there is a connection
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    //CountDownTimer logic begins
    public void mCountDownTimerTotal(long milliseconds, long interval) {
        if (timeIsRunning) {
            countDownTimer1.cancel();
        }
        timeIsRunning = true;
        countDownTimer1 = new CountDownTimer(milliseconds, interval) {
            public void onTick(long millisUntilFinished) {
                countDown1.setText(millisUntilFinished / 1000 + "s");
                timeLeftTotal = (int) (long) millisUntilFinished;
            }

            public void onFinish() {
                countDown1.setText(getResources().getString(R.string.Times_up));
                countDownTimer2.cancel();
                timeIsRunning = false;

                //Shut down listeners
                adjustClickListeners(false);

                //Display end of game message
                displayGameOver();
            }
        }.start();
    }

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
                if (streakLong != 0) {
                    streakLong = 0;
                    displayStreak(streakLong);
                }
                ;
            }
        }.start();
    }
    //Countdowntimer logic ends

    //Restart Game
    public void resetPreferences() {
    }

    //Update displayed score
    public void updateScore(int clickedView) {
        if (clickedView == correctView) {
            successSound.start();
            streakLong = streakLong + 1;
            displayStreak(streakLong);
            if (longestStreak < streakLong) {
                longestStreak = streakLong;
            }
            score = score + (timeLeftInstance + streakLong * 100) * scoreMultiplier;
            scoreDisplayed.setText(getResources().getString(R.string.points, score / 100));
            int timeToAdd;
            if (timeLeftInstance < 1) {
                timeToAdd = 0;
            } else {
                timeToAdd = (timeLeftInstance - 1000) / 2;
            }
            long timeLeftTotalLong = (long) (int) (timeLeftTotal + timeToAdd);
            mCountDownTimerTotal(timeLeftTotalLong, 10);
        } else {
            failSound.start();
            streakLong = 0;
            displayStreak(streakLong);
            score = score - ((int) (long) instanceLenght) * scoreMultiplier;
            scoreDisplayed.setText(getResources().getString(R.string.points, score / 100));
        }
    }

    public void displayStreak(int streak) {
        streakText.setText("");
        String streakString;
        if (streakLong == 0) {
            if (missedLastAttemp) {
                streakString = getResources().getString(R.string.wrong_again);
            } else {
                streakString = getResources().getString(R.string.wrong);
                missedLastAttemp = true;
            }
        } else if (streakLong == 1) {
            streakString = getResources().getString(R.string.correct);
            missedLastAttemp = false;
        } else if (streakLong == 10 || streakLong == 20 || streakLong == 30) {
            streakString = getResources().getString(R.string.correct_tens_of_times, streakLong);
            missedLastAttemp = false;
        } else {
            streakString = getResources().getString(R.string.correct_many_times, streakLong);
            missedLastAttemp = false;
        }
        streakText.setText(streakString);
        CountDownTimer hideStreakText = new CountDownTimer(1000, 10) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                streakText.setText("");
            }
        }.start();
    }

    //End game logic
    //Game over message
    public void displayGameOver(/*boolean askToSignIn*/) {

        //Save event to Firebase Analytics
        Bundle bundle = new Bundle();
        bundle.putLong(FirebaseAnalytics.Param.SCORE, (long) (int) score);
        bundle.putLong(FirebaseAnalytics.Param.VALUE, (long) (int) longestStreak);
        bundle.putString(FirebaseAnalytics.Param.CHARACTER, gameName);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.POST_SCORE, bundle);

        //Custom score saving and highscore calculations
        Log.d(TAG, "displayGameOver: "+highScore+gameName+SharedPrefs);
        highScore = SharedPrefs.getInt("Score " + gameName, 0);
        longestStreakEver = SharedPrefs.getInt("Longest Streak " + gameName, 0);
        if (highScore < score / 100) {
            editor.putInt("Score " + gameName, score / 100);
            editor.apply();
            scoreRecord = true;
            highScore = score / 100;
        } else {
            scoreRecord = false;
        }
        if (longestStreakEver < longestStreak) {
            editor.putInt("Longest Streak " + gameName, longestStreak);
            editor.apply();
            streakRecord = true;
            longestStreakEver = longestStreak;
        } else {
            streakRecord = false;
        }

        if (uid != null) {
            updateScoreDB("Score", uid, userName, highScore);
            updateScoreDB("Streak",uid, userName, longestStreakEver);
        }

        congratulatoryText = (TextView) findViewById(R.id.congratulatoryText);
        subCongratulatoryText = (TextView) findViewById(R.id.subCongratulatoryText);
        String title;
        String subtitle;
        if (scoreRecord || streakRecord) {
            title = (getResources().getStringArray(R.array.title_congratulations))[new Random().nextInt(5)];
            subtitle = (getResources().getStringArray(R.array.subtitle_congratulations))[new Random().nextInt(5)];
        } else {
            title = (getResources().getStringArray(R.array.title_non_congratulations))[new Random().nextInt(5)];
            subtitle = (getResources().getStringArray(R.array.subtitle_non_congratulations))[new Random().nextInt(5)];
        }
        congratulatoryText.setText(title);
        subCongratulatoryText.setText(subtitle);
        TextView scoreEndGame = (TextView) findViewById(R.id.scoreEndGame);
        TextView streakEndGame = (TextView) findViewById(R.id.streakEndGame);
        TextView scoreNumberEndGame = (TextView) findViewById(R.id.scoreNumberEndGame);
        TextView streakNumberEndGame = (TextView) findViewById(R.id.streakNumberEndGame);

        if (scoreRecord) {
            scoreEndGame.setText(getResources().getString(R.string.new_record_score));
            scoreNumberEndGame.setText(score / 100 + "");
        } else {
            scoreEndGame.setText(getResources().getString(R.string.non_record_score));
            scoreNumberEndGame.setText(score / 100 + "");
        }
        if (streakRecord) {
            streakEndGame.setText(getResources().getString(R.string.new_record_streak));
            streakNumberEndGame.setText(longestStreak + "");
        } else {
            streakEndGame.setText(getResources().getString(R.string.non_record_streak));
            streakNumberEndGame.setText(longestStreak + "");
        }

        endOfGameMessage ();
        streakLong = 0;
        adjustClickListeners(false);

        nativeAdView.pause();
        nativeAdViewLarge.loadAd(request);
        nativeAdViewLarge.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        });
        nativeAdViewLarge.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                nativeAdViewLarge.setVisibility(View.GONE);
            }
        });
    }
    public void endOfGameMessage (){
        cleanTextInterface();
        adjustVisibilityOfViews("GameLayout");
        Log.d(TAG, "endOfGameMessage: "+centerRule);
        centerRule.setText(getResources().getString(R.string.game_over_message));
        CountDownTimer countDownMessage = new CountDownTimer(1500,500) {
            @Override
            public void onTick(long l) {;
                centerRule.setText(getResources().getString(R.string.game_over_message));
            }
            @Override
            public void onFinish() {
                cleanTextInterface();
                adjustVisibilityOfViews("GameOver");

                newAppRater.show();
            }
        }.start();
    }
    //End game over message

    //New interstitial request
    public void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();
        mInterstitialAd.loadAd(adRequest);
    }
    public String visibleGameName (String gameName){
        String visibleName = "";
        switch (gameName) {
            case "Math Game":
                visibleName = getString(R.string.math_game_button);
                break;
            case "Populations Game":
                visibleName = getString(R.string.populations_game_button);
                break;
            case "Numbers Game":
                visibleName = getString(R.string.numbers_game_button);
                break;
            case "Rules Game":
                visibleName = getString(R.string.rules_game_button);
                break;
            case "Reaction Game":
                visibleName = getString(R.string.reaction_game_button);
                break;
            case "Memory Game":
                visibleName = getString(R.string.memory_game_button);
                break;
            case "Color Reaction Game":
                visibleName = getString(R.string.color_reaction_game_button);
                break;
        }
        return visibleName;
    }

    public void onInviteClicked() {
        Log.d(TAG, "onInviteClicked: "+visibleGameName(gameName));
        String invitationMessage = getString(R.string.invitation_message,score / 100, visibleGameName(gameName));
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(invitationMessage)
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        int numberOfInvitations = 0;
        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                numberOfInvitations = ids.length;
                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Inviting failed! Please try again", Toast.LENGTH_LONG);
                toast.show();
            }
        }
        //Save event to Firebase Analytics
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Firebase Invite");
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, numberOfInvitations + "");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);
    }

    //Catch clicked views
    @Override
    public void onClick(View v) {
        int colorToUse1;
        int colorToUse2;
        if (gameName.equals(getResources().getString(R.string.numbers_game))) {
            colorToUse1 = ContextCompat.getColor(this, R.color.blue_grey_500);
            colorToUse2 = ContextCompat.getColor(this, R.color.blue_grey_700);
        } else {
            colorToUse1 = ContextCompat.getColor(this, R.color.colorAccentLight);
            colorToUse2 = ContextCompat.getColor(this, R.color.colorAccent);
        }

        final int colorToUse2Object = colorToUse2;

        CountDownTimer topSelected = new CountDownTimer(20, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                answerTop.setTextColor(colorToUse2Object);
            }
        };

        CountDownTimer bottomSelected = new CountDownTimer(20, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                answerBottom.setTextColor(colorToUse2Object);
            }
        };

        switch (v.getId()) {
            case R.id.playAgainButton:
                nativeAdViewLarge.pause();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    playerWantsBack = false;
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
            case R.id.answerTop:
                updateScore(R.id.answerTop);
                resetPreferences();
                mCountDownTimerInstance(instanceLenght, 10);
                answerTop.setTextColor(colorToUse1);
                topSelected.start();
                break;
            case R.id.answerBottom:
                updateScore(R.id.answerBottom);
                resetPreferences();
                mCountDownTimerInstance(instanceLenght, 10);
                answerBottom.setTextColor(colorToUse1);
                bottomSelected.start();
                break;
            case R.id.centerRule:
                updateScore(R.id.centerRule);
                resetPreferences();
                mCountDownTimerInstance(instanceLenght, 10);
                break;
            case R.id.scoreGroup:
                Intent i = new Intent(this, OpeningMenu.class);
                i.putExtra("Highscores", true);
                startActivity(i);
                break;
            case R.id.streakGroup:
                i = new Intent(this, OpeningMenu.class);
                i.putExtra("Highscores", true);
                startActivity(i);
                break;
        }
    }

    public int randomWithExclusion(int number1, int number2, int sizeOfRandom) {
        while (number1 == number2) {
            number1 = new Random().nextInt(sizeOfRandom);
        }
        return number1;
    }

    //Interstitial close listener
    public void setUpInterstitialCloseListener() {
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                //Restart game
                if (playerWantsBack) {
                    goBackToMainMenu();
                } else {
                    restartGamePlay();
                }
            }
        });
    }

    public void resetScore () {
        score = 0;
        streakLong = 0;
        streakRecord = false;
        scoreRecord = false;
    }

    public void restartGamePlay() {
        adjustVisibilityOfViews("CountDown");
        resetScore();
        cleanTextInterface();
        startGameCountDown();
    }

    public void startGameCountDown (){
        CountDownTimer countDownToStart = new CountDownTimer(5000, 1000) {
            int timeLeft = 3;

            @Override
            public void onTick(long millisUntilFinished) {
                String stringToDisplay = "";
                switch (gameName) {
                    case "Math Game":
                        stringToDisplay = getResources().getString(R.string.pre_math_game);
                        break;
                    case "Numbers Game":
                        stringToDisplay = getResources().getString(R.string.pre_numbers_game);
                        break;
                    case "Rules Game":
                        stringToDisplay = getResources().getString(R.string.pre_rules_game);
                        break;
                    case "Populations Game":
                        stringToDisplay = getResources().getString(R.string.pre_country_game);
                        break;
                    case "Reaction Game":
                        stringToDisplay = getResources().getString(R.string.pre_reaction_game);
                        break;
                    case "Memory Game":
                        stringToDisplay = getResources().getString(R.string.pre_memory_game);
                        break;
                    case "Color Reaction Game":
                        stringToDisplay = getResources().getString(R.string.pre_color_reaction_game);
                        break;
                }
                switch (timeLeft) {
                    case 3:
                        gameStartCountDown.setText(timeLeft + "");
                        gameStartCountDown.setTextSize(TypedValue.COMPLEX_UNIT_SP, 150);
                        gameStartCountDownText.setText(stringToDisplay);
                        break;
                    case 2:
                        gameStartCountDown.setText(timeLeft + "");
                        gameStartCountDown.setTextSize(TypedValue.COMPLEX_UNIT_SP, 150);
                        gameStartCountDownText.setText(stringToDisplay);
                        break;
                    case 1:
                        gameStartCountDown.setText(timeLeft + "");
                        gameStartCountDown.setTextSize(TypedValue.COMPLEX_UNIT_SP, 150);
                        break;
                    case 0:
                        gameStartCountDown.setText(getResources().getString(R.string.ready_to_start));
                        gameStartCountDown.setTextSize(TypedValue.COMPLEX_UNIT_SP, 90);
                        break;
                }
                timeLeft = timeLeft - 1;
            }

            @Override
            public void onFinish() {
                gameStartCountDown.setText("");
                gameStartCountDown.setTextSize(TypedValue.COMPLEX_UNIT_SP, 150);
                gameStartCountDownText.setText("");
                restartGameplayReal();
            }
        }.start();
    }

    public void cleanTextInterface() {
        centerRule.setText("");
        answerBottom.setText("");
        answerTop.setText("");
        scoreDisplayed.setText(getResources().getString(R.string.starting_score));
    }

    public void restartGameplayReal() {
        adjustVisibilityOfViews("GameLayout");
        nativeAdView.resume();
        resetCountDowns(gameLenght,instanceLenght);
        adjustClickListeners(true);
        resetPreferences();
    }

    public void resetCountDowns (long totalLong, long instanceLong) {
        mCountDownTimerTotal(totalLong, 10);
        mCountDownTimerInstance(instanceLong, 10);
    }

    public void adjustClickListeners (boolean onOff){
        if (onOff) {
            answerTop.setOnClickListener(this);
            answerBottom.setOnClickListener(this);
            findViewById(R.id.scoreGroup).setOnClickListener(null);
            findViewById(R.id.streakGroup).setOnClickListener(null);
        }
        else {
            answerTop.setOnClickListener(null);
            answerBottom.setOnClickListener(null);
            findViewById(R.id.scoreGroup).setOnClickListener(this);
            findViewById(R.id.streakGroup).setOnClickListener(this);
        }
    }

    public void adjustVisibilityOfViews (String ViewToPutForward) {
        switch (ViewToPutForward) {
            case "GameLayout":
                totalGameLayout.setVisibility(View.VISIBLE);
                gameStartCountDownWrap.setVisibility(View.GONE);
                scoreDisplayer.setVisibility(View.GONE);
                break;
            case "CountDown":
                totalGameLayout.setVisibility(View.GONE);
                gameStartCountDownWrap.setVisibility(View.VISIBLE);
                scoreDisplayer.setVisibility(View.GONE);
                break;
            case "GameOver":
                totalGameLayout.setVisibility(View.GONE);
                gameStartCountDownWrap.setVisibility(View.GONE);
                scoreDisplayer.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void updateScoreDB(final String scoreType, final String uid, final String name, final int scoreValue) {
        mDatabase.child("Games").child(gameName).child(scoreType).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get user value
                int userHighScore;
                Log.d(TAG, "onDataChange: "+dataSnapshot.getValue());
                if (dataSnapshot.getValue() != null && dataSnapshot.child("score").getValue() != null)  {
                    userHighScore = (int) (long) dataSnapshot.child("score").getValue();
                }
                else {
                    userHighScore = 0;
                }

                if (scoreValue > userHighScore) {
                    mDatabase.child("Games").child(gameName).child(scoreType).child(uid).child("score").setValue(scoreValue);
                    mDatabase.child("Games").child(gameName).child(scoreType).child(uid).child("name").setValue(name);
                }
                if (scoreType.equals("Score") && highScore > userHighScore) {
                    //If the locally stored highscore is higher than the one in the DB, we overwrite to save offline progress
                    mDatabase.child("Games").child(gameName).child("Score").child(uid).child("score").setValue(highScore);
                    mDatabase.child("Games").child(gameName).child("Streak").child(uid).child("name").setValue(name);
                }
                else if (scoreType.equals("Streak") && longestStreakEver > userHighScore) {
                    mDatabase.child("Games").child(gameName).child("Streak").child(uid).child("score").setValue(longestStreakEver);
                    mDatabase.child("Games").child(gameName).child("Streak").child(uid).child("name").setValue(name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast toast = Toast.makeText(basicGameFunctionality.this, "Error fetching records!", Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }
    public int calculateRemainingSpace (int DPsToDiscount){
        //Calculate size of screen (currently useless)
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;

        remainingHeight = (int) (long) dpHeight;
        remainingHeight = remainingHeight - DPsToDiscount;
        return remainingHeight;
    }

    public void goBackToMainMenu() {
        startActivity(new Intent(this, OpeningMenu.class));
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBackToMainMenu();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
