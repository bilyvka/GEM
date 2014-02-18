/***
 * Inspired on jChat application from TelecomItalia
 */

package se.celekt.gem_app.jade.agent;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import android.os.Environment;
import android.os.StatFs;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import jade.wrapper.gateway.GatewayAgent;
import se.celekt.gem_app.jade.behaviours.BehaviourLauncher;
import se.celekt.gem_app.jade.behaviours.groups.GroupAnswerProcessBehaviour;
import se.celekt.gem_app.jade.behaviours.tasks.TaskanswerProcessorBehaviour;
import se.celekt.gem_app.objects.LocationMessage;
import se.celekt.mvd.groups.GroupManager;
import se.celekt.mvd.groups.MVDGroup;
import se.celekt.selfadaptation.mape.GPS.GPSKnowledge;
import se.celekt.selfadaptation.mape.GPS.GPSProbeBehaviour;
import se.celekt.selfadaptation.mape.GroupHealing.MVDKnowledge;
import se.celekt.selfadaptation.mape.GroupHealing.MVDMembershipMonitorMasterBehaviour;
import se.celekt.selfadaptation.mape.GroupHealing.MVDMembershipProbeBehaviour;
import se.celekt.selfadaptation.mape.GroupHealing.MVDPlan_ProcessResponsePhoneBehaviour;
import se.celekt.selfadaptation.mape.GroupHealing.PingEcho.PingEchoMasterBehaviour;
import se.celekt.selfadaptation.mape.GroupHealing.PingEcho.PingEchoSlaveBehaviour;

/**
 * Agent running all behaviours. It resides on the phone and it is responsible
 * for DF registration/subscription and for behaviour execution.
 * <p>
 * It extends GatewayAgent as requested by JADE Android add-on and is therefore
 * able to process commands sent through JadeGateway.execute(). Provides as an
 * inner class the behaviour responsible for receiving messages.
 * 
 * 
 * @author Cristina Cuc�
 * @author Marco Ughetti
 * @author Stefano Semeria
 * @author Tiziana Trucco
 * @version 1.0
 * 
 *          Modified and Extended by
 * @author Didac Gil
 * @version 1.1
 * @date 2010-11
 */
public class PhoneAgent extends GatewayAgent {

	private static final long serialVersionUID = -1917541758798934671L;

	static final String logGeneral_filepath = Environment.getExternalStorageDirectory().getPath()+"/GEM/GeM4_app_log.txt";
	static final String logAttempts_filepath = Environment.getExternalStorageDirectory().getPath()+"/GEM/GeM4_attempts_log.txt";

	/**
	 * Instance of JADE Logger for debugging
	 */
	private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());

	//private boolean iAmServer = false;
	private String roleInMVD = "Nothing";
	/**
	 * Name of service description to be registered on DF (allowing us to filter
	 * out the modifications performed by our application from others)
	 */
	public static final String msnDescName = "android-msn-service";

	/** Type of the description of the service */
	public static final String msnDescType = "android-msn";

	/**
	 * Name of service description to be registered on DF (allowing us to filter
	 * out the modifications performed by our application from others)
	 */

	/** Ontology used for sending message */
	public static final String CHAT_ONTOLOGY = "chat_ontology";

	/** Ontology used for subscribing to a single task */
	public static final String TASK_REQUEST_ONTOLOGY = "task_request_ontology";
	public static final String TASK_UPDATE_ONTOLOGY = "task_update_ontology";


	/** Ontology used for sending message of Group creation and negotiation */
	public static final String GROUP_ONTOLOGY = "group_ontology";
	public static final String GROUP_UPDATE_ONTOLOGY = "group_update_ontology";
	public static final String GROUP_UPDATE_UPDATE_PARTICIPANT_ONTOLOGY = "group_update_update_participant_ontology";
	public static final String GROUP_LEAVE_ONTOLOGY = "group_leave_ontology";
	public static final String GROUP_REQUEST_ONTOLOGY = "group_request_ontology";
	public static final String GROUPS_INFORM_ONTOLOGY = "groups_inform_ontology";

	/** Ontology used for sending messages of MVD creation */
	public static final String REMOVE_MVD_ONTOLOGY = "remove_mvd_ontology";

	public static final String ANSWER_SUBMIT_ONTOLOGY = "answer_submit_ontology";

	/** Description of Msn service */
	private DFAgentDescription myDescription;

	/** DF Subscription message */
	private ACLMessage subscriptionMessage;
	private Map<String, ACLMessage> subscriptionMessages = new HashMap<String, ACLMessage>();

//	/** Instance of {@link ContactsUpdaterBehaviour} */
//	private ContactsUpdaterBehaviour contactsUpdaterB;
	
	/** Instance of {@link GPSProbeBehaviour} */
	private GPSProbeBehaviour gpsProbe;
	private MVDMembershipProbeBehaviour mvdProbeB;
	private MVDMembershipMonitorMasterBehaviour mvdMonitorMasterB;

	/**
	 * Message receiver behaviour instance
	 */
	private MessageReceiverBehaviour messageRecvB;
//	private OfferGroupsBehaviour     offerGroupB;


	// private TasksUpdaterBehaviour tasksUpdaterB;

	private TaskanswerProcessorBehaviour taskUpdaterB;
	private GroupAnswerProcessBehaviour groupUpdatedB;
	private PingEchoMasterBehaviour pingEchoMB;
	private PingEchoSlaveBehaviour pingEchoSB;
	private MVDPlan_ProcessResponsePhoneBehaviour planProcessResponses;
