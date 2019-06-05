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

package org.psygrid.outlook.patches.v1_1_12;

import java.util.List;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.hibernate.HibernateFactory;
import org.psygrid.data.reporting.definition.Factory;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.outlook.OutlookDataset;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch44 extends AbstractPatch {


    public boolean isReport(){
        return true;
    }

    public String getName() {
        return "Update reports with group 006003";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {
        Factory factory = new HibernateFactory();

        ReportsClient client = null;
        client = new ReportsClient();

        List<IReport> reports = client.getAllReportsByDataSet(ds.getId(), saml);

        //drop all reports
        for(IReport report : reports){
        	client.deleteReport(ds.getId(), report.getId(), saml);
        }

        //re-install the latest version
        OutlookDataset.createReports(ds, saml);

    }

}
