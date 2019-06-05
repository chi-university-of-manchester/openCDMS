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

package org.psygrid.securitymanager;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.help.CSH;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.auth.LoginAdapter;
import org.jdesktop.swingx.auth.LoginEvent;
import org.jdesktop.swingx.event.ProgressEvent;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.psygrid.collection.entry.remote.ProxySetting;
import org.psygrid.common.proxy.ProxyAuthenticationMethods;
import org.psygrid.common.security.LoginInterfaceFrame;
import org.psygrid.security.PGSecurityException;
import org.psygrid.security.PGSecurityInvalidSAMLException;
import org.psygrid.security.PGSecuritySAMLVerificationException;
import org.psygrid.security.SystemPolicy;
import org.psygrid.security.SystemProject;
import org.psygrid.security.attributeauthority.client.AAManagementClient;
import org.psygrid.security.attributeauthority.service.InputFaultMessage;
import org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage;
import org.psygrid.security.attributeauthority.service.ProcessingFaultMessage;
import org.psygrid.security.components.net.PsyGridClientSocketFactory;
import org.psygrid.security.policyauthority.client.PAManagementClient;
import org.psygrid.securitymanager.actions.AboutAction;
import org.psygrid.securitymanager.actions.AddUserAction;
import org.psygrid.securitymanager.actions.DeleteUserAction;
import org.psygrid.securitymanager.actions.ExitAction;
import org.psygrid.securitymanager.actions.ModifyUserAction;
import org.psygrid.securitymanager.actions.RefreshAction;
import org.psygrid.securitymanager.actions.ResetPasswordAction;
import org.psygrid.securitymanager.controller.AAController;
import org.psygrid.securitymanager.listeners.TimeoutListener;
import org.psygrid.securitymanager.renderers.CustomTreeCellRenderer;
import org.psygrid.securitymanager.security.EntryLoginService;
import org.psygrid.securitymanager.security.FileLockException;
import org.psygrid.securitymanager.security.PersistenceManager;
import org.psygrid.securitymanager.security.SecurityHelper;
import org.psygrid.securitymanager.security.SecurityManager;
import org.psygrid.securitymanager.ui.CustomBoxButton;
import org.psygrid.securitymanager.ui.CustomTree;
import org.psygrid.securitymanager.ui.PsygridLoginDialog;
import org.psygrid.securitymanager.utils.HelpHelper;
import org.psygrid.securitymanager.utils.IconsHelper;
import org.psygrid.securitymanager.utils.LogConfigurator;
import org.psygrid.securitymanager.utils.PropertiesHelper;
import org.psygrid.securitymanager.utils.SortedTreeNode;
import org.psygrid.securitymanager.utils.UserType;
import org.psygrid.www.xml.security.core.types.AttributeType;
import org.psygrid.www.xml.security.core.types.GroupType;
import org.psygrid.www.xml.security.core.types.PolicyType;
import org.psygrid.www.xml.security.core.types.ProjectDescriptionType;
import org.psygrid.www.xml.security.core.types.ProjectType;
import org.psygrid.www.xml.security.core.types.RoleType;
import org.psygrid.www.xml.security.core.types.UserPrivilegesType;


/**
 * Application entry point.
 * 
 * @author Pauline Whelan
 */
public class Application extends LoginInterfaceFrame implements WindowListener {
	
	private static final Log LOG = LogFactory.getLog(Application.class);
	
	//Inactivity timeout value
	private static final int TIMEOUT = 1800000;
	
	//toolbar buttons
	private JButton addUserButton = new CustomBoxButton(new AddUserAction(0));
	private JButton modifyUserButton = new CustomBoxButton(new ModifyUserAction(0));
	private JButton deleteUserButton = new CustomBoxButton(new DeleteUserAction(0));
	private JButton resetPasswordButton = new CustomBoxButton(new ResetPasswordAction(0));
	private JButton refreshButton;
	
	//other gui components
	private CustomTree attributeTree = null;
	private JXStatusBar statusBar;
	private JPanel mainPanel;
	private JToolBar toolBar;
	
	private List<TreePath> expandedPaths = new ArrayList<TreePath>(); 
	private TreePath selectedPath;
	
