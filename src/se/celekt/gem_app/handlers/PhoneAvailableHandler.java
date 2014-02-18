package se.celekt.gem_app.handlers;

import se.celekt.gem_app.jade.Configuration;
import se.celekt.gem_app.jade.agent.Events;
import se.celekt.gem_app.jade.agent.EventsMgr;
import se.celekt.gem_app.jade.handlers.GuiEventHandler;
import se.celekt.gem_app.util.LogSaver;

/**
 * Created by alisa on 6/10/13.
 */
public class PhoneAvailableHandler extends GuiEventHandler {

    boolean firstTime = true;
    
    public PhoneAvailableHandler() {
        EventsMgr.getInstance().registerEvent(Events.GPS_SERVICE_CONNECT_EVENT, this);
    }

    protected void processEvent(Events event) {
        String eventName = event.getName();


        // Handle case of new message
        if (eventName.equals(Events.GPS_SERVICE_CONNECT_EVENT)) {
	            //Show a message that this phone is not bad
	            LogSaver.getInstance().writeLog(LogSaver.PhoneAvailable, LogSaver.MessageType.INFO, "This phone is now available for usage");
	            //show restore screen to present the phone as available        
	        if(firstTime){
                Configuration.loadInitialBehaviors();
        		firstTime = false;
	        }
        }
    }
}
