package se.celekt.selfadaptation.mape.GPS;

import jade.core.Agent;

public class GPSKnowledge {

	private static GPSKnowledge gpsK = new GPSKnowledge();
    public static final String CONCERN = "activity";
    public static String GPS = "GPS";
	private boolean GPS_On = false;
	private float gpsAccuracyValue;
	private float gpsAccuracyRequirement = 15;
	private Agent myAgent;
	
	public Agent getMyAgent() {
		return myAgent;
	}
	
	public void setMyAgent(Agent myAgent) {
		this.myAgent = myAgent;
	}
	
	private long minDelta;
	
	public long getMinDelta() {
		return minDelta;
	}

	public void setMinDelta(long minDelta) {
		this.minDelta = minDelta;
	}

	private long freq;
	
	
	public long getFreq() {
		return freq;
	}

	public void setFreq(long freq) {
		this.freq = freq;
	}

	public float getGpsAccuracyValue() {
		return gpsAccuracyValue;
	}

	public void setGpsAccuracyValue(float gpsAccuracyValue) {
		this.gpsAccuracyValue = gpsAccuracyValue;
	}

	public float getGpsAccuracyRequirement() {
		return gpsAccuracyRequirement;
	}

	public void setGpsAccuracyRequirement(float gpsAccuracyRequirement) {
		this.gpsAccuracyRequirement = gpsAccuracyRequirement;
	}

	public static GPSKnowledge getInstance(){
		return gpsK;
	}

	public boolean isGPS_On() {
		return GPS_On;
	}

	public void setGPS_On(boolean isGPS_On) {
		this.GPS_On = isGPS_On;
	}
	
	
}
