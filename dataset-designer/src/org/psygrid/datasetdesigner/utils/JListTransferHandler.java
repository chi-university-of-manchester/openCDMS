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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;


import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.datasetdesigner.model.DSDocumentOccurrence;
import org.psygrid.datasetdesigner.model.DSOption;
import org.psygrid.datasetdesigner.model.GroupModel;
import org.psygrid.datasetdesigner.model.TreatmentHolderModel;
import org.psygrid.randomization.model.hibernate.Stratum;
import org.psygrid.www.xml.security.core.types.RoleType;
 


/**
 * A wrapper for TransferHandler to describe
 * how data should be transferred from a JList
 * <p>
 * For objects, find a sensible string value to
 * use in copying data from list (e.g. for an IOption,
 * transfer the name if it's not empty, otherwise transfer
 * the display text). 
 * 
 * @author pwhelan
 */
public class JListTransferHandler extends TransferHandler{
	
    /**
     * Bundle up the selected items in a single list for export.
     * Each line is separated by a newline.
     * @param the component to get the transfer data from
     * @return the data to transfer
     */
    protected Transferable createTransferable(JComponent c) {
        JList list = (JList)c;
        Object[] values = list.getSelectedValues();
        
        StringBuffer buff = new StringBuffer();
        
        for (int i = 0; i < values.length; i++) {
	        Object value = values[i];
	        
			if (value instanceof Option) {
				if(((Option)value).getName() != null){
					value = ((Option)value).getName();
				}else{
					value = ((Option)value).getDisplayText();
				}
			}
			
			if (value instanceof OptionEntry) {
				if(((OptionEntry)value).getName() != null){
					value = (((OptionEntry)value).getName());
				}
				else{
					value = ((OptionEntry)value).getDescription();
				}
			}
			
			if (value instanceof DSOption) {
				value = ((DSOption)value).getEntryName() + " - " + ((DSOption)value).getOption().getName();
			}
			
			if (value instanceof DSOption) {
				value = ((DSOption)value).getEntryName() + " - " + ((DSOption)value).getOption().getName();
			}
			
			if (value instanceof NumericEntry) {
				if(((NumericEntry)value).getName() != null){
					value = (((NumericEntry)value).getName());
				}
				else{
					value = ((NumericEntry)value).getDescription();
				}
			}
			
			if (value instanceof DateEntry) {
				if(((DateEntry)value).getName() != null){
					value = (((DateEntry)value).getName());
				}
				else{
					value = ((DateEntry)value).getDescription();
				}
			}
			
			if (value instanceof TextEntry) {
				if(((TextEntry)value).getName() != null){
					value = (((TextEntry)value).getName());
				}
				else{
					value = ((TextEntry)value).getDescription();
				}
			}
	
			if (value instanceof IntegerEntry) {
				if(((IntegerEntry)value).getName() != null){
					value = (((IntegerEntry)value).getName());
				}
				else{
					value = ((IntegerEntry)value).getDescription();
				}
			}
	
			if (value instanceof DerivedEntry) {
				if(((DerivedEntry)value).getName() != null){
					value = (((DerivedEntry)value).getName());
				}
				else{
					value = ((DerivedEntry)value).getDescription();
				}
			}
			
			if (value instanceof Unit) {
				value = ((Unit)value).getAbbreviation();
			}
			
			if (value instanceof Transformer) {
				value = ((Transformer)value).getWsOperation();
			}
			
			if (value instanceof ValidationRule) {
				value = ((ValidationRule)value).getDescription();
			}
			
			if (value instanceof Status) {
				value = ((Status)value).getShortName();
			}
			
			if (value instanceof Section) {
				value = ((Section)value).getName();
			}
			
			if (value instanceof Document) {
				value = ((Document)value).getName();
			}
			
			if (value instanceof DocumentGroup) {
				value = ((DocumentGroup)value).getName();
			}
			
			if (value instanceof DocumentOccurrence) {
				value = ((DocumentOccurrence)value).getDisplayText();
			}
	
			if (value instanceof DSDocumentOccurrence) {
				value = ((DSDocumentOccurrence)value).getDocument().getName() + " - " + ((DSDocumentOccurrence)value).getDocOccurrence().getDisplayText();
			}
			
			if (value instanceof Group) {
				value = ((Group)value).getLongName();
			}
			
			if (value instanceof Site) {
				value = ((Site)value).getSiteName();
			}
			
			if (value instanceof AssociatedConsentForm) {
				value = ((AssociatedConsentForm)value).getQuestion();
			}
			
			if (value instanceof ConsentFormGroup) {
				value = ((ConsentFormGroup)value).getDescription();
			}
			
			if (value instanceof PrimaryConsentForm) {
				value = ((PrimaryConsentForm)value).getQuestion();
			}
			
			if (value instanceof RoleType) {
				value = ((RoleType)value).getName();
			}
			
			if (value instanceof IReport) {
				value = ((IReport)value).getTitle();
			}
			
			if (value instanceof Transformer) {
				value = ((Transformer)value).getWsOperation();
			}
			
			if (value instanceof Stratum) {
				value = ((Stratum)value).getName();
			}
			
			if (value instanceof TreatmentHolderModel) {
				value = ((TreatmentHolderModel)value).getTreatmentName();
			}
	
			if (value instanceof GroupModel) {
				value = ((GroupModel)value).getGroup().getLongName();
			}
			
			if (value == null) {
				value = " ";
			}
            
            buff.append(value);
            if (i != values.length - 1) {
                buff.append("\n");
            }
        }
        
        return new StringSelection(buff.toString());
    }
    
    /**
     * We support both copying of actions.
     * @param c The component to copy from
     * @return int the copy reference
     */
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }

	/**
	 * Do not allow data to be imported into a <code>JList</code>
	 * @param comp the component list
	 * @param transferFlavors types of data that can be transferred
	 * @return false to disallow data being transferred from the list
	 */
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		return false;
	}
	
}
