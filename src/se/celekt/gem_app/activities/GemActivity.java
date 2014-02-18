package se.celekt.gem_app.activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import se.celekt.gem_app.adapters.TabsAdapter;
import se.celekt.gem_app.dialogs.DisconnectedDialog;
import se.celekt.gem_app.dialogs.ExitDialog;
import se.celekt.gem_app.handlers.PhoneAvailableHandler;
import se.celekt.gem_app.handlers.ReadyToConnect;
import se.celekt.gem_app.jade.AsyncsTask;
import se.celekt.gem_app.jade.Configuration;
import se.celekt.gem_app.jade.ConnectAsyncTask;
import se.celekt.gem_app.jade.JChatApplication;
import se.celekt.gem_app.jade.agent.Events;
import se.celekt.gem_app.jade.agent.EventsMgr;
import se.celekt.gem_app.kml.ParseKML;
import se.celekt.gem_app.objects.ContactLocationManager;
import se.celekt.gem_app.objects.GeoNavigator;
import se.celekt.gem_app.tabs.ContactView;
import se.celekt.gem_app.tabs.DistanceView;
import se.celekt.gem_app.tabs.MapView;
import se.celekt.gem_app.util.LogSaver;
import se.celekt.R;

public class GemActivity extends SherlockFragmentActivity implements AsyncsTask {

    private ViewPager mViewPager;
    private TabsAdapter adapter;
    private Configuration jadeConfiguration;
    private ParseKML mParseKML;
    private  static JChatApplication app;

    public GemActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //create logs
        LogSaver.getInstance().createLogs();

        setTheme(R.style.Theme_Sherlock_Light);
        mViewPager = new ViewPager(this);
        adapter = new TabsAdapter( this, mViewPager );
        mViewPager.setAdapter(adapter );
        mViewPager.setOnPageChangeListener( adapter );
        mViewPager.setId( 0x7F04FFF0 );
        super.onCreate(savedInstanceState);

        app = (JChatApplication) getApplication();
        
        
        setTitle(ActivityKnowledge.getInstance().getName());

        //mViewPager.requestTransparentRegion(mViewPager);
        mViewPager.setOffscreenPageLimit(0);
        setContentView(mViewPager);

        EventsMgr.getInstance().registerEvent(Events.TAB_PRESENTED, new ReadyToConnect(this));
        
        /* Adding tabs to the app and registering events that may modify information in the tabs */
        addTab("Contacts", ContactView.class, ContactView.createBundle("Contacts"));

        addTab("Distances", DistanceView.class, DistanceView.createBundle("Distances"));

        addTab("Map", MapView.class, MapView.createBundle("Map") );
        mParseKML = new ParseKML("https://maps.google.se/maps/ms?msid=204752890186201010867.0004de63b7b932201cfdc&msa=0&ll=35.574683,138.411255&spn=1.242067,2.526855&output=kml");
    }


    @Override
    protected void onStart() {
        super.onStart();

        /*
        Register ADDITIONAL events (ALL)
        The previous 3 tab declarations have internal events that have been registered in their classes
        new Events to register.
        */
        new PhoneAvailableHandler();
        
        //presentation of spinning wheel for waiting for connection
        //TODO
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.exit:
                showExitDialog();
                break;
        }
        return true;
    }
   

    protected void addTab(CharSequence title, Class<? extends Fragment> fragmentClass, Bundle args ) {
        adapter.addTab( title, fragmentClass, args );
    }

    public void readytoConnect(){
        //Reading the configuration settings to be able to open the Jade connection afterwards
        setupJadeConfiguration();
        //Connect to Jade
        establishJadeConnection();
    }
    
    public void onComplete() {
        //finish the spinning wheel for waiting for connection
        //TODO
    }

    public ParseKML getParseKML() {
        return mParseKML;
    }

    //**************   JADE related Methods *********************
    /***
     *
     */
    public void setupJadeConfiguration(){
        jadeConfiguration = new Configuration(getApplication(),this);
        ContactLocationManager.getInstance().setupLocation(app.getProperty(JChatApplication.LOCATION_PROVIDER));
        if(!app.getProperty(JChatApplication.LOCATION_PROVIDER).equals("simulator")){
            GeoNavigator.setLocationProviderName(app.getProperty(JChatApplication.LOCATION_PROVIDER));
            GeoNavigator.getInstance(this).startLocationUpdate();
        }
    }

    /***
     *
     */
    public void establishJadeConnection(){
        new ConnectAsyncTask(this).execute(jadeConfiguration);
    }

    //***********   Message presentation methods *******************
    public void showExitDialog(){
        FragmentManager fm = getSupportFragmentManager();
        ExitDialog exitDialog = new ExitDialog();
        exitDialog.show(fm, "fragment_exit");
    }

    public void showDisconnectedDialog(String message){
        FragmentManager fm = getSupportFragmentManager();
        DisconnectedDialog simpleDialog = new DisconnectedDialog(message);
        simpleDialog.show(fm, "show_popup");

    }

    //***********   Disable/Enable view *******************
    public void disableView(){
       mViewPager.setEnabled(false);

    }
    public void enableView(){
        mViewPager.setEnabled(true);
    }
}
