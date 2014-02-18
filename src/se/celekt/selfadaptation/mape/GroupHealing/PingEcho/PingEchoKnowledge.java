package se.celekt.selfadaptation.mape.GroupHealing.PingEcho;

import java.util.HashMap;
import java.util.Map;

public class PingEchoKnowledge {
	private static PingEchoKnowledge pingEchoK = new PingEchoKnowledge();
	private Map<String,StatusType> membersPing;
	
	
	public static PingEchoKnowledge getInstance(){
		return pingEchoK;
	}
	
	public Map<String, StatusType> getMembersPing() {
		return membersPing;
	}


	public void setMemberStatus(String memb, StatusType status){
		this.membersPing.put(memb, status);
	}
	
	public StatusType getMemberStatus(String memb){
		return this.membersPing.get(memb);
		
	}
	
	public void setMembersPing(Map<String, StatusType> membersPing) {
		this.membersPing = membersPing;
	}


	public PingEchoKnowledge() {
	   membersPing = new HashMap<String, StatusType>();
	}
	
	
    public enum StatusType{
    	ALIVE, UNKNOWN1, UNKNOWN2, DEAD, ACKNOWLEDGED
    }
}
