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

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;

public class ChooserModel {
    
    private List<ChooserTableModel> tableModels;
    private int currentModelIndex;

    protected EventListenerList listenerList = new EventListenerList();
    
    /**
     * Creates an instance of this object initialised with the provided
     * <code>choosableList</code>.
     * @param choosableList Used to initialise the model. This list is not
     * copied and as a result, it should not be used by the caller after calling
     * this constructor.
     * @throws ChoosableException
     */
    public ChooserModel(ChoosableList choosableList)
            throws ChoosableException {
        synchronized (this) {
            tableModels = new ArrayList<ChooserTableModel>();
            ChooserTableModel tableModel = createTableModel(choosableList);
            tableModels.add(tableModel);
            currentModelIndex = 0;
        }
    }
        
    public ChooserTableModel createTableModel(Choosable choosable) throws ChoosableException {
        return new ChooserTableModel(choosable);
    }

    public ChooserTableModel getCurrentTableModel() {
        return tableModels.get(currentModelIndex);
    }
    
    public void addChoosableLoadedListener(ChooserModelListener listener) {
        listenerList.add(ChooserModelListener.class, listener);
    }
    
    public void removeChoosableLoadedListener(ChooserModelListener listener) {
        listenerList.remove(ChooserModelListener.class, listener);
    }
    
    private void pruneTableModelsEnd() {
        while (currentModelIndex < tableModels.size() - 1) {
            tableModels.remove(tableModels.size() - 1);
        }
    }
    
    /**
     * Load the choosable located in <code>row</code> of currentTableModel. This
     * method is thread-safe.
     * 
     * @param row
     * @throws ChoosableException
     */
    public void loadChoosable(int row) throws ChoosableException {
        Choosable choosable = tableModels.get(currentModelIndex).getValueAtRow(row);
        addTableModel(choosable); 
    }
    
    protected void fireChoosableLoaded(Choosable choosable) {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChooserModelListener.class) {
                ChooserModelEvent event = new ChooserModelEvent(this,
                            choosable);
                ((ChooserModelListener) listeners[i + 1]).choosableLoaded(event);
            }
        }
    }

    public void setNextTableModel() {
        ++currentModelIndex;  
        fireChoosableLoaded(getCurrentTableModel().getParent());
    }
    
    public void setPreviousTableModel() {
        --currentModelIndex;
        fireChoosableLoaded(getCurrentTableModel().getParent());
    }
    
    private void pruneTableModelsBeginning() {
        while (tableModels.size() > 10) {
            tableModels.remove(0);
            --currentModelIndex;
        }
    }
    
    /**
     * Changes the currentTableModel property to its parent table model. This
     * method is thread-safe.
     * 
     * @throws ChoosableException
     */
    public void setParentTableModel() throws ChoosableException {
        Choosable parent = getCurrentTableModel().getParent().getParent();
        addTableModel(parent);
    }
    
    private void addTableModel(final Choosable parent) throws ChoosableException  {
        ChooserTableModel tableModel = createTableModel(parent);
        synchronized (this) {
            pruneTableModelsEnd();
            tableModels.add(tableModel);
            ++currentModelIndex;
            pruneTableModelsBeginning();
        }
        if (!EventQueue.isDispatchThread())
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    fireChoosableLoaded(parent);
                }
            });
        else {
            fireChoosableLoaded(parent);
        }
    }

    public boolean hasNext() {
        if (currentModelIndex < tableModels.size() - 1) {
            return true;
        }
        
        return false;
    }

    public boolean hasPrevious() {
        if (currentModelIndex > 0) {
            return true;
        }
        
        return false;
    }

    public boolean hasParent() {
        // We actually check if there is a grandparent, since we need that
        // to be able to find the siblings of the parent. Any item displayed
        // will always have a valid parent.
        if (getCurrentTableModel().getParent().getParent() != null) {
            return true;
        }
        return false;
    }
   
}
