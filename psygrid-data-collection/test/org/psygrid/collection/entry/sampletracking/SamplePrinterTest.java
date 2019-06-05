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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

class SamplePrinterTest {
		
    
    public static void main1(String[] args) throws Exception {
    	int width=300;
    	int height=30;
        Document document = new Document(new Rectangle(width,height));
        document.setMargins(0,0,0,0);
        PdfWriter.getInstance(
                document,
                new 
                FileOutputStream("c:/aaa/test.pdf"));
        document.open();                        
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
    	Font font = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Phrase label = new Phrase("Some text",font);
        //label.setAlignment(Element.ALIGN_CENTER);
        //Barcode b = new Barcode();
        PdfPCell cell = new PdfPCell(label);
//        cell.addElement(label);
        cell.setFixedHeight(height);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(0);
        cell.setPadding(0);
        table.addCell(cell);
        document.add(table);
        document.close();
}   

    // http://1t3xt.info/examples/browse/?page=example&id=268
    
    public static void main(String[] args) throws Exception {
    	int width=300;
    	int height=300;
    	int size=8;
        Document document = new Document(new Rectangle(width,height));
        document.setMargins(0,0,0,0);
        PdfWriter writer = PdfWriter.getInstance(
                document,
                new 
                FileOutputStream("c:/aaa/test.pdf"));
        document.open();   
        PdfContentByte cb = writer.getDirectContent();
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
    	Font font = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Phrase label = new Phrase("Some text",font);
        //label.setAlignment(Element.ALIGN_CENTER);
        // CODE 128
		Barcode128 code128 = new Barcode128();
		code128.setCode("0123456789");
		code128.setSize(size);
		code128.setBaseline(size);
		code128.setBarHeight(size*3);
		PdfPCell cell = new PdfPCell(code128.createImageWithBarcode(cb, null, null));	        	        
        cell.setFixedHeight(height);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(0);
        cell.setPadding(0);
        table.addCell(cell);
        document.add(table);
        document.close();
    }   

}
