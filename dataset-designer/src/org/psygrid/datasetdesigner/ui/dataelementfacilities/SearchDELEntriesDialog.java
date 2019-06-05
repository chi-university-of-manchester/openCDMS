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
package org.psygrid.datasetdesigner.ui.dataelementfacilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.DocumentStatus;
import org.psygrid.common.ui.WrappedJOptionPane;
import org.psygrid.data.DELServiceFault;
import org.psygrid.data.SearchType;
import org.psygrid.data.model.IDELQueryObject;
import org.psygrid.data.model.hibernate.BasicEntry;
import org.psygrid.data.model.hibernate.CompositeEntry;
import org.psygrid.data.model.hibernate.DataElementContainer;
import org.psygrid.data.model.hibernate.DataElementStatus;
import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.DerivedEntry;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.data.model.hibernate.ElementHistoryItem;
import org.psygrid.data.model.hibernate.ElementMetaData;
import org.psygrid.data.model.hibernate.ElementStatusContainer;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.EntryStatus;
import org.psygrid.data.model.hibernate.ExternalDerivedEntry;
import org.psygrid.data.model.hibernate.HibernateDataElementFactory;
import org.psygrid.data.model.hibernate.LSID;
import org.psygrid.data.model.hibernate.LSIDException;
import org.psygrid.data.model.hibernate.Section;
import org.psygrid.data.model.hibernate.SectionOccurrence;
import org.psygrid.data.model.hibernate.Status;
import org.psygrid.data.model.hibernate.ValidationRule;
import org.psygrid.data.utils.security.NotAuthorisedFault;
import org.psygrid.datasetdesigner.actions.SearchDELEntriesAction;
import org.psygrid.datasetdesigner.controllers.DatasetController;
import org.psygrid.datasetdesigner.model.DELStudySet;
import org.psygrid.datasetdesigner.model.DocTreeModel;
import org.psygrid.datasetdesigner.model.DummyDocument;
import org.psygrid.datasetdesigner.model.StudyDataSet;
import org.psygrid.datasetdesigner.ui.DocumentPanel;
import org.psygrid.datasetdesigner.ui.MainFrame;
import org.psygrid.datasetdesigner.ui.MainTabbedPane;
import org.psygrid.datasetdesigner.ui.configurationdialogs.AddValidationRuleDialog;
import org.psygrid.datasetdesigner.utils.PropertiesHelper;
import org.psygrid.datasetdesigner.utils.SpringUtilities;
import org.psygrid.datasetdesigner.utils.Utils;

@SuppressWarnings("serial")
public class SearchDELEntriesDialog extends JDialog implements ActionListener, ListSelectionListener, DocumentListener {

	private static final Log LOG = LogFactory.getLog(SearchDELEntriesDialog.class);

	private MainTabbedPane mainPane;

	private JPanel buttonPanel;

	private JButton searchButton;
	private JButton cancelButton;
	private JButton	importButton;
	private JButton viewButton;
	private JButton moreButton;
	private JButton stopButton;

	private JLabel	searchResultsLabel;
	private JLabel 	searchTextLabel;
	private JLabel	docFilterLabel;
	private JLabel 	authorityFilterLabel;
	private JLabel 	statusFilterLabel;

	private JComboBox DELTypeList, searchTypeList;
	private JTable	revisionHistoryTable;
	private JTable	resultsTable;
	private JTextField 	searchText;

	private JLabel revisionHistoryLabel;

	//BEGIN ADVANCED SEARCH FACILITIES
	JCheckBox currentRevCheckBox;
	JComboBox authorityComboBox;
	JComboBox statusComboBox;
	JComboBox docComboBox;
	JCheckBox limitByDocCheckBox;
	JCheckBox limitByAuthorityCheckBox;
	JCheckBox limitByStatusCheckBox;
	//END ADVANCED SEARCH FACILITIES

	private SearchDELEntriesAction.SearchType searchType;

	private IDELQueryObject queryObject;

	private Document doc = null;
	private StudyDataSet ds = null;

	static public boolean delConnectionIsInitialised = false;

	public enum StatusType {Pending, Approved}

	private StatusType elementStatus;

