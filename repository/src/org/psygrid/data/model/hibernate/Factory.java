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

package org.psygrid.data.model.hibernate;

import org.psygrid.data.model.hibernate.*;

/**
 * Factory interface used to manage the creation of data repository
 * model objects.
 * 
 * @author Rob Harper
 *
 */
public interface Factory {
    
    /**
     * Create a new data set with the given name.
     * 
     * @param name The name of the new data set.
     * @return The new data set.
     */
    public DataSet createDataset(String name);
    
    /**
     * Create a new data set with the given name and
     * display text.
     * 
     * @param name The name of the new data set.
     * @param displayText The display text of the new data set.
     * @return The new data set.
     */
    public DataSet createDataset(String name, String displayText);
    
    /**
     * Create a new document with the given name.
     * 
     * @param name The name of the new document-type element.
     * @return The new element.
     */
    public Document createDocument(String name);
    
    /**
     * Create a new document with the given name and
     * display text.
     * 
     * @param name The name of the new document-type element.
     * @param displayText The display text of the new document-type 
     * element.
     * @return The new element.
     */
    public Document createDocument(String name, String displayText);
    
    /**
     * Create a new section with the given name.
     * 
     * @param name The name of the new section.
     * @return The new section.
     */
    public Section createSection(String name);
    
    /**
     * Create a new section with the given name and
     * display text.
     * 
     * @param name The name of the new section.
     * @param displayText The display text of the new section.
     * @return The new section.
     */
    public Section createSection(String name, String displayText);
    
    /**
     * Create a new section occurrence with the given name.
     * 
     * @param name The name of the new section occurrence.
     * @return The new section occurrence.
     */
    public SectionOccurrence createSectionOccurrence(String name);
    
    /**
     * Create a new narrative-type element with the given name.
     * 
     * @param name The name of the new narrative-type element.
     * @return The new element.
     */
    public NarrativeEntry createNarrativeEntry(String name);
    
    /**
     * Create a new narrative-type element with the given name and
     * display text.
     * 
     * @param name The name of the new narrative-type element.
     * @param displayText The display text of the new narrative-type 
     * element.
     * @return The new element.
     */
    public NarrativeEntry createNarrativeEntry(String name, String displayText);
    
    /**
     * Create a new composite-type element with the given name.
     * 
     * @param name The name of the new composite-type element.
     * @return The new element.
     */
    public CompositeEntry createComposite(String name);
    
    /**
     * Create a new composite-type element with the given name and
     * display text.
     * 
     * @param name The name of the new composite-type element.
     * @param displayText The display text of the new composite-type 
     * element.
     * @return The new element.
     */
    public CompositeEntry createComposite(String name, String displayText);
    
    /**
     * Create a new text entry with the given name.
     * 
     * @param name The name of the new text entry.
     * @return The new text entry.
     */
    public TextEntry createTextEntry(String name);
    
    /**
     * Create a new text entry with the given name and display text.
     * 
     * @param name The name of the new Text Entry
     * @param displayText The display text of the new text entry.
     * @return The new Text Entry.
     */
    public TextEntry createTextEntry(String name, String displayText);
    
    /**
     * Create a new text entry with the given name and status.
     * 
     * @param name The name of the new text entry.
     * @param entryStatus The status of the new text entry.
     * @return The new text entry.
     */
    public TextEntry createTextEntry(String name, EntryStatus entryStatus);
    
    /**
     * Create a new text entry with the given name, display text
     * and status.
     * 
     * @param name The name of the new text entry.
     * @param displayText The display text of the new text entry.
     * @param entryStatus The status of the new text entry.
     * @return The new text entry.
     */
    public TextEntry createTextEntry(String name, String displayText, EntryStatus entryStatus);
    
    /**
     * Create a new long text entry with the given name.
     * 
     * @param name The name of the new text entry.
     * @return The new text entry.
     */
    public LongTextEntry createLongTextEntry(String name);
    
    /**
     * Create a new long text entry with the given name and display text.
     * 
     * @param name The name of the new long text entry
     * @param displayText The display text of the new long text entry.
     * @return The new long text entry.
     */
    public LongTextEntry createLongTextEntry(String name, String displayText);
    
    /**
     * Create a new long text entry with the given name and status.
     * 
     * @param name The name of the new long text entry.
     * @param entryStatus The status of the new long text entry.
     * @return The new long text entry.
     */
    public LongTextEntry createLongTextEntry(String name, EntryStatus entryStatus);
    
    /**
     * Create a new long text entry with the given name, display text
     * and status.
     * 
     * @param name The name of the new long text entry.
     * @param displayText The display text of the new long text entry.
     * @param entryStatus The status of the new long text entry.
     * @return The new long text entry.
     */
    public LongTextEntry createLongTextEntry(String name, String displayText, EntryStatus entryStatus);
    
