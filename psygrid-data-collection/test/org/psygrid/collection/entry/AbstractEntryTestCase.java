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
package org.psygrid.collection.entry;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.builder.BuilderHandler;
import org.psygrid.collection.entry.model.OptionComboBoxModel;
import org.psygrid.collection.entry.model.OptionEditableComboBoxModel;
import org.psygrid.collection.entry.model.SectionPresModel;
import org.psygrid.collection.entry.persistence.PersistenceManagerTestHelper;
import org.psygrid.collection.entry.renderer.BasicRenderer;
import org.psygrid.collection.entry.renderer.BasicRendererMouseListener;
import org.psygrid.collection.entry.renderer.EditDialogLauncher;
import org.psygrid.collection.entry.renderer.Renderer;
import org.psygrid.collection.entry.renderer.RendererHandler;
import org.psygrid.collection.entry.renderer.RendererHelper;
import org.psygrid.collection.entry.security.SecurityHelper;
import org.psygrid.collection.entry.security.User;
import org.psygrid.collection.entry.ui.AbstractEditable;
import org.psygrid.collection.entry.ui.EditableComboBox;
import org.psygrid.collection.entry.ui.EditableRadioButton;
import org.psygrid.collection.entry.ui.EntryComponent;
import org.psygrid.collection.entry.ui.EntryTable;
import org.psygrid.collection.entry.util.StandardCodesGetter;
import org.psygrid.data.model.IDateValue;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.IPersistent;
import org.psygrid.data.model.ITextValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.*;

@SuppressWarnings("nls")
public abstract class AbstractEntryTestCase extends TestCase {
    
    private static final Log LOG = LogFactory.getLog(AbstractEntryTestCase.class);
    
    private static long id = 0;
    
    public static final int NUM_STANDARD_RENDERER_COMPONENTS = 3;
    
    protected AbstractEntryTestCase() {
        Launcher.initSafe();
        try {
            new LogConfigurator();
        } catch (IOException e) {
            LOG.warn("Error creating LogConfigurator", e); //$NON-NLS-1$
        }
    }
    
    protected static void createDocumentStatuses(Document document)    {
        
        Factory factory = getFactory();
        Status incomplete = factory.createStatus("Incomplete", "Incomplete", 0);
        Status pending = factory.createStatus("Pending", "Pending Approval", 1);
        Status rejected = factory.createStatus("Rejected", "Rejected", 2);
        Status approved = factory.createStatus("Approved", "Approved", 3);
        
        incomplete.addStatusTransition(pending);
        pending.addStatusTransition(incomplete);
        pending.addStatusTransition(rejected);
        pending.addStatusTransition(approved);
        rejected.addStatusTransition(pending);
        approved.addStatusTransition(pending);
        
        document.addStatus(incomplete);
        document.addStatus(pending);
        document.addStatus(rejected);
        document.addStatus(approved);
        
    }
    
    protected static void createOptionDependent(
            Factory factory, 
            Option option,
            Entry dependentEntry,
            EntryStatus status) {
        
        OptionDependent optDep = factory.createOptionDependent();
        optDep.setEntryStatus(status);
        option.addOptionDependent(optDep);
        optDep.setDependentEntry(dependentEntry);
    }
    
    public static Record getRecord() throws Exception {
        Record record = getDataSet().generateInstance();
        record.generateIdentifier("OLK/G1-1");
        return record;
    }
    
    public List<StandardCode> getStandardCodes(ApplicationModel model) {
        return model.getStandardCodes();
    }
    
    public StandardCodesGetter getStandardCodesGetter(ApplicationModel model) {
        return model.getStandardCodesGetter();
    }
    
