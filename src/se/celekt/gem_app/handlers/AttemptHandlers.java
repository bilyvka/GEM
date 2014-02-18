package se.celekt.gem_app.handlers;


import jade.util.Logger;
import se.celekt.gem_app.jade.agent.Events;
import se.celekt.gem_app.jade.agent.EventsMgr;
import se.celekt.gem_app.jade.handlers.GuiEventHandler;
import se.celekt.gem_app.kml.Placemark;
import se.celekt.gem_app.objects.AttemptManager;
import se.celekt.gem_app.objects.ContactLocation;
import se.celekt.gem_app.objects.ContactLocationManager;
import se.celekt.gem_app.util.LogSaver;
import se.celekt.gem_app.views.MemberListView;


import android.content.Context;

public class AttemptHandlers {
	
	/*Handlers*/

	private AttemptUpdaterHandler attemptHandler;
	
	/**GUI views*/
	private MemberListView mMemberListView;


	private static final Logger myLogger = Logger.getMyLogger(AttemptHandlers.class.getName());



    private  ContactLocationManager mContactLocationManager;

	public AttemptHandlers(Context context, MemberListView memberListView) {

		    attemptHandler = new AttemptUpdaterHandler();
			EventsMgr.getInstance().registerEvent(Events.INCOMING_ATTEMPT_EVENT, attemptHandler);

            mContactLocationManager = new ContactLocationManager();
            mMemberListView = memberListView;
	}
	

   /**
    * Notify all online memberes about attempt
    * */
	
	
	private class AttemptUpdaterHandler extends GuiEventHandler {

		/**
		 * Performs the update of the GUI. It handles the arrival of a new
		 * message with a distance to a device update.
		 * <p>
		 * Two cases are possible:
		 * <ol>
		 * <li>incoming message is related to a device in our group, so updating
		 * the table with its location needs to be done
		 * <li>incoming message is not related to a device in our group, the
		 * message should be ignored
		 * </ol>
		 * 
		 * @param event
		 *            the event that shall be notified to this listener to be
		 *            handled
		 */
		protected void processEvent(Events event) {
			String eventName = event.getName();

			// Handle case of new message
			if (eventName.equals(Events.INCOMING_ATTEMPT_EVENT)) {
				
				//update count attempts
				//AttemptManager.getInstance().updateAttempt();
				String deviceID = (String) event.getParam(Events.INCOMING_ATTEMPT_PARAM_DEVICEID);
				myLogger.log(Logger.INFO, "The device that sends its update to be presented is: " + deviceID);
				String point = event.getParam(Events.INCOMING_ATTEMPT_PARAM_MSG).toString();
				AttemptManager.getInstance().updateCountAttempt(point);
				String message =  deviceID + " tried to find point " + point + " attempt number " + AttemptManager.getInstance().getCountAttempt(point);
				//TODO: dhow dialogs
               // activity.showAlert(message);
				
			}
			
		}
	}
	
	
	public void saveAttempt(String myName, int countAttempts){
		ContactLocation curMyLoc = mContactLocationManager.getMyContactLocation();
		Placemark placemark = new Placemark();
		placemark.setCoordinates(String.valueOf(curMyLoc.getLatitude()) + "," + String.valueOf(curMyLoc.getLongitude()) + "," + String.valueOf(curMyLoc.getAltitude()));
		LogSaver.getInstance().savePlacemark(placemark,"at" + String.valueOf(countAttempts), LogSaver.LogType.ATTEMPT_LOG);

	}
}
