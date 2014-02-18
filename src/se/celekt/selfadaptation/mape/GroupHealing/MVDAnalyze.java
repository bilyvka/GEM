package se.celekt.selfadaptation.mape.GroupHealing;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;

import java.util.ArrayList;

import se.celekt.gem_app.util.LogSaver;
import se.celekt.mvd.groups.Member;
import se.celekt.selfadaptation.mape.GPS.GPSKnowledge;

public class MVDAnalyze extends OneShotBehaviour{

	private static final long serialVersionUID = 8155205213790731289L;

	private static int Incomplete = -1;
	private static int Complete = 0;
	private static int Redundant = 1;
	
	private static MVDAnalyze gpsAnalize = new MVDAnalyze();

	
	public static MVDAnalyze getInstance(){
		return gpsAnalize;
	}
	
	@Override
	public void action() {
		Agent myAgent = MVDKnowledge.getInstance().getMyAgent();
		
		Analyze ab = new Analyze(myAgent, 1000);
		myAgent.addBehaviour(ab);
	}
	
	
	/***
	 * This local behaviour checks:
	 *   1- if a device needs to be removed from the MVD because it is not good for the group (misses a service)
	 *   2- if there is a need for devices in the MVD 
	 * @author didacgildelaiglesia
	 *
	 */
	public class Analyze extends TickerBehaviour{
		
		public Analyze(Agent a, long period) {
			super(a, period);
		}

		private static final long serialVersionUID = 6167671248460699462L;

		@Override
		protected void onTick() {
			//Two main reasons can require a plan execution:

			// 1. A phone is not having a good GPS service, so we need to remove it from the MVDs so other members do not request its service
			boolean needPlan = false;
			if (MVDKnowledge.getInstance().isMemberChanged()){
				needPlan = true;
				LogSaver.getInstance().writeLog(LogSaver.MVDAnalyze, LogSaver.MessageType.INFO, "MVDAnalyze determined a need for a plan because a device became undesired");
				
			}
				
			// 2. The MVD is lacking resources
			if(Incomplete == analyze()){
				LogSaver.getInstance().writeLog(LogSaver.MVDAnalyze, LogSaver.MessageType.INFO, "MVDAnalyze determined a need for a plan because the MVD requires members");
				MVDKnowledge.getInstance().setPhoneRequired(true);
				needPlan = true;
			}

			if (needPlan){
				//triggering the Plan to do its task
				MVDPlan.getInstance().callPlan();
			}
		}
		
		
		private int analyze(){
			int requiredGPS;
			int availableGPS = 0;
			
			//Step one: check how many GPS services are required
			requiredGPS = MVDKnowledge.getInstance().getRequiredGPS();
			
			//Step two: check how many GPS services are listed
			ArrayList<Member> members = MVDKnowledge.getInstance().getMvd().getMembers();
			
			for (Member m : members){
				ArrayList<String> services = m.getServices();
				
				if (services.contains(GPSKnowledge.GPS)){
					availableGPS++;
				}
			}
			
			if (requiredGPS < availableGPS) return Incomplete;
			if (requiredGPS == availableGPS) return Complete;
			return Redundant;
		}
	}
	
}
