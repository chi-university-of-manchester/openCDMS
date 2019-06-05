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
package org.psygrid.collection.entry.renderer;

import java.awt.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.Executable;
import org.psygrid.collection.entry.builder.BuilderHandler;
import org.psygrid.collection.entry.model.TextPresModel;
import org.psygrid.collection.entry.ui.BasicTextEntryField;
import org.psygrid.collection.entry.ui.EntryComponent;
import org.psygrid.collection.entry.ui.EntryLabel;
import org.psygrid.collection.entry.ui.TableTestHelper;
import org.psygrid.collection.entry.ui.TextEntryField;
import org.psygrid.collection.entry.ui.VariableTable;
import org.psygrid.data.model.IDateValue;
import org.psygrid.data.model.INumericValue;
import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public class DerivedRendererTest extends AbstractEntryTestCase {
    
    private DerivedEntry derivedEntry;
    private Section section;
    private NumericEntry numericEntry0;
    private NumericEntry numericEntry1;
    private OptionEntry optionEntry0;
    private Option optionEntry0Option;
    private OptionEntry optionEntry1;
    private Option optionEntry1Option;
    
    private static final int OPTION_ENTRY0_OPTION0_VALUE = 33;
    private static final int OPTION_ENTRY0_OPTION1_VALUE = 45;
    private static final int OPTION_ENTRY1_OPTION0_VALUE = 52;
    private static final int OPTION_ENTRY1_OPTION1_VALUE = 69;
    private DataSet dataSet;
    private Document document;
    private DocumentOccurrence docOccurrence;
    private Application application;
    private SectionOccurrence sectionOcc;
    private DocumentInstance docInstance;
    private Record record;
    private Factory factory;
    
    private void init() throws Exception {
        application = createApplication();
        record = getRecord();
        dataSet = record.getDataSet();
        docOccurrence = getDocumentOccurrence();
        document = docOccurrence.getDocument();
        docInstance = document.generateInstance(docOccurrence);
        dataSet.addDocument(document);
        record.addDocumentInstance(docInstance);
        factory = getFactory();
        sectionOcc = getSectionOccurrence();
        section = sectionOcc.getSection();
        document.addSection(section);
        numericEntry0 = factory.createNumericEntry("Firstnumeric",
                "First numeric");
        numericEntry1 = factory.createNumericEntry("Secondnumeric",
                "Second numeric");
        optionEntry0 = factory.createOptionEntry("Firstoption", "First option");
        optionEntry0.addOption(factory.createOption("Thirty-three",
                OPTION_ENTRY0_OPTION0_VALUE));
        optionEntry0Option = factory.createOption("Fourty-five",
                OPTION_ENTRY0_OPTION1_VALUE);
        optionEntry0.addOption(optionEntry0Option);
        optionEntry1 = factory.createOptionEntry("Secondoption",
                "Second option");
        optionEntry1Option = factory.createOption("Fifty-two",
                OPTION_ENTRY1_OPTION0_VALUE);
        optionEntry1.addOption(optionEntry1Option);
        optionEntry1.addOption(factory.createOption("Sixty-nine",
                OPTION_ENTRY1_OPTION1_VALUE));
        derivedEntry = factory.createDerivedEntry("Subtotal", "Subtotal");

    }
    
    public void testIfFunction() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                
                OptionEntry paidWorkOption = factory.createOptionEntry("Paid work last " +
                        "week", "Did you do any paid work in the last month, either as " +
                        "an employee or self-employed?");
                document.addEntry(paidWorkOption);
                paidWorkOption.setSection(section);
                Option paidWorkYes = factory.createOption("Yes", 1);
                paidWorkOption.addOption(paidWorkYes);
                Option paidWorkNo = factory.createOption("No", 0);
                paidWorkOption.addOption(paidWorkNo);
                paidWorkOption.setOptionCodesDisplayed(false);
                
                OptionEntry govSchemeOption = factory.createOptionEntry("Government " +
                        "scheme", "Were you on a government scheme for employment " +
                        "training?");
                document.addEntry(govSchemeOption);
                govSchemeOption.setSection(section);
                Option govSchemeYes = factory.createOption("Yes", 1);
                govSchemeOption.addOption(govSchemeYes);
                Option govSchemeNo = factory.createOption("No", 0);
                govSchemeOption.addOption(govSchemeNo);
                govSchemeOption.setOptionCodesDisplayed(false);
                
                OptionEntry jobOption = factory.createOptionEntry(
                        "Job or Business Option",
                        "Did you have a job or business you were away from?");
                document.addEntry(jobOption);
                jobOption.setSection(section);
                Option jobYesOption = factory.createOption("Yes", 1);
                jobOption.addOption(jobYesOption);
                Option jobNoOption = factory.createOption("No", 0);
                jobOption.addOption(jobNoOption);
                jobOption.setOptionCodesDisplayed(false);
                
                DerivedEntry scorePaidWork = factory.createDerivedEntry(
                        "Score - Paid Work",
                        "Is paid work in the last month present (1) or absent (0)?");
                document.addEntry(scorePaidWork);
                scorePaidWork.setSection(section);
                scorePaidWork.setFormula("if((a==1.0||b==1.0||c==1.0),1.0,0.0)");
                scorePaidWork.addVariable("a",paidWorkOption);
                scorePaidWork.addVariable("b",govSchemeOption);
                scorePaidWork.addVariable("c",jobOption);
                
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rendererHandler = application.getModel().getCurrentRendererHandler();
                
                EntryComponent field = getFieldAsEntryComponent(rendererHandler, scorePaidWork,
                        0);
                assertEquals("0", field.getTextComponent().getText());
                
                setOption(rendererHandler, paidWorkOption, 0, paidWorkYes);
                String yesExpected = "1";
                assertEquals(yesExpected, field.getTextComponent().getText());
                
                setOption(rendererHandler, govSchemeOption, 0, govSchemeYes);
                assertEquals(yesExpected, field.getTextComponent().getText());
                
                setOption(rendererHandler, jobOption, 0, jobYesOption);
                assertEquals(yesExpected, field.getTextComponent().getText());

                setOption(rendererHandler, paidWorkOption, 0, paidWorkNo);
                assertEquals(yesExpected, field.getTextComponent().getText());
                
                setOption(rendererHandler, govSchemeOption, 0, govSchemeNo);
                assertEquals(yesExpected, field.getTextComponent().getText());
                
                String noExpected = "0";
                setOption(rendererHandler, jobOption, 0, jobNoOption);
                assertEquals(noExpected, field.getTextComponent().getText());
                
            }
        });
    }
    
    public void testNumericEntryWithExistingResponse() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                
                document.addEntry(numericEntry0);
                numericEntry0.setSection(section);
                BasicResponse response0 = numericEntry0.generateInstance(sectionOcc);
                INumericValue value0 = numericEntry0.generateValue();
                double actualValue0 = 5;
                value0.setValue(Double.valueOf(actualValue0));
                response0.setValue(value0);
                docInstance.addResponse(response0);
                
                document.addEntry(numericEntry1);
                numericEntry1.setSection(section);
                BasicResponse response1 = numericEntry1.generateInstance(sectionOcc);
                INumericValue value1 = numericEntry1.generateValue();
                double actualValue1 = 7;
                value1.setValue(Double.valueOf(actualValue1));
                response1.setValue(value1);
                docInstance.addResponse(response1);
                
                derivedEntry.addVariable(numericEntry0.getName(), numericEntry0);
                derivedEntry.addVariable(numericEntry1.getName(), numericEntry1);
                derivedEntry.setFormula(numericEntry0.getName() + "+" + numericEntry1.getName());
                document.addEntry(derivedEntry);
                derivedEntry.setSection(section);
                BasicResponse derivedResponse= derivedEntry.generateInstance(sectionOcc);
                INumericValue derivedValue = derivedEntry.generateValue();
                double derivedDouble = actualValue0 + actualValue1;
                derivedValue.setValue(Double.valueOf(derivedDouble));
                derivedResponse.setValue(derivedValue);
                docInstance.addResponse(derivedResponse);
                
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rendererHandler = application.getModel().getCurrentRendererHandler();
                PresModelRenderer<?> renderer = 
                    (PresModelRenderer<?>) rendererHandler.getExistingRenderer(derivedEntry, 0);
                
                TextPresModel presModel = (TextPresModel) renderer.getPresModel();
                Double presModelValue = (Double) presModel.getValueModel().getValue();
                assertEquals(Double.valueOf(derivedDouble), presModelValue);
                assertEquals(Double.valueOf(derivedDouble), 
                        ((INumericValue) derivedResponse.getValue()).getValue());
                assertEquals(derivedResponse, presModel.getResponse());
                
                actualValue1 = 2;
                
                setField(rendererHandler, numericEntry1, String.valueOf(actualValue1));
                derivedDouble = actualValue0 + actualValue1;
                presModelValue = (Double) presModel.getValueModel().getValue();
                assertEquals(Double.valueOf(derivedDouble), presModelValue);
                assertEquals(Double.valueOf(derivedDouble), 
                        ((INumericValue) derivedResponse.getValue()).getValue());
                assertEquals(derivedResponse, presModel.getResponse());
            }
        });
    }
    
    /**
     * Tests the subtraction of two dates near to each other to make sure we
     * are not getting incorrect results due to rounding.
     * @throws Exception 
     */
    public void testDateSubtractionRounding() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();

                DateEntry dateEntry0 = factory.createDateEntry("DateEntry0");
                document.addEntry(dateEntry0);
                dateEntry0.setSection(section);
                
                DateEntry dateEntry1 = factory.createDateEntry("DateEntry1");
                document.addEntry(dateEntry1);
                dateEntry1.setSection(section);
                
                derivedEntry.addVariable(dateEntry0.getName(), dateEntry0);
                derivedEntry.addVariable(dateEntry1.getName(), dateEntry1);
                derivedEntry.setFormula(dateEntry0.getName() + "-"
                        + dateEntry1.getName());
                document.addEntry(derivedEntry);
                derivedEntry.setSection(section);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();
                String dateEntry0Value = "02-Jan-1970";
                String dateEntry1Value = "01-Jan-1970";
                String derivedEntryValue = "1";
                setField(rendererHandler, dateEntry0, dateEntry0Value);
                setField(rendererHandler, dateEntry1, dateEntry1Value);
                EntryComponent field = getFieldAsEntryComponent(rendererHandler, derivedEntry,
                        0);
                assertEquals(derivedEntryValue, field.getTextComponent().getText());
            }
        });
    }
    
    /**
     * Tests the subtraction of a date before 01-Jan-1969 from a date after
     * 01-Jan-1970 (The starting point for java.util.Date).
     * @throws Exception 
     */
    public void testOldDateSubtraction() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();

                DateEntry dateEntry0 = factory.createDateEntry("DateEntry0");
                document.addEntry(dateEntry0);
                dateEntry0.setSection(section);
                
                DateEntry dateEntry1 = factory.createDateEntry("DateEntry1");
                document.addEntry(dateEntry1);
                dateEntry1.setSection(section);
                
                derivedEntry.addVariable(dateEntry0.getName(), dateEntry0);
                derivedEntry.addVariable(dateEntry1.getName(), dateEntry1);
                derivedEntry.setFormula(dateEntry0.getName() + "-"
                        + dateEntry1.getName());
                document.addEntry(derivedEntry);
                derivedEntry.setSection(section);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();
                String dateEntry0Value = "03-Jan-1970";
                String dateEntry1Value = "30-Dec-1969";
                String derivedEntryValue = "4";
                setField(rendererHandler, dateEntry0, dateEntry0Value);
                setField(rendererHandler, dateEntry1, dateEntry1Value);
                EntryComponent field = getFieldAsEntryComponent(rendererHandler, derivedEntry,
                        0);
                assertEquals(derivedEntryValue, field.getTextComponent().getText());
            }
        });
    }
    
    public void testDateEntrySubtraction() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();

                DateEntry dateEntry0 = factory.createDateEntry("DateEntry0");
                document.addEntry(dateEntry0);
                dateEntry0.setSection(section);
                
                DateEntry dateEntry1 = factory.createDateEntry("DateEntry1");
                document.addEntry(dateEntry1);
                dateEntry1.setSection(section);
                
                derivedEntry.addVariable(dateEntry0.getName(), dateEntry0);
                derivedEntry.addVariable(dateEntry1.getName(), dateEntry1);
                derivedEntry.setFormula(dateEntry0.getName() + "-"
                        + dateEntry1.getName());
                document.addEntry(derivedEntry);
                derivedEntry.setSection(section);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();
                String dateEntry0Value = "12-Jun-2005";
                String dateEntry1Value = "02-Jun-2005";
                String derivedEntryValue = "10";
                setField(rendererHandler, dateEntry0, dateEntry0Value);
                setField(rendererHandler, dateEntry1, dateEntry1Value);
                EntryComponent field = getFieldAsEntryComponent(rendererHandler, derivedEntry,
                        0);
                assertEquals(derivedEntryValue, field.getTextComponent().getText());
            }
        });
    }
    
    /**
     * Note: month is zero-based (To match the java date API, even if this is 
     * confusing)
     */
    private Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month, day);
        return cal.getTime();
    }
    
    private DateFormat getDateFormat() {
        return RendererHelper.getInstance().getDateFormat();
    }
    
    public void testDateEntrySubtraction2() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();

                DateEntry dateEntry0 = factory.createDateEntry("DateEntry0");
                document.addEntry(dateEntry0);
                dateEntry0.setSection(section);
                
                DateEntry dateEntry1 = factory.createDateEntry("DateEntry1");
                document.addEntry(dateEntry1);
                dateEntry1.setSection(section);
                
                derivedEntry.addVariable(dateEntry0.getName(), dateEntry0);
                derivedEntry.addVariable(dateEntry1.getName(), dateEntry1);
                derivedEntry.setFormula(dateEntry0.getName() + "-"
                        + dateEntry1.getName());
                document.addEntry(derivedEntry);
                derivedEntry.setSection(section);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();
                
                Date dateEntry0Value = getDate(1999, 11, 02);
                String dateEntry0ValueText = getDateFormat().format(dateEntry0Value);
                
                Date dateEntry1Value = getDate(1990, 05, 15);
                String dateEntry1ValueText = getDateFormat().format(dateEntry1Value);
                
                long derivedEntryValueInMs = dateEntry0Value.getTime() - dateEntry1Value.getTime();
                BigDecimal derivedEntryValue = getDateAsNumberOfDays(derivedEntryValueInMs);
                setField(rendererHandler, dateEntry0, dateEntry0ValueText);
                setField(rendererHandler, dateEntry1, dateEntry1ValueText);
                EntryComponent field = getFieldAsEntryComponent(rendererHandler, derivedEntry,
                        0);
                BigDecimal actualDerivedEntryValue = new BigDecimal(field.getTextComponent().getText());
                assertTrue(derivedEntryValue.compareTo(actualDerivedEntryValue) == 0);
            }
        });
    }
    
    private BigDecimal getDateAsNumberOfDays(long dateInMs) {
        BigDecimal numMsInDay = BigDecimal.valueOf(1000 * 60 * 60 * 24);
        BigDecimal dateValue = BigDecimal.valueOf(dateInMs);
        BigDecimal numOfDays = dateValue.divide(numMsInDay, 1,
                RoundingMode.HALF_DOWN);
        return numOfDays;
        
    }
    
    public void testDateEntrySubtractionWithNegativeValue2() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();

                DateEntry dateEntry0 = factory.createDateEntry("DateEntry0");
                document.addEntry(dateEntry0);
                dateEntry0.setSection(section);
                
                DateEntry dateEntry1 = factory.createDateEntry("DateEntry1");
                document.addEntry(dateEntry1);
                dateEntry1.setSection(section);
                
                derivedEntry.addVariable(dateEntry0.getName(), dateEntry0);
                derivedEntry.addVariable(dateEntry1.getName(), dateEntry1);
                derivedEntry.setFormula(dateEntry0.getName() + "-"
                        + dateEntry1.getName());
                document.addEntry(derivedEntry);
                derivedEntry.setSection(section);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();
                
                Date dateEntry0Value = getDate(1990, 05, 15);
                String dateEntry0ValueText = getDateFormat().format(dateEntry0Value);
                
                Date dateEntry1Value = getDate(1999, 11, 02);
                String dateEntry1ValueText = getDateFormat().format(dateEntry1Value);
                
                long derivedEntryValueInMs = dateEntry0Value.getTime() - dateEntry1Value.getTime();
                BigDecimal derivedEntryValue = getDateAsNumberOfDays(derivedEntryValueInMs);
                setField(rendererHandler, dateEntry0, dateEntry0ValueText);
                setField(rendererHandler, dateEntry1, dateEntry1ValueText);
                EntryComponent field = getFieldAsEntryComponent(rendererHandler, derivedEntry,
                        0);
                BigDecimal actualDerivedEntryValue = new BigDecimal(field.getTextComponent().getText());
                assertTrue(derivedEntryValue.compareTo(actualDerivedEntryValue) == 0);
            }
        });
    }
    
    public void testDateEntrySubtractionWithNegativeResult() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();

                DateEntry dateEntry0 = factory.createDateEntry("DateEntry0");
                document.addEntry(dateEntry0);
                dateEntry0.setSection(section);
                
                DateEntry dateEntry1 = factory.createDateEntry("DateEntry1");
                document.addEntry(dateEntry1);
                dateEntry1.setSection(section);
                
                derivedEntry.addVariable(dateEntry0.getName(), dateEntry0);
                derivedEntry.addVariable(dateEntry1.getName(), dateEntry1);
                derivedEntry.setFormula(dateEntry0.getName() + "-"
                        + dateEntry1.getName());
                document.addEntry(derivedEntry);
                derivedEntry.setSection(section);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();
                String dateEntry0Value = "02-Jun-2005";
                String dateEntry1Value = "12-Jun-2005";
                String derivedEntryValue = "-10";
                setField(rendererHandler, dateEntry0, dateEntry0Value);
                setField(rendererHandler, dateEntry1, dateEntry1Value);
                EntryComponent field = getFieldAsEntryComponent(rendererHandler, derivedEntry,
                        0);
                assertEquals(derivedEntryValue, field.getTextComponent().getText());
            }
        });
    }
    
    public void testNumericSumWithStandardCode() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                
                document.addEntry(numericEntry0);
                numericEntry0.setSection(section);
                BasicResponse response0 = numericEntry0.generateInstance(sectionOcc);
                INumericValue value0 = numericEntry0.generateValue();
                List<StandardCode> standardCodes = 
                    getStandardCodesGetter(application.getModel()).getStandardCodes();
                StandardCode selectedStdCode = standardCodes.get(0);
                value0.setStandardCode(selectedStdCode);
                response0.setValue(value0);
                docInstance.addResponse(response0);
                
                document.addEntry(numericEntry1);
                numericEntry1.setSection(section);
                BasicResponse response1 = numericEntry1.generateInstance(sectionOcc);
                INumericValue value1 = numericEntry1.generateValue();
                double actualValue1 = 7;
                value1.setValue(Double.valueOf(actualValue1));
                response1.setValue(value1);
                docInstance.addResponse(response1);
                
                derivedEntry.addVariable(numericEntry0.getName(), numericEntry0);
                derivedEntry.addVariable(numericEntry1.getName(), numericEntry1);
                derivedEntry.setFormula(numericEntry0.getName() + "+" + numericEntry1.getName());
                document.addEntry(derivedEntry);
                derivedEntry.setSection(section);
                
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rendererHandler = application.getModel().getCurrentRendererHandler();
                
                EntryComponent field = getFieldAsEntryComponent(rendererHandler, derivedEntry,
                        0);
                StandardCode derivedEntryStdCode = getDerivedEntryStdCode(standardCodes);
                String derivedEntryStdCodeText = RendererHelper.getInstance().getStandardCodeText(derivedEntryStdCode);
                assertEquals(derivedEntryStdCodeText, field.getTextComponent().getText());
                PresModelRenderer<?> renderer = 
                    (PresModelRenderer<?>) rendererHandler.getExistingRenderer(derivedEntry, 0);
                TextPresModel presModel = (TextPresModel) renderer.getPresModel();
                String actualDerivedEntryStdCodeText = RendererHelper.getInstance().getStandardCodeText(
                        (StandardCode) presModel.getStandardCodeModel().getValue());
                assertEquals(derivedEntryStdCodeText, actualDerivedEntryStdCodeText);
            }
        });
    }
    
    private StandardCode getDerivedEntryStdCode(List<StandardCode> stdCodes) {
        for (StandardCode stdCode : stdCodes) {
            if (stdCode.isUsedForDerivedEntry()) {
                return stdCode;
            }
        }
        return null;
    }
    
    public void testDateSubtractionWithStandardCode() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();

                DateEntry dateEntry0 = factory.createDateEntry("DateEntry0");
                document.addEntry(dateEntry0);
                dateEntry0.setSection(section);

                BasicResponse response0 = dateEntry0
                        .generateInstance(sectionOcc);
                IDateValue value0 = dateEntry0.generateValue();
                List<StandardCode> standardCodes = getStandardCodesGetter(
                        application.getModel()).getStandardCodes();
                StandardCode selectedStdCode = standardCodes.get(0);
                value0.setStandardCode(selectedStdCode);
                response0.setValue(value0);
                docInstance.addResponse(response0);

                DateEntry dateEntry1 = factory.createDateEntry("DateEntry1");
                document.addEntry(dateEntry1);
                dateEntry1.setSection(section);

                BasicResponse response1 = dateEntry1
                        .generateInstance(sectionOcc);
                IDateValue value1 = dateEntry1.generateValue();
                Date actualValue1 = new Date();
                value1.setValue(actualValue1);
                response1.setValue(value1);
                docInstance.addResponse(response1);

                derivedEntry.addVariable(dateEntry0.getName(), dateEntry0);
                derivedEntry.addVariable(dateEntry1.getName(), dateEntry1);
                derivedEntry.setFormula(dateEntry0.getName() + "-"
                        + dateEntry1.getName());
                document.addEntry(derivedEntry);
                derivedEntry.setSection(section);

                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                EntryComponent field = getFieldAsEntryComponent(rendererHandler, derivedEntry,
                        0);
                StandardCode derivedEntryStdCode = getDerivedEntryStdCode(standardCodes);
                String derivedEntryStdCodeText = RendererHelper.getInstance().getStandardCodeText(derivedEntryStdCode);
                assertEquals(derivedEntryStdCodeText, field.getTextComponent().getText());
                PresModelRenderer<?> renderer = 
                    (PresModelRenderer<?>) rendererHandler.getExistingRenderer(derivedEntry, 0);
                TextPresModel presModel = (TextPresModel) renderer.getPresModel();
                String actualDerivedEntryStdCodeText = RendererHelper.getInstance().getStandardCodeText(
                        (StandardCode) presModel.getStandardCodeModel().getValue());
                assertEquals(derivedEntryStdCodeText, actualDerivedEntryStdCodeText);
            }
        });
    }
    
    public void testDateSubtractionWithPartialDate() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();

                DateEntry dateEntry0 = factory.createDateEntry("DateEntry0");
                document.addEntry(dateEntry0);
                dateEntry0.setSection(section);

                BasicResponse response0 = dateEntry0
                        .generateInstance(sectionOcc);
                IDateValue value0 = dateEntry0.generateValue();
                int month = 5;
                value0.setMonth(Integer.valueOf(month));
                int year = 1995;
                value0.setYear(Integer.valueOf(year));
                response0.setValue(value0);
                docInstance.addResponse(response0);

                DateEntry dateEntry1 = factory.createDateEntry("DateEntry1");
                document.addEntry(dateEntry1);
                dateEntry1.setSection(section);

                BasicResponse response1 = dateEntry1
                        .generateInstance(sectionOcc);
                IDateValue value1 = dateEntry1.generateValue();
                Date actualValue1 = getDate(1999, 05, 25);
                value1.setValue(actualValue1);
                response1.setValue(value1);
                docInstance.addResponse(response1);

                derivedEntry.addVariable(dateEntry0.getName(), dateEntry0);
                derivedEntry.addVariable(dateEntry1.getName(), dateEntry1);
                derivedEntry.setFormula(dateEntry0.getName() + "-"
                        + dateEntry1.getName());
                document.addEntry(derivedEntry);
                derivedEntry.setSection(section);

                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                Date dateEntry0Value = getDate(year, month, 15);
                
                long derivedEntryValueInMs = dateEntry0Value.getTime() - actualValue1.getTime();
                BigDecimal derivedEntryValue = getDateAsNumberOfDays(derivedEntryValueInMs);
                EntryComponent field = getFieldAsEntryComponent(rendererHandler, derivedEntry,
                        0);
                BigDecimal actualDerivedEntryValue = new BigDecimal(field.getTextComponent().getText());
                assertTrue(derivedEntryValue.compareTo(actualDerivedEntryValue) == 0);
            }
        });
    }
    
    public void testDateSubtractionWithJustMonth() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();

                DateEntry dateEntry0 = factory.createDateEntry("DateEntry0");
                document.addEntry(dateEntry0);
                dateEntry0.setSection(section);

                BasicResponse response0 = dateEntry0
                        .generateInstance(sectionOcc);
                IDateValue value0 = dateEntry0.generateValue();
                int month = 5;
                value0.setMonth(Integer.valueOf(month));
                response0.setValue(value0);
                docInstance.addResponse(response0);

                DateEntry dateEntry1 = factory.createDateEntry("DateEntry1");
                document.addEntry(dateEntry1);
                dateEntry1.setSection(section);

                BasicResponse response1 = dateEntry1
                        .generateInstance(sectionOcc);
                IDateValue value1 = dateEntry1.generateValue();
                Date actualValue1 = getDate(1999, 05, 25);
                value1.setValue(actualValue1);
                response1.setValue(value1);
                docInstance.addResponse(response1);

                derivedEntry.addVariable(dateEntry0.getName(), dateEntry0);
                derivedEntry.addVariable(dateEntry1.getName(), dateEntry1);
                derivedEntry.setFormula(dateEntry0.getName() + "-"
                        + dateEntry1.getName());
                document.addEntry(derivedEntry);
                derivedEntry.setSection(section);

                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                long derivedEntryValueInMs = 0 - actualValue1.getTime();
                BigDecimal derivedEntryValue = getDateAsNumberOfDays(derivedEntryValueInMs);
                EntryComponent field = getFieldAsEntryComponent(rendererHandler, derivedEntry,
                        0);
                BigDecimal actualDerivedEntryValue = new BigDecimal(field.getTextComponent().getText());
                assertTrue(derivedEntryValue.compareTo(actualDerivedEntryValue) == 0);
            }
        });
    }

    public void testDateSubtractionWithJustYear() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();

                DateEntry dateEntry0 = factory.createDateEntry("DateEntry0");
                document.addEntry(dateEntry0);
                dateEntry0.setSection(section);

                BasicResponse response0 = dateEntry0
                        .generateInstance(sectionOcc);
                IDateValue value0 = dateEntry0.generateValue();
                int year = 1995;
                value0.setYear(Integer.valueOf(year));
                response0.setValue(value0);
                docInstance.addResponse(response0);

                DateEntry dateEntry1 = factory.createDateEntry("DateEntry1");
                document.addEntry(dateEntry1);
                dateEntry1.setSection(section);

                BasicResponse response1 = dateEntry1
                        .generateInstance(sectionOcc);
                IDateValue value1 = dateEntry1.generateValue();
                Date actualValue1 = getDate(1999, 05, 25);
                value1.setValue(actualValue1);
                response1.setValue(value1);
                docInstance.addResponse(response1);

                derivedEntry.addVariable(dateEntry0.getName(), dateEntry0);
                derivedEntry.addVariable(dateEntry1.getName(), dateEntry1);
                derivedEntry.setFormula(dateEntry0.getName() + "-"
                        + dateEntry1.getName());
                document.addEntry(derivedEntry);
                derivedEntry.setSection(section);

                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rendererHandler = application.getModel()
                        .getCurrentRendererHandler();

                Date dateEntry0Value = getDate(year, 6, 1);
                
                long derivedEntryValueInMs = dateEntry0Value.getTime() - actualValue1.getTime();
                BigDecimal derivedEntryValue = getDateAsNumberOfDays(derivedEntryValueInMs);
                EntryComponent field = getFieldAsEntryComponent(rendererHandler, derivedEntry,
                        0);
                BigDecimal actualDerivedEntryValue = new BigDecimal(field.getTextComponent().getText());
                assertTrue(derivedEntryValue.compareTo(actualDerivedEntryValue) == 0);
            }
        });
    }
    
    
    public void testNumericEntrySum() throws Exception {
        final String[] text = new String[1];
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                document.addEntry(numericEntry0);
                numericEntry0.setSection(section);
                document.addEntry(numericEntry1);
                numericEntry1.setSection(section);
                derivedEntry.addVariable(numericEntry0.getName(), numericEntry0);
                derivedEntry.addVariable(numericEntry1.getName(), numericEntry1);
                derivedEntry.setFormula(numericEntry0.getName() + "+" + numericEntry1.getName());
                document.addEntry(derivedEntry);
                derivedEntry.setSection(section);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rendererHandler = application.getModel().getCurrentRendererHandler();
                setField(rendererHandler, numericEntry0, "78");
                setField(rendererHandler, numericEntry1, "45");
                EntryComponent field = getFieldAsEntryComponent(rendererHandler, derivedEntry, 0);
                text[0] = field.getTextComponent().getText();
            }
        });
        
        assertEquals("123", text[0]);
    }
    
    public void testOptionEntrySum() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                document.addEntry(optionEntry0);
                optionEntry0.setSection(section);
                document.addEntry(optionEntry1);
                optionEntry1.setSection(section);
                derivedEntry.addVariable(optionEntry0.getName(), optionEntry0);
                derivedEntry.addVariable(optionEntry1.getName(), optionEntry1);
                derivedEntry.setFormula(optionEntry0.getName() + "+" + optionEntry1.getName());
                document.addEntry(derivedEntry);
                derivedEntry.setSection(section);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rendererHandler = application.getModel().getCurrentRendererHandler();
                setOption(rendererHandler, optionEntry0, 0, optionEntry0Option);
                setOption(rendererHandler, optionEntry1, 0, optionEntry1Option);
                EntryComponent field = getFieldAsEntryComponent(rendererHandler, derivedEntry, 0);
                String text = field.getTextComponent().getText();
                int expected = OPTION_ENTRY0_OPTION1_VALUE + OPTION_ENTRY1_OPTION0_VALUE;
                assertEquals(String.valueOf(expected), text);
            }
        });
    }
    
    public void testOptionEntrySumAfterChangingFromStandardCode() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                document.addEntry(optionEntry0);
                optionEntry0.setSection(section);
                document.addEntry(optionEntry1);
                optionEntry1.setSection(section);
                derivedEntry.addVariable(optionEntry0.getName(), optionEntry0);
                derivedEntry.addVariable(optionEntry1.getName(), optionEntry1);
                derivedEntry.setFormula(optionEntry0.getName() + "+" + optionEntry1.getName());
                document.addEntry(derivedEntry);
                derivedEntry.setSection(section);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rendererHandler = application.getModel().getCurrentRendererHandler();
                StandardCode stdCode = getStandardCodes(application.getModel()).get(0);
                
                /* Set one of the options to a standard code */
                setOption(rendererHandler, optionEntry0, 0, stdCode);
                setOption(rendererHandler, optionEntry1, 0, optionEntry1Option);
                EntryComponent field = getFieldAsEntryComponent(rendererHandler, derivedEntry, 0);
                String derivedEntryText = field.getTextComponent().getText();
                assertEquals("999. Data unable to be captured", derivedEntryText);
                
                /* Change selected option to a valid number */
                setOption(rendererHandler, optionEntry0, 0, optionEntry0Option);
                derivedEntryText = field.getTextComponent().getText();
                int expected = OPTION_ENTRY0_OPTION1_VALUE + OPTION_ENTRY1_OPTION0_VALUE;
                assertEquals(String.valueOf(expected), derivedEntryText);
            }
        });
    }
    
    public void testEntriesInTwoSections() throws Exception {
        final String[] text = new String[1];
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                document.addEntry(numericEntry0);
                numericEntry0.setSection(section);                
                document.addEntry(numericEntry1);
                numericEntry1.setSection(section);
                Section secondSec = getFactory().createSection("Second section", "Second section");
                SectionOccurrence secondSecOcc = getFactory().createSectionOccurrence("Second section occ");
                secondSec.addOccurrence(secondSecOcc);
                document.addSection(secondSec);
                derivedEntry.addVariable(numericEntry0.getName(), numericEntry0);
                derivedEntry.addVariable(numericEntry1.getName(), numericEntry1);
                derivedEntry.setFormula(numericEntry0.getName() + "+" + numericEntry1.getName());
                document.addEntry(derivedEntry);
                derivedEntry.setSection(secondSec);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rHandler = application.getModel().getCurrentRendererHandler();
                setField(rHandler, numericEntry0, "78");
                setField(rHandler, numericEntry1, "45");
                application.getModel().nextSection();
                rHandler = application.getModel().getCurrentRendererHandler();
                
                EntryComponent field = getFieldAsEntryComponent(rHandler, derivedEntry, 0);
                text[0] = field.getTextComponent().getText();
            }
        });
        
        assertEquals("123", text[0]);
    }
    
    private void setField(RendererHandler rendererHandler, Entry entry,
            String text) {
        EntryComponent field = getFieldAsEntryComponent(rendererHandler, entry, 0);
        field.getTextComponent().setText(text);
    }
    
    public void testGetRendererSPI() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                RendererHandler rendererHandler = getRendererHandler(sectionOcc);
                RendererSPI rspi = rendererHandler.getRendererSPI(derivedEntry,
                        null);
                assertTrue(rspi instanceof DerivedRendererSPI);
            }
        });
    }
    
    public void testGetRenderer() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                document.addEntry(derivedEntry);
                derivedEntry.setSection(section);
                BuilderHandler builderHandler = getBuilderHandler(docInstance,
                        sectionOcc);
                RendererHandler rendererHandler = builderHandler
                        .getCurrentRendererHandler();
                BasicRenderer<?> renderer = (BasicRenderer<?>) rendererHandler
                        .getRenderer(derivedEntry, null);
                assertTrue(renderer.getField() != null);
                EntryLabel label = (EntryLabel) renderer.getLabel();
                assertEquals(derivedEntry.getDisplayText(), label.getText());
                assertTrue(renderer.getPresModel() != null);
                assertEquals(NUM_STANDARD_RENDERER_COMPONENTS + 1,
                        renderer.getComponents().size());
            }
        });
    }
    
    public void testAggregate() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                CompositeEntry scores = factory.createComposite("Scores table",
                        "Scores table");
                document.addEntry(scores);
                scores.setSection(section);
                
                NumericEntry posts =  factory.createNumericEntry("Number of posts",
                        "Number of posts");
                scores.addEntry(posts);
                posts.setSection(section);
                
                DerivedEntry scoresTotal = factory.createDerivedEntry("Scores " +
                        "total", "Scores total");
                document.addEntry(scoresTotal);
                scoresTotal.setSection(section);
                scoresTotal.setAggregateOperator("+");
                scoresTotal.setComposite(scores);
                scoresTotal.addVariable("a", posts);
                scoresTotal.setFormula("a");
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                
                RendererHandler rHandler = application.getModel()
                        .getCurrentRendererHandler();
                CompositeRenderer<?> compRenderer = (CompositeRenderer<?>) rHandler
                        .getExistingRenderer(scores, 0);
                VariableTable entryTable = (VariableTable) compRenderer.getComposite();

                List<Map<JComponent,Boolean>> comps = TableTestHelper.getComps(entryTable);

                assertEquals("Wrong number of rows found in table", 1, comps.size());
                Map<JComponent,Boolean> firstRow = comps.get(0);
                
                EntryComponent field = getFieldAsEntryComponent(rHandler, scoresTotal, 0);

                assertEquals("0", field.getTextComponent().getText());

                Iterator it = firstRow.keySet().iterator();
                TextEntryField row1Column1 = (TextEntryField) it.next();
                
                int postsRow1Value = 50;
                row1Column1.getTextComponent().setText(String.valueOf(postsRow1Value));
                assertEquals(String.valueOf(50), field.getTextComponent().getText());
               
                entryTable.getAddRowButton().doClick();
                
                assertEquals(String.valueOf(50), 
                        field.getTextComponent().getText());
                
                comps = TableTestHelper.getComps(entryTable);
                
                Map<JComponent,Boolean> secondRow = comps.get(1);
                it = secondRow.keySet().iterator();
                TextEntryField row2Column1 = (TextEntryField) it.next();
                
                int postsrow2Value = 75;
                row2Column1.getTextComponent().setText(String.valueOf(postsrow2Value));
                assertEquals(String.valueOf(75 + 50), field.getTextComponent().getText());
            }
        });
    }
    public void testAggregateWithTwoEntries() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                CompositeEntry scores = factory.createComposite("Scores table",
                        "Scores table");
                document.addEntry(scores);
                scores.setSection(section);
                
                NumericEntry posts =  factory.createNumericEntry("Number of posts",
                        "Number of posts");
                scores.addEntry(posts);
                posts.setSection(section);
                
                NumericEntry cost = factory.createNumericEntry("Annual cost",
                        "Total annual cost");
                scores.addEntry(cost);
                cost.setSection(section);
                
                DerivedEntry scoresTotal = factory.createDerivedEntry("Scores " +
                        "total", "Scores total");
                document.addEntry(scoresTotal);
                scoresTotal.setSection(section);
                scoresTotal.setAggregateOperator("+");
                scoresTotal.setComposite(scores);
                scoresTotal.addVariable("a", posts);
                scoresTotal.addVariable("b", cost);
                scoresTotal.setFormula("a * b");
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                
                RendererHandler rHandler = application.getModel()
                        .getCurrentRendererHandler();
                assertEquals(4, rHandler.getNumExistingRenderers());
                CompositeRenderer<?> compRenderer = 
                    (CompositeRenderer<?>) rHandler.getExistingRenderer(scores, 0);
                VariableTable entryTable = (VariableTable) compRenderer.getComposite();

                List<Map<JComponent,Boolean>> comps = TableTestHelper.getComps(entryTable);

                assertEquals("Wrong number of rows found in table", 1, comps.size());
                Map<JComponent,Boolean> firstRow = comps.get(0);
                
                EntryComponent field = getFieldAsEntryComponent(rHandler, scoresTotal, 0);

                assertEquals("0", field.getTextComponent().getText());
                
                Iterator it = firstRow.keySet().iterator();
                TextEntryField row1Column1 = (TextEntryField) it.next();
                TextEntryField row1Column2 = (TextEntryField) it.next();
                
                int postsRow1Value = 50;
                row1Column1.getTextComponent().setText(String.valueOf(postsRow1Value));
                assertEquals("0", field.getTextComponent().getText());
                
                int costRow1Value = 25;
                row1Column2.getTextComponent().setText(String.valueOf(costRow1Value));
                int row1Total = postsRow1Value * costRow1Value;
                assertEquals(String.valueOf(row1Total), 
                        field.getTextComponent().getText());
                
                entryTable.getAddRowButton().doClick();
                
                assertEquals(6, rHandler.getNumExistingRenderers());
                assertEquals(String.valueOf(row1Total), 
                        field.getTextComponent().getText());
                
                comps = TableTestHelper.getComps(entryTable);
                
                Map<JComponent,Boolean> secondRow = comps.get(1);
                it = secondRow.keySet().iterator();
                TextEntryField row2Column1 = (TextEntryField) it.next();
                TextEntryField row2Column2 = (TextEntryField) it.next();
                
                int postsrow2Value = 75;
                row2Column1.getTextComponent().setText(String.valueOf(postsrow2Value));
                assertEquals(String.valueOf(row1Total), field.getTextComponent().getText());
                
                int costRow2Value = 10;
                int row2Total = postsrow2Value * costRow2Value;
                row2Column2.getTextComponent().setText(String.valueOf(costRow2Value));
                assertEquals(String.valueOf(row1Total + row2Total), 
                        field.getTextComponent().getText());
            }
        });
    }
    
    public void testDerivedEntryWithUnits() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                document.addEntry(numericEntry0);
                numericEntry0.setSection(section);
                document.addEntry(numericEntry1);
                derivedEntry.addVariable(numericEntry0.getName(), numericEntry0);
                derivedEntry.setFormula(numericEntry0.getName());
                document.addEntry(derivedEntry);
                derivedEntry.setSection(section);
                derivedEntry.addUnit(factory.createUnit("mg"));
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rendererHandler = application.getModel().getCurrentRendererHandler();
                BasicTextEntryField field = (BasicTextEntryField) getFieldAsEntryComponent(rendererHandler, derivedEntry, 0);
                assertEquals(false, field.isEditable());
                assertEquals(false, field.getUnitsComboBox().isEditable());
                
                boolean hasUnitsComboBox = false;
                for (int i = 0, c = field.getComponentCount(); i < c; ++i) {
                    Component comp = field.getComponent(i);
                    if (comp.equals(field.getUnitsComboBox())) {
                        hasUnitsComboBox = true;
                    }
                }
                
                assertEquals(true, hasUnitsComboBox);
            }
        });
    }
    
    public void testDerivedEntryWithoutUnits() throws Exception {
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                init();
                document.addEntry(numericEntry0);
                numericEntry0.setSection(section);
                document.addEntry(numericEntry1);
                derivedEntry.addVariable(numericEntry0.getName(), numericEntry0);
                derivedEntry.setFormula(numericEntry0.getName());
                document.addEntry(derivedEntry);
                derivedEntry.setSection(section);
                application.setSelectedDocOccurrenceInstance(docInstance, 0);
                application.setVisible(true);
                RendererHandler rendererHandler = application.getModel().getCurrentRendererHandler();
                BasicTextEntryField field = (BasicTextEntryField) getFieldAsEntryComponent(rendererHandler, derivedEntry, 0);
                assertEquals(false, field.isEditable());
                boolean hasUnitsComboBox = false;
                for (int i = 0, c = field.getComponentCount(); i < c; ++i) {
                    Component comp = field.getComponent(i);
                    if (comp.equals(field.getUnitsComboBox())) {
                        hasUnitsComboBox = true;
                    }
                }
                
                assertEquals(false, hasUnitsComboBox);
            }
        });
    }
    
    public static void main(String[] args) throws Exception {
        initLauncher();
        new DerivedRendererTest().testDateEntrySubtraction2();
    }
}
