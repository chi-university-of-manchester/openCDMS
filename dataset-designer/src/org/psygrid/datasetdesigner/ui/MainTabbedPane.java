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

package org.psygrid.datasetdesigner.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.psygrid.data.model.hibernate.Document;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.model.DELStudySet;
import org.psygrid.datasetdesigner.ui.dataelementfacilities.DataElementClientInitializer;

/**
 * A JTabbedPane that contains the document designer panels in the DSD
 * @author pwhelan
 */
public class MainTabbedPane extends JTabbedPane implements MouseListener {
	
	/**
	 * the currently active doc panel
	 */
	private DocumentPanel docPanel;

	/**
	 * preferred size of the doc panel pane
	 */
	private Dimension preferredCompSize;

	/**
	 * main frame of the application
	 */
	private MainFrame frame;

	/**
	 * The data element client initializer
	 */
	private DataElementClientInitializer delInitializer;

	/**
	 * Constructor - set the main window & add listeners 
	 * @param frame the main window of the application
	 */
	public MainTabbedPane(MainFrame frame, DataElementClientInitializer delInitializer) {
		this.frame = frame;
		addMouseListener(this);
		addChangeListener(new StateChangedListener());
		this.delInitializer = delInitializer;
	}

	/**
	 * Open a new tab with the document
	 * Check if the document is already open; if not, open it
	 * @param document - the document to place in the newly opened document panel
	 */
	public void openTab(Document document) {
		/*
		 * Get whether the document is part of the
		 * data element library view, based on the
		 * currently active ds.
		 */
		boolean isDEL = false;
		if (DatasetController.getInstance().getActiveDs() instanceof DELStudySet) {
			isDEL = true;
		}

		DocumentPanel docToOpenPanel = getPanelForDocument(document); 
		
		if (docToOpenPanel == null) {
			docToOpenPanel = new DocumentPanel(frame, document, isDEL);
			docToOpenPanel.setPreferredCenterCompSize(preferredCompSize);
			addTab(document.getName(), docToOpenPanel);
		}

		setSelectedComponent(docToOpenPanel);
		DatasetController.getInstance().setActiveDocument(document);
		
		if (DatasetController.getInstance().getActiveDs().isReadOnly()) {
			frame.getMainEntryPanel().setButtonsEnabled(false);
		} else {
			frame.getMainEntryPanel().setButtonsEnabled(((Document)document).getIsEditable());
		}
	}

	/**
	 * Return the main window of the application
	 * @return JFrame the main window
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Refresh the size of the document panel currently open
	 * @param preferredCompSize the size to be set to the document panel
	 */
	public void setPreferredSizeForCenterComp(Dimension preferredCompSize) {
		this.preferredCompSize = preferredCompSize;
		
		if (getCurrentPanel() != null) 
		{
			getCurrentPanel().updateCenterPanel(preferredCompSize);
		} else {
			this.setPreferredSize(preferredCompSize);
		}
		
	}

	/**
	 * Find the Document Panel for this IDocument
	 * @param doc the document to find the panel for
	 * @return DocumentPanel the found DocumentPanel
	 */
	public DocumentPanel getPanelForDocument(Document doc){
		int tabCount = getTabCount();
		for (int i=0; i<tabCount; i++) {
			if (getComponentAt(i) instanceof DocumentPanel) {
				DocumentPanel curPanel = (DocumentPanel)getComponentAt(i);
				if (curPanel.getDocument().equals(doc)){
					return curPanel;
				}
			}
		}
		
		return null;
	}

	/**
	 * Get the Currently open docPanel
	 * @return DocumentPanel The currently open DocumentPanel
	 */
	public DocumentPanel getCurrentPanel() {
		return docPanel;
	}

	/**
	 * Close the tab containing this document 
	 * and remove it from the stored hashmap
	 * @param document the document to close
	 */
	public void closeTab(Document document) {
		remove(getPanelForDocument(document));
	}
	
	/**
	 * Close the tab containing this DocumentPanel
	 * @param docPanel the panel to close
	 */
	public void closeAll() {
		removeAll();
	}

