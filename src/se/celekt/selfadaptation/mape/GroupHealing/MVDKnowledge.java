package se.celekt.selfadaptation.mape.GroupHealing;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

import se.celekt.mvd.groups.MVDGroup;
import se.celekt.mvd.groups.Member;


public class MVDKnowledge {

	public static String GPS_DISSABLE_NOTIFICATION_ONTOLOGY = "GPS_DISSABLE_NOTIFICATION_ONTOLOGY";
	public static String GPS_SERVICE_REQUEST_ONTOLOGY = "GPS_SERVICE_REQUEST_ONTOLOGY";
	private static MVDKnowledge gpsK = new MVDKnowledge();
	
	private Agent myAgent;
	
	//Probe information
	public static String serviceDescType = "location-gps";
	public static String serviceDescName = "HTC-hero-gps-service";
	
	private long freq;
	
	
	public long getFreq() {
		return freq;
	}

	public void setFreq(long freq) {
		this.freq = freq;
	}

	//Monitor information
	private MVDGroup mvd;
	private int requiredGPS;
	public static String MONITOR_COMMUNICATION_ONTOLOGY = "MONITOR_COMMUNICATION_ONTOLOGY"; 
	
	//Analyze information
	private boolean phoneRequired;	
	
	public boolean isPhoneRequired() {
		return phoneRequired;
	}

	public void setPhoneRequired(boolean phoneRequired) {
		this.phoneRequired = phoneRequired;
	}

	private boolean memberChanged;
	
	public boolean isMemberChanged() {
		return memberChanged;
	}

	public void setMemberChanged(boolean memberChanged) {
		this.memberChanged = memberChanged;
	}

	private String ConversationID_for_newPhone;
	
	//Plan information
	private int number_of_calledProposalsSent;
	private int number_of_calledProposalsReceived;
	
	private ACLMessage messageForRemovedPhones;
	private ACLMessage messageForRemainingPhones;
	
	public ACLMessage getMessageForRemovedPhones() {
		return messageForRemovedPhones;
	}

	public void setMessageForRemovedPhones(ACLMessage messageForRemovedPhones) {
		this.messageForRemovedPhones = messageForRemovedPhones;
	}

	public ACLMessage getMessageForRemainingPhones() {
		return messageForRemainingPhones;
	}

	public void setMessageForRemainingPhones(ACLMessage messageForRemainingPhones) {
		this.messageForRemainingPhones = messageForRemainingPhones;
	}

	private AID newPhone;
	
	public AID getNewPhone() {
		return newPhone;
	}

	public void setNewPhone(AID newPhone) {
		this.newPhone = newPhone;
	}

	private boolean newPhoneProcessed;

	public boolean isNewPhoneProcessed() {
		return newPhoneProcessed;
	}

	public void setNewPhoneProcessed(boolean newPhoneProcessed) {
		this.newPhoneProcessed = newPhoneProcessed;
	}



	public String getConversationID_for_newPhone() {
		return ConversationID_for_newPhone;
	}

	public void setConversationID_for_newPhone(String conversationID_for_newPhone) {
		ConversationID_for_newPhone = conversationID_for_newPhone;
	}

	public Agent getMyAgent() {
		return myAgent;
	}

	public void setMyAgent(Agent myAgent) {
		this.myAgent = myAgent;
	}

	public MVDGroup getMvd() {
		return mvd;
	}

	public void setMvd(MVDGroup mvd) {
		this.mvd = mvd;
	}
	
	public int getRequiredGPS() {
		return requiredGPS;
	}

	public void setRequiredGPS(int requiredGPS) {
		this.requiredGPS = requiredGPS;
	}

	private boolean state;
	
	public static MVDKnowledge getInstance(){
		return gpsK;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public void setMemberOff(String member){
		Member m = mvd.getMember(member);
		ArrayList<String> services = m.getServices();
		
		if (services.contains("GPS")){
			services.remove("GPS");
		}
		
		m.setServices(services);
		this.mvd.updateMember(m);
	}

	public int getNumber_of_calledProposalsSent() {
		return number_of_calledProposalsSent;
	}

	public void setNumber_of_calledProposalsSent(int number_of_calledProposalsSent) {
		this.number_of_calledProposalsSent = number_of_calledProposalsSent;
	}

	public int getNumber_of_calledProposalsReceived() {
		return number_of_calledProposalsReceived;
	}

	public void setNumber_of_calledProposalsReceived(
			int number_of_calledProposalsReceived) {
		this.number_of_calledProposalsReceived = number_of_calledProposalsReceived;
	}
	
}
