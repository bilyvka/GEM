package se.celekt.gem_app.objects;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import jade.util.Logger;

/**
 * Created by alisa on 6/8/13.
 */
public class ContactLocationManager implements LocationListener {

    private static final Logger myLogger = Logger.getMyLogger(ContactLocationManager.class.getName());


    private ContactLocation location; // location

    private static ContactLocationManager mContactLocationManager = new ContactLocationManager();
    private String providerName;

    public void setupLocation(String provider){
        location = new ContactLocation(provider);
        providerName = provider;
    }

    public ContactLocation getMyContactLocation(){
        if(providerName.equals("gps") || providerName.equals("network")){
            return location;
        }
        else {
            return GPSSimulator.getInstance().getMyContactLocation();
        }
    }

    public void onLocationChanged(Location location) {
        this.location.changeLocation(location);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        myLogger.log(Logger.FINE, "Status of provider " +  provider + " has changed and now is " + status);
    }

    public void onProviderEnabled(String provider) {
        myLogger.log(Logger.FINE, "Location provider " +  provider + " disabled!");
    }

    public void onProviderDisabled(String provider) {
        myLogger.log(Logger.FINE, "Location provider " +  provider + " enabled!");
    }

    public static ContactLocationManager getInstance(){
        return mContactLocationManager;
    }

    public synchronized void updateMyContactLocation(Location loc) {
        location.changeLocation(loc);

    }

}
