package layout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mariano.numberreflexgame.PlayerScores;
import com.mariano.numberreflexgame.R;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HighScoresFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HighScoresFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HighScoresFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private TextView yourScore;
    private String scoreType;
    private String activeGameName;

    NativeExpressAdView nativeAdView;
    AdRequest request;

    //HighScoreVariables
    public Map<String, Map> topScores;
    String scoreTypeExtended;
    TabLayout tablayout;

    SharedPreferences SharedPrefs;
    SharedPreferences.Editor editor;

    View fragmentRoot;
    //Login variables
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseUser userFirebase;
    private ProgressDialog mProgressDialog;
    public String userName;
    public String uid;
    GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;

    public String packageName;

    private OnFragmentInteractionListener mListener;

    public DatabaseReference mDatabase;

    public HighScoresFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HighScoresFragment newInstance() {
        HighScoresFragment fragment = new HighScoresFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRoot = inflater.inflate(R.layout.fragment_high_scores, container, false);
        packageName = "com.mariano.numberreflexgame";
        // Inflate the layout for this fragment

        //Tab layout
        tablayout = (TabLayout) fragmentRoot.findViewById(R.id.gameNamesLabels);
        tablayout.setOnTabSelectedListener(this);

        yourScore = (TextView) fragmentRoot.findViewById(R.id.yourScore);

        //Firebase Login//Firebase Login
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
                    // User is signed out
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        //Obtain Database instance
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Define Sharedprefs editor
        SharedPrefs = this.getActivity().getSharedPreferences(getString(R.string.save_file_name), 0);
        editor = SharedPrefs.edit();

        //Reset screen
        scoreType = "Score";
        activeGameName = getResources().getString(R.string.numbers_game);
        cleanInterface();

        return fragmentRoot;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        updateUIHighScore(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        updateUIHighScore(0);
    }

    private void updateUIHighScore(int activeTab) {
        switch (activeTab){
            case 0:
                activeGameName = getResources().getString(R.string.numbers_game);
                break;
            case 1:
                activeGameName = getResources().getString(R.string.math_game);
                break;
            case 2:
                activeGameName = getResources().getString(R.string.rules_game);
                break;
            case 3:
                activeGameName = getResources().getString(R.string.populations_game);
                break;
            case 4:
                activeGameName = getResources().getString(R.string.reaction_game);
                break;
            case 5:
                activeGameName = getResources().getString(R.string.memory_game);
                break;
            case 6:
                activeGameName = getResources().getString(R.string.color_reaction_game);
                break;
        }
        cleanInterface();
        getDataFromDB(activeGameName, scoreType);
        if (userName != null) {
            getOwnDataFromDB(activeGameName, scoreType);
        } else {
            if (scoreType.equals("Score")) {
                scoreTypeExtended = scoreType;
            } else {
                scoreTypeExtended = "Longest Streak";
            }
            int scoreToInput = SharedPrefs.getInt(scoreTypeExtended + " " + activeGameName, 0);
            yourScore.setText(scoreToInput + "");
        }
    }

    private void cleanInterface() {
        fragmentRoot.findViewById(R.id.spinnerView).setVisibility(View.VISIBLE);
        fragmentRoot.findViewById(R.id.recordsWrapper).setVisibility(View.GONE);
        yourScore.setText("");
        for (int i = 1; i < 11; i++) {
            int view1 = getResources().getIdentifier("namePosition" + i, "id", packageName);
            int view2 = getResources().getIdentifier("scorePosition" + i, "id", packageName);
            TextView textview1 = (TextView) fragmentRoot.findViewById(view1);
            TextView textview2 = (TextView) fragmentRoot.findViewById(view2);
            textview1.setText("");
            textview2.setText("");
        }
    }

    private void getOwnDataFromDB(String gameName, String scoreType) {
        mDatabase.child("Games").child(gameName).child(scoreType).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int highScore = 0;
                if (dataSnapshot.getValue() != null && dataSnapshot.child("score").getValue() != null) {
                    highScore = (int) (long) dataSnapshot.child("score").getValue();
                }
                yourScore.setText(highScore + "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast toast = Toast.makeText(getActivity(), "Error fetching records!", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private void getDataFromDB(String gameName, String scoreType) {
        mDatabase.child("Games").child(gameName).child(scoreType).orderByChild("score").limitToLast(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get user value

                topScores = (Map<String, Map>) dataSnapshot.getValue();
                List<PlayerScores> top = new LinkedList<PlayerScores>();

                for (Map<String, Object> score : topScores.values()) {
                    System.out.println("agregando " + score);
                    top.add(new PlayerScores((String) score.get("name"), (Long) score.get("score")));
                }
                Collections.sort(top);

                int place = 1;
                for (PlayerScores pepe : top) {
                    ((TextView) fragmentRoot.findViewById(getResources().getIdentifier("scorePosition" + place, "id", packageName))).setText("" + pepe.getScore());
                    ((TextView) fragmentRoot.findViewById(getResources().getIdentifier("namePosition" + place, "id", packageName))).setText(pepe.getName());
                    place = place + 1;
                }

                fragmentRoot.findViewById(R.id.spinnerView).setVisibility(View.GONE);
                fragmentRoot.findViewById(R.id.recordsWrapper).setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast toast = Toast.makeText(getActivity(), "Error fetching records!", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
}
