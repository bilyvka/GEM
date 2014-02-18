package se.celekt.gem_app.jade;

import android.app.Application;
import android.content.Context;

import jade.android.ConnectionListener;
import jade.android.JadeGateway;
import jade.core.AID;
import jade.core.Profile;
import jade.imtp.leap.JICP.JICPProtocol;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import jade.util.leap.Properties;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import se.celekt.gem_app.jade.agent.PhoneAgent;
import se.celekt.gem_app.jade.behaviours.BehaviourLauncher;
import se.celekt.gem_app.util.LogSaver;
import se.celekt.gem_app.util.LogSaver.MessageType;


import java.net.ConnectException;


/**
 * Created by alisa on 5/31/13.
 */
public class Configuration implements ConnectionListener {

    private static JadeGateway gateway;

    private static JChatApplication app;

    private static Context context;

    private static final Logger myLogger = Logger.getMyLogger(Configuration.class.getName());

    /**
     * Custom dialog containing Jade connection parameters entered by the user.
     */



    public Configuration(Application application, Context context) {
        this.app = (JChatApplication)application;
        this.context = context;
    }

    public void connect(){
        Properties jadeProperties = getJadeProperties();
        // DIDAC_C: creates a connection with the JadeGateway for
        // communications with the JADE platform
        try {
            JadeGateway.connect(PhoneAgent.class.getName(), new String[]{"5000"}, jadeProperties, context, this);
        } catch (Exception e) {
           myLogger.log(Logger.SEVERE, "Error in connecting to JADE", e);
            e.printStackTrace();
            LogSaver.getInstance().writeLog(LogSaver.JADE, MessageType.ERROR, "Error in connecting to JADE" + e.getMessage());

        }

    }
    /**
     * Retrieve the jade properties, needed to connect to the JADE main
     * container.
     * <p>
     * These properties are:
     * <ul>
     * <li> <code>MAIN_HOST</code>: hostname or IP address of the machine on
     * which the main container is running. Taken from resource file or settings
     * dialog.
     * <li> <code>MAIN_PORT</code>: port used by the JADE main container. Taken
     * from resource file or settings dialog.
     * <li> <code>MSISDN_KEY</code>: name of the JADE agent (the phone number or phoneID or studentID)
     * </ul>
     *
     * @return the jade properties
     */
    private Properties getJadeProperties() {
        // fill Jade connection properties
        Properties jadeProperties = new Properties();
        jadeProperties.setProperty(Profile.MAIN_HOST,		app.getProperty(JChatApplication.JADE_DEFAULT_HOST));
        jadeProperties.setProperty(Profile.MAIN_PORT,		app.getProperty(JChatApplication.JADE_DEFAULT_PORT));
        jadeProperties.setProperty(JICPProtocol.MSISDN_KEY,	app.getProperty(JChatApplication.PREFERENCE_PHONE_NUMBER));
        return jadeProperties;
    }

    public static void shoutDownJade(){
        if (gateway != null) {
            try {
                gateway.shutdownJADE();
            } catch (ConnectException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            gateway.disconnect(context);
        	LogSaver.getInstance().writeLog(LogSaver.JADE, MessageType.INFO, "Shout down jade!!");

            myLogger.log(Logger.INFO,"Shout down jade!!");
        }
    }
    /**
     * Callback methods called when connection to Android add-on's
     * MicroRuntimeService is completed Provides an instance of the
     * {@link JadeGateway} that is stored and sends the updater to the agent
     * using JadeGateway.execute() making it able to update the GUI.
     *
     *
     *            instance of the JadeGateway returned after the call to
     *            <code>JadeGateway.connect()</code>
     */
    public void onConnected(JadeGateway gw) {
        this.gateway = gw;
        if(gateway != null){

        	LogSaver.getInstance().writeLog(LogSaver.JADE, MessageType.INFO, "Going to connect to the back end to create the container");

            try {
                myLogger.log(Logger.INFO, "Going to connect to the back end to create the container");
                this.gateway.execute("");
                //Starts the initial behaviors that the agent needs to perform
                 //loadInitialBehaviors();

            } catch (StaleProxyException e) {
                e.printStackTrace();
                myLogger.log(Logger.SEVERE, "onConnected(): FAIL!");
            } catch (ControllerException e) {
                e.printStackTrace();
                myLogger.log(Logger.SEVERE, "onConnected(): FAIL!");
            } catch (InterruptedException e) {
                e.printStackTrace();
                myLogger.log(Logger.SEVERE, "onConnected(): FAIL!");
            } catch (Exception e) {
                e.printStackTrace();
                myLogger.log(Logger.SEVERE, "onConnected(): FAIL!");
            }
        }
    }
//

    public void onDisconnected() {
        shoutDownJade();
    }
//
//    public void registerEvents(){
//
//    }
    /**
     * Loads the behaviors needed for this activity. This is called once the phone is ready to participate in the activity
     */
   public static void loadInitialBehaviors(){
     //request for the groups I should belong to
     //   myLogger.log(Logger.INFO, "going to execute the GroupRequesterBehaviour");
      
	 
	   try{
            BehaviourLauncher bl = new BehaviourLauncher("se.celekt.gem_app.jade.behaviours.groups.GroupRequesterBehaviour");
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setOntology(PhoneAgent.GROUPS_INFORM_ONTOLOGY);
            String service ="groupManager-type";
            Object[] args = new Object[] {msg, service};
            bl.setArguments(args);
            gateway.execute(bl);
            LogSaver.getInstance().writeLog(LogSaver.BEHAVIOURS, MessageType.INFO, "going launch GroupRequesterBehaviour ");
            myLogger.log(Logger.INFO, "GroupRequesterBehaviour behaviour loaded");
        }catch (Exception e){
            myLogger.log(Logger.SEVERE, "problem while trying to execute the GroupRequesterBehaviour");
            e.printStackTrace();
            LogSaver.getInstance().writeLog(LogSaver.BEHAVIOURS, MessageType.ERROR, "GroupRequesterBehaviour behaviour not loaded " + e.getMessage());
            
        }


        //TODO: move this code to another method
       // AttemptManager.getInstance().setGateway(gateway);
       // FoundManager.getInstance().setGatwey(gateway);
    }

    public static void requestPosition(AID aid){
         try {
                //Message creation
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(aid);
                msg.setContent("Give me the GPS position, man");
                msg.setOntology("gps_request_ontology");
                msg.setLanguage("XML");

                //Launching the message
                BehaviourLauncher bl = new BehaviourLauncher("se.celekt.gem_app.jade.behaviours.GeneralSenderBehaviour");
                Object[] args = new Object[] { /*ServiceType*/ null, /*ACLMessage*/ msg, /*maxRequestedServers*/1 };
                bl.setArguments(args);
                gateway.execute(bl);
                LogSaver.getInstance().writeLog(LogSaver.BEHAVIOURS, MessageType.INFO, "going to launch the GeneralSenderBehaviour ");
                LogSaver.getInstance().writeLog(LogSaver.GPS, MessageType.INFO, "request GPS position in agent AID " + aid.getName());
                
            } catch (Exception e) {
                myLogger.log(Logger.SEVERE, "Error in requesting the distance", e);
                e.printStackTrace();
                LogSaver.getInstance().writeLog(LogSaver.BEHAVIOURS, MessageType.ERROR, "GeneralSenderBehaviour behaviour not loaded " + e.getMessage());
                
            }

    }


    public static JadeGateway getGateway() {
        return gateway;
    }


}
