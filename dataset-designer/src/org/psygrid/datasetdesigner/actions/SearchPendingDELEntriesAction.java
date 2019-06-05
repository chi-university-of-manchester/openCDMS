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
package org.psygrid.datasetdesigner.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Section;
import org.psygrid.data.model.hibernate.SectionOccurrence;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.DummyDocument;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.ui.MainTabbedPane;
import org.psygrid.datasetdesigner.ui.dataelementfacilities.SearchDELEntriesDialog;

public class SearchPendingDELEntriesAction extends SearchDELEntriesAction {

	public SearchPendingDELEntriesAction(MainTabbedPane docPane, SearchType searchType){
		super(docPane, searchType, "Retrieve Elements Awaiting Approval");
	}

	public void actionPerformed(ActionEvent e) {
		//We will always access this from the DEL view
		
		//Retrieve the DEL 'dummy' dataset
		StudyDataSet ds = DocTreeModel.getInstance().getDELDataset();

		//Create a dummy document. This should only happen in the DEL view.
		DummyDocument doc = new DummyDocument();
		doc.setMyDataSet((DataSet)ds.getDs());
		List<Section> sections = new ArrayList<Section>();
		Section section = new Section("");
		section.setDescription("");
		section.setDisplayText("");
		section.addOccurrence(new SectionOccurrence(""));
		sections.add(section);
		doc.setSections(sections);
		doc.setIsEditable(false);
		
		//Search type should always be 'All' so this method can be simplified from the method in SearchDELEntriesAction
		new SearchDELEntriesDialog(docPane, ds, doc, searchType, SearchDELEntriesDialog.StatusType.Pending);
	}
}
