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

import java.util.List;

import org.apache.wicket.markup.html.form.Button;
import org.opencdms.web.modules.query.panels.ListEditor.ListItem;


/**
 * @author Rob Harper
 *
 */
public abstract class EditorButton extends Button
{
	private static final long serialVersionUID = 1L;

    private transient ListItem< ? > parent;

    public EditorButton(String id)
    {
        super(id);
    }

    protected final ListItem< ? > getItem()
    {
        if (parent == null)
        {
            parent = findParent(ListItem.class);
        }
        return parent;
    }

    protected final List< ? > getList()
    {
        return getEditor().items;
    }

    protected final ListEditor< ? > getEditor()
    {
        return (ListEditor< ? >)getItem().getParent();
    }


    @Override
    protected void onDetach()
    {
        parent = null;
        super.onDetach();
    }

}