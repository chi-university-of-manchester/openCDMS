package org.psygrid.collection.entry.editexternalid;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.common.strings.IStringEditor;
import org.psygrid.common.strings.StringEditorFactory;
import org.psygrid.data.model.hibernate.DataSet;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import org.psygrid.data.model.hibernate.Record;

public class EditExternalIdentifierPanel extends JPanel {
	
	
	public enum actionCommands{
		SAVE,
		CANCEL
	}
	
	private class EditableSubsetComponentSet{
		
		private final JLabel label;
		private final JTextField subsetEditField;
		
		public EditableSubsetComponentSet(JLabel l, JTextField f){
			label = l;
			subsetEditField = f;
		}

		public JLabel getLabel() {
			return label;
		}

		public JTextField getSubsetEditField() {
			return subsetEditField;
		}
		
	};
	
	private JLabel originalIdLabel;
	private JTextField originalIdTextField;
	private final Record record;
	private final ActionListener dialog;
	private JButton displayIdentifierButton;
	private JButton saveButton;
	private JButton cancelButton;
	private JTextField newIdentifierTextField;
	private String newIdentifierText = null;
	protected DefaultFormBuilder builder;
	private Map<String, EditableSubsetComponentSet> substringEditFieldMap = new HashMap<String, EditableSubsetComponentSet>();
	private final IStringEditor extIdEditor;
	
	
	public EditExternalIdentifierPanel(Record rec, ActionListener d){
		dialog = d;
		record = rec;
		extIdEditor = StringEditorFactory.generateStringEditor(PersistenceManager.getInstance().getExternalIdMap().get(record.getIdentifier().getIdentifier()), ((DataSet)rec.getDataSet()).getExternalIdEditableSubstringMap(), ((DataSet)rec.getDataSet()).getExternalIdEditableSubstringValidationMap());
		init();
	}
	
	protected String getNewIdentifier(){
		return extIdEditor.getNewString();
	}
	
	private void init(){
		initBuilder();
		//init components
		initComponents();
		//init handling
		this.initHandlers();
		//init build
		this.build();
	}
	
	protected void initBuilder() {
		builder = new DefaultFormBuilder(new FormLayout(
				"50dlu, 3dlu, 125dlu:grow, 3dlu, 125dlu"), this); //$NON-NLS-1$
		builder.setDefaultDialogBorder();
	}
	
	protected void initComponents(){
		
		this.originalIdLabel = new JLabel("Original identifier: ");
		
		DataSet ds = (DataSet)record.getDataSet();
		
		String externalId = PersistenceManager.getInstance().getExternalIdMap().get(record.getIdentifier().getIdentifier());
		this.originalIdTextField = new JTextField(externalId);
		originalIdTextField.setEnabled(false);
		
		Map<String, String> regexMap = ds.getExternalIdEditableSubstringMap();
		
		Set<String> keys = regexMap.keySet();
		for(String key : keys){
			JLabel l = new JLabel("Edit " + key);
			JTextField editField = new JTextField(regexMap.get(key));
			this.substringEditFieldMap.put(key, new EditableSubsetComponentSet(l, editField));
		}
		
		displayIdentifierButton = new JButton("Display new identifier");
		displayIdentifierButton.setEnabled(true);
		newIdentifierTextField = new JTextField();
		newIdentifierTextField.setEditable(true);
		
		this.saveButton = new JButton("Save");
		this.cancelButton = new JButton("Cancel");
		
	}
	
	protected void build(){
		//assume components are not null.
		//originalIdLabel | originalIdTextField
		//all the substring edit labels | fields
		// displayIdentifiersButton | newIdentifierTextField
		// save | cancel
		
		builder.append(originalIdLabel);
		builder.append(originalIdTextField, 2);
		
		Set<String> keys = this.substringEditFieldMap.keySet();
		
		for(String key: keys){
			EditableSubsetComponentSet s = substringEditFieldMap.get(key);
			builder.append(s.getLabel());
			builder.append(s.getSubsetEditField(), 2);
		}
		
		builder.append(displayIdentifierButton);
		builder.append(newIdentifierTextField, 2);
		
		JPanel saveCancelButtonBar = ButtonBarFactory.buildOKCancelBar(this.saveButton, this.cancelButton);
		saveButton.setText("Save");
		builder.append(saveCancelButtonBar, builder.getColumnCount());
		
	}
	
	protected void initHandlers(){
		
		displayIdentifierButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				//Here we want to grab user's input text and feed it into the string editor class.
				//See if we can get a new identifier from it.
				boolean success = true;
				Set<String> keys = substringEditFieldMap.keySet();
				for(String key:keys){
					EditableSubsetComponentSet s = substringEditFieldMap.get(key);
					String text = s.getSubsetEditField().getText();
					success = extIdEditor.setNewSubset(key, text);
				}
				
				if(success){
					newIdentifierTextField.setText(extIdEditor.getNewString());
					saveButton.setEnabled(true);
				}else{
					saveButton.setEnabled(false);
				}
				
				
			}
			
		});
		
		
		
		cancelButton.setActionCommand(actionCommands.CANCEL.toString());
		cancelButton.addActionListener(dialog);
		saveButton.setActionCommand(actionCommands.SAVE.toString());
		saveButton.addActionListener(dialog);
		
	}

}
