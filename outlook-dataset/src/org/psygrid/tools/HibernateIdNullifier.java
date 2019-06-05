package org.psygrid.tools;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.datasetdesigner.model.StudyDataSet;

public class HibernateIdNullifier {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fullPathToStudyFile = args[0];
		
		Object obj = null;
		
		try {
			PersistenceManager.getInstance().setAliases();
			obj = PersistenceManager.getInstance().load(fullPathToStudyFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StudyDataSet studyDS = (StudyDataSet)obj;
		
		DataSet ds = studyDS.getDs();
		DataSet dsProper = (DataSet)ds;
		dsProper.setPrepareElementForNewRevision(true);
		dsProper = dsProper.toDTO().toHibernate();
		dsProper.setPrepareElementForNewRevision(false);
		studyDS.setDs(dsProper);
		
		String fullPathOfSavedFileName = args[1];
		try {
			PersistenceManager.getInstance().save(studyDS, fullPathOfSavedFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		

	}

}
