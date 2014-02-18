package se.celekt.gem_app.jade.behaviours;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class GeneralSenderBehaviour  extends OneShotBehaviour{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3832048734404001832L;

	/** Instance of Jade Logger, for debugging purpose. */
	private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());
	
	/** ACLMessage to be sent */
	private ACLMessage theMsg;
	private String service_type; 
	private int amountReceivers;
	private boolean search=false;
	
	
	public GeneralSenderBehaviour(Object[] arguments){
		myLogger.log(Logger.INFO, "GeneralSenderBehaviour was called" );
		
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
			}else if(arguments[i] instanceof Integer){
				myLogger.log(Logger.INFO, "Argument["+i+"] is an int");
				amountReceivers = (Integer) arguments[i];
			}
			
/*			if(arguments[i] instanceof ServiceType){
				service_type = (String) arguments[i];
			}*/
		}
		
	}

	
	/**
	 * Sends the message. Executed by JADE agent.
	 */
	@Override
	public void action() {
		myLogger.log(Logger.INFO, "getServiceProviders was called with the type:"+service_type );
	    DFAgentDescription dfd = new DFAgentDescription();
	    ServiceDescription sd  = new ServiceDescription();
	    myLogger.log(Logger.INFO, "The agent has been asked to look for "+ service_type);
	    sd.setType(service_type);
	    dfd.addServices(sd);
	    
	    DFAgentDescription[] receivers = null;
	    if(search){
			try {
				myLogger.log(Logger.INFO, "Agent ("+ myAgent.getLocalName() + ") looking for services with the type ("+sd.getType()+") in the DF");
				receivers = DFService.search(myAgent, dfd);
				myLogger.log(Logger.INFO, receivers.length + " results have been found" );
				for (int i=0; i <receivers.length; i++){
					String targetAgent = receivers[i].getName().getLocalName();
					theMsg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));

					//We check that we do not send it to more receivers than needed
					this.amountReceivers  --;
					if (this.amountReceivers == 0) break;
				}
				
			} catch (Exception e) {
				myLogger.log(Logger.WARNING, "Couldn't handle it" );
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		myLogger.log(Logger.FINE, theMsg.toString());
		myAgent.send(theMsg);
	}

}
