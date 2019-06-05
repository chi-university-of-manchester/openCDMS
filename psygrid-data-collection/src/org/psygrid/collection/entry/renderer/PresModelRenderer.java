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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.psygrid.collection.entry.Icons;
import org.psygrid.collection.entry.model.EntryPresModel;
import org.psygrid.collection.entry.ui.Messages;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.model.hibernate.EditAction;

public class PresModelRenderer<T extends EntryPresModel> extends Renderer {
    
    private T presModel;
    private JLabel validationLabel;
    private JLabel helpLabel;
    private JLabel restrictedLabel;
    
    public PresModelRenderer(List<JComponent> components, 
            T presModel) {
        super(components);
        this.presModel = presModel;
    }
    
    public PresModelRenderer(T presModel) {
        this.presModel = presModel;
    }
    
    public T getPresModel() {
        return presModel;
    }
    
    public void setPresModel(T presModel) {
        this.presModel = presModel;
    }

    public JLabel getValidationLabel() {
        return validationLabel;
    }

    public void setValidationLabel(JLabel validationLabel) {
        this.validationLabel = validationLabel;
        if (validationLabel != null) {
            components.add(validationLabel);
        }
    }

    public void setHelpLabel(){
        helpLabel = new JLabel();
        if ( null != presModel.getEntry().getDescription()
        		&& !(presModel.getEntry().getDescription().equals(""))){
            helpLabel.setIcon(Icons.getInstance().getIcon("help"));
            helpLabel.setToolTipText("Click for help on this entry");
            helpLabel.addMouseListener(
                new MouseAdapter(){
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        WrappedJOptionPane.showWrappedMessageDialog(
                                RendererHelper.getInstance().findJFrameFromRenderer(PresModelRenderer.this), 
                                presModel.getEntry().getDescription(),
                                "Help",
                                JOptionPane.INFORMATION_MESSAGE);
                    }        
                }
            );
        }
        components.add(helpLabel);
    }
    
    public JLabel getHelpLabel() {
        return helpLabel;
    }
    
    
    public void setRestrictedLabel(){
        restrictedLabel = new JLabel();
        
        if ( EditAction.DENY.equals(presModel.getEntry().getEditingPermitted())) {
        	restrictedLabel.setIcon(Icons.getInstance().getIcon("entry_locked"));
            restrictedLabel.setToolTipText(Messages.getString("Entry.denied"));
        }
        else if (EditAction.READONLY.equals(presModel.getEntry().getEditingPermitted())){
            restrictedLabel.setIcon(Icons.getInstance().getIcon("entry_locked"));
            restrictedLabel.setToolTipText(Messages.getString("Entry.readonly"));
        }
        components.add(restrictedLabel);
    }
    
    public JLabel getRestrictedLabel() {
    	return restrictedLabel;
    }
}
