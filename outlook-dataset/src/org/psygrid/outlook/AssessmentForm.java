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

package org.psygrid.outlook;

import org.psygrid.data.model.hibernate.*;

public abstract class AssessmentForm {

    protected static void createOptionDependent(
            Factory factory,
            Option option,
            Entry dependentEntry) {

        createOptionDependent(factory, option,
                dependentEntry, EntryStatus.MANDATORY);
    }

    protected static void createOptionDependent(
            Factory factory,
            Option option,
            Entry dependentEntry,
            EntryStatus status) {

        OptionDependent optDep = factory.createOptionDependent();
        optDep.setEntryStatus(status);
        option.addOptionDependent(optDep);
        optDep.setDependentEntry(dependentEntry);
    }

    protected static void createOptions(
    		Factory factory,
    		OptionEntry optionEntry,
    		String[] optionTexts) {
    	createOptions(factory, optionEntry, optionTexts, null, null);
    }

    protected static void createOptions(
    		Factory factory,
    		OptionEntry optionEntry,
    		String[] optionTexts,
    		String[] optionDescs) {
    	createOptions(factory, optionEntry, optionTexts, null, optionDescs);
    }

	protected static void createOptions(
			Factory factory,
			OptionEntry optionEntry,
			String[] optionTexts,
			int[] optionCodes) {
		createOptions(factory, optionEntry, optionTexts, optionCodes, null);
	}

	protected static void createOptions(
			Factory factory,
			OptionEntry optionEntry,
			String[] optionTexts,
			int[] optionCodes,
			String[] optionDescs) {

    	for ( int i=0, c=optionTexts.length; i<c; i++ ){
    		Option o = factory.createOption(optionTexts[i], optionTexts[i]);
    		if ( null != optionCodes ){
    			o.setCode(optionCodes[i]);
    		}
    		if ( null != optionDescs ){
    			o.setDescription(optionDescs[i]);
    		}
    		optionEntry.addOption(o);
    	}
    }

    public static void createDocumentStatuses(
            Factory factory,
            Document document){

    	createReviewAndApproveStatuses(factory, document);

    }

    public static void createReviewAndApproveStatuses(Factory factory, Document document){
        Status incomplete = factory.createStatus(Status.DOC_STATUS_INCOMPLETE, "Incomplete", 0);
        Status pending = factory.createStatus(Status.DOC_STATUS_PENDING, "Pending Approval", 1);
        Status rejected = factory.createStatus(Status.DOC_STATUS_REJECTED, "Rejected", 2);
        Status approved = factory.createStatus(Status.DOC_STATUS_APPROVED, "Approved", 3);
        Status complete = factory.createStatus(Status.DOC_STATUS_COMPLETE, "Complete", 4);

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

    public static void createNoReviewAndApproveStatuses(Factory factory, Document document){
        Status incomplete = factory.createStatus(Status.DOC_STATUS_INCOMPLETE, "Incomplete", 0);
        Status complete = factory.createStatus(Status.DOC_STATUS_COMPLETE, "Complete", 1);
        Status controlled = factory.createStatus(Status.DOC_STATUS_CONTROLLED, "Controlled", 2);

        incomplete.addStatusTransition(complete);
        complete.addStatusTransition(incomplete);
        complete.addStatusTransition(controlled);

        document.addStatus(incomplete);
        document.addStatus(complete);
        document.addStatus(controlled);
    }

}
