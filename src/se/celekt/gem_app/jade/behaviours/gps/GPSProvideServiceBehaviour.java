package se.celekt.gem_app.jade.behaviours.gps;


import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import se.celekt.gem_app.jade.agent.PhoneAgent;
import se.celekt.gem_app.objects.ContactLocation;
import se.celekt.gem_app.objects.ContactLocationManager;
import se.celekt.gem_app.util.LogSaver;

public class GPSProvideServiceBehaviour extends OneShotBehaviour{
	private static final long serialVersionUID = -3893860354702707608L;
	public static final String GPS_SERVICE_ONTOLOGY= "gps_service_ontology";
	public static final String gpsDescName = "HTC-hero-gps-service";
	
	/**  Type of the description of the service  */
	public static final String gpsDescType = "location-gps";
	
	private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());
	
//	public GPSPublishBehaviour(){
//		//Initiate variables if needed
//		//myLogger = Logger.getMyLogger(agent.getClass().getName());
//		myLogger.log(Logger.INFO, "GPSPublishBehaviour contructor has been called");
//	}
	
	public GPSProvideServiceBehaviour(Object[] arguments){
		//Initiate variables if needed
		//myLogger = Logger.getMyLogger(myAgent.getClass().getName());
		myLogger.log(Logger.INFO, "GPSPublishBehaviour contructor has been called");
	}
	
	@Override
	public void action() {
		// Adding the GPS Service in the Agent Description
		((PhoneAgent) myAgent).addServiceInDFAgentDescription(gpsDescName, gpsDescType);
		GPSResponseRequestBehaviour gpsProvB = new GPSResponseRequestBehaviour();
		myAgent.addBehaviour(gpsProvB);
//		myLogger.log(Logger.INFO, "GPSPublishing service has been registered as a cyclic service");
	}

	
	private class GPSResponseRequestBehaviour extends CyclicBehaviour {

		private static final long serialVersionUID = -8699458963111663026L;
		
		/**  Ontology used for filtering message */
		public static final String GPS_REQUEST_ONTOLOGY= "gps_request_ontology";
		
		//TODO
		@Override
		public void action() {

			//Filtering from the Message Pile the messages that match with the behaviour filters
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchOntology(GPS_REQUEST_ONTOLOGY), 
													 MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
			ACLMessage msg = myAgent.receive(mt);
			ACLMessage rsp = new ACLMessage(ACLMessage.CONFIRM);
			
			//If a message is received being a GPS answer to a REQUEST, we process there the information
			//with the behaviour we desire
			if(msg != null){
				myLogger.log(Logger.FINE, msg.toString());

				AID sender = msg.getSender();
				ContactLocation curMyLoc = ContactLocationManager.getInstance().getMyContactLocation();
				rsp.addReceiver(sender);
				rsp.setOntology(GPS_REQUEST_ONTOLOGY);
				rsp.setContent("This is my location: -"+ curMyLoc.getLatitude() +"-"+curMyLoc.getLongitude());
				myLogger.log(Logger.INFO, curMyLoc.toString());

				myAgent.send(rsp);

			}else{
				block();
			}
		}
	}
}
