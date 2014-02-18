package se.celekt.gem_app.kml;

import com.google.android.gms.maps.model.LatLng;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alisa on 6/5/13.
 */
@Root(name="Polygon", strict=false)
public class Polygon {

    @Element
    @Path("outerBoundaryIs/LinearRing")
    private String coordinates;

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public List<LatLng> cord(){
        List<LatLng> result = new ArrayList<LatLng>();
        String[] points = coordinates.split("\\r?\\n");
        for(String point:points){
            String[] latlong = point.split(",");
            if(latlong.length > 1){
            result.add(new LatLng(Double.parseDouble(latlong[1]),Double.parseDouble(latlong[0])));
            }
        }
    return result;
    }
}
