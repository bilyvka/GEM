/*****************************************************************
 MVD is an application for Android based on JADE
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

package se.celekt.gem_app.jade.behaviours;

/**
 *  
 * @author Didac Gil
 * @version 1.0 
 */

public class BehaviourLauncher {
	
	private String behaviourClassPath;
//	private enum a {Subscription, Request, Publish}; 
	
	private String serviceType;
//	private String serviceName;

	private Object[] argument_list;  //the first argument is the agent launching the behaviour. the second Object is an Object list with Arguments
	
	
	public BehaviourLauncher(String behaviourClassPath){
		this.behaviourClassPath=behaviourClassPath;
	}
	
	public BehaviourLauncher(String behaviourClassPath, String service_type){
		this.behaviourClassPath = behaviourClassPath;
		this.serviceType = service_type;
	}

	public String getBehaviourName(){
		return this.behaviourClassPath;
	}
	
	public String getServiceType(){
		return this.serviceType;
	}
	
	public void setArguments(Object[] args){
		this.argument_list = args;
	}
	
	public Object[] getArguments(){
		return argument_list;
	}
	
}
