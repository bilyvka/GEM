package se.celekt.gem_app.kml;

/**
 * Created by alisa on 6/5/13.
 */


import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

public class KmlDocument {
    @Element
    private String name;
    @Element(required=false)
    private String description;
    @ElementList(name="Style", inline=true)
    private List<Style> styles;
    @ElementList(name="Placemark", inline=true)
    private List<Placemark> placemarks;

//    @ElementList(name="StyleMap", inline=true)
//    private List<StyleMap> mStyleMaps;

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
    public List<Style> getStyles() {
        return styles;
    }
    public void setStyles(List<Style> styles) {
        this.styles = styles;
    }
    public List<Placemark> getPlacemarks() {
        return placemarks;
    }
    public void setPlacemarks(List<Placemark> placemarks) {
        this.placemarks = placemarks;
    }

//    public List<StyleMap> getStyleMaps() {
//        return mStyleMaps;
//    }
//
//    public void setStyleMaps(List<StyleMap> styleMaps) {
//        mStyleMaps = styleMaps;
//    }
}
