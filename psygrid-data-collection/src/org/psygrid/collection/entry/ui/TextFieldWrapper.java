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

import java.awt.AWTException;
import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.MenuComponent;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.im.InputContext;
import java.awt.im.InputMethodRequests;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Locale;
import java.util.Set;

import javax.accessibility.AccessibleContext;
import javax.swing.Action;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.CaretListener;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.Keymap;
import javax.swing.text.NavigationFilter;

import org.psygrid.collection.entry.Application;

/**
 * Wraps a JTextField and simply routes all the method calls to the wrapped item
 * with the exception of {@link #scrollRectToVisible(Rectangle)}. In this
 * case, in addition to calling the method in the wrapped text field, the
 * superclass method is also called (<code>JComponent#scrollRectToVisible(Rectangle)</code>).
 * This is required in case one wants <code>scrollRectToVisible</code> to cause
 * the text field to become visible if it's inside a JScrollPane and outside of
 * the visible area. This wrapper should only be used in this circumstance.<p>
 * 
 * Aside: it seems to me that this should be the default behaviour of JTextField.
 * It's unclear whether it's a bug or intentional.
 *  
 * @author Ismael Juma (ismael@juma.me.uk)
 *
 * @see Application#moveScrollBarToShowFocusedComponent(PropertyChangeEvent)
 */
@SuppressWarnings("all")
public class TextFieldWrapper extends JComponent    {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final JTextField wrappedTextField;
    
    public TextFieldWrapper(JTextField wrappedTextField) {
        this. wrappedTextField = wrappedTextField;
    }

    public boolean action(Event evt, Object what) {
        return wrappedTextField.action(evt, what);
    }

    public Component add(Component comp, int index) {
        return wrappedTextField.add(comp, index);
    }

    public void add(Component comp, Object constraints, int index) {
        wrappedTextField.add(comp, constraints, index);
    }

    public void add(Component comp, Object constraints) {
        wrappedTextField.add(comp, constraints);
    }

    public Component add(Component comp) {
        return wrappedTextField.add(comp);
    }

    public void add(PopupMenu popup) {
        wrappedTextField.add(popup);
    }

    public Component add(String name, Component comp) {
        return wrappedTextField.add(name, comp);
    }

    public void addActionListener(ActionListener l) {
        wrappedTextField.addActionListener(l);
    }

    public void addCaretListener(CaretListener listener) {
        wrappedTextField.addCaretListener(listener);
    }

    public void addComponentListener(ComponentListener l) {
        wrappedTextField.addComponentListener(l);
    }

    public void addContainerListener(ContainerListener l) {
        wrappedTextField.addContainerListener(l);
    }

    public void addFocusListener(FocusListener l) {
        wrappedTextField.addFocusListener(l);
    }

    public void addHierarchyBoundsListener(HierarchyBoundsListener l) {
        wrappedTextField.addHierarchyBoundsListener(l);
    }

    public void addHierarchyListener(HierarchyListener l) {
        wrappedTextField.addHierarchyListener(l);
    }

    public void addInputMethodListener(InputMethodListener l) {
        wrappedTextField.addInputMethodListener(l);
    }

    public void addKeyListener(KeyListener l) {
        wrappedTextField.addKeyListener(l);
    }

    public void addMouseListener(MouseListener l) {
        wrappedTextField.addMouseListener(l);
    }

    public void addMouseMotionListener(MouseMotionListener l) {
        wrappedTextField.addMouseMotionListener(l);
    }

    public void addMouseWheelListener(MouseWheelListener l) {
        wrappedTextField.addMouseWheelListener(l);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        wrappedTextField.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        wrappedTextField.addPropertyChangeListener(propertyName, listener);
    }

    public void applyComponentOrientation(ComponentOrientation o) {
        wrappedTextField.applyComponentOrientation(o);
    }

    public boolean areFocusTraversalKeysSet(int id) {
        return wrappedTextField.areFocusTraversalKeysSet(id);
    }

    public Rectangle bounds() {
        return wrappedTextField.bounds();
    }

