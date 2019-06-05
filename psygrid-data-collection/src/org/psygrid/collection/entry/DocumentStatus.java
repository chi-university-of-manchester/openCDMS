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


package org.psygrid.collection.entry;

import java.util.EnumSet;
import java.util.Set;

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Status;

/**
 * Represents the status of a IDocumentInstance. Also provides methods to convert
 * to and from a IStatus. A IDocumentInstance uses an IStatus to represent its status
 * internally and the allowed values are defined in the IDocument from which it
 * was created. However, it has been defined that any given IDocument will always
 * contain IStatus objects with the following short names: "Incomplete", "Pending",
 * "Rejected" and "Approved". This class makes this restriction explicit and 
 * provides the benefits that come from being an <code>enum</code>.
 * 
 * (Jun 2007) Added Document States 'Locally Incomplete' and 'Ready to Submit' to 
 * represent statuses of IDocumentInstances held locally, before submission to the
 * repository. This is required to make an explicit the difference between incomplete
 * documents held by the repository and local documents - all of which have a status
 * of incomplete before repository submission. These new statues are different in that
 * they are held soley by the Record/DocumentStatusMap and not by the IDocumentInstance. 
 * 
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 * @see IStatus
 * @see IDocument
 */
public enum DocumentStatus {
    NOT_STARTED,
	INCOMPLETE,
	PENDING,
	REJECTED,
	APPROVED,
	LOCALLY_INCOMPLETE,
	READY_TO_SUBMIT,
	VIEW_ONLY,
	DATASET_DESIGNER,
	COMPLETE,
	CONTROLLED,
	COMMIT_FAILED;

	/**
	 * Returns a set containing the statuses for remote documents. In other
	 * words, all statuses apart from the ones returned by {@link #getInternal()}
	 * and {@link #getLocal()}.
	 */
	public static Set<DocumentStatus> getRemote() {
	    EnumSet<DocumentStatus> toExclude = getLocal();
	    toExclude.addAll(getInternal());
	    return EnumSet.complementOf(toExclude);
	}
	
	/**
	 * Returns a set containing the statuses for local documents, documents
	 * that have not yet been submitted to the repository. 
	 */
	public static EnumSet<DocumentStatus> getLocal() {
	    return EnumSet.of(NOT_STARTED, LOCALLY_INCOMPLETE, READY_TO_SUBMIT);
	}
	
	/**
	 * Returns a set containing the statuses that are not user visible.
	 */
	public static EnumSet<DocumentStatus> getInternal(){
	    return EnumSet.of(DATASET_DESIGNER, VIEW_ONLY);
	}
	
	/**
     * Returns a set containg the user visible statuses. In other words, all
     * statuses apart from the ones returned from {@link #getInternal()}.
     */
	public static EnumSet<DocumentStatus> getUserVisible(boolean isNoReviewAndApprove, boolean isAlwaysOnline) {
		if ( isNoReviewAndApprove ){
			if ( isAlwaysOnline ){
				return EnumSet.of(NOT_STARTED, INCOMPLETE, COMPLETE, CONTROLLED);
			}
			else{
				return EnumSet.of(NOT_STARTED, INCOMPLETE, LOCALLY_INCOMPLETE, READY_TO_SUBMIT, COMPLETE, CONTROLLED);				
			}
		}
		else{
			if ( isAlwaysOnline ){
				return EnumSet.of(NOT_STARTED, INCOMPLETE, COMPLETE, PENDING, REJECTED, APPROVED);
			}
			else{
				return EnumSet.of(NOT_STARTED, INCOMPLETE, LOCALLY_INCOMPLETE, READY_TO_SUBMIT, COMPLETE, PENDING, REJECTED, APPROVED);				
			}
		}
	}
	
