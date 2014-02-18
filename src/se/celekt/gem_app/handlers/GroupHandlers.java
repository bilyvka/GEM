package se.celekt.gem_app.handlers;

import jade.util.Logger;
import se.celekt.gem_app.jade.agent.Events;
import se.celekt.gem_app.jade.agent.EventsMgr;
import se.celekt.gem_app.jade.handlers.GuiEventHandler;
import se.celekt.gem_app.views.MemberListView;

public class GroupHandlers {
	
	private GroupUpdaterHandler groupHandler = new GroupUpdaterHandler();
	private AnswersSentOKHandler answersSentOKHandler = new AnswersSentOKHandler();

	private static final Logger myLogger = Logger.getMyLogger(GroupHandlers.class.getName());
	
	private MemberListView controller;
	
	public GroupHandlers(MemberListView controller) {
		EventsMgr.getInstance().registerEvent(Events.INCOMING_GROUP_UPDATE_EVENT, groupHandler);
		EventsMgr.getInstance().registerEvent(Events.INCOMING_ANSWER_SENT_OK_EVENT, answersSentOKHandler);
		EventsMgr.getInstance().registerEvent(Events.INCOMING_MEMBER_STATUS_EVENT, groupHandler);
		EventsMgr.getInstance().registerEvent(Events.UPTADE_ATTEMPTS_EVENT, groupHandler);
		
		this.controller = controller;
	}
	
	private class GroupUpdaterHandler extends GuiEventHandler {

		/**
		 * Performs the update of the GUI to provide new information about
		 * groups. It handles the arrival of a new message with groups to
		 * update.
		 * <p>
		 * 
		 * @param event
		 *            the event that shall be notified to this listener to be
		 *            handled
		 */
		protected void processEvent(Events event) {
			String eventName = event.getName();

			// Handle case of new message
			if (eventName.equals(Events.INCOMING_GROUP_UPDATE_EVENT)) {
				// This message is a group update. We do not care who sent the
				// message, it has been checked by the agent behavior

				myLogger.log(Logger.INFO,"We got some information about groups to update");
                 
				String concern = (String) event.getParam(Events.INCOMING_GROUP_UPDATE_CONCERN);

				if(concern != null){

				   controller.updateContactList(concern);
				}
				else{
				   controller.updateContactList();
				}


			}
			else if(eventName.equals(Events.INCOMING_MEMBER_STATUS_EVENT)){
				 //  controller.displayMessage((String)event.getParam(Events.INCOMING_MEMBER_STATUS_MSG));
				 //  controller.turnOffInterface(true);
			}
			else if (eventName.equals(Events.UPTADE_ATTEMPTS_EVENT)){
				//controller.displayMessage((String)event.getParam(Events.INCOMING_MEMBER_STATUS_MSG));
				//controller.turnOffInterface(false);
				//update attempts
				//HashMap<String,Integer> newAttempts = (HashMap<String,Integer>)event.getParam(Events.INCOMING_ATTEMPT_PARAM_DEVICEID);
				//AttemptManager.getInstance().updateAttempts(newAttempts);
			}
		}
	}
	private class AnswersSentOKHandler extends GuiEventHandler {

		/**
		 * Performs the update of the GUI to provide new information about
		 * tasks. It handles the arrival of a new message with tasks to be
		 * updated in the presentation.
		 * <p>
		 * 
		 * @param event
		 *            the event that shall be notified to this listener to be
		 *            handled
		 */
		protected void processEvent(Events event) {
			String eventName = event.getName();

			// Handle case of new message
			if (eventName.equals(Events.INCOMING_ANSWER_SENT_OK_EVENT)) {
				
			}
		}
	}
	

}
