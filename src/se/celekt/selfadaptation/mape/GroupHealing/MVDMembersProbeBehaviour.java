/***
 * This class provides a probe behaviour to identify changes in the GPS service of the members.
 * This class runs in the Master
 * It will process a message from any of the members in the MVD defining that the GPS Service was removed.
 * 
 * The agent (master) will notify about this removed member to the rest of the members in the MVD, and will notify the Monitor component to analyze the need for group self-healing
 */

package se.celekt.selfadaptation.mape.GroupHealing;

import se.celekt.mvd.groups.GroupManager;
import se.celekt.mvd.groups.MVDGroup;
import se.celekt.mvd.groups.Member;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.util.Logger;


public class MVDMembersProbeBehaviour extends OneShotBehaviour {
    private static final long serialVersionUID = 4833061659099220346L;

    public static final String CONCERN = "activity";
    private MVDGroup localMvd;

    /**
     * Instance of the Jade Logger for debugging
     */
    private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());


    public MVDMembersProbeBehaviour(){
        localMvd = GroupManager.getInstance().getGroupByConcern(CONCERN);
    }

    /**
     * Overrides the Behaviour.action() method. This method is executed by the agent thread.
     * It basically defines one sub behaviour that periodically checks the state of the GPS service
     */
    @Override
    public void action()  {
        MVDKnowledge.getInstance().setMyAgent(myAgent);

        ProbeGPSServiceBehaviour monitor = new ProbeGPSServiceBehaviour();
        myAgent.addBehaviour(monitor);
    }

    /***
     * This class provides a behaviour to process messages from devices in the MVD that are turning the GPS service off
     * @author didacgildelaiglesia
     *
     */
    public class ProbeGPSServiceBehaviour extends CyclicBehaviour{
        private static final long serialVersionUID = -8259433221135585281L;

        @Override
        public void action() {
            try{
                //Check if any of the members has changed
                MVDGroup mvd = GroupManager.getInstance().getGroupByConcern(CONCERN);

                for (Member mem:  mvd.getMembers()){
                    Member localMem = localMvd.getMember(mem.getName());

                    AID aid = mem.getAgentID();
                    AID localAid = localMem.getAgentID();

                    //The member changed. Either it is known or we got him new
                    if(aid != null && !aid.getLocalName().equals(localAid.getLocalName())){
                        String member = mem.getName();
                        //Notify the monitor that one phone was removed.
                        if(aid.getLocalName() == null)
                            MVDMonitorMembership.getInstance().updateMemberState(member, "off");
                        else
                            MVDMonitorMembership.getInstance().updateMemberState(member, "on");
                    }
                }

            }catch(Throwable t){
                myLogger.log(Logger.SEVERE,	"***  Uncaught Exception for agent "+ myAgent.getLocalName() + "  in MembersProbeBehaviour/ProcessGPSOffBehaviour***", t);
            }
        }

    }

}
