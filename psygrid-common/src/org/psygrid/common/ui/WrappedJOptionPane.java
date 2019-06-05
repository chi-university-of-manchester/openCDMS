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

package org.psygrid.common.ui;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Extension of the JOptionPane class to provide methods to
 * create dialogs that have a fixed width, and the message
 * text wraps to fit into this.
 * 
 * @author Rob Harper
 *
 */
public class WrappedJOptionPane extends JOptionPane {

    private static final long serialVersionUID = 3819233909252898651L;

    int maxCharactersPerLineCount;
    
    WrappedJOptionPane(int maxCharactersPerLineCount) {
        this.maxCharactersPerLineCount = maxCharactersPerLineCount;
    }
    
    @Override
    public int getMaxCharactersPerLineCount() {
        return maxCharactersPerLineCount;
    }
    
    /**
     * Show a message dialog with the message text wrapped at 100 
     * characters wide.
     * 
     * @param parentComponent
     * @param message
     * @param title
     * @param messageType
     */
    public static void showWrappedMessageDialog(
            Component parentComponent, Object message, String title, int messageType){
        JOptionPane pane = new WrappedJOptionPane(100);
        pane.setMessage(message);
        pane.setMessageType(messageType);
        JDialog dialog = pane.createDialog(parentComponent, title);
        dialog.setVisible(true);
    }
    
    /**
     * Show a confirm dialog with the message text wrapped at 100 
     * characters wide.
     * 
     * @param parentComponent
     * @param message
     * @param title
     * @param optionType
     * @param messageType
     * @return
     */
    public static int showWrappedConfirmDialog(
    		Component parentComponent, Object message, String title, int optionType, int messageType){
        JOptionPane pane = new WrappedJOptionPane(100);
        pane.setMessage(message);
        pane.setMessageType(messageType);
        pane.setOptionType(optionType);
        JDialog dialog = pane.createDialog(parentComponent, title);
        dialog.setVisible(true);
        
        Object selectedValue = pane.getValue();
        if(selectedValue == null){
            return CLOSED_OPTION;
        }
        if(selectedValue instanceof Integer){
            return ((Integer)selectedValue).intValue();
        }
        return CLOSED_OPTION;
    }
    
}
