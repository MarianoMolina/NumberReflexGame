package com.mariano.numberreflexgame;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;

import layout.GameList;
import layout.HighScoresFragment;
import layout.RulesExplanationFragment;


public class OpeningMenu extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, NavigationView.OnNavigationItemSelectedListener{

    public FragmentManager fragmentManager;
    public View fragmentHolder;
    private GameList startingFragment;
    private HighScoresFragment startingFragment2;

    //Drawer
    DrawerLayout drawer;
    Menu nav_Menu;
    NavigationView navigationView;

    //Invites variables
    private static final int REQUEST_INVITE = 0;


    public DatabaseReference mDatabase;

    //Layout variables
    public String gameToOpenHighScoresIn;

    public String packageName;

    SharedPreferences SharedPrefs;
    SharedPreferences.Editor editor;
    AdRequest request;
    NativeExpressAdView nativeAdView;
    public FirebaseAnalytics mFirebaseAnalytics;

    //Login variables
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseUser userFirebase;
    private ProgressDialog mProgressDialog;
    public String userName;
    public String uid;
    GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;
    String TAG = "Email Password";

    //MoPub
    ViewBinder viewBinder;
    MoPubStaticNativeAdRenderer adRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Facebook analytics
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //Create layout
        setContentView(R.layout.activity_opening_menu);

        //Fragment Manager and holder
        fragmentManager = getSupportFragmentManager();
        fragmentHolder = findViewById(R.id.fragmentHolder);

        packageName = "com.mariano.numberreflexgame";

        //Drawer and App bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, myToolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nav_Menu = navigationView.getMenu();

        getSupportActionBar().setTitle(getString(R.string.title_choose_game));

        //Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Obtain Database instance
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Configure Google Sign In & Google Api
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
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
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + uid);
                    userName = userFirebase.getDisplayName();
                    Log.d(TAG, "Auth onCreate openingmenu1: " + userName);

                    //Send to analytics that user loggedin
                    Bundle params = new Bundle();
                    params.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, "Google " + userName);
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, params);

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                updateUI(userFirebase);
            }
        };
        updateUI(userFirebase);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (fragmentHolder != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            Intent i = getIntent();
            boolean openHighScores = i.getBooleanExtra("Highscores",false);

            if (openHighScores) {
                startingFragment2 = new HighScoresFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentHolder, startingFragment2).commit();
            }
            else {
                startingFragment = new GameList();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentHolder, startingFragment).commit();
            }
        }

        //Define Sharedprefs editor
        SharedPrefs = getSharedPreferences(getString(R.string.save_file_name), 0);
        editor = SharedPrefs.edit();


        //Native ad
        /*nativeAdView = (NativeExpressAdView) findViewById(R.id.nativeAdView);
        request = new AdRequest.Builder()
                .addTestDevice("YOUR_DEVICE_ID")
                .build();
        nativeAdView.loadAd(request);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: Run method");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);
        return true;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_play) {
            Fragment fragment = new GameList();
            fragmentManager.beginTransaction().replace(R.id.fragmentHolder,fragment).addToBackStack(null).commit();
            getSupportActionBar().setTitle(getString(R.string.title_choose_game));
        } else if (id == R.id.nav_how_to_play) {
            Fragment fragment = new RulesExplanationFragment();
            fragmentManager.beginTransaction().replace(R.id.fragmentHolder,fragment).addToBackStack(null).commit();
            getSupportActionBar().setTitle(getString(R.string.title_how_to));
        } else if (id == R.id.nav_highscores) {
            gameToOpenHighScoresIn = getResources().getString(R.string.numbers_game);
            Fragment fragment = new HighScoresFragment();
            fragmentManager.beginTransaction().replace(R.id.fragmentHolder,fragment).addToBackStack(null).commit();
            getSupportActionBar().setTitle(getString(R.string.title_highscores));
        } else if (id == R.id.nav_login) {
            signInGoogle();
        } else if (id == R.id.nav_share) {
            onInviteClickedOpeningMenu();
        } else if (id == R.id.nav_logout){
            signOut();
        }
        else if (id == R.id.nav_rate) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.mariano.numberreflexgame")));
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        updateUI(null);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    private void updateUI(FirebaseUser user) {
        Log.d(TAG, "updateUI: Trying");
        hideProgressDialog();
        //hideSignInLayout();
        if (user != null) {
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_logout).setVisible(true);
        } else {
            nav_Menu.findItem(R.id.nav_login).setVisible(true);
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                //Send to analytics that user loggedin
                Bundle params = new Bundle();
                params.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, "Google "+ userName);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP,params);

            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(OpeningMenu.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
// [END auth_with_google]

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    // [START signin]
    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.d(TAG, "Auth trying to sign in: ");
    }
    // [END signin]

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void onInviteClickedOpeningMenu() {
        String invitationMessage = getString(R.string.invitation_message_alternate);
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title_alternate))
                .setMessage(invitationMessage)
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

}