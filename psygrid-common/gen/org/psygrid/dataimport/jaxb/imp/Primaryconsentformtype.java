//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.13 at 09:02:50 AM GMT 
//


package org.psygrid.dataimport.jaxb.imp;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for primaryconsentformtype complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="primaryconsentformtype">
 *   &lt;complexContent>
 *     &lt;extension base="{}consentformtype">
 *       &lt;sequence>
 *         &lt;element name="associatedconsentform" type="{}consentformtype" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "primaryconsentformtype", propOrder = {
    "associatedconsentform"
})
public class Primaryconsentformtype
    extends Consentformtype
{

    protected List<Consentformtype> associatedconsentform;

    /**
     * Gets the value of the associatedconsentform property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the associatedconsentform property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAssociatedconsentform().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Consentformtype }
     * 
     * 
     */
    public List<Consentformtype> getAssociatedconsentform() {
        if (associatedconsentform == null) {
            associatedconsentform = new ArrayList<Consentformtype>();
        }
        return this.associatedconsentform;
    }

}
