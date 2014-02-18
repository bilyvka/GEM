package se.celekt.gem_app.kml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by alisa on 6/5/13.
 */
@Root(name="hotSpot")
public class HotSpot {
    @Attribute
    private String x;

    @Attribute
    private String y;

    @Attribute
    private String xunits;

    @Attribute
    private String yunits;

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getXunits() {
        return xunits;
    }

    public void setXunits(String xunits) {
        this.xunits = xunits;
    }

    public String getYunits() {
        return yunits;
    }

    public void setYunits(String yunits) {
        this.yunits = yunits;
    }
}
