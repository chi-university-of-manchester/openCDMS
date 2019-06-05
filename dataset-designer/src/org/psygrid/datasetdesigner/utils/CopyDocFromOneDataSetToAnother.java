package org.psygrid.datasetdesigner.utils;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.DataElementContainer;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.EntryStatus;
import org.psygrid.datasetdesigner.model.StudyDataSet;

public class CopyDocFromOneDataSetToAnother {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		
		String sourceDSName = args[0];
		String documentName = args[1];
		String destinationDS = args[2];
		String modifiedDSPath = args[3];
		String newDocName = null;
		
		if(args.length > 4){
			newDocName = args[4];
		}
		
		
		PersistenceManager.getInstance().setAliases();
		
		Object obj1 = PersistenceManager.getInstance().load(sourceDSName);
		Object obj2 = PersistenceManager.getInstance().load(destinationDS);
		
		StudyDataSet ds1 = (StudyDataSet)obj1;
		StudyDataSet ds2 = (StudyDataSet)obj2;
		
		DataSet sourceDS = ds1.getDs();
		Document docToCopy = null;
		
		//Retrieve the source document from the source dataset
		int numDocs = ds1.getDs().numDocuments();
		for(int i = 0; i < numDocs; i++){
			Document d = sourceDS.getDocument(i);
			if(d.getName().equals(documentName)){
				docToCopy = d;
				break;
			}
		}
		
		//Now we need to start putting this document into the destination dataset according to the same logic used when
		//loading a document from the DEL.
		


		Document theDoc = (Document)docToCopy;
		theDoc.setMyDataSet((DataSet) ds2.getDs());
		
		if(newDocName != null){
			theDoc.setName(newDocName);
			theDoc.setDisplayText(newDocName);
		}

		for(Entry e1: theDoc.getEntries()){
			Utils.addElementPropertiesToDataset((DataSet)theDoc.getDataSet(), new DataElementContainer(e1));

			if(e1 instanceof CompositeEntry){
				CompositeEntry compEntry = (CompositeEntry) e1;

				if(compEntry.getEntryStatus() == null){
					compEntry.setEntryStatus(EntryStatus.MANDATORY);
				}

				for(BasicEntry bE: compEntry.getEntries()){
					bE.setEntryStatus(compEntry.getEntryStatus());
					bE.setSection(compEntry.getSection());


				}
			}

		}
		ds2.getDs().addDocument(theDoc);
	
		PersistenceManager.getInstance().save(ds2, modifiedDSPath);

	}

}
