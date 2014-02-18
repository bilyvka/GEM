package se.celekt.selfadaptation.mape.GPS;

import se.celekt.gem_app.util.LogSaver;
import jade.core.Agent;

public class GPSExecute {
	private static boolean SetOn = true;
	private static boolean SetOff = false;

	private static GPSExecute gpsExecute = new GPSExecute();
	
	
	public static GPSExecute getInstance(){
		return gpsExecute;
	}
	
	public void setGPSOn(){
		GPSKnowledge.getInstance().setGPS_On(true); 
		
		Agent myAgent = GPSKnowledge.getInstance().getMyAgent();
		Object[] args = new Object[] {"HTC-hero-gps-service","location-gps",SetOn}; 
						
		String logMessage = "Setting the GPS service ON";
		LogSaver.getInstance().writeLog(LogSaver.GPSExecute, LogSaver.MessageType.INFO, logMessage);
		
		GPSEffectorServiceBehaviour ebh = new GPSEffectorServiceBehaviour(args);
		myAgent.addBehaviour(ebh);
	}
	
	public void setGPSOff(){
		GPSKnowledge.getInstance().setGPS_On(false);
		
		Agent myAgent = GPSKnowledge.getInstance().getMyAgent();
		Object[] args = new Object[] {"HTC-hero-gps-service","location-gps",SetOff}; 
	
		String logMessage = "Setting the GPS service OFF";
		LogSaver.getInstance().writeLog(LogSaver.GPSExecute, LogSaver.MessageType.INFO, logMessage);
		
		GPSEffectorServiceBehaviour ebh = new GPSEffectorServiceBehaviour(args);
		myAgent.addBehaviour(ebh);
	}

}
