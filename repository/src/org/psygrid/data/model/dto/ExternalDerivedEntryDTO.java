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

package org.psygrid.data.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to represent an entry whose value in a record is
 * derived from the values of other entrys and calculated
 * using an external service.
 * 
 * @author Lucy Bridges
 */
public class ExternalDerivedEntryDTO extends BasicEntryDTO {

	protected TransformerDTO externalTransformer;

	/**
	 * Array of variables that are inputs into the calculation.
	 */
	private BasicEntryDTO[] variables = new BasicEntryDTO[0];

	/**
	 * Array of variable keys that represent how the variables
	 * are referenced in a formula.
	 */
	private String[] variableKeys= new String[0];

	private boolean transformWithStdCodes;

	/**
	 * Specify the list of variable names that must have values entered (not
	 * standard codes), if transformWithStdCodes is allowed.
	 */
	private String[] transformRequiredVariables = new String[0];

	/**
	 * Get the array of input variables.
	 * 
	 * @return The array of input variables.
	 */
	public BasicEntryDTO[] getVariables() {
		return variables;
	}

	private MultipleVariableTestDTO test;

	/**
	 * Set the array of input variables.
	 * 
	 * @param variables The array of input variables.
	 */
	public void setVariables(BasicEntryDTO[] variables) {
		this.variables = variables;
	}

	/**
	 * Get the array of variable keys.
	 * 
	 * @return The array of variable keys.
	 */
	public String[] getVariableKeys() {
		return variableKeys;
	}

	/**
	 * Set the array of variable keys.
	 * 
	 * @param variableKeys The array of variable keys.
	 */
	public void setVariableKeys(String[] variableKeys) {
		this.variableKeys = variableKeys;
	}

	/**
	 * 
	 * @return externalTransformer
	 */
	public TransformerDTO getExternalTransformer() {
		return externalTransformer;
	}

	public void setExternalTransformer(TransformerDTO transformer) {
		this.externalTransformer = transformer;
	}


	public boolean isTransformWithStdCodes() {
		return transformWithStdCodes;
	}

	public void setTransformWithStdCodes(boolean transformWithStdCodes) {
		this.transformWithStdCodes = transformWithStdCodes;
	}

	public String[] getTransformRequiredVariables() {
		return transformRequiredVariables;
	}

	public void setTransformRequiredVariables(String[] transformRequiredVariables) {
		this.transformRequiredVariables = transformRequiredVariables;
	}

	public org.psygrid.data.model.hibernate.ExternalDerivedEntry toHibernate(Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
		//check for an already existing instance of a hibernate object for this 
		//derived entry in the map of references
		org.psygrid.data.model.hibernate.ExternalDerivedEntry hDE = null;
		if ( hRefs.containsKey(this)){
			hDE = (org.psygrid.data.model.hibernate.ExternalDerivedEntry)hRefs.get(this);
		}
		if ( null == hDE ){
			//an instance of the external derived entry has not already
			//been created, so create it, and add it to the 
			//map of references
			hDE = new org.psygrid.data.model.hibernate.ExternalDerivedEntry();
			hRefs.put(this, hDE);
			toHibernate(hDE, hRefs);
		}

		return hDE;
	}

	public void toHibernate(org.psygrid.data.model.hibernate.ExternalDerivedEntry hDE, Map<PersistentDTO, org.psygrid.data.model.hibernate.Persistent> hRefs){
		super.toHibernate(hDE, hRefs);
		// hDE.setExternalClient(externalClient);
		hDE.setTransformWithStdCodes(transformWithStdCodes);

		Map hVars = hDE.getVariables();
		for ( int i=0; i<this.variableKeys.length; i++){
			String key = this.variableKeys[i];
			BasicEntryDTO value = this.variables[i];
			if ( null != key && null != value ){
				hVars.put(key, value.toHibernate(hRefs));
			}
		}

		if (test != null){
			hDE.setTest(test.toHibernate(hRefs));
		}

		if ( externalTransformer != null ) {
			hDE.setExternalTransformer(externalTransformer.toHibernate(hRefs));
		}

		List<String> hReqVars = new ArrayList<String>();
		if (transformRequiredVariables != null) {
			for (int j=0; j < transformRequiredVariables.length; j++) {
				if (transformRequiredVariables[j] != null) {
					hReqVars.add(transformRequiredVariables[j]);
				}
			}
		}
		hDE.setTransformRequiredVariables(hReqVars);
	}

	public MultipleVariableTestDTO getTest() {
		return test;
	}

	public void setTest(MultipleVariableTestDTO test) {
		this.test = test;
	}
}
