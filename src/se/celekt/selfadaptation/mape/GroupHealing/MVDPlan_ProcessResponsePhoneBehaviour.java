/***
 * This behaviour processes responses from phones, which may be offering their services to join an MVD
 * we only need to get the first phone responding to the request.
 * After the first response, we can stop this behaviour
 * 
 * @author didacgildelaiglesia
 */

package se.celekt.selfadaptation.mape.GroupHealing;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

import se.celekt.gem_app.util.LogSaver;
import se.celekt.mvd.groups.MVDGroup;
import se.celekt.mvd.groups.Member;
import se.celekt.selfadaptation.mape.GPS.GPSKnowledge;

/**
 * @author didacgildelaiglesia
 *
 */
public class MVDPlan_ProcessResponsePhoneBehaviour extends CyclicBehaviour{
	private static final long serialVersionUID = -6273527798584403413L;

	@Override
	public void action() {
		MessageTemplate mtPGpsService = MessageTemplate.and(
				MessageTemplate.MatchOntology(MVDKnowledge.GPS_SERVICE_REQUEST_ONTOLOGY),
				MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
				MessageTemplate.MatchConversationId(MVDKnowledge.getInstance().getConversationID_for_newPhone());
		ACLMessage msgPGpsService = myAgent.receive(mtPGpsService);
		
		if (msgPGpsService != null){
			// This agent is offering its services to join the MVD. We can pick the first agent making an offer, and ignore the rest
			//This three lines here trigger the MVDExecute to integrate the new phone
			MVDKnowledge.getInstance().setConversationID_for_newPhone(""); //This way we will reject all other requests.
			MVDKnowledge.getInstance().setNewPhone(msgPGpsService.getSender());
			MVDKnowledge.getInstance().setNewPhoneProcessed(false);

			AID newPhoneAID = msgPGpsService.getSender();
			// Pick one of the members in the group that does not have a phone assigned, and update it
			updateMemberAID(newPhoneAID);
			
			LogSaver.getInstance().writeLog(LogSaver.MVDPlanProcess, LogSaver.MessageType.INFO, "A new phone is selected: "+newPhoneAID.getLocalName());
			
			
			MVDExecute execute = new MVDExecute();
			execute.callExecuteRemaining();
		}
		else {
			LogSaver.getInstance().writeLog(LogSaver.MVDPlanProcess, LogSaver.MessageType.INFO, "A proposal to integrate a phone in the group is rejected: ");
			block();
		}
			myAgent.removeBehaviour(this);
	}

	/**
	 * 
	 * @param newAID
	 */
	private void updateMemberAID(AID newAID){
		ACLMessage msg = MVDKnowledge.getInstance().getMessageForRemainingPhones();

		MVDGroup mvd;
		try {
			mvd = (MVDGroup) msg.getContentObject();
			for (Member mem: mvd.getMembers()){
				if(!mem.getServices().contains(GPSKnowledge.GPS)){
					//This is a candidate to be replaced by the new phone
					mem.addService(GPSKnowledge.GPS);
					mem.set_agentID(newAID);
					mvd.updateMember(mem);
					
					msg.setContentObject(mvd);
					break;
				}
				
			}
		} catch (UnreadableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}