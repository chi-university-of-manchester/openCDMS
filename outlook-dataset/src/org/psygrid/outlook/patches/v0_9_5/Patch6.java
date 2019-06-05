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

package org.psygrid.outlook.patches.v0_9_5;

import org.psygrid.data.model.hibernate.DataSet;
import org.psygrid.data.reporting.client.ReportsClient;
import org.psygrid.data.reporting.definition.IReport;
import org.psygrid.outlook.Reports;
import org.psygrid.outlook.patches.AbstractPatch;

public class Patch6 extends AbstractPatch {

    public String getName() {
        return "Add clinical and management reports";
    }

    public void applyPatch(DataSet ds, String saml) throws Exception {

        //Clinical reports
        IReport panssReportBaseline = Reports.panssReportBaseline(ds);
        IReport panssReport6Months = Reports.panssReport6Months(ds);
        IReport panssReport12Months = Reports.panssReport12Months(ds);
        IReport youngManiaReportBaseline = Reports.youngManiaBaseline(ds);
        IReport youngManiaReport6Months = Reports.youngMania6Months(ds);
        IReport youngManiaReport12Months = Reports.youngMania12Months(ds);
        IReport gafReportBaseline = Reports.gafReportBaseline(ds);
        IReport gafReport12Months = Reports.gafReport12Months(ds);
        IReport drugCheckReportBaseline = Reports.drugCheckReportBaseline(ds);
        IReport drugCheckReport12Months = Reports.drugCheckReport12Months(ds);

        //Management reports
        IReport cpmReport = Reports.cpmMgmtReport(ds);
        IReport ciReport = Reports.ciMgmtReport(ds);
        IReport piBaReport = Reports.piBristolAvonMgmtReport(ds);
        IReport piEaReport = Reports.piEastAngliaMgmtReport(ds);
        IReport piEmReport = Reports.piEastMidlandsMgmtReport(ds);
        IReport piNeReport = Reports.piNorthEastMgmtReport(ds);
        IReport piNlReport = Reports.piNorthLondonMgmtReport(ds);
        IReport piNwReport = Reports.piNorthWestMgmtReport(ds);
        IReport piSlReport = Reports.piSouthLondonMgmtReport(ds);
        IReport piWmReport = Reports.piWestMidlandsMgmtReport(ds);

        //save the reports
        ReportsClient client = new ReportsClient();
        client.saveReport(panssReportBaseline, saml);
        client.saveReport(panssReport6Months, saml);
        client.saveReport(panssReport12Months, saml);
        client.saveReport(youngManiaReportBaseline, saml);
        client.saveReport(youngManiaReport6Months, saml);
        client.saveReport(youngManiaReport12Months, saml);
        client.saveReport(gafReportBaseline, saml);
        client.saveReport(gafReport12Months, saml);
        client.saveReport(drugCheckReportBaseline, saml);
        client.saveReport(drugCheckReport12Months, saml);
        client.saveReport(cpmReport, saml);
        client.saveReport(ciReport, saml);
        client.saveReport(piBaReport, saml);
        client.saveReport(piEaReport, saml);
        client.saveReport(piEmReport, saml);
        client.saveReport(piNeReport, saml);
        client.saveReport(piNlReport, saml);
        client.saveReport(piNwReport, saml);
        client.saveReport(piSlReport, saml);
        client.saveReport(piWmReport, saml);

    }

}
