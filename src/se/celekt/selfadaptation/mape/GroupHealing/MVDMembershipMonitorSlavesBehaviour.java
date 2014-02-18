/**
 * 
 */
package se.celekt.selfadaptation.mape.GroupHealing;

import jade.core.Agent;
import se.celekt.gem_app.activities.ActivityKnowledge;
import se.celekt.gem_app.util.LogSaver;
import se.celekt.mvd.groups.GroupManager;
import se.celekt.mvd.groups.Member;


/**
 * @author didacgildelaiglesia
 *
 */
public class MVDMembershipMonitorSlavesBehaviour {
	
	private static MVDMembershipMonitorSlavesBehaviour gpsMonitor = new MVDMembershipMonitorSlavesBehaviour();
	
	//abstract void callAnalize();
	
	public MVDMembershipMonitorSlavesBehaviour(){
	}
	
	/**
	 * Gets the single instance of GPSMonitor.
	 * 
	 * @return single instance of GPSMonitor
	 */
	public static MVDMembershipMonitorSlavesBehaviour getInstance() {
		return gpsMonitor;
	}

	/***
	 * 
	 * @param state
	 */
	public void updateMemberState(boolean state){

		//If the service is going Off, then we need to report to a master Monitor.
		if(!state){
			LogSaver.getInstance().writeLog(LogSaver.MVDMonitorSlave, LogSaver.MessageType.INFO, "MVDMonitorSlave detected the member becoming undesireable");
			Agent myAgent = MVDKnowledge.getInstance().getMyAgent();
			Object[] args = new Object[] {ActivityKnowledge.getInstance().getName(), state}; 
		
			String logMessage = "This device changed its availability state";
			LogSaver.getInstance().writeLog(LogSaver.MVDMonitor, LogSaver.MessageType.INFO, logMessage);
			
			//Check if we are in an MVD
			Member master = GroupManager.getInstance().getGroupByConcern(ActivityKnowledge.CONCERN).getMaster();
			if(master != null){
				MVDMonitorCommunicationBehaviour ebh = new MVDMonitorCommunicationBehaviour(args);
				myAgent.addBehaviour(ebh);
			}
		}
	}
	
}
