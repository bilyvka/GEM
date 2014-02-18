/**
 * 
 */
package se.celekt.selfadaptation.mape.GroupHealing;

import se.celekt.gem_app.util.LogSaver;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

/**
 * @author didacgildelaiglesia
 *
 */
public class MVDEffector {

private static MVDEffector mvdEffector = new MVDEffector();
	
	public static MVDEffector getInstance(){
		return mvdEffector;
	}
	
	public MVDEffector(){
	}
	
	public void process(Agent myAgent, ACLMessage msg){
		myAgent.send(msg);
		String logMessage = "Sending messages to update MVD";
		LogSaver.getInstance().writeLog(LogSaver.MVDEffector, LogSaver.MessageType.INFO, logMessage);
	}
	
	
}
