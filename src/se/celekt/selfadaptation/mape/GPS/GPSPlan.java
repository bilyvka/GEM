package se.celekt.selfadaptation.mape.GPS;

import se.celekt.gem_app.activities.GemActivity;
import se.celekt.gem_app.util.LogSaver;

public class GPSPlan {
	private static GPSPlan gpsPlan= new GPSPlan();
	
	
	
	public static GPSPlan getInstance(){
		return gpsPlan;
	}
	
	public void callPlan(){
		String logMessage = "Planning new GPS service state";
		LogSaver.getInstance().writeLog(LogSaver.GPSPlan, LogSaver.MessageType.INFO, logMessage);
		if (GPSKnowledge.getInstance().isGPS_On()){
			GPSExecute.getInstance().setGPSOff();
		}
		else{
			GPSExecute.getInstance().setGPSOn();
		}
		LogSaver.getInstance().writeLog(LogSaver.GPSPlanDone, LogSaver.MessageType.INFO, logMessage);
	}
}
