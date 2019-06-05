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


package org.psygrid.collection.entry.print;

import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import org.psygrid.collection.entry.EntryMessages;

/**
 * @author Rob Harper
 *
 */
public class PageNumberer extends PdfPageEventHelper {

    private PdfTemplate tpl;
	private BaseFont font;
    
	@Override
	public void onOpenDocument(PdfWriter writer, Document document) {
		try{
	        font = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
			tpl = writer.getDirectContent().createTemplate(100, 100);
	        tpl.setBoundingBox(new Rectangle(-20, -20, 100, 100));
		}
		catch(Exception ex){
			throw new ExceptionConverter(ex);
		}
	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
	    PdfContentByte cb = writer.getDirectContent();
	    cb.saveState();
        String text = EntryMessages.getString("PageNumberer.pageNumberMessage_p1") + writer.getPageNumber() + EntryMessages.getString("PageNumberer.pageNumberMessage_p2");
        float textSize = font.getWidthPoint(text, 10);
        float textBase = document.bottom() - 20;
        cb.beginText();
        cb.setFontAndSize(font, 10);
        cb.setTextMatrix(document.left(), textBase);
        cb.showText(text);
        cb.endText();
        cb.addTemplate(tpl, document.left() + textSize, textBase);
        cb.saveState();
	}

	@Override
	public void onCloseDocument(PdfWriter writer, Document document) {
	       tpl.beginText();
	       tpl.setFontAndSize(font, 10);
	       tpl.setTextMatrix(0, 0);
	       tpl.showText("" + (writer.getPageNumber() - 1));
	       tpl.endText();
	}

}
