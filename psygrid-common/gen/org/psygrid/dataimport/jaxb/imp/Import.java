//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.13 at 09:02:50 AM GMT 
//


package org.psygrid.dataimport.jaxb.imp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="projectDirectory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sources" type="{}sourcestype"/>
 *         &lt;element name="group" type="{}grouptype" minOccurs="0"/>
 *         &lt;element name="consentformgroups" type="{}consentformgroupstype"/>
 *         &lt;element name="schedulestartdate" type="{}schedulestartdatetype" minOccurs="0"/>
 *         &lt;element name="skiprows" type="{}skiprowstype" minOccurs="0"/>
 *         &lt;element name="users" type="{}userstype" minOccurs="0"/>
 *         &lt;element name="documents" type="{}documentstype"/>
 *         &lt;element name="translations" type="{}translationstype" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="project" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "projectDirectory",
    "sources",
    "group",
    "consentformgroups",
    "schedulestartdate",
    "skiprows",
    "users",
    "documents",
    "translations"
})
@XmlRootElement(name = "import")
public class Import {

    protected String projectDirectory;
    @XmlElement(required = true)
    protected Sourcestype sources;
    protected Grouptype group;
    @XmlElement(required = true)
    protected Consentformgroupstype consentformgroups;
    protected Schedulestartdatetype schedulestartdate;
    protected Skiprowstype skiprows;
    protected Userstype users;
    @XmlElement(required = true)
    protected Documentstype documents;
    protected Translationstype translations;
    @XmlAttribute(required = true)
    protected String project;

    /**
     * Gets the value of the projectDirectory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProjectDirectory() {
        return projectDirectory;
    }

    /**
     * Sets the value of the projectDirectory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProjectDirectory(String value) {
        this.projectDirectory = value;
    }

    /**
     * Gets the value of the sources property.
     * 
     * @return
     *     possible object is
     *     {@link Sourcestype }
     *     
     */
    public Sourcestype getSources() {
        return sources;
    }

    /**
     * Sets the value of the sources property.
     * 
     * @param value
     *     allowed object is
     *     {@link Sourcestype }
     *     
     */
    public void setSources(Sourcestype value) {
        this.sources = value;
    }

    /**
     * Gets the value of the group property.
     * 
     * @return
     *     possible object is
     *     {@link Grouptype }
     *     
     */
    public Grouptype getGroup() {
        return group;
    }

    /**
     * Sets the value of the group property.
     * 
     * @param value
     *     allowed object is
     *     {@link Grouptype }
     *     
     */
    public void setGroup(Grouptype value) {
        this.group = value;
    }

    /**
     * Gets the value of the consentformgroups property.
     * 
     * @return
     *     possible object is
     *     {@link Consentformgroupstype }
     *     
     */
    public Consentformgroupstype getConsentformgroups() {
        return consentformgroups;
    }

    /**
     * Sets the value of the consentformgroups property.
     * 
     * @param value
     *     allowed object is
     *     {@link Consentformgroupstype }
     *     
     */
    public void setConsentformgroups(Consentformgroupstype value) {
        this.consentformgroups = value;
    }

    /**
     * Gets the value of the schedulestartdate property.
     * 
     * @return
     *     possible object is
     *     {@link Schedulestartdatetype }
     *     
     */
    public Schedulestartdatetype getSchedulestartdate() {
        return schedulestartdate;
    }

    /**
     * Sets the value of the schedulestartdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Schedulestartdatetype }
     *     
     */
    public void setSchedulestartdate(Schedulestartdatetype value) {
        this.schedulestartdate = value;
    }

    /**
     * Gets the value of the skiprows property.
     * 
     * @return
     *     possible object is
     *     {@link Skiprowstype }
     *     
     */
    public Skiprowstype getSkiprows() {
        return skiprows;
    }

    /**
     * Sets the value of the skiprows property.
     * 
     * @param value
     *     allowed object is
     *     {@link Skiprowstype }
     *     
     */
    public void setSkiprows(Skiprowstype value) {
        this.skiprows = value;
    }

    /**
     * Gets the value of the users property.
     * 
     * @return
     *     possible object is
     *     {@link Userstype }
     *     
     */
    public Userstype getUsers() {
        return users;
    }

    /**
     * Sets the value of the users property.
     * 
     * @param value
     *     allowed object is
     *     {@link Userstype }
     *     
     */
    public void setUsers(Userstype value) {
        this.users = value;
    }

    /**
     * Gets the value of the documents property.
     * 
     * @return
     *     possible object is
     *     {@link Documentstype }
     *     
     */
    public Documentstype getDocuments() {
        return documents;
    }

    /**
     * Sets the value of the documents property.
     * 
     * @param value
     *     allowed object is
     *     {@link Documentstype }
     *     
     */
    public void setDocuments(Documentstype value) {
        this.documents = value;
    }

    /**
     * Gets the value of the translations property.
     * 
     * @return
     *     possible object is
     *     {@link Translationstype }
     *     
     */
    public Translationstype getTranslations() {
        return translations;
    }

    /**
     * Sets the value of the translations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Translationstype }
     *     
     */
    public void setTranslations(Translationstype value) {
        this.translations = value;
    }

    /**
     * Gets the value of the project property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProject() {
        return project;
    }

    /**
     * Sets the value of the project property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProject(String value) {
        this.project = value;
    }

}
