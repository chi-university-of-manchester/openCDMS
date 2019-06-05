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

package org.psygrid.outlook.patches.v1_0_2;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch4 extends AbstractPatch {

    public String getName() {
        return "CSSRI - change Yes=1,No=2 to No=0,Yes=1 in follow-up; add label for 'Hospital/Community Accomodation Details' qu 5";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {

        Document bl = ds.getDocument(22);
        if ( !"CSSRI".equals(bl.getName())){
            throw new RuntimeException("This is not the CSSRI document - it is "+bl.getName());
        }

        Entry cost1 = bl.getEntry(53);
        if ( !"Who contributes towards cost of accommodation".equals(cost1.getName())){
            throw new RuntimeException("This is not the Baseline 'Who contributes towards cost of accommodation' entry, it is "+cost1.getName());
        }
        cost1.setLabel("5");

        Document fu = ds.getDocument(27);
        if ( !"CSSRI".equals(fu.getName())){
            throw new RuntimeException("This is not the CSSRI document - it is "+fu.getName());
        }

        OptionEntry oe1 = (OptionEntry)fu.getEntry(0);
        if ( !"Living Situation changed".equals(oe1.getName()) ){
            throw new RuntimeException("This is not the Living Situation changed entry, it is "+oe1.getName());
        }
        oe1.moveOption(1, 0);
        Option no1 = oe1.getOption(0);
        if ( !"No".equals(no1.getDisplayText()) ){
            throw new RuntimeException("This is not the Living Situation changed No option, it is "+no1.getDisplayText());
        }
        no1.setCode(0);

        OptionEntry oe2 = (OptionEntry)fu.getEntry(12);
        if ( !"Employment Situation changed".equals(oe2.getName()) ){
            throw new RuntimeException("This is not the Employment Situation changed entry, it is "+oe2.getName());
        }
        oe2.moveOption(1, 0);
        Option no2 = oe2.getOption(0);
        if ( !"No".equals(no2.getDisplayText()) ){
            throw new RuntimeException("This is not the Employment Situation changed No option, it is "+no2.getDisplayText());
        }
        no2.setCode(0);

        Option yes2 = oe2.getOption(1);
        if ( !"Yes".equals(yes2.getDisplayText()) ){
            throw new RuntimeException("This is not the Employment Situation changed Yes option, it is "+yes2.getDisplayText());
        }

        //Remove option dependents, and set status of the dependent entries back to mandatory
        int odIndex = 10;
        OptionDependent od10 = yes2.getOptionDependent(odIndex);
        if ( !"Income changed".equals(od10.getDependentEntry().getName()) ){
            throw new RuntimeException("This is not the dependent for the 'Income changed' entry, it is for the "+od10.getDependentEntry().getName());
        }
        od10.getDependentEntry().setEntryStatus(EntryStatus.MANDATORY);
        yes2.removeOptionDependent(odIndex);

        odIndex = 9;
        OptionDependent od9 = yes2.getOptionDependent(odIndex);
        if ( !"Do you receive any state benefits?".equals(od9.getDependentEntry().getName()) ){
            throw new RuntimeException("This is not the dependent for the 'Do you receive any state benefits?' entry, it is for the "+od9.getDependentEntry().getName());
        }
        od9.getDependentEntry().setEntryStatus(EntryStatus.MANDATORY);
        yes2.removeOptionDependent(odIndex);

        odIndex = 8;
        OptionDependent od8 = yes2.getOptionDependent(odIndex);
        if ( !"Stopped benefits".equals(od8.getDependentEntry().getName()) ){
            throw new RuntimeException("This is not the dependent for the Stopped benefits entry, it is for the "+od8.getDependentEntry().getName());
        }
        od8.getDependentEntry().setEntryStatus(EntryStatus.MANDATORY);
        yes2.removeOptionDependent(odIndex);

        odIndex = 7;
        OptionDependent od7 = yes2.getOptionDependent(odIndex);
        if ( !"New benefits".equals(od7.getDependentEntry().getName()) ){
            throw new RuntimeException("This is not the dependent for the New benefits entry, it is for the "+od7.getDependentEntry().getName());
        }
        od7.getDependentEntry().setEntryStatus(EntryStatus.MANDATORY);
        yes2.removeOptionDependent(odIndex);

        odIndex = 6;
        OptionDependent od6 = yes2.getOptionDependent(odIndex);
        if ( !"Unemployed reason".equals(od6.getDependentEntry().getName()) ){
            throw new RuntimeException("This is not the dependent for the Unemployed reason entry, it is for the "+od6.getDependentEntry().getName());
        }
        od6.getDependentEntry().setEntryStatus(EntryStatus.MANDATORY);
        yes2.removeOptionDependent(odIndex);

        odIndex = 5;
        OptionDependent od5 = yes2.getOptionDependent(odIndex);
        if ( !"Weeks unemployed".equals(od5.getDependentEntry().getName()) ){
            throw new RuntimeException("This is not the dependent for the Weeks unemployed entry, it is for the "+od5.getDependentEntry().getName());
        }
        od5.getDependentEntry().setEntryStatus(EntryStatus.MANDATORY);
        yes2.removeOptionDependent(odIndex);

        odIndex = 4;
        OptionDependent od4 = yes2.getOptionDependent(odIndex);
        if ( !"Absent from work due to illness".equals(od4.getDependentEntry().getName()) ){
            throw new RuntimeException("This is not the dependent for the Absent from work due to illness entry, it is for the "+od4.getDependentEntry().getName());
        }
        od4.getDependentEntry().setEntryStatus(EntryStatus.MANDATORY);
        yes2.removeOptionDependent(odIndex);

        odIndex = 3;
        OptionDependent od3 = yes2.getOptionDependent(odIndex);
        if ( !"Occupation".equals(od3.getDependentEntry().getName()) ){
            throw new RuntimeException("This is not the dependent for the Occupation entry, it is for the "+od3.getDependentEntry().getName());
        }
        od3.getDependentEntry().setEntryStatus(EntryStatus.MANDATORY);
        yes2.removeOptionDependent(odIndex);

        odIndex = 2;
        OptionDependent od2 = yes2.getOptionDependent(odIndex);
        if ( !"Full or part time".equals(od2.getDependentEntry().getName()) ){
            throw new RuntimeException("This is not the dependent for the Full or part time entry, it is for the "+od2.getDependentEntry().getName());
        }
        od2.getDependentEntry().setEntryStatus(EntryStatus.MANDATORY);
        yes2.removeOptionDependent(odIndex);

        odIndex = 1;
        OptionDependent od1 = yes2.getOptionDependent(odIndex);
        if ( !"Employment status".equals(od1.getDependentEntry().getName()) || !(od1.getDependentEntry() instanceof OptionEntry) ){
            throw new RuntimeException("This is not the dependent for the employment status entry, it is for the "+od1.getDependentEntry().getName());
        }
        od1.getDependentEntry().setEntryStatus(EntryStatus.MANDATORY);
        yes2.removeOptionDependent(odIndex);


        OptionEntry oe3 = (OptionEntry)fu.getEntry(34);
        if ( !"Income changed".equals(oe3.getName()) ){
            throw new RuntimeException("This is not the Income changed entry, it is "+oe3.getName());
        }
        oe3.moveOption(1, 0);
        Option no3 = oe3.getOption(0);
        if ( !"No".equals(no3.getDisplayText()) ){
            throw new RuntimeException("This is not the Income changed No option, it is "+no3.getDisplayText());
        }
        no3.setCode(0);

        Entry cost2 = fu.getEntry(62);
        if ( !"Who contributes towards cost of accommodation".equals(cost2.getName())){
            throw new RuntimeException("This is not the Follow-Up 'Who contributes towards cost of accommodation' entry, it is "+cost2.getName());
        }
        cost2.setLabel("5");

    }

}
