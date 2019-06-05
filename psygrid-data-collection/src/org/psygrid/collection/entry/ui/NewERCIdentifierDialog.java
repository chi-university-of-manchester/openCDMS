package org.psygrid.collection.entry.ui;

import javax.swing.WindowConstants;

import org.psygrid.collection.entry.Application;
import org.psygrid.data.model.hibernate.DataSet;

public class NewERCIdentifierDialog extends NewIdentifierDialog {
	
    public NewERCIdentifierDialog(Application parent, DataSet dataSet) throws IdentifierPanelException {
        super(parent, dataSet, true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        /*
        addWindowListener(new WindowAdapter() {
    	    public void windowClosing(WindowEvent we) {
    	        we.getComponent().setVisible(true);     
    	    }
    	});
    	*/
        
    }

}
