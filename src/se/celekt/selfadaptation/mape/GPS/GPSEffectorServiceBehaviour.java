package se.celekt.selfadaptation.mape.GPS;

import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.Logger;
import jade.util.leap.Iterator;
import se.celekt.gem_app.activities.ActivityKnowledge;
import se.celekt.gem_app.jade.agent.PhoneAgent;
import se.celekt.gem_app.jade.agent.Events;
import se.celekt.gem_app.jade.agent.EventsMgr;
import se.celekt.gem_app.util.LogSaver;
import se.celekt.mvd.groups.GroupManager;
import se.celekt.mvd.groups.Member;

public class GPSEffectorServiceBehaviour extends OneShotBehaviour{

	private static final long serialVersionUID = -2598574616724366094L;

	/** Instance of Jade Logger, for debugging purpose. */
	private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());
	
	/** ACLMessage to be sent */

	private String descName;
	private String descType;
	private boolean activate;
	
	
	public GPSEffectorServiceBehaviour(Object[] arguments){
		myLogger.log(Logger.INFO, "GPSEffectorServiceBehaviour was called" );
		myLogger.log(Logger.INFO, "Amount of arguments: "+arguments.length);

		this.descName	= (String)  arguments[0];
		this.descType	= (String)  arguments[1];
		this.activate 	= (Boolean) arguments[2];
	}

	
	/**
	 * Sends the message. Executed by JADE agent.
	 */
	public void action() {
        String logMessage;
        int logCode;
        LogSaver.MessageType logType;

		ServiceDescription newServiceDescription = new ServiceDescription();
		newServiceDescription.setName(descName);
		newServiceDescription.setType(descType);

		PhoneAgent agent = (PhoneAgent) myAgent;
		
		DFAgentDescription description = agent.getAgentDescription();
		
		if (this.activate){ 
			Member mem = GroupManager.getInstance().getGroupByConcern(ActivityKnowledge.CONCERN).getMember(ActivityKnowledge.getInstance().getName());
			mem.addService(GPSKnowledge.GPS);
			
			//Activate the GPS Service
			description.addServices(newServiceDescription);
            logMessage = " GPS service went ON";
            logCode = LogSaver.GPSEffectorON;
            logType = LogSaver.MessageType.INFO;
			
			//The first time, fire the event to get engaged to the activity
			Events event = EventsMgr.getInstance().createEvent(Events.GPS_SERVICE_CONNECT_EVENT);
			myLogger.log(Logger.FINE,	"Firing an event to notify that the phone is ready: " + event.toString());
			// Send the message to the main Controler to deal with it
			EventsMgr.getInstance().fireEvent(event);
		}else{
			Member mem = GroupManager.getInstance().getGroupByConcern(ActivityKnowledge.CONCERN).getMember(ActivityKnowledge.getInstance().getName());
			mem.removeService(GPSKnowledge.GPS);
			
            logMessage = " GPS service went OFF";
            logCode = LogSaver.GPSEffectorOFF;
            logType = LogSaver.MessageType.INFO;
			//Deactivate the GPS service
			ServiceDescription serviceDescription;
			Iterator allServices = description.getAllServices();
			while(allServices.hasNext()){
				serviceDescription = (ServiceDescription) allServices.next();
				if(serviceDescription.getType() == descType && serviceDescription.getName()==descName){
					description.removeServices(serviceDescription);
				}
			}
            //The first time, fire the event to get engaged to the activity
            Events event = EventsMgr.getInstance().createEvent(Events.GPS_SERVICE_DISCONNECT_EVENT);
            myLogger.log(Logger.FINE,	"Firing an event to notify that the phone is not providing the GPS service: " + event.toString());
            // Send the message to the main Controler to deal with it
            EventsMgr.getInstance().fireEvent(event);
		}
		
		try {
			DFService.modify(myAgent, description);
            LogSaver.getInstance().writeLog(logCode, logType, logMessage);
		} catch (FIPAException e) {
			myLogger.log(Logger.WARNING, "Problems removing a service in the agent");
			e.printStackTrace();
		}
		
	}

}
