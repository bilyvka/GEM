/**
 * 
 */
package se.celekt.selfadaptation.mape.GroupHealing;


/**
 * @author didacgildelaiglesia
 *
 */
public class MVDMonitorMembership {
	
	private static MVDMonitorMembership gpsMonitor = new MVDMonitorMembership();
	
	//abstract void callAnalize();
	
	public MVDMonitorMembership(){
		
	}
	
	/**
	 * Gets the single instance of GPSMonitor.
	 * 
	 * @return single instance of GPSMonitor
	 */
	public static MVDMonitorMembership getInstance() {
		return gpsMonitor;
	}
	
	public void updateMemberState(String member, String state){
		//This should trigger the Analyze to detect the change.
		MVDKnowledge.getInstance().setMemberOff(member);
	}
	
}
