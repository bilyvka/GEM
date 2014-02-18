/***
 * This class provides the effector behavior to be run in a master device.
 * When a GPS device is turning off the GPS service, it will get removed from the MVD where it belongs.
 * 
 */
package se.celekt.selfadaptation.mape.GPS;

import java.io.IOException;
import java.util.HashMap;

import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import se.celekt.gem_app.jade.agent.PhoneAgent;
import se.celekt.gem_app.jade.agent.Events;
import se.celekt.gem_app.jade.agent.EventsMgr;
import se.celekt.mvd.groups.GroupManager;
import se.celekt.mvd.groups.MVDGroup;
import se.celekt.mvd.groups.Member;
import se.celekt.selfadaptation.mape.GroupHealing.MVDKnowledge;

public class GPSEffectorGroupBehaviour extends CyclicBehaviour{

	private static final long serialVersionUID = -2598574616724366094L;

	/** Instance of Jade Logger, for debugging purpose. */
	private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());
		
	
	public GPSEffectorGroupBehaviour(){

	}

	
	/**
	 * Sends the message. Executed by JADE agent.
	 */
	public void action() {
		// Filter to see if we are getting a message from the any of the members of the MVD (Execute) 
		// saying that a phone has turned the GPS service off.
		MessageTemplate mtIGPSOff = MessageTemplate.and(
				MessageTemplate.MatchOntology(MVDKnowledge.GPS_DISSABLE_NOTIFICATION_ONTOLOGY),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		ACLMessage msgIGPSOff = myAgent.receive(mtIGPSOff);
		
		if(null != msgIGPSOff){
			
			//remove the phone from the group (Update the knowledge)
			String member = msgIGPSOff.getSender().getLocalName();
			GroupManager.getInstance().setOfflineMemberInGroup(member);
			
			//notify the rest of the contacts and the GroupManagerServer
			ACLMessage rsp = new ACLMessage(ACLMessage.REQUEST); //Performative
			rsp.setOntology(PhoneAgent.GROUP_UPDATE_ONTOLOGY);     //Ontology
			MVDGroup newGroup = GroupManager.getInstance().getGroupByMemberAndConcern(myAgent.getLocalName(),GPSKnowledge.CONCERN);
			HashMap<String, MVDGroup> mapGroup = new HashMap<String, MVDGroup>();
			mapGroup.put(GPSKnowledge.CONCERN, newGroup);
			try {
				rsp.setContentObject(mapGroup);					//Content
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for(Member potentialReceivers : newGroup.getMembers()){
				if(potentialReceivers.getAgentID() != null && !potentialReceivers.getName().equals(myAgent.getLocalName())){
					rsp.addReceiver(potentialReceivers.getAgentID());  //Receivers
				}
			}
			
//			//Do we want to communicate to the Main Activity server about changes in the group?
//			String service_type ="groupManager-type";			
//			DFAgentDescription[] receivers = getService(service_type);
//			if(null == receivers){
//				myLogger.log(Logger.WARNING, "No agent found that offers the service" );
//			}
//			else if (receivers.length>0){
//				String targetAgent = receivers[0].getName().getLocalName().toString();
//				rsp.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));
//			}
			
			myAgent.send(rsp);
			
			callOnlineContactsViewer();
			
			//trigger the MVD Self-healing MAPE?
			
		}
		
	}
	
	/**
	 * Allows to find agents that offer a service of a specific type
	 * @param type
	 * @return array with the AIDs of the agents that can provide the service with the type
	 */
	public DFAgentDescription[] getService(String type){
		myLogger.log(Logger.INFO, "getService Called for the service "+type );
		DFAgentDescription[] results = getServiceProviders(type);
		return results;
	}
	
	public DFAgentDescription[] getServiceProviders(String type){
		myLogger.log(Logger.INFO, "getServiceProviders was called with the type:"+type );
	    DFAgentDescription dfd = new DFAgentDescription();
	    ServiceDescription sd  = new ServiceDescription();
	    myLogger.log(Logger.INFO, "The agent has been asked to look for "+ type);
	    sd.setType(type);
	    dfd.addServices(sd);
	    
	    DFAgentDescription[] result;
		try {
			myLogger.log(Logger.INFO, "Agent ("+ myAgent.getLocalName() + ") looking for services with the type ("+sd.getType()+") in the DF");
			result = DFService.search(myAgent, dfd);
			myLogger.log(Logger.INFO, result.length + " results have been found" );
		    if (result.length>0)
		        myLogger.log(Logger.INFO," Result[0]:" + result[0].getName() );
		    return result;
		    
		} catch (Exception e) {
			myLogger.log(Logger.WARNING, "Couldn't handle it" );
			e.printStackTrace();
			return null;
		}
	}
	
	private void callOnlineContactsViewer(){
		//update the view of online contacts
		Events event = EventsMgr.getInstance().createEvent(Events.INCOMING_GROUP_UPDATE_EVENT);
		event.addParam(Events.INCOMING_GROUP_UPDATE_CONCERN, "activity");
		EventsMgr.getInstance().fireEvent(event);
	}

}
