/***
 * This class provides a probe behaviour to identify changes in the GPS module
 */
package se.celekt.selfadaptation.mape.GroupHealing;

import java.io.IOException;
import java.util.HashMap;

import se.celekt.gem_app.activities.ActivityKnowledge;
import se.celekt.gem_app.jade.agent.PhoneAgent;
import se.celekt.gem_app.jade.agent.Events;
import se.celekt.gem_app.jade.agent.EventsMgr;
import se.celekt.mvd.groups.GroupManager;
import se.celekt.mvd.groups.MVDGroup;
import se.celekt.mvd.groups.Member;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;


public class MVDMembersEffectorBehaviour extends OneShotBehaviour {
	private static final long serialVersionUID = 4833061659099220346L;

		
	/** 
	 * Instance of the Jade Logger for debugging 
	 */
	private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());

	/**
	 * Instantiates a new contacts updater behaviour.
	 * 
	 * @param updateTime the update time
	 *
	 */
	public MVDMembersEffectorBehaviour(long updateTime, float maxError){

	}

	/**
	 * Overrides the Behaviour.action() method. This method is executed by the agent thread.
	 * It basically defines one sub behaviour that periodically checks the state of the GPS service
	 */
	@Override
	public void action()  {
		try {
			MVDKnowledge.getInstance().setMyAgent(myAgent);
			
			ProcessGPSOffBehaviour monitor = new ProcessGPSOffBehaviour();
			myAgent.addBehaviour(monitor);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			myLogger.log(Logger.SEVERE, "Severe error: ", e);
			e.printStackTrace();
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

	/***
	 * This class provides a behaviour to process messages from devices in the MVD that are turning the GPS service off
	 * @author didacgildelaiglesia
	 *
	 */
	public class ProcessGPSOffBehaviour extends CyclicBehaviour{
		private static final long serialVersionUID = -8259433221135585281L;

		@Override
		public void action() {
			try{
				MessageTemplate mtIGPSOff = MessageTemplate.and(
						MessageTemplate.MatchOntology(MVDKnowledge.GPS_DISSABLE_NOTIFICATION_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				ACLMessage msgIGPSOff = myAgent.receive(mtIGPSOff);
				
				if (msgIGPSOff != null){
					//remove the phone from the group 
					GroupManager.getInstance().setOfflineMemberInGroup(msgIGPSOff.getSender().getLocalName());
					
					//update the view of online contacts
					Events event = EventsMgr.getInstance().createEvent(Events.INCOMING_GROUP_UPDATE_EVENT);
					event.addParam(Events.INCOMING_GROUP_UPDATE_CONCERN, ActivityKnowledge.CONCERN);
					EventsMgr.getInstance().fireEvent(event);
					
					//notify the rest of the contacts and the GroupManagerServer
					ACLMessage rsp = new ACLMessage(ACLMessage.REQUEST); //Performative
					rsp.setOntology(PhoneAgent.GROUP_UPDATE_ONTOLOGY);     //Ontology
					MVDGroup newGroup = GroupManager.getInstance().getGroupByMemberAndConcern(myAgent.getLocalName(),ActivityKnowledge.CONCERN);
					HashMap<String, MVDGroup> mapGroup = new HashMap<String, MVDGroup>();
					mapGroup.put(ActivityKnowledge.CONCERN, newGroup);
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
					
					String service_type ="groupManager-type";			
					DFAgentDescription[] receivers = getService(service_type);
					if(null == receivers){
						myLogger.log(Logger.WARNING, "No agent found that offers the service" );
					}
					else if (receivers.length>0){
						String targetAgent = receivers[0].getName().getLocalName().toString();
						rsp.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));
					}
					myAgent.send(rsp);
					
				}else{
					block();
				}
				
			}catch(Throwable t){
				myLogger.log(Logger.SEVERE,	"***  Uncaught Exception for agent "+ myAgent.getLocalName() + "  in MembersProbeBehaviour/ProcessGPSOffBehaviour***", t);
			}
		}
	}

}
