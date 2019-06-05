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

import javax.swing.table.TableCellRenderer;

import org.psygrid.collection.entry.Application;

public class ReportChooserPanel extends ChooserPanel    {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public ReportChooserPanel(Application application, ChooserDialog dialog) {
        super(application, dialog);
    }
    
    @Override
    public void init(ChoosableList choosableList) {
        Object type = choosableList.getType();
        if (type != null && type instanceof ChoosableType == false) {
            throw new IllegalArgumentException("choosableList#getType() must " + //$NON-NLS-1$
                    "return an enum of type RecordChoosableType, but it " + //$NON-NLS-1$
                    "returns: " + type.getClass()); //$NON-NLS-1$
        }
        super.init(choosableList);
    }

    @Override
    protected TableCellRenderer getTableCellRenderer() {
        return new ReportChooserTableRenderer();
    }

    @Override
    protected void processSelectionAction(int row, Choosable selectedValue) {
    	ChoosableType type = selectedValue.getType();
        switch (type) {
        case DATASET:
        case RECORD:
            loadChoosable(row);
            break;
        case REPORT:
            ChoosableReport report = (ChoosableReport) selectedValue;
            ReportSelectedEvent event = new ReportSelectedEvent(this,
                    report.getReportDefinition(), report.getRecord());
            fireReportSelected(event);
        }
        
    }

    public void addReportSelectedListener(ReportSelectedListener listener) {
        listenerList.add(ReportSelectedListener.class, listener);        
    }
    
    public void removeReportSelectedListener(ReportSelectedListener listener) {
        listenerList.remove(ReportSelectedListener.class, listener);        
    }
    
    protected void fireReportSelected(ReportSelectedEvent event) {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ReportSelectedListener.class) {
                ((ReportSelectedListener) listeners[i + 1]).reportSelected(event);
            }
        }
    }
}
