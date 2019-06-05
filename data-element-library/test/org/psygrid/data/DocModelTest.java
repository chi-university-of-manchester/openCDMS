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
package org.psygrid.data;

import java.util.List;

import org.psygrid.data.model.IDataSet;
import org.psygrid.data.model.ModelException;
import org.psygrid.data.model.RepositoryModel;
import org.psygrid.data.model.RepositoryModelException;
import org.psygrid.data.model.RepositoryModel.NodeType;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.outlook.OutlookDataset;


public class DocModelTest {

	/**
	 * @param args
	 * @throws RepositoryModelException 
	 * @throws ModelException 
	 */
	public static void main(String[] args) throws ModelException, RepositoryModelException {
		
		//Get out a document (say baseline audit) from outlook dataset
		//Convert it into our new format!
		String desiredDocument = new String(args[0]);
	
		IDataSet ds = OutlookDataset.createDataset();
		DataSet rawDS = (DataSet) ds;
		List<Document> docs = rawDS.getDocuments();
		

		Document doc = null;
		for(Document aDoc:docs){
			if(aDoc.getName().equals(desiredDocument)){
				doc = aDoc;
				break;
			}
		}
		
		
		//At this point, check to see if the model is valid.
		
		if(doc != null){
			RepositoryModel repModel = new RepositoryModel(doc, RepositoryModel.NodeIdentificationMethod.Binary, true);
			int debugLine = 6;
			
			List<RepositoryModel.RepositoryObject> leafNodes = repModel.getLeafNodes();
	
			int maxTreeDepth = repModel.getMaxDepth();
			
			debugLine = 7;
			
			//Once the leaf nodes are obtained, it is necessary to ascend them.
			//Start with the deepest node and ascend a step.
			
			int maxDepth = repModel.getMaxDepth();
			
			
			for(int i = maxDepth; i >= 0; i--){
				List<RepositoryModel.RepositoryObject> objects = repModel.getNodesAtSpecifiedDepth(i, NodeType.All);
				for(RepositoryModel.RepositoryObject obj : objects){
					repModel.setIsProcessed(obj);
				}
			}
			
			//I would like to get 
			
		}
		

	}

}
