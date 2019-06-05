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

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.wicket.Component;
import org.apache.wicket.IClusterable;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.WicketTreeModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.EnumeratedType;

/**
 * Simple tree component that provides node panel with checkbox allowing user to select individual
 * nodes.
 * 
 * @author Doug Leeper
 */
public class CheckBoxTree extends BaseTree
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param id
	 */
	public CheckBoxTree(String id)
	{
		super(id);
	}

	/**
	 * 
	 * Construct.
	 * 
	 * @param id
	 * @param model
	 *            model that provides the {@checkbox TreeModel}
	 */
	public CheckBoxTree(String id, IModel<TreeModel> model)
	{
		super(id, model);
	}

	/**
	 * 
	 * Construct.
	 * 
	 * @param id
	 * @param model
	 *            Tree model
	 */
	public CheckBoxTree(String id, TreeModel model)
	{
		super(id, new WicketTreeModel());
		setModelObject(model);
	}

	/**
	 * @see org.apache.wicket.markup.html.tree.BaseTree#newNodeComponent(java.lang.String,
	 *      org.apache.wicket.model.IModel)
	 */
	protected Component newNodeComponent(String id, IModel<Object> model)
	{
		return new CheckBoxIconPanel(id, model, CheckBoxTree.this)
		{
			private static final long serialVersionUID = 1L;

			protected void onNodeCheckUpdated(TreeNode node, CheckBoxTree tree, AjaxRequestTarget target)
			{
				super.onNodeCheckUpdated(node, tree, target);
				CheckBoxTree.this.onNodeCheckUpdated(node, tree, target);
			}
		};
	}

	/**
	 * Method invoked after the node has been selected / unselected.
	 * 
	 * @param node
	 * @param tree
	 * @param target
	 */
	protected void onNodeCheckUpdated(TreeNode node, BaseTree tree, AjaxRequestTarget target)
	{

	}
	
	/**
	 * The type of junction checkboxs and node selection checkboxs.
	 * <dl>
	 * <dt>Regular checkbox</dt>
	 * <dd>Non-ajax checkbox, always refreshes the whole page. Works with javascript disabled.</dd>
	 * <dt>Ajax checkbox</dt>
	 * <dd>Links that supports partial updates. Doesn't work with javascript disabled</dd>
	 * <dt>Ajax fallback checkbox</dt>
	 * <dd>Link that supports partial updates. With javascript disabled acts like regular checkbox. The
	 * drawback is that generated url (thus the entire html) is larger then using the other two</dd>
	 * </dl>
	 */
	public static final class CheckType extends EnumeratedType
	{

		/** partial updates with no fallback. */
		public static final CheckType AJAX = new CheckType("AJAX");

		/**
		 * partial updates that falls back to a regular checkbox in case the client does not support
		 * javascript.
		 */
		public static final CheckType AJAX_FALLBACK = new CheckType("AJAX_FALLBACK");

		/**
		 * non-ajax version that always re-renders the whole page.
		 */
		public static final CheckType REGULAR = new CheckType("REGULAR");

		private static final long serialVersionUID = 1L;

		/**
		 * Construct.
		 * 
		 * @param name
		 *            the name of the type of the checkbox
		 */
		public CheckType(String name)
		{
			super(name);
		}
	}

	/**
	 * Helper class for calling an action from a checkbox.
	 * 
	 * @author Doug Leeper
	 */
	public interface ICheckCallback extends IClusterable
	{
		/**
		 * Called when the click is executed.
		 * 
		 * @param target
		 *            The ajax request target
		 */
		void onUpdate(AjaxRequestTarget target);
	}

	/**
	 * Creates a checkbox of type specified by current checkType. When the checkbox is clicked it calls the
	 * specified callback.
	 * 
	 * @param id
	 *            The component id
	 * @param callback
	 *            The checkbox call back
	 * @return The checkbox component
	 */
	public MarkupContainer newCheckBox(String id, IModel<Boolean> model, final ICheckCallback callback)
	{
		if (getCheckType() == CheckType.REGULAR)
		{
			return new CheckBox(id, model)
			{
				private static final long serialVersionUID = 1L;

				/**
				 * @see org.apache.wicket.markup.html.form.CheckBox#onSelectionChanged()
				 */
				public void onSelectionChanged(Object newValue)
				{
					callback.onUpdate(null);
				}
				
				public boolean wantOnSelectionChangedNotifications() {
					return true;
				}
			};
		}
		else if (getCheckType() == CheckType.AJAX)
		{
			return new AjaxCheckBox(id, model)
			{
				private static final long serialVersionUID = 1L;

				/**
				 * @see org.apache.wicket.ajax.markup.html.AjaxLink#onClick(org.apache.wicket.ajax.AjaxRequestTarget)
				 */
				public void onUpdate(AjaxRequestTarget target)
				{
					callback.onUpdate(target);
				}
				
				public boolean wantOnSelectionChangedNotifications() {
					return false;
				}
			};
		}
		else
		{
			return new AjaxCheckBox(id, model)
			{
				private static final long serialVersionUID = 1L;

				/**
				 * @see org.apache.wicket.ajax.markup.html.AjaxFallbackLink#onClick(org.apache.wicket.ajax.AjaxRequestTarget)
				 */
				public void onUpdate(AjaxRequestTarget target)
				{
					callback.onUpdate(target);
				}
				
				public boolean wantOnSelectionChangedNotifications() {
					return false;
				}
			};
		}
	}
	
	public IModel<Boolean> newCheckBoxModel( final TreeNode tnode ) {
		return new IModel<Boolean>() {

			private static final long serialVersionUID = 1L;

			public Boolean getObject() {
				return getTreeState().isNodeSelected(tnode);
			}

			public void setObject(Boolean object) {			
			}

			public void detach() {
			}
			
		};
	}

	/**
	 * Returns the current type of checkboxs on tree items.
	 * 
	 * @return The checkbox type
	 */
	public CheckType getCheckType()
	{
		return checkType;
	}

	/**
	 * Sets the type of checkboxs on tree items. After the checkbox type is changed, the whole tree is
	 * rebuild and re-rendered.
	 * 
	 * @param checkType
	 *            type of checkboxs
	 */
	public void setCheckType(CheckType checkType)
	{
		if (this.checkType != checkType)
		{
			this.checkType = checkType;
			invalidateAll();
		}
	}
	
	private CheckType checkType = CheckType.AJAX;
	
	public boolean isValid(){
		return true;
	}
	
	public void validate(){
		
	}
}
