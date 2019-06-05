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

package org.psygrid.collection.entry.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ListDataEvent;

import org.psygrid.collection.entry.ApplicationModel;
import org.psygrid.collection.entry.EntryHelper;
import org.psygrid.collection.entry.event.ChangeVetoedEvent;
import org.psygrid.collection.entry.event.ChangeVetoedListener;
import org.psygrid.collection.entry.event.SectionChangedEvent;
import org.psygrid.collection.entry.event.SectionEvent;
import org.psygrid.collection.entry.event.SectionListener;
import org.psygrid.collection.entry.util.IncInteger;
import org.psygrid.data.model.hibernate.SectionOccurrence;

public class SectionComboBoxModel extends EntryComboBoxModel {

    private SectionListener sectionListener;
    private ApplicationModel model;
    private boolean showSectionCombo = false;
    private boolean showToolbarOnOpen = false;
    
    /* 
     * We could have used renderers instead of returning a text representation
     * from the model and converting it back on setSelectedItem. It gets a bit
     * complicated due to the fact that labels are sometimes auto-generated and
     * others they're explicit and the only way to know is to look at all the
     * items. This seemed a reltatively easy way to achieve that.
     */
    private Map<SectionPresModel, String> presModelToText = new HashMap<SectionPresModel, String>();
    private Map<String, SectionPresModel> textToPresModel = new HashMap<String, SectionPresModel>();

    public SectionComboBoxModel(ApplicationModel model) {
        this.model = model;
        sectionListener = new SectionListener() {
            
            public void sectionAdded(SectionEvent event) {
                handleSectionAddedOrRemoved();
            }

            public void sectionRemoved(SectionEvent event) {
                handleSectionAddedOrRemoved();
            }
            
            private void handleSectionAddedOrRemoved() {
                presModelToText.clear();
                textToPresModel.clear();
                init();
                fireListDataChanged(new ListDataEvent(SectionComboBoxModel.this,
                        ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
            }
            
            public void sectionChanged(SectionChangedEvent event) {
                fireListDataChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, 0));
            }

        };
        model.addSectionListener(sectionListener);
        init();
    }
    
    public void addChangeVetoedListener(ChangeVetoedListener listener) {
        listenerList.add(ChangeVetoedListener.class, listener);
    }
    
    public void removeChangeVetoedListener(ChangeVetoedListener listener) {
        listenerList.remove(ChangeVetoedListener.class, listener);
    }
    
    private void fireChangeVetoed(ChangeVetoedEvent event) {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeVetoedListener.class) {
                ((ChangeVetoedListener) listeners[i + 1]).changeVetoed(event);
            }
        }
    }
    
    public void dispose() {
        model.removeSectionListener(sectionListener);
    }
    
    private void init() {
        List<SectionPresModel> sectionPresModels = model.getDocSectionOccPresModels();
        boolean hasLabel = EntryHelper.anyHasLabel(sectionPresModels);
        
        int secOccCounter = 0;
        int secOccInstCounter = 0;
        Map<SectionOccurrence, IncInteger> secOccInstMap = new HashMap<SectionOccurrence, IncInteger>();
        for (int i = 0 ; i < sectionPresModels.size(); ++i) {
            SectionPresModel sectionPresModel = sectionPresModels.get(i);
            if ( 0 == i && null != sectionPresModel.getSecOccInstance() ){
            	showToolbarOnOpen = true;
            }
            if ( null == sectionPresModel.getSecOccInstance() ){
            	secOccCounter++;
            }
            else{
            	secOccInstCounter++;
            }
            String sectionLabelText = getSectionNumberLabel(sectionPresModel, i, hasLabel) +
                EntryHelper.getSectionLabelText( sectionPresModel, secOccInstMap);
            textToPresModel.put(sectionLabelText, sectionPresModel);
            presModelToText.put(sectionPresModel, sectionLabelText);
        }
        if ( secOccInstCounter>0 || secOccCounter>1 ){
        	showSectionCombo = true;
        }
    }
    
    private String getSectionNumberLabel(SectionPresModel sectionPresModel, int index,
            boolean hasLabel) {
        SectionOccurrence sectionOcc = sectionPresModel.getSectionOccurrence();
        String numberLabel;
        if (hasLabel) {
            if (sectionOcc.getLabel() != null && 
                    (!sectionOcc.getLabel().equals(""))) { //$NON-NLS-1$
                numberLabel = sectionOcc.getLabel() + ". "; //$NON-NLS-1$
            }
            else
                numberLabel = ""; //$NON-NLS-1$
        }
        else
            numberLabel = index + 1 + ". "; //$NON-NLS-1$
        
        return numberLabel;
    }
    
    public Object getSelectedItem() {
        return getElementAt(model.getCurrentSectionIndex());
    }

    public void setSelectedItem(Object anItem) {
        if (!isEnabled(anItem)) {
            fireChangeVetoed(new ChangeVetoedEvent(this));
            return;
        }
        SectionPresModel sectionPresModel = textToPresModel.get(anItem);
        /* 
         * This causes a section changed event to be fired that in turn causes
         * a ListDataEvent to be fired.
         */
        model.setSection(model.getDocSectionOccPresModels().indexOf(sectionPresModel));
    }
    
    public Object getElementAt(int index) {
        return presModelToText.get(model.getDocSectionOccPresModels().get(index));
    }

    public int getSize() {
        return model.getDocSectionOccPresModels().size();
    }

    public boolean isEnabled(Object value) {
        SectionPresModel sectionPresModel = textToPresModel.get(value);
        int valueIndex = model.getDocSectionOccPresModels().indexOf(sectionPresModel);
        if (valueIndex == model.getCurrentSectionIndex())
            return true;
        List<Integer> sectionIndices = model.getSectionTransitionsIndices();
        return sectionIndices.contains(Integer.valueOf(valueIndex));
    }

	public boolean isShowSectionCombo() {
		return showSectionCombo;
	}

	public boolean isShowToolbarOnOpen() {
		return showToolbarOnOpen;
	}
}
