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
package org.psygrid.datasetdesigner.model;


import java.util.ArrayList;

import org.psygrid.randomization.model.hibernate.Stratum;

import org.psygrid.datasetdesigner.model.TreatmentHolderModel;

public class RandomisationHolderModel  {
	
	private ArrayList<Stratum> randomisationStrata = new ArrayList<Stratum>();
	private ArrayList<TreatmentHolderModel> randomisationTreatments = new ArrayList<TreatmentHolderModel>();
	private int minimumBlockSize = -1;
	private int maximumBlockSize = -1;
	
	
	public ArrayList<Stratum> getRandomisationStrata() {
		return randomisationStrata;
	}

	public void setRandomisationStrata(ArrayList<Stratum> randomisationStrata) {
		this.randomisationStrata = randomisationStrata;
	}

	public ArrayList<TreatmentHolderModel> getRandomisationTreatments() {
		return randomisationTreatments;
	}

	public void setRandomisationTreatments(
			ArrayList<TreatmentHolderModel> randomisationTreatments) {
		this.randomisationTreatments = randomisationTreatments;
	}

	public int getMaximumBlockSize() {
		return maximumBlockSize;
	}

	public void setMaximumBlockSize(int maximumBlockSize) {
		this.maximumBlockSize = maximumBlockSize;
	}

	public int getMinimumBlockSize() {
		return minimumBlockSize;
	}

	public void setMinimumBlockSize(int minimumBlockSize) {
		this.minimumBlockSize = minimumBlockSize;
	}
	
	
	public boolean validate() {
		boolean validated = true;
		
		if (minimumBlockSize == -1 || maximumBlockSize == -1) {
			validated = false;
		}
		
		if (getRandomisationTreatments().size() == 0) {
			validated = false;
		}
		
		/*
		if (getRandomisationStrata().size() == 0) {
			validated = false;
		}
		*/
		
		return validated;
	}
	
	
}