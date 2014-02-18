package se.celekt.gem_app.kml;

/**
 * Created by alisa on 6/5/13.
 */


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root(name="Style")
public class Style {
    @Attribute
    private String id;

    @Element(required=false)
    @Path("IconStyle/Icon")
    private String href;

    @Element(required=false)
    @Path("IconStyle/scale")
    private double scale;

    @Element(required=false)
    @Path("IconStyle/hotSpot")
    private HotSpot hotSpot;

    @Element(required=false)
    @Path("IconStyle/color")
    private String color;

    @Element(required=false)
    @Path("LineStyle/color")
    private String line_color;

    @Element(required=false)
    @Path("LineStyle/width")
    private double line_width;


    @Element(required=false)
    @Path("ListStyle/ItemIcon")
    private double href_icon;

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public HotSpot getHotSpot() {
        return hotSpot;
    }

    public void setHotSpot(HotSpot hotSpot) {
        this.hotSpot = hotSpot;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getLine_color() {
        return line_color;
    }

    public void setLine_color(String line_color) {
        this.line_color = line_color;
    }

    public double getLine_width() {
        return line_width;
    }

    public void setLine_width(double line_width) {
        this.line_width = line_width;
    }
}
