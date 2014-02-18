package se.celekt.gem_app.adapters;

import jade.util.Logger;

import se.celekt.R;
import se.celekt.gem_app.util.LogSaver;
import se.celekt.mvd.groups.GroupManager;
import se.celekt.mvd.groups.MVDGroup;
import se.celekt.mvd.groups.Member;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupAdapter extends ArrayAdapter<Member> {
	private Context context;
	private List<Member> members;
	private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());
	private LayoutInflater inflater;
	private Map<String,String> distances = new HashMap<String, String>();
		
	
	public GroupAdapter(Context context, int textViewResourceId,
			List<Member> members) {
		super(context, textViewResourceId, members);
		this.members = members;
		this.context = context;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	
	
    @Override
    public View getView(int position, View view, ViewGroup parent) {
         
          try {
  			if(view == null){
  	    		view = inflater.inflate(R.layout.element_layout,parent,false);
  			}
  			TextView name = (TextView)view.findViewById(R.id.contact_name);
  			TextView dist = (TextView)view.findViewById(R.id.contact_dist);
  			
  			name.setText(members.get(position).getName());
  		    dist.setText(distances.get(name.getText()));
  		    
  			if(this.getItem(position).getAgentID()!=null){
    			setStyle(StateType.ONLINE,view);
    		}
    		else {
				setStyle(StateType.OFFLINE,view);
			}	
  	        
  			
  		} catch(IndexOutOfBoundsException ex){

  			myLogger.log(Logger.SEVERE,"ERROR: a runtime exception should be thrown! Value of position is: " + position );
  		}
  		return view;
    }
    
    public void updateGroupView(String concern,String memberName){

    	MVDGroup group = GroupManager.getInstance().getGroupByMemberAndConcern(memberName, concern);
      	//this.clear();
        members.clear();
    	for(Member member: group.getMembers()){
    		if(! member.getName().equals(memberName)){
    			//this.add(member);
    			members.add(member);
    		}

    	}
        myLogger.log(Logger.INFO, "The group was updated");
        LogSaver.getInstance().writeLog(LogSaver.GROUP_STATUS, LogSaver.MessageType.INFO, "The " + group.getName() + " was updated ");
    	
    	notifyDataSetChanged();
    	    	
    }
    public void updateGroupView(String memberName){
    	MVDGroup group = GroupManager.getInstance().getGroupByMemberName(memberName); 
    	this.clear();
    	for(Member member: group.getMembers()){
    		if(! member.getName().equals(memberName)){
    	     	this.add(member);
    		}
    	}
 	    LogSaver.getInstance().writeLog(LogSaver.GROUP_STATUS, LogSaver.MessageType.INFO, "The " + group.getName() + " was updated ");
 	  
    	notifyDataSetChanged();
      	        	
    }
    
    public void setDistanceView(String distance,String memberName){
    	distances.put(memberName, distance);
    
    	List<Member> memebers = new ArrayList<Member>(this.members);
    	this.clear();
    	
    	for(Member member: memebers){
    		 	this.add(member);
    	
    	}
 	    LogSaver.getInstance().writeLog(LogSaver.DISTANCE_REQUEST, LogSaver.MessageType.INFO, "The distance is " + distance + " was updated  for member " + memberName);
 	  
    	notifyDataSetChanged();
    
    }
    
    private void setStyle(StateType state, View view){
		TextView contactName = (TextView) view.findViewById(R.id.contact_name);
		TextView contactDist = (TextView) view.findViewById(R.id.contact_dist);
		ImageView image = (ImageView) view.findViewById(R.id.contact_status);
		Resources res = context.getResources();
		
		switch (state) {
			case ONLINE:
				contactName.setTextColor(res.getColor(R.color.online_contact_color));
				contactName.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
				image.setImageResource(R.drawable.online);
				
		break;

			case OFFLINE:
								
				contactName.setTextColor(res.getColor(R.color.offline_contact_color));
				contactName.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
				image.setImageResource(R.drawable.offline);
			break;
			default:
				break;
			
		}
	}
    
    public enum StateType{
		ONLINE,OFFLINE
	}
}
