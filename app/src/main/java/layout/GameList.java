package layout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mariano.numberreflexgame.ColorReaction;
import com.mariano.numberreflexgame.CountryGame;
import com.mariano.numberreflexgame.GamePlay;
import com.mariano.numberreflexgame.MathGame;
import com.mariano.numberreflexgame.MemoryGame;
import com.mariano.numberreflexgame.R;
import com.mariano.numberreflexgame.ReactionGame;
import com.mariano.numberreflexgame.RulesGame;

import java.util.Random;

public class GameList extends Fragment {

    //Button variables
    private View startButton1;
    private View startButton2;
    private View startButton3;
    private View startButton4;
    private View startButton5;
    private View startButton6;
    private View startButton7;
    private View randomGame;
    private int random;

    View fragmentRoot;

    private OnFragmentInteractionListener mListener;

    public GameList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment GameList.
     */
    // TODO: Rename and change types and number of parameters
    public static GameList newInstance() {
        GameList fragment = new GameList();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRoot = inflater.inflate(R.layout.fragment_game_list, container, false);
        createViewsClicks();
        // Inflate the layout for this fragment
        return fragmentRoot;
    }

    private void createViewsClicks (){
        // Views
        startButton1 = fragmentRoot.findViewById(R.id.numbersGame);
        startButton2 = fragmentRoot.findViewById(R.id.mathGame);
        startButton3 = fragmentRoot.findViewById(R.id.rulesGame);
        startButton4 = fragmentRoot.findViewById(R.id.populationsGame);
        startButton5 = fragmentRoot.findViewById(R.id.reactionGame);
        startButton6 = fragmentRoot.findViewById(R.id.memoryGame);
        startButton7 = fragmentRoot.findViewById(R.id.colorReaction);
        randomGame = fragmentRoot.findViewById(R.id.randomGame);

        //Game buttons listeners
        randomGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                random = new Random().nextInt(6);
                switch (random) {
                    case 0:
                        startActivity(new Intent(getActivity(), RulesGame.class));
                        break;
                    case 1:
                        startActivity(new Intent(getActivity(), CountryGame.class));
                        break;
                    case 2:
                        startActivity(new Intent(getActivity(), ReactionGame.class));
                        break;
                    case 3:
                        startActivity(new Intent(getActivity(), MathGame.class));
                        break;
                    case 4:
                        startActivity(new Intent(getActivity(), GamePlay.class));
                        break;
                    case 5:
                        startActivity(new Intent(getActivity(), MemoryGame.class));
                        break;
                }
            }
        });
        startButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), GamePlay.class));
            }
        });
        startButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MathGame.class));
            }
        });
        startButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RulesGame.class));
            }
        });
        startButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CountryGame.class));
            }
        });
        startButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ReactionGame.class));
            }
        });
        startButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MemoryGame.class));
            }
        });
        startButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ColorReaction.class));
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
}
