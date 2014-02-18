package se.celekt.selfadaptation.mape.GroupHealing;

import java.io.IOException;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import se.celekt.gem_app.activities.ActivityKnowledge;
import se.celekt.gem_app.util.LogSaver;
import se.celekt.mvd.groups.GroupManager;
import se.celekt.mvd.groups.Member;
import se.celekt.selfadaptation.mape.GPS.GPSKnowledge;

public class MVDMonitorCommunicationBehaviour extends OneShotBehaviour{

	private static final long serialVersionUID = -2598574616724366094L;

	/** Instance of Jade Logger, for debugging purpose. */
	private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());
	
	/** ACLMessage to be sent */

	private String memberName;
	private boolean serviceState;
	
	
	public MVDMonitorCommunicationBehaviour(Object[] arguments){
		myLogger.log(Logger.INFO, "GPSEffectorServiceBehaviour was called" );
		myLogger.log(Logger.INFO, "Amount of arguments: "+arguments.length);

		this.memberName	= (String)  arguments[0];
		this.serviceState 	= (Boolean) arguments[1];
	}

	
	/**
	 * Sends the message. Executed by JADE agent.
	 */
	public void action() {
        String logMessage;
        int logCode = LogSaver.MVDMonitorSlave;
        LogSaver.MessageType logType;
        
        
		Member master = GroupManager.getInstance().getGroupByConcern(ActivityKnowledge.CONCERN).getMaster();
		AID masterAID = master.getAgentID();
		
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);				//Performative
		msg.setOntology(MVDKnowledge.MONITOR_COMMUNICATION_ONTOLOGY);	//Ontology
		msg.addReceiver(masterAID);										//Receiver

		Member member = GroupManager.getInstance().getGroupByConcern(ActivityKnowledge.CONCERN).getMember(memberName);
		Member newMember = new Member(memberName, myAgent.getAID());
		newMember.setServices(member.getServices());
		
		if(!this.serviceState){
			newMember.removeService(GPSKnowledge.GPS);
		}
		
		try {
			msg.setContentObject(newMember);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		myAgent.send(msg);
		logMessage = "Slave Monitor communicated with Master Monitor";
		logType = LogSaver.MessageType.INFO;
		LogSaver.getInstance().writeLog(logCode, logType, logMessage);
		
	}

}
