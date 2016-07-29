package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mariano.numberreflexgame.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RulesExplanationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RulesExplanationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RulesExplanationFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    ImageView rulesImageDisplayed;
    int rulesImagePosition = 1;
    View fragmentRoot;

    public RulesExplanationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RulesExplanationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RulesExplanationFragment newInstance(String param1, String param2) {
        RulesExplanationFragment fragment = new RulesExplanationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentRoot = inflater.inflate(R.layout.fragment_rules_explanation, container, false);
        fragmentRoot.findViewById(R.id.previousRuleButton).setOnClickListener(this);
        fragmentRoot.findViewById(R.id.nextRuleButton).setOnClickListener(this);
        rulesImageDisplayed = (ImageView) fragmentRoot.findViewById(R.id.rulesImageDisplayed);
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
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.previousRuleButton:
                rotateRulesImages(false);
                break;
            case R.id.nextRuleButton:
                rotateRulesImages(true);
                break;
        }
    }
    private void rotateRulesImages (boolean forward) {
        if (forward) {
            if (rulesImagePosition == 4) {
                rulesImagePosition = 1;
            } else {
                rulesImagePosition = rulesImagePosition + 1;
            }
        }
        else {
            if (rulesImagePosition == 1) {
                rulesImagePosition = 4;
            } else {
                rulesImagePosition = rulesImagePosition - 1;
            }
        }
        int id = getResources().getIdentifier("com.mariano.numberreflexgame:drawable/rules_" + rulesImagePosition,null,null);
        rulesImageDisplayed.setImageResource(id);
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
