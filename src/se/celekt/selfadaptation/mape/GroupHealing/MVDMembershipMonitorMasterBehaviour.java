/***
 * This class provides a probe behaviour to identify changes in the GPS service of the members.
 * This class runs in the Master
 * It will process a message from any of the members in the MVD defining that the GPS Service was removed.
 * 
 * The agent (master) will notify about this removed member to the rest of the members in the MVD, and will notify the Monitor component to analyze the need for group self-healing
 */

package se.celekt.selfadaptation.mape.GroupHealing;

import se.celekt.gem_app.activities.ActivityKnowledge;
import se.celekt.gem_app.util.LogSaver;
import se.celekt.mvd.groups.GroupManager;
import se.celekt.mvd.groups.MVDGroup;
import se.celekt.mvd.groups.Member;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;


public class MVDMembershipMonitorMasterBehaviour extends OneShotBehaviour {
    private static final long serialVersionUID = 4833061659099220346L;

    /**
     * Instance of the Jade Logger for debugging
     */
    private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());


    public MVDMembershipMonitorMasterBehaviour(){
    }

    /**
     * Overrides the Behaviour.action() method. This method is executed by the agent thread.
     * It basically defines one sub behaviour that periodically checks the state of the GPS service
     */
    @Override
    public void action()  {
        MVDKnowledge.getInstance().setMyAgent(myAgent);

    }

    /***
     * This class provides a behaviour to process messages from Monitor components in slave devices in the MVD that are turning the GPS service off
     * @author didacgildelaiglesia
     *
     */
    public class MVDMonitorBehaviour extends CyclicBehaviour{
        private static final long serialVersionUID = -8259433221135585281L;

        @Override
        public void action() {
            try{
            	//Listen for message
    			MessageTemplate mtIMonService = MessageTemplate.and(
    					MessageTemplate.MatchOntology(MVDKnowledge.MONITOR_COMMUNICATION_ONTOLOGY),
    					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
    			ACLMessage msgIMonService = myAgent.receive(mtIMonService);
            	
    			if(msgIMonService != null){
    				Member member = (Member) msgIMonService.getContentObject();
                    //Check if any of the members has changed
    				
                    MVDGroup mvd = GroupManager.getInstance().getGroupByConcern(ActivityKnowledge.CONCERN);

                    Member oldMem = mvd.getMember(member.getName());
                    oldMem.setServices(member.getServices());
                    MVDKnowledge.getInstance().setMemberChanged(true);
                    LogSaver.getInstance().writeLog(LogSaver.MVDMonitorMaster, LogSaver.MessageType.INFO, "MVDMaster updated the services of a member");
    			}

                //Now, at one point the MVDAnalyze should know about this change
                
            }catch(Throwable t){
                myLogger.log(Logger.SEVERE,	"***  Uncaught Exception for agent "+ myAgent.getLocalName() + "  in MembersProbeBehaviour/ProcessGPSOffBehaviour***", t);
            }
        }

    }

}