	private class StateChangedListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			try {
				MainFrame main = frame;
				MainTree tree = main.getMainTree();
				boolean isDEL = ((DocumentPanel)getSelectedComponent()).isDEL();
				tree.setIsDEL(isDEL);
				Document doc = (Document)((DocumentPanel)getSelectedComponent()).getDocument();
				frame.getMainEntryPanel().setButtonsEnabled(doc.getIsEditable());
				DatasetController.getInstance().setActiveDocument(doc);
			} catch (NullPointerException ex) {
				DatasetController.getInstance().setActiveDocument(null);
			}
		}
	}

	/**
	 * Add the table with this title and component to the tabbedpane
	 * @param title the name to be displayed for this panel
	 * @param component the component to put in the panel
	 */
	public void addTab(String title, Component component) {
		this.addTab(title, component, null);
	}

	/**
	 * Add the table with this title and component to the tabbedpane
	 * @param title the name to be displayed for this panel
	 * @param component the component to put in the panel
	 * @param extraIcon the icon to display in the header
	 */
	public void addTab(String title, Component component, Icon extraIcon) {
		super.addTab(title, new CloseTabIcon(extraIcon), component);
	}

	/**
	 * Close the tabe if the mouse is clicked on the 'x'
	 * @param e the MouseEvent that a click was detected
	 */
	public void mouseClicked(MouseEvent e) {
		int tabNumber=getUI().tabForCoordinate(this, e.getX(), e.getY());
		if (tabNumber < 0) return;
		Rectangle rect=((CloseTabIcon)getIconAt(tabNumber)).getBounds();
		if (rect.contains(e.getX(), e.getY())) {
			//the tab is being closed
			closeTab(((DocumentPanel)getSelectedComponent()).getDocument());
		}
	}

	/**
	 * Required to implement with MouseListener interface - not overridden
	 * @param e the calling MouseEvent
	 */
	public void mouseEntered(MouseEvent e) {}

	/**
	 * Required to implement with MouseListener interface - not overridden
	 * @param e the calling MouseEvent
	 */
	public void mouseExited(MouseEvent e) {}

	/**
	 * Required to implement with MouseListener interface - not overridden
	 * @param e the calling MouseEvent
	 */
	public void mousePressed(MouseEvent e) {}

	/**
	 * Required to implement with MouseListener interface - not overridden
	 * @param e the calling MouseEvent
	 */
	public void mouseReleased(MouseEvent e) {}


	/**
	 * Get the DataElementClientInitializer
	 * @return the DataElementClientInitializer
	 */
	public DataElementClientInitializer getDelInitializer() {
		return delInitializer;
	}

	/**
	 * The class which generates the 'X' icon for the tabs. The constructor
	 * accepts an icon which is extra to the 'X' icon, so you can have tabs
	 * like in JBuilder. This value is null if no extra icon is required.
	 */
	class CloseTabIcon implements Icon {
		private int xpos;
		private int ypos;
		private int width;
		private int height;
		private Icon fileIcon;


		/**
		 * 
		 * @param fileIcon the icon to show in the tabbed panel
		 */
		public CloseTabIcon(Icon fileIcon) {
			this.fileIcon=fileIcon;
			width=16;
			height=16;
		}

		/**
		 * Paint the icon in the top right of the window
		 */
		public void paintIcon(Component c, Graphics g, int x, int y) {
			this.xpos=x;
			this.ypos=y;

			Color col=g.getColor();

			g.setColor(Color.black);
			int y_p=y+2;
			g.drawLine(x+1, y_p, x+12, y_p);
			g.drawLine(x+1, y_p+13, x+12, y_p+13);
			g.drawLine(x, y_p+1, x, y_p+12);
			g.drawLine(x+13, y_p+1, x+13, y_p+12);
			g.drawLine(x+3, y_p+3, x+10, y_p+10);
			g.drawLine(x+3, y_p+4, x+9, y_p+10);
			g.drawLine(x+4, y_p+3, x+10, y_p+9);
			g.drawLine(x+10, y_p+3, x+3, y_p+10);
			g.drawLine(x+10, y_p+4, x+4, y_p+10);
			g.drawLine(x+9, y_p+3, x+3, y_p+9);
			g.setColor(col);
			if (fileIcon != null) {
				fileIcon.paintIcon(c, g, x+width, y_p);
			}
		}

		/**
		 * Return the width of the icon
		 * @return int the width of the icon
		 */
		public int getIconWidth() {
			return width + (fileIcon != null? fileIcon.getIconWidth() : 0);
		}

		/**
		 * Return the height of the icon
		 * @return int the height of the icon
		 */
		public int getIconHeight() {
			return height;
		}

		/**
		 * Return the bounds of the area to draw on
		 * @return Rectangle contains the bounds
		 */
		public Rectangle getBounds() {
			return new Rectangle(xpos, ypos, width, height);
		}
	}
}