	public SearchDELEntriesDialog(MainTabbedPane docPane, StudyDataSet ds, Document document, SearchDELEntriesAction.SearchType searchType, StatusType elementStatus){
		super(docPane.getFrame(), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.searchfor")+" "+searchType);
		mainPane = docPane;
		this.searchType = searchType;
		this.elementStatus = elementStatus;
		this.ds = ds;
		this.doc = document;

		init();
	}

	public SearchDELEntriesDialog(MainTabbedPane docPane, StudyDataSet ds, SearchDELEntriesAction.SearchType searchType){
		super(docPane.getFrame(), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.searchfor")+" "+searchType);
		mainPane = docPane;
		this.searchType = searchType;
		this.ds = ds;
		this.elementStatus = StatusType.Approved;
		init();
	}

	public SearchDELEntriesDialog(MainTabbedPane docPane, Document document, SearchDELEntriesAction.SearchType searchType){
		super(docPane.getFrame(), PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.searchfor")+" "+searchType);
		mainPane = docPane;
		this.searchType = searchType;
		this.doc = document;
		this.ds = DatasetController.getInstance().getActiveDs();
		this.elementStatus = StatusType.Approved;
		init();
	}

	private void init() {
		getContentPane().setLayout(new BorderLayout());
		JPanel holderPanel = new JPanel();
		holderPanel.setLayout(new BorderLayout());
		holderPanel.add(buildSearchCriteriaPanel(), BorderLayout.NORTH);
		holderPanel.add(buildSearchResultsPanel(), BorderLayout.CENTER);
		getContentPane().add(new JScrollPane(holderPanel), BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);  
		setVisible(true);
	}

	private JPanel buildSearchResultsPanel(){
		JPanel resultsPanel = new JPanel();
		resultsPanel.setLayout(new BoxLayout(resultsPanel,BoxLayout.Y_AXIS));

		JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		textPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.searchresults")));

		searchResultsLabel = new JLabel();
		textPanel.add(this.searchResultsLabel);
		searchResultsLabel.setText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.none"));

		moreButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.more"));
		textPanel.add(moreButton);
		moreButton.setEnabled(false);
		moreButton.addActionListener(this);

		resultsPanel.add(textPanel);	

		resultsTable = new JTable(new CustomTableModel());
		//substance 4.0 defaults table headers to the left
		((DefaultTableCellRenderer)resultsTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultsTable.getColumnModel().getColumn(0).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.name"));
		resultsTable.getColumnModel().getColumn(1).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.revisioncount"));
		resultsTable.getColumnModel().getColumn(2).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.elementtype"));
		resultsTable.getSelectionModel().addListSelectionListener(this);

		JScrollPane filterColumnScrollPane = new javax.swing.JScrollPane(resultsTable);
		filterColumnScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		filterColumnScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		filterColumnScrollPane.setMaximumSize(new java.awt.Dimension(800, 180));
		filterColumnScrollPane.setMinimumSize(new java.awt.Dimension(400, 160));
		filterColumnScrollPane.setPreferredSize(new java.awt.Dimension(600,180));

		JPanel scrollWrapperLeftAlignmentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		scrollWrapperLeftAlignmentPanel.add(filterColumnScrollPane);

		resultsPanel.add(scrollWrapperLeftAlignmentPanel);


		JPanel textPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		revisionHistoryLabel = new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.revisionhistory"));
		textPanel2.add(revisionHistoryLabel);

		resultsPanel.add(textPanel2);

		this.revisionHistoryTable = new JTable(new CustomRHTableModel());
		//substance 4.0 defaults table headers to the left
		((DefaultTableCellRenderer)revisionHistoryTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		this.revisionHistoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		revisionHistoryTable.getColumnModel().getColumn(0).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.revision"));
		revisionHistoryTable.getColumnModel().getColumn(1).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.description"));
		revisionHistoryTable.getColumnModel().getColumn(2).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.date"));
		revisionHistoryTable.getColumnModel().getColumn(3).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.who"));

		JScrollPane revHistoryScrollPane = new javax.swing.JScrollPane(revisionHistoryTable);
		revHistoryScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		revHistoryScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		revHistoryScrollPane.setMaximumSize(new java.awt.Dimension(800, 140));
		revHistoryScrollPane.setMinimumSize(new java.awt.Dimension(400, 140));
		revHistoryScrollPane.setPreferredSize(new java.awt.Dimension(600,140));

		JPanel revHistoryLeftAlignmentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		revHistoryLeftAlignmentPanel.add(revHistoryScrollPane);

		resultsPanel.add(revHistoryLeftAlignmentPanel);

		JPanel buttonPanel = new JPanel(new FlowLayout());
		viewButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.view"));
		viewButton.setEnabled(false);
		viewButton.addActionListener(this);
		importButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.import"));
		importButton.addActionListener(this);
		importButton.setEnabled(false);

		cancelButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.close"));
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);
		cancelButton.setVisible(true);

		buttonPanel.add(viewButton);
		buttonPanel.add(importButton);

		resultsPanel.add(buttonPanel);

		resultsPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.searchresults")));

		return resultsPanel;
	}

	private class CustomComboBoxModel extends DefaultComboBoxModel{

		Object[] items = null;

		public CustomComboBoxModel(Object[] items){
			super(items);
			this.items = items;
		}


		public Document getSelectedDocument(){
			Document theDoc = null;
			Object obj = this.getSelectedItem();
			int objLength = items.length;
			for(int i = 0; i < objLength; i++){
				if(((Document)items[i]).getName() == obj){
					theDoc = (Document)items[i];
					break;
				}
			}
			return theDoc;
		}


		public String getElementAt(int index){
			return ((Document)super.getElementAt(index)).getName();	
		}
	}

	@SuppressWarnings("serial")
	private class CustomTableModel extends DefaultTableModel{
		private List<DataElementContainer> elements; 

		public CustomTableModel() {
			elements = new ArrayList<DataElementContainer>();
		}

		@Override
		public void addRow(Vector elem) {
			elements.add((DataElementContainer)elem.get(0));
			fireTableDataChanged();
		}

		public void	clearContent(){
			elements.clear();
		}

		public DataElementContainer getRowObject(int row){
			return elements.get(row);
		}

		@Override
		public void removeRow(int row) {
			elements.remove(row);
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			if (elements != null) {
				return elements.size();
			}
			return 0;
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}

		@Override
		public Object getValueAt(int row, int column) {
			DataElementContainer elem = elements.get(row);
			Object returnObj;
			switch(column){
			case 0:
			{
				returnObj = elem.getElementName();
			}
			break;
			case 1:
			{
				LSID id = elem.getElementLSIDObject();
				Integer revisionNumber = Integer.valueOf(id.getRevisionId());
				returnObj = revisionNumber;
			}
			break;
			case 2:
			{
				returnObj = elem.getElementLSIDObject().getNamespaceId();
				String namespaceId = elem.getElementLSIDObject().getNamespaceId();
				int index = namespaceId.lastIndexOf(".");
				returnObj = namespaceId.substring(index+1);
				if (returnObj.equals("Document")) {
					returnObj = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.document");
				}
			}
			break;
			default:
				returnObj = elem.getElementName();
			}

			return returnObj;
		}
	}

	private class CustomRHTableModel extends DefaultTableModel{
		private List<ElementHistoryItem> elements; 

		public CustomRHTableModel() {
			elements = new ArrayList<ElementHistoryItem>();
		}

		public void	clearContent(){
			elements.clear();
		}

		public ElementHistoryItem getRowObject(int row){
			return elements.get(row);
		}

		@Override
		public void addRow(Vector elem) {
			elements.add((ElementHistoryItem)elem.get(0));
			fireTableDataChanged();
		}

		@Override
		public void removeRow(int row) {
			elements.remove(row);
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			if (elements != null) {
				return elements.size();
			}
			return 0;
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}

		@Override
		public Object getValueAt(int row, int column) {
			revisionHistoryTable.getColumnModel().getColumn(0).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.revision"));
			revisionHistoryTable.getColumnModel().getColumn(1).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.description"));
			revisionHistoryTable.getColumnModel().getColumn(2).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.date"));
			revisionHistoryTable.getColumnModel().getColumn(3).setHeaderValue(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.who"));

			ElementHistoryItem elem = elements.get(row);
			Object returnObj;
			switch(column){
			case 0:
			{
				String revisionString = null;
				try {
					revisionString = LSID.valueOf(elem.getLsid()).getRevisionId();
				} catch (LSIDException e) {
					//This won't happen!
				}
				Integer revision = Integer.valueOf(revisionString);
				returnObj = revision;
			}
			break;
			case 1:
			{
				returnObj = elem.getDescription();
			}
			break;
			case 2:
			{
				returnObj = elem.getHistoryEventDate();
			}
			break;
			default:
				returnObj = elem.getWho();
			}

			return returnObj;
		}
	}

	private JPanel buildButtonPanel() {
		JPanel searchCriteriaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		searchCriteriaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		searchButton = new JButton(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search"));
		searchButton.addActionListener(this);
		searchCriteriaPanel.add(searchButton);

		return searchCriteriaPanel;
	}

	private String getDocFilterString(){

		String returnString = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.none");

		//If the doc check box is checked,
		//then get the text of the current selected item in the 
		//document list box.
		if(limitByDocCheckBox != null && limitByDocCheckBox.isSelected()){
			returnString = (String)this.docComboBox.getSelectedItem();
		}
		return returnString;
	}

	private String getAuthorityFilterString(){
		String returnString = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.none");

		if(limitByAuthorityCheckBox != null &&limitByAuthorityCheckBox.isSelected()){
			returnString = (String)this.authorityComboBox.getSelectedItem();
		}
		return returnString;
	}

	private String getStatusFilterString(){
		String returnString = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.none");

		if(limitByStatusCheckBox != null &&limitByStatusCheckBox.isSelected()){
			returnString = ((StatusType)this.statusComboBox.getSelectedItem()).toString();
		}
		return returnString;
	}

	private String getSearchString(){
		String fullString = null;
		String beginning = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.findall")+" ";
		SearchType sType = null;
		String searchTypeString = null;

		//Convert the string to its plural form.
		String elementType = (String)this.DELTypeList.getSelectedItem();

		if (elementType.contains("Entry")) {
			elementType = elementType.replace("Entry", "Entries");
		}
		else if (elementType.equals("All Types")) {
			elementType = "element types";
		} else {
			elementType = elementType.concat("s");
		}
		fullString = beginning.concat(elementType);

		if(this.searchText.getText() != null && this.searchText.getText().length() > 0){
			sType = (SearchType)this.searchTypeList.getSelectedItem();
			if(sType == SearchType.beginsWith) {
				searchTypeString = " "+PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.namedescription")+" "+
				PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.beginwith");
			}
			else if (sType == SearchType.contains) {
				searchTypeString = " "+PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.namedescription")+" "+
				PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.contain");
			}
			else if (sType == SearchType.endsWith) {
				searchTypeString = " "+PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.namedescription")+" "+
				PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.endwith");
			}
			else if (sType == SearchType.exactMatch) {
				searchTypeString = " "+PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.namedescription")+" "+
				PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.exactlymatch");
			}

			fullString = fullString.concat(searchTypeString) + " '" + searchText.getText() + "'.";

		}else{
			fullString = fullString.concat(".                                                                      ");
		}

		return fullString;
	}

	private JPanel buildSearchCriteriaPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));


		JPanel searchCriteriaPanel = new JPanel();
		searchCriteriaPanel.setLayout(new SpringLayout());
		searchCriteriaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		searchCriteriaPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.searchtext")));
		searchText = new JTextField(10);
		searchText.getDocument().addDocumentListener(this);

		searchCriteriaPanel.add(searchText);

		searchCriteriaPanel.add(new JLabel());
		searchCriteriaPanel.add(new JLabel());

		searchCriteriaPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.searchtype")));

		SearchType[] searchTypes = SearchType.values();
		searchTypeList = new JComboBox(searchTypes);
		searchTypeList.setPreferredSize(new Dimension(50, 20));
		searchTypeList.setMaximumSize(new Dimension(50,20));
		searchTypeList.setMinimumSize(new Dimension(50, 20));
		searchTypeList.addActionListener(this);

		searchCriteriaPanel.add(searchTypeList);

		searchCriteriaPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.elementtype")));

		//TODO rename CompositeEntry to Table Entry
		if(this.searchType == SearchDELEntriesAction.SearchType.Entries){
			String[] types = mainPane.getDelInitializer().getTypes();//TODO:DEL - handle failure

			List<String> typeList = Arrays.asList(types);

			List<String> newList = new ArrayList<String>(typeList);
			newList.remove(newList.size()-1);
			newList.remove(newList.size()-1);
			newList.remove(newList.size()-1);
			newList.remove(newList.size()-1); //Remove the last four entries (validation rules)
			newList.remove(3); //BasicEntry
			newList.remove(1); //Document
			newList.remove(0); //DataSet

			//Weed out the DataSet and Document options - these are not appropriate here.
			String[] discriminatedTypes = new String[newList.size()];
			newList.toArray(discriminatedTypes);
			discriminatedTypes[0] = "All Types";

			DELTypeList = new JComboBox(discriminatedTypes);
		} 
		else if(this.searchType == SearchDELEntriesAction.SearchType.Documents){
			String[] discriminatedTypes = new String[1];
			discriminatedTypes[0] = PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.document");
			DELTypeList = new JComboBox(discriminatedTypes);
		} 
		else if(this.searchType == SearchDELEntriesAction.SearchType.ValidationRules){
			String[] discriminatedTypes = new String[5];
			discriminatedTypes[0] = "All Validation Rules";
			discriminatedTypes[1] = "DateValidationRule";
			discriminatedTypes[2] = "NumericValidationRule";
			discriminatedTypes[3] = "IntegerValidationRule";
			discriminatedTypes[4] = "TextValidationRule";
			DELTypeList = new JComboBox(discriminatedTypes);
		}
		//This is only used for searching for elements to approve..
		else if(this.searchType == SearchDELEntriesAction.SearchType.All){	
			String[] types = mainPane.getDelInitializer().getTypes();//TODO:DEL - handle failure

			List<String> typeList = Arrays.asList(types);

			List<String> newList = new ArrayList<String>(typeList);
			newList.remove(0); //Ignore dataset
			newList.set(0, PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.document"));

			//Ignore entries, as these can't be approved individually
			for (int i=13; i > 0; i--) {
				newList.remove(i); 
			}

			newList.add("All Validation Rules");
			String[] discriminatedTypes = new String[newList.size()];
			newList.toArray(discriminatedTypes);
			DELTypeList = new JComboBox(discriminatedTypes);
		}

		DELTypeList.addActionListener(this);
		DELTypeList.setPreferredSize(new Dimension(155, 20));
		DELTypeList.setMinimumSize(new Dimension(155, 20));
		DELTypeList.setMaximumSize(new Dimension(155, 20));
		searchCriteriaPanel.add(DELTypeList);	

		SpringUtilities.makeCompactGrid(searchCriteriaPanel,
				2, 4, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad


		JPanel textPanel = new JPanel();
		textPanel.setLayout(new SpringLayout());
		textPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		textPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.currentsearch")));
		this.searchTextLabel = new JLabel();
		searchTextLabel.setText(this.getSearchString());
		textPanel.add(searchTextLabel);

		//Now add the advanced filtering info.
		int row = 3;
		if(this.searchType == SearchDELEntriesAction.SearchType.Entries
				|| this.searchType == SearchDELEntriesAction.SearchType.All){
			textPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.documentfilter")));
			docFilterLabel = new JLabel();
			docFilterLabel.setText(this.getDocFilterString());
			textPanel.add(docFilterLabel);
			row++;
		}

		authorityFilterLabel = new JLabel();
		if (mainPane.getDelInitializer().getLSIDAuthorities().length > 1) {
			textPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.authorityfilter")));
			authorityFilterLabel.setText(this.getAuthorityFilterString());
			textPanel.add(authorityFilterLabel);
		}
		else {
			textPanel.add(new JLabel(""));
			textPanel.add(new JLabel(""));
		}

		if (DELSecurity.getInstance().canViewPending()) {
			boolean isDEL = false;
			if (ds instanceof DELStudySet) {
				isDEL = true;
			}
			statusFilterLabel = new JLabel();
			textPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.statusfilter")));
			if (isDEL) {
				if (elementStatus != null) {
					statusFilterLabel.setText(elementStatus.toString());
				}
				else {
					statusFilterLabel.setText(this.getStatusFilterString());
				}
			}
			else {
				//Only approved elements can be checked out outside of the DEL view
				statusFilterLabel.setText(StatusType.Approved.toString());
			}
			textPanel.add(statusFilterLabel);

		}
		else {
			textPanel.add(new JLabel(""));
			textPanel.add(new JLabel(""));
		}

		SpringUtilities.makeCompactGrid(textPanel,
				row, 2, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad


		mainPanel.add(textPanel);
		mainPanel.add(searchCriteriaPanel);
		buttonPanel = buildButtonPanel();
		mainPanel.add(buttonPanel);

		JTabbedPane tabbedSearchPane = new JTabbedPane();
		tabbedSearchPane.add("Basic", mainPanel);

		JPanel outerPanel = new JPanel();
		outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
		outerPanel.add(tabbedSearchPane);

		outerPanel.setBorder(BorderFactory.createTitledBorder(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.specifysearchcriteria")));

		//Build the advanced search panel now.
		JPanel advancedCriteriaPanel = new JPanel();
		advancedCriteriaPanel.setLayout(new SpringLayout());

		int rows = 1;
		if(this.searchType != SearchDELEntriesAction.SearchType.Documents
				&& this.searchType != SearchDELEntriesAction.SearchType.ValidationRules){
			rows++;
			advancedCriteriaPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.limitsearchbydocument")));
			limitByDocCheckBox = new JCheckBox();
			limitByDocCheckBox.addActionListener(this);
			advancedCriteriaPanel.add(limitByDocCheckBox);
			List<Document> docSummaryList = mainPane.getDelInitializer().getDocSummaryList();
			CustomComboBoxModel customModel = new CustomComboBoxModel(docSummaryList == null ? new ArrayList<Document>().toArray():docSummaryList.toArray());
			docComboBox = new JComboBox(customModel);
			advancedCriteriaPanel.add(docComboBox);

		}

		String[] authorities = mainPane.getDelInitializer().getLSIDAuthorities();
		authorityComboBox = new JComboBox(authorities);
		limitByAuthorityCheckBox = new JCheckBox();

		if (searchType.equals(SearchDELEntriesAction.SearchType.Entries)
				&& !(this.doc instanceof DummyDocument)
				&& doc != null) {
			//Restrict the search to the authority belonging to the current document.
			//As documents cannot have entries belonging to another authority.
			String docAuthority = this.doc.getDataSet().getProjectCode();	
			authorities = new String[1];
			authorities[0] = docAuthority;
			limitByAuthorityCheckBox.setEnabled(false);
			authorityComboBox.setEnabled(false);
		}
		if (authorities.length > 1) {
			rows++;
			advancedCriteriaPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.limitsearchbyauthority")));
			limitByAuthorityCheckBox.addActionListener(this);
			advancedCriteriaPanel.add(limitByAuthorityCheckBox);

			advancedCriteriaPanel.add(authorityComboBox);
		}

		StatusType[] statuses = StatusType.values();
		statusComboBox = new JComboBox(statuses);
		limitByStatusCheckBox = new JCheckBox();

		boolean isDEL = false;
		if (ds instanceof DELStudySet) {
			isDEL = true;
		}
		if (DELSecurity.getInstance().canViewPending()) {
			if (isDEL) {
				rows++;
				advancedCriteriaPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.limitsearchbystatus")));
				limitByStatusCheckBox.addActionListener(this);
				advancedCriteriaPanel.add(limitByStatusCheckBox);
				advancedCriteriaPanel.add(statusComboBox);

				if (elementStatus != null) {
					limitByStatusCheckBox.setSelected(true);
					statusComboBox.setSelectedItem(elementStatus);
				}
				if (searchType.equals(SearchDELEntriesAction.SearchType.Entries)
						&& !(this.doc instanceof DummyDocument)) {
					//Do not allow a pending element to be added to an existing document.
					//The elementStatus has already been set, so do not allow it to be changed.
					limitByStatusCheckBox.setEnabled(false);
					statusComboBox.setEnabled(false);
				}
			}
			else {
				//Only approved elements should be checked out outside of the DELView
				//In this case, the status combo box should not be shown, but should
				//have 'approved' selected.
				limitByStatusCheckBox.setSelected(true);
				statusComboBox.setSelectedItem(StatusType.Approved);
			}
		}


		advancedCriteriaPanel.add(new JLabel(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.listcurrentrevision")));
		currentRevCheckBox = new JCheckBox();
		currentRevCheckBox.addActionListener(this);
		advancedCriteriaPanel.add(currentRevCheckBox);
		advancedCriteriaPanel.add(new JLabel());
		if (!DELSecurity.getInstance().canApproveElements() && !DELSecurity.getInstance().canEditElements()) {
			//User is a DEL Viewer only so should only be able to search current revisions (a revision history is still shown for each element returned)
			currentRevCheckBox.setEnabled(false);
		}

		int inc = 0;
		while (rows < 3) {
			//Add filler rows to preserve the sizes of remaining checkboxes
			rows++;
			inc++;
			advancedCriteriaPanel.add(new JLabel(" "));
			advancedCriteriaPanel.add(new JLabel(" "));
			advancedCriteriaPanel.add(new JLabel(" "));
			advancedCriteriaPanel.add(new JLabel(" "));
			advancedCriteriaPanel.add(new JLabel(" "));
			advancedCriteriaPanel.add(new JLabel(" "));
		}
		rows += inc;

		SpringUtilities.makeCompactGrid(advancedCriteriaPanel,
				rows, 3, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		tabbedSearchPane.add(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.advanced"), advancedCriteriaPanel);

		currentRevCheckBox.setSelected(true);
		limitByAuthorityCheckBox.setSelected(false);
		authorityComboBox.setEnabled(false);
		if(this.searchType != SearchDELEntriesAction.SearchType.Documents
				&& this.searchType != SearchDELEntriesAction.SearchType.ValidationRules){
			limitByDocCheckBox.setSelected(false);
			docComboBox.setEnabled(false);
		}

		return outerPanel;
	}

	private void clearResultsTables(){
		CustomTableModel ctModel = (CustomTableModel)this.resultsTable.getModel();
		ctModel.clearContent();
		CustomRHTableModel ctRHModel = (CustomRHTableModel)this.revisionHistoryTable.getModel();
		ctRHModel.clearContent();
		resultsTable.invalidate();
		revisionHistoryTable.invalidate();
		paintComponents(getGraphics());
	}

	/**
	 * Populates the search results label in the following format:
	 * 'Displaying x of y results'
	 *  where x is the number of elements returned (according to search granularity)
	 *  and y is the total number of results returned by the query.
	 *  If there are more results to be had, the 'more' button is enabled.
	 *  Otherwise, it is disabled.
	 */
	private void populateQueryResultInfo(){
		//The format of the string is x of y
		//Where x is the number of items already in the table + the number
		//of newly returned results
		//and y is the total number of results for the query.

		CustomTableModel ctModel = (CustomTableModel)this.resultsTable.getModel();
		int rowCount = ctModel.getRowCount();
		int resultsDisplayedSoFar = rowCount + this.queryObject.getReturnedElementCount();

		String resultString = "Displaying " + new Integer(resultsDisplayedSoFar).toString() + " of " +
		new Integer(queryObject.getTotalResults()).toString() + " results.";

		if(queryObject.getRemainingResults() > 0){
			moreButton.setEnabled(true);
		}else{
			moreButton.setEnabled(false);
		}

		this.searchResultsLabel.setText(resultString);

	}

	public void actionPerformed(ActionEvent e) {
		this.searchTextLabel.setText(this.getSearchString());

		if(this.searchType == SearchDELEntriesAction.SearchType.Entries
				|| this.searchType == SearchDELEntriesAction.SearchType.All ){
			this.docFilterLabel.setText(this.getDocFilterString());
		}

		this.authorityFilterLabel.setText(this.getAuthorityFilterString());

		if (this.statusFilterLabel != null) {
			this.statusFilterLabel.setText(this.getStatusFilterString());
		}

		paintComponents(getGraphics());



		if(e.getSource() == searchButton){
			doSearch();
		}
		else if (e.getSource() == moreButton) {
			try {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				queryObject = doSearch(queryObject);
			} catch (Exception ex){
				LOG.error("Could notget more - " + ex.getMessage() + "Search string was " + queryObject.getSearchCriteria() +
						"Search type was " + queryObject.getSearchType() + " and element type was " + queryObject.getElementType());
				WrappedJOptionPane.showMessageDialog(this, "Get More failed - " + ex.getMessage() + ".", "Query Failure", JOptionPane.ERROR_MESSAGE);
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				return;
			}

			populateQueryResultInfo();
			populateResultsList();

			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		else if (e.getSource() == importButton) {
			importElement();
		}
		else if (e.getSource() == viewButton) {
			viewElement();
		}
		else if (e.getSource() == this.limitByDocCheckBox) {
			docComboBox.setEnabled(limitByDocCheckBox.isSelected());
		}
		else if (e.getSource() == this.limitByAuthorityCheckBox) {
			authorityComboBox.setEnabled(this.limitByAuthorityCheckBox.isSelected());
		}
		else if (e.getSource() == cancelButton) {
			this.dispose();
		}
	}

	private String getSelectedElementLSID(){

		String itemLSID = null;
		if(resultsTable.getSelectedRow() != -1){
			if(this.revisionHistoryTable.getSelectedRow() != -1){
				itemLSID = getLSID();
			}else{
				int selectionIndex = resultsTable.getSelectedRow();
				CustomTableModel tableModel = (CustomTableModel)resultsTable.getModel();
				DataElementContainer elem = tableModel.getRowObject(selectionIndex);
				itemLSID = elem.getElementLSID();
			}		
		}
		return itemLSID;
	}



	private String getLSID(){
		//get the revision number from the ElementHistoryItem
		CustomRHTableModel customRHModel = (CustomRHTableModel)this.revisionHistoryTable.getModel();
		int selectedRevisionIndex = revisionHistoryTable.getSelectedRow();
		ElementHistoryItem item = customRHModel.getRowObject(selectedRevisionIndex);

		return item.getLsid();
	}

	public void valueChanged(ListSelectionEvent e) {

		if(resultsTable.getSelectedRow() != -1){
			if(e.getValueIsAdjusting()){

				CustomRHTableModel model = (CustomRHTableModel)this.revisionHistoryTable.getModel();
				model.clearContent();

				//Populate the revision history list.
				DataElementContainer elem  = ((CustomTableModel)this.resultsTable.getModel()).getRowObject(this.resultsTable.getSelectedRow());

				boolean elemHasHistory = true;

				if(elem.getLatestMetaData() != null){
					if(elem.getLatestMetaData().getHistoryList() == null || 
							elem.getLatestMetaData().getHistoryList().size() == 0){
						elemHasHistory = false;
					}
				}else{
					elemHasHistory = false;
				}

				if (!elemHasHistory) {
					try {
						String saml = mainPane.getDelInitializer().getSaml();
						ElementMetaData metaData = (ElementMetaData) mainPane.getDelInitializer().getClient().getMetaData(elem.getElementLSID(), saml);
						elem.addMetaData(metaData);
					}catch (Exception ex){
						LOG.error("Error when trying to retrieve element metadata for " + elem.getElementLSID() + " - " + ex.getMessage());
						WrappedJOptionPane.showMessageDialog(this, "Error tryring to retrieve element metadata for " + elem.getElementName() + ".", "Data Retrieval Error", JOptionPane.ERROR_MESSAGE);
					}
				}

				this.populateRevisionHistoryList(elem);
				this.revisionHistoryLabel.setText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.revisionhistoryfor")+" " + elem.getElementName());

				this.importButton.setEnabled(true);
				this.viewButton.setEnabled(true);
			}
		}else{
			this.importButton.setEnabled(false);
			this.viewButton.setEnabled(false);
		}
	}

	void populateRevisionHistoryList(DataElementContainer element){

		List<ElementHistoryItem> historyList = new ArrayList<ElementHistoryItem>();
		if (element.getLatestMetaData() != null) {
			historyList = element.getLatestMetaData().getHistoryList();
		}

		/*
		 * Sort the history list by revision.
		 */
		ArrayList<ElementHistoryItem> orderedHistoryList = new ArrayList<ElementHistoryItem>(historyList.size());
		for (ElementHistoryItem item: historyList) {
			orderedHistoryList.add(null);
		}
		for (ElementHistoryItem item: historyList) {
			try {
				LSID lsid = LSID.valueOf(item.getLsid());
				int index = Integer.parseInt(lsid.getRevisionId());
				if (orderedHistoryList.get(index) == null) {
					orderedHistoryList.set(index, item);	}

			}
			catch (Exception e) {
				LOG.error("Unable to parse "+item.getLsid(), e);
			}
		}
		boolean success = orderedHistoryList.remove(null);
		while (success) {
			success = orderedHistoryList.remove(null);
		}

		CustomRHTableModel tableModel = (CustomRHTableModel)revisionHistoryTable.getModel();
		try {
			for (int i=0; i<orderedHistoryList.size(); i++) {
				Vector vWrapper = new Vector();
				vWrapper.add(orderedHistoryList.get(i));
				tableModel.addRow(vWrapper);
			}
		} catch (org.psygrid.data.model.hibernate.ModelException mex) {
			//reached the end of options
		} catch (NullPointerException nex) {
		}
	}

	private void populateResultsList(){
		CustomTableModel tableModel = (CustomTableModel)resultsTable.getModel();

		List<DataElementContainer> returnedElements = this.queryObject.getReturnedElements();

		try {
			for (int i=0; i<queryObject.getReturnedElementCount(); i++) {
				Vector vWrapper = new Vector();
				vWrapper.add(returnedElements.get(i));
				tableModel.addRow(vWrapper);
			}
		} catch (org.psygrid.data.model.hibernate.ModelException mex) {
			//reached the end of options
		} catch (NullPointerException nex) {
		}
		resultsTable.doLayout();
	}


	/**
	 * Searches according to the search criteria specified by IDELQueryObject object passed in.
	 * In practice, the queryObj passed in is always this.queryObject, and this.queryObject is also assigned
	 * the return result, thus maintaining the progress of a multiple-stage query.
	 * @param queryObj - object containing the search criteria.
	 * @return queryObj returned from the server.
	 * @throws ServiceException 
	 * @throws NotAuthorisedFault 
	 * @throws DELServiceFault 
	 * @throws RemoteException 
	 * @throws SocketTimeoutException 
	 * @throws ConnectException 
	 */
	private IDELQueryObject doSearch(IDELQueryObject queryObj) throws ConnectException, SocketTimeoutException, RemoteException, DELServiceFault, NotAuthorisedFault, ServiceException {	
		String saml = mainPane.getDelInitializer().getSaml();
		queryObj = mainPane.getDelInitializer().getClient().sophisticatedSearchByTypeAndName(queryObj, saml);
		return queryObj;
	}

	public void changedUpdate(DocumentEvent e) {
		this.searchTextLabel.setText(this.getSearchString());
	}

	public void insertUpdate(DocumentEvent e) {
		this.searchTextLabel.setText(this.getSearchString());
	}

	public void removeUpdate(DocumentEvent e) {
		this.searchTextLabel.setText(this.getSearchString());
	}

	private void doSearch() {
		clearResultsTables();
		importButton.setEnabled(false);
		viewButton.setEnabled(false);
		moreButton.setEnabled(false);
		this.revisionHistoryLabel.setText(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.revisionhistory"));

		String searchCriteria = searchText.getText();
		SearchType type = (SearchType)searchTypeList.getSelectedItem();
		String elementType = (String)DELTypeList.getSelectedItem();
		if(elementType.equals("All Types")){
			elementType = "Entry";
		}
		else if (elementType.equals("All Validation Rules")) {
			elementType = "ValidationRule";
		}
		else if (elementType.equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.ui.document"))) {
			elementType = "Document";
		}

		HibernateDataElementFactory factory = new org.psygrid.data.model.hibernate.HibernateDataElementFactory();
		queryObject = factory.createDELQueryManager(elementType, searchCriteria, type.toString());

		//Arg0 is the doc list
		//Arg1 is the authority list

		List<String> docLSIDs = new ArrayList<String>();
		if((this.searchType != SearchDELEntriesAction.SearchType.Documents 
				&& this.searchType != SearchDELEntriesAction.SearchType.ValidationRules)
				&& limitByDocCheckBox.isSelected()){
			CustomComboBoxModel model = (CustomComboBoxModel)docComboBox.getModel();
			Document doc = model.getSelectedDocument();
			docLSIDs.add(doc.getLSID().toString());
		}else{
			//DO NADA
		}

		List<String> authorities = new ArrayList<String>();
		if(this.limitByAuthorityCheckBox.isSelected()){
			String authority = (String)this.authorityComboBox.getSelectedItem();
			authorities.add(authority);
		}else{
			//DO NADA
		}

		List<DataElementStatus> statusExclusions = new ArrayList<DataElementStatus>();

		String statusFilter = getStatusFilterString();
		boolean viewPending = false;
		if (authorities.size() > 0) {
			//Check whether user can view pending documents in any of the selected authorities
			for (String authority: authorities) {
				if (DELSecurity.getInstance().canViewPending(authority)) {
					viewPending = true;
				}
			}
		}
		else {
			if (DELSecurity.getInstance().canViewPending()) {
				viewPending = true;
			}
		}

		boolean isDEL = false;
		if (ds instanceof DELStudySet) {
			isDEL = true;
		}

		if (!isDEL || !viewPending) {
			//If we are not in the DEL view then only approved elements can 
			//be searched for.
			//Otherwise, users with just 'view' access can see approved elements only.
			statusFilter = DataElementStatus.APPROVED.toString();
		}

		if (!statusFilter.equals(PropertiesHelper.getStringFor("org.psygrid.datasetdesigner.del.search.none"))) {
			//We want to search for a particular status and exclude all others
			DataElementStatus status = DataElementStatus.valueOf(statusFilter.toUpperCase());
			switch (status) {
			case APPROVED:
				statusExclusions.add(DataElementStatus.PENDING);
				statusExclusions.add(DataElementStatus.INCOMPLETE);
				statusExclusions.add(DataElementStatus.NOTAPPLICABLE);
				statusExclusions.add(DataElementStatus.REJECTED);
				break;
			case PENDING:
				statusExclusions.add(DataElementStatus.APPROVED);
				statusExclusions.add(DataElementStatus.INCOMPLETE);
				statusExclusions.add(DataElementStatus.NOTAPPLICABLE);
				statusExclusions.add(DataElementStatus.REJECTED);
				break;
			default:
				//The other statuses are not currently used, so no need to check at present.
				break;	
			}
		}
		queryObject.populateAdvancedSearchCriteria(docLSIDs, authorities, statusExclusions, currentRevCheckBox.isSelected());

		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			stopButton = new JButton("Stop!");
			buttonPanel.removeAll();
			buttonPanel.add(stopButton);
			buttonPanel.revalidate();
			buttonPanel.updateUI();
			invalidate();
			validate();
			repaint();
			searchButton.addActionListener(new ActionListener() {
				//This cancels the search (unable to do anything server side at present).
				//TODO this doesn't work at present
				public void actionPerformed(ActionEvent event) {
					buttonPanel.removeAll();
					buttonPanel.add(searchButton);
					buttonPanel.revalidate();
					buttonPanel.updateUI();
					invalidate();
					validate();
					repaint();
					return;	
				}
			});

			queryObject = doSearch(queryObject);
		} catch (Exception ex){	
			LOG.error("Could not do search - " + ex.getMessage() + "Search string was " + searchCriteria +
					"Search type was " + type.toString() + " and element type was " + elementType);
			
			//Update the UI to replace the stop button with the search button again
			buttonPanel.removeAll();
			buttonPanel.add(searchButton);
			buttonPanel.revalidate();
			buttonPanel.updateUI();
			invalidate();
			validate();
			repaint();
			String message = "Query failed";
			if (ex.getMessage() != null) {
				message += " - " + ex.getMessage() + ".";
			}
			WrappedJOptionPane.showMessageDialog(this, message, "Query Failure", JOptionPane.ERROR_MESSAGE);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}


		populateQueryResultInfo();
		populateResultsList();

		//Update the UI to replace the stop button with the search button again
		buttonPanel.removeAll();
		buttonPanel.add(searchButton);
		buttonPanel.revalidate();
		buttonPanel.updateUI();
		invalidate();
		validate();
		repaint();

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	@SuppressWarnings("unchecked")
	private void importElement() {

		String itemLSID = getSelectedElementLSID();

		boolean isDEL = false;
		if (ds instanceof DELStudySet) {
			isDEL = true;
		}
		List<ElementStatusContainer> currentElements = DocTreeModel.getInstance().getCheckedOutLSIDs(true, false);	//Retrieve all elements
		List<String> currentLSIDs = new ArrayList<String>();
		for (ElementStatusContainer current: currentElements) {
			currentLSIDs.add(current.getLsid().toString());
		}

		DataElementContainer importElement = null;


		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			importElement = (DataElementContainer)Utils.getCompleteBrowseElement(mainPane, itemLSID);
		} catch (Exception e2) {
			LOG.error("Could not retrieve LSID " + itemLSID + "for import.",e2);
			WrappedJOptionPane.showMessageDialog(this, "Import failed. Could not retrieve element from library - " + e2.getMessage() + ".", "Import Failure", JOptionPane.ERROR_MESSAGE);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		} 

		if(importElement.getElement() instanceof Entry){
			//Check whether the entry is to be imported into the currently active document or imported into a 'dummy document'
			//This option occurs when the search entries dialog is invoked from the main menu.
			//Only approved elements can be imported into an editable document
			if ((doc instanceof DummyDocument)
					&& DatasetController.getInstance().getActiveDocument() != null
					//&& !(DatasetController.getInstance().getActiveDocument() instanceof DummyDocument)
					&& ((Document)DatasetController.getInstance().getActiveDocument()).getIsEditable()
					&& importElement.getStatus().equals(DataElementStatus.APPROVED)) {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				int answer = WrappedJOptionPane.showConfirmDialog(this, "Would you like to import this entry into the current document?", "", WrappedJOptionPane.YES_NO_OPTION, WrappedJOptionPane.QUESTION_MESSAGE);
				switch (answer) {
				case WrappedJOptionPane.YES_OPTION:
					doc = DatasetController.getInstance().getActiveDocument();
					break;
				case WrappedJOptionPane.NO_OPTION:
					//Do nothing
					break;
				}
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			}

			//If we are just importing a single element outside of a document then
			//ensure that it's name is reflected in the 'dummy' document.
			if (doc instanceof DummyDocument) {
				if (doc.numEntries() > 0) {
					if (ds == null) {
						//Retrieve the DEL 'dummy' dataset
						this.ds = DocTreeModel.getInstance().getDELDataset();
					}

					//Create a new dummy document. This will happen if an entry has been imported 
					//previously using the current search dialog.
					this.doc = createDummyDocument();
				}
				doc.setName(importElement.getElementName());
				doc.setDescription(importElement.getElementDescription());
				doc.setDisplayText(importElement.getElementName());

				if (isDEL) {
					if (currentLSIDs.contains(itemLSID)) {
						//Element is already held locally, so opening read only
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						WrappedJOptionPane.showMessageDialog(this, "The entry \""+importElement.getElementName()+"\" has already been checked out locally and cannot be checked out again individually.", "Unable to import", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}

				DocTreeModel.getInstance().importIDocument(doc, doc.getDataSet());
				//DatasetController.getInstance().setActiveDocument(doc);
				mainPane.openTab(doc);
			}
			else if (!((Document)doc).getIsEditable()) {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				WrappedJOptionPane.showMessageDialog(this, "The current document is read only. Unable to import entry.", "Unable to import", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			else {
				List<String> docLSIDs = new ArrayList<String>();
				for (Entry entry: ((Document)doc).getEntries()) {
					LSID lsid = entry.getLSID();
					if (lsid != null) {
						docLSIDs.add(lsid.toString());
					}
				}

				if (docLSIDs.contains(itemLSID)) {
					//We are trying to add an entry to the same document twice.
					//The DSD cannot handle two entries with the same name in the same document at present.
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					WrappedJOptionPane.showMessageDialog(this, "The current document already contains this entry. Unable to import.", "Unable to import", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}


			if (isDEL) {
				if (! DELSecurity.getInstance().canEditElements(importElement.getElementLSIDObject().getAuthorityId())) {
					importElement.setIsEditable(false);
				}
				else {
					//Only the head revision can be edited
					if (!importElement.getHeadRevision()) {
						WrappedJOptionPane.showMessageDialog(this, "The entry \""+importElement.getElementName()+"\" is not the latest version and will be opened read only.", "Unable to edit", JOptionPane.INFORMATION_MESSAGE);
						importElement.setIsEditable(false);

						//Set any child elements as not editable too.
						if (importElement.getElement() instanceof DerivedEntry) {
							DerivedEntry bob = (DerivedEntry)importElement.getElement();
							Map<String,Entry> variables = bob.getVariables();
							for (String variableName: variables.keySet()) {
								Entry e = variables.get(variableName);
								e.setIsEditable(false);
							}
						}
						else if (importElement.getElement() instanceof ExternalDerivedEntry) {
							ExternalDerivedEntry bob = (ExternalDerivedEntry)importElement.getElement();
							Map<String, BasicEntry> variables = bob.getVariables();
							for (String variableName: variables.keySet()) {
								Entry e = (Entry)variables.get(variableName);
								e.setIsEditable(false);
							}
						}
						else if (importElement.getElement() instanceof CompositeEntry) {
							CompositeEntry bob = (CompositeEntry)importElement.getElement();
							for (Entry e: bob.getEntries()) {
								e.setIsEditable(false);
							}
						}
					}
					else if (currentLSIDs.contains(itemLSID)) {
						//Element is already held locally, so opening read only
						WrappedJOptionPane.showMessageDialog(this, "The entry \""+importElement.getElementName()+"\" has already been checked out locally and will be opened read only.", "Unable to edit", JOptionPane.INFORMATION_MESSAGE);
						importElement.setIsEditable(false);

						//Set any child elements as not editable too.
						if (importElement.getElement() instanceof DerivedEntry) {
							DerivedEntry bob = (DerivedEntry)importElement.getElement();
							Map<String,Entry> variables = bob.getVariables();
							for (String variableName: variables.keySet()) {
								Entry e = variables.get(variableName);
								e.setIsEditable(false);
							}
						}
						else if (importElement.getElement() instanceof ExternalDerivedEntry) {
							ExternalDerivedEntry bob = (ExternalDerivedEntry)importElement.getElement();
							Map<String, BasicEntry> variables = bob.getVariables();
							for (String variableName: variables.keySet()) {
								Entry e = (Entry)variables.get(variableName);
								e.setIsEditable(false);
							}
						}
						else if (importElement.getElement() instanceof CompositeEntry) {
							CompositeEntry bob = (CompositeEntry)importElement.getElement();
							for (Entry e: bob.getEntries()) {
								e.setIsEditable(false);
							}
						}
					}
					/*
					 * Check that the entries associated with the entry we are checking out haven't already been checked out locally.
					 * 
					 * If an entry is being imported into a dummy document then set subordinate elements as read only anyway.
					 */
					else if (importElement.getElement() instanceof DerivedEntry) {
						DerivedEntry de = (DerivedEntry)importElement.getElement();
						Map<String,Entry> variables = de.getVariables();
						for (String variableName: variables.keySet()) {
							Entry e = variables.get(variableName);
							if (doc instanceof DummyDocument) {
								e.setIsEditable(false);
							}
							else if (currentLSIDs.contains(e.getLSID().toString())) {
								WrappedJOptionPane.showMessageDialog(this, "The entry \""+e.getDisplayText()+"\" has already been checked out locally and will be opened read only.", "Unable to edit", JOptionPane.INFORMATION_MESSAGE);
								e.setIsEditable(false);
							}
						}
					}
					else if (importElement.getElement() instanceof ExternalDerivedEntry) {
						ExternalDerivedEntry ede = (ExternalDerivedEntry)importElement.getElement();
						Map<String, BasicEntry> variables = ede.getVariables();
						for (String variableName: variables.keySet()) {
							Entry e = (Entry)variables.get(variableName);
							if (doc instanceof DummyDocument) {
								e.setIsEditable(false);
							}
							else if (currentLSIDs.contains(e.getLSID().toString())) {
								WrappedJOptionPane.showMessageDialog(this, "The entry \""+e.getDisplayText()+"\" has already been checked out locally and will be opened read only.", "Unable to edit", JOptionPane.INFORMATION_MESSAGE);
								e.setIsEditable(false);
							}
						}
					}
					else if (importElement.getElement() instanceof CompositeEntry) {
						CompositeEntry ce = (CompositeEntry)importElement.getElement();
						for (Entry e: ce.getEntries()) {
							if (currentLSIDs.contains(e.getLSID().toString())) {
								WrappedJOptionPane.showMessageDialog(this, "The entry \""+e.getDisplayText()+"\" has already been checked out locally and will be opened read only.", "Unable to edit", JOptionPane.INFORMATION_MESSAGE);
								e.setIsEditable(false);
							}
						}
					}
				}
			}

			Utils.addElementPropertiesToDataset((DataSet)doc.getDataSet(), importElement);
			((Entry)importElement.getElement()).setMyDataSet((DataSet)doc.getDataSet());
			Utils.prepareElement(((Entry)importElement.getElement()), mainPane.getPanelForDocument(doc)); 
			this.mainPane.getPanelForDocument(doc).importEntry((Entry)importElement.getElement());
			DocTreeModel.getInstance().addEntry((Entry)importElement.getElement(), doc);
			((Document)doc).setIsRevisionCandidate(true);
		}else if(importElement.getElement() instanceof Document){

			Document theDoc = (Document)importElement.getElement();
			theDoc.setMyDataSet((DataSet) ds.getDs());

			boolean docReadOnly = false;
			//Only the head revision of an element can be edited
			if (isDEL) {
				if (!importElement.getHeadRevision()) {
					WrappedJOptionPane.showMessageDialog(this, "This document is not the latest version and will be opened read only.", "Unable to edit", JOptionPane.INFORMATION_MESSAGE);
					importElement.setIsEditable(false);
					docReadOnly = true;
				}
			}

			if (ds == null) {
				System.out.println("Dataset is null!!!!!!!!");
			}
			else {
				if (isDEL) {
					if (! DELSecurity.getInstance().canEditElements(importElement.getElementLSIDObject().getAuthorityId())) {
						importElement.setIsEditable(false);
					}
				}

				for (Document doc: ((DataSet)this.ds.getDs()).getDocuments()) {
					if (doc.getLSID() != null && doc.getLSID().toString().equals(itemLSID)) {
						//Document is already held locally, so unable to check out again.
						//The GUI cannot handle two documents with the same name at present. It also makes no sense to check it out again.
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						WrappedJOptionPane.showMessageDialog(this, "This document has already been checked out locally and cannot be checked out again.", "Unable to import", JOptionPane.INFORMATION_MESSAGE);
						return;			
					}
				}
			}


			for(Entry e1: theDoc.getEntries()){
				Utils.addElementPropertiesToDataset((DataSet)theDoc.getDataSet(), new DataElementContainer(e1));

				boolean entryReadOnly = false;
				if (isDEL) {
					String entryLSID = e1.getLSID().toString();
					if (! DELSecurity.getInstance().canEditElements(importElement.getElementLSIDObject().getAuthorityId())) {
						e1.setIsEditable(false);
					}
					else if (currentLSIDs.contains(entryLSID)) {
						//Element is already held locally, so opening read only
						if (!docReadOnly) {
							WrappedJOptionPane.showMessageDialog(this, "The entry \""+e1.getDisplayText()+"\" has already been checked out locally, so it is being opened read only.", "Error", JOptionPane.INFORMATION_MESSAGE);
							entryReadOnly = true;	//Don't display the message for each composite entry
						}
						e1.setIsEditable(false);
					}
					else if (!e1.getHeadRevision()) {
						//Only the head revision can be edited
						if (!docReadOnly && !entryReadOnly) {
							WrappedJOptionPane.showMessageDialog(this, "The entry \""+e1.getDisplayText()+"\" is not the latest version, so it is being opened read only.", "Unable to edit", JOptionPane.INFORMATION_MESSAGE);
							entryReadOnly = true;	//Don't display the message for each composite entry
						}
						e1.setIsEditable(false);
					}					
				}

				if(e1 instanceof CompositeEntry){
					CompositeEntry compEntry = (CompositeEntry) e1;

					if(compEntry.getEntryStatus() == null){
						compEntry.setEntryStatus(EntryStatus.MANDATORY);
					}

					for(BasicEntry bE: compEntry.getEntries()){
						bE.setEntryStatus(compEntry.getEntryStatus());
						bE.setSection(compEntry.getSection());

						if (isDEL) {
							String bELSID = bE.getLSID().toString();
							if (! DELSecurity.getInstance().canEditElements(importElement.getElementLSIDObject().getAuthorityId())) {
								e1.setIsEditable(false);
							}
							else if (currentLSIDs.contains(bELSID)) {
								//Element is already held locally, so unable to check out again.
								if (!docReadOnly && !entryReadOnly) {
									WrappedJOptionPane.showMessageDialog(this, "The entry \""+bE.getDisplayText()+"\" has already been checked out locally, so it is being opened read only.", "Unable to edit", JOptionPane.INFORMATION_MESSAGE);
								}
								e1.setIsEditable(false);
							}
							else if (!bE.getHeadRevision()) {
								//Only the head revision can be edited
								if (!docReadOnly && !entryReadOnly) {
									WrappedJOptionPane.showMessageDialog(this, "The entry \""+bE.getDisplayText()+"\" is not the latest version, so it is being opened read only.", "Unable to edit", JOptionPane.INFORMATION_MESSAGE);
								}
								e1.setIsEditable(false);
							}
						}
					}
				}

			}
			DocTreeModel.getInstance().importIDocument(theDoc, ds.getDs());
		}
		else if (importElement.getElement() instanceof ValidationRule) {
			if (currentLSIDs.contains(itemLSID)) {
				//Element is already held locally, so don't retrieve again
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				WrappedJOptionPane.showMessageDialog(this, "This validation rule has already been checked out locally.", "Unable to import", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			if (isDEL) {		
				if (! DELSecurity.getInstance().canEditElements(importElement.getElementLSIDObject().getAuthorityId())) {
					importElement.setIsEditable(false);
				}
				else if (!importElement.getHeadRevision()) {
					WrappedJOptionPane.showMessageDialog(this, "This validation rule is not the latest version and will be opened read only.", "Unable to edit", JOptionPane.INFORMATION_MESSAGE);
					importElement.setIsEditable(false);
				}
			}
			ds.getDs().addValidationRule((ValidationRule)importElement.getElement());
		}
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		JOptionPane.showMessageDialog(this, "Import Successful.", "Import", JOptionPane.INFORMATION_MESSAGE);
	}

	private void viewElement() {
		String itemLSID = getSelectedElementLSID();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		DataElementContainer importElement;
		try {
			importElement = Utils.getCompleteBrowseElement(mainPane,itemLSID);
		} catch (Exception e2) {
			LOG.error("Could not retrieve LSID " + itemLSID + "for viewing.",e2);
			WrappedJOptionPane.showMessageDialog(this, "Preview failed. Could not retrieve element from library - " + e2.getMessage() + ".", "Preview Failure", JOptionPane.ERROR_MESSAGE);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}

		/*
		 * Get whether the study used is actually the element library
		 */
		boolean isDEL = false;
		if (ds instanceof DELStudySet) {
			isDEL = true;
		}
		if(importElement.getElement() instanceof Entry){
			if(Utils.elementRequiresPreview(importElement.getElement())){
				Utils.createImportPreviewContextForEntry((Entry)importElement.getElement());
				Utils.prepareElement(importElement.getElement(), null);

				new ViewImportDialog(this, ((Entry)importElement.getElement()).getDataSet().getDocument(0), isDEL);
			}else{
				if (doc == null) {
					doc = createDummyDocument();
					((Entry)importElement.getElement()).setSection(doc.getSection(0));
					((Entry)importElement.getElement()).setMyDataSet((DataSet)ds.getDs());
					doc.addEntry((Entry)importElement.getElement());

				}
				if (DatasetController.getInstance().getActiveDocument() == null) {
					DatasetController.getInstance().setActiveDocument(doc);
				}

				DocumentPanel panel = mainPane.getPanelForDocument(doc);

				if (panel == null) {
					panel = mainPane.getCurrentPanel();
				}
				if (panel == null) {
					if (ds == null) {
						//Retrieve the DEL 'dummy' dataset
						this.ds = DocTreeModel.getInstance().getDELDataset();
					}

					if (ds != null) {
						//Create a new dummy document to add to a temporary document panel.
						DummyDocument doc = createDummyDocument();
						panel = new DocumentPanel((MainFrame)mainPane.getFrame(), doc, isDEL);
					}
				}
				if (panel == null) {
					//This should never happen now..
					WrappedJOptionPane.showMessageDialog(this, "You must open the document before you can view this element.", "Info", JOptionPane.INFORMATION_MESSAGE);
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					return;
				}else{
					((Entry)importElement.getElement()).setIsEditable(false);
					panel.viewEntry((Entry)importElement.getElement(), this);
				}
			}
		}
		else if (importElement.getElement() instanceof Document) {
			Document doc = (Document)importElement.getElement();
			Utils.createImportPreviewContextForDocument(doc);
			for(Entry e1: doc.getEntries()){
				e1.setIsEditable(false);
				if(e1 instanceof CompositeEntry){
					CompositeEntry compEntry = (CompositeEntry) e1;
					compEntry.setIsEditable(false);
					if(compEntry.getEntryStatus() == null) {
						compEntry.setEntryStatus(EntryStatus.MANDATORY);
					}

					for(BasicEntry bE: compEntry.getEntries()){
						bE.setEntryStatus(EntryStatus.MANDATORY);
						bE.setSection(compEntry.getSection());
						bE.setIsEditable(false);
					}

				}
			}
			doc.setIsEditable(false);
			new ViewImportDialog(this, doc, isDEL);
		}
		else if (importElement.getElement() instanceof ValidationRule) {
			((ValidationRule)importElement.getElement()).setIsEditable(false);
			new AddValidationRuleDialog(this, (ValidationRule)importElement.getElement());
		}

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	private DummyDocument createDummyDocument() {
		DummyDocument doc = new DummyDocument();
		doc.setMyDataSet((DataSet)ds.getDs());
		List<Section> sections = new ArrayList<Section>();
		Section section = new Section("");
		section.setDescription("");
		section.setDisplayText("");
		section.addOccurrence(new SectionOccurrence(""));
		sections.add(section);
		doc.setSections(sections);
		doc.setIsEditable(false);
		doc.setStatuses(new ArrayList<Status>());
		Status docStatus = new Status(DocumentStatus.DATASET_DESIGNER.toString(), DocumentStatus.DATASET_DESIGNER.toStatusLongName(), 0);
		doc.addStatus(docStatus);
		
		return doc;
	}
}
