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
package org.psygrid.collection.entry.chooser;

import java.util.List;

import org.psygrid.collection.entry.AbstractEntryTestCase;
import org.psygrid.collection.entry.persistence.DataSetSummary;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DocumentGroup;

@SuppressWarnings("nls")
public class DocChooserTest extends AbstractEntryTestCase  {

    private DataSet dataSet;
    private ChoosableList choosableList;
    
    @Override
    protected void setUp() throws Exception {
        dataSet = getDataSet();
        
        DataSetGetter dataSetGetter1 = new DataSetGetter() {
            public DataSet getCompleteDataSet() {
                return dataSet;
            }
        };
        DataSetSummary dss1 = new DataSetSummary(dataSet);
        dss1.setDataSetGetter(dataSetGetter1);
        //TODO changed this after DataSetSummary was modified to not implement Choosable, just so
        //the class will compile! The unit test WILL NOW FAIL.
        List<Choosable> dssList = null;/*Arrays.asList(new DataSetSummary[] {
                dss1
        });*/
        
        choosableList = new ChoosableList(dssList);
    }
    
    public void testTablePruning() {
        DocumentGroup group1 = getFactory().createDocumentGroup("Group 1");
        dataSet.addDocumentGroup(group1);
        DocumentGroup group2 = getFactory().createDocumentGroup("Group 2");
        dataSet.addDocumentGroup(group2);
        DocumentGroup group3 = getFactory().createDocumentGroup("Group 3");
        dataSet.addDocumentGroup(group3);
        DocumentGroup group4 = getFactory().createDocumentGroup("Group 4");
        dataSet.addDocumentGroup(group4);
        DocumentGroup group5 = getFactory().createDocumentGroup("Group 5");
        dataSet.addDocumentGroup(group5);
        try {
            ChooserModel model = new ChooserModel(choosableList);
            //Enter data set
            model.loadChoosable(0);
            //Enter first document group
            model.loadChoosable(0);
            model.setParentTableModel();
            model.loadChoosable(1);
            model.setParentTableModel();
            model.loadChoosable(2);
            model.setParentTableModel();
            model.loadChoosable(3);
            model.setParentTableModel();
            model.loadChoosable(4);
            model.setParentTableModel();
        } catch (ChoosableException e) {
            fail(e.getMessage());
        }   
    }
}
