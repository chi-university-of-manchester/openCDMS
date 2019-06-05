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


package org.psygrid.collection.entry.renderer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.EventListenerList;

import org.psygrid.collection.entry.Fonts;
import org.psygrid.collection.entry.event.EntryStatusEvent;
import org.psygrid.collection.entry.event.EntryStatusListener;
import org.psygrid.collection.entry.event.RendererCreatedEvent;
import org.psygrid.collection.entry.model.EntryPresModel;
import org.psygrid.collection.entry.model.SectionPresModel;
import org.psygrid.collection.entry.ui.EntryLabel;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.ValidationResult;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.EntryStatus;
import org.psygrid.data.model.hibernate.NarrativeEntry;
import org.psygrid.data.model.hibernate.Status;

public class NarrativeRendererSPI implements RendererSPI   {

    public boolean canHandle(Entry model, Entry modelParent) {
        if (model instanceof NarrativeEntry) {
            return true;
        }
        
        return false;
    }

    public Renderer getRenderer(RendererData rendererData) {
        NarrativeEntry entry = (NarrativeEntry) rendererData.getModel();
        String displayText = RendererHelper.getInstance().concatEntryLabelAndDisplayText(entry);
        EntryLabel label = new EntryLabel(displayText);
        switch(entry.getStyle()) {
        case HEADER:
            label.setFont(Fonts.getInstance().getHeaderFont());
            break;
        case SMALL:
            label.setFont(Fonts.getInstance().getSmallFont());
            break;
        case NORMAL:
            break;
        }
        Status docStatus = rendererData.getDocOccurrenceInstance().getStatus();
        RendererHandler handler = rendererData.getRendererHandler();
        PresModelRenderer<NarrativePresModel> r = new PresModelRenderer<NarrativePresModel>(new NarrativePresModel(rendererData));
        handler.putRenderer(entry, rendererData.getRowIndex(), r);
        r.setLabel(label);
        
        /* 
         * Pass label as field since the receiving method does not expect that
         * to be null.
         */
        RendererHelper.getInstance().processEntryStatus(null, r.getPresModel(), label,
                rendererData.isCopy(), docStatus, rendererData.isEditable());
        
        //Uses the label as the field since NarrativeRenderer doesn't have a 
        //field
        RendererHelper.getInstance().processDescription(null, entry, label);
        
        int rowIndex = rendererData.getRowIndex();
        handler.putRenderer(entry, rowIndex, r);
        handler.fireRendererCreatedEvent(new RendererCreatedEvent(this, r));
        return new Renderer(label);
    }
    
    /**
     * Only used for the EntryStatus related functionality (i.e. entries
     * can be enabled or disabled during runtime or startup.
     */
    private static final class NarrativePresModel implements EntryPresModel    {

        private final EventListenerList listenerList = new EventListenerList();
        private final DocumentInstance docInstance;
        private final NarrativeEntry entry;
        private final ValueModel entryStatusModel;
        private final SectionPresModel sectionPresModel;
        
        public NarrativePresModel(RendererData rendererData) {
            docInstance = rendererData.getDocOccurrenceInstance();
            entry = (NarrativeEntry) rendererData.getModel();
            entryStatusModel = new ValueHolder(entry.getEntryStatus());
            sectionPresModel = rendererData.getRendererHandler().getSectionPresModel();
            initEventHandling();
        }
        
        private void initEventHandling() {
            // Safe not to remove
            getEntryStatusModel().addValueChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    EntryStatus oldValue = (EntryStatus) evt.getOldValue();
                    EntryStatus newValue = (EntryStatus) evt.getNewValue();
                    fireEntryStatusEvent(new EntryStatusEvent(NarrativePresModel.this,
                            oldValue, newValue, sectionPresModel));
                }
            });
        }
        
        private void fireEntryStatusEvent(EntryStatusEvent event) {
            Object[] listeners = listenerList.getListenerList();

            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == EntryStatusListener.class) {
                    ((EntryStatusListener) listeners[i + 1]).statusChanged(event);
                }
            }
        }

        public void addEntryStatusListener(EntryStatusListener listener) {
            listenerList.add(EntryStatusListener.class, listener);
        }
        
        public void removeEntryStatusListener(EntryStatusListener listener) {
            listenerList.remove(EntryStatusListener.class, listener);
        }

        public DocumentInstance getDocInstance() {
            return docInstance;
        }

        public NarrativeEntry getEntry() {
            return entry;
        }

        public EntryStatusListener[] getEntryStatusListeners() {
            return listenerList.getListeners(EntryStatusListener.class);
        }

        public ValueModel getEntryStatusModel() {
            return entryStatusModel;
        }

        public SectionPresModel getSectionOccPresModel() {
            return sectionPresModel;
        }

        public void performValidation(boolean partial) {
            // Do nothing
        }

        public ValidationResult validate(boolean partial) {
            return ValidationResult.EMPTY;
        }

		public void reset() {
			//do nothing
		}

		public void touch() {
			// TODO Auto-generated method stub
			
		}
    }
}