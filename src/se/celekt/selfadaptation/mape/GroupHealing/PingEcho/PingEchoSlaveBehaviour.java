package se.celekt.selfadaptation.mape.GroupHealing.PingEcho;

import se.celekt.mvd.groups.GroupManager;
import se.celekt.mvd.groups.Member;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class PingEchoSlaveBehaviour extends OneShotBehaviour{

	private static final long serialVersionUID = -8953494805786476209L;

	/** Instance of Jade Logger, for debugging purpose. */
	private Logger myLogger;
	private long freq;
	
	public static final String CONCERN = "activity";
		
	public PingEchoSlaveBehaviour(Agent agent, long freq) {
		myLogger = Logger.getMyLogger(agent.getClass().getName());
		this.freq = freq;
	}
	
	@Override
	public void action() {
		Member master = GroupManager.getInstance().getGroupByMemberAndConcern(myAgent.getLocalName(),CONCERN).getMaster();
		PingEchoKnowledge.getInstance().setMemberStatus(master.getName(), PingEchoKnowledge.StatusType.ALIVE);
		
		CheckSlave_PingAnswerBehaviour pingSlaveAnswerProcessB = new CheckSlave_PingAnswerBehaviour();
		myAgent.addBehaviour(pingSlaveAnswerProcessB);
		
		CheckMaster_PingSendBehaviour pingMasterCheckB = new CheckMaster_PingSendBehaviour(myAgent, this.freq);
		myAgent.addBehaviour(pingMasterCheckB);
		
		CheckMaster_ProcessDeathBehaviour processDeadM = new CheckMaster_ProcessDeathBehaviour(myAgent, this.freq);
		myAgent.addBehaviour(processDeadM);
	}

	/***
	 * This behaviour processes the pings from the Master. The master has ping-ed us to see if we are alive, so we just reply with an Echo.
	 * @author didacgildelaiglesia
	 *
	 */
	public class CheckSlave_PingAnswerBehaviour extends CyclicBehaviour{
		private static final long serialVersionUID = -3332811968129128763L;

		@Override
		public void action() {
			try{
				//This is the filter to see if the message we got is a PingEcho request from Master
				MessageTemplate mtRPingEcho = MessageTemplate.and(
						MessageTemplate.MatchOntology(PingEchoMasterBehaviour.PINGECHO_SLAVE_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
				ACLMessage msgRPingEcho = myAgent.receive(mtRPingEcho);
				
				if (msgRPingEcho != null) {
					ACLMessage rsp = new ACLMessage(ACLMessage.CONFIRM); //Performative
					rsp.setOntology(msgRPingEcho.getOntology()); //Ontology
					rsp.addReceiver(msgRPingEcho.getSender()); //Receiver
					rsp.setContent("echo");
					myAgent.send(rsp);
//					MsnEvent event = MsnEventMgr.getInstance().createEvent(MsnEvent.BEEP);
//					event.addParam(MsnEvent.BEEP_TONE, 1);
//					MsnEventMgr.getInstance().fireEvent(event);
					myLogger.log(Logger.FINE, rsp.toString());
				}else{
					block();
				}
				
			}catch (Throwable t) {
				myLogger.log(Logger.SEVERE,	"***  Uncaught Exception for agent "+ myAgent.getLocalName() + "  in PingEchoSlaveBehaviour/PingMasterProcessBehaviour***", t);
			}
		}
	}
	
	/***
	 * This behaviour will send Pings to the master and Flag it with states (UNKNOWN1, UNKNOWN2 and DEAD).
	 * A complementary EchoMasterProcessBehaviour behaviour waits for the master answer and sets it as ALIVE, to start with the 3 ping process again
	 * A complementaty ProcessDeadMasterBehaviour behaviour deals with Dead master and new Master selection
	 * 
	 * @author didacgildelaiglesia
	 */
	public class CheckMaster_PingSendBehaviour extends TickerBehaviour{

		private static final long serialVersionUID = -6665322631551011024L;
		
		public CheckMaster_PingSendBehaviour(Agent agent, long period) {
			super(agent, period);
			//Initiate variables if needed
		}

		@Override
		protected void onTick() {
			try{
				ACLMessage rsp = new ACLMessage(ACLMessage.REQUEST); //Performative
				rsp.setOntology(PingEchoMasterBehaviour.PINGECHO_MASTER_ONTOLOGY);                  //Ontology
				
				//Get the master of the group
				Member master = GroupManager.getInstance().getGroupByMemberAndConcern(myAgent.getLocalName(),CONCERN).getMaster();
	
				if(null != master){
					if(PingEchoKnowledge.getInstance().getMemberStatus(master.getName()) == PingEchoKnowledge.StatusType.ALIVE){
						PingEchoKnowledge.getInstance().setMemberStatus(master.getName(), PingEchoKnowledge.StatusType.UNKNOWN1);
						AID aID = master.getAgentID();
						rsp.addReceiver(aID);                        //Receivers
					}else if(PingEchoKnowledge.getInstance().getMemberStatus(master.getName()) == PingEchoKnowledge.StatusType.UNKNOWN1){
						PingEchoKnowledge.getInstance().setMemberStatus(master.getName(), PingEchoKnowledge.StatusType.UNKNOWN2);
						AID aID = master.getAgentID();
						rsp.addReceiver(aID);                        //Receivers
					}else if(PingEchoKnowledge.getInstance().getMemberStatus(master.getName()) == PingEchoKnowledge.StatusType.UNKNOWN2){
						PingEchoKnowledge.getInstance().setMemberStatus(master.getName(), PingEchoKnowledge.StatusType.DEAD);
						AID aID = master.getAgentID();
						rsp.addReceiver(aID);                        //Receivers
					}
					rsp.setContent("ping");                              //Content
					if(rsp.getAllReceiver().hasNext()){
						myAgent.send(rsp);						
						myLogger.log(Logger.FINE, rsp.toString());
					}					
				}else{
					//The master is not even declared. This may mean that we are in the process of selecting a new master. Nothing to do here.
				}
			}catch (Throwable t) {
				myLogger.log(Logger.SEVERE,	"***  Uncaught Exception for agent "+ myAgent.getLocalName() + "  in PingEchoSlaveBehaviour/PingMasterCheckBehaviour***", t);
			}
		}
		
	}
	
	/***
	 * This class allows to process the Echos received from the Slaves, and updates their status to ALIVE
	 * @author didacgildelaiglesia
	 *
	 */
	public class CheckMaster_EchoProcessBehaviour extends CyclicBehaviour{

		private static final long serialVersionUID = 6319489158768509621L;

		@Override
		public void action() {
			//Filtering from the Message Pile the messages that match with the behaviour filters
			try{
				MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchOntology(PingEchoMasterBehaviour.PINGECHO_MASTER_ONTOLOGY), 
														 MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
				ACLMessage msg = myAgent.receive(mt);
				
				if(msg != null){
					String masterName = msg.getSender().getLocalName();
					PingEchoKnowledge.getInstance().setMemberStatus(masterName, PingEchoKnowledge.StatusType.ALIVE);
				}else{
					block();
				}
			}catch (Throwable t) {
				myLogger.log(Logger.SEVERE,	"***  Uncaught Exception for agent "+ myAgent.getLocalName() + "  in PingEchoSlaveBehaviour/EchoMasterProcessBehaviour***", t);
			}
		}
		
	}
	
	public class CheckMaster_ProcessDeathBehaviour extends TickerBehaviour{
		private static final long serialVersionUID = -7829613750908639864L;
		public CheckMaster_ProcessDeathBehaviour(Agent a, long period) {
			super(a, period);
			// Instantiate local variables if needed
		}

		@Override
		protected void onTick() {
			Member master = GroupManager.getInstance().getGroupByMemberAndConcern(myAgent.getLocalName(),CONCERN).getMaster();
			
			if(PingEchoKnowledge.getInstance().getMemberStatus(master.getName()) == PingEchoKnowledge.StatusType.DEAD){
				PingEchoKnowledge.getInstance().setMemberStatus(master.getName(), PingEchoKnowledge.StatusType.ACKNOWLEDGED);
				//TODO: Communicate to everybody that the master is dead
				
				//TODO: Deal with the decision for a new Master
			}
		}
	}

}
