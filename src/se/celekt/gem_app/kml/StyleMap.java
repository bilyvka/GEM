package se.celekt.gem_app.kml;



import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * Created by alisa on 6/7/13.
 */
public class StyleMap extends StyleSelector {
    /** The pair list. */
    @ElementList(entry="Pair", inline=false, type=Pair.class, required=false)
    private List<Pair> pairList;

    /**
     * Gets the pair.
     *
     * @return the pair
     */
    public List<Pair> getPairList() {
        return this.pairList;
    }

    /**
     * Sets the pair.
     *
     * @param pairList the new pair
     */
    public void setPairList(List<Pair> pairList) {
        this.pairList = pairList;
    }
}