	/**
	 * Returns the <code>DocumentStatus</code> equivalent to <code>status</code>.
	 * 
	 * @param status equivalent to the DocumentStatus that is required.
	 * @return the <code>DocumentStatus</code> equivalent to <code>status</code>.
	 * 
	 * @throws IllegalArgumentException if the short name of IStatus does not
	 * match any of allowed values for a document status. These are mentioned
	 * in the class description.
	 */
	public static DocumentStatus valueOf(Status status) {
		String name = null;
		if (status != null) {
			name = status.getShortName();
			for (DocumentStatus docStatus : DocumentStatus.values())    {
				if (docStatus.toString().equals(name)) {
					return docStatus;
				}
			}
		}
		throw new IllegalArgumentException("There is no DocumentStatus that " + //$NON-NLS-1$
				"matches with: " + name); //$NON-NLS-1$
	}

	/**
	 * Returns the equivalent IStatus in <code>document</code> 
	 * for <code>docStatus</code>.
	 * 
	 * @param document IDocument to look for an equivalent IStatus.
	 * @param docStatus The DocumentStatus whose equivalent IStatus is required.
	 * @return the equivalent IStatus in <code>document</code> for 
	 * <code>docStatus</code>.
	 * 
	 * @throws IllegalArgumentException if <code>document</code> has no IStatus
	 * equivalent to <code>docStatus</code>.
	 */
	public static Status toIStatus(Document document, DocumentStatus docStatus) {
		for (int i = 0, c = document.numStatus(); i < c; ++i) {
			Status status = document.getStatus(i);
			if (docStatus.toString().equals(status.getShortName())) {
				return status;
			}
		}
		throw new IllegalArgumentException("There is no IStatus that matches with: " + //$NON-NLS-1$
				docStatus);
	}
	
	public static DocumentStatus fromStatusLongName(String statusLongName) {
	    for (DocumentStatus status : EnumSet.allOf(DocumentStatus.class)) {
	        if (status.toStatusLongName().equals(statusLongName))
	            return status;
	    }
	    return null;
	}

	/**
	 * Returns the same value as calling {@link IStatus#getLongName()} in
	 * the equivalent IStatus.
	 * 
	 * @return longName
	 */
	public String toStatusLongName() {
		switch (this) {
		case NOT_STARTED:
		    return "Not Started";
		case INCOMPLETE:
			return "Incomplete";
		case PENDING:
			return "Pending Approval";
		case REJECTED:
			return "Rejected";
		case APPROVED:
			return "Approved";
		case LOCALLY_INCOMPLETE:
			return "Locally Incomplete";
		case READY_TO_SUBMIT:
			return "Ready to Submit";
		case DATASET_DESIGNER:
			return "Dataset Designer";
		case VIEW_ONLY:
			return "View Only";
		case COMPLETE:
			return "Complete";
		case CONTROLLED:
			return "Controlled";
		case COMMIT_FAILED:
			return "Commit Failed";
		}
		//Should never happen
		return null;
	}

	/**
	 * Returns the same value as calling {@link IStatus#getShortName()} in
	 * the equivalent IStatus.
	 */
	@Override
	public String toString() {
		switch (this) {
		case NOT_STARTED:
		    return "Not Started"; //$NON-NLS-1$
		case INCOMPLETE:
			return "Incomplete"; //$NON-NLS-1$
		case PENDING:
			return "Pending"; //$NON-NLS-1$
		case REJECTED:
			return "Rejected"; //$NON-NLS-1$
		case APPROVED:
			return "Approved"; //$NON-NLS-1$
		case LOCALLY_INCOMPLETE:
			return "Locally Incomplete"; //$NON-NLS-1$
		case READY_TO_SUBMIT:
			return "Ready to Submit"; //$NON-NLS-1$
		case DATASET_DESIGNER:
			return "Dataset Designer"; //$NON-NLS-1$
		case VIEW_ONLY:
			return "View Only"; //$NON-NLS-1$
		case COMPLETE:
			return "Complete"; //$NON-NLS-1$
		case CONTROLLED:
			return "Controlled"; //$NON-NLS-1$
		case COMMIT_FAILED:
			return "Commit Failed";
		}
		//Should never happen
		return null;
	}
}