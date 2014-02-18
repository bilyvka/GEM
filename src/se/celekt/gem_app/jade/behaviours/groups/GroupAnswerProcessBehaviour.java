/**
 * 
 */
package se.celekt.gem_app.jade.behaviours.groups;

import se.celekt.gem_app.jade.agent.PhoneAgent;
import se.celekt.gem_app.jade.agent.Events;
import se.celekt.gem_app.jade.agent.EventsMgr;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;
import se.celekt.mvd.groups.GroupManager;
import se.celekt.mvd.groups.MVDGroup;

/**
 * @author didacgil
 *
 */
public class GroupAnswerProcessBehaviour extends CyclicBehaviour{

	private static final long serialVersionUID = 3526915141850854966L;

	/** Instance of Jade Logger, for debugging purpose. */
	private Logger myLogger;
	
	
	public GroupAnswerProcessBehaviour(jade.core.Agent agent){
		myLogger = Logger.getMyLogger(agent.getClass().getName());
	}
	
	/**
	 * Sends the message. Executed by JADE agent.
	 */
	@Override
	public void action() {

		//Filtering from the Message Pile the messages that match with the behaviour filters
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchOntology(PhoneAgent.GROUPS_INFORM_ONTOLOGY),
												 MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		ACLMessage msg = myAgent.receive(mt);
		
		if(msg != null){


			String sessionId = msg.getConversationId();
			myLogger.log(Logger.INFO, "Received Message... session ID is " + sessionId);

			try {
                myLogger.log(Logger.INFO,"Objest " + msg.getContentObject());
				MVDGroup mvd = (MVDGroup) msg.getContentObject();
                GroupManager.getInstance().addOrUpdateGroup(mvd);
				
				//if I am master, launch the PingEcho behaviour as master, else, launch the PingEcho behaviour as Slave (of course, remove the other)
                if(mvd.isMaster(myAgent.getLocalName())){
                    ((PhoneAgent) myAgent).addMasterBehaviour();
                }
                else {
                	((PhoneAgent) myAgent).addSlaveBehaviour();
                }
                
				
				// Preparing an event for the Viewer to present the
				// changes in the groups
				Events event = EventsMgr.getInstance().createEvent(	Events.INCOMING_GROUP_UPDATE_EVENT);
				event.addParam(Events.INCOMING_GROUP_UPDATE_CONCERN,	"activity");
				EventsMgr.getInstance().fireEvent(event);
				
			} catch (UnreadableException e) {
                myLogger.log(Logger.SEVERE,e.getMessage());
				e.printStackTrace();
			}
		}else{
			block();
		}				
	}
}
