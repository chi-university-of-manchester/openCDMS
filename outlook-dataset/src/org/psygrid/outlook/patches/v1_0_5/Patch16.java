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

package org.psygrid.outlook.patches.v1_0_5;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.psygrid.data.model.IDateValue;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.repository.client.RepositoryClient;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch16 extends AbstractPatch {

    @Override
    public String getName() {
        return "Correct medication tables in Personal Details Form";
    }

    @Override
    public void applyPatch(DataSet ds, String saml) throws Exception {

        Factory factory = new HibernateFactory();

        Document doc = ds.getDocument(5);
        if ( !"Personal Details".equals(doc.getName())){
            throw new RuntimeException("This is not the Personal Details form, it is "+doc.getName());
        }

        Section mainSec = doc.getSection(0);

        Unit mgUnit = ds.getUnit(23);
        if ( !"mg".equals(mgUnit.getAbbreviation())){
            throw new RuntimeException("This is not the mg unit, it is "+mgUnit.getAbbreviation());
        }

        //Changes to Previous Antipsychotic Medication table
        {
            CompositeEntry prevMedComp = (CompositeEntry)doc.getEntry(36);
            if ( !"Previous antipsychotic Medication".equals(prevMedComp.getName())){
                throw new RuntimeException("This is not the Previous antipsychotic Medication composite, it is "+prevMedComp.getName());
            }

            BasicEntry prevMedDose = prevMedComp.getEntry(1);
            if ( !"Dose".equals(prevMedDose.getName())){
                throw new RuntimeException("This is not the Previous Medication - Dose entry, it is "+prevMedDose.getName());
            }

            //remove mg/day and mg/week units, add mg unit
            prevMedDose.removeUnit(1);
            prevMedDose.removeUnit(0);
            prevMedDose.addUnit(mgUnit);

            //create frequency option entry
            OptionEntry freq = factory.createOptionEntry("Frequency", "Frequency");
            prevMedComp.insertEntry(freq, 2);
            freq.setSection(mainSec);
            freq.addOption(factory.createOption("3 times daily", "3 times daily", 1));
            freq.addOption(factory.createOption("2 times daily", "2 times daily", 2));
            freq.addOption(factory.createOption("Once daily", "Once daily", 3));
            freq.addOption(factory.createOption("Weekly", "Weekly", 4));
            freq.addOption(factory.createOption("Every 2 weeks", "Every 2 weeks", 5));
            freq.addOption(factory.createOption("Monthly", "Monthly", 6));

            DateEntry dateFirstDose = factory.createDateEntry("Date of first dose", "Date of first dose");
            prevMedComp.addEntry(dateFirstDose);
            dateFirstDose.setSection(mainSec);
        }

        //Changes to Current Medication table
        {
            CompositeEntry currMedComp = (CompositeEntry)doc.getEntry(38);
            if ( !"Current Medication".equals(currMedComp.getName())){
                throw new RuntimeException("This is not the Current Medication composite, it is "+currMedComp.getName());
            }

            BasicEntry dose = currMedComp.getEntry(1);
            if ( !"Dose".equals(dose.getName())){
                throw new RuntimeException("This is not the Current Medication - Dose entry, it is "+dose.getName());
            }

            //remove mg/day and mg/week units, add mg unit
            dose.removeUnit(1);
            dose.removeUnit(0);
            dose.addUnit(mgUnit);

            //create frequency option entry
            OptionEntry freq = factory.createOptionEntry("Frequency", "Frequency");
            currMedComp.insertEntry(freq, 2);
            freq.setSection(mainSec);
            freq.addOption(factory.createOption("3 times daily", "3 times daily", 1));
            freq.addOption(factory.createOption("2 times daily", "2 times daily", 2));
            freq.addOption(factory.createOption("Once daily", "Once daily", 3));
            freq.addOption(factory.createOption("Weekly", "Weekly", 4));
            freq.addOption(factory.createOption("Every 2 weeks", "Every 2 weeks", 5));
            freq.addOption(factory.createOption("Monthly", "Monthly", 6));
        }

    }

    @Override
    public Object preApplyPatch(DataSet ds, RepositoryClient client, String saml) throws Exception {

        System.out.println("Entering preApplyPatch...");

        //Before the Personal Details form can be changed in the dataset we need
        //to set the units of all the Dose values to null, so that these units can be removed

        //Data that will be returned and later used in postApplyPatch
        //Map of record id => composite entry id => row number => value => unit abbreviation
        Map<Long, Map<Long, Map<Long, Map<Long, String>>>> data = new HashMap<Long, Map<Long, Map<Long, Map<Long, String>>>>();

        Document doc = ds.getDocument(5);
        if ( !"Personal Details".equals(doc.getName())){
            throw new RuntimeException("This is not the Personal Details form, it is "+doc.getName());
        }

        CompositeEntry prevMedComp = (CompositeEntry)doc.getEntry(36);
        if ( !"Previous antipsychotic Medication".equals(prevMedComp.getName())){
            throw new RuntimeException("This is not the Previous antipsychotic Medication composite, it is "+prevMedComp.getName());
        }

        BasicEntry prevMedDose = prevMedComp.getEntry(1);
        if ( !"Dose".equals(prevMedDose.getName())){
            throw new RuntimeException("This is not the Previous Medication - Dose entry, it is "+prevMedDose.getName());
        }

        CompositeEntry currMedComp = (CompositeEntry)doc.getEntry(38);
        if ( !"Current Medication".equals(currMedComp.getName())){
            throw new RuntimeException("This is not the Current Medication composite, it is "+currMedComp.getName());
        }

        BasicEntry currMedDose = currMedComp.getEntry(1);
        if ( !"Dose".equals(currMedDose.getName())){
            throw new RuntimeException("This is not the Current Medication - Dose entry, it is "+currMedDose.getName());
        }

        DocumentOccurrence docOcc = doc.getOccurrence(0);
        SectionOccurrence secOcc = doc.getSection(0).getOccurrence(0);

        List<Record> records = client.getRecords(ds.getId(), saml);
        for ( Record record: records ){
            System.out.println("Processing record "+record.getIdentifier().getIdentifier());
            record.attach(ds);
            DocumentInstance docInst = record.getDocumentInstance(docOcc);
            if ( null != docInst ){

                Map<Long, Map<Long, Map<Long, String>>> recordMap = data.get(record.getId());
                if ( null == recordMap ){
                    recordMap = new HashMap<Long, Map<Long, Map<Long, String>>>();
                    data.put(record.getId(), recordMap);
                }

                System.out.println("Retrieving data for record "+record.getIdentifier().getIdentifier());
                Record r = client.getRecordSingleDocument(record.getId(), docInst.getId(), ds, saml);
                DocumentInstance di = r.getDocumentInstance(docOcc);

                //nullify units for Previous Antipsychotic Medication - Dose

                {
                    Map<Long, Map<Long, String>> prevMedMap = recordMap.get(prevMedComp.getId());
                    if ( null == prevMedMap ){
                        prevMedMap = new HashMap<Long, Map<Long, String>>();
                        recordMap.put(prevMedComp.getId(), prevMedMap);
                    }

                    CompositeResponse prevMedResp = (CompositeResponse)di.getResponse(prevMedComp, secOcc);
                    for ( int i=0; i<prevMedResp.numCompositeRows(); i++ ){
                        CompositeRow row = prevMedResp.getCompositeRow(i);
                        Map<Long, String> rowMap = prevMedMap.get(row.getId());
                        if ( null == rowMap ){
                            rowMap = new HashMap<Long, String>();
                            prevMedMap.put(row.getId(), rowMap);
                        }

                        BasicResponse doseResp = row.getResponse(prevMedDose);
                        {
                            Unit u = doseResp.getValue().getUnit();
                            if ( null == u ){
                                rowMap.put(doseResp.getValue().getId(), null);
                            }
                            else{
                                rowMap.put(doseResp.getValue().getId(), u.getAbbreviation());
                            }
                            doseResp.getValue().setUnit(null);
                        }

                        //also have to do all the "old" values
                        for ( Value v: ((BasicResponse)doseResp).getOldValues() ){
                            Unit u = v.getUnit();
                            if ( null == u ){
                                rowMap.put(v.getId(), null);
                            }
                            else{
                                rowMap.put(v.getId(), u.getAbbreviation());
                            }
                            v.setUnit(null);
                        }
                    }

                    //deleted rows...
                    {
                        CompositeResponse cr = (CompositeResponse)prevMedResp;
                        for ( CompositeRow row: cr.getDeletedRows() ){

                            Map<Long, String> rowMap = prevMedMap.get(row.getId());
                            if ( null == rowMap ){
                                rowMap = new HashMap<Long, String>();
                                prevMedMap.put(row.getId(), rowMap);
                            }

                            BasicResponse doseResp = row.getResponse(prevMedDose);
                            {
                                Unit u = doseResp.getValue().getUnit();
                                if ( null == u ){
                                    rowMap.put(doseResp.getValue().getId(), null);
                                }
                                else{
                                    rowMap.put(doseResp.getValue().getId(), u.getAbbreviation());
                                }
                                doseResp.getValue().setUnit(null);
                            }

                            //also have to do all the "old" values of deleted rows!
                            for ( Value v: ((BasicResponse)doseResp).getOldValues() ){
                                Unit u = v.getUnit();
                                if ( null == u ){
                                    rowMap.put(v.getId(), null);
                                }
                                else{
                                    rowMap.put(v.getId(), u.getAbbreviation());
                                }
                                v.setUnit(null);
                            }
                        }

                    }
                }

                //-------------------------------------------------------
                //nullify units for Current Medication - Dose
                //-------------------------------------------------------

                {
                    Map<Long, Map<Long, String>> currMedMap = recordMap.get(currMedComp.getId());
                    if ( null == currMedMap ){
                        currMedMap = new HashMap<Long, Map<Long, String>>();
                        recordMap.put(currMedComp.getId(), currMedMap);
                    }

                    CompositeResponse currMedResp = (CompositeResponse)di.getResponse(currMedComp, secOcc);
                    for ( int i=0; i<currMedResp.numCompositeRows(); i++ ){
                        CompositeRow row = currMedResp.getCompositeRow(i);

                        Map<Long, String> rowMap = currMedMap.get(row.getId());
                        if ( null == rowMap ){
                            rowMap = new HashMap<Long, String>();
                            currMedMap.put(row.getId(), rowMap);
                        }

                        BasicResponse doseResp = row.getResponse(currMedDose);
                        {
                            Unit u = doseResp.getValue().getUnit();
                            if ( null == u ){
                                rowMap.put(doseResp.getValue().getId(), null);
                            }
                            else{
                                rowMap.put(doseResp.getValue().getId(), u.getAbbreviation());
                            }
                            doseResp.getValue().setUnit(null);
                        }

                        //also have to do all the "old" values
                        for ( Value v: ((BasicResponse)doseResp).getOldValues() ){
                            Unit u = v.getUnit();
                            if ( null == u ){
                                rowMap.put(v.getId(), null);
                            }
                            else{
                                rowMap.put(v.getId(), u.getAbbreviation());
                            }
                            v.setUnit(null);
                        }

                    }

                    //deleted rows...
                    {
                        CompositeResponse cr = (CompositeResponse)currMedResp;
                        for ( CompositeRow row: cr.getDeletedRows() ){

                            Map<Long, String> rowMap = currMedMap.get(row.getId());
                            if ( null == rowMap ){
                                rowMap = new HashMap<Long, String>();
                                currMedMap.put(row.getId(), rowMap);
                            }

                            BasicResponse doseResp = row.getResponse(currMedDose);
                            {
                                Unit u = doseResp.getValue().getUnit();
                                if ( null == u ){
                                    rowMap.put(doseResp.getValue().getId(), null);
                                }
                                else{
                                    rowMap.put(doseResp.getValue().getId(), u.getAbbreviation());
                                }
                                doseResp.getValue().setUnit(null);
                            }

                            //also have to do all the "old" values
                            for ( Value v: ((BasicResponse)doseResp).getOldValues() ){
                                Unit u = v.getUnit();
                                if ( null == u ){
                                    rowMap.put(v.getId(), null);
                                }
                                else{
                                    rowMap.put(v.getId(), u.getAbbreviation());
                                }
                                v.setUnit(null);
                            }
                        }

                    }
                }

                System.out.println("Saving record "+record.getIdentifier().getIdentifier());
                client.saveRecord(r, true, saml);
            }
        }

        System.out.println("Exiting preApplyPatch.");
        return data;
    }

    @Override
    public void postApplyPatch(DataSet ds, Object obj, RepositoryClient client, String saml) throws Exception {

        System.out.println("Entering postApplyPatch...");

        Map<Long, Map<Long, Map<Long, Map<Long, String>>>> data = (Map<Long, Map<Long, Map<Long, Map<Long, String>>>>)obj;

        Document doc = ds.getDocument(5);
        if ( !"Personal Details".equals(doc.getName())){
            throw new RuntimeException("This is not the Personal Details form, it is "+doc.getName());
        }

        CompositeEntry prevMedComp = (CompositeEntry)doc.getEntry(36);
        if ( !"Previous antipsychotic Medication".equals(prevMedComp.getName())){
            throw new RuntimeException("This is not the Previous antipsychotic Medication composite, it is "+prevMedComp.getName());
        }

        BasicEntry prevMedDose = prevMedComp.getEntry(1);
        if ( !"Dose".equals(prevMedDose.getName())){
            throw new RuntimeException("This is not the Previous Medication - Dose entry, it is "+prevMedDose.getName());
        }

        OptionEntry prevMedFreq = (OptionEntry)prevMedComp.getEntry(2);
        if ( !"Frequency".equals(prevMedFreq.getName())){
            throw new RuntimeException("This is not the Previous Medication - Frequency entry, it is "+prevMedFreq.getName());
        }

        Option prevMedFreqDaily = prevMedFreq.getOption(2);
        if ( !"Once daily".equals(prevMedFreqDaily.getName())){
            throw new RuntimeException("This is not the Previous Medication - Frequency - Once daily option, it is "+prevMedFreqDaily.getName());
        }

        Option prevMedFreqWeekly = prevMedFreq.getOption(3);
        if ( !"Weekly".equals(prevMedFreqWeekly.getName())){
            throw new RuntimeException("This is not the Previous Medication - Frequency - Weekly option, it is "+prevMedFreqWeekly.getName());
        }

        DateEntry prevMedDate = (DateEntry)prevMedComp.getEntry(4);
        if ( !"Date of first dose".equals(prevMedDate.getName())){
            throw new RuntimeException("This is not the Previous Medication - Date of first dose entry, it is "+prevMedDate.getName());
        }

        CompositeEntry currMedComp = (CompositeEntry)doc.getEntry(38);
        if ( !"Current Medication".equals(currMedComp.getName())){
            throw new RuntimeException("This is not the Current Medication composite, it is "+currMedComp.getName());
        }

        BasicEntry currMedDose = currMedComp.getEntry(1);
        if ( !"Dose".equals(currMedDose.getName())){
            throw new RuntimeException("This is not the Current Medication - Dose entry, it is "+currMedDose.getName());
        }

        OptionEntry currMedFreq = (OptionEntry)currMedComp.getEntry(2);
        if ( !"Frequency".equals(currMedFreq.getName())){
            throw new RuntimeException("This is not the Current Medication - Frequency entry, it is "+currMedFreq.getName());
        }

        Option currMedFreqDaily = currMedFreq.getOption(2);
        if ( !"Once daily".equals(currMedFreqDaily.getName())){
            throw new RuntimeException("This is not the Current Medication - Frequency - Once daily option, it is "+currMedFreqDaily.getName());
        }

        Option currMedFreqWeekly = currMedFreq.getOption(3);
        if ( !"Weekly".equals(currMedFreqWeekly.getName())){
            throw new RuntimeException("This is not the Current Medication - Frequency - Weekly option, it is "+currMedFreqWeekly.getName());
        }

        DocumentOccurrence docOcc = doc.getOccurrence(0);
        SectionOccurrence secOcc = doc.getSection(0).getOccurrence(0);

        //get standard codes
        List<StandardCode> stdCodes = client.getStandardCodes(saml);

        List<Record> records = client.getRecords(ds.getId(), saml);
        for ( Record record: records ){
            System.out.println("Processing record "+record.getIdentifier().getIdentifier());
            record.attach(ds);
            DocumentInstance docInst = record.getDocumentInstance(docOcc);
            if ( null != docInst ){

                Map<Long, Map<Long, Map<Long, String>>> recordMap = data.get(record.getId());
                if ( null == recordMap ){
                    throw new RuntimeException("Missing data for record id="+record.getId());
                }

                System.out.println("Retrieving data for record "+record.getIdentifier().getIdentifier());
                Record r = client.getRecordSingleDocument(record.getId(), docInst.getId(), ds, saml);
                DocumentInstance di = r.getDocumentInstance(docOcc);

                //Set new unit for Previous Antipsychotic Medication - Dose and set
                //value for Previous Antipsychotic Medication - Frequency

                Map<Long, Map<Long, String>> prevMedMap = recordMap.get(prevMedComp.getId());
                if ( null == prevMedMap ){
                    throw new RuntimeException("Missing data for response id = "+prevMedComp.getId()+" of record "+record.getId());
                }

                CompositeResponse prevMedResp = (CompositeResponse)di.getResponse(prevMedComp, secOcc);
                for ( int i=0; i<prevMedResp.numCompositeRows(); i++ ){
                    CompositeRow row = prevMedResp.getCompositeRow(i);

                    Map<Long, String> rowMap = prevMedMap.get(row.getId());
                    if ( null == rowMap ){
                        throw new RuntimeException("Missing data for row = "+i+" of response "+prevMedComp.getId());
                    }

                    BasicResponse doseResp = row.getResponse(prevMedDose);

                    {
                        //set unit to mg
                        doseResp.getValue().setUnit(prevMedDose.getUnit(0));
                        //add response for frequency
                        String oldUnit = rowMap.get(doseResp.getValue().getId());
                        BasicResponse freq = prevMedFreq.generateInstance(secOcc);
                        row.addResponse(freq);
                        IOptionValue val = (IOptionValue)prevMedFreq.generateValue();
                        freq.setValue(val);
                        if ( null == oldUnit || "mg/day".equals(oldUnit) ){
                            //unit was mg/day (null is included to cover bug 610)
                            //so set the frequency to daily
                            val.setValue(prevMedFreqDaily);
                        }
                        else if ("mg/week".equals(oldUnit) ){
                            //unit was mg/week so set the frequency to weekly
                            val.setValue(prevMedFreqWeekly);
                        }
                        //add response for date
                        BasicResponse date = prevMedDate.generateInstance(secOcc);
                        row.addResponse(date);
                        IDateValue dateVal = prevMedDate.generateValue();
                        date.setValue(dateVal);
                        dateVal.setStandardCode(stdCodes.get(3));
                    }

                    //do "old" dose values
                    for ( Value v: ((BasicResponse)doseResp).getOldValues() ){
                        v.setUnit(prevMedDose.getUnit(0));
                        //not going to add the frequency for old values, getting too complicated!
                    }
                }

                //Set new unit for Current Medication - Dose and set
                //value for Current Medication - Frequency

                Map<Long, Map<Long, String>> currMedMap = recordMap.get(currMedComp.getId());
                if ( null == currMedMap ){
                    throw new RuntimeException("Missing data for response id = "+currMedComp.getId()+" of record "+record.getId());
                }

                CompositeResponse currMedResp = (CompositeResponse)di.getResponse(currMedComp, secOcc);
                for ( int i=0; i<currMedResp.numCompositeRows(); i++ ){
                    CompositeRow row = currMedResp.getCompositeRow(i);

                    Map<Long, String> rowMap = currMedMap.get(row.getId());
                    if ( null == rowMap ){
                        throw new RuntimeException("Missing data for row = "+i+" of response "+currMedComp.getId());
                    }

                    BasicResponse doseResp = row.getResponse(currMedDose);

                    {
                        //set unit to mg
                        doseResp.getValue().setUnit(currMedDose.getUnit(0));
                        //add response for frequency
                        String oldUnit = rowMap.get(doseResp.getValue().getId());
                        BasicResponse freq = currMedFreq.generateInstance(secOcc);
                        row.addResponse(freq);
                        IOptionValue val = (IOptionValue)currMedFreq.generateValue();
                        freq.setValue(val);
                        if ( null == oldUnit || "mg/day".equals(oldUnit) ){
                            //unit was mg/day (null is included to cover bug 610)
                            //so set the frequency to daily
                            val.setValue(currMedFreqDaily);
                        }
                        else if ("mg/week".equals(oldUnit) ){
                            //unit was mg/week so set the frequency to weekly
                            val.setValue(currMedFreqWeekly);
                        }
                    }

                    //do "old" dose values
                    for ( Value v: ((BasicResponse)doseResp).getOldValues() ){
                        v.setUnit(currMedDose.getUnit(0));
                        //not going to add the frequency for old values, getting too complicated!
                    }
                }

                System.out.println("Saving record "+record.getIdentifier().getIdentifier());
                client.saveRecord(r, true, saml);

            }
        }

        System.out.println("Exiting preApplyPatch.");
    }

    @Override
    public boolean isolated() {
        return true;
    }

}