    public int checkImage(Image image, ImageObserver observer) {
        return wrappedTextField.checkImage(image, observer);
    }

    public int checkImage(Image image, int width, int height, ImageObserver observer) {
        return wrappedTextField.checkImage(image, width, height, observer);
    }

    public boolean contains(Point p) {
        return wrappedTextField.contains(p);
    }

    public void copy() {
        wrappedTextField.copy();
    }

    public int countComponents() {
        return wrappedTextField.countComponents();
    }

    public Image createImage(ImageProducer producer) {
        return wrappedTextField.createImage(producer);
    }

    public Image createImage(int width, int height) {
        return wrappedTextField.createImage(width, height);
    }

    public VolatileImage createVolatileImage(int width, int height, ImageCapabilities caps) throws AWTException {
        return wrappedTextField.createVolatileImage(width, height, caps);
    }

    public VolatileImage createVolatileImage(int width, int height) {
        return wrappedTextField.createVolatileImage(width, height);
    }

    public void cut() {
        wrappedTextField.cut();
    }

    public void deliverEvent(Event e) {
        wrappedTextField.deliverEvent(e);
    }

    public void doLayout() {
        wrappedTextField.doLayout();
    }

    public void enable(boolean b) {
        wrappedTextField.enable(b);
    }

    public void enableInputMethods(boolean enable) {
        wrappedTextField.enableInputMethods(enable);
    }

    public boolean equals(Object obj) {
        return wrappedTextField.equals(obj);
    }

    public Component findComponentAt(int x, int y) {
        return wrappedTextField.findComponentAt(x, y);
    }