    /**
     * Create a new date entry with the given name.
     * 
     * @param name The name of the new date entry
     * @return The new date entry.
     */
    public DateEntry createDateEntry(String name);
    
    /**
     * Create a new date entry with the given name and display text.
     * 
     * @param name The name of the new date entry
     * @param displayText The display text of the new date entry.
     * @return The new date entry.
     */
    public DateEntry createDateEntry(String name, String displayText);
    
    /**
     * Create a new date entry with the given name and status.
     * 
     * @param name The name of the new date entry
     * @param entryStatus The status of the new date entry.
     * @return The new date entry.
     */
    public DateEntry createDateEntry(String name, EntryStatus entryStatus);
    
    /**
     * Create a new date entry with the given name, display text
     * and status.
     * 
     * @param name The name of the new date entry
     * @param displayText The display text of the new date entry.
     * @param entryStatus The status of the new date entry.
     * @return The new date entry.
     */
    public DateEntry createDateEntry(String name, String displayText, EntryStatus entryStatus);
    
    /**
     * Create a new numeric entry with the given name.
     * 
     * @param name The name of the new numeric entry.
     * @return The new numeric entry.
     */
    public NumericEntry createNumericEntry(String name);
    
    /**
     * Create a new numeric entry with the given name and display text.
     * 
     * @param name The name of the new numeric entry.
     * @param displayText The display text of the new numeric entry.
     * @return The new numeric entry.
     */
    public NumericEntry createNumericEntry(String name, String displayText);
    
    /**
     * Create a new numeric entry with the given name and status.
     * 
     * @param name The name of the new numeric entry.
     * @param entryStatus The status of the new numeric entry.
     * @return The new numeric entry.
     */
    public NumericEntry createNumericEntry(String name, EntryStatus entryStatus);
    
    /**
     * Create a new numeric entry with the given name, display text
     * and status.
     * 
     * @param name The name of the new numeric entry.
     * @param displayText The display text of the new numeric entry.
     * @param entryStatus The status of the new numeric entry.
     * @return The new numeric entry.
     */
    public NumericEntry createNumericEntry(String name, String displayText, EntryStatus entryStatus);
    
    /**
     * Create a new integer entry with the given name.
     * 
     * @param name The name of the new integer entry.
     * @return The new integer entry.
     */
    public IntegerEntry createIntegerEntry(String name);
    
    /**
     * Create a new integer entry with the given name and display text.
     * 
     * @param name The name of the new integer entry.
     * @param displayText The display text of the new integer entry.
     * @return The new integer entry.
     */
    public IntegerEntry createIntegerEntry(String name, String displayText);
    
    /**
     * Create a new integer entry with the given name and status.
     * 
     * @param name The name of the new integer entry.
     * @param entryStatus The status of the new integer entry.
     * @return The new integer entry.
     */
    public IntegerEntry createIntegerEntry(String name, EntryStatus entryStatus);
    
    /**
     * Create a new integer entry with the given name, display text
     * and status.
     * 
     * @param name The name of the new integer entry.
     * @param displayText The display text of the new integer entry.
     * @param entryStatus The status of the new integer entry.
     * @return The new integer entry.
     */
    public IntegerEntry createIntegerEntry(String name, String displayText, EntryStatus entryStatus);
    
    /**
     * Create a new option entry with the given name.
     * 
     * @param name The name of the new option entry.
     * @return The new option entry.
     */
    public OptionEntry createOptionEntry(String name);
    
    /**
     * Create a new option entry with the given name and display
     * text.
     * 
     * @param name The name of the new option entry
     * @param displayText The display text of the new option entry.
     * @return The new option entry.
     */
    public OptionEntry createOptionEntry(String name, String displayText);
    
    /**
     * Create a new option entry with the given name and status.
     * 
     * @param name The name of the new option entry.
     * @param entryStatus The status of the new option entry.
     * @return The new option entry.
     */
    public OptionEntry createOptionEntry(String name, EntryStatus entryStatus);
    
    /**
     * Create a new option entry with the given name, display text 
     * and status.
     * 
     * @param name The name of the new option entry.
     * @param displayText The display text of the new option entry.
     * @param entryStatus The status of the new option entry.
     * @return The new option entry.
     */
    public OptionEntry createOptionEntry(String name, String displayText, EntryStatus entryStatus);
    
    /**
     * Create a new boolean entry with the given name.
     * 
     * @param name The name of the new boolean entry.
     * @return The new boolean entry.
     */
    public BooleanEntry createBooleanEntry(String name);
    
