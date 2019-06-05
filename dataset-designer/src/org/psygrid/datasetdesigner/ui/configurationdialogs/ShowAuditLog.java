package org.psygrid.datasetdesigner.ui.configurationdialogs;

import java.awt.event.ActionListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;

import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.psygrid.data.model.hibernate.AuditableChange;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Element;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;

import org.psygrid.data.model.hibernate.AuditLog;


/**
 * Audit log dialog showing the history of the element provided
 * @author pwhelan
 */

public class ShowAuditLog  extends JDialog implements ActionListener{

	/**
	 * Prefix for referencing in the properties file
	 */
	private final static String STRING_PREFIX = "org.psygrid.datasetdesigner.ui.configurationdialogs.";
	
	/**
	 * The owner of this dialog
	 */
	private Window parentWindow;
	
	/**
	 * The element to which this audit log belongs
	 */
	private Element element;
	
	
	/**
	 * Ok
	 */
	private JButton okButton;
		
	public ShowAuditLog(JFrame parentFrame, Element element) {
		super(parentFrame);
		init(parentFrame, element);
	}
	
	
	/**
	 * Initialise the window; lay out the components etc
	 * @param parentWindow the owner window
	 * @param element the element that is being changed
	 * @param action the type of action taking place: edit, delete etc
	 */
	private void init(Window parentWindow, Element element) {
		setTitle(PropertiesHelper.getStringFor(STRING_PREFIX + "showauditlog"));
		this.parentWindow = parentWindow;
		this.element = element;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(buildMainPanel(), BorderLayout.CENTER);
		getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
	
	public JScrollPane buildMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		if (element instanceof DataSet) {
			addElementLogToPanel(element, mainPanel);
			//then iterate documents
			for (int i=0; i<((DataSet)element).numDocuments(); i++) {
				addElementLogToPanel(((DataSet)element).getDocument(i), mainPanel);
				//and iterate doc entries
				for (int j=0; j<((DataSet)element).getDocument(i).numEntries(); j++) {
					addElementLogToPanel(((DataSet)element).getDocument(i).getEntry(j), mainPanel);
				}
			}
		}
		
		JScrollPane scrollPane = new JScrollPane(mainPanel);
		scrollPane.setPreferredSize(new Dimension(200, 500));
		
		return new JScrollPane(mainPanel);
	}
	
	public void addElementLogToPanel(Element element, JPanel mainPanel) {
		AuditLog auditLog = element.getAuditLog();
		mainPanel.add(new JLabel("Element: " + element.getName() + " version: " + element.getAutoVersionNo()));
		if (auditLog != null) {
			for (AuditableChange auditChange: auditLog.getAuditableChanges()) {
				mainPanel.add(new JLabel("Changed " + auditChange.getComment() + " of type "  + auditChange.getAction() + " at " + auditChange.getTimestamp() + " by " + auditChange.getUser()));
			}
		}
	}
	

	/**
	 * Lay out the button panel containing
	 * the Ok and Cancel buttons
	 * 
	 * @return the configured button panel
	 */
	private JPanel buildButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		buttonPanel.add(okButton);
		return buttonPanel;
	}
	
	public void actionPerformed(ActionEvent aet) {
		if (aet.getSource() == okButton) {
			this.dispose();
		}
	}
	
	
}
