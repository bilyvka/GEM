package se.celekt.selfadaptation.mape.GPS;

import se.celekt.gem_app.util.LogSaver;

public class GPSAnalyze {
	private static boolean Change = true;
	private static boolean Keep = false;
	
	private static GPSAnalyze gpsAnalize = new GPSAnalyze();

	
	public static GPSAnalyze getInstance(){
		return gpsAnalize;
	}
	
	public void callAnalyze(){
		String logMessage = "Analyzing new settings for GPS service";
		LogSaver.getInstance().writeLog(LogSaver.GPSAnalyze, LogSaver.MessageType.INFO, logMessage);
		if(Change == analyze()){
			LogSaver.getInstance().writeLog(LogSaver.GPSAnalyzeFail, LogSaver.MessageType.INFO, logMessage);
			GPSPlan.getInstance().callPlan();
		}
	}
	
	private boolean analyze(){
		if(GPSKnowledge.getInstance().isGPS_On() &&
		   GPSKnowledge.getInstance().getGpsAccuracyValue() > GPSKnowledge.getInstance().getGpsAccuracyRequirement()) 
			return Change;

		if(!GPSKnowledge.getInstance().isGPS_On() &&
		    GPSKnowledge.getInstance().getGpsAccuracyValue() <= GPSKnowledge.getInstance().getGpsAccuracyRequirement()) 
			return Change;
		return Keep;
	}

}
