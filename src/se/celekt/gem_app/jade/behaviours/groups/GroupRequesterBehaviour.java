package se.celekt.gem_app.jade.behaviours.groups;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class GroupRequesterBehaviour extends OneShotBehaviour{

	private static final long serialVersionUID = -2598574616724366094L;

	/** Instance of Jade Logger, for debugging purpose. */
	private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());
	
	/** ACLMessage to be sent */
	private ACLMessage theMsg;
	
	private String service_type=""; 
	private boolean search=false;
	
	public GroupRequesterBehaviour(Object[] arguments){
		myLogger.log(Logger.INFO, "GroupRequesterBehaviour was called" );
		
		myLogger.log(Logger.INFO, "Amount of arguments: "+arguments.length);
		for(int i=0; i< arguments.length; i++){
			if(arguments[i] instanceof ACLMessage){
				myLogger.log(Logger.INFO, "Argument["+i+"] is an ACLMessage");
				theMsg = (ACLMessage) arguments[i];
				myLogger.log(Logger.INFO, theMsg.toString());
				if (!theMsg.getAllReceiver().hasNext()){ 
					search = true;
				} 
			}else if(arguments[i] instanceof String){
				myLogger.log(Logger.INFO, "Argument["+i+"] is a String");
				service_type= (String) arguments[i];
				myLogger.log(Logger.INFO, service_type);
			}
			
/*			if(arguments[i] instanceof ServiceType){
				service_type = (String) arguments[i];
			}*/
		}
		
	}
	
	public void searchServiceProviders(String service_type){
		myLogger.log(Logger.INFO, "GroupRequesterBehaviour was called" );
		
		//We look for agents providing the service
		DFAgentDescription[] receivers = this.getService(service_type);
		
		//if any agent found, we add their agentID to the recipient list
		//and the "action()" method will be called sending the message
		if(null == receivers){
			myLogger.log(Logger.WARNING, "No agent found that offers the service" );
		}
		else if (receivers.length>0){
			String targetAgent = receivers[0].getName().getLocalName().toString();
			this.theMsg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	
	/**
	 * Sends the message. Executed by JADE agent.
	 */
	public void action() {
		if (search && !service_type.equals("")){
			this.searchServiceProviders(service_type);			
		}
		
		theMsg.setSender(myAgent.getAID());
		myLogger.log(Logger.FINE, theMsg.toString());
		myAgent.send(theMsg);
	}

}