	private JMenuBar menuBar;
	
	//store local list of project users privileges list
	private HashMap<ProjectType, UserPrivilegesType[]> projectUserPrivsList;
	
	//store local list of projects
	private List<ProjectType> projectTypes;
	
	public Application () {
		String title = PropertiesHelper.getPropertyHelper().getStringFor("org.psgyrid.securitymanager.mainframe.title"); 
		String clientSystem = "";
		try {
			clientSystem = getClientProperties().getProperty("client.system");
		}
		catch (IOException ioe) {
			LOG.warn("Unable to retrieve client system name.", ioe);
		}
		title += " - "+clientSystem;
		setTitle(title);
		
		setIconImage(IconsHelper.getInstance().getImageIcon("psygrid.jpg").getImage());
		setSize(new Dimension(800, 800));
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int)(size.getWidth()/2 - (this.getWidth()/2)), (int)(size.getHeight()/2 - (this.getHeight()/2)));
		addWindowListener(this);
		AAController.getInstance().addActionListener(new TreeUpdaterListener());
		statusBar = new JXStatusBar();
		getContentPane().add(statusBar, BorderLayout.SOUTH);
		setJMenuBar(createMenuBar());
		getContentPane().add(createToolBar(), BorderLayout.NORTH);
	    
		//listen for inactivity
		TimeoutListener timeoutListener = new TimeoutListener(this, TIMEOUT);
		addMouseListener(timeoutListener);
	    addMouseMotionListener(timeoutListener);
	    addKeyListener(timeoutListener);
	    
	    // We poll at half the timeout interval to reduce the fudge factor
	    // introduced by events delivered at just under the timeout value.
	    Timer timer = new Timer(TIMEOUT / 2, timeoutListener);
	    timer.setRepeats(true);
	    timer.start();
	    
	    setVisible(true);
	}

	
	private static Application application;
	
	public static Application getInstance()
	{
		if (application == null)
		{
			application = new Application();
		}
		return application;
	}
	
	public static void main (String[] args)
	{
		try {
			UIManager.setLookAndFeel(new SubstanceLookAndFeel());
		} catch (UnsupportedLookAndFeelException ilafe) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("Look and Feel not found", ilafe); 
			}
		}
		
		try {
			new LogConfigurator();
		} catch (IOException e) {
			LOG.warn("Error creating LogConfigurator", e); //$NON-NLS-1$
		}

		
		final Application application = new Application();
		application.setToolsEnabled(false);
		application.setVisible(true);
		
		try
		{
			PersistenceManager.getInstance().initBaseDir();
		} catch (FileLockException fex)
		{
			JOptionPane.showMessageDialog(
					new JDialog(),
					"Only one instance of the Security Manager may be run at any one time. \n Please close the application and start again.", //$NON-NLS-1$
					"Only one instance", //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE);
			System.exit(-1);

		} catch (Exception ex)
		{
			LOG.error("Error initialising PersistenceManager",ex);
			System.exit(-1);
		}
		
		// Setup the proxy settings to the default
		try {
			List<ProxySetting> lps = PersistenceManager.getInstance()
					.loadProxySettings();
			boolean proxyUsed = false;
			if (null != lps) {
				for ( ProxySetting ps: lps ){
					if ( ps.getDefaultProxy().booleanValue() ){
						// Set the JVM properties
						if (LOG.isInfoEnabled()) {
							LOG.info("Proxy settings: https.proxyHost=" + ps.getServer() + ", https.proxyPort=" + ps.getPort()); //$NON-NLS-1$
						}
						System.setProperty("http.proxyHost", ps.getServer());
						System.setProperty("http.proxyPort", ps.getPort());
						System.setProperty("https.proxyHost", ps.getServer());
						System.setProperty("https.proxyPort", ps.getPort());
						if(!ProxyAuthenticationMethods.NONE.name().equals(ps.getAuthenticationMethod())){
							application.setDoProxyAuth(true);
							PsyGridClientSocketFactory.setAuthScheme(ProxyAuthenticationMethods.valueOf(ps.getAuthenticationMethod()));
							if((ProxyAuthenticationMethods.WINDOWS.name().equals(ps.getAuthenticationMethod()))){
								PsyGridClientSocketFactory.setNTdomain(ps.getDomain());
							}
						}
						proxyUsed = true;
					}
				}
			}
			if ( !proxyUsed ){
				if (LOG.isInfoEnabled()) {
					LOG.info("No default proxy setting - using direct connection"); 
				}
			}
		} catch (IOException ioe) {
			if (LOG.isInfoEnabled()) {
				LOG.info("No proxy settings found - using direct connection"); 
			}
		}
		
		EntryLoginService service = new EntryLoginService(application);
		service.addLoginListener(new LoginAdapter() {
			@Override
			public void loginFailed(LoginEvent source) {
					LOG.info("Login failed", source.getCause()); //$NON-NLS-1$
			}

		});
		
		PsygridLoginDialog loginDialog = new PsygridLoginDialog(application, service);
		loginDialog.setVisible(true);

	}
	
	/**
	 * @return Returns the doProxyAuth.
	 */
	public boolean isDoProxyAuth() {
		return doProxyAuth;
	}

	/**
	 * @param doProxyAuth The doProxyAuth to set.
	 */
	public void setDoProxyAuth(boolean doProxyAuth) {
		this.doProxyAuth = doProxyAuth;
	}
	
	public void setToolsEnabled(boolean enabled) {
		addUserButton.setEnabled(enabled);
		modifyUserButton.setEnabled(enabled);
		deleteUserButton.setEnabled(enabled);
		resetPasswordButton.setEnabled(enabled);
		refreshButton.setEnabled(enabled);
	}
	
	public void reinit() {
		storeTreeState();
		init();
		updateTreeState();
	}
	
	public void init() {
		SwingWorker worker = new SwingWorker() {
			public Object doInBackground() {
			   	setStarted();
			   	loadServerModels();
			   	return null;
			}
				
			public void done() {
				doGUIBuilding();
				setFinished();
			}
		};

		worker.execute();
		
	}
	
	public void setStarted() {
    	this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		EventQueue.invokeLater(new Runnable() {
	       	public void run() {
	       		statusBar.progressStarted(new ProgressEvent(this));
	       		statusBar.setLeadingMessage(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.ui.progressstarted"));
	        }
		});
	}
	
	public void setFinished() {
		EventQueue.invokeLater(new Runnable() {
		       	public void run() {
		       		statusBar.progressEnded(new ProgressEvent(this));
		       		statusBar.setLeadingMessage(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.ui.progressloaded"));
		        }
		    });
    	this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	public void doGUIBuilding() {
		mainPanel = createMainPanel();
		
		getContentPane().removeAll();
		getContentPane().add(statusBar, BorderLayout.SOUTH);
		setJMenuBar(createMenuBar());

		if (toolBar!=null && !toolBar.isShowing())
		{
			getContentPane().add(createToolBar(), BorderLayout.NORTH);
		}
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		getContentPane().validate();
		getContentPane().repaint();
		validate();
		repaint();
		
		updateTreeState();
	}
	

	private void loadServerModels() {
		try
		{
			projectUserPrivsList = new HashMap<ProjectType, UserPrivilegesType[]>();
			projectTypes = new ArrayList<ProjectType>();

			if(SecurityManager.getInstance().isSuperUser()){
        		ProjectDescriptionType[] pdta = SecurityHelper.getAAQueryClient().getPort().getProjects();
    			for(ProjectDescriptionType a : pdta){
    				projectTypes.add(a.getProject());
    			}
        	} else {
                projectTypes  = SecurityHelper.getAAQueryClient().getMyProjects();
        	}

			for (int i=0; i<projectTypes.size(); i++) {		
				if (projectTypes.get(i).getName().equals("SYSTEM")){
					try {
						UserPrivilegesType[] upt = null;
						
						if(SecurityManager.getInstance().isSuperUser()){
							upt = SecurityHelper.getAAManagementClient().getPort().getUsersAndPrivilegesInProject(projectTypes.get(i));
						} else {
							List <RoleType> myRoles = SecurityHelper.getAAQueryClient().getMyRolesInProject(projectTypes.get(i));
							{
								for (RoleType r: myRoles)
								{
									if (r.getName().equals("SystemAdministrator")) {
										upt = SecurityHelper.getAAManagementClient().getPort().getUsersAndPrivilegesInProject(projectTypes.get(i));
									}
								}
							}
							//in the case of a SYSTEM project, null privileges so don't store 
							//i.e. don't display to the user
						}
						if (upt != null) {
							projectUserPrivsList.put(projectTypes.get(i), upt);
						}
						
					} catch (Exception ex) {
						LOG.error("MF: Error fetching user privileges from project" , ex);
					}
				} else {
					try {
						SecurityHelper.getAAManagementClient().getPort().getUsersAndPrivilegesInProject(projectTypes.get(i));
						UserPrivilegesType[] upt = SecurityHelper.getAAManagementClient().getPort().getUsersAndPrivilegesInProject(projectTypes.get(i));
						projectUserPrivsList.put(projectTypes.get(i), upt);
					} catch (Exception ex) {
						//log error and don't add to the list
						LOG.error("MF: Error fetching user privileges from project" , ex);
					}
				}
			}
		} catch (Exception ex) {
			//just log the error, not much else we can do
			LOG.error("MF: Error fetching user privileges from project 22" , ex);
		}
	}
	
	private JPanel createMainPanel() {
		mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		SortedTreeNode root3 = null;
		
		try
		{
			//create dummy root node to keep project children all on same level of tree
			SortedTreeNode dummyRootNode = new SortedTreeNode();
			
			for (int i=0; i<projectTypes.size(); i++) {
				UserPrivilegesType[] upt = projectUserPrivsList.get((ProjectType)projectTypes.get(i));
				//can be null if member of SYSTEM project but no permissions
				//to see the SYSTEM project (e.g. no sysadmin role)
				if (upt!= null) {
					root3 = processUserHierarchy(upt);
					//for display purposes, set name of node to be the project
					root3.setUserObject(projectTypes.get(i));
					dummyRootNode.add(root3);
				}
			}
			
			attributeTree = new CustomTree(this, dummyRootNode);
			attributeTree.setCellRenderer(new CustomTreeCellRenderer());
			attributeTree.setRootVisible(false);
			
			JScrollPane jScrollPane1 = new JScrollPane(); 
				jScrollPane1.setViewportView(attributeTree);
				
		} catch (Exception e) {
			LOG.error("MainFrame: error fetching projects, users and groups " + e.getMessage());
		}
		
		mainPanel.add(new JScrollPane(attributeTree), BorderLayout.CENTER);
		return mainPanel;

	}
	
	private JMenuBar createMenuBar() {
		menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu(PropertiesHelper.getPropertyHelper().getStringFor("org.psgyrid.securitymanager.mainframe.file"));
		fileMenu.add(new JMenuItem(new ExitAction()));
		
		JMenu toolsMenu = new JMenu(PropertiesHelper.getPropertyHelper().getStringFor("org.psgyrid.securitymanager.mainframe.tools"));	
		toolsMenu.add(new JMenuItem(new AddUserAction(true)));
		toolsMenu.add(new JMenuItem(new ModifyUserAction(true)));
		toolsMenu.add(new JMenuItem(new DeleteUserAction(true)));
		toolsMenu.add(new JMenuItem(new ResetPasswordAction(true)));
		toolsMenu.add(new JMenuItem(new RefreshAction(this, true)));
		JMenu helpMenu = new JMenu(PropertiesHelper.getPropertyHelper().getStringFor("org.psgyrid.securitymanager.mainframe.help"));
		JMenuItem helpMenuItem = new JMenuItem(PropertiesHelper.getPropertyHelper().getStringFor("org.psgyrid.securitymanager.mainframe.helphelp"));
		helpMenuItem.addActionListener(
		    new CSH.DisplayHelpFromSource(HelpHelper.getInstance().getHelpBroker()) );
		helpMenu.add(helpMenuItem);
		helpMenu.add(new JMenuItem(new AboutAction()));
		
		menuBar.add(fileMenu);
		menuBar.add(toolsMenu);
		menuBar.add(helpMenu);
		
		return menuBar;
	}
	
	public void setUserName(String uid){
        setTitle(getTitle()+" - "+uid);
	}

	
	/**
	 * Window Listener methods
	 */
	public void windowActivated(WindowEvent e) {
	}

	/**
	 * Exit when window closed.
	 */
	public void windowClosed(WindowEvent e) {
		try {
			PersistenceManager.getInstance().dispose();
		} catch (IOException ex)
		{
			//do nothing.. what could we do anyway?
		}
		System.exit(0);
	}

	/**
	 * Exit when window closed.
	 */
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}
	
	public void storeTreeState() {
		DefaultMutableTreeNode root = ((DefaultMutableTreeNode)attributeTree.getModel().getRoot());
		expandedPaths = new ArrayList();
		
		for (int i=0; i<attributeTree.getRowCount(); i++) {
			TreePath currentPath = attributeTree.getPathForRow(i);
			
			if (attributeTree.isExpanded(currentPath))
			{
				expandedPaths.add(currentPath);
				SortedTreeNode node = (SortedTreeNode)currentPath.getLastPathComponent();
			}
		}
		selectedPath = attributeTree.getSelectionPath();
	}
	
	public void updateTreeState() {
		for (int j=0; j<attributeTree.getRowCount(); j++) {
			for (int i=0; i<expandedPaths.size(); i++) {
				TreePath rowPath = attributeTree.getPathForRow(j);
				SortedTreeNode node = (SortedTreeNode) rowPath.getLastPathComponent(); 
				SortedTreeNode expNode = (SortedTreeNode)expandedPaths.get(i).getLastPathComponent();
				if (node.getUserObject() instanceof ProjectType) {
					ProjectType lastProject = (ProjectType)node.getUserObject();
					if (expNode.getUserObject() instanceof ProjectType) {
						ProjectType expandedProj = (ProjectType)expNode.getUserObject();
						if (lastProject.getName().equals(expandedProj.getName())) {
							attributeTree.expandPath(rowPath);
						}
					}
				} else if (node.getUserObject() instanceof UserType) {
					UserType lastUser = (UserType)node.getUserObject();
					if (expNode.getUserObject() instanceof UserType) {
						UserType expandedUser = (UserType)expNode.getUserObject();
						if (lastUser.getName().equals(expandedUser.getName())){
							attributeTree.expandPath(rowPath);
						}
					}
				} else if (node.getUserObject().equals("Roles")) {
					if (expNode.getUserObject().equals("Roles")) {
						SortedTreeNode parentNode =  (SortedTreeNode)expandedPaths.get(i).getParentPath().getLastPathComponent();
						UserType parentType = (UserType)parentNode.getUserObject();
						SortedTreeNode parentRowNode = (SortedTreeNode)rowPath.getParentPath().getLastPathComponent();
						UserType parentRowType = (UserType)parentRowNode.getUserObject();
						if (parentType.getName().equals(parentRowType.getName())) {
							attributeTree.expandPath(rowPath);
						}
					}
				} else if (node.getUserObject().equals("Groups")) {
					if (expNode.getUserObject().equals("Groups")) {
						SortedTreeNode parentNode = (SortedTreeNode)expandedPaths.get(i).getParentPath().getLastPathComponent();
						UserType parentType = (UserType)parentNode.getUserObject();
						SortedTreeNode parentRowNode = (SortedTreeNode)rowPath.getParentPath().getLastPathComponent();
						UserType parentRowType = (UserType)parentRowNode.getUserObject();
						if (parentType.getName().equals(parentRowType.getName())) {
							attributeTree.expandPath(rowPath);
						}
					}
				}
			}
		}
		updateSelectionPath();
	}
	
	public void updateSelectionPath() {
		//get last component
		
		try
		{
			SortedTreeNode selNode = (SortedTreeNode)selectedPath.getLastPathComponent();
			for (int i=0; i<attributeTree.getRowCount(); i++)
			{
				TreePath rowPath = attributeTree.getPathForRow(i);
				SortedTreeNode node = (SortedTreeNode) rowPath.getLastPathComponent(); 
				
				if (node.getUserObject() instanceof ProjectType) {
					ProjectType lastProject = (ProjectType)node.getUserObject();
					if (selNode.getUserObject() instanceof ProjectType) {
						ProjectType expandedProj = (ProjectType)selNode.getUserObject();
						if (lastProject.getName().equals(expandedProj.getName())) {
							attributeTree.setSelectionPath(rowPath);
							attributeTree.scrollPathToVisible(rowPath);
						}
					}
				} else if (node.getUserObject() instanceof UserType) {
					UserType lastUser = (UserType)node.getUserObject();
					if (selNode.getUserObject() instanceof UserType) {
						UserType expandedUser = (UserType)selNode.getUserObject();
						if (lastUser.getName().equals(expandedUser.getName())){
							attributeTree.setSelectionPath(rowPath);
							attributeTree.scrollPathToVisible(rowPath);
						}
					}
				} else if (node.getUserObject() instanceof RoleType) {
					RoleType lastRole = (RoleType)node.getUserObject();
					if (selNode.getUserObject() instanceof RoleType) {
						RoleType expandedRole = (RoleType)selNode.getUserObject();
						if (lastRole.getName().equals(expandedRole.getName()))
						{
							attributeTree.setSelectionPath(rowPath);
							attributeTree.scrollPathToVisible(rowPath);
						}
					}
				} else if (node.getUserObject() instanceof GroupType) {
					GroupType lastGroup = (GroupType)node.getUserObject();
					if (selNode.getUserObject() instanceof GroupType) {
						GroupType expandedGroup = (GroupType)selNode.getUserObject();
						if (lastGroup.getName().equals(expandedGroup.getName())) {
							attributeTree.setSelectionPath(rowPath);
							attributeTree.scrollPathToVisible(rowPath);
						}
					}
				} else if (node.getUserObject().equals("Roles")) {
					if (selNode.getUserObject().equals("Roles")) {
						SortedTreeNode parentNode =  (SortedTreeNode)selectedPath.getParentPath().getLastPathComponent();
						UserType parentType = (UserType)parentNode.getUserObject();
						SortedTreeNode parentRowNode = (SortedTreeNode)rowPath.getParentPath().getLastPathComponent();
						UserType parentRowType = (UserType)parentRowNode.getUserObject();
						if (parentType.getName().equals(parentRowType.getName())) {
							attributeTree.setSelectionPath(rowPath);
							attributeTree.scrollPathToVisible(rowPath);
						}
					}
				} else if (node.getUserObject().equals("Groups")) {
					if (selNode.getUserObject().equals("Groups")) {
						SortedTreeNode parentNode = (SortedTreeNode)selectedPath.getParentPath().getLastPathComponent();
						UserType parentType = (UserType)parentNode.getUserObject();
						SortedTreeNode parentRowNode = (SortedTreeNode)rowPath.getParentPath().getLastPathComponent();
						UserType parentRowType = (UserType)parentRowNode.getUserObject();
						if (parentType.getName().equals(parentRowType.getName())) {
							attributeTree.setSelectionPath(rowPath);
							attributeTree.scrollPathToVisible(rowPath);
						}
					}
				}
			}
		} catch (NullPointerException nex)
		{
			//selection error, ignore
		}
		
	}
	
	public boolean isSysAdmin(ProjectType p)
	{
		boolean isSysAdmin = false;
		
		if(org.psygrid.securitymanager.security.SecurityManager.getInstance().isSuperUser()){
			isSysAdmin = true;
		} else {
			try
			{
				List <RoleType> myRoles = SecurityHelper.getAAQueryClient().getMyRolesInProject(p);

				for (RoleType r: myRoles) {
					if (r.getName().equals("SystemAdministrator")) {
						isSysAdmin = true;
					}
				}
			} catch (PGSecurityInvalidSAMLException pgsaml) {
				LOG.error("MainFrame : pg invalid saml " + pgsaml.getMessage());
			} catch (PGSecurityException pgsec) {
				LOG.error("MainFrame : pgsecurity " +pgsec.getMessage());
			} catch (PGSecuritySAMLVerificationException pgver) {
				LOG.error("MainFrame: pgver " + pgver.getMessage());
			} catch (ConnectException cex) {
				LOG.error("MainFrame : cex " + cex.getMessage());
			} catch (NotAuthorisedFaultMessage nafm) {
				LOG.error("MainFrame : nafm " + nafm.getMessage());
			}
		}		
		return isSysAdmin;
	}
	
	
	private SortedTreeNode processUserHierarchy(UserPrivilegesType[] p) {
		SortedTreeNode dmtn = new SortedTreeNode("root");
		if (p != null) {
			for (UserPrivilegesType upt : p) {
				SortedTreeNode u = new SortedTreeNode(new UserType(upt
						.getUser().getDistinguishedName()));
				if (upt.getAttribute() != null) {
					for (AttributeType at : upt.getAttribute()) {
						SortedTreeNode role = new SortedTreeNode("Roles");
						SortedTreeNode group = new SortedTreeNode("Groups");
						if (at.getRole() != null) {
							for (RoleType r : at.getRole()) {
								if (r.getName().equals("SystemAdministrator")) {
									if (isSysAdmin(at.getProject())) {
										role.add(new SortedTreeNode(r));
									}
								} else {
									role.add(new SortedTreeNode(r));
								}
							}
						}
						if (at.getGroup() != null) {
							for (GroupType g : at.getGroup()) {
								group.add(new SortedTreeNode(g));
							}
						}
						u.add(role);
						u.add(group);
					}
				}
				dmtn.add(u);
			}
		}
		return dmtn;
	}

	private JToolBar createToolBar() {
		toolBar = new JToolBar();
		toolBar.setName(PropertiesHelper.getPropertyHelper().getStringFor("org.psygrid.securitymanager.ui.toolbar"));
		toolBar.add(addUserButton);
		toolBar.add(modifyUserButton);
		toolBar.add(deleteUserButton);
		toolBar.add(resetPasswordButton);
		refreshButton = new CustomBoxButton(new RefreshAction(this));
		toolBar.add(refreshButton);
		
		return toolBar;
	}

	
	private class TreeUpdaterListener implements ActionListener
	{
		public void actionPerformed(ActionEvent aet) {
			reinit();
		}
	}
	
	public void checkSystemSecurityConfiguration(){
		if(SecurityManager.getInstance().isSuperUser()){
			try{
				LOG.info("Checking for SYSTEM project");
				ProjectDescriptionType[] pdta = SecurityHelper.getAAQueryClient().getPort().getProjects();
				if(pdta==null){
					installSystemProject();
				} else {
					if(pdta.length==0){
						installSystemProject();					
					}
				}

				LOG.info("Checking for SYSTEM policy");
				PolicyType policyType = SecurityHelper.getPAManagementClient().getPort().getPolicyByProject(new PolicyType("SYSTEM", "-1", null, null));
				if(policyType==null){
					installSystemPolicy();
				}
	        } catch (ProcessingFaultMessage e) {
				LOG.error("Failed to check security system configuration; "+ e.getMessage());
	        } catch (ConnectException e) {
				LOG.error("Failed to check security system configuration; "+ e.getMessage());
	        } catch (InputFaultMessage e) {
				LOG.error("Failed to check security system configuration; "+ e.getMessage());
	        } catch (org.psygrid.security.attributeauthority.service.NotAuthorisedFaultMessage e) {
				LOG.error("Failed to check security system configuration; "+ e.getMessage());
	        } catch (RemoteException e) {
				LOG.error("Failed to check security system configuration; "+ e.getMessage());
	        }
		} else {
			LOG.warn("Skipped security system configuration check; insufficient privileges");
		}
	}
	
	private void installSystemProject() {
		AAManagementClient mc = SecurityHelper.getAAManagementClient();
		try {
			SystemProject.insert(mc);
		} catch (Exception e) {
			LOG.error("Failed to install system project; "+ e.getMessage());
		}
	}
	
	private void installSystemPolicy(){
		try {

			PAManagementClient mc = SecurityHelper.getPAManagementClient();
		
			SystemPolicy.insert(mc);
			
		} catch (Exception e) {
			LOG.error("Failed to install system policy; "+ e.getMessage());
		}	
	}
	
	private static Properties getClientProperties() throws IOException {
		Properties props = new Properties();
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("client.properties");
		if (stream != null)
			props.load(stream); 
		return props;
	}
}