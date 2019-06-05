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


package org.psygrid.collection.entry.chooser;

import java.awt.Frame;
import java.io.File;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.psygrid.collection.entry.externaldocparser.SelectedFileInfo;
import org.psygrid.collection.entry.ui.URLEntryDialog;

public class ImportFileChooser {
	
	//This will return either a URL or a File
	
    private SelectedFileInfo selectedFileInfo = SelectedFileInfo.noSelection;
	private File file = null;
	private URL theURL = null;
	private Frame dlgOwnerFrame = null;
	
	private boolean queryForWebDocuments = false; //TODO: This is a feature suppressor. It should be removed in future.
	
	public ImportFileChooser(Frame frame){
    	super();
    	dlgOwnerFrame = frame;
    }
    
    private int queryForDocumentType(){
	    Object[] options = {Messages.getString("ImportFileChooser.choice_local"), Messages.getString("ImportFileChooser.choice_web"), Messages.getString("ImportFileChooser.choice_cancel")};
		int choice = JOptionPane.showOptionDialog(null,
		Messages.getString("ImportFileChooser.chooseImportSourceQuery"),
		Messages.getString("ImportFileChooser.chooseImportSourceTitle"),
		JOptionPane.YES_NO_CANCEL_OPTION,
		JOptionPane.QUESTION_MESSAGE,
		null,
		options,
		options[2]);
		return choice;
    }
    
    private void launchLocalFileChooser() throws NoFileSelectedException{
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);
		
		if(returnVal == JFileChooser.APPROVE_OPTION){
			file = fc.getSelectedFile();
			theURL = null;
			selectedFileInfo = SelectedFileInfo.local;
		}
		else if(returnVal == JFileChooser.CANCEL_OPTION)
			throw new NoFileSelectedException("No local file was selected.");
    }
    
    private void launchRemoteFileChooser() throws NoFileSelectedException{
    	
		class URLRetrieval implements URLEntryDialog.Callable {
			private URL chosenURL = null;

			public void call(URL chosenURL) {
				this.chosenURL = chosenURL;
			}
			
			public URL getURL(){
				return chosenURL;
			}
		}
		
		URLRetrieval urlRetriever = new URLRetrieval();
		new URLEntryDialog(dlgOwnerFrame, urlRetriever);
		theURL = urlRetriever.getURL();
		file = null;
		if(theURL != null)
			selectedFileInfo = SelectedFileInfo.remote;
		else
			throw new NoFileSelectedException("No url was specified.");
    }
    
	public void queryForImportFile() throws NoFileSelectedException {
		
		if(queryForWebDocuments){
			int result = queryForDocumentType();
			switch(result){
				case JOptionPane.CANCEL_OPTION: //Cancel
				{
					selectedFileInfo = SelectedFileInfo.noSelection;
					break;
				}
				case JOptionPane.YES_OPTION: //Local
				{
					launchLocalFileChooser();
					break;	
				}
				case JOptionPane.NO_OPTION: //Remote
				{
					launchRemoteFileChooser();
					break;
				}
				default: //This will not happen
					break;
			}
		}
		else { //simply launch the local file chooser.
			launchLocalFileChooser();
		}
	}
	
	public URL getSelectedURL(){
		return theURL;
	}
	
	public SelectedFileInfo getSelectedFileInfo(){
		return selectedFileInfo;
	}
	
	public File getSelectedFile(){
		return file;
	}
}

