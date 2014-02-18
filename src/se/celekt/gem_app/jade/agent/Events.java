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
 
package se.celekt.gem_app.jade.agent;

import jade.util.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * This is an event. An event is basically a container object that carries
 * arbitrary data and has an unique name.
 * It is filled by the entity that fires the event.

 *
 */
public class Events {
	
		private Logger myLogger = Logger.getMyLogger(Events.class.getName());
		
		/**/
		public static final String TAB_PRESENTED="TAB_PRESENTED";

		
		/** Event that is fired when a new task arrives*/
		public static final String INCOMING_TASK_UPDATE_EVENT="INCOMING_TASK_UPDATE_EVENT";
		public static final String INCOMING_ANSWER_SENT_OK_EVENT="INCOMING_ANSWER_SENT_OK_EVENT";
		
		/** Event that is fired when a new distance to a device arrives (answer from calculating a distance)*/
		public static final String INCOMING_DISTANCE_EVENT="INCOMING_DISTANCE_EVENT";
		public static final String INCOMING_DISTANCE_PARAM_MSG="INCOMING_DISTANCE_PARAM_MSG";
		public static final String INCOMING_DISTANCE_PARAM_DEVICEID="INCOMING_DISTANCE_PARAM_DEVICEID";
		
		/** Event that is fired when a new attempt is registered*/
		public static final String INCOMING_ATTEMPT_EVENT="INCOMING_ATTEMPT_EVENT";
		public static final String INCOMING_ATTEMPT_PARAM_MSG="INCOMING_ATTEMPT_PARAM_MSG";
		public static final String INCOMING_ATTEMPT_PARAM_DEVICEID="INCOMING_ATTEMPT_PARAM_DEVICEID";
		public static final String REQUEST_ATTEMPTS_EVENT="REQUEST_ATTEMPTS_EVENT";
		public static final String UPTADE_ATTEMPTS_EVENT="UPDATE_ATTEMPTS_EVENT";
		
		/** Event that is fired when a new point found is registered*/
		public static final String INCOMING_FOUND_POINT_EVENT = "INCOMING_FOUND_POINT_EVENT";
		public static final String INCOMING_FOUND_POINT_PARAM_DEVICEID = "INCOMING_FOUND_POINT_PARAM_DEVICEID";
		public static final String INCOMING_FOUND_POINT_PARAM_MSG = "INCOMING_FOUND_POINT_PARAM_MSG";
		
		/** Event that is fired when a new point unfound is registered*/
		public static final String INCOMING_UNFOUND_POINT_EVENT = "INCOMING_UNFOUND_POINT_EVENT";
		public static final String INCOMING_UNFOUND_POINT_PARAM_DEVICEID = "INCOMING_UNFOUND_POINT_PARAM_DEVICEID";
		public static final String INCOMING_UNFOUND_POINT_PARAM_MSG = "INCOMING_UNFOUND_POINT_PARAM_MSG";
		

		/** Event that is fired when a new attempt is registered*/
		public static final String INCOMING_MEMBER_STATUS_EVENT="INCOMING_MEMBER_STATUS_EVENT";
		public static final String INCOMING_MEMBER_STATUS_MSG="INCOMING_MEMBER_STATUS_MSG";
		public static final String INCOMING_MEMBER_DEVICEID="INCOMING_ATTEMPT_PARAM_DEVICEID";

		
		/** Event that is fired when a change in the group (members) is registered*/
		public static final String INCOMING_GROUP_UPDATE_EVENT = "INCOMING_GROUP_UPDATE_EVENT";
		public static final String INCOMING_GROUP_UPDATE_DEVICEID = "INCOMING_GROUP_UPDATE_DEVICEID";
		public static final String INCOMING_GROUP_UPDATE_CONCERN = "INCOMING_GROUP_UPDATE_CONCERN";


        /** Event that is fired when the phone changes its GPS service state */
        public static final String GPS_SERVICE_CONNECT_EVENT = "GPS_SERVICE_CONNECT_EVENT";
        public static final String GPS_SERVICE_DISCONNECT_EVENT = "GPS_SERVICE_DISCONNECT_EVENT";


    /**
		 * Name of the event
		 */
		private  String name;
		
		/**
		 * Maps that stores event parameters
		 */
		private Map<String, Object> paramsMap;
		
		/**
		 * Returns the name of the event
		 * @return event name
		 */
		public final String getName() {
			return name;
			
		}
		
		/**
		 * Builds a new event
		 * @param name name of the event
		 */
		public Events(String name){
			this.name = name;
		}
		
		
		/**
		 * Adds a parameter to the event using the given name
		 * 
		 * @param name name of the parameter
		 * @param value value to be added
		 */
		public void addParam(String name, Object value){
			if (paramsMap == null){
				paramsMap = new HashMap<String, Object>();
			}
			
			paramsMap.put(name, value);
			myLogger.log(Logger.FINE,"putting in event map parameter " + name +" having value "+ value.toString() );
		}
		
		/**
		 * Retrieves a parameter from an event
		 * 
		 * @param name of the parameter to retrieve
		 * @return value of the parameter
		 */
		public Object getParam(String name){
			return paramsMap.get(name);
		}
}


