package se.celekt.gem_app.util;

import android.os.Environment;
import android.text.format.Time;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jade.util.Logger;
import se.celekt.gem_app.activities.ActivityKnowledge;
import se.celekt.gem_app.kml.Placemark;

/**
 * Created by alisa on 6/8/13.
 */
public class LogSaver {

	public static int JADE = 1;
    public static int GPS = 2;
    public static int BEHAVIOURS = 3;
    public static int REGISTER_EVENT = 4;
    public static int DISTANCE_REQUEST = 5;
    public static int GROUP_STATUS = 6;
    public static int ATTEMPTS = 8;
    public static int FILE_CREATION = 9;

    /**/
    public static int PhoneAvailable = 10;
    public static int PhoneUnavailable = 11;

    public static int PresentationGroupUpdate = 20;
    /**/

    /* GPS Self-adaptation related codes 6xx */
    public static int GPSProbe = 610;
    public static int GPSProbe2 = 611;
    public static int GPSMonitor = 620;
    public static int GPSAnalyze = 630;
    public static int GPSAnalyzeFail = 631;
    public static int GPSPlan = 640;
    public static int GPSPlanDone = 641;
    public static int GPSExecute = 650;
    public static int GPSEffectorON = 661;
    public static int GPSEffectorOFF = 662;

    /* MVD Self-healing related codes 7xx */
    public static int MVDProbe = 710;
    public static int MVDMonitor = 720;
    public static int MVDMonitorMaster = 721;
    public static int MVDMonitorSlave = 722;
    public static int MVDAnalyze = 730;
    public static int MVDPlan = 740;
    public static int MVDPlanInit = 741;
    public static int MVDPlanStartRemove = 742;
    public static int MVDPlanFinishedRemove = 743;
    public static int MVDPlanStartModify = 744;
    public static int MVDPlanFinishedModify = 745;
    public static int MVDPlanSearches = 746;
    public static int MVDPlanProcess = 747;
    public static int MVDExecute = 750;
    public static int MVDExecuteRemove = 751;
    public static int MVDExecuteModify = 752;
    public static int MVDEffector = 760;



    private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());
    private static LogSaver logger = new LogSaver();


    private String attempts ="attempts";
    private String found ="found";
    private String general_path;
    private String file_path = Environment.getExternalStorageDirectory().getPath()+"/GEM/Logs/";


    public static LogSaver getInstance() {
        return logger;
    }

    public void createLogs() {
        //create general log file
        File folder = new File(Environment.getExternalStorageDirectory() + "/GEM/Logs");
        if(!folder.exists()){
            folder.mkdir();
        }
        general_path = folder.getAbsolutePath()+ "/general.txt";
        createKML(attempts);
        createKML(found);

    }

    private void createKML(String name){
        StringBuilder beginFile = new StringBuilder();
        beginFile.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        beginFile.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n");
        beginFile.append("\t<Document>\n");


        File folder = new File(Environment.getExternalStorageDirectory() + "/GEM/Logs");
        if(!folder.exists()){
            folder.mkdir();
        }
        String filename = folder.getAbsolutePath()+ "/" +  name + ".kml";
        try {
            FileWriter file = new FileWriter(filename);
            file.write(beginFile.toString());
            file.close();
            myLogger.log(Logger.INFO," KML file " + filename + " was created");
        } catch (IOException e) {
            myLogger.log(Logger.WARNING," KML file " + filename + " was not created " + e.getMessage());
        }
    }




    public void writeLog(int code,MessageType messageType, String msg){

        StringBuilder log = new StringBuilder();
        log.append(checkTime());
        log.append(";");
        log.append(code);
        log.append(";");
        log.append(messageType.name());
        log.append(";");
        log.append(msg);
        log.append(";");
        log.append(ActivityKnowledge.getInstance().getName());
        log.append(System.getProperty("line.separator"));

        try {
            FileWriter f = new FileWriter(general_path,true);
            f.append(log);
            f.close();
        } catch (IOException e) {
            myLogger.log(Logger.SEVERE,"Cannot save data to file " + general_path + e.getMessage());
        }

    }


    public void savePlacemark(Placemark place, String name, LogType logType){
        StringBuilder placemark = new StringBuilder();
        placemark.append("\n\t\t<Placemark>\n");
        placemark.append("\t\t\t<name>");
        placemark.append(name);
        placemark.append("</name>/n");
        placemark.append("\t\t\t<Icon> </Icon>\n");
        placemark.append("\t\t\t<Point>\n");
        placemark.append("\t\t\t\t<coordinates>");
        placemark.append(place.getCoordinates());
        placemark.append("</coordinates>\n");
        placemark.append("\t\t\t</Point>\n");
        placemark.append("\t\t</Placemark>\n");
        String filename = null;
        switch (logType){
            case ATTEMPT_LOG: filename = attempts; break;
            case FOUND_LOG: filename = found; break;
		default:
			filename = general_path; 
			break;
        }
        filename = file_path + "/" + filename + ".kml";
        //write in the end of file
        FileWriter file;
        try {
            file = new FileWriter(filename, true);
            file.append(placemark);
            file.close();

        } catch (IOException e) {
            myLogger.log(Logger.WARNING,"Point Attempt or Found not saved in kml file " + e.getMessage());
            e.printStackTrace();
        }

    }

    public String checkTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        return sdf.format(new Date());
    }
    public enum LogType{
        ATTEMPT_LOG,FOUND_LOG,GENERAL_LOG
    }

    public enum MessageType{
        ERROR,WARN,INFO,DEBUG
    }
}
