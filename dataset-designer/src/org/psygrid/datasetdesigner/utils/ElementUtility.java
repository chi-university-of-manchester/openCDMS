/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as
published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.psygrid.datasetdesigner.utils;

import org.psygrid.data.model.hibernate.*;

import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.model.DSDocumentOccurrence;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.controllers.DatasetController;

import java.util.ArrayList;
import java.util.Date;


/**
 * A utility for creating dataset objects
 *
 *
 * @author pwhelan
 */
public class ElementUtility {
    /**
     * 
     */
    private static HibernateFactory factory;

    /**
     *
     *
     * @return 
     */
    public static HibernateFactory getFactory() {
        if (factory == null) {
            factory = new HibernateFactory();
        }

        return factory;
    }

    /**
     *
     *
     * @return 
     */
    public static ConsentFormGroup createIConsentFormGroup() {
        ConsentFormGroup icfg = (ConsentFormGroup) getFactory()
                                                       .createConsentFormGroup();

        return icfg;
    }

    /**
     *
     *
     * @param question 
     * @param refNumber 
     *
     * @return 
     */
    public static AssociatedConsentForm createIAssociatedConsentForm(
            String question, String refNumber) {
        AssociatedConsentForm associatedConsentForm = getFactory()
                                                           .createAssociatedConsentForm();
        associatedConsentForm.setQuestion(question);
        associatedConsentForm.setReferenceNumber(refNumber);

        return associatedConsentForm;
    }

    /**
     * Used by the wizard to create a basic document
     *
     * @param name
     * @return IDocument returns the newly created document
     */
    public static Document createIDocumentWizard(String name, StudyDataSet dsSet) {
        Document doc = getFactory().createDocument(name);
        doc.setDisplayText(name);
        
        //assign document statuses
        if (doc.getDataSet() != null) {
        	if (doc.getDataSet().isNoReviewAndApprove()) {
        		Utils.createNoReviewAndApproveStatuses(getFactory(), doc);
        	} else {
        		Utils.createReviewAndApproveStatuses(getFactory(), doc);
        	}
        //if dataset is null, assume no review and approve
        } else {
    		Utils.createNoReviewAndApproveStatuses(getFactory(), doc);
        }

        ((Document) doc).setIsEditable(true);

        //add main sections to all the docs by default
        String section = PropertiesHelper.getStringFor(
                "org.psygrid.datasetdesigner.ui.mainsection");
        doc.addSection(createISection(section, section, section, false));

        dsSet.getDs().addDocument(doc);

        return doc;
    }

    /**
     *
     *
     * @param message 
     * @param description 
     * @param absLowerLimit 
     * @param absUpperLimit 
     * @param relLowerLimit 
     * @param relUpperLimit 
     * @param relLowerLimitUnits 
     * @param relUpperLimitUnits 
     *
     * @return 
     */
    public static DateValidationRule createIDateValidationRule(
        String message, String description, Date absLowerLimit,
        Date absUpperLimit, Integer relLowerLimit, Integer relUpperLimit,
        TimeUnits relLowerLimitUnits, TimeUnits relUpperLimitUnits) {
        DateValidationRule dateRule = getFactory().createDateValidationRule();
        dateRule.setMessage(message);
        dateRule.setDescription(description);
        ((ValidationRule) dateRule).setIsEditable(true);
        ((ValidationRule) dateRule).setName(description);

        dateRule.setAbsLowerLimit(absLowerLimit);
        dateRule.setAbsUpperLimit(absUpperLimit);
        dateRule.setRelLowerLimit(relLowerLimit);
        dateRule.setRelUpperLimit(relUpperLimit);
        dateRule.setRelLowerLimitUnits(relLowerLimitUnits);
        dateRule.setRelUpperLimitUnits(relUpperLimitUnits);

        return dateRule;
    }

    public static Document createIDummyDocument(String name) {
        Document document = getFactory().createDocument(name);
        return document;
    }
    
