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


package org.psygrid.collection.entry.ui;

import java.util.List;

import org.psygrid.collection.entry.Application;
import org.psygrid.collection.entry.event.EslEvent;
import org.psygrid.collection.entry.event.EslListener;
import org.psygrid.collection.entry.util.EslHelper;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Record;
import org.psygrid.esl.model.ISubject;

/**
 * A dialog window allowing the user to search the ESL to
 * retrieve a record as an alternative to using an identifier.
 * 
 * The dialog window displays all possible fields from the ESL
 * and will retrieve a list of matching subjects.
 * 
 * @author Lucy Bridges
 *
 */
public class EslFullSearchSubjectDialog extends EslDialog {

    private static final long serialVersionUID = 1L;

    private Application application;
    private ApplicationDialog parent;
    private DataSet dataset;
    
    private Record selectedRecord;
    
	public EslFullSearchSubjectDialog(Application application, ApplicationDialog parent, DataSet dataset) {
		super(application, parent);
		this.parent = parent;
		this.application = application;
		this.dataset = dataset;
		init(null);
	}

	@Override
	public void initListeners() {
        contentPanel.addEslListener(new EslListener() {
            public void eslCompleted(EslEvent event) {
                if ( null == event.getEslSubject() ){
                    dispose();
                }
                else{
                	event.getEslSubject().setStudyNumber(null);		//Don't search by study number (must not be an empty string)
                	//Get a list of matching subjects from the ESL
                	List<ISubject> subjects = EslHelper.searchEslSubject(application, parent, event.getEslSubject(), dataset);
                	//Close fullsearch window
                	dispose();
            		//Display the appropriate dialog window.
            		selectedRecord = EslHelper.displaySearchResults(application, parent, subjects, dataset);
            		
                }
            }
        });
	}
	
	public Record getSelectedRecord() {
		return selectedRecord;
	}
	
	@Override
	public EslPanel createContentPanel(ISubject subject) {
		return new EslFullSearchPanel(dataset);
	}
	
}
