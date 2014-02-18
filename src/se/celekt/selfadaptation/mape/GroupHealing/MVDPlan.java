/***
 * 
 */
package se.celekt.selfadaptation.mape.GroupHealing;

import java.io.IOException;

import se.celekt.gem_app.activities.ActivityKnowledge;
import se.celekt.gem_app.jade.agent.PhoneAgent;
import se.celekt.gem_app.util.LogSaver;
import se.celekt.mvd.groups.GroupManager;
import se.celekt.mvd.groups.MVDGroup;
import se.celekt.mvd.groups.Member;
import se.celekt.selfadaptation.mape.GPS.GPSKnowledge;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class MVDPlan{
	
	private static MVDPlan mvdPlan = new MVDPlan();
	
	public static MVDPlan getInstance(){
		return mvdPlan;
	}
	
	public MVDPlan(){
	}
	
	public void callPlan(){
		LogSaver.getInstance().writeLog(LogSaver.MVDPlanInit, LogSaver.MessageType.INFO, "MVDPlan called");
		Agent myAgent = MVDKnowledge.getInstance().getMyAgent();

		ACLMessage msgRemoved = new ACLMessage(ACLMessage.INFORM); //Performative
		msgRemoved.setOntology(PhoneAgent.GROUP_UPDATE_ONTOLOGY);	//Ontology	
		
		ACLMessage msgRemaining = new ACLMessage(ACLMessage.INFORM); //Performative
		msgRemaining.setOntology(PhoneAgent.GROUP_UPDATE_ONTOLOGY);	//Ontology	
		
		//Step0: Prepare the messages with initial content. 
		//The message for the phones to remove will contain an empty MVD
		//The message for the remaining phones will be initially a modification from their current group
		initializeMSGs(msgRemoved, msgRemaining);

		//Step1: Prepare a MSG to remove phones that are not used anymore and notify the other members
		if(MVDKnowledge.getInstance().isMemberChanged()){
			LogSaver.getInstance().writeLog(LogSaver.MVDPlanStartRemove, LogSaver.MessageType.INFO, "MVDPlan start determining undesired members");
			processRemovedPhones(myAgent, msgRemoved, msgRemaining); //Determine the phones to remove, and modify the MVD for the remaining to consider the removed phones
			MVDKnowledge.getInstance().setMessageForRemovedPhones(msgRemoved);
			MVDKnowledge.getInstance().setMessageForRemainingPhones(msgRemaining);
			LogSaver.getInstance().writeLog(LogSaver.MVDPlanFinishedRemove, LogSaver.MessageType.INFO, "MVDPlan determined undesired members");
			
			MVDExecute execute = new MVDExecute();
			execute.callExecuteRemoved();
		}
		
		//Step2: Complete the MSG looking for a newPhone (if required) 
		if(MVDKnowledge.getInstance().isPhoneRequired()){
			LogSaver.getInstance().writeLog(LogSaver.MVDPlanStartModify, LogSaver.MessageType.INFO, "MVDPlan start determining modifications for remaining phones");
			//We need to send a CallForProposals to get a newPhone in the MVD
			
			CallForProposalsGPS callForProposalsGPSb = new CallForProposalsGPS();
			myAgent.addBehaviour(callForProposalsGPSb);
			LogSaver.getInstance().writeLog(LogSaver.MVDPlanFinishedModify, LogSaver.MessageType.INFO, "MVDPlan determined modifications for remaining phones");
		}
		
	}
	
	private void initializeMSGs(ACLMessage msgRemoved, ACLMessage msgRemaining){
		MVDGroup emptyMVD = new MVDGroup(ActivityKnowledge.CONCERN);
		MVDGroup newMVD = new MVDGroup(ActivityKnowledge.CONCERN);

		for(Member mem : GroupManager.getInstance().getGroupByConcern(ActivityKnowledge.CONCERN).getMembers()){
			Member memCopy = new Member(mem.getName(), mem.getAgentID());
			newMVD.addMember(memCopy);
		}
		
		try {
			msgRemaining.setContentObject(newMVD); //Content for msgRemaining
			msgRemoved.setContentObject(emptyMVD); //Content for msgRemoved
		} catch (IOException e) {
			e.printStackTrace();
		}						

	}
	
	/***
	 * This method updates messages for removed and remaining phones from an MVD to consider all the devices that are 
	 * leaving the group.
	 * 
	 * @param agent
	 * @param msgRemoved
	 * @param msgRemaining
	 * 
	 * @author didacgildelaiglesia
	 */
	private void processRemovedPhones(Agent agent, ACLMessage msgRemoved, ACLMessage msgRemaining){
		
		try {
			for (Member mem : GroupManager.getInstance().getGroupByConcern(ActivityKnowledge.CONCERN).getMembers()){
				//If the phone is not having the service, it will receive an empty group, to leave the MVD
				if(!mem.getServices().contains(GPSKnowledge.GPS)){	//These are all the phones that have reported not to have a reliable GPS service
					//Add the phone to the receivers of empty MVD
					msgRemoved.addReceiver(mem.getAgentID());		//Receivers

					//Update this member for the remaining phones
					MVDGroup mvd = (MVDGroup) msgRemaining.getContentObject();
					Member memUpdated = new Member(mem.getName(), null);
					mvd.updateMember(memUpdated);
				}else{
					//Add the phone to the receivers of 
					msgRemaining.addReceiver(mem.getAgentID());
				}
			}
		} catch (UnreadableException e) {
			e.printStackTrace();
		}		
	}
	
	
	
	/***
	 * This behaviour looks for devices having a specific service and
	 * sends them a CallForProposals
	 * 
	 * @author didacgildelaiglesia
	 *
	 */
	public class CallForProposalsGPS extends OneShotBehaviour{
		private static final long serialVersionUID = -7351446949607023318L;

		@Override
		public void action() {
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd  = new ServiceDescription();
            sd.setType( "location-gps" );
            dfd.addServices(sd);
            
            DFAgentDescription[] result;
			try {
				result = DFService.search(myAgent, dfd);
	            System.out.println(result.length + " results" );
	            if (result.length>0){
	            	System.out.println(" " + result[0].getName() );
	            	
	            	MVDKnowledge.getInstance().setNumber_of_calledProposalsReceived(result.length);

	            	//Send them the call for proposal
	            	ACLMessage msg = new ACLMessage(ACLMessage.CFP);
	            	msg.setOntology(MVDKnowledge.GPS_SERVICE_REQUEST_ONTOLOGY);
	            	
	            	for (int i = 0; i <result.length; i++){
	            		AID r = result[i].getName();
	            		msg.addReceiver(r);
	            	}
	            	myAgent.send(msg);
	            	LogSaver.getInstance().writeLog(LogSaver.MVDPlanSearches, LogSaver.MessageType.INFO, "MVDPlan launches a CFP to integrate a new member");
	                
	            }
			} catch (FIPAException e) {
				e.printStackTrace();
			}
			
		}

		
	}
		
}
