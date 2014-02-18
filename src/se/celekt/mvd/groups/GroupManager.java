package se.celekt.mvd.groups;

import jade.util.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;


/**
 * Manages the list of MVDGroup where the agent is registered. It is
 * responsible for adding and removing MVDGroup in a thread safe way.
 * 
 * 
 * @author Didac Gil
 * @version 1.0
 */
public class GroupManager {

	/**
	 * Static instance of the contact manager. Singleton
	 */
	private static GroupManager manager = new GroupManager();

	/**
	 * Map containing all the available MVDGroup where this agent is registered
	 */
	private final Map<String, MVDGroup> groupsMap;

	/**
	 * Instance of the Jade logger for debugging
	 */
	private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());

	/**
	 * Instantiates a new MVDGroup manager.
	 */
	private GroupManager() {
		groupsMap = new HashMap<String, MVDGroup>();
	}

	/**
	 * Removes the MVDGroup identified with the provided name from the
	 * MVDGroup Map list.
	 * 
	 * @param name
	 *            of the name to be removed from the Map
	 */
	public synchronized void removeGroup(String name) {
		MVDGroup group = groupsMap.get(name);

		if (group != null) {
			groupsMap.remove(name);
		}

	}

	public synchronized void addOrUpdateGroup(MVDGroup group){
		//check if this group already exists
		if (groupsMap.containsKey(group.getName())){
			groupsMap.remove(group.getName());
		}
		groupsMap.put(new String(group.getName()), new MVDGroup(group));
	}
	
	
	/**
	 * Updates or adds groups into our Map of groups
	 * 
	 * @param groups
	 */
	public synchronized void updateGroups(Map<String, MVDGroup> groups) {
		// for each group in this list, we need to check whether this group
		// exists --> update
		// or if it is a new group --> create
		for (String s : groups.keySet()) {
			try {
				// TODO
				// If locally, it should be evaluated if the group conflicts with other
				// groups. Apply the OCL rules here
				groupsMap.put(new String(s), new MVDGroup(groups.get(s)));
			} catch (Exception e) {
				// In the Android 2.1, it seems to crash. An empty contactsMap
				// HashMap returns a size of 1, with matches <null,null>, which
				// provokes a NullPointerException when you try to add it in the
				// cMap
			}
		}
	}

	public synchronized void removeMemberFromGroup(String groupName,String member) {
		MVDGroup group = groupsMap.get(groupName);
		if (group == null)
			myLogger.log(Logger.INFO, "Thread "
					+ Thread.currentThread().getId() + ":MVDGroup "
					+ groupName + " is not a valid MVDGroup name. "
					+ member + " cannot be removed.");
		else {
			group.removeMember(member);
			groupsMap.remove(groupName);
			groupsMap.put(groupName, group);
		}
	}

	public synchronized void addMemberToGroup(String groupName,
			Member member) {
		MVDGroup group = groupsMap.get(groupName);
		// If group does not exist, we create the group and add the member
		if (group == null) {
			myLogger.log(Logger.INFO, "Thread "
					+ Thread.currentThread().getId() + ":MVDGroup "
					+ groupName
					+ " didn't exist. Creating MVDGroup and adding "
					+ member + " member.");
			MVDGroup group2 = new MVDGroup(groupName);
			group2.addMember(member);
			groupsMap.put(groupName, group2);
		}
		// if the group exists, the member gets added
		else {
			group.addMember(member);
			groupsMap.remove(groupName);
			groupsMap.put(groupName, group);
		}
	}

	public MVDGroup getGroupByItemPosition(int position) {
		int counter = position;
		for (Map.Entry<String, MVDGroup> entry : groupsMap.entrySet()) {
			MVDGroup group = entry.getValue();
			counter -= group.getMembers().size();
			if (counter <1) return group;
		}
		return null;
	}

	public boolean hasMemberInGroup(String groupName, String member) {
		MVDGroup mvd = groupsMap.get(groupName);
		if (mvd != null)
			if (mvd.hasMember(member))
				return true;
		return false;
	}
    
	public MVDGroup getGroupByConcern(String concern){
		for (String groupnames:groupsMap.keySet()){
			if(groupsMap.get(groupnames).getConcern().equals(concern)){
				return groupsMap.get(groupnames);
			}
		}
		return null;
	}
	/**
	 * Adds a new {@link MVDGroup} with the given name and key or
	 * updates the key of an existing one.
	 * 
	 * @param group
	 *            of the new MVDGroup or of the MVD
	 *            should be updated
	 */
	public synchronized void updateGroup(MVDGroup group) {
		groupsMap.put(group.getName(), group);
		myLogger.log(Logger.INFO, "Group " + group.getName() + " was updated");

	}

	/**
	 * Retrieves a Mobile Virtual Device declaration given its name
	 * 
	 * @param name
	 *            the name of the MVDGroup that should be retrieved
	 * @return the MVDGroup having the given name
	 */
	public MVDGroup getGroup(String name) {
		return this.groupsMap.get(name);
	}

	/**
	 * Gets the single instance of MVDManager.
	 * 
	 * @return single instance of MVDManager
	 */
	public static GroupManager getInstance() {
		return manager;
	}

	/**
	 * Cleans up the MVDGroup map
	 */
	public void shutdown() {
		this.groupsMap.clear();
	}

	/**
	 * Retrieves a map containing mappings between MVDGroup and names
	 * 
	 * @return copy of the inner MVDGroup map
	 */
	public synchronized HashMap<String, MVDGroup> getGroups() {
		HashMap<String, MVDGroup> groupM = new HashMap<String, MVDGroup>();
		for (String s : this.groupsMap.keySet()) {
			try {
				groupM.put(new String(s),
						new MVDGroup(this.groupsMap.get(s)));
			} catch (Exception e) {
				//TODO: add to log
			}
		}
		return groupM;
	}
	
	public synchronized MVDGroup getGroupByMemberName(final String memberName) {
		MVDGroup groupM = null;
		
		for(String groupNames:groupsMap.keySet()){
			if (groupsMap.get(groupNames).hasMember(memberName)){
				groupM = groupsMap.get(groupNames);
				Collections.sort(groupM.getMembers(), new Comparator<Member>() {
					public int compare(Member m1, Member m2) {
						if(m1.getName().equals(memberName)){
							return -1;
						}
						return 1;
						
					};
				});
				break;
			}
		}
		return groupM;
	}
	
	public synchronized MVDGroup getGroupByMemberAndConcern(final String memberName, String concern) {
		MVDGroup groupM = null;
		
		for(String groupNames:groupsMap.keySet()){
			if (groupsMap.get(groupNames).hasMember(memberName) && groupsMap.get(groupNames).hasConcern(concern)){
				groupM = groupsMap.get(groupNames);
				Collections.sort(groupM.getMembers(), new Comparator<Member>() {
					public int compare(Member m1, Member m2) {
						if(m1.getName().equals(memberName)){
							return -1;
						}
						return 1;
						
					};
				});
				break;
			}
		}
	
		return groupM;
	}
	
	public synchronized void setOfflineMemberInGroup(final String memberName) {
		MVDGroup groupM = getGroupByMemberName(memberName);
		for(Member m: groupM.getMembers()){
			if(m.getName().equals(memberName)){
			 m.set_agentID(null);
			}
		}
	}
}
