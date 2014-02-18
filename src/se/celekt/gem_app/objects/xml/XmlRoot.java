package se.celekt.gem_app.objects.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by alisa on 6/17/13.
 */
@Root(name="xml")
public class XmlRoot {
    @Element(name="Document")
    private XmlDocument document;

    public void setDocument(XmlDocument document) {
        this.document = document;
    }

    public XmlDocument getDocument() {
        return document;
    }
}
