/***
 * This class provides a probe behaviour to identify changes in the GPS module
 */
package se.celekt.selfadaptation.mape.GPS;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.util.Logger;
import se.celekt.gem_app.objects.ContactLocation;
import se.celekt.gem_app.objects.ContactLocationManager;
import se.celekt.gem_app.util.LogSaver;


public class GPSProbeBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = -6954576345672524434L;

	/** 
	 * Time between each monitor of the GPS accuracy. 
	 * Read from configuration file 
	 */
	
	private long monitorTime;
	private float maxError;
	
	/** 
	 * Instance of the Jade Logger for debugging 
	 */
	private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());

	/**
	 * Instantiates a new contacts updater behaviour.
	 * 
	 * @param updateTime the update time
	 *
	 */
	public GPSProbeBehaviour(){
		this.monitorTime = GPSKnowledge.getInstance().getFreq();
		this.maxError = GPSKnowledge.getInstance().getMinDelta();
	}

	/**
	 * Overrides the Behaviour.action() method. This method is executed by the agent thread.
	 * It basically defines one sub behaviour that periodically checks the state of the GPS service
	 */
	@Override
	public void action()  {
		try {
			GPSKnowledge.getInstance().setMyAgent(myAgent);
			
			GPSProbeCheckBehaviour probe = new GPSProbeCheckBehaviour(myAgent, this.monitorTime, this.maxError);
			myAgent.addBehaviour(probe);
			
			myLogger.log(Logger.INFO, "The freq. to check the GPS state is " + this.monitorTime);
			myLogger.log(Logger.INFO, "The number of retries to check the GPS state is " + this.maxError);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			myLogger.log(Logger.SEVERE, "Severe error: ", e);
			e.printStackTrace();
		}
	}


	/***
	 * This behaviour probes the GPS service. Did the accuracy change much from the last time?
	 * @author didacgildelaiglesia
	 *
	 */
	public class GPSProbeCheckBehaviour extends TickerBehaviour{
		private static final long serialVersionUID = 7746025667515057267L;
		
		private Logger myLogger= Logger.getMyLogger(this.getClass().getName());
		private float maxThreshold;
		private float prevAccuracy = (float)999.999;
		
		//Constructor
		public GPSProbeCheckBehaviour(Agent agent, long period, float maxThreshold) {
			super(agent, period);
			
			//Initiate variables if needed
			myLogger = Logger.getMyLogger(agent.getClass().getName());
			myLogger.log(Logger.INFO, "GPSProbeCheckBehaviour has been called with maxError "+ maxThreshold);
			this.maxThreshold = maxThreshold;
		}
		
	
		@Override
		protected void onTick() {
			
			//Check if the current Location accuracy is good enough.
			ContactLocation curMyLoc = ContactLocationManager.getInstance().getMyContactLocation();
            //simulate gps

			float currentAccuracy = curMyLoc.getAccuracy();
			myLogger.log(Logger.INFO, "Tick! Checking the GPS accuracy");
			
			if(curMyLoc.hasAccuracy()){
				if(Math.abs(prevAccuracy - currentAccuracy) > maxThreshold){
					//The radius accuracy is not good enough
					myLogger.log(Logger.WARNING, "The accuraccy of the GPS module changed too much");
					this.prevAccuracy = currentAccuracy;

                    String logMessage = "The accuracy is "+currentAccuracy;

                    LogSaver.getInstance().writeLog(LogSaver.GPSProbe, LogSaver.MessageType.INFO, logMessage);
					GPSMonitor.getInstance().setCurrentAccuracyValue(this.prevAccuracy);
				}
			}
			else{
				myLogger.log(Logger.WARNING, "The GPS is not connected");
				this.prevAccuracy = (float)999.999;
				GPSMonitor.getInstance().setCurrentAccuracyValue(this.prevAccuracy);
			}


		}
	}

}
