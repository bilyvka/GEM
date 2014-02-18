package se.celekt.gem_app.tabs;

import android.os.Bundle;

import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;

import jade.util.Logger;
import se.celekt.gem_app.activities.GemActivity;
import se.celekt.R;

/**
 * Created by alisa on 5/30/13.
 */
public class MapView extends SherlockFragment {

    public static final String EXTRA_TITLE = "title";

    private GoogleMap map;
    private SupportMapFragment mMapFragment;
    private Bundle mBundle;
    private FragmentTransaction fragmentTransaction;
    private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

     View map_view = inflater.inflate(R.layout.map_view, container, false);
     //container.requestTransparentRegion(map_view);

        FrameLayout frameLayout = new FrameLayout(getActivity());
        frameLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        ((ViewGroup) map_view).addView(frameLayout,
                new ViewGroup.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT));

        return map_view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(map == null){
        mMapFragment = new SupportMapFragment() {
            @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                map = mMapFragment.getMap();
                if (map != null) {
                    try {
                        MapsInitializer.initialize(this.getActivity());
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                    ((GemActivity)getActivity()).getParseKML().drawMap(map);
                }
            }
        };
        fragmentTransaction = getFragmentManager()
                .beginTransaction();
        fragmentTransaction.add(R.id.mapFragmentHole, mMapFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        }
    }

    public static Bundle createBundle( String title ) {
        Bundle bundle = new Bundle();
        bundle.putString( EXTRA_TITLE, title );
        return bundle;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (map != null) {
            ((GemActivity)getActivity()).getParseKML().drawMap(map);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //first saving my state, so the bundle wont be empty.
        //outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        try{
            fragmentTransaction.remove(mMapFragment);
            fragmentTransaction.commit();
        }catch(Exception e){
        }
        super.onDestroyView();
    }
}
