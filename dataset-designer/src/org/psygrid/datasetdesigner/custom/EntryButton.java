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

package org.psygrid.datasetdesigner.custom;

import java.awt.Dimension;

import javax.swing.JButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.dnd.*;

import java.awt.datatransfer.*;

import javax.swing.TransferHandler;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import org.psygrid.datasetdesigner.dnd.StringTransferable;
import org.psygrid.datasetdesigner.utils.IconsHelper;

import org.psygrid.datasetdesigner.custom.JMultiLineLabel;

import org.psygrid.datasetdesigner.utils.HelpHelper;

/**
 * A custom button used for the entries - narrative, text etc 
 * Consists of a text header, help button and a draggable button
 * @author pwhelan
 */
public class EntryButton extends JPanel implements DragGestureListener,
							DragSourceListener,
							DragSourceMotionListener {
	
	//dragsource for the button
	private DragSource dragSource;
	 
	//the entry button for the panel
	private JButton entryButton;
	
	//the label for the entry 
	private JTextPane pane;
	private JMultiLineLabel mLabel;
	
	//fixed sizes for the holding panel
	private final static int PANEL_WIDTH = 98;
	private final static int PANEL_HEIGHT = 80;
	
	//fixed sizes for the buttons themselves
	private final static int BUTTON_WIDTH = 50;
	private final static int BUTTON_HEIGHT = 32;

	/**
	 * Constructor - lay out the components
	 * @param text the text header
	 * @param icon the icon to be used for display
	 * @param tooltip the tooltip when hovered over button
	 * @param helpID the help id mapping onto the help file
	 */
	public EntryButton(String text, String icon, String tooltip, String helpID)
	{
		setLayout(new BorderLayout());

		setMaximumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		setMinimumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		
		JPanel centerPanel = new JPanel();
		
		entryButton = new JButton("", IconsHelper.getInstance().getImageIcon(icon));
		entryButton.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		entryButton.setMinimumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		entryButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		entryButton.setToolTipText(tooltip);
		entryButton.setTransferHandler(new TransferHandler("text"));
		
		this.dragSource = DragSource.getDefaultDragSource();
		
		this.dragSource.createDefaultDragGestureRecognizer(
			      entryButton, DnDConstants.ACTION_MOVE, this );
		
		centerPanel.add(entryButton);

		add(centerPanel, BorderLayout.CENTER);	
		
		JPanel labelPanel = new JPanel();
		JLabel label = new JLabel();
		label.setLayout(new BorderLayout());
		pane = new JTextPane();
		pane.setBackground(label.getBackground());
//		 or try making it opaque
		pane.setText(tooltip);
		label.add(pane,BorderLayout.CENTER);
		
		labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		labelPanel.add(HelpHelper.getInstance().getHelpButtonWithID(helpID));
		
		mLabel = new JMultiLineLabel();
		mLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		mLabel.setOpaque(true);
		mLabel.setBackground(Color.red);
		mLabel.setMaxWidth(70);
		mLabel.setText(tooltip);
		labelPanel.add(mLabel);
		add(labelPanel, BorderLayout.NORTH);
	}
	 
	/**
	 * Interface - must be included
	 */
	public void dragDropEnd(DragSourceDropEvent dsde) {
	}

	/**
	 * Interface - must be included
	 */
	public void dragEnter(DragSourceDragEvent dsde) {
	}

	/**
	 * Interface - must be included
	 */
	public void dragExit(DragSourceEvent dse) {
	}

	/**
	 * Interface - must be included
	 */
	public void dragOver(DragSourceDragEvent dsde) {
	}

	/**
	 * Interface - must be included
	 */
	public void dropActionChanged(DragSourceDragEvent dsde) {
	}


	/**
	 * Recognise the drag and start dragging it
	 * @param dge the drag event
	 */
	public void dragGestureRecognized(DragGestureEvent dge) {
		
	      // get the label's text and put it inside a Transferable
	      // Transferable transferable = new StringSelection( DragLabel.this.getText() );
	      Transferable transferable = new StringTransferable( entryButton.getToolTipText() );      

	      // now kick off the drag
//	      try {
		// initial cursor, transferrable, dsource listener      
//	    	  		  dge.startDrag(DragSource.DefaultCopyDrop,
//	    			  transferable,
//	    			  this);
//		
//	      }catch( InvalidDnDOperationException idoe ) {
//	    	  idoe.printStackTrace();
//	    	  System.err.println( idoe );
//	    	  setCursor(DragSource.DefaultCopyNoDrop);
//	      }
		
		try {
			dragSource.startDrag(dge, 
								 DragSource.DefaultCopyDrop, 
								 transferable,
								 this);
	   }catch( InvalidDnDOperationException idoe ) {
		   idoe.printStackTrace();  
		   System.err.println( idoe );
	   }
	}

	/**
	 * Interface - must be included
	 */
	public void dragMouseMoved(DragSourceDragEvent dsde) {
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		entryButton.setEnabled(enabled);
		pane.setEnabled(enabled);
		mLabel.setEnabled(enabled);
	}
}