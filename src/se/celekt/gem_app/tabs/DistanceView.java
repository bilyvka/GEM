package se.celekt.gem_app.tabs;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import se.celekt.R;
import se.celekt.gem_app.activities.GemActivity;
import se.celekt.gem_app.adapters.AttemptAdapter;
import se.celekt.gem_app.objects.Attempt;
import se.celekt.gem_app.views.AttemptView;

/**
 * Created by alisa on 5/30/13.
 */
public class DistanceView extends ListFragment {
    public static final String EXTRA_TITLE = "title";

    private AttemptAdapter mAttemptAdapter;
    private List<Attempt>  mAttempts;
    private AttemptView mAttemptView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAttempts = new ArrayList<Attempt>();
        mAttemptAdapter = new AttemptAdapter(this.getActivity(),R.id.atttempt_list,mAttempts);
        mAttemptView = new AttemptView(mAttemptAdapter,((GemActivity)getActivity()).getParseKML().getPoints());
        View header =  getActivity().getLayoutInflater().inflate(R.layout.header_attempt_list, null);
        getListView().addHeaderView(header);

        setListAdapter(mAttemptAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        setListAdapter(mAttemptAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        setListAdapter(mAttemptAdapter);
    }

    public static Bundle createBundle( String title ) {
        Bundle bundle = new Bundle();
        bundle.putString( EXTRA_TITLE, title );
        return bundle;
    }
    @Override public void onSaveInstanceState(Bundle outState) {
        //first saving my state, so the bundle wont be empty.
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE"); super.onSaveInstanceState(outState);
    }
}
