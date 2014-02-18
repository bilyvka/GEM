package se.celekt.mvd.groups;

import java.io.Serializable;
//import java.util.List;
import java.util.ArrayList;

/**
 * This class represents a Group of participants .
 * Each ParticipantGroup has an ID and a mandatory name.
 *  
 * @author Didac Gil
 * @version 1.0 
 */

public class MVDGroup implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1311107417978049803L;
	private final String name;
	private String key;
	private String concern;
	private ArrayList<Member> members;
		
	public MVDGroup(String name){
		this.name = name;
		this.key = "";
		this.members = new ArrayList<Member>();
		
	}
	
		
	public MVDGroup(String name, String concern){
		this.name = name;
		this.key = "";
		this.members = new ArrayList<Member>();
		this.concern = concern;
	}
	
	public MVDGroup(MVDGroup group){
		this.name = group.name;
		this.key = group.key;
		this.members = new ArrayList<Member>();
		for (Member m : group.members){
			this.members.add(new Member(m));
		}
		this.concern = group.concern;

	}
	
		
	public ArrayList<Member> getMembers(){
		return members;
	}
	
	//
	public void addMember(Member participant){
		if (!members.contains(participant))
			members.add(participant);
	}
	
	public boolean hasMember(Member participant){
		return members.contains(participant);
	}
	
	public boolean hasMember(String participant){
		for (Member p: members ){
			if (p.toString().equals(participant)) return true;
		}
		return false;
	}
	public boolean hasMemberAndConcern(String participant,String concern){
		for (Member p: members ){
			if (p.toString().equals(participant)) return true;
		}
		return false;
	}
	public void removeMember(String mem){
		Member m = new Member(mem, null);
		if (members.contains(m)) {
			members.remove(m);
		}
	}
	
	public void updateMember(Member m){
		if (members.contains(m)){
			members.remove(m);
		}
		members.add(m);
	}
	public Member getMember(String memberName){
		for(Member member:members){
			if(member.getName().equals(memberName)){
				return member;
			}
		}
		return null;
	}
    public Member getMaster(){
        for(Member memb: this.members){
            if(isMaster(memb.getName())){
                return memb;
            }
        }
        return null;

    }
	public  String getName(){
		return name;
	}
	
	public String getKey(){
		return key;
	}
    public boolean isMaster(String name){
        for(Member member:members){
            if (member.getName().equals(name) && member.isMaster()){
                return true;
            }
        }
        return false;
    }
    public boolean hasConcern(String concern){
        return this.concern.equals(concern);
    }
	@Override
	public String toString() {
		return name;
	}
	public String getConcern() {
		return concern;
	}
    public void setConcern(String concern) {
		this.concern = concern;
	}
	@Override
	public boolean equals(Object o) {
		
		boolean res= false;
		
		if (o instanceof MVDGroup) {
			MVDGroup other = (MVDGroup) o;					
			res= name.equals(other.name);	
		}		
		return res;
	}

    public boolean hasOnlineMembers(){
        for (Member member: members ){
            if (member.getAgentID() != null) return true;
        }
        return false;
    }
	
}
