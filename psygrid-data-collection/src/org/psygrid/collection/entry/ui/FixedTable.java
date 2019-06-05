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

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.model.EntryTableModel;
import org.psygrid.collection.entry.model.SectionPresModel;
import org.psygrid.collection.entry.renderer.BasicRenderer;
import org.psygrid.collection.entry.renderer.Renderer;
import org.psygrid.collection.entry.renderer.RendererHandler;
import org.psygrid.collection.entry.renderer.RendererHelper;
import org.psygrid.collection.entry.renderer.RendererData.EditableStatus;
import org.psygrid.data.model.ITextValue;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.TextEntry;

public class FixedTable extends EntryTable  {

    private static final long serialVersionUID = 1L;
    
    public FixedTable(EntryTableModel model, RendererHandler rendererHandler, JLabel validationLabel) {
        super(model, rendererHandler, validationLabel);
    }
    
    private BasicRenderer<BasicPresModel> getFirstColumnRenderer(int rowIndex) {
        CompositeEntry compositeEntry = model.getEntry();
        TextEntry basicEntry = (TextEntry) compositeEntry.getEntry(0);
        
        DocumentInstance docInstance =
            rendererHandler.getBuilderHandler().getDocOccurrenceInstance();
        SectionPresModel sectionOccPresModel = 
            rendererHandler.getSectionPresModel();
        
        BasicResponse response =
            RendererHelper.getInstance().getModelResponse(basicEntry, model.getResponse(), 
                docInstance, sectionOccPresModel, rowIndex);
        
        ITextValue value;
        if (response == null) {
            value = basicEntry.generateValue();
            
            String valueText = compositeEntry.getRowLabel(rowIndex);
            value.setValue(valueText);
            response = RendererHelper.getInstance().processResponse(basicEntry, model.getResponse(), 
                    docInstance, sectionOccPresModel, rowIndex, value);
        }
        else {
            value = (ITextValue) RendererHelper.getInstance().checkAndGetValue(response);
        }
        
        BasicPresModel presModel = rendererHandler.createBasicPresModel(this, 
                response, value, null);
        BasicTextEntryField textField = new BasicTextEntryField(presModel.getValueModel());
        textField.getTextComponent().setEditable(false);
        
        BasicRenderer<BasicPresModel> renderer = new BasicRenderer<BasicPresModel>(null, new JLabel(), textField,
                presModel);
        rendererHandler.putRenderer(basicEntry, rowIndex, renderer);
        return renderer;
    }

    @Override
    protected Renderer getRenderer(int rowIndex, int columnIndex, 
            EditableStatus editable) {
        if (columnIndex == 0) {
            return getFirstColumnRenderer(rowIndex);
        }
        
        return super.getRenderer(rowIndex, columnIndex, editable);
    }
    
    @Override
    protected int getFarRightMargin() {
        if ( applyStdCodeToRowButtons.size() > 0 ) {
            return applyStdCodeToRowButtons.get(0).getPreferredSize().width;
        }
        return 0;
    }
    
    @Override
    protected JComponent getFarRightComponent(int row) {
        return applyStdCodeToRowButtons.get(row);
    }
        
    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();

        if (applyStdCodeToRowButtons.size() > 0)  {
            d.width += applyStdCodeToRowButtons.get(0).getPreferredSize().width;
        }
        
        return d;
    }
}
