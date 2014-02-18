package se.celekt.gem_app.kml;

/**
 * Created by alisa on 6/5/13.
 */


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root(name="Placemark", strict=false)
public class Placemark {
    @Element
    private String name;
    @Element(required=false)
    private String description;
    @Element(name="styleUrl", required=false)
    private String styleUrl;

    @Element
    @Path("Point")
    private String coordinates;

    @Element(required=false)
    private Polygon Polygon;


    private double lat;
    private double lon;





    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStyleUrl() {
        return styleUrl;
    }

    public void setStyleUrl(String styleUrl) {
        this.styleUrl = styleUrl;
    }

    public String getCoordinates() {

        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;

    }

    public Polygon getPolygon() {
        return Polygon;
    }

    public void setPolygon(Polygon polygon) {
        Polygon = polygon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        if(coordinates != null){
            String[] c = coordinates.split(",");
                if(c != null && c.length>1){
                setLat(Double.parseDouble(c[1]));

                }
        }
        return lat;
    }

    public double getLon() {
        if(coordinates != null){
        String[] c = coordinates.split(",");
            if(c != null && c.length>1){
                setLon(Double.parseDouble(c[0]));
            }
        }

        return lon;
    }



}
