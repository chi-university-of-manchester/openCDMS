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

import javax.swing.AbstractAction;

import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.model.DummyDocument;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.ui.MainTabbedPane;
import org.psygrid.datasetdesigner.ui.dataelementfacilities.SearchDELEntriesDialog;

public class SearchDELEntriesAction extends AbstractAction {

	private static final long serialVersionUID = 1515221139086865953L;

	public enum SearchType{
		Entries,
		Documents,
		Datasets,
		ValidationRules,	
		All
	}

	private Document document;
	private StudyDataSet ds;
	protected MainTabbedPane docPane;
	protected SearchType searchType;
	
	public SearchDELEntriesAction(MainTabbedPane docPane, SearchType searchType, String title){
		super(title);
		this.docPane = docPane;
		this.searchType = searchType;
		this.document = null;
		this.ds = null;
	}
	
	public SearchDELEntriesAction(MainTabbedPane docPane, SearchType searchType){
		super("Search Library "+searchType.toString());
		this.docPane = docPane;
		this.searchType = searchType;
		this.document = null;
		this.ds = null;
	}
	
	public SearchDELEntriesAction(MainTabbedPane docPane, StudyDataSet ds, SearchType searchType){
		this(docPane, searchType);
		this.ds = ds;
	}
	
	public SearchDELEntriesAction(MainTabbedPane docPane, Document document, SearchType searchType) {
		this(docPane, searchType);
		this.document = document;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(searchType == SearchType.Entries){
			//if document instanceof DummyDocument then create a new doc 
			//as entries must never be added to dummydocs..
			if (document == null || document instanceof DummyDocument) {
				if (ds == null) {
					//Retrieve the DEL 'dummy' dataset - must be the currently active set
					this.ds = DatasetController.getInstance().getActiveDs();
				}
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
				doc.setStatuses(new ArrayList<Status>());
				Status docStatus = new Status(DocumentStatus.DATASET_DESIGNER.toString(), DocumentStatus.DATASET_DESIGNER.toStatusLongName(), 0);
				doc.addStatus(docStatus);
				document = doc;
			}
			
			new SearchDELEntriesDialog(docPane, this.document, searchType);
		}
		else if(searchType == SearchType.Documents){
			if (ds == null) {
				//Retrieve the DEL 'dummy' dataset
				this.ds = DatasetController.getInstance().getActiveDs();
			}
			new SearchDELEntriesDialog(docPane, this.ds, searchType);
		}
		else if(searchType == SearchType.ValidationRules){
			if (ds == null) {
				//Retrieve the DEL 'dummy' dataset - must be the currently active set
				this.ds = DatasetController.getInstance().getActiveDs();
			}
			new SearchDELEntriesDialog(docPane, this.ds, searchType);
		}
	}
}
