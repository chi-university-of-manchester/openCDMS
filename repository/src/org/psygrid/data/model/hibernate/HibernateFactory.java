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

import org.psygrid.data.model.*;

import java.util.Date;

public class HibernateFactory implements Factory {

    public IntegerValidationRule createIntegerValidationRule() {
        return new IntegerValidationRule();
    }

    public DataSet createDataset(String name) {
        return new DataSet(name);
    }

    public DataSet createDataset(String name, String displayText) {
        return new DataSet(name, displayText);
    }

    public Document createDocument(String name) {
        return new Document(name);
    }

    public Document createDocument(String name, String displayText) {
        return new Document(name, displayText);
    }

    public Section createSection(String name) {
        return new Section(name);
    }

    public Section createSection(String name, String displayText) {
        return new Section(name, displayText);
    }

    public CompositeEntry createComposite(String name) {
        return new CompositeEntry(name);
    }

    public CompositeEntry createComposite(String name, String displayText) {
        return new CompositeEntry(name, displayText);
    }

    public TextEntry createTextEntry(String name) {
        return new TextEntry(name);
    }

    public TextEntry createTextEntry(String name, EntryStatus entryStatus) {
        return new TextEntry(name, entryStatus);
    }

    public TextEntry createTextEntry(String name, String displayText, EntryStatus entryStatus) {
        return new TextEntry(name, displayText, entryStatus);
    }

    public TextEntry createTextEntry(String name, String displayText) {
        return new TextEntry(name, displayText);
    }

    public LongTextEntry createLongTextEntry(String name) {
        return new LongTextEntry(name);
    }

    public LongTextEntry createLongTextEntry(String name, EntryStatus entryStatus) {
        return new LongTextEntry(name, entryStatus);
    }

    public LongTextEntry createLongTextEntry(String name, String displayText, EntryStatus entryStatus) {
        return new LongTextEntry(name, displayText, entryStatus);
    }

    public LongTextEntry createLongTextEntry(String name, String displayText) {
        return new LongTextEntry(name, displayText);
    }

    public NumericEntry createNumericEntry(String name) {
        return new NumericEntry(name);
    }

    public NumericEntry createNumericEntry(String name, EntryStatus entryStatus) {
        return new NumericEntry(name, entryStatus);
    }

    public NumericEntry createNumericEntry(String name, String displayText, EntryStatus entryStatus) {
        return new NumericEntry(name, displayText, entryStatus);
    }

    public NumericEntry createNumericEntry(String name, String displayText) {
        return new NumericEntry(name, displayText);
    }

    public OptionEntry createOptionEntry(String name) {
        return new OptionEntry(name);
    }

    public OptionEntry createOptionEntry(String name, EntryStatus entryStatus) {
        return new OptionEntry(name, entryStatus);
    }

    public OptionEntry createOptionEntry(String name, String displayText, EntryStatus entryStatus) {
        return new OptionEntry(name, displayText, entryStatus);
    }

    public OptionEntry createOptionEntry(String name, String displayText) {
        return new OptionEntry(name, displayText);
    }
    
    public DateEntry createDateEntry(String name) {
        return new DateEntry(name);
    }

    public DateEntry createDateEntry(String name, EntryStatus entryStatus) {
        return new DateEntry(name, entryStatus);
    }

    public DateEntry createDateEntry(String name, String displayText, EntryStatus entryStatus) {
        return new DateEntry(name, displayText, entryStatus);
    }

    public DateEntry createDateEntry(String name, String displayText) {
        return new DateEntry(name, displayText);
    }

    public PrimaryConsentForm createPrimaryConsentForm() {
        return new PrimaryConsentForm();
    }

    public AssociatedConsentForm createAssociatedConsentForm() {
        return new AssociatedConsentForm();
    }

    public Option createOption(String displayText, int code) {
        return new Option(displayText, code);
    }

    public Option createOption(String displayText) {
        return new Option(displayText);
    }

    public Option createOption(String name, String displayText) {
        return new Option(name, displayText);
    }

    public Option createOption(String name, String displayText, int codeValue) {
        return new Option(name, displayText, codeValue);
    }

    public Unit createUnit(String abbreviation) {
        return new Unit(abbreviation);
    }

    public OptionDependent createOptionDependent() {
        return new OptionDependent();
    }

    public DerivedEntry createDerivedEntry(String name) {
        return new DerivedEntry(name);
    }
    
    public DerivedEntry createDerivedEntry(String name, EntryStatus entryStatus) {
        return new DerivedEntry(name, entryStatus);
    }

