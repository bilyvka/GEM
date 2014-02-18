/*****************************************************************
 jChat is a  chat application for Android based on JADE
  Copyright (C) 2008 Telecomitalia S.p.A. 
 
 GNU Lesser General Public License

 This is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation, 
 version 2.1 of the License. 

 This software is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this software; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 Boston, MA  02111-1307, USA.
 *****************************************************************/
 
package se.celekt.gem_app.adapters;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.celekt.gem_app.objects.Attempt;
import se.celekt.gem_app.util.LogSaver;
import se.celekt.R;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adapter that describes the content of the list of attempts
 * It is used to customized its appearance using a custom xml layout for its elements
 * 
 * @author Lars Lorenz
 * @author Didac Gil
 * @author alisa
 * @version 1.0 
 */
public class AttemptAdapter extends ArrayAdapter<Attempt> {
	private  List<Attempt> attempts = new ArrayList<Attempt>();
	private LayoutInflater inflater;
	private Context context;
	
	public AttemptAdapter(Context context, int textViewResourceId, List<Attempt> attempts) {
		super(context, textViewResourceId,attempts);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.attempts = attempts;
		this.context = context;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if(view == null){
	    	view = inflater.inflate(R.layout.attempt_item_list,parent,false);
	    	
		}
		
		    TextView pointName = (TextView)view.findViewById(R.id.point);
			TextView countAttempt = (TextView)view.findViewById(R.id.attempt);
			String name = attempts.get(position).getPointName();
			pointName.setText(name);
			countAttempt.setText(String.valueOf(attempts.get(position).getCountAttempt()));
//			if(FoundManager.getInstance().isFound(name)){
//				setStyle(StateType.FOUND,view);
//			}
//			else {
//				setStyle(StateType.ATTEMPT,view);
//			}
		return view;
	}
	
	
	public void updateView(int count, int position){
		if(position >= 0){
			this.getItem(position).setCountAttempt(count);
			sort();
			LogSaver.getInstance().writeLog(LogSaver.ATTEMPTS, LogSaver.MessageType.INFO, "The attempts were updated");
			
			notifyDataSetChanged();
		}
	}
	
	public void updateAttempt(List<Attempt> newAttempts){
		this.clear();
		for(Attempt attempt:newAttempts){
			this.add(attempt);
			   
		}
		LogSaver.getInstance().writeLog(LogSaver.ATTEMPTS, LogSaver.MessageType.INFO, "The attempts were updated");
		
		notifyDataSetChanged();
	}
	
	public void sort(){
		Collections.sort(attempts, new Comparator<Attempt>() {

			public int compare(Attempt lhs, Attempt rhs) {
				return lhs.getPointName().compareTo(rhs.getPointName());
			}
		
			
		});
	}
	
	private void setStyle(StateType state, View view){
		TextView pointName = (TextView) view.findViewById(R.id.point);
		TextView countAttempt = (TextView) view.findViewById(R.id.attempt);
		
		Resources res = context.getResources();
		
		switch (state) {
			case ATTEMPT:
				pointName.setTextColor(res.getColor(R.color.online_contact_color));
				pointName.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
				countAttempt.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
								
		break;

			case FOUND:
								
				pointName.setTextColor(res.getColor(R.color.offline_contact_color));
				pointName.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
				countAttempt.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
			break;
			default:
				break;
			
		}
	}
    
    public enum StateType{
    	ATTEMPT,FOUND
	}
}
