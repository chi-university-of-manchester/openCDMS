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

import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPopupMenu;

import org.psygrid.collection.entry.TogglePopupButtonTrigger;
import org.psygrid.collection.entry.renderer.RendererHelper;

public abstract class AbstractEntryWithButton extends EntryComponent
        implements EntryWithButton {
    
    private JButton popupButton;

    private int popupButtonWidth = 20;
    
    private MouseListener popupHandler;
    
    private JPopupMenu popup;
    
    public AbstractEntryWithButton() {
        initComponents();
    }
    
    @Override
    public void addMouseListener(MouseListener listener) {
        popupButton.addMouseListener(listener);
        super.addMouseListener(listener);
    }
    
    @Override
    public void removeMouseListener(MouseListener listener) {
        popupButton.removeMouseListener(listener);
        super.removeMouseListener(listener);
    }
    
    private void initComponents() {
        popupButton = RendererHelper.getInstance().getNoAnswerButton();
    }
    
    public JPopupMenu getPopup() {
        return popup;
    } 
    
    public void setPopup(JPopupMenu popup) {
        if (this.popup != null) {
            if (this.popup.equals(popup)) {
                return;
            }
            popupButton.removeMouseListener(popupHandler);
        }
        
        this.popup = popup;
        if (popup != null) {
            popupHandler = new TogglePopupButtonTrigger(popup);
            popupButton.addMouseListener(popupHandler);
        }
        else {
            popupHandler = null;
        }
    }

    public final JButton getPopupButton() {
        return popupButton;
    }

  //  public final void setPopupButton(JButton button) {
  //      popupButton = button;
  //  }
    
    protected final int getPopupButtonWidth() {
        return popupButtonWidth;
    }
    
    @Override
    public void setEditable(boolean b) {
        if (!b) {
            popupButton.setEnabled(false);
        }
        else {
            popupButton.setEnabled(isEnabled());
        }
        super.setEditable(b);
    }
    
    @Override
    public void setEnabled(boolean b, boolean isStandardCode) {
        popupButton.setEnabled(b);
        super.setEnabled(b, isStandardCode);
    }
    
    public boolean getEnabled() {
    	return popupButton.isEnabled();
    }
}
