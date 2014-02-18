package se.celekt.gem_app.kml;

import org.simpleframework.xml.Element;

/**
 * Created by alisa on 6/7/13.
 */
public class Pair extends Object {
    /** The key. */
    @Element(required=false)
    private String key;

    /** The style url. */
    @Element(required=false)
    private String styleUrl;

    /** The style. */
    @Element(name="Style", required=false)
    private Style style;

    /**
     * Gets the key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key.
     *
     * @param key the new key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the style url.
     *
     * @return the style url
     */
    public String getStyleUrl() {
        return styleUrl;
    }

    /**
     * Sets the style url.
     *
     * @param styleUrl the new style url
     */
    public void setStyleUrl(String styleUrl) {
        this.styleUrl = styleUrl;
    }

    /**
     * Gets the style.
     *
     * @return the style
     */
    public Style getStyle() {
        return style;
    }

    /**
     * Sets the style.
     *
     * @param style the new style
     */
    public void setStyle(Style style) {
        this.style = style;
    }
}
