package org.psygrid.collection.entry.ui;

import javax.swing.WindowConstants;

import org.psygrid.collection.entry.Application;
import org.psygrid.data.model.hibernate.Record;

public class ERCConsentDialog extends ConsentDialog {
	
    public ERCConsentDialog(Application parent, 
            Record record) {
        super(parent, record);
        cancelButton.setEnabled(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

}
