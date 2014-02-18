package se.celekt.gem_app.kml;

import org.simpleframework.xml.Attribute;

/**
 * Created by alisa on 6/7/13.
 */
public abstract class Object {
    /** The id. */
    @Attribute(required=false)
    private String id;

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(String id) {
        this.id = id;
    }
}