    /**
     *
     *
     * @param name 
     * @param displayText 
     * @param description 
     * @param datasetName 
     * @param cfg 
     *
     * @return 
     */
    public static Document createIDocument(String name, String displayText,
                                           String description, String datasetName, ConsentFormGroup cfg) {
        Document document = getFactory().createDocument(name);
        
        //assign document statuses
        if (document.getDataSet() != null) {
        	if (document.getDataSet().isNoReviewAndApprove()) {
        		Utils.createNoReviewAndApproveStatuses(getFactory(), document);
        	} else {
        		Utils.createReviewAndApproveStatuses(getFactory(), document);
        	}
        //if dataset is null, assume no review and approve
        } else {
    		Utils.createNoReviewAndApproveStatuses(getFactory(), document);
        }
        
        document.setDisplayText(displayText);
        document.setDescription(description);
        ((Document) document).setIsEditable(true);

        StudyDataSet studySet = DatasetController.getInstance().getActiveDs();
        
        if (cfg != null) {
            document.addConsentFormGroup(cfg);
        } 

        studySet.getDs().addDocument(document);
        DocTreeModel.getInstance().addDocument(document);

        //when creating a new document, automatically add a main section
        String section = PropertiesHelper.getStringFor(
                "org.psygrid.datasetdesigner.ui.mainsection");
        document.addSection(createISection(section, section, section, false));

        return document;
    }

    /**
     *
     *
     * @param name 
     * @param displayText 
     * @param label 
     * @param description 
     * @param allowedRecordStatus 
     * @param prerequisiteGroups 
     * @param updateStatus 
     *
     * @return 
     */
    public static DocumentGroup createIDocumentGroup(String name,
                                                     String displayText, String label, String description,
                                                     ArrayList<Status> allowedRecordStatus,
                                                     ArrayList<DocumentGroup> prerequisiteGroups, Status updateStatus) {
        DocumentGroup docGroup = getFactory().createDocumentGroup(name);
        docGroup.setDisplayText(displayText);
        docGroup.setLabel(label);
        docGroup.setDescription(description);
        docGroup.setAllowedRecordStatus(allowedRecordStatus);

        for (DocumentGroup group : prerequisiteGroups) {
            docGroup.addPrerequisiteGroup(group);
        }

        docGroup.setUpdateStatus(updateStatus);

        return docGroup;
    }

    /**
     * Used by the wizard to create a base document group
     * Display text is defaulted to the name
     * @param name
     * @return
     */
    public static DocumentGroup createIDocumentGroupWizard(String name) {
        DocumentGroup docGroup = getFactory().createDocumentGroup(name);
        docGroup.setDisplayText(name);

        return docGroup;
    }

    /**
     *
     *
     * @param doc 
     * @param docGroup 
     * @param name 
     * @param displayText 
     * @param description 
     * @param label 
     * @param locked 
     * @param randomisationTrigger 
     *
     * @return 
     */
    public static DSDocumentOccurrence createIDocumentOccurrence(
        Document doc, DocumentGroup docGroup, String name,
        String displayText, String description, String label, boolean locked,
        boolean randomisationTrigger) {
        DocumentOccurrence docOcc = getFactory().createDocumentOccurrence(name);
        docOcc.setName(name);
        docOcc.setDescription(description);
        docOcc.setDisplayText(displayText);
        docOcc.setLocked(locked);
        docOcc.setRandomizationTrigger(randomisationTrigger);
        docOcc.setLabel(label);
        docOcc.setDocumentGroup(docGroup);

        DSDocumentOccurrence dsDocOcc = new DSDocumentOccurrence();
        dsDocOcc.setDocOccurrence(docOcc);
        dsDocOcc.setDocument(doc);

        return dsDocOcc;
    }