    /**
     * Create a new boolean entry with the given name and display text.
     * 
     * @param name The name of the new boolean entry.
     * @param displayText The display text of the new boolean entry.
     * @return The new boolean entry.
     */
    public BooleanEntry createBooleanEntry(String name, String displayText);
    
    /**
     * Create a new boolean entry with the given name and status.
     * 
     * @param name The name of the new boolean entry.
     * @param entryStatus The status of the new boolean entry.
     * @return The new boolean entry.
     */
    public BooleanEntry createBooleanEntry(String name, EntryStatus entryStatus);
    
    /**
     * Create a new boolean entry with the given name, display text
     * and status.
     * 
     * @param name The name of the new boolean entry.
     * @param displayText The display text of the new boolean entry.
     * @param entryStatus The status of the new boolean entry.
     * @return The new boolean entry.
     */
    public BooleanEntry createBooleanEntry(String name, String displayText, EntryStatus entryStatus);
    
    /**
     * Create a new derived entry with the given name.
     * 
     * @param name The name of the new derived entry.
     * @return The new derived entry.
     */
    public DerivedEntry createDerivedEntry(String name);
    
    /**
     * Create a new derived entry with the given name and display text.
     * 
     * @param name The name of the new derived entry.
     * @param displayText The display text of the new derived entry.
     * @return The new derived entry.
     */
    public DerivedEntry createDerivedEntry(String name, String displayText);
    
    /**
     * Create a new derived entry with the given name and status.
     * 
     * @param name The name of the new derived entry.
     * @param entryStatus The status of the new derived entry.
     * @return The new derived entry.
     */
    public DerivedEntry createDerivedEntry(String name, EntryStatus entryStatus);
    
    /**
     * Create a new derived entry with the given name, display text
     * and status.
     * 
     * @param name The name of the new derived entry.
     * @param displayText The display text of the new derived entry.
     * @param entryStatus The status of the new derived entry.
     * @return The new derived entry.
     */
    public DerivedEntry createDerivedEntry(String name, String displayText, EntryStatus entryStatus);
    
    /**
     * Create a new external derived entry with the given name, display text
     * and status.
     * 
     * @param name The name of the new external derived entry.
     * @param displayText The display text of the new external derived entry.
     * @param entryStatus The status of the new external derived entry.
     * @return The new external derived entry.
     */
    public ExternalDerivedEntry createExternalDerivedEntry(String name, String displayText, EntryStatus entryStatus);
    
    
    /**
     * Create a new external derived entry with the given name.
     * 
     * @param name The name of the new external derived entry.
     * @return The new external derived entry.
     */
    public ExternalDerivedEntry createExternalDerivedEntry(String name);
    
    /**
     * Create a new external derived entry with the given name and display text.
     * 
     * @param name The name of the new external derived entry.
     * @param displayText The display text of the new external derived entry.
     * @return The new external derived entry.
     */
    public ExternalDerivedEntry createExternalDerivedEntry(String name, String displayText);
    
    /**
     * Create a new external derived entry with the given name and status.
     * 
     * @param name The name of the new external derived entry.
     * @param entryStatus The status of the new external derived entry.
     * @return The new external derived entry.
     */
    public ExternalDerivedEntry createExternalDerivedEntry(String name, EntryStatus entryStatus);
    
    /**
     * Create a new consent form group.
     * 
     * @return The new consent form group.
     */
    public ConsentFormGroup createConsentFormGroup();
    
    /**
     * Create a new consent form.
     * 
     * @return The new consent form.
     */
    public PrimaryConsentForm createPrimaryConsentForm();
    
    /**
     * Create a new associated consent form.
     * 
     * @return The new associated consent form.
     */
    public AssociatedConsentForm createAssociatedConsentForm();
    
    /**
     * Create a new option with the given display text.
     * 
     * @param displayText The displayText of the new option.
     * @return The new option.
     */
    public Option createOption(String displayText);
    
    /**
     * Create a new option with the given name and display text.
     * 
     * @param displayText The displayText of the new option.
     * @param code The code value of the new option.
     * @return The new option.
     */
    public Option createOption(String displayText, int code);
    
    /**
     * Create a new option with the given name and display text.
     * 
     * @param name The name of the new option.
     * @param displayText The displayText of the new option.
     * @return The new option.
     */
    public Option createOption(String name, String displayText);
    
    /**
     * Create a new option with the given name, display text and code value.
     * 
     * @param name The name of the new option.
     * @param displayText The displayText of the new option.
     * @param code The code value of the new option.
     * @return The new option.
     */
    public Option createOption(String name, String displayText, int code);
    
