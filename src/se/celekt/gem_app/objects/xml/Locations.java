package se.celekt.gem_app.objects.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by alisa on 6/17/13.
 */
@Root(name="Locations")
public class Locations {

    @Element(required=false)

    private double lat;

    @Element(required=false)

    private double lon;

    @Element(required=false)

    private float accuracy;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }
}