    /**
     *
     *
     * @param groupname 
     * @param longName 
     * @param sites 
     * @param secondaryGroups 
     *
     * @return 
     */
    public static Group createIGroup(String groupname, String longName,
                                     ArrayList<Site> sites, ArrayList<Group> secondaryGroups) {
        Group group = getFactory().createGroup(groupname);
        ((Group) group).setLongName(longName);

        if (sites != null) {
            for (Site site : sites) {
                group.addSite(site);
            }
        }

        return group;
    }

    /**
     *
     *
     * @param message 
     * @param description 
     * @param lowerLimit 
     * @param upperLimit 
     *
     * @return 
     */
    public static IntegerValidationRule createIIntegerValidationRule(
        String message, String description, Integer lowerLimit,
        Integer upperLimit) {
        IntegerValidationRule integerRule = getFactory()
                                                 .createIntegerValidationRule();
        integerRule.setMessage(message);
        integerRule.setDescription(description);
        ((ValidationRule) integerRule).setIsEditable(true);
        ((ValidationRule) integerRule).setName(description);
        integerRule.setLowerLimit(lowerLimit);
        integerRule.setUpperLimit(upperLimit);

        return integerRule;
    }

    /**
     *
     *
     * @param message 
     * @param description 
     * @param lowerLimit 
     * @param upperLimit 
     *
     * @return 
     */
    public static NumericValidationRule createINumericValidationRule(
        String message, String description, Double lowerLimit, Double upperLimit) {
        NumericValidationRule numericRule = getFactory()
                                                 .createNumericValidationRule();
        numericRule.setMessage(message);
        numericRule.setDescription(description);
        ((ValidationRule) numericRule).setIsEditable(true);
        ((ValidationRule) numericRule).setName(description);

        numericRule.setLowerLimit(lowerLimit);
        numericRule.setUpperLimit(upperLimit);

        return numericRule;
    }

    /**
     *
     *
     * @param question 
     * @param refNumber 
     * @param associatedForms 
     *
     * @return 
     */
    public static PrimaryConsentForm createIPrimaryConsent(String question,
        String refNumber) {
        PrimaryConsentForm primaryConsentForm = (PrimaryConsentForm) getFactory()
                                                                         .createPrimaryConsentForm();
        primaryConsentForm.setQuestion(question);
        primaryConsentForm.setReferenceNumber(refNumber);

        return primaryConsentForm;
    }

    /**
     *
     *
     * @param sectionName 
     * @param sectionDisplayText 
     * @param descriptionField 
     * @param multipleSectionsAllowed 
     *
     * @return 
     */
    public static Section createISection(String sectionName,
                                         String sectionDisplayText, String descriptionField,
                                         boolean multipleSectionsAllowed) {
        Section section = getFactory().createSection(sectionName);
        section.setDisplayText(sectionDisplayText);
        section.setDescription(descriptionField);

        SectionOccurrence secOcc = getFactory()
                                        .createSectionOccurrence(sectionName);
        secOcc.setDescription(descriptionField);
        secOcc.setMultipleAllowed(multipleSectionsAllowed);
        section.addOccurrence(secOcc);

        return section;
    }

    /**
     *
     *
     * @param sectionOccName 
     *
     * @return 
     */
    public static SectionOccurrence createISectionOccurrence(
            String sectionOccName) {
        SectionOccurrence sectionOcc = getFactory()
                                            .createSectionOccurrence(sectionOccName);

        return sectionOcc;
    }

    /**
     *
     *
     * @param siteName 
     * @param siteID 
     * @param geographicCode 
     * @param consultants 
     *
     * @return 
     */
    public static Site createISite(String siteName, String siteID,
        String geographicCode, ArrayList<String> consultants) {
        Site site = new Site(siteName, siteID, geographicCode);

        for (String consultant : consultants) {
            site.addConsultant(consultant);
        }

        return site;
    }

