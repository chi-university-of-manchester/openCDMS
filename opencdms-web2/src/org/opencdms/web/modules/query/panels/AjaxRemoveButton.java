/*
Copyright (c) 2006-2009, The University of Manchester, UK.

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

package org.opencdms.web.modules.query.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.opencdms.web.modules.query.panels.ListEditor.ListItem;


/**
 * @author Rob Harper
 *
 */
public class AjaxRemoveButton extends AjaxEditorButton{

	private static final long serialVersionUID = 1L;

	public AjaxRemoveButton(String id)
    {
        super(id);
        setDefaultFormProcessing(false);
    }

    @Override
	protected void onSubmit(AjaxRequestTarget arg0, Form<?> arg1) {
        int idx = getItem().getIndex();

        for (int i = idx + 1; i < getItem().getParent().size(); i++)
        {
            ListItem< ? > item = (ListItem< ? >)getItem().getParent().get(i);
            item.setIndex(item.getIndex() - 1);
        }

        getList().remove(idx);
        getEditor().remove(getItem());
	}

    @Override
    public boolean isEnabled()
    {
        return getEditor().checkRemove(getItem());
    }
}
