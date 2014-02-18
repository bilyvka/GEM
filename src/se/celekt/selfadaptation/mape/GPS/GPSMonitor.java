/**
 * 
 */
package se.celekt.selfadaptation.mape.GPS;

import se.celekt.gem_app.activities.GemActivity;
import se.celekt.gem_app.util.LogSaver;


/**
 * @author didacgildelaiglesia
 *
 */
public class GPSMonitor {
	
	private static GPSMonitor gpsMonitor = new GPSMonitor();
	
	//abstract void callAnalize();
	
	public GPSMonitor(){
		
	}
	
	/**
	 * Gets the single instance of GPSMonitor.
	 * 
	 * @return single instance of GPSMonitor
	 */
	public static GPSMonitor getInstance() {
		return gpsMonitor;
	}
	

	public void setCurrentAccuracyRequirement(float currentAccuracyRequirement) {
		GPSKnowledge.getInstance().setGpsAccuracyRequirement(currentAccuracyRequirement);
		
		callAnalyzer();
	}

	
	public void setCurrentAccuracyValue(float currentAccuracy){
		String logMessage = "Monitored new GPS accuracy";
		LogSaver.getInstance().writeLog(LogSaver.GPSMonitor, LogSaver.MessageType.INFO, logMessage);
		GPSKnowledge.getInstance().setGpsAccuracyValue(currentAccuracy);
		callAnalyzer();
	}

	private void callAnalyzer(){
		GPSAnalyze.getInstance().callAnalyze();
	}
	
	//Something should be done about this problem. We should remove the LocationService, or we should remove the phone
	//from the organization it belongs and set it as not-desired

}