    /**
     *
     *
     * @param shortNameField 
     * @param code 
     * @param longNameField 
     * @param genState 
     * @param active 
     *
     * @return 
     */
    public static Status createIStatus(String shortNameField, int code,
                                       String longNameField, GenericState genState, boolean active) {
        Status status = getFactory().createStatus(shortNameField, code);
        status.setLongName(longNameField);
        status.setGenericState(genState);
        status.setInactive(!active);

        return status;
    }

    /**
     *
     *
     * @param message 
     * @param description 
     * @param lowerLimit 
     * @param upperLimit 
     * @param pattern 
     * @param patternDetails 
     *
     * @return 
     */
    public static TextValidationRule createITextValidationRule(
        String message, String description, Integer lowerLimit,
        Integer upperLimit, String pattern, String patternDetails) {
        TextValidationRule textRule = getFactory().createTextValidationRule();
        textRule.setMessage(message);
        textRule.setDescription(description);
        textRule.setLowerLimit(lowerLimit);
        textRule.setUpperLimit(upperLimit);
        ((ValidationRule) textRule).setIsEditable(true);
        ((ValidationRule) textRule).setName(description);

        if (!((pattern == null) || pattern.equals(""))) {
            textRule.setPattern(pattern);
        }

        if (!((patternDetails == null) || patternDetails.equals(""))) {
            textRule.setPatternDetails(patternDetails);
        }

        return textRule;
    }

    /**
     *
     *
     * @param wsnamespace 
     * @param wsoperation 
     * @param wsurl 
     * @param resultClass 
     * @param viewableOutput 
     *
     * @return 
     */
    public static Transformer createITransformer(String wsnamespace,
                                                 String wsoperation, String wsurl, String resultClass,
                                                 boolean viewableOutput) {
        Transformer transformer = getFactory()
                                       .createTransformer(wsurl, wsnamespace,
                                               wsoperation, resultClass, viewableOutput);

        return transformer;
    }

    /**
     *
     *
     * @param abbreviation 
     * @param description 
     * @param baseUnit 
     * @param factor 
     *
     * @return 
     */
    public static Unit createIUnit(String abbreviation, String description,
                                   Unit baseUnit, String factor) {
        Unit unit = getFactory().createUnit(abbreviation);
        unit.setAbbreviation(abbreviation);
        unit.setDescription(description);

        if (baseUnit != null) {
            unit.setBaseUnit(baseUnit);
        }

        if ((factor != null) && !factor.equals("")) {
            unit.setFactor(new Double(factor));
        }

        if ((baseUnit != null) && !baseUnit.equals("")) {
            unit.setBaseUnit(baseUnit);
        } else {
            unit.setBaseUnit(null);
        }

        return unit;
    }

    /**
     * Configure the standard document statuses
     * @param factory HibernateFactory
     * @param document the IDocument for which statuses must be added
     */
    public static void createDocumentStatuses(Document document) {
        Status incomplete = factory.createStatus(Status.DOC_STATUS_INCOMPLETE,
                "Incomplete", 0);
        Status pending = factory.createStatus(Status.DOC_STATUS_PENDING,
                "Pending Approval", 1);
        Status rejected = factory.createStatus(Status.DOC_STATUS_REJECTED,
                "Rejected", 2);
        Status approved = factory.createStatus(Status.DOC_STATUS_APPROVED,
                "Approved", 3);
        Status complete = factory.createStatus(Status.DOC_STATUS_COMPLETE,
                "Complete", 4);
        
        incomplete.addStatusTransition(pending);
        incomplete.addStatusTransition(complete);
        complete.addStatusTransition(incomplete);
        complete.addStatusTransition(pending);
        pending.addStatusTransition(incomplete);
        pending.addStatusTransition(rejected);
        pending.addStatusTransition(approved);
        rejected.addStatusTransition(pending);
        approved.addStatusTransition(pending);
        
        document.addStatus(incomplete);
        document.addStatus(pending);
        document.addStatus(rejected);
        document.addStatus(approved);
        document.addStatus(complete);
    }

