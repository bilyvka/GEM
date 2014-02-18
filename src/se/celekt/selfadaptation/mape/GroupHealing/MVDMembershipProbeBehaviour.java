/***
 * This class provides a probe behaviour to identify changes in the GPS service of the members.
 * This class runs in the Master
 * It will process a message from any of the members in the MVD defining that the GPS Service was removed.
 * 
 * The agent (master) will notify about this removed member to the rest of the members in the MVD, and will notify the Monitor component to analyze the need for group self-healing
 */

package se.celekt.selfadaptation.mape.GroupHealing;

import se.celekt.gem_app.activities.ActivityKnowledge;
import se.celekt.gem_app.jade.agent.PhoneAgent;
import se.celekt.gem_app.util.LogSaver;
import se.celekt.mvd.groups.GroupManager;
import se.celekt.mvd.groups.Member;
import se.celekt.selfadaptation.mape.GPS.GPSKnowledge;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
//import jade.domain.FIPAAgentManagement.DFAgentDescription;
//import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.Logger;
//import jade.util.leap.Iterator;


public class MVDMembershipProbeBehaviour extends OneShotBehaviour {
    private static final long serialVersionUID = 4833061659099220346L;

    public static final String CONCERN = "activity";
   
    
	private long monitorTime;

    /**
     * Instance of the Jade Logger for debugging
     */
    private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());


    public MVDMembershipProbeBehaviour(){
		monitorTime = MVDKnowledge.getInstance().getFreq();
    }

    /**
     * Overrides the Behaviour.action() method. This method is executed by the agent thread.
     * It basically defines one sub behaviour that periodically checks the state of the GPS service
     */
    @Override
    public void action()  {
        MVDKnowledge.getInstance().setMyAgent(myAgent);
              
        ProbeGPSServiceBehaviour monitor = new ProbeGPSServiceBehaviour((PhoneAgent)myAgent, this.monitorTime);
        myAgent.addBehaviour(monitor);
        
        myLogger.log(Logger.INFO, "The freq. to check the Membership state is " + this.monitorTime);
    }

    /***
     * This class provides a behaviour to process messages from devices in the MVD that are turning the GPS service off
     * @author didacgildelaiglesia
     *
     */
    public class ProbeGPSServiceBehaviour extends TickerBehaviour{
		private static final long serialVersionUID = -8259433221135585281L;
		boolean serviceState;
		
    	public ProbeGPSServiceBehaviour(PhoneAgent a, long period) {
			super(a, period);
			this.serviceState = false;
//			PhoneAgent agent = (PhoneAgent) myAgent;
//    		DFAgentDescription description = agent.getAgentDescription();
//    		
//    		ServiceDescription serviceDescription;
//    		Iterator allServices = description.getAllServices();
//			while(allServices.hasNext()){
//				serviceDescription = (ServiceDescription) allServices.next();
//				if(serviceDescription.getType().equals(MVDKnowledge.serviceDescType) && 
//				   serviceDescription.getName().equals(MVDKnowledge.serviceDescName)){
//						this.serviceState = true;
//				}
//			}
		}


        @Override
        public void onTick() {
            try{
            	boolean currentServiceState = false;
            	
                //Check if my availability for a group has changed
            	Member mem = GroupManager.getInstance().getGroupByConcern(ActivityKnowledge.CONCERN).getMember(ActivityKnowledge.getInstance().getName());
            	if (mem.getServices().contains(GPSKnowledge.GPS)) currentServiceState=true;
            	
//        		PhoneAgent agent = (PhoneAgent) myAgent;
//        		DFAgentDescription currentDescription = agent.getAgentDescription();
//        		
//        		ServiceDescription serviceDescription;
//        		Iterator allServices = currentDescription.getAllServices();
//    			while(allServices.hasNext()){
//    				serviceDescription = (ServiceDescription) allServices.next();
//    				if(serviceDescription.getType().equals(MVDKnowledge.serviceDescType) && 
//    				   serviceDescription.getName().equals(MVDKnowledge.serviceDescName)){
//    						currentServiceState = true;
//    				}
//    			}
    			
    			if(currentServiceState != this.serviceState){
    				//The monitored service state has changed from the last time I checked.
    				// We need to report to the local Monitor component
    				LogSaver.getInstance().writeLog(LogSaver.MVDProbe, LogSaver.MessageType.INFO, "MVDProbe detected change");
    				MVDMembershipMonitorSlavesBehaviour.getInstance().updateMemberState(currentServiceState);
    			}

            }catch(Throwable t){
                myLogger.log(Logger.SEVERE,	"***  Uncaught Exception for agent "+ myAgent.getLocalName() + "  in MembersProbeBehaviour/ProcessGPSOffBehaviour***", t);
            }
        }

    }

}
