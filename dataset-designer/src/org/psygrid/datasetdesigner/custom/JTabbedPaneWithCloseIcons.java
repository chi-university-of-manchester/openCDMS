/**
* Cooperative Open Workflow (COW)                                      
* Copyright (C) 2005 Laboratoire TRIGONE - Equipe NOCE                 
*                                                                      
* This library is free software; you can redistribute it and/or        
* modify it under the terms of the GNU Lesser General Public           
* License as published by the Free Software Foundation; either         
* version 2.1 of the License, or any later version.                    
*                                                                      
* This library is distributed in the hope that it will be useful,      
* but WITHOUT ANY WARRANTY; without even the implied warranty of       
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU    
* Lesser General Public License for more details.                      
*                                                                      
* You should have received a copy of the GNU Lesser General Public     
* License along with this library; if not, write to the Free Software  
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 
* USA                                                                  
*                                                                      
* Initial developer(s): David Le Montagner                             
* Initial developer(s): Benoit Quevat                                  
*                                                                      
* $Id: JTabbedPaneWithCloseIcons.java,v 1.1 2005/12/09 16:29:12 moisev Exp $
**/
package org.psygrid.datasetdesigner.custom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * A JTabbedPane which has a close ('X') icon on each tab.
 *
 * To add a tab, use the method addTab(String, Component)
 *
 * To have an extra icon on each tab (e.g. like in JBuilder, showing the file type) use
 * the method addTab(String, Component, Icon). Only clicking the 'X' closes the tab.
 */
public class JTabbedPaneWithCloseIcons extends JTabbedPane implements MouseListener {
  public JTabbedPaneWithCloseIcons() {
    super();
    addMouseListener(this);
  }
 
  public void addTab(String title, Component component) {
    this.addTab(title, component, null);
  }
 
  public void addTab(String title, Component component, Icon extraIcon) {
    super.addTab(title, new CloseTabIcon(extraIcon), component);
  }
 
  public void cleanUp() {
	  
  }
  
  public void mouseClicked(MouseEvent e) {
    int tabNumber=getUI().tabForCoordinate(this, e.getX(), e.getY());
    if (tabNumber < 0) return;
    Rectangle rect=((CloseTabIcon)getIconAt(tabNumber)).getBounds();
    if (rect.contains(e.getX(), e.getY())) {
      //the tab is being closed
      this.removeTabAt(tabNumber);
      cleanUp();
    }
    
    
  }
 
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}
}
 
/**
 * The class which generates the 'X' icon for the tabs. The constructor
 * accepts an icon which is extra to the 'X' icon, so you can have tabs
 * like in JBuilder. This value is null if no extra icon is required.
 */
class CloseTabIcon implements Icon {
  private int xPos;
  private int yPos;
  private int width;
  private int height;
  private Icon fileIcon;
 
  public CloseTabIcon(Icon fileIcon) {
    this.fileIcon=fileIcon;
    width=16;
    height=16;
  }
 
  public void paintIcon(Component c, Graphics g, int x, int y) {
    this.xPos=x;
    this.yPos=y;
 
    Color col=g.getColor();
 
    g.setColor(Color.black);
    int y_p=y+2;
    g.drawLine(x+1, y_p, x+12, y_p);
    g.drawLine(x+1, y_p+13, x+12, y_p+13);
    g.drawLine(x, y_p+1, x, y_p+12);
    g.drawLine(x+13, y_p+1, x+13, y_p+12);
    g.drawLine(x+3, y_p+3, x+10, y_p+10);
    g.drawLine(x+3, y_p+4, x+9, y_p+10);
    g.drawLine(x+4, y_p+3, x+10, y_p+9);
    g.drawLine(x+10, y_p+3, x+3, y_p+10);
    g.drawLine(x+10, y_p+4, x+4, y_p+10);
    g.drawLine(x+9, y_p+3, x+3, y_p+9);
    g.setColor(col);
    if (fileIcon != null) {
      fileIcon.paintIcon(c, g, x+width, y_p);
    }
  }
 
  public int getIconWidth() {
    return width + (fileIcon != null? fileIcon.getIconWidth() : 0);
  }
 
  public int getIconHeight() {
    return height;
  }
 
  public Rectangle getBounds() {
    return new Rectangle(xPos, yPos, width, height);
  }
}
