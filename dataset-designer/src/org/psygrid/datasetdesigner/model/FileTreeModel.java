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
package org.psygrid.datasetdesigner.model;

import java.io.File;
import javax.swing.tree.TreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.*;
import javax.swing.tree.TreePath;

/**
 * @author pwhelan
 *
 */
/**
 * The methods in this class allow the JTree component to traverse
 * the file system tree, and display the files and directories.
 **/
public class FileTreeModel implements TreeModel {
  // We specify the root directory when we create the model.
  protected DefaultMutableTreeNode root;
  public FileTreeModel(DefaultMutableTreeNode root) { this.root = root; }

  // The model knows how to return the root object of the tree
  public Object getRoot() { return root; }

  // Tell JTree whether an object in the tree is a leaf or not
  public boolean isLeaf(Object node) {  return ((File)((DefaultMutableTreeNode)node).getUserObject()).isFile(); }

  // Tell JTree how many children a node has
  public int getChildCount(Object parent) {
    String[] children = ((File)((DefaultMutableTreeNode)parent).getUserObject()).list();
    if (children == null) return 0;
    return children.length;
  }

  // Fetch any numbered child of a node for the JTree.
  // Our model returns File objects for all nodes in the tree.  The
  // JTree displays these by calling the File.toString() method.
  public Object getChild(Object parent, int index) {
    String[] children = ((File)((DefaultMutableTreeNode)parent).getUserObject()).list();
    if ((children == null) || (index >= children.length)) return null;
    return new DefaultMutableTreeNode(new File(((File)((DefaultMutableTreeNode)parent).getUserObject()), children[index]));
  }

  // Figure out a child's position in its parent node.
  public int getIndexOfChild(Object parent, Object child) {
    String[] children = ((File)((DefaultMutableTreeNode)parent).getUserObject()).list();
    if (children == null) return -1;
    String childname = ((File)((DefaultMutableTreeNode)child).getUserObject()).getName();
    for(int i = 0; i < children.length; i++) {
      if (childname.equals(children[i])) return i;
    }
    return -1;
  }

  // This method is only invoked by the JTree for editable trees.  
  // This TreeModel does not allow editing, so we do not implement 
  // this method.  The JTree editable property is false by default.
  public void valueForPathChanged(TreePath path, Object newvalue) {}

  // Since this is not an editable tree model, we never fire any events,
  // so we don't actually have to keep track of interested listeners.
  public void addTreeModelListener(TreeModelListener l) {}
  public void removeTreeModelListener(TreeModelListener l) {}
}
