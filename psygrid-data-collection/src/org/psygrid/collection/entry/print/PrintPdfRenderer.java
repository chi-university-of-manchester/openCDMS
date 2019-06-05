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

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.persistence.PersistenceManager;
import org.psygrid.collection.entry.renderer.AbstractRendererSPI;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.hibernate.*;
import org.psygrid.data.model.IValue;
import org.psygrid.data.reporting.renderer.RendererException;
import org.psygrid.collection.entry.EntryMessages;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class PrintPdfRenderer {
	
	private static final Log LOG = LogFactory.getLog(PrintPdfRenderer.class);

	private static final int DEFAULT_TABLE_ROWS = 10;
	private static final int NUM_COLS_FOR_LANDSCAPE = 5;
	private static final int DEFAULT_MULTI_SECTION_COUNT = 5;
	
	private static final Font DEFAULT_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10);
	private static final Font ITALIC_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC);
	private static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

    public void renderRecord(Record record, OutputStream os) throws RendererException {
        
        Document document = new Document();
        
        try{
            PdfWriter writer = PdfWriter.getInstance(document, os);
            writer.setStrictImageSequence(true);
            document.open();
            
            //write the Dataset name
            DataSet ds = record.getDataSet();
            //build up document structure
            int counter = 0;
            for ( int i=0; i<ds.numDocumentGroups(); i++ ){
                DocumentGroup grp = ds.getDocumentGroup(i);
                for ( int j=0; j<ds.numDocuments(); j++ ){
                    org.psygrid.data.model.hibernate.Document doc = ds.getDocument(j);
                    for ( int k=0; k<doc.numOccurrences(); k++ ){
                        DocumentOccurrence docOcc = doc.getOccurrence(k);
                        if ( docOcc.getDocumentGroup().equals(grp) ){
                            DocumentInstance di = record.getDocumentInstance(docOcc);
                            if ( null != di ){
                                if ( counter > 0 ){
                                    renderDocumentInstance(di, document, true);
                                }
                                else{
                                    renderDocumentInstance(di, document, false);
                                }
                                counter++;
                            }
                        }
                    }
                }
            }
                        
        }
        catch (DocumentException ex) {
            throw new RendererException("Unable to render the record '"+record.getIdentifier().getIdentifier()+"' as a PDF", ex);
        } 
        
        document.close();
    }
    
    public void renderSingleDocumentInstance(DocumentInstance instance, OutputStream os) throws RendererException {
        
        Document document = new Document();
        
        try{
            PdfWriter writer = PdfWriter.getInstance(document, os);
            writer.setStrictImageSequence(true);
            document.open();
            
            renderDocumentInstance(instance, document, true);
            
        }
        catch (DocumentException ex) {
            throw new RendererException("Unable to render the document '"+instance.getOccurrence().getDocument().getDisplayText()+"' as a PDF", ex);
        } 
        
        document.close();
    }
    
    private void renderDocumentInstance(DocumentInstance instance, Document pdf, boolean pageBreak) throws DocumentException {
        
        if ( pageBreak ){
            pdf.newPage();
        }
        
        //write the document name
        DocumentOccurrence docOcc = instance.getOccurrence();
        org.psygrid.data.model.hibernate.Document doc = docOcc.getDocument();
        Paragraph title = new Paragraph(
                doc.getDisplayText()+" - "+docOcc.getDisplayText(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20));
        title.setAlignment(Element.ALIGN_CENTER);
        pdf.add(title);
        
        //write the study number
        String identifier = instance.getRecord().getUseExternalIdAsPrimary() == true ? instance.getRecord().getExternalIdentifier() : instance.getRecord().getIdentifier().getIdentifier();
        Paragraph studyNumber = new Paragraph(EntryMessages.getString("PrintPdfRenderer.studyNumberPreface") +identifier);
        studyNumber.setSpacingBefore(15F);
        pdf.add(studyNumber);
        
        //write the last change who/when
        Paragraph lastChange = new Paragraph("Last edited by "+instance.getLatestHistoryFormatted());
        lastChange.setSpacingBefore(15F);
        pdf.add(lastChange);
        
        //build up section structure
        for ( int i=0; i<doc.numSections(); i++ ){
            Section section = doc.getSection(i);
            List<Entry> entries = new ArrayList<Entry>();
            for ( int j=0; j<doc.numEntries(); j++ ){
                Entry e = doc.getEntry(j);
                if (section.equals(e.getSection())){
                    entries.add(e);
                }
            }
            //render this section
            renderSection(instance, section, entries, pdf);
        }
    }
    
    private void renderSection(DocumentInstance instance, Section section, List<Entry> entries, Document pdf) throws DocumentException {
        
        for ( int i=0; i<section.numOccurrences(); i++ ){
            SectionOccurrence secOcc = section.getOccurrence(i);
            
            if ( secOcc.isMultipleAllowed() ){
                //This section occurrence allows multiple runtime instances,
                //so we need to iterate over these
            	int counter = 0;
                for ( SecOccInstance soi: instance.getSecOccInstances(secOcc) ){
                	counter++;
                    renderSectionTitle(secOcc, pdf, counter);
                    for ( Entry e: entries ){
                        renderEntryTitle(e, pdf);
                        if ( e instanceof BasicEntry){
                            renderBasicEntry((BasicEntry)e, instance, soi, pdf);
                        }
                        else if ( e instanceof CompositeEntry){
                            renderCompositeEntry((CompositeEntry)e, instance, soi, pdf);
                        }
                    }
                }
            }
            else{
                //normal section occurrence, which only permits a single instance
                renderSectionTitle(secOcc, pdf);                
                for ( Entry e: entries ){
                    renderEntryTitle(e, pdf);
                    if ( e instanceof BasicEntry){
                        renderBasicEntry((BasicEntry)e, instance, secOcc, pdf);
                    }
                    else if ( e instanceof CompositeEntry){
                        renderCompositeEntry((CompositeEntry)e, instance, secOcc, pdf);
                    }
                }
            }
        }
        
    }
    
    private void renderSectionTitle(SectionOccurrence secOcc, Document pdf, Integer count) throws DocumentException {
    	String text = null;
    	if ( null != count ){
    		text = secOcc.getCombinedDisplayText() + " ("+count+")";
    	}
    	else{
    		text = secOcc.getCombinedDisplayText();
    	}
        Paragraph secTitle = new Paragraph(
                text,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        secTitle.setSpacingBefore(15F);
        pdf.add(secTitle);        
    }
    
    private void renderSectionTitle(SectionOccurrence secOcc, Document pdf) throws DocumentException {
    	renderSectionTitle(secOcc, pdf, null);
    }
        
    private void renderEntryTitle(Entry entry, Document pdf) throws DocumentException {
        String entryText = null;
        if ( null != entry.getLabel() ){
            entryText = entry.getLabel()+") "+entry.getDisplayText();
        }
        else{
            entryText = entry.getDisplayText();
        }
        Paragraph para = new Paragraph(entryText);
        para.setSpacingBefore(10F);
        pdf.add(para);        
        //Render the description (if there is one)
        if ( null != entry.getDescription() ){
        	Font italics = new Font(para.getFont());
        	italics.setStyle(Font.ITALIC);
        	Paragraph desc = new Paragraph(entry.getDescription(), italics);
        	pdf.add(desc);
        }
    }
    
    private void renderEntryTitleForBlank(Entry entry, Document pdf) throws DocumentException {
        String entryText = null;
        if ( null != entry.getLabel() ){
            entryText = entry.getLabel()+") "+entry.getDisplayText();
        }
        else{
            entryText = entry.getDisplayText();
        }
        Paragraph para = new Paragraph(entryText, DEFAULT_FONT);
        para.setSpacingBefore(10F);
        pdf.add(para);        
        //Render the description (if there is one)
        if ( null != entry.getDescription() ){
        	Paragraph desc = new Paragraph(entry.getDescription(), ITALIC_FONT);
        	pdf.add(desc);
        }
    }
    
    private void renderBasicEntry(BasicEntry entry, DocumentInstance instance, SectionOccurrence secOcc, Document pdf) throws DocumentException {
        BasicResponse resp = (BasicResponse)instance.getResponse(entry, secOcc);
        Paragraph para = new Paragraph(getBasicResponseText(resp, false));
        para.setIndentationLeft(20F);
        pdf.add(para);
    }
    
    private void renderBasicEntry(BasicEntry entry, DocumentInstance instance, SecOccInstance soi, Document pdf) throws DocumentException {
        BasicResponse resp = (BasicResponse)instance.getResponse(entry, soi);
        Paragraph para = new Paragraph(getBasicResponseText(resp, false));
        para.setIndentationLeft(20F);
        pdf.add(para);
    }
    
    private void renderCompositeEntry(CompositeEntry entry, DocumentInstance instance, SectionOccurrence secOcc, Document pdf) throws DocumentException {
        CompositeResponse resp = (CompositeResponse)instance.getResponse(entry, secOcc);
        if ( null == resp ){
            Paragraph para = new Paragraph("-");
            para.setIndentationLeft(20F);
            pdf.add(para);
        }
        else{
            PdfPTable table = new PdfPTable(entry.numEntries());
            table.setSpacingBefore(15F);
            table.setSpacingAfter(15F);
            table.setHeaderRows(1);
            //header row
            for ( int i=0; i<entry.numEntries(); i++ ){
                PdfPCell c = new PdfPCell(new Paragraph(entry.getEntry(i).getDisplayText()));
                c.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(c);
            }
            //data rows
            for ( int i=0; i<resp.numCompositeRows(); i++ ){
                CompositeRow row = resp.getCompositeRow(i);
                for ( int j=0; j<entry.numEntries(); j++ ){
                    BasicResponse br = row.getResponse(entry.getEntry(j));
                    if (EditAction.DENY.equals(resp.getEditingPermitted())) {
                    	table.addCell(getBasicResponseText(br, true));
                    }
                    else {
                    	table.addCell(getBasicResponseText(br, false));
                    }
                }
            }
            if ( 0 == resp.numCompositeRows() ){
                //if there are no actual rows, add a blank one just so the
                //table is rendered properly
                for ( int i=0; i<entry.numEntries(); i++ ){
                    table.addCell("-");
                }                
            }
            
            pdf.add(table);
        }
    }
    
    private void renderCompositeEntry(CompositeEntry entry, DocumentInstance instance, SecOccInstance soi, Document pdf) throws DocumentException {
        CompositeResponse resp = (CompositeResponse)instance.getResponse(entry, soi);
        if ( null == resp ){
            Paragraph para = new Paragraph("-");
            para.setIndentationLeft(20F);
            pdf.add(para);
        }
        else{
            PdfPTable table = new PdfPTable(entry.numEntries());
            table.setSpacingBefore(15F);
            table.setSpacingAfter(15F);
            table.setHeaderRows(1);
            //header row
            for ( int i=0; i<entry.numEntries(); i++ ){
                PdfPCell c = new PdfPCell(new Paragraph(entry.getEntry(i).getDisplayText()));
                c.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(c);
            }
            //data rows
            for ( int i=0; i<resp.numCompositeRows(); i++ ){
                CompositeRow row = resp.getCompositeRow(i);
                for ( int j=0; j<entry.numEntries(); j++ ){
                    BasicResponse br = row.getResponse(entry.getEntry(j));
                    if (EditAction.DENY.equals(resp.getEditingPermitted())) {
                    	table.addCell(getBasicResponseText(br, true));
                    }
                    else {
                    	table.addCell(getBasicResponseText(br, false));
                    }
                }
            }
            if ( 0 == resp.numCompositeRows() ){
                //if there are no actual rows, add a blank one just so the
                //table is rendered properly
                for ( int i=0; i<entry.numEntries(); i++ ){
                    table.addCell("-");
                }                
            }
            
            pdf.add(table);
        }
    }
    
    private String getBasicResponseText(BasicResponse resp, boolean compositeHide){
        String responseText = null;
        if ( null != resp && null != resp.getValue() ){
        	//The response is not viewable by the user so replace it with '*****'
        	if (compositeHide || EditAction.DENY.equals(resp.getEditingPermitted())) {
        		return AbstractRendererSPI.HIDDEN_VALUE;
            }
            IValue val = resp.getValue();
            //see if a standard code has been selected
            if ( null != val.getStandardCode() ){
                responseText = val.getStandardCode().getCode()+". "+val.getStandardCode().getDescription();
            }
            else{
                StringBuilder builder = new StringBuilder();
                String value = val.getValueAsString();
                if ( null != value ){
                    builder.append(val.getValueAsString());
                }
                if ( val instanceof IOptionValue ){
                    //special case for option values, which may have additional
                    //"please specify" text
                    IOptionValue optVal = (IOptionValue)val;
                    if ( null != optVal.getTextValue() ){
                        builder.append(" - ").append(optVal.getTextValue());
                    }
                }
                if ( null != val.getUnit() ){
                    builder.append(" ").append(val.getUnit().getAbbreviation());
                }
                if ( builder.length() > 0 ){
                    responseText = builder.toString();
                }
            }
        }
        //if the response text is still null, set it to '-'
        if ( null == responseText ){
            responseText = "-";
        }
        return responseText;
    }

    public void renderBlankDocumentOccurrence(DocumentOccurrence docOcc, OutputStream os) throws RendererException {
    	
        Document document = new Document();
        
        try{
            PdfWriter writer = PdfWriter.getInstance(document, os);
            writer.setStrictImageSequence(true);
            writer.setPageEvent(new PageNumberer());
            document.open();
            renderBlankDocumentOccurrence(docOcc, document);
            
        }
        catch (DocumentException ex) {
            throw new RendererException("Unable to render the document '"+docOcc.getDocument().getDisplayText()+"' as a PDF", ex);
        } 
        
        document.close();
    }

    
    private void renderBlankDocumentOccurrence(DocumentOccurrence docOcc, Document pdf) throws DocumentException {
    	
        //write a space to enter the study number
		PdfPTable table = new PdfPTable(2);
		table.setSpacingAfter(10F);
		table.setHorizontalAlignment(Element.ALIGN_LEFT);
		table.setTotalWidth(250f);
		table.setLockedWidth(true);
		table.setWidths(new float[]{0.35f, 0.65f});
		PdfPCell snLabel = new PdfPCell(new Paragraph(EntryMessages.getString("PrintPdfRenderer.studyNumberPrefaceForBlankDoc"), BOLD_FONT));
		snLabel.setVerticalAlignment(Element.ALIGN_MIDDLE);
		snLabel.setMinimumHeight(20f);
		snLabel.setBorder(0);
		table.addCell(snLabel);
		table.addCell(new PdfPCell());
		pdf.add(table);
		
        //write the document name
        org.psygrid.data.model.hibernate.Document doc = docOcc.getDocument();
        Paragraph title = new Paragraph(
                doc.getDisplayText()+" - "+docOcc.getDisplayText(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20));
        title.setAlignment(Element.ALIGN_CENTER);
        pdf.add(title);
        
        //write the standard codes
        Paragraph stdCodesDesc = new Paragraph(EntryMessages.getString("PrintPdfRenderer.standardCodesParagraph"), ITALIC_FONT);
        stdCodesDesc.setSpacingBefore(5f);
        pdf.add(stdCodesDesc);
        for ( StandardCode sc: getStandardCodes() ){
        	pdf.add(new Paragraph(sc.getCode()+". "+sc.getDescription(), ITALIC_FONT));
        }
        
        //build up section structure
        for ( int i=0; i<doc.numSections(); i++ ){
            Section section = doc.getSection(i);
            List<Entry> entries = new ArrayList<Entry>();
            for ( int j=0; j<doc.numEntries(); j++ ){
                Entry e = doc.getEntry(j);
                if (!e.isLocked() && section.equals(e.getSection())){
                    entries.add(e);
                }
            }
            //render this section
            renderBlankSection(doc, section, entries, pdf);
        }

    	
    }
    
    private void renderBlankSection(org.psygrid.data.model.hibernate.Document doc, Section section, List<Entry> entries, Document pdf) throws DocumentException {
        
        for ( int i=0; i<section.numOccurrences(); i++ ){
            SectionOccurrence secOcc = section.getOccurrence(i);
            
            if ( secOcc.isMultipleAllowed() ){
                //This section occurrence allows multiple runtime instances,
                //so we need to iterate over these
                for ( int j=0; j<DEFAULT_MULTI_SECTION_COUNT; j++ ){
                    renderSectionTitle(secOcc, pdf, j+1);                
                    for ( Entry e: entries ){
                        if ( e instanceof BasicEntry){
                            renderBlankBasicEntry((BasicEntry)e, pdf);
                        }
                        else if ( e instanceof CompositeEntry){
                            renderBlankCompositeEntry((CompositeEntry)e, pdf);
                        }
                        else if ( e instanceof NarrativeEntry){
                        	renderEntryTitleForBlank(e, pdf);
                        }
                    }
                }
            }
            else{
                //normal section occurrence, which only permits a single instance
                renderSectionTitle(secOcc, pdf);                
                for ( Entry e: entries ){
                    if ( e instanceof BasicEntry){
                        renderBlankBasicEntry((BasicEntry)e, pdf);
                    }
                    else if ( e instanceof CompositeEntry){
                        renderBlankCompositeEntry((CompositeEntry)e, pdf);
                    }
                    else if ( e instanceof NarrativeEntry){
                    	renderEntryTitleForBlank(e, pdf);
                    }
                }
            }
        }
        
    }
    
    private void renderBlankBasicEntry(BasicEntry e, Document pdf) throws DocumentException {
        //Render area to write/select the answer
    	renderEntryTitleForBlank(e, pdf);
        if ( e instanceof OptionEntry){
        	OptionEntry oe = (OptionEntry)e;
        	Paragraph instructions = new Paragraph(EntryMessages.getString("PrintPdfRenderer.optionEntryInstructions"), ITALIC_FONT);
        	pdf.add(instructions);
    		PdfPTable table = new PdfPTable(6);
    		table.setSpacingBefore(5f);
    		table.setHorizontalAlignment(Element.ALIGN_LEFT);
    		table.setTotalWidth(500f);
    		table.setLockedWidth(true);
    		table.setWidths(new float[]{0.03f, 0.01f, 0.21f, 0.1f, 0.25f, 0.4f});
    		for ( int i=0, c=oe.numOptions(); i<c; i++ ){
    			Option o = oe.getOption(i);
    			PdfPCell tickBox = new PdfPCell();
    			tickBox.setMinimumHeight(15f);
    			table.addCell(tickBox);
    			PdfPCell spacer = new PdfPCell();
    			spacer.setBorder(0);
    			table.addCell(spacer);
    			Paragraph optionText = null;
    			if ( oe.isOptionCodesDisplayed() && null != o.getCode() ){
    				optionText = new Paragraph(o.getCode()+". "+o.getDisplayText(), DEFAULT_FONT);
    			}
    			else{
    				optionText = new Paragraph(o.getDisplayText(), DEFAULT_FONT);
    			}
    			PdfPCell textBox = new PdfPCell(optionText);
    			textBox.setBorder(0);
    			textBox.setColspan(4);
    			table.addCell(textBox);
    			PdfPCell spacerRow = new PdfPCell();
    			spacerRow.setBorder(0);
    			spacerRow.setColspan(6);
    			spacerRow.setMinimumHeight(3f);
    			table.addCell(spacerRow);
    			if ( o.isTextEntryAllowed() ){
    				PdfPCell indent = new PdfPCell();
    				indent.setBorder(0);
    				indent.setColspan(2);
    				table.addCell(indent);
    				PdfPCell otherText = new PdfPCell();
    				otherText.setColspan(3);
    				otherText.setMinimumHeight(20f);
    				table.addCell(otherText);
    				PdfPCell right = new PdfPCell();
    				right.setBorder(0);
    				table.addCell(right);
        			PdfPCell spacerRow2 = new PdfPCell();
        			spacerRow2.setBorder(0);
        			spacerRow2.setColspan(6);
        			spacerRow2.setMinimumHeight(3f);
        			table.addCell(spacerRow2);
    			}
    		}
    		//add a row for standard codes
			PdfPCell tickBox = new PdfPCell();
			tickBox.setMinimumHeight(15f);
			table.addCell(tickBox);
			PdfPCell spacer = new PdfPCell();
			spacer.setBorder(0);
			table.addCell(spacer);
			PdfPCell scText = new PdfPCell(new Paragraph(EntryMessages.getString("PrintPdfRenderer.standardCodePreface"), DEFAULT_FONT));
			scText.setBorder(0);
			table.addCell(scText);
			PdfPCell scInput = new PdfPCell();
			table.addCell(scInput);
			PdfPCell scBlank = new PdfPCell();
			scBlank.setBorder(0);
			scBlank.setColspan(2);
			table.addCell(scBlank);
			
    		pdf.add(table);
        }
        else if ( e instanceof LongTextEntry){
    		PdfPTable table = new PdfPTable(1);
    		table.setSpacingBefore(5f);
    		table.setHorizontalAlignment(Element.ALIGN_LEFT);
    		table.setTotalWidth(500f);
    		table.setWidths(new float[]{1f});
    		table.setLockedWidth(true);
    		PdfPCell entryBox = new PdfPCell();
    		entryBox.setMinimumHeight(70f);
    		table.addCell(entryBox);
    		pdf.add(table);
        }
        else {
    		PdfPTable table = new PdfPTable(2);
    		table.setSpacingBefore(5f);
    		table.setHorizontalAlignment(Element.ALIGN_LEFT);
    		table.setTotalWidth(500f);
    		table.setLockedWidth(true);
    		table.setWidths(new float[]{0.6f, 0.4f});
    		PdfPCell entryBox = new PdfPCell();
    		entryBox.setMinimumHeight(20f);
    		if ( e instanceof DerivedEntry || e instanceof ExternalDerivedEntry){
    			//shade entry boxes that don't need to be completed
    			entryBox.setBackgroundColor(Color.LIGHT_GRAY);
    		}
    		table.addCell(entryBox);
    		StringBuilder units = new StringBuilder();
    		for ( int i=0, c=e.numUnits(); i<c; i++ ){
    			if ( i > 0 ){
    				units.append(" / ");
    			}
    			units.append(e.getUnit(i).getAbbreviation());
    		}
    		PdfPCell unitBox = new PdfPCell(new Paragraph(units.toString(), DEFAULT_FONT));
    		unitBox.setBorder(0);
    		table.addCell(unitBox);
    		pdf.add(table);   	
        }
        
    }
    
    private void renderBlankCompositeEntry(CompositeEntry e, Document pdf) throws DocumentException {
    	
    	int rowCount = e.numRowLabels();
    	if ( 0 == rowCount ){
    		rowCount = DEFAULT_TABLE_ROWS;
    	}
    	
    	if ( e.numEntries() > NUM_COLS_FOR_LANDSCAPE ){
    		//insert a page break and rotate the page to landscape
    		pdf.setPageSize(PageSize.A4.rotate());
    		pdf.newPage();
    	}

    	renderEntryTitleForBlank(e, pdf);

        List<String> notesList = new ArrayList<String>();

        PdfPTable table = new PdfPTable(e.numEntries());
        table.setSpacingBefore(5F);
        table.setHeaderRows(1);
        //header row
        for ( int i=0; i<e.numEntries(); i++ ){
        	BasicEntry be = e.getEntry(i);
        	StringBuilder notes = new StringBuilder();
        	if ( null != be.getDescription() ){
        		notes.append(be.getDescription().trim());
        		if ( !".".equals(notes.substring(notes.length()-1, notes.length())) ){
        			notes.append(".");
        		}
        		notes.append(" ");
        	}
        	if ( be instanceof OptionEntry){
        		OptionEntry oe = (OptionEntry)be;
        		notes.append("Options: ");
        		for ( int j=0, d=oe.numOptions(); j<d; j++ ){
        			if ( j > 0 ){
        				notes.append("; ");
        			}
        			Option o = oe.getOption(j);
        			if ( null != o.getCode() && oe.isOptionCodesDisplayed() ){
        				notes.append(o.getCode());
        				notes.append(". ");
        			}
        			notes.append(o.getDisplayText());
        		}
        		notes.append(". ");
        	}
        	if ( be.numUnits() > 0 ){
        		notes.append("Units: ");
        		for ( int j=0, d=be.numUnits(); j<d; j++ ){
        			if ( j > 0 ){
        				notes.append("; ");
        			}
        			notes.append(be.getUnit(j).getAbbreviation());
        		}
        		notes.append(". ");
        	}
        	Paragraph para = null;
        	if ( notes.length() > 0 ){
        		notesList.add(notes.toString());
        		para = new Paragraph(be.getDisplayText()+" ("+notesList.size()+")", DEFAULT_FONT);
        	}
        	else{
        		para = new Paragraph(be.getDisplayText(), DEFAULT_FONT);
        	}
            PdfPCell c = new PdfPCell(para);
            c.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(c);
        }
        
        //data rows
        for ( int i=0; i<rowCount; i++ ){
            for ( int j=0; j<e.numEntries(); j++ ){
            	PdfPCell cell = null;
            	if ( 0 == j && e.numRowLabels()>0 ){
            		cell = new PdfPCell(new Paragraph(e.getRowLabel(i), DEFAULT_FONT));
            	}
            	else{
            		cell = new PdfPCell();
            	}
        		cell.setMinimumHeight(20f);
        		table.addCell(cell);
            }
        }
        
        pdf.add(table);
        
        //any additional notes
        int counter = 0;
        for (String notes: notesList){
        	counter++;
        	Paragraph p = new Paragraph("("+counter+") "+notes, ITALIC_FONT);
        	pdf.add(p);
        }

    	if ( e.numEntries() > NUM_COLS_FOR_LANDSCAPE ){
    		//rotate the page back to portrait
    		pdf.setPageSize(PageSize.A4);
    		pdf.newPage();
    	}        
        
    }
    
    private static List<StandardCode> getStandardCodes(){
		List<StandardCode> codes;
		try {
			codes = PersistenceManager.getInstance().loadStandardCodes();
		} catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Error reading standard codes from disk.", e); //$NON-NLS-1$
			}
			codes = new ArrayList<StandardCode>(0);
		//caused by calling from the dataset designer
		} catch (NullPointerException ex) {
			codes = new ArrayList<StandardCode>(0);
		}

		return codes;
    }
}
