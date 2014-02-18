package se.celekt.gem_app.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import se.celekt.gem_app.adapters.AttemptAdapter;
import se.celekt.gem_app.jade.Configuration;
import se.celekt.gem_app.jade.behaviours.BehaviourLauncher;
import se.celekt.mvd.groups.GroupManager;
import se.celekt.mvd.groups.MVDGroup;
import se.celekt.mvd.groups.Member;


import jade.android.JadeGateway;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class AttemptManager implements Serializable{

	private static final long serialVersionUID = -5051190197710317262L;

	private static final Logger myLogger = Logger.getMyLogger(AttemptManager.class.getName());
	private static AttemptManager manager = new AttemptManager();
	//private static AttemptHandlers handlers = new AttemptHandlers();
	
	private HashMap<String, Integer> pointsAttempt = new HashMap<String, Integer>();
	//private int countAttempt;
	private JadeGateway gateway;
	private String selectedButton;
	private List<Attempt> attempts = new ArrayList<Attempt>();
	private AttemptAdapter attemptAdapter;
	private String ATTEMPT_INFORM_ONTOLOGY = "attempt_inform_ontology";
	
	public AttemptManager() {
		//countAttempt = 0;
        gateway = Configuration.getGateway();
	}
	
	public void sendAttempt(String myName){
		
		//check online members
		MVDGroup myGroup = GroupManager.getInstance().getGroupByMemberAndConcern(myName, "activity");
				if(myGroup.hasOnlineMembers()){
					     updateAttempt();
					    //send request to get position to all members in group
						for(Member member: myGroup.getMembers()){
					        if (member.getAgentID() != null && !member.getName().equals(myName)){
							requestPosition(member.getAgentID());
					        informMember(myName, member.getAgentID());
					        }
					        
						}
						saveAttempt(myName);
				}
				else {
					
				}
	}
	
	public void requestPosition(AID aid) {
		
		// We need to call a behavior to be launched that:
		// sends a message to request a position and be able to calculate the distance (distance not calculated here)
		if(aid!=null){
			
		try {
			//Message creation
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(aid);
			msg.setContent("Give me the GPS position, man");
			msg.setOntology("gps_request_ontology");
			msg.setLanguage("XML");
			
			//Launching the message
			BehaviourLauncher bl = new BehaviourLauncher("it.telecomitalia.jchat.Behaviours.GeneralSenderBehaviour");
			Object[] args = new Object[] { /*ServiceType*/ null, /*ACLMessage*/ msg, /*maxRequestedServers*/1 };
			bl.setArguments(args);
			gateway.execute(bl);
		} catch (Exception e) {
			myLogger.log(Logger.SEVERE, "Error in requesting the distance", e);
			e.printStackTrace();
		}
	  }
	} 
	
	public void informMember(String myName, AID aid){
		
		
		try {
			//Message creation
			ACLMessage informMsg = new ACLMessage(ACLMessage.INFORM); // Performative
			informMsg.addReceiver(aid);
			informMsg.setOntology(ATTEMPT_INFORM_ONTOLOGY); // Ontology
			informMsg.setContent(selectedButton); // Content
			
			//Launching the message
			BehaviourLauncher bl = new BehaviourLauncher("it.telecomitalia.jchat.Behaviours.GeneralSenderBehaviour");
			Object[] args = new Object[] { /*ServiceType*/ null, /*ACLMessage*/ informMsg, /*maxRequestedServers*/1 };
			bl.setArguments(args);
			gateway.execute(bl);
		} catch (Exception e) {
			myLogger.log(Logger.SEVERE, "Error in infoming member about attempt", e);
			e.printStackTrace();
		}
		
	}
	


	public static AttemptManager getInstance() {
		return manager;
	}
	
	public int getCountAttempt(String point){
		if (pointsAttempt.get(point) != null) {
			return pointsAttempt.get(point);
		}
		return 0;
	}
	
	public void updateAttempt(){
		        //count attempts 
				if(pointsAttempt.get(selectedButton) != null){
				  int count = pointsAttempt.get(selectedButton) + 1;
				  pointsAttempt.put(selectedButton, count);
				  attemptAdapter.updateView(count,index(selectedButton));
				}
				else{
				  pointsAttempt.put(selectedButton, 1);
				  attemptAdapter.updateView(1,index(selectedButton));
				}
				
	}
	
	public void updateAttempts(HashMap<String,Integer> newAttempts){
		pointsAttempt.clear();
		for (String point:newAttempts.keySet()){
			pointsAttempt.put(point, newAttempts.get(point));
		}
	}
	
	public void updateCountAttempt(String pointName){
		 //count attempts 
		if(pointsAttempt.get(pointName) != null){
		  int count = pointsAttempt.get(pointName) + 1;
		  pointsAttempt.put(pointName, count);
		  attemptAdapter.updateView(count,index(pointName));
		}
		else{
		  pointsAttempt.put(pointName, 1);
		  attemptAdapter.updateView(1,index(pointName));
		}	
	}
	
	public void setupAttempts(){
//	  List<Placemark> places =	KmlManager.getInstance().getPlaces();
//	  attempts.clear();
//	  for(Placemark place:places){
//		  attempts.add(new Attempt(place.getName(),0));
//	  }
//	  attemptAdapter.setupListAttempt(attempts);
	}
	
	public int index(String nameButton){
		for(int i = 0; i<attempts.size();i++){
			if(attempts.get(i).getPointName().equals(nameButton)){
				return i;
			}
		}
		return -1;
	}
	
	public void saveAttempt(String myName){
	   //  handlers.saveAttempt(myName, countAttempt);
	}
	public void setSelectedButton(String name){
		selectedButton = name;
	}
	
	public HashMap<String, Integer> getPointsAttempt() {
		return pointsAttempt;
	}

	public void setPointsAttempt(HashMap<String, Integer> pointsAttempt) {
		this.pointsAttempt = pointsAttempt;
	}

	public String getSelectedButton() {
		return selectedButton;
	}
	
	public boolean hasAttempt(){
		if(pointsAttempt.get(selectedButton)!=null && pointsAttempt.get(selectedButton)>0){
			return true;
		}
		else{
			return false;
		}
	}
	
}