    /**
     * Create a new unit with the given abbreviation.
     * 
     * @param abbreviation The abbreviation of the new unit.
     * @return The new Unit.
     */
    public Unit createUnit(String abbreviation);
    
    /**
     * Create a new option dependent.
     * 
     * @return The new option dependent.
     */
    public OptionDependent createOptionDependent();

    /**
     * Create a new binary object.
     * 
     * @param data binary data that the binary object represents.
     * @return The new binary object.
     */
    public BinaryObject createBinaryObject(byte[] data);
    
    /**
     * Create a new date validation rule.
     * 
     * @return The new date validation rule.
     */
    public DateValidationRule createDateValidationRule();
    
    /**
     * Create a new numeric validation rule.
     * 
     * @return The new numeric valdiation rule.
     */
    public NumericValidationRule createNumericValidationRule();
    
    /**
     * Create a new integer validation rule.
     * 
     * @return The new integer valdiation rule.
     */
    public IntegerValidationRule createIntegerValidationRule();
    
    /**
     * Create a new text validation rule.
     * 
     * @return The new text validation rule.
     */
    public TextValidationRule createTextValidationRule();
    
    /**
     * Create a new status with the given name (used for both
     * the short and long names) and code.
     * 
     * @param name The name of the new status.
     * @param code The code of the new status.
     * @return The new status.
     */
    public Status createStatus(String name, int code);
    
    /**
     * Create a new status with the given short name, long name
     * and code.
     * 
     * @param shortName The short name of the new status.
     * @param longName The long name of the new status.
     * @param code The code of the new status.
     * @return The new status.
     */
    public Status createStatus(String shortName, String longName, int code);
    
    /**
     * Create a new standard code, with the given description and code.
     * 
     * @param description The description of the standard code.
     * @param code The numeric code of the standard code.
     * @return The new status
     */
    public StandardCode createStandardCode(String description, int code);
    
    /**
     * Create a new document occurrence, with the given name.
     * 
     * @param name The name of the document occurrence.
     * @return The new document occurrence.
     */
    public DocumentOccurrence createDocumentOccurrence(String name);
    
    /**
     * Create a new reminder, with the given time and units
     * <p>
     * For example, a reminder to be sent 45 days after the creation
     * of a record would be created with time of 45 and units of days.
     * 
     * @param time The time for the reminder.
     * @param units The units for the reminder.
     * @param level The level of the reminder.
     * @return The new reminder.
     */
    public Reminder createReminder(int time, TimeUnits units, ReminderLevel level);
    
    /**
     * Create a new transformer.
     * 
     * @return The new transformer.
     */
    public Transformer createTransformer();
    
    /**
     * Create a new transformer with the given URL, namespace, operation
     * and result class.
     * 
     * @param wsUrl The web-service URL of the transformer.
     * @param wsNamespace The web-service namespace of the transformer.
     * @param wsOperation The web-service operation of the transformer.
     * @param resultClass The name of the sub-class of Value that the result
     * of the transformation will be stored in.
     * @return The new transformer.
     */
    public Transformer createTransformer(String wsUrl, String wsNamespace, String wsOperation, String resultClass);

    /**
     * Create a new transformer with the given URL, namespace, operation,
     * result class and viewable output flag
     * 
     * @param wsUrl The web-service URL of the transformer.
     * @param wsNamespace The web-service namespace of the transformer.
     * @param wsOperation The web-service operation of the transformer.
     * @param resultClass The name of the sub-class of Value that the result
     * @param viewableOutput The viewable output flag.
     * of the transformation will be stored in.
     * @return The new transformer.
     */
    public Transformer createTransformer(String wsUrl, String wsNamespace, String wsOperation, String resultClass, boolean viewableOutput);

    /**
     * Create a new document group with the given name.
     * 
     * @param name The name of the group.
     * @return The new document group.
     */
    public DocumentGroup createDocumentGroup(String name);
    
    /**
     * Create a new group with the given name.
     * 
     * @param name The name of the group.
     * @return The new group.
     */
    public Group createGroup(String name);
    
    /**
     * Create a new ESL custom field with the given name.
     * 
     * @param name The name.
     * @return
     */
    public EslCustomField createEslCustomField(String name);
    
    /**
     * Create an instance of an audit log used for 
     * tracking changes to elements
     * 
     * @return the newly created audit log
     */
    public AuditLog createAuditLog();
    
    
    /**
     * Create an auditable change used to specify
     * an individual change to an element
     * 
     * @param action the type of change
     * @param comment the user's comment
     * @param the user making the change
     * @return the change
     */
    public AuditableChange createAuditableChange(String action, String comment, String user);
    
}