    /**
     *
     *
     * @param name 
     *
     * @return 
     */
    public static TextEntry createTextEntry(String name) {
        return getFactory().createTextEntry(name);
    }

    /**
     *
     *
     * @param name 
     * @param displayText 
     *
     * @return 
     */
    public static TextEntry createTextEntry(String name, String displayText) {
        return getFactory().createTextEntry(name, displayText);
    }

    /**
     *
     *
     * @param name 
     *
     * @return 
     */
    public static LongTextEntry createLongTextEntry(String name) {
        return getFactory().createLongTextEntry(name);
    }

    /**
     *
     *
     * @param name 
     *
     * @return 
     */
    public static NumericEntry createNumericEntry(String name) {
        return getFactory().createNumericEntry(name);
    }

    /**
     *
     *
     * @param name 
     *
     * @return 
     */
    public static OptionEntry createOptionEntry(String name) {
        return getFactory().createOptionEntry(name);
    }

    /**
     *
     *
     * @param name 
     *
     * @return 
     */
    public static Option createOption(String name) {
        return getFactory().createOption(name);
    }

    /**
     *
     *
     * @return 
     */
    public static OptionDependent createOptionDependent() {
        return getFactory().createOptionDependent();
    }

    /**
     *
     *
     * @param name 
     *
     * @return 
     */
    public static BooleanEntry createBooleanEntry(String name) {
        return getFactory().createBooleanEntry(name);
    }

    /**
     *
     *
     * @param name 
     *
     * @return 
     */
    public static DerivedEntry createDerivedEntry(String name) {
        return getFactory().createDerivedEntry(name);
    }

    /**
     *
     *
     * @param name 
     *
     * @return 
     */
    public static ExternalDerivedEntry createExternalDerivedEntry(String name) {
    	ExternalDerivedEntry entry = getFactory().createExternalDerivedEntry(name);
    	entry.setTransformWithStdCodes(true);
    	return entry;
    }

    /**
     *
     *
     * @param name 
     *
     * @return 
     */
    public static DateEntry createDateEntry(String name) {
        return getFactory().createDateEntry(name);
    }

    /**
     *
     *
     * @param name 
     *
     * @return 
     */
    public static IntegerEntry createIntegerEntry(String name) {
        return getFactory().createIntegerEntry(name);
    }

    /**
     *
     *
     * @param name 
     *
     * @return 
     */
    public static NarrativeEntry createNarrativeEntry(String name) {
        return getFactory().createNarrativeEntry(name);
    }

    /**
     *
     *
     * @param name 
     *
     * @return 
     */
    public static CompositeEntry createComposite(String name) {
        return getFactory().createComposite(name);
    }
    
    public static EslCustomField createEslCustomField(String name, ArrayList<String> values) {
    	EslCustomField field =  getFactory().createEslCustomField(name);
    	for ( String value: values ){
    		field.addValue(value);
    	}
    	return field;
    }
    
    public static AuditableChange createAuditableChange(String action, String comment, String user) {
    	return getFactory().createAuditableChange(action, comment, user);
    }

    public static AuditLog createAuditLog() {
    	return getFactory().createAuditLog();
    }
    
    /**
     * If all document occurrences of a document are locked, then the
     * document itself is locked
     * Ignores occurrences  generated for previewing items 
     * in Create document panel
     * @param document
     * @return true if doc is locked; false if not
     */
    public static boolean isDocumentLocked(Document document) {
    	int numLocked = 0;
    	int numOccs = document.numOccurrences();
    	
    	if (document.numOccurrences() == 0) {
    		numLocked = -1;
    	}
    	
    	for (int i=0; i<document.numOccurrences(); i++) {
    		if (document.getOccurrence(i).getName().startsWith("Preview")) {
    			numOccs--;
    		} else {
    			if (document.getOccurrence(i).isLocked()) {
    				numLocked++;
    			}
    		}
    	}
    	
    	return (numLocked == numOccs);
    }
    
}
