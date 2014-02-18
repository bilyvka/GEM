package se.celekt.gem_app.handlers;


import jade.util.Logger;
import se.celekt.gem_app.jade.agent.Events;
import se.celekt.gem_app.jade.agent.EventsMgr;
import se.celekt.gem_app.jade.handlers.GuiEventHandler;

public class FoundHandlers {
	static final String logAttempts_filepath ="/sdcard/GEM/GeM4_found_log.txt";
	private static final Logger myLogger = Logger.getMyLogger(FoundHandlers.class.getName());
	private FoundHandler foundHandler;
	//private ContactListActivity activity;
	
	public FoundHandlers() {
		foundHandler = new FoundHandler();
		EventsMgr.getInstance().registerEvent(Events.INCOMING_FOUND_POINT_EVENT, foundHandler);
		EventsMgr.getInstance().registerEvent(Events.INCOMING_UNFOUND_POINT_EVENT, foundHandler);
	}
	
	private class FoundHandler extends GuiEventHandler {

		@Override
		protected void processEvent(Events event) {
			String eventName = event.getName();

			// Handle case of new message
			if (eventName.equals(Events.INCOMING_FOUND_POINT_EVENT)) {
				// This message is a distance calculation

				String deviceID = (String) event.getParam(Events.INCOMING_FOUND_POINT_PARAM_DEVICEID);
				myLogger.log(Logger.INFO, "The device that sends its update to be presented is: " + deviceID);
				
				String pointName = event.getParam(Events.INCOMING_FOUND_POINT_PARAM_MSG).toString();
				String attemptMsg = deviceID + " found point " + pointName;
				//activity.showAlert(attemptMsg);
				//activity.foundButton(pointName);
				
			}
			else if (eventName.equals(Events.INCOMING_UNFOUND_POINT_EVENT)){
				String deviceID = (String) event.getParam(Events.INCOMING_UNFOUND_POINT_PARAM_DEVICEID);
				myLogger.log(Logger.INFO, "The device that sends its update to be presented is: " + deviceID);
				
				String pointName = event.getParam(Events.INCOMING_UNFOUND_POINT_PARAM_MSG).toString();
				String attemptMsg = deviceID + " unfound point " + pointName;
				//activity.showAlert(attemptMsg);
				//activity.unfoundButton(pointName);
			}
			
		}
		
	}
	
//	public void setViews(ContactListActivity activity){
//
//		this.activity = activity;
//
//	}
}
