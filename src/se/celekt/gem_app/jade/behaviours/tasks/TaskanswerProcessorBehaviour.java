/**
 * 
 */
package se.celekt.gem_app.jade.behaviours.tasks;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

/**
 * @author didacgil
 *
 */
public class TaskanswerProcessorBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = 3526915141850854966L;

	/** Instance of Jade Logger, for debugging purpose. */
	private Logger myLogger;
	
	/**  Ontology used for filtering message */
	public static final String TASK_RESPONSE_ONTOLOGY= "task_response_ontology";

	
	public TaskanswerProcessorBehaviour(Agent agent){
		myLogger = Logger.getMyLogger(agent.getClass().getName());
	}
	
	/**
	 * Sends the message. Executed by JADE agent.
	 */
	@Override
	public void action() {
		//myLogger.log(Logger.FINE, "Sending msg " +  theMsg.toString());				

		//Filtering from the Message Pile the messages that match with the behaviour filters
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchOntology(TASK_RESPONSE_ONTOLOGY), 
												 MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
		
		ACLMessage msg = myAgent.receive(mt);
		
		//If a message is received being a GPS answer to a REQUEST, we process there the information
		//with the behaviour we desire
		if(msg != null){
			myLogger.log(Logger.FINE, msg.toString());

			//retrieve the session id
			String sessionId = msg.getConversationId();
			myLogger.log(Logger.FINE, "Received Message... session ID is " + sessionId);

//				Object taskStructure = msg.getContentObject();
			//TODO
			// send the taskStructure to the module that deals with the presentation and the interpretation
		}else{
			block();
		}				
	}
}