    public Application createApplication() {
        final Application application = new Application();
        application.setLoadPendingDocumentsMItemEnabled(Boolean.valueOf(true));
        for (WindowListener listener : application.getWindowListeners()) {
            application.removeWindowListener(listener);
        }
        application.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                application.exitWithoutConfirmation(false);
            }
        });
        application.getModel().setStandardCodesGetter(new StandardCodesGetter() {
           private List<StandardCode> standardCodes;
           public List<StandardCode> getStandardCodes() {
               if (standardCodes == null) {
                standardCodes = AbstractEntryTestCase.getStandardCodes();
               }
               return standardCodes;
            } 
        });
        application.populateMenuItems();
        return application;
    }
    protected boolean compareOptionAndRadioButton(String optionText,
            Component radioButton) {
        if (radioButton instanceof EditableRadioButton == false) {
            return false;
        }
        EditableRadioButton rButton = (EditableRadioButton) radioButton;
        if (optionText.equals(rButton.getText())) {
            return true;
        }
        return false;
    }
    
    protected JComponent getComponentFromOption(RendererHandler rHandler, 
            OptionEntry entry, int rowIndex, Option  option,
            boolean radioButton) {
        Renderer renderer = rHandler.getExistingRenderer(entry, rowIndex);
        if (renderer == null) {
            throw new IllegalStateException("entry does not exist in the document: "
                    + entry);
        }
        List<JComponent> comps = renderer.getComponents();
        String optionText = RendererHelper.getInstance().getOptionText(entry, option);
        for (JComponent comp : comps) {
            if (comp instanceof JPanel) {
                for (Component panelComp : comp.getComponents()) {
                    if (compareOptionAndRadioButton(optionText, panelComp)) {
                        return (radioButton ? (JComponent) panelComp : comp);
                    }
                }
            }
            if (compareOptionAndRadioButton(optionText, comp)) {
                return comp;
            }
        }
        
        return null;
    }
    
    protected void addDateResponse(DocumentInstance docInstance,
            SectionOccurrence sectionOcc, DateEntry dateEntry,
            Date date) {
        IDateValue value = (IDateValue) addResponse(docInstance, sectionOcc,
                dateEntry);
        value.setValue(date);
    }
    
    protected void addDateResponse(DocumentInstance docInstance,
            SectionOccurrence sectionOcc, DateEntry dateEntry,
            Integer month, Integer year) {
        IDateValue value = (IDateValue) addResponse(docInstance, sectionOcc,
                dateEntry);
        value.setMonth(month);
        value.setYear(year);
    }
    
    protected void addTextResponse(DocumentInstance docInstance,
            SectionOccurrence sectionOcc, TextEntry textEntry,
            String text) {
        ITextValue value = (ITextValue) addResponse(docInstance, sectionOcc,
                textEntry);
        value.setValue(text);
    }
    
    protected void addOptionResponse(DocumentInstance docInstance,
            SectionOccurrence sectionOcc, OptionEntry optionEntry,
            Option  option) {
        IOptionValue value = (IOptionValue) addResponse(docInstance, sectionOcc,
                optionEntry);
        value.setValue(option);
    }
    
    private IValue addResponse(DocumentInstance docInstance,
            SectionOccurrence sectionOcc, BasicEntry entry) {
        BasicResponse response = entry.generateInstance(sectionOcc);
        IValue value = entry.generateValue();
        response.setValue(value);
        docInstance.addResponse(response);
        return value;
    }
    
    /**
     * Gets the component in the list of options that matches <code>option</code>'s
     * text. In case a specific option corresponds to a JPanel, then the
     * <code>radioButton</code> parameter defines whether the EditableRadioButton
     * or the JPanel is returned.
     * @param rHandler
     * @param entry
     * @param radioButton
     * @return JComponent used to render <code>option</code>
     */
    protected JComponent getComponentFromOption(RendererHandler rHandler, 
            OptionEntry entry, int rowIndex, String optionText,
            boolean radioButton) {
        Renderer renderer = rHandler.getExistingRenderer(entry, rowIndex);
        if (renderer == null) {
            throw new IllegalStateException("entry does not exist in the document: "
                    + entry);
        }
        List<JComponent> comps = renderer.getComponents();
        for (JComponent comp : comps) {
            if (comp instanceof JPanel) {
                for (Component panelComp : comp.getComponents()) {
                    if (compareOptionAndRadioButton(optionText, panelComp)) {
                        return (radioButton ? (JComponent) panelComp : comp);
                    }
                }
            }
            if (compareOptionAndRadioButton(optionText, comp)) {
                return comp;
            }
        }
        
        return null;
    }
    
    protected JComponent getLabel(RendererHandler rendererHandler, Entry entry,
            int rowIndex) {
        BasicRenderer<?> renderer = 
            (BasicRenderer<?>) rendererHandler.getExistingRenderer(entry, rowIndex);
        return renderer.getLabel();
    }
    
    protected EntryComponent getFieldAsEntryComponent(RendererHandler rendererHandler, 
            Entry entry, int rowIndex) {
        BasicRenderer<?> renderer = 
            (BasicRenderer<?>) rendererHandler.getExistingRenderer(entry, rowIndex);
        return (EntryComponent) renderer.getField();
    }
    
    protected AbstractEditable getField(RendererHandler rendererHandler, 
            Entry entry, int rowIndex) {
        BasicRenderer<?> renderer = 
            (BasicRenderer<?>) rendererHandler.getExistingRenderer(entry, rowIndex);
        return (AbstractEditable) renderer.getField();
    }
    
    protected void setComboBoxOption(RendererHandler rendererHandler, 
            OptionEntry entry, int rowIndex, Option  option) {
        BasicRenderer<?> renderer = 
            (BasicRenderer<?>) rendererHandler.getExistingRenderer(entry, rowIndex);
        EditableComboBox comboBox = (EditableComboBox) renderer.getField();
        OptionComboBoxModel model = (OptionComboBoxModel) comboBox.getModel();
        model.setSelectedItem(RendererHelper.getInstance().getOptionText(entry, option));
    }
    
    protected void setOption(RendererHandler rendererHandler, OptionEntry entry,
            int rowIndex, Option  option) {
        EditableRadioButton radioButton = (EditableRadioButton) getComponentFromOption(
                rendererHandler, entry, rowIndex, option, true);
        radioButton.doClick();
    }
    
    protected void setOption(RendererHandler rendererHandler, OptionEntry entry,
            int rowIndex, StandardCode stdCode) {
        String optionText = RendererHelper.getInstance().getStandardCodeText(stdCode);
        EditableRadioButton radioButton = (EditableRadioButton) getComponentFromOption(
                rendererHandler, entry, rowIndex, optionText, true);
        radioButton.doClick();
    }
    
    protected List<User> getUsers() {
        List<User> users = new ArrayList<User>();
        char[] userName = "test".toCharArray(); //$NON-NLS-1$
        char[] password = "password".toCharArray(); //$NON-NLS-1$
        String hashedUserName = SecurityHelper.hash(userName);
        String hashedPassword = SecurityHelper.hash(password);
        User user = new User(hashedUserName, hashedPassword);
        users.add(user);
        return users;
    }

    protected String getSelectedComboBoxOption(RendererHandler rendererHandler, OptionEntry entry, int rowIndex) {
        List<JComponent> comps = 
            rendererHandler.getExistingRenderer(entry, rowIndex).getComponents();
        return getSelectedComboBoxOption(comps);
    }

    private String getTextValue(OptionEditableComboBoxModel model) {
        return (String) model.getTextValueModel().getValue();
    }
    
    protected String getSelectedComboBoxOption(List<JComponent> comps) {
        for (int i = 0, c = comps.size(); i < c; ++i) {
            JComponent comp = comps.get(i);
            if (comp instanceof EditableComboBox) {
                EditableComboBox cBox = (EditableComboBox) comp;
                return (String) cBox.getSelectedItem();
            }
        }

        return null;
    }

    protected String getSelectedComboBoxOptionText(List<JComponent> comps) {
        for (int i = 0, c = comps.size(); i < c; ++i) {
            JComponent comp = comps.get(i);
            if (comp instanceof EditableComboBox) {
                EditableComboBox cBox = (EditableComboBox) comp;
                if (cBox.getModel() instanceof OptionEditableComboBoxModel) {
                    return getTextValue((OptionEditableComboBoxModel) 
                            cBox.getModel());
                }
            }
        }

        return null;
    }

    protected String getSelectedComboBoxOptionText(RendererHandler rendererHandler, OptionEntry entry, int rowIndex) {
        List<JComponent> comps = 
            rendererHandler.getExistingRenderer(entry, rowIndex).getComponents();
        return getSelectedComboBoxOptionText(comps);
    }

    private String getSelectedRadioButtonOption(List<JComponent> comps) {
        for (JComponent comp : comps) {
            if (comp instanceof EditableRadioButton) {
                EditableRadioButton radioButton = (EditableRadioButton) comp;
                if (radioButton.isSelected()) {
                    return radioButton.getText();
                }
            }
            else if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                List<JComponent> panelComps = new ArrayList<JComponent>(
                        panel.getComponentCount());
                for (int j = 0; j < panel.getComponentCount(); ++j) {
                    panelComps.add((JComponent) panel.getComponent(j));
                }
                String selectedOption = getSelectedRadioButtonOption(panelComps);
                if (selectedOption != null) {
                    return selectedOption;
                }
            }
        }
    
        return null;
    }

    protected String getSelectedRadioButtonOption(RendererHandler rendererHandler, OptionEntry entry, int rowIndex) {
        List<JComponent> comps = 
            rendererHandler.getExistingRenderer(entry, rowIndex).getComponents();
        return getSelectedRadioButtonOption(comps);
    }

    protected void assertHeadings(EntryTable entryTable, String ... expectedHeadings) {
        Iterator<String> keySetIt = 
            entryTable.getModel().getHeadings().keySet().iterator();
        assertEquals(entryTable.getModel().getHeadings().size(), expectedHeadings.length);
        for (String expectedHeading : expectedHeadings) {
            String heading = keySetIt.next();
            assertEquals(expectedHeading, heading);
        }
    }

    protected List<Window> getOwnedWindowsShowing(Application app) {
        Window[] ownedWindows = app.getOwnedWindows();
        List<Window> showingWindows = new ArrayList<Window>();
        for (int i = 0, c = ownedWindows.length; i < c; ++i) {
            Window ownedWindow = ownedWindows[i];
            if (ownedWindow.isShowing()) {
                showingWindows.add(ownedWindow);
            }
        }
        return showingWindows;
    }

    protected <T extends Component> T getComponent(Class<T> klass,
            Container container, String propertyName, String propertyValue)
            throws Exception {
        for (int i = 0, c = container.getComponentCount(); i < c; ++i) {
            Component comp = container.getComponent(i);
            if (klass.isInstance(comp)) {
                if (propertyName == null && propertyValue == null) {
                    return klass.cast(comp);
                }
                String methodName = getGetter(propertyName);
                Method m = klass.getMethod(methodName);
                String value = (String) m.invoke(comp);
                if (value.equals(propertyValue)) {
                    return klass.cast(comp);
                }
            }
            if (comp instanceof Container) {
                T requestedComp = getComponent(klass, (Container) comp,
                        propertyName, propertyValue);
                if (requestedComp != null) {
                    return requestedComp;
                }
            }
        }
        return null;
    }

    private String getGetter(String propertyName) {
        String methodName = "get" + propertyName.substring(0, 1).toUpperCase() + 
                propertyName.substring(1, propertyName.length());
        return methodName;
    }

    protected JButton getJButton(Container container, String text) throws Exception {
        return getComponent(JButton.class, container, "text", text);
    }
    
    protected JLabel getJLabel(Container container, String text) throws Exception   {
        return getComponent(JLabel.class, container, "text", text);
    }

    public static void initLauncher() throws Exception {
        Launcher.initSafe();
        PersistenceManagerTestHelper.initPersistenceManager("", false);
    }

    protected static BuilderHandler getBuilderHandler(DocumentInstance
            docOccurrenceInstance, SectionOccurrence sectionOccurrence) {
        RendererHandler rendererHandler = getRendererHandler(sectionOccurrence);
        List<SectionPresModel> sectionPresModels = new ArrayList<SectionPresModel>();
        sectionPresModels.add(new SectionPresModel(sectionOccurrence));
        BuilderHandler builderHandler = new BuilderHandler(docOccurrenceInstance,
                getStandardCodes(), sectionPresModels);
        builderHandler.addRendererHandler(rendererHandler);
        return builderHandler;
    }
    
    protected static RendererHandler getRendererHandler(SectionOccurrence
            sectionOccurrence) {
        return new RendererHandler(new SectionPresModel(sectionOccurrence));
    }
    
    protected static List<StandardCode> getStandardCodes() {
        List<StandardCode> codes = new ArrayList<StandardCode>();

        StandardCode sc1 = getFactory().createStandardCode("Data not known", 960);
        StandardCode sc2 = getFactory().createStandardCode("Not applicable", 970);
        StandardCode sc3 = getFactory().createStandardCode("Refused to answer", 980);
        StandardCode sc4 = getFactory().createStandardCode("Data unable to be captured", 999);
        sc4.setUsedForDerivedEntry(true);
        codes.add(sc1);
        codes.add(sc2);
        codes.add(sc3);
        codes.add(sc4);

        return codes;
    }
    
    protected static Factory getFactory() {
        return new HibernateFactory();
    }
    
    protected static DataSet getDataSet() throws Exception {
        DataSet dataSet = getFactory().createDataset("Test dataset",
                "Test dataset");
        setId(dataSet);
        dataSet.setProjectCode("OLK");
        return dataSet;
    }
    
    protected static Document getDocument() {
        Document doc = getFactory().createDocument("Personal Details",
                "Personal Details Form");
        createDocumentStatuses(doc);
        return doc;
    }
    
    protected static DocumentOccurrence getDocumentOccurrence() {
        DocumentOccurrence docOccurrence = getFactory().createDocumentOccurrence("Baseline");
        Document doc = getDocument();
        doc.addOccurrence(docOccurrence);
        return docOccurrence;
    }
    
    protected static Document getDocumentWithDataSet() throws Exception {
        Document doc = getDocument();
        getDataSet().addDocument(doc);
        return doc;
    }
    
    protected static SectionOccurrence getSectionOccurrence(String name) {
        Section mainSec = getFactory().createSection(name, name);
        SectionOccurrence sectionOcc =
            getFactory().createSectionOccurrence(name + " section occurrence");
        mainSec.addOccurrence(sectionOcc);
        return sectionOcc;
    }
    
    protected static SectionOccurrence getSectionOccurrence() {
        return getSectionOccurrence("Main");
    }
    protected static SectionOccurrence getSectionWithParents() throws Exception    {
        SectionOccurrence sectionOcc = getSectionOccurrence();
        getDocumentWithDataSet().addSection(sectionOcc.getSection());
        return sectionOcc;
    }
    
    public static <T>T setStatus(T statused,
            Status status) throws Exception   {
        Method m;
        m = statused.getClass().getMethod("setStatus", Status.class);
        m.invoke(statused, status);
        return statused;
    }
    
    public static <T extends IValue>T setTransformed(T value, 
            boolean transformed) throws Exception   {
        Method m;
        m = value.getClass().getMethod("setTransformed", boolean.class);
        m.invoke(value, Boolean.valueOf(transformed));
        return value;
    }
    
    public static <T extends IPersistent> T setId(T persistent) throws Exception {
        Method m;
        m = persistent.getClass().getMethod("setId", Long.class);
        Long tempId;
        synchronized (AbstractEntryTestCase.class) {
            tempId = Long.valueOf(id);
            ++id;
        }
        m.invoke(persistent, tempId);
        return persistent;
    }
    
    static final class EditDialogLauncherHolder {
        private MouseListener editDialogLauncher;
        private MouseEvent mouseEvent;

        public synchronized final void setEditDialogLauncher(MouseListener editDialogLauncher) {
            this.editDialogLauncher = editDialogLauncher;
        }
        public synchronized final void setMouseEvent(MouseEvent mouseEvent) {
            this.mouseEvent = mouseEvent;
        }
        
        public synchronized void launch() {
            editDialogLauncher.mouseClicked(mouseEvent);
        }
    }
    
    protected EditDialogLauncherHolder getEditDialogLauncherHolder(
            final RendererHandler rendererHandler, final Entry entry)
            throws Exception {
        final EditDialogLauncherHolder info = new EditDialogLauncherHolder();
        invokeAndWait(new Executable() {
            public void execute() throws Exception {
                JComponent comp = getLabel(rendererHandler, entry, 0);
                if (comp == null) {
                    comp = getField(rendererHandler, entry, 0);
                }
                populateEditDialogLauncherHolder(info, comp);
            }
        });
        return info;
    }
    
    protected void launchEditDialogBox(
            final EditDialogLauncherHolder editDialogLauncherHolder,
            Executable executable) throws Exception {
        invokeLaterAndWait(new Executable() {
            public void execute() throws Exception {
                editDialogLauncherHolder.launch();
            }
        }, executable);
    }

    protected void populateEditDialogLauncherHolder(
            final EditDialogLauncherHolder editDialogLauncherHolder, JComponent component) {
        MouseListener editDialogLauncher = null;
        for (MouseListener listener : component.getMouseListeners()) {
            if (listener instanceof BasicRendererMouseListener || 
                    listener instanceof EditDialogLauncher) {
                editDialogLauncher = listener;
                break;
            }
        }
        assertNotNull(editDialogLauncher);
        editDialogLauncherHolder.setEditDialogLauncher(editDialogLauncher);
        editDialogLauncherHolder.setMouseEvent(new MouseEvent(component,
                MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, 2, false));
    }
    /**
     * Invokes <code>asyncExecutable</code> by calling EventQueue.invokeLater
     * and invokes <code>syncExecutable</code> if not <code>null</code> by
     * calling EventQueue.invokeAndWait.
     * 
     * If an exception is thrown from asyncExecutable, it's rethrown in
     * syncExecutable.
     * 
     * @param asyncExecutable
     * @param syncExecutable
     */
    protected static void invokeLaterAndWait(final Executable asyncExecutable,
            final Executable syncExecutable) throws Exception   {
        final ValueHolder<Exception> exception = ValueHolder.create(null);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    asyncExecutable.execute();
                } catch (Exception e) {
                    exception.setValue(e);
                }
            }
        });
        if (syncExecutable == null) {
            return;
        }
        Executable asyncExecWithException = new Executable() {
            public void execute() throws Exception {
                if (exception.getValue() != null) {
                    throw exception.getValue();
                }
                syncExecutable.execute();
            }
        };
        EventQueue.invokeAndWait(toRunnable(asyncExecWithException));
    }
    
    protected static void invokeAndWait(Executable executable) throws Exception {
        Runnable r = toRunnable(executable);
        EventQueue.invokeAndWait(r);
    }

    private static Runnable toRunnable(final Executable executable) {
        Runnable r = new Runnable() {
            public void run() {
                try {
                    executable.execute();
                } catch (Exception e) {
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    }
                    /* 
                     * Wrap into RuntimeException because Runnable doesn't allow
                     * Exception or subclasses to be thrown.
                     */
                    throw new RuntimeException(e);
                }
            }
        };
        return r;
    }
}
