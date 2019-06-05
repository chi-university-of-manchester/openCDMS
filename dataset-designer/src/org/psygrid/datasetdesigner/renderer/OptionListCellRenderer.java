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
package org.psygrid.datasetdesigner.renderer;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.psygrid.data.model.hibernate.*;

import org.psygrid.randomization.model.hibernate.Stratum;

import org.psygrid.www.xml.security.core.types.RoleType;

import org.psygrid.data.reporting.definition.IReport;

import org.psygrid.datasetdesigner.model.TreatmentHolderModel;

import org.psygrid.datasetdesigner.model.CustomFieldValueModel;
import org.psygrid.datasetdesigner.model.DSDocumentOccurrence;
import org.psygrid.datasetdesigner.model.DSOption;
import org.psygrid.datasetdesigner.model.GroupModel;


public class OptionListCellRenderer extends DefaultListCellRenderer {
	
	private boolean useForDEL = false;

	public boolean isUseForDEL() {
		return useForDEL;
	}
	
	public OptionListCellRenderer(){
		super();
		this.useForDEL = false;
	}

	public OptionListCellRenderer(boolean useForDEL){
		super();
		this.useForDEL = useForDEL;
	}
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		if(!useForDEL){
			//only render the display text of the option 
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

			if (value instanceof BooleanEntry) {
				if(((BooleanEntry)value).getName() != null){
					value = (((BooleanEntry)value).getName());
				}
				else{
					value = ((BooleanEntry)value).getDescription();
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

			if (value instanceof CustomFieldValueModel){
				value = ((CustomFieldValueModel)value).getValue();
			}
			
			if (value instanceof EslCustomField){
				value = ((EslCustomField)value).getName();
			}
			
			if (value instanceof GroupModel) {
				value = ((GroupModel)value).getGroup().getLongName();
			}
			
			if (value == null) {
				value = " ";
			}
			
		}else{
			if(value instanceof Element){
				value =  ((Element)value).getName();
			}
		}
		
		return super.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);
	}
	
	
	
	
}