    public Component findComponentAt(Point p) {
        return wrappedTextField.findComponentAt(p);
    }

    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
        wrappedTextField.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
        wrappedTextField.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
        wrappedTextField.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
        wrappedTextField.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
        wrappedTextField.firePropertyChange(propertyName, oldValue, newValue);
    }

    public AccessibleContext getAccessibleContext() {
        return wrappedTextField.getAccessibleContext();
    }

    public Action getAction() {
        return wrappedTextField.getAction();
    }

    public ActionListener[] getActionListeners() {
        return wrappedTextField.getActionListeners();
    }

    public Action[] getActions() {
        return wrappedTextField.getActions();
    }

    public Color getBackground() {
        return wrappedTextField.getBackground();
    }

    public Rectangle getBounds() {
        return wrappedTextField.getBounds();
    }

    public Caret getCaret() {
        return wrappedTextField.getCaret();
    }

    public Color getCaretColor() {
        return wrappedTextField.getCaretColor();
    }

    public CaretListener[] getCaretListeners() {
        return wrappedTextField.getCaretListeners();
    }

    public int getCaretPosition() {
        return wrappedTextField.getCaretPosition();
    }

    public ColorModel getColorModel() {
        return wrappedTextField.getColorModel();
    }

    public int getColumns() {
        return wrappedTextField.getColumns();
    }

    public Component getComponent(int n) {
        return wrappedTextField.getComponent(n);
    }

    public Component getComponentAt(int x, int y) {
        return wrappedTextField.getComponentAt(x, y);
    }

    public Component getComponentAt(Point p) {
        return wrappedTextField.getComponentAt(p);
    }

    public int getComponentCount() {
        return wrappedTextField.getComponentCount();
    }

    public ComponentListener[] getComponentListeners() {
        return wrappedTextField.getComponentListeners();
    }

    public ComponentOrientation getComponentOrientation() {
        return wrappedTextField.getComponentOrientation();
    }

    public Component[] getComponents() {
        return wrappedTextField.getComponents();
    }

    public ContainerListener[] getContainerListeners() {
        return wrappedTextField.getContainerListeners();
    }

    public Cursor getCursor() {
        return wrappedTextField.getCursor();
    }

    public Color getDisabledTextColor() {
        return wrappedTextField.getDisabledTextColor();
    }

    public Document getDocument() {
        return wrappedTextField.getDocument();
    }

    public boolean getDragEnabled() {
        return wrappedTextField.getDragEnabled();
    }

    public DropTarget getDropTarget() {
        return wrappedTextField.getDropTarget();
    }

    public char getFocusAccelerator() {
        return wrappedTextField.getFocusAccelerator();
    }

    public Container getFocusCycleRootAncestor() {
        return wrappedTextField.getFocusCycleRootAncestor();
    }

    public FocusListener[] getFocusListeners() {
        return wrappedTextField.getFocusListeners();
    }

    public Set<AWTKeyStroke> getFocusTraversalKeys(int id) {
        return wrappedTextField.getFocusTraversalKeys(id);
    }

    public boolean getFocusTraversalKeysEnabled() {
        return wrappedTextField.getFocusTraversalKeysEnabled();
    }

    public FocusTraversalPolicy getFocusTraversalPolicy() {
        return wrappedTextField.getFocusTraversalPolicy();
    }

    public Font getFont() {
        return wrappedTextField.getFont();
    }

    public Color getForeground() {
        return wrappedTextField.getForeground();
    }

    public GraphicsConfiguration getGraphicsConfiguration() {
        return wrappedTextField.getGraphicsConfiguration();
    }

    public HierarchyBoundsListener[] getHierarchyBoundsListeners() {
        return wrappedTextField.getHierarchyBoundsListeners();
    }

    public HierarchyListener[] getHierarchyListeners() {
        return wrappedTextField.getHierarchyListeners();
    }

    public Highlighter getHighlighter() {
        return wrappedTextField.getHighlighter();
    }

    public int getHorizontalAlignment() {
        return wrappedTextField.getHorizontalAlignment();
    }

    public BoundedRangeModel getHorizontalVisibility() {
        return wrappedTextField.getHorizontalVisibility();
    }

    public boolean getIgnoreRepaint() {
        return wrappedTextField.getIgnoreRepaint();
    }

    public InputContext getInputContext() {
        return wrappedTextField.getInputContext();
    }

    public InputMethodListener[] getInputMethodListeners() {
        return wrappedTextField.getInputMethodListeners();
    }

    public InputMethodRequests getInputMethodRequests() {
        return wrappedTextField.getInputMethodRequests();
    }

    public KeyListener[] getKeyListeners() {
        return wrappedTextField.getKeyListeners();
    }

    public Keymap getKeymap() {
        return wrappedTextField.getKeymap();
    }

    public LayoutManager getLayout() {
        return wrappedTextField.getLayout();
    }

    public Locale getLocale() {
        return wrappedTextField.getLocale();
    }

    public Point getLocation() {
        return wrappedTextField.getLocation();
    }

    public Point getLocationOnScreen() {
        return wrappedTextField.getLocationOnScreen();
    }

    public Insets getMargin() {
        return wrappedTextField.getMargin();
    }

    public MouseListener[] getMouseListeners() {
        return wrappedTextField.getMouseListeners();
    }

    public MouseMotionListener[] getMouseMotionListeners() {
        return wrappedTextField.getMouseMotionListeners();
    }

    public Point getMousePosition() throws HeadlessException {
        return wrappedTextField.getMousePosition();
    }

    public Point getMousePosition(boolean allowChildren) throws HeadlessException {
        return wrappedTextField.getMousePosition(allowChildren);
    }

    public MouseWheelListener[] getMouseWheelListeners() {
        return wrappedTextField.getMouseWheelListeners();
    }

    public String getName() {
        return wrappedTextField.getName();
    }

    public NavigationFilter getNavigationFilter() {
        return wrappedTextField.getNavigationFilter();
    }

    public Container getParent() {
        return wrappedTextField.getParent();
    }

    public ComponentPeer getPeer() {
        return wrappedTextField.getPeer();
    }

    public Dimension getPreferredScrollableViewportSize() {
        return wrappedTextField.getPreferredScrollableViewportSize();
    }

    public Dimension getPreferredSize() {
        return wrappedTextField.getPreferredSize();
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return wrappedTextField.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return wrappedTextField.getPropertyChangeListeners(propertyName);
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return wrappedTextField.getScrollableBlockIncrement(visibleRect, orientation, direction);
    }

    public boolean getScrollableTracksViewportHeight() {
        return wrappedTextField.getScrollableTracksViewportHeight();
    }

    public boolean getScrollableTracksViewportWidth() {
        return wrappedTextField.getScrollableTracksViewportWidth();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return wrappedTextField.getScrollableUnitIncrement(visibleRect, orientation, direction);
    }

    public int getScrollOffset() {
        return wrappedTextField.getScrollOffset();
    }

    public String getSelectedText() {
        return wrappedTextField.getSelectedText();
    }

    public Color getSelectedTextColor() {
        return wrappedTextField.getSelectedTextColor();
    }

    public Color getSelectionColor() {
        return wrappedTextField.getSelectionColor();
    }

    public int getSelectionEnd() {
        return wrappedTextField.getSelectionEnd();
    }

    public int getSelectionStart() {
        return wrappedTextField.getSelectionStart();
    }

    public Dimension getSize() {
        return wrappedTextField.getSize();
    }

    public String getText() {
        return wrappedTextField.getText();
    }

    public String getText(int offs, int len) throws BadLocationException {
        return wrappedTextField.getText(offs, len);
    }

    public Toolkit getToolkit() {
        return wrappedTextField.getToolkit();
    }

    public String getToolTipText(MouseEvent event) {
        return wrappedTextField.getToolTipText(event);
    }

    public TextUI getUI() {
        return wrappedTextField.getUI();
    }

    public String getUIClassID() {
        return wrappedTextField.getUIClassID();
    }

    public boolean gotFocus(Event evt, Object what) {
        return wrappedTextField.gotFocus(evt, what);
    }

    public boolean handleEvent(Event evt) {
        return wrappedTextField.handleEvent(evt);
    }

    public boolean hasFocus() {
        return wrappedTextField.hasFocus();
    }

    public int hashCode() {
        return wrappedTextField.hashCode();
    }

    public void hide() {
        wrappedTextField.hide();
    }

    public boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h) {
        return wrappedTextField.imageUpdate(img, infoflags, x, y, w, h);
    }

    public Insets insets() {
        return wrappedTextField.insets();
    }

    public boolean inside(int x, int y) {
        return wrappedTextField.inside(x, y);
    }

    public void invalidate() {
        wrappedTextField.invalidate();
    }

    public boolean isAncestorOf(Component c) {
        return wrappedTextField.isAncestorOf(c);
    }

    public boolean isBackgroundSet() {
        return wrappedTextField.isBackgroundSet();
    }

    public boolean isCursorSet() {
        return wrappedTextField.isCursorSet();
    }

    public boolean isDisplayable() {
        return wrappedTextField.isDisplayable();
    }

    public boolean isEditable() {
        return wrappedTextField.isEditable();
    }

    public boolean isEnabled() {
        return wrappedTextField.isEnabled();
    }

    public boolean isFocusable() {
        return wrappedTextField.isFocusable();
    }

    public boolean isFocusCycleRoot() {
        return wrappedTextField.isFocusCycleRoot();
    }

    public boolean isFocusCycleRoot(Container container) {
        return wrappedTextField.isFocusCycleRoot(container);
    }

    public boolean isFocusOwner() {
        return wrappedTextField.isFocusOwner();
    }

    public boolean isFocusTraversable() {
        return wrappedTextField.isFocusTraversable();
    }

    public boolean isFocusTraversalPolicySet() {
        return wrappedTextField.isFocusTraversalPolicySet();
    }

    public boolean isFontSet() {
        return wrappedTextField.isFontSet();
    }

    public boolean isForegroundSet() {
        return wrappedTextField.isForegroundSet();
    }

    public boolean isLightweight() {
        return wrappedTextField.isLightweight();
    }

    public boolean isMaximumSizeSet() {
        return wrappedTextField.isMaximumSizeSet();
    }

    public boolean isMinimumSizeSet() {
        return wrappedTextField.isMinimumSizeSet();
    }

    public boolean isPreferredSizeSet() {
        return wrappedTextField.isPreferredSizeSet();
    }

    public boolean isShowing() {
        return wrappedTextField.isShowing();
    }

    public boolean isValid() {
    	 if(wrappedTextField != null){
    		 return wrappedTextField.isValid();	
    	 }else{
    		 return false;
    	 }
        
    }

    public boolean isValidateRoot() {
        return wrappedTextField.isValidateRoot();
    }

    public boolean isVisible() {
        return wrappedTextField.isVisible();
    }

    public boolean keyDown(Event evt, int key) {
        return wrappedTextField.keyDown(evt, key);
    }

    public boolean keyUp(Event evt, int key) {
        return wrappedTextField.keyUp(evt, key);
    }

    public void layout() {
        wrappedTextField.layout();
    }

    public void list() {
        wrappedTextField.list();
    }

    public void list(PrintStream out, int indent) {
        wrappedTextField.list(out, indent);
    }

    public void list(PrintStream out) {
        wrappedTextField.list(out);
    }

    public void list(PrintWriter out, int indent) {
        wrappedTextField.list(out, indent);
    }

    public void list(PrintWriter out) {
        wrappedTextField.list(out);
    }

    public Component locate(int x, int y) {
        return wrappedTextField.locate(x, y);
    }

    public Point location() {
        return wrappedTextField.location();
    }

    public boolean lostFocus(Event evt, Object what) {
        return wrappedTextField.lostFocus(evt, what);
    }

    public Dimension minimumSize() {
        return wrappedTextField.minimumSize();
    }

    public Rectangle modelToView(int pos) throws BadLocationException {
        return wrappedTextField.modelToView(pos);
    }

    public boolean mouseDown(Event evt, int x, int y) {
        return wrappedTextField.mouseDown(evt, x, y);
    }

    public boolean mouseDrag(Event evt, int x, int y) {
        return wrappedTextField.mouseDrag(evt, x, y);
    }

    public boolean mouseEnter(Event evt, int x, int y) {
        return wrappedTextField.mouseEnter(evt, x, y);
    }

    public boolean mouseExit(Event evt, int x, int y) {
        return wrappedTextField.mouseExit(evt, x, y);
    }

    public boolean mouseMove(Event evt, int x, int y) {
        return wrappedTextField.mouseMove(evt, x, y);
    }

    public boolean mouseUp(Event evt, int x, int y) {
        return wrappedTextField.mouseUp(evt, x, y);
    }

    public void move(int x, int y) {
        wrappedTextField.move(x, y);
    }

    public void moveCaretPosition(int pos) {
        wrappedTextField.moveCaretPosition(pos);
    }

    public void nextFocus() {
        wrappedTextField.nextFocus();
    }

    public void paintAll(Graphics g) {
        wrappedTextField.paintAll(g);
    }

    public void paintComponents(Graphics g) {
        wrappedTextField.paintComponents(g);
    }

    public void paste() {
        wrappedTextField.paste();
    }

    public void postActionEvent() {
        wrappedTextField.postActionEvent();
    }

    public boolean postEvent(Event e) {
        return wrappedTextField.postEvent(e);
    }

    public Dimension preferredSize() {
        return wrappedTextField.preferredSize();
    }

    public boolean prepareImage(Image image, ImageObserver observer) {
        return wrappedTextField.prepareImage(image, observer);
    }

    public boolean prepareImage(Image image, int width, int height, ImageObserver observer) {
        return wrappedTextField.prepareImage(image, width, height, observer);
    }

    public void printComponents(Graphics g) {
        wrappedTextField.printComponents(g);
    }

    public void read(Reader in, Object desc) throws IOException {
        wrappedTextField.read(in, desc);
    }

    public void remove(Component comp) {
        wrappedTextField.remove(comp);
    }

    public void remove(int index) {
        wrappedTextField.remove(index);
    }

    public void remove(MenuComponent popup) {
        wrappedTextField.remove(popup);
    }

    public void removeActionListener(ActionListener l) {
        wrappedTextField.removeActionListener(l);
    }

    public void removeAll() {
        wrappedTextField.removeAll();
    }

    public void removeCaretListener(CaretListener listener) {
        wrappedTextField.removeCaretListener(listener);
    }

    public void removeComponentListener(ComponentListener l) {
        wrappedTextField.removeComponentListener(l);
    }

    public void removeContainerListener(ContainerListener l) {
        wrappedTextField.removeContainerListener(l);
    }

    public void removeFocusListener(FocusListener l) {
        wrappedTextField.removeFocusListener(l);
    }

    public void removeHierarchyBoundsListener(HierarchyBoundsListener l) {
        wrappedTextField.removeHierarchyBoundsListener(l);
    }

    public void removeHierarchyListener(HierarchyListener l) {
        wrappedTextField.removeHierarchyListener(l);
    }

    public void removeInputMethodListener(InputMethodListener l) {
        wrappedTextField.removeInputMethodListener(l);
    }

    public void removeKeyListener(KeyListener l) {
        wrappedTextField.removeKeyListener(l);
    }

    public void removeMouseListener(MouseListener l) {
        wrappedTextField.removeMouseListener(l);
    }

    public void removeMouseMotionListener(MouseMotionListener l) {
        wrappedTextField.removeMouseMotionListener(l);
    }

    public void removeMouseWheelListener(MouseWheelListener l) {
        wrappedTextField.removeMouseWheelListener(l);
    }

    public void removeNotify() {
        wrappedTextField.removeNotify();
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        wrappedTextField.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        wrappedTextField.removePropertyChangeListener(propertyName, listener);
    }

    public void repaint() {
        wrappedTextField.repaint();
    }

    public void repaint(int x, int y, int width, int height) {
        wrappedTextField.repaint(x, y, width, height);
    }

    public void repaint(long tm) {
        wrappedTextField.repaint(tm);
    }

    public void replaceSelection(String content) {
        wrappedTextField.replaceSelection(content);
    }

    public void resize(Dimension d) {
        wrappedTextField.resize(d);
    }

    public void resize(int width, int height) {
        wrappedTextField.resize(width, height);
    }

    public void scrollRectToVisible(Rectangle r) {
        wrappedTextField.scrollRectToVisible(r);
        super.scrollRectToVisible(r);
    }

    public void select(int selectionStart, int selectionEnd) {
        wrappedTextField.select(selectionStart, selectionEnd);
    }

    public void selectAll() {
        wrappedTextField.selectAll();
    }

    public void setAction(Action a) {
        wrappedTextField.setAction(a);
    }

    public void setActionCommand(String command) {
        wrappedTextField.setActionCommand(command);
    }

    public void setBounds(int x, int y, int width, int height) {
        wrappedTextField.setBounds(x, y, width, height);
    }

    public void setBounds(Rectangle r) {
        wrappedTextField.setBounds(r);
    }

    public void setCaret(Caret c) {
        wrappedTextField.setCaret(c);
    }

    public void setCaretColor(Color c) {
        wrappedTextField.setCaretColor(c);
    }

    public void setCaretPosition(int position) {
        wrappedTextField.setCaretPosition(position);
    }

    public void setColumns(int columns) {
        wrappedTextField.setColumns(columns);
    }

    public void setComponentOrientation(ComponentOrientation o) {
        wrappedTextField.setComponentOrientation(o);
    }

    public void setCursor(Cursor cursor) {
        wrappedTextField.setCursor(cursor);
    }

    public void setDisabledTextColor(Color c) {
        wrappedTextField.setDisabledTextColor(c);
    }

    public void setDocument(Document doc) {
        wrappedTextField.setDocument(doc);
    }

    public void setDragEnabled(boolean b) {
        wrappedTextField.setDragEnabled(b);
    }

    public void setDropTarget(DropTarget dt) {
        wrappedTextField.setDropTarget(dt);
    }

    public void setEditable(boolean b) {
        wrappedTextField.setEditable(b);
    }

    public void setFocusable(boolean focusable) {
        wrappedTextField.setFocusable(focusable);
    }

    public void setFocusAccelerator(char aKey) {
        wrappedTextField.setFocusAccelerator(aKey);
    }

    public void setFocusCycleRoot(boolean focusCycleRoot) {
        wrappedTextField.setFocusCycleRoot(focusCycleRoot);
    }

    public void setFocusTraversalKeysEnabled(boolean focusTraversalKeysEnabled) {
        wrappedTextField.setFocusTraversalKeysEnabled(focusTraversalKeysEnabled);
    }

    public void setFocusTraversalPolicy(FocusTraversalPolicy policy) {
        wrappedTextField.setFocusTraversalPolicy(policy);
    }

    public void setFont(Font f) {
        wrappedTextField.setFont(f);
    }

    public void setHighlighter(Highlighter h) {
        wrappedTextField.setHighlighter(h);
    }

    public void setHorizontalAlignment(int alignment) {
        wrappedTextField.setHorizontalAlignment(alignment);
    }

    public void setIgnoreRepaint(boolean ignoreRepaint) {
        wrappedTextField.setIgnoreRepaint(ignoreRepaint);
    }

    public void setKeymap(Keymap map) {
        wrappedTextField.setKeymap(map);
    }

    public void setLayout(LayoutManager mgr) {
        wrappedTextField.setLayout(mgr);
    }

    public void setLocale(Locale l) {
    	System.out.println("setLocale");
        wrappedTextField.setLocale(l);
    }

    public void setLocation(int x, int y) {
        wrappedTextField.setLocation(x, y);
    }

    public void setLocation(Point p) {
        wrappedTextField.setLocation(p);
    }

    public void setMargin(Insets m) {
        wrappedTextField.setMargin(m);
    }

    public void setName(String name) {
        wrappedTextField.setName(name);
    }

    public void setNavigationFilter(NavigationFilter filter) {
        wrappedTextField.setNavigationFilter(filter);
    }

    public void setScrollOffset(int scrollOffset) {
        wrappedTextField.setScrollOffset(scrollOffset);
    }

    public void setSelectedTextColor(Color c) {
        wrappedTextField.setSelectedTextColor(c);
    }

    public void setSelectionColor(Color c) {
        wrappedTextField.setSelectionColor(c);
    }

    public void setSelectionEnd(int selectionEnd) {
        wrappedTextField.setSelectionEnd(selectionEnd);
    }

    public void setSelectionStart(int selectionStart) {
        wrappedTextField.setSelectionStart(selectionStart);
    }

    public void setSize(Dimension d) {
        wrappedTextField.setSize(d);
    }

    public void setSize(int width, int height) {
        wrappedTextField.setSize(width, height);
    }

    public void setText(String t) {
        wrappedTextField.setText(t);
    }

    public void setUI(TextUI ui) {
        wrappedTextField.setUI(ui);
    }

    public void show() {
        wrappedTextField.show();
    }

    public void show(boolean b) {
        wrappedTextField.show(b);
    }

    public Dimension size() {
        return wrappedTextField.size();
    }

    public String toString() {
        return wrappedTextField.toString();
    }

    public void transferFocus() {
        wrappedTextField.transferFocus();
    }

    public void transferFocusBackward() {
        wrappedTextField.transferFocusBackward();
    }

    public void transferFocusDownCycle() {
        wrappedTextField.transferFocusDownCycle();
    }

    public void transferFocusUpCycle() {
        wrappedTextField.transferFocusUpCycle();
    }

    public void updateUI() {
        wrappedTextField.updateUI();
    }

    public void validate() {
        wrappedTextField.validate();
    }

    public int viewToModel(Point pt) {
        return wrappedTextField.viewToModel(pt);
    }

    public void write(Writer out) throws IOException {
        wrappedTextField.write(out);
    }
}
