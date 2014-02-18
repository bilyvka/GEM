package se.celekt.mvd.groups;

import jade.core.AID;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents a Group of participants .
 * Each ParticipantGroup has an ID and a mandatory name.
 *  
 * @author Didac Gil
 * @version 1.0 
 */

public class Member implements Serializable{

	private static final long serialVersionUID = 8836131350259354692L;
	private final String _name;
	private AID _agentID;
	private ArrayList<String> _services;
	private boolean master;
	
	public Member(String name, AID agentID){
		this._name = name;
		this._agentID = agentID;
		this._services = new ArrayList<String>();
	}
	
	public Member(Member m){
		this._name = m._name;
		this._agentID = m._agentID;
		this._services = new ArrayList<String>();
		this.master = m.master;
		for (String service : m._services){
			this._services.add(service);
		}
	}
	
	public AID getAgentID(){
		return _agentID;
	}
	
	public String getName(){
		return _name;
	}
	
	public void addService(String service){
		this._services.add(service);
	} 
	
	public void removeService(String service){
		for(String s:_services){
			if(s.equals(service)){
				this._services.remove(s);
			}
		}
	} 
	
	public void setServices(ArrayList<String> services){
		this._services=services;
	}
	
	public ArrayList<String> getServices(){
		return this._services;
	}
	
	public void setMaster(boolean master) {
		this.master = master;
	}
	
	public boolean isMaster() {
		return master;
	}
	
	@Override
	public String toString() {
		return _name;
	}
	public void set_agentID(AID _agentID) {
		this._agentID = _agentID;
	}
	@Override
	public boolean equals(Object o) {
		//Needed for methods such as List.contains(Object)
		boolean res= false;
		
		if (o instanceof Member) {
			Member other = (Member) o;					
			res= _name.equals(other._name);	
		}		
		return res;
	}
	
}
