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

package org.psygrid.collection.entry.ui;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.util.WindowUtils;
import org.psygrid.collection.entry.EntryMessages;
import org.psygrid.collection.entry.Icons;
import org.psygrid.collection.entry.persistence.DataSetSummary;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.persistence.VersionMap;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

public class HelpAboutDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private DefaultFormBuilder builder;

    private JButton okButton;
    
    private JLabel psygridLabel;
    private JLabel appNameLabel;
    private JLabel versionLabel;
    private JLabel clientVersionLabel;
    private JLabel repoVersionLabel;
    private JLabel aaVersionLabel;
    private JLabel paVersionLabel;
    private JLabel eslVersionLabel;
    private JLabel copyrightLabel;
    private JLabel supportLabel;
    private JLabel datasetsLabel;
    private List<JLabel> datasets;
    
    public HelpAboutDialog(JFrame parent) throws HeadlessException {
        super(parent, Messages.getString("HelpAboutDialog.dialogTitle"), true);
        initBuilder();
        initComponents();
        initEventHandling();
        build();
        pack();
        setLocation(WindowUtils.getPointForCentering(this));
    }

    private void initBuilder() {
        builder = new DefaultFormBuilder(new FormLayout("left:default,3dlu,default:grow"),  //$NON-NLS-1$
                new JPanel());
        builder.setDefaultDialogBorder();
    }
    
    private void initComponents() {
        psygridLabel = new JLabel(Icons.getInstance().getIcon("opencdmslogo2"));
        appNameLabel = new JLabel(Messages.getString("HelpAboutDialog.appNameLabel"));
        versionLabel = new JLabel(Messages.getString("HelpAboutDialog.versionLabel"));
        String clientVersion = null;
        try{
            Properties props = new Properties();
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("client.properties"));
            clientVersion = props.getProperty("client.version");
        }
        catch(IOException ex){
            clientVersion = "Unknown";
        }
        catch(NullPointerException ex){
            //if Properties#load can't find the properties file then
            //it seems to (very helpfully!) throw an NPE
            clientVersion = "Unknown";
        }
        clientVersionLabel = new JLabel(Messages.getString("HelpAboutDialog.clientVersionLabel")+clientVersion);
        PersistenceManager pManager = PersistenceManager.getInstance();
        VersionMap versions = null;
        synchronized ( pManager ){
            versions = PersistenceManager.getInstance().getVersionMap();
        }
        repoVersionLabel = new JLabel(Messages.getString("HelpAboutDialog.repoVersionLabel")+versions.getVersion(VersionMap.REPO_NAME));
        aaVersionLabel = new JLabel(Messages.getString("HelpAboutDialog.aaVersionLabel")+versions.getVersion(VersionMap.AA_NAME));
        paVersionLabel = new JLabel(Messages.getString("HelpAboutDialog.paVersionLabel")+versions.getVersion(VersionMap.PA_NAME));
        eslVersionLabel = new JLabel(Messages.getString("HelpAboutDialog.eslVersionLabel")+versions.getVersion(VersionMap.ESL_NAME));
        
        datasetsLabel = new JLabel(Messages.getString("HelpAboutDialog.datasetsLabel"));
        datasets = new ArrayList<JLabel>();
        List<DataSetSummary> dsSummaries = null;
        synchronized ( pManager ){
            dsSummaries = pManager.getData().getDataSetSummaries();
        }
        for ( DataSetSummary dss: dsSummaries ){
            datasets.add(new JLabel(dss.getDisplayText()+ Messages.getString("HelpAboutDialog.datasetsDetailedDescriptionLabel_p1") +dss.getProjectCode()+  Messages.getString("HelpAboutDialog.datasetsDetailedDescriptionLabel_p2") +dss.getVersionNo()));
        }
        
        copyrightLabel = new JLabel(Messages.getString("HelpAboutDialog.copyrightLabel"));
        supportLabel = new JLabel(Messages.getString("HelpAboutDialog.supportLabel"));
                
        okButton = new JButton(EntryMessages.getString("Entry.ok")); //$NON-NLS-1$
    }

    private void initEventHandling() {
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void build() {
        builder.setRowGroupingEnabled(false);
        builder.append(psygridLabel, builder.getColumnCount());
        builder.append(appNameLabel, builder.getColumnCount());
        builder.append(versionLabel, clientVersionLabel);
        builder.append(new JLabel(), repoVersionLabel);
        builder.append(new JLabel(), aaVersionLabel);
        builder.append(new JLabel(), paVersionLabel);
        builder.append(new JLabel(), eslVersionLabel);
        builder.appendUnrelatedComponentsGapRow();
        builder.nextLine(2);
        builder.append(datasetsLabel, builder.getColumnCount());
        for ( int i=0; i<datasets.size(); i++ ){
            if ( 0==i ){
                builder.append(datasetsLabel, datasets.get(i));
            }
            else{
                builder.append(new JLabel(), datasets.get(i));
            }
        }
        builder.appendUnrelatedComponentsGapRow();
        builder.nextLine(2);
        builder.append(copyrightLabel, builder.getColumnCount());
        builder.append(supportLabel, builder.getColumnCount());
        builder.appendUnrelatedComponentsGapRow();
        builder.nextLine(2);
        JPanel buttonsPanel = ButtonBarFactory.buildOKBar(okButton);
        builder.append(buttonsPanel, builder.getColumnCount());
        
        getContentPane().add(builder.getPanel());
    }
}
