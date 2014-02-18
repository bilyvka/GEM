package se.celekt.gem_app.handlers;

import android.location.Location;

import java.util.regex.Pattern;

import jade.util.Logger;
import se.celekt.gem_app.jade.agent.Events;
import se.celekt.gem_app.jade.agent.EventsMgr;
import se.celekt.gem_app.jade.handlers.GuiEventHandler;
import se.celekt.gem_app.objects.ContactLocation;
import se.celekt.gem_app.objects.ContactLocationManager;
import se.celekt.gem_app.objects.LocationMessage;
import se.celekt.gem_app.util.LogSaver;
import se.celekt.gem_app.views.MemberListView;

/**
 * Created by alisa on 6/20/13.
 */
public class DistanceHandler extends GuiEventHandler {

    private static final Logger myLogger = Logger.getMyLogger(DistanceHandler.class.getName());
    private MemberListView mMemberListView;

    public DistanceHandler(MemberListView memberListView) {
        EventsMgr.getInstance().registerEvent(Events.INCOMING_DISTANCE_EVENT, this);
        mMemberListView = memberListView;
    }

    protected void processEvent(Events event) {
        String eventName = event.getName();

        // Handle case of new message
        if (eventName.equals(Events.INCOMING_DISTANCE_EVENT)) {
            // This message is a distance calculation

            String deviceID = (String) event.getParam(Events.INCOMING_DISTANCE_PARAM_DEVICEID);
            myLogger.log(Logger.INFO,"The device that sends its update to be presented is: " + deviceID);

            LocationMessage locationMsg = (LocationMessage) event.getParam(Events.INCOMING_DISTANCE_PARAM_MSG);


            String hisLocation = locationMsg.getMessageContent();
            String[] location = split(hisLocation);
            ContactLocation curMyLoc = ContactLocationManager.getInstance().getMyContactLocation();
            //curMyLoc.getAccuracy(); //This gives the accuracy of our Location service
            Location loc = new Location("gps");
            loc.setLatitude(Double.valueOf(location[1]));
            loc.setLongitude(Double.valueOf(location[2]));
            float distance = Math.round(loc.distanceTo(curMyLoc));

            mMemberListView.updateDistance(String.valueOf(distance), deviceID);



            LogSaver.getInstance().writeLog(
                    LogSaver.DISTANCE_REQUEST, LogSaver.MessageType.INFO,
                    "Status:OK. MyLat: " + curMyLoc.getLatitude()
                            + "- MyLon:" + curMyLoc.getLongitude()
                            + "- DeviceID:" + deviceID + " - othersLat:"
                            + location[0].toString() + " - othersLon:"
                            + location[1].toString() + " - distance:"
                            + distance);
        }
    }

    private String[] split(String coordinates) {
        String[] x = Pattern.compile("-").split(coordinates);
        return x;
    }
}
