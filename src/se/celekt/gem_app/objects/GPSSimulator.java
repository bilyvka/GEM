package se.celekt.gem_app.objects;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import jade.util.Logger;
import se.celekt.gem_app.objects.xml.Locations;
import se.celekt.gem_app.objects.xml.XmlRoot;

/**
 * Created by alisa on 6/17/13.
 */
public class GPSSimulator implements GPSListener {
    private static final Logger myLogger = Logger.getMyLogger(GPSSimulator.class.getName());
    private ContactLocation mContactLocation;
    private static GPSSimulator mGPSSimulator = new GPSSimulator();
    private List<ContactLocation> mContactLocations = new LinkedList<ContactLocation>();

    private List<Locations> mLocationses;

    private String path = "/sdcard/GEM/gps.xml";
    private int counter = 0;

    public GPSSimulator() {
        parse();
    }
    public static GPSSimulator getInstance(){
        return mGPSSimulator;
    }

    public ContactLocation getMyContactLocation() {
        if(counter < mContactLocations.size() && mContactLocations.size()>1){
            mContactLocation = mContactLocations.get(counter);
            counter++;
        }
        else {
            counter = 0;
            mContactLocation = mContactLocations.get(counter);
        }
        return mContactLocation;
    }

    public void parse() {
         try {
            InputStream input = new FileInputStream(path);
            Serializer serializer = new Persister();
            XmlRoot xml = serializer.read(XmlRoot.class, input);
            mLocationses = xml.getDocument().getLocationses();
            for(Locations locations:mLocationses){
                mContactLocations.add(new ContactLocation("fake-gps",locations.getLat(),locations.getLon(),locations.getAccuracy()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            myLogger.log(Logger.SEVERE,"File gps.xml dosn't exist in path sdcard/GEM/gps.xml" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            myLogger.log(Logger.SEVERE,"ERROR parse kml file" + e.getMessage());
        }
    }
}
