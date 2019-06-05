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

package org.psygrid.collection.entry.sampletracking;

import java.io.IOException;
import java.io.OutputStream;

import org.psygrid.data.sampletracking.ConfigInfo;
import org.psygrid.data.sampletracking.SampleInfo;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

class SamplePrinter {
		
    public static void print(ConfigInfo config,SampleInfo sample, OutputStream os) throws IOException {
        try {
	        Document document = new Document(new Rectangle(config.getLabelWidth(),config.getLabelHeight()));
	        document.setMargins(0,0,0,0);
	        PdfWriter writer = PdfWriter.getInstance(document, os);
	        document.open();                        
	        PdfPTable table = new PdfPTable(1);
	        table.setWidthPercentage(100);
	        PdfPCell cell = null;
	        if(config.isPrintBarcodes()){
		        PdfContentByte cb = writer.getDirectContent();
				Barcode128 code128 = new Barcode128();
				code128.setCode(sample.getSampleID());
				code128.setSize(config.getLabelFontSize());
				code128.setBaseline(config.getLabelFontSize());
				code128.setBarHeight(config.getLabelFontSize()*3);
	            cell = new PdfPCell(code128.createImageWithBarcode(cb, null, null));	        	
	        }
	        else {
		        Font font = FontFactory.getFont(FontFactory.HELVETICA, config.getLabelFontSize());
		        Paragraph label = new Paragraph(sample.getSampleID(),font);
	            cell = new PdfPCell(label);	        	
	        }
            cell.setFixedHeight(config.getLabelHeight());
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorder(0);
            cell.setPadding(0);
            table.addCell(cell);
            document.add(table);
	        document.close();
		} catch (DocumentException e) {
			throw new IOException("Unable to print sample label");
		}            
    }   
    
}