//    private AttemptBehaviour attemptB;
//    private MemberBehaviuor memberBehaviuor;
    //private GPSPublishBehaviour gpsPublishB;
	// private GPSanswerProcessorBehaviour gpsAnswerB;

	private int attempts = 0;

	/**
	 * Method to share the DFAgentDescription and allow behaviours to know the
	 * current description of the agent
	 * 
	 * @return myDescription
	 */
	public void addServiceInDFAgentDescription(String descName, String descType) {

		ServiceDescription newServiceDescription = new ServiceDescription();
		newServiceDescription.setName(descName);
		newServiceDescription.setType(descType);
		// publishing the new description with the new service
		myDescription.addServices(newServiceDescription);

		try {
			DFService.modify(this, myDescription);
		} catch (FIPAException e) {
			myLogger.log(Logger.WARNING,
					"Problems adding a service in the agent");
			e.printStackTrace();
		}
	}

	/**
	 * Method to update the DFAgentDescription of the agent, which will allow to
	 * add and remove services
	 * 
	 *
	 */
	public void removeServiceInDFAgentDescription(String descName,	String descType) {
		ServiceDescription newServiceDescription = new ServiceDescription();
		newServiceDescription.setName(descName);
		newServiceDescription.setType(descType);
		myDescription.removeServices(newServiceDescription);
		try {
			DFService.modify(this, myDescription);
		} catch (FIPAException e) {
			myLogger.log(Logger.WARNING, "Problems removing a service in the agent");
			e.printStackTrace();
		}
	}

	/**
	 * Overrides the Agent.setup() method, performing registration on DF,
	 * prepares the DF subscription message, and adds theContactsUpdater Behaviour
	 * 
	 */
	protected void setup() {
		super.setup();
		Thread.currentThread().getId();
		myLogger.log(Logger.INFO, "setup() called: My currentThread has this ID: "	+ Thread.currentThread().getId());
		myDescription = new DFAgentDescription();
		// fill a msn service description
		ServiceDescription msnServiceDescription = new ServiceDescription();
		msnServiceDescription.setName(msnDescName);
		msnServiceDescription.setType(msnDescType);
		myDescription.addServices(msnServiceDescription);

		// subscribe to DF
		subscriptionMessage = DFService.createSubscriptionMessage(this,	this.getDefaultDF(), myDescription, null);

//		ContactLocation curLoc = ContactManager.getInstance().getMyContactLocation();
//
//		Property p = new Property(PROPERTY_NAME_LOCATION_LAT, Double.valueOf(curLoc.getLatitude()));
//		msnServiceDescription.addProperties(p);
//		p = new Property(PROPERTY_NAME_LOCATION_LONG, Double.valueOf(curLoc.getLongitude()));
//		msnServiceDescription.addProperties(p);
//		p = new Property(PROPERTY_NAME_LOCATION_ALT, Double.valueOf(curLoc.getAltitude()));
//		msnServiceDescription.addProperties(p);
		myDescription.setName(this.getAID());

		try {
			myLogger.log(Logger.INFO, "Registering in DF!");
			DFService.register(this, myDescription);
//			myLogger.log(Logger.INFO, "Registered in the DF!");
		} catch (FIPAException e) {
			e.printStackTrace();
            myLogger.log(Logger.SEVERE, e.getMessage());
		}

		
		//ADDITIONAL BEHAVIOURS!!!!
		// added behaviour to dispatch chat messages
		messageRecvB = new MessageReceiverBehaviour();
		addBehaviour(messageRecvB);
		String[] args = (String[]) getArguments();
		myLogger.log(Logger.INFO, "UPDATE TIME: " + args[0]);


		// added behaviour to update our current location on the DF and to
		// retrieve other contacts
		
		GPSKnowledge.getInstance().setFreq(500);
		GPSKnowledge.getInstance().setMinDelta(3);
		gpsProbe = new GPSProbeBehaviour();
		addBehaviour(gpsProbe);
		
		MVDKnowledge.getInstance().setFreq(500);
		this.mvdProbeB = new MVDMembershipProbeBehaviour();
		addBehaviour(mvdProbeB);
//
		// TODO
		// This calls should be removed from the Agent Implementation, but
		// called by the JADEGateway.execute() method, giving a
		// BehaviourLauncher object

		// added behaviour to receive new tasks
		taskUpdaterB = new TaskanswerProcessorBehaviour(this);
		addBehaviour(taskUpdaterB);

		groupUpdatedB = new GroupAnswerProcessBehaviour(this);
		addBehaviour(groupUpdatedB);
		
	}

    public void addBehaviuors(){

    }

	/*
	 * This method is called in case a Device is the master of a MVD
	 */
	public void addMasterBehaviour(){
		if(this.roleInMVD.equals("Master")){
			//Nothing to do. I am already running the master behaviour
		}else{
			if(this.roleInMVD.equals("Slave")){
				removeSlaveBehaviour();
			}
			this.roleInMVD = "Master";
			String[] args = (String[]) getArguments();
		    pingEchoMB = new PingEchoMasterBehaviour(this, Long.parseLong(args[0]));
		    addBehaviour(pingEchoMB);
		    
		    planProcessResponses = new MVDPlan_ProcessResponsePhoneBehaviour();
		    addBehaviour(planProcessResponses);

	        mvdMonitorMasterB = new MVDMembershipMonitorMasterBehaviour();
		    addBehaviour(mvdMonitorMasterB);
		}
	}
	
	/*
	 * This method is called in case a Device is a slave of a MVD
	 */
	public void addSlaveBehaviour() {
		if(this.roleInMVD.equals("Slave")){
			//Nothing to do. I am already running the slave behaviour
		}else{
			if(this.roleInMVD.equals("Master")){
				removeMasterBehaviour();
			}
			this.roleInMVD = "Slave";
			String[] args = (String[]) getArguments();
		pingEchoSB = new PingEchoSlaveBehaviour(this, Long.parseLong(args[0]));
		addBehaviour(pingEchoSB);
		}
	}
	
	public void removeMasterBehaviour() {
		removeBehaviour(pingEchoMB);
		removeBehaviour(mvdMonitorMasterB);
		removeBehaviour(planProcessResponses);
	}
	
	public void removeSlaveBehaviour() {
		removeBehaviour(pingEchoSB);
	}
	
	
	
	
	/**
	 * Gets the DF subscription message.
	 * 
	 * @return the DF subscription message
	 */
	public ACLMessage getSubscriptionMessage() {
		return subscriptionMessage;
	}

	public ACLMessage getSubscriptionMessage(String key) {
		return subscriptionMessages.get(key);
	}

	/**
	 * Gets the agent description.
	 * 
	 * @return the agent description
	 */
	public DFAgentDescription getAgentDescription() {
		return myDescription;
	}

	/**
	 * Overrides agent takeDown() method
	 */
	protected void takeDown() {
		myLogger.log(Logger.INFO, "Doing agent takeDown() ");
	}

	/**
	 * Allows to look for services declared in the DF
	 * 
	 * @param type
	 * @return a list of DFAgentsDescriptions with the AID that provide that
	 *         service type
	 */
	public DFAgentDescription[] getServiceProviders(String type) {
		myLogger.log(Logger.FINE,
				"getServiceProviders was called with the type:" + type);
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(type);
		dfd.addServices(sd);

		DFAgentDescription[] result;
		try {
			result = DFService.search(this, dfd);
			System.out.println(result.length + " results");
			if (result.length > 0)
				System.out.println(" " + result[0].getName());
			return result;

		} catch (FIPAException e) {
			myLogger.log(Logger.WARNING, "Couldn't handle it");
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private Object createObject(Constructor<?> constructor, Object[] arguments) {

		Object object = null;

		try {
			object = constructor.newInstance(arguments);
			return object;
		} catch (InstantiationException e) {
			myLogger.log(Logger.FINE, "ClassNotFound");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			myLogger.log(Logger.FINE, "ClassNotFound");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			myLogger.log(Logger.FINE, "ClassNotFound");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			myLogger.log(Logger.FINE, "ClassNotFound");
			e.printStackTrace();
		}
		return object;
	}

	
	/**
	 * Overrides GatewayAgent.processCommand(). Receives a command from
	 * JadeGateway.execute() The behaviour for sending a message in particular
	 * is received in this way
	 * 
	 * @param command
	 *            a generic command that this agent shall execute.
	 */
	protected void processCommand(final Object command) {
		if (command instanceof Behaviour) {
			myLogger.log(Logger.INFO, "Processing a new behaviour");
			myLogger.log(Logger.INFO,
					"    to run in agent " + this.getLocalName());
			addBehaviour((Behaviour) command);

		}
//		 else{
//			//We are asking the Agent to start a new behavior that has been filled in with the BehaviourLauncher
//			if (command instanceof BehaviourLauncher) {
//				myLogger.log(Logger.INFO, "Processing a new dynamically loaded behaviour");
//				try{
//					String behaviourClassPath = ((BehaviourLauncher) command).getBehaviourName();
//					myLogger.log(Logger.INFO, "Behaviour to load: "	+ behaviourClassPath);
//					
//					LoadBehaviour lb = new LoadBehaviour();
//					lb.setClassName(behaviourClassPath);
//				} catch (Exception e){
//					myLogger.log(Logger.SEVERE, "ClassNotFound of Issues while loading the class contructor");
//					e.printStackTrace();
//				}
//				myLogger.log(Logger.FINE, "Behaviour added");
//			}
//		} 
		
		else {
			//We are asking the Agent to start a new behavior that has been filled in with the BehaviourLauncher
			if (command instanceof BehaviourLauncher) {
				myLogger.log(Logger.INFO, "Processing a new dynamically loaded behaviour");
				try {
					String behaviourClassPath = ((BehaviourLauncher) command).getBehaviourName();
					myLogger.log(Logger.INFO, "Behaviour to load: "	+ behaviourClassPath);
					Object[] argument_list = ((BehaviourLauncher) command).getArguments();

					Class<?> classInstance = Class.forName(behaviourClassPath);
					Constructor<?>[] constructors = classInstance.getDeclaredConstructors();

					for (int i = 0; i < constructors.length; i++) {
						Constructor<?> constructor = constructors[i];

						Object[] arguments = {/* this,*/ argument_list };
						Object obj = createObject(constructor, arguments);
						addBehaviour((Behaviour) obj);
					}
				} catch (Exception e) {
					myLogger.log(Logger.WARNING,	"ClassNotFound or Crashing while Loading the contructor");
					e.printStackTrace();
				}
				myLogger.log(Logger.FINE, "Behaviour added");
			}
		}
		releaseCommand(command);
	}

	/**
	 * Defines the behaviour for receiving chat messages. Each time a message is
	 * received, a UI feedback is required (adding message to message window or
	 * adding a notification). The {@link jade.core.behaviours.CyclicBehaviour} continuously executes
	 * its action method and does something as soon as a message arrives.
	 */
	private class MessageReceiverBehaviour extends CyclicBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 932445511064091097L;

		private int memspaceProviderAgents = 2; // value that should be updated
												// by a DFAgentDescription call,
												// looking for providers.
		private int memspaceRepliesCnt = 0; // This will help us to determine if
											// we got answers to the proposal
											// from all the providers
		private int memspaceStep = 0; // To control if we reached already the
										// amount of replies we were expecting
										// to this CFP
		private AID bestMemSpaceProvider = null;
		private int bestSpace = 0;

		private int fastCopyProviderAgents = 2;
		private int fastCopyRepliesCnt = 0;
		private int fastCopyStep = 0;
		private AID bestFastCopyProvider = null;
		private String bestFastCopyDate = "";

		private int copyProviderAgents = 2;
		private int copyRepliesCnt = 0;
		private int copyStep = 0;
		private AID bestCopyProvider = null;
		private String bestCopy = "";

//		private TransferActivity updateActivity(TransferActivity destAct,
//				TransferActivity origAct) {
//			// List<TransferTask> taskList = destAct.getTasks();
//
//			return destAct;
//		}

		/**
		 * Overrides the Behaviour.action() method and receives messages. After
		 * a message is received the following operations take place:
		 * <ul>
		 * <li>If the message is related to a new conversation (it is the first
		 * message received) a new session is created, otherwise the session is
		 * retrieved
		 * <li>The message is added to the conversation
		 * <li>an event for a new message is sent to update the GUI in the
		 * appropriate way
		 * </ul>
		 */
		public void action() {

			try {
		
				
				// CHAT
				MessageTemplate mtChat = MessageTemplate
						.MatchOntology(CHAT_ONTOLOGY);
				ACLMessage msgChat = myAgent.receive(mtChat);
				// END CHAT

				// GROUP CREATTION
				MessageTemplate mtGroup = MessageTemplate
						.MatchOntology(GROUP_ONTOLOGY);
				ACLMessage msgGroup = myAgent.receive(mtGroup);
				// END GROUP CREATION

				// GROUP UPDATE
				MessageTemplate mtRGroupUpdate = MessageTemplate.and(
						MessageTemplate.MatchOntology(GROUP_UPDATE_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
				ACLMessage msgRGroupUpdate = myAgent.receive(mtRGroupUpdate);
				MessageTemplate mtCGroupUpdate = MessageTemplate.and(
						MessageTemplate.MatchOntology(GROUP_UPDATE_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
				ACLMessage msgCGroupUpdate = myAgent.receive(mtCGroupUpdate);
				MessageTemplate mtIGroupUpdate = MessageTemplate.and(
						MessageTemplate.MatchOntology(GROUP_UPDATE_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				ACLMessage msgIGroupUpdate = myAgent.receive(mtIGroupUpdate);
				MessageTemplate mtXGroupUpdate = MessageTemplate.and(
						MessageTemplate.MatchOntology(GROUP_UPDATE_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.CANCEL));
				ACLMessage msgXGroupUpdate = myAgent.receive(mtXGroupUpdate);
				// END GROUP UPDATE

				// GROUP UPDATE - PARTICIPANT
				MessageTemplate mtRParticipantUpdate = MessageTemplate
						.and(MessageTemplate
								.MatchOntology(GROUP_UPDATE_UPDATE_PARTICIPANT_ONTOLOGY),
								MessageTemplate
										.MatchPerformative(ACLMessage.REQUEST));
				ACLMessage msgRParticpantUpdate = myAgent
						.receive(mtRParticipantUpdate);


				// GROUP REQUEST
				MessageTemplate mtRGroupRequest = MessageTemplate.and(
						MessageTemplate.MatchOntology(GROUP_REQUEST_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
				ACLMessage msgRGroupRequest = myAgent.receive(mtRGroupRequest);
				MessageTemplate mtCGroupRequest = MessageTemplate.and(
						MessageTemplate.MatchOntology(GROUP_REQUEST_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
				ACLMessage msgCGroupRequest = myAgent.receive(mtCGroupRequest);
				// END GROUP REQUEST

				// GROUP LEAVE
//				MessageTemplate mtIGroupLeave = MessageTemplate.and(
//						MessageTemplate.MatchOntology(GROUP_LEAVE_ONTOLOGY),
//						MessageTemplate.MatchPerformative(ACLMessage.INFORM));
//				ACLMessage msgIGroupLeave = myAgent.receive(mtIGroupLeave);
				// END GROUP LEAVE

				// REMOVE MVD
				MessageTemplate mtRemoveMVD = MessageTemplate.and(
						MessageTemplate.MatchOntology(REMOVE_MVD_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				ACLMessage msgRemoveMVD = myAgent.receive(mtRemoveMVD);
				// END REMOVE MVD

				// GPS
				String GPS_REQUEST_ONTOLOGY = "gps_request_ontology";
//				MessageTemplate mtRGPS = MessageTemplate.and(
//						MessageTemplate.MatchOntology(GPS_REQUEST_ONTOLOGY),
//						MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
//				ACLMessage msgRGPS = myAgent.receive(mtRGPS);

				MessageTemplate mtCGPS = MessageTemplate.and(
						MessageTemplate.MatchOntology(GPS_REQUEST_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
				ACLMessage msgCGPS = myAgent.receive(mtCGPS);
				// END GPS

				// TASK
				MessageTemplate mtCTask = MessageTemplate.and(
						MessageTemplate.MatchOntology(TASK_REQUEST_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
				ACLMessage msgCTask = myAgent.receive(mtCTask);

				MessageTemplate mtITaskUpdate = MessageTemplate.and(
						MessageTemplate.MatchOntology(TASK_UPDATE_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				ACLMessage msgITaskUpdate = myAgent.receive(mtITaskUpdate);
				// END TASK

				// SEND ANSWERS
				MessageTemplate mtCAnswer = MessageTemplate.and(
						MessageTemplate.MatchOntology(ANSWER_SUBMIT_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
				ACLMessage msgCAnswer = myAgent.receive(mtCAnswer);
				MessageTemplate mtIAnswer = MessageTemplate.and(
						MessageTemplate.MatchOntology(ANSWER_SUBMIT_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				ACLMessage msgIAnswer = myAgent.receive(mtIAnswer);
				// END SEND ANSWERS

				// MEMORYSPACE
				String MEMSPACE_REQUEST_ONTOLOGY = "memspace_request_ontology";
				MessageTemplate mtCFPMemSpace = MessageTemplate.and(
						MessageTemplate
								.MatchOntology(MEMSPACE_REQUEST_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.CFP));
				ACLMessage msgCFPMemSpace = myAgent.receive(mtCFPMemSpace);

				MessageTemplate mtPMemSpace = MessageTemplate.and(
						MessageTemplate
								.MatchOntology(MEMSPACE_REQUEST_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
				ACLMessage msgPMemSpace = myAgent.receive(mtPMemSpace);

				MessageTemplate mtAPMemSpace = MessageTemplate.and(
						MessageTemplate
								.MatchOntology(MEMSPACE_REQUEST_ONTOLOGY),
						MessageTemplate
								.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
				ACLMessage msgAPMemSpace = myAgent.receive(mtAPMemSpace);

				MessageTemplate mtIMemSpace = MessageTemplate.and(
						MessageTemplate
								.MatchOntology(MEMSPACE_REQUEST_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				ACLMessage msgIMemSpace = myAgent.receive(mtIMemSpace);
				// END MEMORYSPACE

				// MEMORY RETRIEVAL
				String MEMRETRIEVAL_REQUEST_ONTOLOGY = "memretrieval_request_ontology";
				MessageTemplate mtCFPMemRetrieval = MessageTemplate.and(
						MessageTemplate
								.MatchOntology(MEMRETRIEVAL_REQUEST_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.CFP));
				ACLMessage msgCFPMemRetrieval = myAgent
						.receive(mtCFPMemRetrieval);

				MessageTemplate mtPMemRetrieval = MessageTemplate.and(
						MessageTemplate
								.MatchOntology(MEMRETRIEVAL_REQUEST_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
				ACLMessage msgPMemRetrieval = myAgent.receive(mtPMemRetrieval);

				MessageTemplate mtAPMemRetrieval = MessageTemplate.and(
						MessageTemplate
								.MatchOntology(MEMRETRIEVAL_REQUEST_ONTOLOGY),
						MessageTemplate
								.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
				ACLMessage msgAPMemRetrieval = myAgent
						.receive(mtAPMemRetrieval);

				MessageTemplate mtIMemRetrieval = MessageTemplate.and(
						MessageTemplate
								.MatchOntology(MEMRETRIEVAL_REQUEST_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				ACLMessage msgIMemRetrieval = myAgent.receive(mtIMemRetrieval);
				// END MEMORY RETRIEVAL

				// MEMORY RETRIEVAL SHORT
				MessageTemplate mtRMemRetrieval = MessageTemplate.and(
						MessageTemplate
								.MatchOntology(MEMRETRIEVAL_REQUEST_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
				ACLMessage msgRMemRetrieval = myAgent.receive(mtRMemRetrieval);

				MessageTemplate mtCMemRetrieval = MessageTemplate.and(
						MessageTemplate
								.MatchOntology(MEMRETRIEVAL_REQUEST_ONTOLOGY),
						MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
				ACLMessage msgCMemRetrieval = myAgent.receive(mtCMemRetrieval);

				myLogger.log(Logger.FINE, "Here I am, and maybe I am getting messages you dont want me to process... sorry");

				// ATTEMPT
//				if (msgIAttempt != null) {
//					myLogger.log(Logger.INFO, msgIAttempt.toString());
//
//					// prepare the Model with information, and call the
//					// controler so it updates the view
//					// prepare an "IncomingMessage"
//					AttemptMessage attemptMessage = new AttemptMessage(msgIAttempt);
//					Events event = EventsMgr.getInstance().createEvent(Events.INCOMING_ATTEMPT_EVENT);
//					event.addParam(Events.INCOMING_ATTEMPT_PARAM_DEVICEID, msgIAttempt.getSender().getLocalName());
//					event.addParam(Events.INCOMING_ATTEMPT_PARAM_MSG,	attemptMessage);
//					myLogger.log(Logger.FINE,	"This is the event that will be launced for the Attempt update: " + event.toString());
//					// Send the message to the main Controler to deal with it
//					EventsMgr.getInstance().fireEvent(event);
//				}
				// END ATTEMPT

							
				
				// CHAT
				// MESSAGE------------------------------------------------------------------------------------------------------------------------------------
				if (msgChat != null) {
//					myLogger.log(Logger.FINE, msgChat.toString());
//					MsnSessionManager sessionManager = MsnSessionManager
//							.getInstance();
//
//					// retrieve the session id
//					String sessionId = msgChat.getConversationId();
//					myLogger.log(Logger.FINE,
//							"Received Message... session ID is " + sessionId);
//					String senderPhoneNum = msgChat.getSender().getLocalName();
//
//					// Create a session Message from the received ACLMessage
//					MsnSessionMessage sessionMessage = new MsnSessionMessage(msgChat);
//
//					// Check if we can retrieve a session. If so we should have
//					// got a copy
//					MsnSession currentSession = MsnSessionManager.getInstance().retrieveSession(sessionId);
//
//					// If we have a new session
//					if (currentSession == null) {
//						// Create a new session with the specified ID
//						sessionManager.addMsnSession(sessionId,	msgChat.getAllReceiver(), senderPhoneNum);
//					}
//
//					// prepare an "IncomingMessage"
//					Events event = EventsMgr.getInstance().createEvent(	Events.INCOMING_MESSAGE_EVENT);
//					event.addParam(Events.INCOMING_MESSAGE_PARAM_SESSIONID,	sessionId);
//					event.addParam(Events.INCOMING_MESSAGE_PARAM_MSG,	sessionMessage);
//					// Add message to session
//					sessionManager.addMessageToSession(sessionId,	sessionMessage);
//					EventsMgr.getInstance().fireEvent(event);
				}
				// END CHAT


				// GROUP UPDATE
				else if (msgRGroupUpdate != null) {
					// We got a message from a device saying that he has an
					// update on groups. This should trigger the controler (from
					// the M-V-C),
					// creating a model with the information, and calling the
					// viewer so the information is displayed
					// We will answer with the confirmation or cancel of the
					// groups updates.
					myLogger.log(Logger.INFO, msgRGroupUpdate.toString());

					// Notification back (check if the group can be done or goes
					// against some rules that this agents knows)
					// TODO
					String reason = "";
					if (false) {
						ACLMessage rsp = new ACLMessage(ACLMessage.CANCEL); // Performative
						rsp.setOntology(msgRGroupUpdate.getOntology()); // Ontology
						rsp.setContent(reason); // Content
						rsp.addReceiver(msgRGroupUpdate.getSender()); // Receiver

						myAgent.send(rsp);
						myLogger.log(Logger.FINE, rsp.toString());
					} else {
						// Call the GroupManager to update the groups

						Map<String,MVDGroup> groupMap = (Map<String,MVDGroup>) msgRGroupUpdate.getContentObject();
						Set<String> concerns = groupMap.keySet();
						String concern = concerns.iterator().next();
						GroupManager.getInstance().updateGroup(groupMap.get(concern));

                        myLogger.log(Logger.FINE, "CONCERN :" + concern);
						
						// Preparing an event for the Viewer to present the
						// changes in the groups
						Events event = EventsMgr.getInstance().createEvent(Events.INCOMING_GROUP_UPDATE_EVENT);
						event.addParam(Events.INCOMING_GROUP_UPDATE_CONCERN,concern);
						EventsMgr.getInstance().fireEvent(event);

						// Answer to the origin saying that the updates were
						// performed
						ACLMessage rsp = new ACLMessage(ACLMessage.CONFIRM);
						rsp.setOntology(msgRGroupUpdate.getOntology());
						rsp.setContent("");
						rsp.addReceiver(msgRGroupUpdate.getSender());
						myAgent.send(rsp);
						myLogger.log(Logger.FINE, rsp.toString());
					}
				}

				else if (msgCGroupUpdate != null) {
					// Confirmation from devices that the new update has been
					// applied
					myLogger.log(Logger.FINE, msgCGroupUpdate.toString());
					// TODO
					// Right now we do not care much about it, just in case we
					// want to process something in the future when the others
					// notify that they agreed with the new organizations
				}

				else if (msgIGroupUpdate != null) {
					// We got a message from a device saying that he has an
					// update on groups that should be applied. This should
					// trigger the controler (from the M-V-C),
					// creating a model with the information, an calling the
					// viewer so the information is displayed.
					// We will answer just there is a problem implementing this
					// new group.
					myLogger.log(Logger.FINE, msgIGroupUpdate.toString());

					// Notification back (check if the group can be done or goes
					// against some rules that this agents knows)
					// TODO
					String reason = "";
					if (false) {
						ACLMessage rsp = new ACLMessage(ACLMessage.CANCEL); // Performative
						rsp.setOntology(msgCGroupUpdate.getOntology()); // Ontology
						rsp.setContent(reason); // Content
						rsp.addReceiver(msgCGroupUpdate.getSender()); // Receiver

						myAgent.send(rsp);
						myLogger.log(Logger.FINE, rsp.toString());
					} else {
						// Call the GroupManager to update the groups
						@SuppressWarnings("unchecked")
					//GroupManager.getInstance().updateGroups(groupMap);

						// Preparing an event for the Viewer to present the
						// changes in the groups
						Events event = EventsMgr.getInstance().createEvent(	Events.INCOMING_GROUP_UPDATE_EVENT);
						event.addParam(Events.INCOMING_GROUP_UPDATE_DEVICEID,	"");
						EventsMgr.getInstance().fireEvent(event);
					}
				} else if (msgXGroupUpdate != null) {
					// One device does not agree with the new organizations
					// structures, so it needs to be rolled-back, or deal with
					// the problems to
					// reach an agreement with the devices that are complaining
					// TODO

				}
				// END GROUP UPDATE

				// GROUP UPDATE PARTICIPANT
				else if (msgRParticpantUpdate != null) {
					// We got a message from a device saying that he has an
					// update on a participant joining a group. This should
					// trigger the controler (from the M-V-C),
					// creating a model with the information, and calling the
					// viewer so the information is displayed
					// We will answer with the confirmation or cancel of the
					// groups updates.
					myLogger.log(Logger.INFO, msgRParticpantUpdate.toString());
					String senderPhoneName = msgRParticpantUpdate.getSender().getLocalName(); // Name of the device sending the
												// message

					// Notification back (check if the group can be done or goes
					// against some rules that this agents knows)
					// TODO
					String reason = "";
					if (false) {
						ACLMessage rsp = new ACLMessage(ACLMessage.CANCEL); // Performative
						rsp.setOntology(msgRParticpantUpdate.getOntology()); // Ontology
						rsp.setContent(reason); // Content
						rsp.addReceiver(msgRParticpantUpdate.getSender()); // Receiver

						myAgent.send(rsp);
						myLogger.log(Logger.FINE, rsp.toString());
					} else {
						// Call the GroupManager to update the groups updating
						// the group where the participant is added or removed

						String content = msgRParticpantUpdate.getContent();
						String[] contentArray = content.split("-");
						String action = contentArray[0];
						String groupName = contentArray[1];
						String participantID = senderPhoneName;

						// The participant is added
						if (action.equals("add")) {
							//Participant p = new Participant(participantID, 0,"");
							//GroupManager.getInstance().addMemberToGroup(groupName, p);
						}

						// The participant is removed
						else if (action.equals("remove")) {
							//GroupManager.getInstance().removeMemberFromGroup(groupName, participantID);
						}

						// Preparing an event for the Viewer to present the
						// changes in the groups
						Events event = EventsMgr.getInstance().createEvent(	Events.INCOMING_GROUP_UPDATE_EVENT);
						event.addParam(Events.INCOMING_GROUP_UPDATE_DEVICEID,	participantID);
						EventsMgr.getInstance().fireEvent(event);

						// Answer to the origin saying that the updates were
						// performed
						ACLMessage rsp = new ACLMessage(ACLMessage.CONFIRM);
						rsp.setOntology(msgRParticpantUpdate.getOntology());
						rsp.setContent("");
						rsp.addReceiver(msgRParticpantUpdate.getSender());
						myAgent.send(rsp);
						myLogger.log(Logger.FINE, rsp.toString());
					}
				}
				// END GROUP UPDATE PARTICIPANT

				// GROUP REQUEST
				else if (msgRGroupRequest != null){
					// We have provided a service to offer Groups, and there is a phone using us!!!
					// We should answer him as we were the main server... who may be down right now
					myLogger.log(Logger.FINE, msgRGroupRequest.toString());
					//HashMap<String, ParticipantGroup> groupMap = GroupManager.getInstance().getGroups();
					ACLMessage rsp = new ACLMessage(ACLMessage.CONFIRM);  // Performative
					rsp.setOntology(msgRGroupRequest.getOntology());  //Ontology
					//rsp.setContentObject(groupMap);	// Content
					rsp.addReceiver(msgRGroupRequest.getSender()); //Receiver
					
					myAgent.send(rsp);
					myLogger.log(Logger.FINE, rsp.toString());
				}
				
				else if (msgCGroupRequest != null) {
					// We got a message from a device saying that he has an
					// update on groups that should be applied. This should
					// trigger the controler (from the M-V-C),
					// creating a model with the information, an calling the
					// viewer so the information is displayed.
					// We will answer just there is a problem implementing this
					// new group.
					myLogger.log(Logger.FINE, msgCGroupRequest.toString());

					// Notification back (check if the group can be done or goes
					// against some rules that this agents knows)
					// TODO
					String reason = "";
					if (false) {
						ACLMessage rsp = new ACLMessage(ACLMessage.CANCEL); // Performative
						rsp.setOntology(msgCGroupUpdate.getOntology()); // Ontology
						rsp.setContent(reason); // Content
						rsp.addReceiver(msgCGroupUpdate.getSender()); // Receiver

						myAgent.send(rsp);
						myLogger.log(Logger.FINE, rsp.toString());
					} else {
						// Call the GroupManager to update the groups
						//GroupManager.getInstance().updateGroups(groupMap);

						// Preparing an event for the Viewer to present the
						// changes in the groups
						Events event = EventsMgr.getInstance().createEvent(Events.INCOMING_GROUP_UPDATE_EVENT);
						event.addParam(Events.INCOMING_GROUP_UPDATE_DEVICEID,		"");
						EventsMgr.getInstance().fireEvent(event);
					}
				}

				// END GROUP REQUEST

				// REMOVE MVD
				else if (msgRemoveMVD != null) {
					// removing the local MVD now and setting the flag to
					// no-answered
					try {
						
					} catch (Exception e) {

					}

				}
				// END REMOVE MVD

				// GPS------------------------------------------------------------------------------------------------------------------------------------
//				else if (msgRGPS != null) {
//
//					// If a message is received being a GPS answer to a REQUEST,
//					// we process there the information
//					// with the behaviour we desire
//
//					myLogger.log(Logger.INFO, msgRGPS.toString());
//					ContactLocation curMyLoc = ContactManager.getInstance().getMyContactLocation();
//					// I should check here if the current location is valid.
//					ContactLocation location;
//					if (curMyLoc.getLatitude() == Double.POSITIVE_INFINITY	|| curMyLoc.getLongitude() == Double.POSITIVE_INFINITY) {
//						location = new ContactLocation(curMyLoc);
//					} else {
//						location = new ContactLocation(curMyLoc);
//					}
//					ACLMessage rsp = new ACLMessage(ACLMessage.CONFIRM); // Performative
//					rsp.setOntology(msgRGPS.getOntology()); // Ontology
//					rsp.setContent(location.getLatitude() + "-"	+ location.getLongitude()); // Content
//					rsp.addReceiver(msgRGPS.getSender()); // Receiver
//
//					myAgent.send(rsp);
//					myLogger.log(Logger.FINE, rsp.toString());
//
//				}
				// This is a message with the location of a college
				else if (msgCGPS != null) {
					myLogger.log(Logger.INFO, msgCGPS.toString());

					// prepare the Model with information, and call the
					// controler so it updates the view
					// prepare an "IncomingMessage"
					LocationMessage locationMessage = new LocationMessage(msgCGPS);
					Events event = EventsMgr.getInstance().createEvent(Events.INCOMING_DISTANCE_EVENT);
					event.addParam(Events.INCOMING_DISTANCE_PARAM_DEVICEID,msgCGPS.getSender().getLocalName());
					event.addParam(Events.INCOMING_DISTANCE_PARAM_MSG,locationMessage);
					myLogger.log(Logger.FINE,	"An event will be launced for the GPS update");
					// Send the message to the main Controler to deal with it
					EventsMgr.getInstance().fireEvent(event);

				}
				// END
				// GPS------------------------------------------------------------------------------------------------------------------------------------

				// TASK
				else if (msgCTask != null) {
					myLogger.log(Logger.FINE, msgCTask.toString());

//					TransferActivity activity = (TransferActivity) msgCTask.getContentObject();
//					ActivityManager.getInstance().updateActivity(activity);

					// Preparing an event for the Viewer to present the
					// changes in the tasks
					Events event = EventsMgr.getInstance().createEvent(	Events.INCOMING_TASK_UPDATE_EVENT);
					EventsMgr.getInstance().fireEvent(event);

				}

				else if (msgITaskUpdate != null) {
					myLogger.log(Logger.FINE, msgITaskUpdate.toString());
					// Notification from another member in my group with new
					// answers!
					// GroupManager.getInstance().getGroups();

					// Retrieve the Object, which it will be an Activity, look
					// for changes in relation with
					// my activity, and update with the new values.
//					TransferActivity myActivity = ActivityManager.getInstance().getActivity();
//					TransferActivity outActivity = (TransferActivity) msgITaskUpdate.getContentObject();
//					myActivity = updateActivity(myActivity, outActivity);

					// Preparing an event for the Viewer to present the changes
					// in the tasks
					Events event = EventsMgr.getInstance().createEvent(	Events.INCOMING_TASK_UPDATE_EVENT);
					EventsMgr.getInstance().fireEvent(event);
				}
				// END TASK

				// SEND ANSWERS
				else if (msgCAnswer != null) {
					// TODO
					Events event = EventsMgr.getInstance().createEvent(
							Events.INCOMING_ANSWER_SENT_OK_EVENT);
					EventsMgr.getInstance().fireEvent(event);					
					// ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
					// //PERFORMATIC
					// msg.setOntology(ANSWER_SUBMIT_ONTOLOGY); //ONTOLOGY
					// InputStream input = new
					// FileInputStream("/sdcard/GeM3_app_log.txt");
					// int data = input.read();
					// StringBuilder sb = new StringBuilder();
					// while (data != -1) {
					// sb.append((char)data);
					// data = input.read();
					// }
					// input.close();
					// msg.setContentObject(sb); //CONTENT
					// msg.setLanguage("final");
					//
				}
				else if (msgIAnswer != null) {
					// TODO
					// ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
					// //PERFORMATIC
					// msg.setOntology(ANSWER_SUBMIT_ONTOLOGY); //ONTOLOGY
					// InputStream input = new
					// FileInputStream("/sdcard/GeM3_app_log.txt");
					// int data = input.read();
					// StringBuilder sb = new StringBuilder();
					// while (data != -1) {
					// sb.append((char)data);
					// data = input.read();
					// }
					// input.close();
					// msg.setContentObject(sb); //CONTENT
					// msg.setLanguage("final");
					//
				}
				// END SEND ANSWERS

				// MEMORYSPACE------------------------------------------------------------------------------------------------------------------------------------
				else if (msgCFPMemSpace != null) {
					// We got a call-for-proposal to share some memory. We
					// should check that we have space to save the information,
					// and answer with the amount of space left in our memory
					// card
					myLogger.log(Logger.INFO, msgCFPMemSpace.toString());

					String state = Environment.getExternalStorageState();
					boolean mExternalStorageAvailable;
					boolean mExternalStorageWriteable;
					if (Environment.MEDIA_MOUNTED.equals(state)) {
						mExternalStorageAvailable = mExternalStorageWriteable = true;
					} else if (Environment.MEDIA_MOUNTED_READ_ONLY
							.equals(state)) {
						mExternalStorageAvailable = true;
						mExternalStorageWriteable = false;
					} else {
						mExternalStorageAvailable = mExternalStorageWriteable = false;
					}

					if (mExternalStorageAvailable && mExternalStorageWriteable) {
						String path = "/sdcard";
						StatFs statFs = new StatFs(path);
						int freespace = statFs.getAvailableBlocks();
						String size = String.valueOf(freespace);
						ACLMessage rsp = new ACLMessage(ACLMessage.PROPOSE); // performative
						rsp.setOntology(msgCFPMemSpace.getOntology()); // Ontology
						rsp.setContent(size); // Content
						rsp.addReceiver(msgCFPMemSpace.getSender()); // receiver
						myAgent.send(rsp);
						myLogger.log(Logger.FINE, rsp.toString());
					} else {
						ACLMessage rsp = new ACLMessage(ACLMessage.REFUSE); // performative
						rsp.setOntology(msgCFPMemSpace.getOntology()); // ontology
						rsp.setContent(""); // content
						rsp.addReceiver(msgCFPMemSpace.getSender()); // receiver
						myAgent.send(rsp);
						myLogger.log(Logger.FINE, rsp.toString());
					}

				} else if (msgPMemSpace != null) {
					myLogger.log(Logger.INFO, msgPMemSpace.toString());
					if (memspaceStep == 0) {
						myLogger.log(Logger.FINE, "case 0 "
								+ memspaceRepliesCnt);
						int space = Integer.parseInt(msgPMemSpace.getContent());
						if (bestMemSpaceProvider == null || bestSpace < space) {
							bestSpace = space;
							bestMemSpaceProvider = msgPMemSpace.getSender();
						}
						myLogger.log(Logger.FINE, "bestSpace:" + bestSpace	+ ",bestProvider:" + bestMemSpaceProvider);

						memspaceRepliesCnt++;
						if (memspaceRepliesCnt >= memspaceProviderAgents)
							memspaceStep = 1;
					}
					if (memspaceStep == 1) {
						myLogger.log(Logger.FINE, "case 1 "	+ memspaceRepliesCnt);
						ACLMessage rsp = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL); // Performative
						rsp.setOntology(msgPMemSpace.getOntology()); // ontology
						rsp.setContent("blablabla"); // content
						rsp.addReceiver(bestMemSpaceProvider); // receiver
						myAgent.send(rsp);
						myLogger.log(Logger.FINE, rsp.toString());
						memspaceStep = 0;
					}

				} else if (msgAPMemSpace != null) {
					// The client accepted our proposal, and the content should
					// be a Serializable Object that we will put... somewhere in
					// the memory card. How? We will decide, our client doesn't
					// care
					// Object content = msgAPMemSpace.getContentObject();
					String content = msgAPMemSpace.getContent();

					// Checking the possibility to write in the SDCard
					String state = Environment.getExternalStorageState();
					boolean mExternalStorageAvailable;
					boolean mExternalStorageWriteable;
					if (Environment.MEDIA_MOUNTED.equals(state)) {
						mExternalStorageAvailable = mExternalStorageWriteable = true;
					} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
						mExternalStorageAvailable = true;
						mExternalStorageWriteable = false;
					} else {
						mExternalStorageAvailable = mExternalStorageWriteable = false;
					}

					if (mExternalStorageAvailable && mExternalStorageWriteable) {
						// File root =
						// Environment.getExternalStorageDirectory();
						File root = new File("/sdcard/download/"+ msgAPMemSpace.getSender().getLocalName());
						if (!root.isDirectory())
							root.mkdirs();
						String saveFileName = System.currentTimeMillis()+ ".bkp";
						FileOutputStream fos = new FileOutputStream(root + "/"	+ saveFileName);
						ObjectOutputStream out = new ObjectOutputStream(fos);
						myLogger.log(Logger.FINE, content);
						out.writeObject(content);
						out.close();
						myLogger.log(Logger.FINE, "Info has been saved");
						ACLMessage rsp = new ACLMessage(ACLMessage.INFORM); // Performative
						rsp.setOntology(msgAPMemSpace.getOntology()); // Ontology
						rsp.setContent("Done"); // Content
						rsp.addReceiver(msgAPMemSpace.getSender()); // receiver
						myAgent.send(rsp);
					} else {
						myLogger.log(Logger.WARNING, "not possible to write the object in memory");
					}

				} else if (msgIMemSpace != null) {
					// We got a confirmation that the Object was saved. Great,
					// we can stop worrying about saving that piece of
					// information then.
					myLogger.log(Logger.INFO, msgIMemSpace.toString());
					// TODO

				}
				// END
				// MEMORYSPACE------------------------------------------------------------------------------------------------------------------------------------

				// MEMORY
				// RETRIEVAL------------------------------------------------------------------------------------------------------------------------------------
				else if (msgCFPMemRetrieval != null) {
					// We got a call-for-proposal to provide a piece of data. We
					// should check that we have this information in our memory
					// and reply with a Proposal with its timestamp
					myLogger.log(Logger.INFO, msgCFPMemRetrieval.toString());

					String state = Environment.getExternalStorageState();
					boolean mExternalStorageAvailable;
					if (Environment.MEDIA_MOUNTED.equals(state)) {
						mExternalStorageAvailable = true;
					} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
						mExternalStorageAvailable = true;
					} else {
						mExternalStorageAvailable = false;
					}

					if (mExternalStorageAvailable) {
						// Check if we have a file
						File root = new File(Environment.getExternalStorageDirectory()+ "/download/"+ msgCFPMemRetrieval.getSender().getLocalName());
						myLogger.log(Logger.FINE, root.toString());
						boolean exists = false;
						String content = "";
						if (root.isDirectory()) {
							String fileList[] = root.list(null);
							exists = true;
							// TODO
							// Check that actually they are sorted in
							// chronological order, being 0 the newest one
							content = fileList[0];
						}

						if (exists) {
							ACLMessage rsp = new ACLMessage(ACLMessage.PROPOSE); // Performative
							rsp.setOntology(msgCFPMemRetrieval.getOntology()); // Ontology
							rsp.setContent(content); // Content
							rsp.addReceiver(msgCFPMemRetrieval.getSender()); // receiver
							myAgent.send(rsp);
							myLogger.log(Logger.FINE, rsp.toString());
						} else {
							ACLMessage rsp = new ACLMessage(ACLMessage.REFUSE); // Performative
							rsp.setOntology(msgCFPMemRetrieval.getOntology()); // Ontology
							rsp.setContent(""); // Content
							rsp.addReceiver(msgCFPMemRetrieval.getSender()); // receiver
							myAgent.send(rsp);
							myLogger.log(Logger.FINE, rsp.toString());
							myLogger.log(Logger.FINE, "No files for this user");
						}
					} else {
						ACLMessage rsp = new ACLMessage(ACLMessage.REFUSE); // Performative
						rsp.setOntology(msgCFPMemRetrieval.getOntology()); // Ontology
						rsp.setContent(""); // Content
						rsp.addReceiver(msgCFPMemRetrieval.getSender()); // receiver
						myAgent.send(rsp);
						myLogger.log(Logger.FINE, rsp.toString());
						myLogger.log(Logger.FINE,	"Not possible to access the SDCard");
					}
				} else if (msgPMemRetrieval != null) {
					// We got a proposal... lets check which one is the best
					myLogger.log(Logger.INFO, msgPMemRetrieval.toString());
					if (copyStep == 0) {
						myLogger.log(Logger.FINE, "case 0 " + copyRepliesCnt);
						String copyDate = msgPMemRetrieval.getContent();
						if (bestCopyProvider == null	|| bestCopy.compareToIgnoreCase(copyDate) < 0) {
							bestCopy = copyDate;
							bestCopyProvider = msgPMemRetrieval.getSender();
						}
						myLogger.log(Logger.FINE, "bestSpace:" + bestSpace	+ ",bestMemSpaceProvider:" + bestCopyProvider);

						copyRepliesCnt++;
						if (copyRepliesCnt >= copyProviderAgents)
							copyStep = 1;
					}
					if (copyStep == 1) {
						myLogger.log(Logger.FINE, "case 1 " + copyRepliesCnt);
						ACLMessage rsp = new ACLMessage(
								ACLMessage.ACCEPT_PROPOSAL); // Performative
						rsp.setOntology(msgPMemRetrieval.getOntology()); // Ontology
						rsp.setContent(bestCopy); // Content
						rsp.addReceiver(bestCopyProvider); // receiver
						myAgent.send(rsp);
						myLogger.log(Logger.FINE, rsp.toString());
						copyStep = 0;
					}

				} else if (msgAPMemRetrieval != null) {
					// The client has accepted our proposal, so we have to send
					// him the content of the demanded file
					myLogger.log(Logger.INFO, msgAPMemRetrieval.toString());

					String state = Environment.getExternalStorageState();
					boolean mExternalStorageAvailable;
					if (Environment.MEDIA_MOUNTED.equals(state)) {
						mExternalStorageAvailable = true;
					} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
						mExternalStorageAvailable = true;
					} else {
						mExternalStorageAvailable = false;
					}

					if (mExternalStorageAvailable) {
						// Check if we have a file
						File file = new File(Environment.getExternalStorageDirectory()+ "/download/"+ msgAPMemRetrieval.getSender().getLocalName() + "/"	+ msgAPMemRetrieval.getContent());
						myLogger.log(Logger.FINE, file.toString());

						if (file.isFile()) {
							// read the content of file
							FileInputStream fis = null;
							BufferedInputStream bis = null;
							DataInputStream dis = null;

							try {
								fis = new FileInputStream(file);
								bis = new BufferedInputStream(fis);
								dis = new DataInputStream(bis);
								String content = "";
								while (dis.available() != 0) {
									myLogger.log(Logger.FINE, dis.readLine());
									content.concat(dis.readLine());
									myLogger.log(Logger.FINE, "not crashed yet");
								}
								ACLMessage rsp = new ACLMessage(
										ACLMessage.INFORM); // Performative
								rsp.setOntology(msgAPMemRetrieval.getOntology()); // Ontology
								rsp.setContent(content); // content
								rsp.addReceiver(msgAPMemRetrieval.getSender()); // receiver
								myAgent.send(rsp);
								myLogger.log(Logger.FINE, rsp.toString());
							} catch (FileNotFoundException e) {
								ACLMessage rsp = new ACLMessage(
										ACLMessage.REFUSE); // Performative
								rsp.setOntology(msgAPMemRetrieval.getOntology()); // Ontology
								rsp.setContent(""); // Content
								rsp.addReceiver(msgAPMemRetrieval.getSender()); // receiver
								myAgent.send(rsp);
								myLogger.log(Logger.FINE, rsp.toString());
								myLogger.log(Logger.FINE,	"No such file for this user");
							} catch (IOException e) {
								ACLMessage rsp = new ACLMessage(
										ACLMessage.REFUSE); // Performative
								rsp.setOntology(msgAPMemRetrieval.getOntology()); // Ontology
								rsp.setContent(""); // COntent
								rsp.addReceiver(msgAPMemRetrieval.getSender()); // receiver
								myAgent.send(rsp);
								myLogger.log(Logger.FINE, rsp.toString());
								myLogger.log(Logger.FINE, "No such file for this user");
							}
						} else {
							ACLMessage rsp = new ACLMessage(ACLMessage.REFUSE); // Performative
							rsp.setOntology(msgAPMemRetrieval.getOntology()); // Ontology
							rsp.setContent(""); // Content
							rsp.addReceiver(msgAPMemRetrieval.getSender()); // receiver
							myAgent.send(rsp);
							myLogger.log(Logger.FINE, rsp.toString());
							myLogger.log(Logger.FINE,	"No such file for this user");
						}
					} else {
						ACLMessage rsp = new ACLMessage(ACLMessage.REFUSE); // Performative
						rsp.setOntology(msgAPMemRetrieval.getOntology()); // Ontology
						rsp.setContent(""); // Content
						rsp.addReceiver(msgAPMemRetrieval.getSender()); // receiver
						myAgent.send(rsp);
						myLogger.log(Logger.FINE, rsp.toString());
						myLogger.log(Logger.FINE, "Not possible to access the SDCard");
					}

				} else if (msgIMemRetrieval != null) {
					// the information has been retrieved from the source. We
					// should put it back into our memory
					String content = msgIMemRetrieval.getContent();

				}
				// MEMORY
				// RETRIEVAL------------------------------------------------------------------------------------------------------------------------------------

				// MEMORY RETRIEVAL
				// SHORT------------------------------------------------------------------------------------------------------------------------------------
				else if (msgRMemRetrieval != null) {
					myLogger.log(Logger.INFO, msgRMemRetrieval.toString());

					String state = Environment.getExternalStorageState();
					boolean mExternalStorageAvailable;
					if (Environment.MEDIA_MOUNTED.equals(state)) {
						mExternalStorageAvailable = true;
						myLogger.log(Logger.FINE,	"The memory card is there and writable");
					} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
						mExternalStorageAvailable = true;
						myLogger.log(Logger.FINE,	"The memory card is there and readable");
					} else {
						mExternalStorageAvailable = false;
						myLogger.log(Logger.FINE,	"The memory card is not there");
					}

					if (mExternalStorageAvailable) {
						// Check if we have a folder for this client
						File root = new File(Environment.getExternalStorageDirectory()+ "/download/"	+ msgRMemRetrieval.getSender().getLocalName());
						// in case we have the folder, check which is the latest
						// file, get its content and deliver it
						myLogger.log(Logger.FINE, "path to find the file: "	+ root.toString());
						boolean exists = false;
						String filename;

						if (root.isDirectory()) {
							myLogger.log(Logger.FINE, "This path exist");
							String fileList[] = root.list(null);
							// TODO
							// check now that the there is actually a file
							exists = true;
							// TODO
							// Check that actually they are sorted in
							// chronological order, being 0 the newest one
							filename = fileList[0];
							File file = new File(root + "/" + filename);
							myLogger.log(Logger.FINE,
									"we will offer this file " + filename);
							// we need to read the file content now and put it
							// in the content
							FileInputStream fileIS = new FileInputStream(file);
							BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS));
							String readString = new String();
							String content = new String();

							while ((readString = buf.readLine()) != null) {
								myLogger.log(Logger.FINE, "Line from file "	+ readString);
								content.concat(readString);
							}

							ACLMessage rsp = new ACLMessage(ACLMessage.CONFIRM); // Perspective
							rsp.setOntology(msgRMemRetrieval.getOntology()); // Ontology
							rsp.setContent(content); // Content
							rsp.addReceiver(msgRMemRetrieval.getSender()); // receiver
							myAgent.send(rsp);
							myLogger.log(Logger.FINE, rsp.toString());

						} else {
							ACLMessage rsp = new ACLMessage(ACLMessage.REFUSE); // Perspective
							rsp.setOntology(msgRMemRetrieval.getOntology()); // Ontology
							rsp.setContent(""); // Content
							rsp.addReceiver(msgRMemRetrieval.getSender()); // receiver
							myAgent.send(rsp);
							myLogger.log(Logger.FINE, rsp.toString());
							myLogger.log(Logger.FINE,	"No such file for this user");
						}
					} else {
						ACLMessage rsp = new ACLMessage(ACLMessage.REFUSE); // Performative
						rsp.setOntology(msgRMemRetrieval.getOntology()); // Ontology
						rsp.setContent(""); // Content
						rsp.addReceiver(msgRMemRetrieval.getSender()); // receiver
						myAgent.send(rsp);
						myLogger.log(Logger.FINE, rsp.toString());
						myLogger.log(Logger.FINE,	"Not possible to access the SDCard");
					}

				} else if (msgCMemRetrieval != null) {
					// We got a proposal... lets check which one is the best
					myLogger.log(Logger.INFO, msgCMemRetrieval.toString());
					if (fastCopyStep == 0) {
						myLogger.log(Logger.FINE, "case 0 "	+ fastCopyRepliesCnt);

						// TODO
						// this should be changed to retrieve an object and get
						// its date
						String copyDate = msgCMemRetrieval.getContent();
						if (bestFastCopyProvider == null || bestFastCopyDate.compareToIgnoreCase(copyDate) < 0) {
							bestFastCopyDate = copyDate;
							// bestFastCopy = content;
							bestFastCopyProvider = msgCMemRetrieval.getSender();
						}
						myLogger.log(Logger.FINE, "bestSpace:" + bestSpace	+ ",bestMemSpaceProvider:" + bestCopyProvider);

						fastCopyRepliesCnt++;
						if (fastCopyRepliesCnt >= fastCopyProviderAgents)
							fastCopyStep = 1;
					}
					if (fastCopyStep == 1) {
						// put the content in the memory for the main
						// application
						myLogger.log(Logger.FINE, "case 1 "	+ fastCopyRepliesCnt);
						fastCopyStep = 0;
					}
				}
				// MEMORY RETRIEVAL
				// SHORT-----------------------------------------------------------------------------------------------------------

				// KEEP WAITING UNTIL A NEW MESSAGE GETS INTO THE
				// PILE------------------------------------------------------------------------------------------------------------
				else {
					myLogger.log(Logger.WARNING,	"Got a message that I was not expecting. It doesnt fit with my filters");
					block();
				}

			} catch (Throwable t) {
				myLogger.log(Logger.SEVERE,	"***  Uncaught Exception for agent " + myAgent.getLocalName() + "  ***", t);
			}
		}
	}
}
