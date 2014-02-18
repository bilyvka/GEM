package se.celekt.gem_app.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import se.celekt.R;

import se.celekt.gem_app.dialogs.ExitDialog;
import se.celekt.gem_app.dialogs.SimpleDialog;
import se.celekt.gem_app.jade.AsyncsTask;
import se.celekt.gem_app.jade.Configuration;
import se.celekt.gem_app.jade.ConnectAsyncTask;
import se.celekt.gem_app.jade.JChatApplication;
import se.celekt.gem_app.jade.JadeParameterDialog;
import se.celekt.gem_app.objects.ContactLocationManager;
import se.celekt.gem_app.objects.GeoNavigator;

/**
 * Created by alisa on 6/3/13.
 */
public class Connect_MAS extends SherlockFragmentActivity  {

    private  JadeParameterDialog parameterDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock_Light);
        super.onCreate(savedInstanceState);
        parameterDialog = new JadeParameterDialog(this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                parameterDialog.show();
                break;
            case R.id.exit:
                showExitDialog();
                break;
            case R.id.connect:
                     showUI();     // fill Jade connection properties
                 break;
        }
        return true;
    }

    public void showExitDialog(){
        FragmentManager fm = getSupportFragmentManager();
        ExitDialog exitDialog = new ExitDialog();
        exitDialog.show(fm, "fragment_exit");
    }



    public void showUI(){
    	//define the name
    	JChatApplication app = (JChatApplication) getApplication();
    	ActivityKnowledge.getInstance().setName(app.getProperty(JChatApplication.PREFERENCE_PHONE_NUMBER));
    	
    	//open GemActivity
        Intent myIntent = new Intent(Connect_MAS.this, GemActivity.class);
        Connect_MAS.this.startActivity(myIntent);
        closeActivity();
    }

    public void closeActivity(){

        finish();
    }



    public void showDialog(String message){
        FragmentManager fm = getSupportFragmentManager();
        SimpleDialog simpleDialog = new SimpleDialog(message);
        simpleDialog.show(fm, "show_popup");
    }
}
