package se.celekt.selfadaptation.mape.GroupHealing.PingEcho;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import se.celekt.gem_app.jade.agent.PhoneAgent;
import se.celekt.gem_app.jade.agent.Events;
import se.celekt.gem_app.jade.agent.EventsMgr;
import se.celekt.mvd.groups.GroupManager;
import se.celekt.mvd.groups.MVDGroup;
import se.celekt.mvd.groups.Member;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class PingEchoMasterBehaviour extends OneShotBehaviour {
	

	private static final long serialVersionUID = -8656085772731350126L;
	
	/** Instance of Jade Logger, for debugging purpose. */
	private Logger myLogger;
	private long freq;
	
	/**  Ontology used for filtering message */
	public static final String PINGECHO_SLAVE_ONTOLOGY= "PingEcho-slave-request_ontology";
	public static final String PINGECHO_MASTER_ONTOLOGY= "PingEcho-master-request_ontology";
	public static final String CONCERN = "activity";

	
	public PingEchoMasterBehaviour(jade.core.Agent agent, long freq) {
		myLogger = Logger.getMyLogger(agent.getClass().getName());
		this.freq = freq;
	}
	
	@Override
	public void action() {
				
		//initialize all the status
		ArrayList<Member> members = GroupManager.getInstance().getGroupByMemberAndConcern(myAgent.getLocalName(),CONCERN).getMembers();

		for(Member memb : members){
			PingEchoKnowledge.getInstance().setMemberStatus(memb.getName(), PingEchoKnowledge.StatusType.ALIVE);
		}
		try{
			CheckSlave_PingSendBehaviour pingSlaveCheckB = new CheckSlave_PingSendBehaviour(myAgent, this.freq);
			myAgent.addBehaviour(pingSlaveCheckB);
			
			CheckSlave_EchoProcessBehaviour echoSlaveProcessB = new CheckSlave_EchoProcessBehaviour();
			myAgent.addBehaviour(echoSlaveProcessB);
			
			CheckSlave_ProcessDeathsBehaviour processDeadsB = new CheckSlave_ProcessDeathsBehaviour(myAgent, this.freq);
			myAgent.addBehaviour(processDeadsB);
			
			CheckMaster_PingAnswerBehaviour pingAnswerB = new CheckMaster_PingAnswerBehaviour();
			myAgent.addBehaviour(pingAnswerB);
			
		}catch (Throwable t) {
			myLogger.log(Logger.SEVERE,	"***  Uncaught Exception for agent "+ myAgent.getLocalName() + "  in PingEchoMasterBehaviour***", t);
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
	 * This behaviour will send Pings to the slaves and Flag the slaves with states (UNKNOWN1, UNKNOWN2, DEAD).
	 * A complementary EchoProcessBehaviour behaviour waits for Slaves answers and sets them as ALIVE, to start with the 3 ping process again
	 * A complementaty ProcessDeadSlavesBehaviour behaviour deals with Dead slaves
	 * 
	 * @author didacgildelaiglesia
	 */
	public class CheckSlave_PingSendBehaviour extends TickerBehaviour{

		private static final long serialVersionUID = -6665322631551011024L;
		
		public CheckSlave_PingSendBehaviour(jade.core.Agent agent, long period) {
			super(agent, period);
			//Initiate variables if needed
		}

		@Override
		protected void onTick() {
			try{
				ACLMessage rsp = new ACLMessage(ACLMessage.REQUEST); //Performative
				rsp.setOntology(PINGECHO_SLAVE_ONTOLOGY);                  //Ontology
				ArrayList<Member> members = GroupManager.getInstance().getGroupByMemberAndConcern(myAgent.getLocalName(),CONCERN).getMembers();
	
				for(Member memb : members){
					if(null != memb.getAgentID() && !memb.getAgentID().getLocalName().equals(myAgent.getAID().getLocalName())){
						//Set flag that this Member is ping-ed, to check afterwards if it is dead or not (look for replies)
						if(PingEchoKnowledge.getInstance().getMemberStatus(memb.getName()) == PingEchoKnowledge.StatusType.ALIVE ){
							PingEchoKnowledge.getInstance().setMemberStatus(memb.getName(), PingEchoKnowledge.StatusType.UNKNOWN1);
							AID aID = memb.getAgentID();
							rsp.addReceiver(aID);                        //Receivers
						}else if(PingEchoKnowledge.getInstance().getMemberStatus(memb.getName()) == PingEchoKnowledge.StatusType.UNKNOWN1){
							PingEchoKnowledge.getInstance().setMemberStatus(memb.getName(), PingEchoKnowledge.StatusType.UNKNOWN2);
							AID aID = memb.getAgentID();
							rsp.addReceiver(aID);                        //Receivers
						}else if(PingEchoKnowledge.getInstance().getMemberStatus(memb.getName()) == PingEchoKnowledge.StatusType.UNKNOWN2){
							PingEchoKnowledge.getInstance().setMemberStatus(memb.getName(), PingEchoKnowledge.StatusType.DEAD);
						}
						
					}
				}
				rsp.setContent("ping");                              //Content
				if(rsp.getAllReceiver().hasNext()){
					myAgent.send(rsp);
					//debug to check how often the pings are sent
					//Events event = EventsMgr.getInstance().createEvent(Events.BEEP);
					//event.addParam(Events.BEEP_TONE, 0);
					//EventsMgr.getInstance().fireEvent(event);
					
					myLogger.log(Logger.FINE, rsp.toString());
				}
			}catch (Throwable t) {
				myLogger.log(Logger.SEVERE,	"***  Uncaught Exception for agent "+ myAgent.getLocalName() + "  in PingEchoMasterBehaviour/PingCheckBehaviour***", t);
			}
		}
		
	}
	
	/***
	 * This class allows to process the Echos received from the Slaves, and updates their status to ALIVE
	 * @author didacgildelaiglesia
	 *
	 */
	public class CheckSlave_EchoProcessBehaviour extends CyclicBehaviour{

		private static final long serialVersionUID = 6319489158768509621L;

		@Override
		public void action() {
			//Filtering from the Message Pile the messages that match with the behaviour filters
			try{
				MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchOntology(PINGECHO_SLAVE_ONTOLOGY), 
														 MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
				ACLMessage msg = myAgent.receive(mt);
				
				if(msg != null){
					String slaveName = msg.getSender().getLocalName();
					PingEchoKnowledge.getInstance().setMemberStatus(slaveName, PingEchoKnowledge.StatusType.ALIVE);
				}else{
					block();
				}
			}catch (Throwable t) {
				myLogger.log(Logger.SEVERE,	"***  Uncaught Exception for agent "+ myAgent.getLocalName() + "  in PingEchoMasterBehaviour/CheckSlave_EchoProcessBehaviour***", t);
			}
		}
		
	}
	
	/***
	 * Once every X time, we will check if we have DEAD slaves
	 * @author didacgildelaiglesia
	 */
	public class CheckSlave_ProcessDeathsBehaviour extends TickerBehaviour{

		private static final long serialVersionUID = -162920540387485304L;
		
		public CheckSlave_ProcessDeathsBehaviour(jade.core.Agent a, long period) {
			super(a, period);
			// Instantiate local variables if needed
		}

		@Override
		protected void onTick() {
			boolean sendMessage = false;
			ArrayList<Member> members = GroupManager.getInstance().getGroupByMemberAndConcern(myAgent.getLocalName(),CONCERN).getMembers();
			//Updating my local knowledge of the MVDGroup
			for(Member memb : members){
				if(PingEchoKnowledge.getInstance().getMemberStatus(memb.getName()) == PingEchoKnowledge.StatusType.DEAD){
					myLogger.log(Logger.INFO, "Agent "+memb.getName()+ " is dead in group "+ GroupManager.getInstance().getGroupByMemberAndConcern(myAgent.getLocalName(),CONCERN).getName());
					//communicate to the GroupManager and fire an event to update my presentaiton
					GroupManager.getInstance().setOfflineMemberInGroup(memb.getName());
					PingEchoKnowledge.getInstance().setMemberStatus(memb.getName(), PingEchoKnowledge.StatusType.ACKNOWLEDGED);
					sendMessage = true;
				}
			}
			
			if(sendMessage){
				//Updating my view in the GUI
				Events event = EventsMgr.getInstance().createEvent(Events.INCOMING_GROUP_UPDATE_EVENT);
				event.addParam(Events.INCOMING_GROUP_UPDATE_CONCERN, "activity");
				EventsMgr.getInstance().fireEvent(event);
				
				// communicate the new group change to all slaves in the group and the 
				// GroupManager on the Activity Server (MVDResilence project)
				ACLMessage rsp = new ACLMessage(ACLMessage.REQUEST); //Performative
				rsp.setOntology(PhoneAgent.GROUP_UPDATE_ONTOLOGY);     //Ontology
				MVDGroup newGroup = GroupManager.getInstance().getGroupByMemberAndConcern(myAgent.getLocalName(),CONCERN);
				HashMap<String, MVDGroup> mapGroup = new HashMap<String, MVDGroup>();
				mapGroup.put(CONCERN, newGroup);
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
			}

		}
				
	}

	/***
	 * This behaviour processes the pings from the slaves. A slave has ping-ed me to see if I am alive, so I just reply with an Echo.
	 * @author didacgildelaiglesia
	 *
	 */
	public class CheckMaster_PingAnswerBehaviour extends CyclicBehaviour{
		private static final long serialVersionUID = -3332811968129128763L;

		@Override
		public void action() {
			try{
				//This is the filter to see if the message we got is a PingEcho request from Master
				MessageTemplate mtRPingEcho = MessageTemplate.and(
						MessageTemplate.MatchOntology(PingEchoMasterBehaviour.PINGECHO_MASTER_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
				ACLMessage msgRPingEcho = myAgent.receive(mtRPingEcho);
				
				if (msgRPingEcho != null) {
					ACLMessage rsp = new ACLMessage(ACLMessage.CONFIRM); //Performative
					rsp.setOntology(msgRPingEcho.getOntology()); //Ontology
					rsp.addReceiver(msgRPingEcho.getSender()); //Receiver
					rsp.setContent("echo");
					myAgent.send(rsp);
					myLogger.log(Logger.FINE, rsp.toString());
				}else{
					block();
				}
				
			}catch (Throwable t) {
				myLogger.log(Logger.SEVERE,	"***  Uncaught Exception for agent "+ myAgent.getLocalName() + "  in PingEchoMasterBehaviour/CheckMaster_PingAnswerBehaviour***", t);
			}
		}
	}
}