    public DerivedEntry createDerivedEntry(String name, String displayText, EntryStatus entryStatus) {
        return new DerivedEntry(name, displayText, entryStatus);
    }

    public ExternalDerivedEntry createExternalDerivedEntry(String name, String displayText) {
        return new ExternalDerivedEntry(name, displayText);
    }

    public ExternalDerivedEntry createExternalDerivedEntry(String name) {
        return new ExternalDerivedEntry(name);
    }
    
    public ExternalDerivedEntry createExternalDerivedEntry(String name, EntryStatus entryStatus) {
        return new ExternalDerivedEntry(name, entryStatus);
    }

    public ExternalDerivedEntry createExternalDerivedEntry(String name, String displayText, EntryStatus entryStatus) {
        return new ExternalDerivedEntry(name, displayText, entryStatus);
    }

    public DerivedEntry createDerivedEntry(String name, String displayText) {
        return new DerivedEntry(name, displayText);
    }
    
    public BooleanEntry createBooleanEntry(String name, EntryStatus entryStatus) {
        return new BooleanEntry(name, entryStatus);
    }

    public BooleanEntry createBooleanEntry(String name, String displayText, EntryStatus entryStatus) {
        return new BooleanEntry(name, displayText, entryStatus);
    }

    public BooleanEntry createBooleanEntry(String name, String displayText) {
        return new BooleanEntry(name, displayText);
    }

    public BooleanEntry createBooleanEntry(String name) {
        return new BooleanEntry(name);
    }

    public BinaryObject createBinaryObject(byte[] data) {
        return new BinaryObject(data);
    }

    public ConsentFormGroup createConsentFormGroup() {
        return new ConsentFormGroup();
    }

    public DateValidationRule createDateValidationRule() {
        return new DateValidationRule();
    }

    public NumericValidationRule createNumericValidationRule() {
        return new NumericValidationRule();
    }

    public TextValidationRule createTextValidationRule() {
        return new TextValidationRule();
    }

    public Status createStatus(String name, int code) {
        return new Status(name, name, code);
    }

    public Status createStatus(String shortName, String longName, int code) {
        return new Status(shortName, longName, code);
    }

    public StandardCode createStandardCode(String description, int code) {
        return new StandardCode(description, code);
    }

    public DocumentOccurrence createDocumentOccurrence(String name) {
        return new DocumentOccurrence(name);
    }

    public Reminder createReminder(int time, TimeUnits units, ReminderLevel level) {
        return new Reminder(time, units, level);
    }

    public NarrativeEntry createNarrativeEntry(String name, String displayText) {
        return new NarrativeEntry(name, displayText);
    }

    public NarrativeEntry createNarrativeEntry(String name) {
        return new NarrativeEntry(name);
    }

    public Transformer createTransformer() {
        return new Transformer();
    }

    public Transformer createTransformer(String wsUrl, String wsNamespace, String wsOperation, String resultClass) {
        return new Transformer(wsUrl, wsNamespace, wsOperation, resultClass);
    }

    public Transformer createTransformer(String wsUrl, String wsNamespace, String wsOperation, String resultClass, boolean viewableOutput) {
        return new Transformer(wsUrl, wsNamespace, wsOperation, resultClass, viewableOutput);
    }
    
    public DocumentGroup createDocumentGroup(String name) {
        return new DocumentGroup(name);
    }

    public SectionOccurrence createSectionOccurrence(String name) {
        return new SectionOccurrence(name);
    }

    public IntegerEntry createIntegerEntry(String name, EntryStatus entryStatus) {
        return new IntegerEntry(name, entryStatus);
    }

    public IntegerEntry createIntegerEntry(String name, String displayText, EntryStatus entryStatus) {
        return new IntegerEntry(name, displayText, entryStatus);
    }

    public IntegerEntry createIntegerEntry(String name, String displayText) {
        return new IntegerEntry(name, displayText);
    }

    public IntegerEntry createIntegerEntry(String name) {
        return new IntegerEntry(name);
    }

    public Group createGroup(String name) {
        return new Group(name);
    }

    public EslCustomField createEslCustomField(String name) {
    	EslCustomField field = new EslCustomField();
    	field.setName(name);
    	return field;
    }
    
    public AuditableChange createAuditableChange(String action, String comment, String user) {
    	AuditableChange change = new AuditableChange();
    	change.setAction(action);
    	change.setComment(comment);
    	change.setUser(user);
    	change.setTimestamp(new Date(System.currentTimeMillis()));
    	return change;
    }
    
    public AuditLog createAuditLog() {
    	return new AuditLog();
    }
    
}
