/***
 * This class executes the plans defined in the MAPE look for
 * MVD selfhealing.
 * 
 * @author Didac Gil de la Iglesia didacgil9@gmail.com
 */
package se.celekt.selfadaptation.mape.GroupHealing;

import jade.util.leap.Iterator;

import se.celekt.gem_app.util.LogSaver;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class MVDExecute{
		
	public void callExecuteRemoved(){
		Agent myAgent = MVDKnowledge.getInstance().getMyAgent();
		ACLMessage msg = MVDKnowledge.getInstance().getMessageForRemovedPhones();
		MVDEffector.getInstance().process(myAgent, msg);
		String logMessage = "Device to be notified to leave the group: ";
		Iterator it = msg.getAllIntendedReceiver();
		while(it.hasNext()){
			logMessage.concat(it.next().toString());
		}
		LogSaver.getInstance().writeLog(LogSaver.MVDExecuteRemove, LogSaver.MessageType.INFO, logMessage);
	}

	public void callExecuteRemaining(){
		Agent myAgent = MVDKnowledge.getInstance().getMyAgent();
		ACLMessage msg = MVDKnowledge.getInstance().getMessageForRemainingPhones();
		MVDEffector.getInstance().process(myAgent, msg);
		String logMessage = "Device to be notified to update the group: ";
		Iterator it = msg.getAllIntendedReceiver();
		while(it.hasNext()){
			logMessage.concat(it.next().toString());
		}
		LogSaver.getInstance().writeLog(LogSaver.MVDExecuteModify, LogSaver.MessageType.INFO, logMessage);
	}
		
}
