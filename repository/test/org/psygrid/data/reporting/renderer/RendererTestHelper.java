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

package org.psygrid.data.reporting.renderer;

import java.util.Date;

import org.psygrid.data.model.IValue;
import org.psygrid.data.reporting.Chart;
import org.psygrid.data.reporting.ChartPoint;
import org.psygrid.data.reporting.ChartRow;
import org.psygrid.data.reporting.ChartSeries;
import org.psygrid.data.reporting.RecordReport;
import org.psygrid.data.reporting.Report;

public class RendererTestHelper {

    public static Report createTestReport(){
        RecordReport r = new RecordReport();
        r.setTitle("Test report");
        r.setSubject("OLK/009001-1");
        r.setRequestor("RSH");
        r.setRequestDate(new Date());
        r.setCharts(new Chart[3]);
        
        //Bar chart
        Chart bc = new Chart();
        r.getCharts()[0] = bc;
        bc.setTitle("Test bar chart");
        String[] types = new String[1];
        types[0] = Chart.CHART_BAR;
        bc.setTypes(types);
        //bc.setSeriesLabels(new String[]{"series 1", "series 2"});
        bc.setRows(new ChartRow[5]);
        for ( int i=0; i<bc.getRows().length; i++ ){
            ChartRow row = new ChartRow();
            bc.getRows()[i] = row;
            row.setLabel("Row "+i);
            row.setLabelType(IValue.TYPE_STRING);
            //row.setSeries(new ChartPoint[2]);
            row.setSeries(new ChartSeries[2]);
            ChartSeries s1 = new ChartSeries();
            ChartSeries s2 = new ChartSeries();
            s1.setPoints(new ChartPoint[1]);
            s2.setPoints(new ChartPoint[1]);
            
            ChartPoint pt = new ChartPoint();
            pt.setValue(Integer.toString(i));
            pt.setValueType(IValue.TYPE_INTEGER);
            s1.setLabel("series 1");
            s1.getPoints()[0] = pt;
            row.getSeries()[0] = s1;
            
            ChartPoint pt2 = new ChartPoint();
            pt2.setValue(Integer.toString(i+2));
            pt2.setValueType(IValue.TYPE_INTEGER);
            s2.setLabel("series 2");
            s2.getPoints()[0] = pt2;
            row.getSeries()[1] = s2;
        }
        
        //Pie chart
        Chart pc = new Chart();
        r.getCharts()[1] = pc;
        pc.setTitle("Test pie chart");
        String[] types2 = new String[1];
        types2[0] = Chart.CHART_PIE;
        pc.setTypes(types2);
       // pc.setSeriesLabels(new String[]{"series 1", "series 2","series 3", "series 4"});
        pc.setRows(new ChartRow[5]);
        for ( int i=0; i<pc.getRows().length; i++ ){
            ChartRow row = new ChartRow();
            pc.getRows()[i] = row;
            row.setLabel("Row "+i);
            row.setLabelType(IValue.TYPE_STRING);
           // row.setSeries(new ChartPoint[4]);
            row.setSeries(new ChartSeries[4]);
            
            ChartSeries s1 = new ChartSeries();
            ChartSeries s2 = new ChartSeries();
            ChartSeries s3 = new ChartSeries();
            ChartSeries s4 = new ChartSeries();
            
            s1.setPoints(new ChartPoint[1]);
            s2.setPoints(new ChartPoint[1]);
            s3.setPoints(new ChartPoint[1]);
            s4.setPoints(new ChartPoint[1]);
            
            ChartPoint pt = new ChartPoint();
            pt.setValue(Integer.toString(i));
            pt.setValueType(IValue.TYPE_INTEGER);
            //row.getSeries()[0] = pt;
            s1.setLabel("series 1");
            s1.getPoints()[0] = pt;
            row.getSeries()[0] = s1;
            
            ChartPoint pt2 = new ChartPoint();
            pt2.setValue(Integer.toString(i+2));
            pt2.setValueType(IValue.TYPE_INTEGER);
            //row.getSeries()[1] = pt2;
            s2.setLabel("series 2");
            s2.getPoints()[0] = pt2;
            row.getSeries()[1] = s2;
            
            ChartPoint pt3 = new ChartPoint();
            pt3.setValue(Integer.toString(i));
            pt3.setValueType(IValue.TYPE_INTEGER);
           // row.getSeries()[2] = pt3;
            s3.setLabel("series 3");
            s3.getPoints()[0] = pt3;
            row.getSeries()[2] = s3;
            
            ChartPoint pt4 = new ChartPoint();
            pt4.setValue(Integer.toString(i+2));
            pt4.setValueType(IValue.TYPE_INTEGER);
            //row.getSeries()[3] = pt4;
            s4.setLabel("series 4");
            s4.getPoints()[0] = pt4;
            row.getSeries()[3] = s4;
        }
        
        //Table
        Chart c = new Chart();
        r.getCharts()[2] = c;
        c.setTitle("Test table");
        String[] types3 = new String[1];
        types3[0] = Chart.CHART_TABLE;
        c.setTypes(types3);
        //c.setSeriesLabels(new String[]{"default"});
        c.setRows(new ChartRow[20]);
        for ( int i=0; i<c.getRows().length; i++ ){
            ChartRow row = new ChartRow();
            c.getRows()[i] = row;
            row.setLabel("Row "+i);
            row.setLabelType(IValue.TYPE_STRING);
          //  row.setSeries(new ChartPoint[1]);
            row.setSeries(new ChartSeries[1]);
            ChartSeries s1 = new ChartSeries();
            s1.setLabel("default");
            s1.setPoints(new ChartPoint[1]);
            
            ChartPoint pt = new ChartPoint();
            pt.setValue("Value "+i);
            pt.setValueType(IValue.TYPE_STRING);
            s1.getPoints()[0] = pt;
            
            row.getSeries()[0] = s1;
        }
        
        return r;
    }
}
