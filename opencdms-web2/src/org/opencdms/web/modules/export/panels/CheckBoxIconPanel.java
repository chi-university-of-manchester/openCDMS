/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opencdms.web.modules.export.panels;

import javax.swing.tree.TreeNode;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.LabelIconPanel;
import org.apache.wicket.model.IModel;

/**
 * Simple panel that contains a link with icon and a link with a label.
 * 
 * @author Matej Knopp
 */
public class CheckBoxIconPanel extends LabelIconPanel
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the panel.
	 * 
	 * @param id
	 *            component id
	 * @param model
	 *            model that is used to access the TreeNode
	 * @param tree
	 */
	public CheckBoxIconPanel(String id, IModel<Object> model, CheckBoxTree tree)
	{
		super(id, model, tree);
	}
	
	/**
	 * @see org.apache.wicket.markup.html.tree.LabelIconPanel#addComponents(org.apache.wicket.model.IModel,
	 *      org.apache.wicket.markup.html.tree.BaseTree)
	 */
	protected void addComponents(final IModel<Object> model, final BaseTree tree)
	{
		final CheckBoxTree cbTree = (CheckBoxTree)tree;
		CheckBoxTree.ICheckCallback callback = new CheckBoxTree.ICheckCallback()
		{
			private static final long serialVersionUID = 1L;

			public void onUpdate(AjaxRequestTarget target)
			{
				onNodeCheckUpdated((TreeNode)model.getObject(), cbTree, target);
			}
		};

		add(newImageComponent("icon", tree, model));
		
		IModel<Boolean> dataModel = cbTree.newCheckBoxModel((TreeNode)model.getObject());
		add(cbTree.newCheckBox("checkbox", dataModel, callback) );
		add(newContentComponent("content", tree, model));
	}

	/**
	 * Handler invoked when the checkbox is clicked. By default makes the node selected
	 * 
	 * @param node
	 * @param tree
	 * @param target
	 */
	protected void onNodeCheckUpdated(TreeNode node, CheckBoxTree tree, AjaxRequestTarget target)
	{
		tree.getTreeState().selectNode(node, !tree.getTreeState().isNodeSelected(node));
		tree.updateTree(target);
	}
	
}
