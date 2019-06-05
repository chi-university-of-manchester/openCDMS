/*
	Copyright (c) 2008, The University of Manchester, UK.

	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
	02110-1301, USA.
 */
package org.psygrid.command.patches.v1_1_33;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.model.hibernate.Document;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch7 extends AbstractPatch {


	@Override
	public String getName() {
		return "Patch to update documents to include instance actions. This must be applied after repository sql patch 50";
	}

	@Override
	public void applyPatch(DataSet ds, String saml) throws Exception {

		for (Document doc: ((DataSet)ds).getDocuments()) {
			if (doc.getAction() != null) {
				doc.setInstanceAction(doc.getAction());
			}
		}
	}

}