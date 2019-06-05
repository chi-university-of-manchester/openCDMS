package org.psygrid.datasetdesigner.model;

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
import org.psygrid.collection.entry.model.EntryComboBoxModel;
import org.psygrid.collection.entry.model.SectionPresModel;


import org.psygrid.data.model.hibernate.SectionOccurrence;

public class DsdSectionComboBoxModel extends EntryComboBoxModel {

    private SectionListener sectionListener;
    private ApplicationModel model;
    
    /* 
     * We could have used renderers instead of returning a text representation
     * from the model and converting it back on setSelectedItem. It gets a bit
     * complicated due to the fact that labels are sometimes auto-generated and
     * others they're explicit and the only way to know is to look at all the
     * items. This seemed a reltatively easy way to achieve that.
     */
    private Map<SectionPresModel, String> presModelToText = new HashMap<SectionPresModel, String>();
    private Map<String, SectionPresModel> textToPresModel = new HashMap<String, SectionPresModel>();

    public DsdSectionComboBoxModel(ApplicationModel model) {
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
                fireListDataChanged(new ListDataEvent(DsdSectionComboBoxModel.this,
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
        
        Map<SectionOccurrence, IncInteger> secOccInstCounter = new HashMap<SectionOccurrence, IncInteger>();
        for (int i = 0 ; i < sectionPresModels.size(); ++i) {
            SectionPresModel sectionPresModel = sectionPresModels.get(i);
            String sectionLabelText = getSectionNumberLabel(sectionPresModel, i, hasLabel) +
                EntryHelper.getSectionLabelText( sectionPresModel, secOccInstCounter);
            textToPresModel.put(sectionLabelText, sectionPresModel);
            presModelToText.put(sectionPresModel, sectionLabelText);
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
//        if (!isEnabled(anItem)) {
//            fireChangeVetoed(new ChangeVetoedEvent(this));
//            return;
//        }
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
    	return true;
//        SectionPresModel sectionPresModel = textToPresModel.get(value);
//        int valueIndex = model.getDocSectionOccPresModels().indexOf(sectionPresModel);
//        if (valueIndex == model.getCurrentSectionIndex())
//            return true;
//        List<Integer> sectionIndices = model.getSectionTransitionsIndices();
//        return sectionIndices.contains(Integer.valueOf(valueIndex));
    }
}
