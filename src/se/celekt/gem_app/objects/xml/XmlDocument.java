package se.celekt.gem_app.objects.xml;

import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * Created by alisa on 6/17/13.
 */
public class XmlDocument {
    @ElementList(name="Locations",inline = true)
    private List<Locations> mLocationses;

    public List<Locations> getLocationses() {
        return mLocationses;
    }

    public void setLocationses(List<Locations> locationses) {
        mLocationses = locationses;
    }
}
