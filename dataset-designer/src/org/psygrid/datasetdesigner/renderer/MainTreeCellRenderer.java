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
package org.psygrid.datasetdesigner.renderer;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.psygrid.data.model.hibernate.*;
import org.psygrid.datasetdesigner.model.DELStudySet;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.model.DSDocumentOccurrence;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.DummyDocument;
import org.psygrid.datasetdesigner.utils.IconsHelper;
import org.psygrid.datasetdesigner.utils.Utils;
import org.psygrid.datasetdesigner.utils.ElementUtility;

public class MainTreeCellRenderer extends DefaultTreeCellRenderer {
	
	/**
	 * A default tree cell renderer that will then 
	 * make changes to the components 
	 */
	private TreeCellRenderer defaultRenderer;
	
	/**
	 * Constructor - empty
	 */
	public MainTreeCellRenderer() {
		
	}
	
	/**
	 * Constructor 
	 * @param defaultRenderer the default cell renderer
	 */
	public MainTreeCellRenderer(TreeCellRenderer defaultRenderer) {
		this.defaultRenderer = defaultRenderer; 
	}

	/**
	 * If a default renderer isn't set, then use the DefaultTreeCellRenderer
	 * Otherwise, use the default renderer and just update the text, icon 
	 * and tooltip
	 * 
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		Icon icon = null;
		
		if (value != null && !(value instanceof String)) {
			value = ((DefaultMutableTreeNode)value).getUserObject();
			
			if (value instanceof DummyDocument) {
				//Dummy documents should be invisible, so their sole entry should be displayed instead.
				if (((DummyDocument)value).numEntries() > 0) {
					value = Utils.getMainEntry((DummyDocument)value);
				}
			} 

			if (value instanceof DELStudySet) {
				value = ((StudyDataSet)value).getDs().getName();
				icon = IconsHelper.getInstance().getImageIcon("del.png");
			}
		
			if (value instanceof StudyDataSet) {
				if (expanded) {
					if (((StudyDataSet)value).getDs().isPublished()) {
						icon = IconsHelper.getInstance().getImageIcon("study_publishedopen.png");
					} else {
						icon = IconsHelper.getInstance().getImageIcon("folder_green_open.png");
					}
					value = ((StudyDataSet)value).getDs().getName();
				} else {
					if (((StudyDataSet)value).getDs().isPublished()) {
						icon = IconsHelper.getInstance().getImageIcon("study_published.png");
					} else {
						icon = IconsHelper.getInstance().getImageIcon("folder_green.png");
					}
					value = ((StudyDataSet)value).getDs().getName();
				}
			}
			else if (value instanceof DataSet) {
				value = ((DataSet)value).getName();
				
				if (expanded) {
					if (((DataSet)value).isPublished()) {
						icon = IconsHelper.getInstance().getImageIcon("study_published.png");
					} else {
						icon = IconsHelper.getInstance().getImageIcon("folder_green_open.png");
						
					}
					//icon = IconsHelper.getInstance().getImageIcon("folder_green_open.png");
				} else {
					//show in chooser
					if (((DataSet)value).isPublished()) {
						icon = IconsHelper.getInstance().getImageIcon("study_published.png"); 
					}
				}
			} else if (value instanceof Document) {
				String iconString = "Copy16.png";
				
				if (ElementUtility.isDocumentLocked((Document)value)) {
					iconString = "document_locked.png";
				}

				if (DocTreeModel.getInstance().getDELDataset() != null && ((Document)value).getMyDataSet().equals(DocTreeModel.getInstance().getDELDataset().getDs())) {
					if (((Document)value).getIsRevisionCandidate()) {	//is from DEL and has been edited
						if (((Document)value).getLSID() == null) {
							//Is not currently in the DEL and has entries which can be added too
							iconString = "Copy16_new.png";
						}
						else {
							iconString = "Copy16_del_edited.png";
						}
					}
					else if (((Document)value).getLSID() != null) {	//is from DEL
						iconString = "Copy16_del.png";
					}
					else {
						iconString = "Copy16_new.png";	//Is part of the DEL view but has not been added to the DEL yet
					}
				}
				else if (((Document)value).getLSID() != null) {	//is from DEL (although not in DEL view)
					iconString = "Copy16_del.png";
				}
				
				value = ((Document)value).getName();
				icon = IconsHelper.getInstance().getImageIcon(iconString);
			} else if (value instanceof TextEntry) {
				String iconString = "textentry16"+getIconType((Entry)value);
				value = ((Entry)value).getName();
				icon = IconsHelper.getInstance().getImageIcon(iconString);
			} else if (value instanceof LongTextEntry) {
				icon = IconsHelper.getInstance().getImageIcon("longtextentry16"+getIconType((Entry)value));
				value = ((Entry)value).getName();
			} else if (value instanceof CompositeEntry) {
				icon = IconsHelper.getInstance().getImageIcon("compositeentry16"+getIconType((Entry)value));
				value = ((Entry)value).getName();
			} else if (value instanceof NarrativeEntry) {
				icon = IconsHelper.getInstance().getImageIcon("narrativeentry16"+getIconType((Entry)value));
				value = ((Entry)value).getName();
			} else if (value instanceof DateEntry) {
				icon = IconsHelper.getInstance().getImageIcon("dateentry16"+getIconType((Entry)value));
				value = ((Entry)value).getName();
			} else if (value instanceof BooleanEntry) {
				icon = IconsHelper.getInstance().getImageIcon("booleanentry16"+getIconType((Entry)value));
				value = ((Entry)value).getName();
			} else if (value instanceof OptionEntry) {
				icon = IconsHelper.getInstance().getImageIcon("optionentry16"+getIconType((Entry)value));
				value = ((Entry)value).getName();
			} else if (value instanceof NumericEntry) {
				icon = IconsHelper.getInstance().getImageIcon("numericentry16"+getIconType((Entry)value));
				value = ((Entry)value).getName();
			} else if (value instanceof DerivedEntry) {
				icon = IconsHelper.getInstance().getImageIcon("derivedentry16"+getIconType((Entry)value));
				value = ((Entry)value).getName();
			} else if (value instanceof IntegerEntry) {
				icon = IconsHelper.getInstance().getImageIcon("integerentry16"+getIconType((Entry)value));
				value = ((Entry)value).getName();
			} else if (value instanceof CompositeEntry) {
				icon = IconsHelper.getInstance().getImageIcon("compositeentry16"+getIconType((Entry)value));
				value = ((Entry)value).getName();
			} else if (value instanceof ExternalDerivedEntry) {
				icon = IconsHelper.getInstance().getImageIcon("externalderivedentry16"+getIconType((Entry)value));
				value = ((Entry)value).getName();
			}

			if (value instanceof DocumentGroup) {
				if (expanded) {
					value = ((DocumentGroup)value).getName();
					icon = IconsHelper.getInstance().getImageIcon("folder_open.png");
				}  else {
					value = ((DocumentGroup)value).getName();
					icon = IconsHelper.getInstance().getImageIcon("folder.png");
				}
			}

			if (value instanceof DocumentOccurrence) {
				value = ((DocumentOccurrence)value).getDisplayText();
				icon = IconsHelper.getInstance().getImageIcon("Copy16.png");
			}

			if (value instanceof DSDocumentOccurrence) {
				value = ((DSDocumentOccurrence)value).getDocument().getName() + " - " + ((DSDocumentOccurrence)value).getDocOccurrence().getDisplayText();
				icon = IconsHelper.getInstance().getImageIcon("Copy16.png");
			}
		}
		
		Component comp;

		//in case, we're using a constructor that hasn't set the default renderer
		if (defaultRenderer != null) {
			comp = defaultRenderer.getTreeCellRendererComponent(tree, value,
					selected, expanded, leaf, row, hasFocus);
		} else {
			comp = super.getTreeCellRendererComponent(tree, value,
					selected, expanded, leaf, row, hasFocus);
		}
		
		/* 
		 * Just in case it's not a JLabel 
		 */
		if (comp instanceof JLabel) {
			((JLabel)comp).setIcon(icon);
			if (value != null) {
				setText(value.toString());
			}
		}
		
		if (comp instanceof JComponent) {
			JComponent jcomp = (JComponent) comp;
			if (value != null) {
				jcomp.setToolTipText(value.toString());
			}
		}


		return comp;
	}

	/**
	 * Get the type of the icon 
	 * @param curValue the current value to find the type of
	 * @return the extension of the image file used to represent this entry
	 */
	private String getIconType(Entry curValue) {
		String type = ".png";	//Default filename

		if (DocTreeModel.getInstance().getDELDataset() != null && curValue.getMyDataSet().equals(DocTreeModel.getInstance().getDELDataset().getDs())) {
			if (curValue.getIsRevisionCandidate() && curValue.getLSID() != null) {	//is from DEL and has been edited
				type = "_del_edited.png";
			}
			else if (curValue.getLSID() != null) {	//is from DEL
				type = "_del.png";
			}
			else {
				type = "_new.png";	//Is part of the DEL view but has not been added to the DEL yet
			}
		}
		else if (curValue.getLSID() != null) {	//is from DEL (although not in DEL view)
			type = "_del.png";
		}
		return type;
	}
}