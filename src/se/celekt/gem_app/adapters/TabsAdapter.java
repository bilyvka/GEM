package se.celekt.gem_app.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import java.util.ArrayList;
import java.util.List;

import jade.util.Logger;

/**
 * Created by alisa on 5/30/13.
 */
public class TabsAdapter extends FragmentStatePagerAdapter implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

    private final SherlockFragmentActivity mActivity;
    private final ActionBar mActionBar;
    private final ViewPager mPager;
    private static final Logger myLogger = Logger.getMyLogger(TabsAdapter.class.getName());

    public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
        super(activity.getSupportFragmentManager());
        this.mActivity = activity;
        this.mActionBar = activity.getSupportActionBar();
        this.mPager = pager;

        mActionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );
    }

    private static class TabInfo {
        public final Class<? extends Fragment> fragmentClass;
        public final Bundle args;
        public TabInfo(Class<? extends Fragment> fragmentClass,
                       Bundle args) {
            this.fragmentClass = fragmentClass;
            this.args = args;
        }

        private Class<? extends Fragment> getFragmentClass() {
            return fragmentClass;
        }
    }

    private List<TabInfo> mTabs = new ArrayList<TabInfo>();

    public void addTab( CharSequence title, Class<? extends Fragment> fragmentClass, Bundle args ) {
        final TabInfo tabInfo = new TabInfo( fragmentClass, args );

        ActionBar.Tab tab = mActionBar.newTab();
        tab.setText( title );
        tab.setTabListener( this );
        tab.setTag( tabInfo );

        mTabs.add( tabInfo );

        mActionBar.addTab( tab );
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        final TabInfo tabInfo = mTabs.get( position );
        return (Fragment) Fragment.instantiate( mActivity, tabInfo.fragmentClass.getName(), tabInfo.args );
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    public void onPageScrollStateChanged(int arg0) {
    }

    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    public void onPageSelected(int position) {
        	/*
        	 * Select tab when user swiped
        	 */
        mActionBar.setSelectedNavigationItem( position );
    }

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        	/*
        	 * Slide to selected fragment when user selected tab
        	 */


        TabInfo tabInfo = (TabInfo) tab.getTag();
        myLogger.log(Logger.SEVERE, "Tab info :" + tabInfo);
        for ( int i = 0; i < mTabs.size(); i++ ) {
            if ( mTabs.get( i ) == tabInfo ) {
                mPager.setCurrentItem( i );
            }
        }
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
//       TabInfo tabInfo = (TabInfo) tab.getTag();
//
//       Fragment fragment = (Fragment) Fragment.instantiate( mActivity, tabInfo.fragmentClass.getName(), tabInfo.args );
//        if(fragment != null){
//            ft.remove(fragment);
//            ft.commit();
//        }
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

}
