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

package org.opencdms.web.modules.imports.panels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Bytes;
import org.opencdms.web.core.application.OpenCdmsWebSession;
import org.opencdms.web.core.security.SamlHelper;
import org.opencdms.web.modules.imports.models.ImportModel;
import org.psygrid.data.importing.ImportData;
import org.psygrid.data.importing.client.ImportClient;
import org.psygrid.www.xml.security.core.types.ProjectType;

/**
 * @author Terry Child
 *
 */
public class ImportPanel extends Panel implements IHeaderContributor {

	private static final Log logger = LogFactory.getLog(ImportPanel.class);

	private static final long serialVersionUID = 1L;
	
	public ImportPanel(String id) {
		super(id);
		
		add(new ImportForm("form"));
		
        FeedbackPanel uploadFeedback = new FeedbackPanel("feedback");
        add(uploadFeedback);
	}

    private class ImportForm extends Form<ImportModel> {

		private static final long serialVersionUID = 1L;
		
		DropDownChoice<ProjectType> study = null;
    	DropDownChoice<String> sourceTypes = null;
    	FileUploadField fileUpload = null;
    	
    	@SuppressWarnings("serial")
		public ImportForm(String id) {
    		super(id,new CompoundPropertyModel<ImportModel>(new ImportModel()));
    		    	        
    		final OpenCdmsWebSession session = (OpenCdmsWebSession)getSession();
    		    		
    		study = new DropDownChoice<ProjectType>("study");
    		study.setChoices(session.getUser().getImportableProjects());
    		study.setChoiceRenderer(new ChoiceRenderer<ProjectType>("name", "idCode"));
    		study.setRequired(true);
    		study.add(new AjaxFormComponentUpdatingBehavior("onchange"){
    			protected void onUpdate(AjaxRequestTarget target) {
    				// update the doc occurrence list
        			String idcode = study.getModelObject().getIdCode();
					String saml = SamlHelper.getSaml(session.getUser());
					ImportClient client = new ImportClient();
					try {
		        		String[] docTypes = client.getImportTypes(idcode,saml);
						sourceTypes.setChoices(Arrays.asList(docTypes));
					} catch (Exception e) {
					     error("Problem initialising the import source types:"+e.getMessage());
					     logger.error("Problem initialising the import source types.",e);
					} 	    		
					target.addComponent(sourceTypes);
    			}	
    		});
    		add(study);

    		sourceTypes = new DropDownChoice<String>("sourceType");
    		//sourceTypes.setChoiceRenderer(new ChoiceRenderer<IDocumentOccurrence>("name", "id"));
    		sourceTypes.setOutputMarkupId(true);
    		sourceTypes.setRequired(true);
    		add(sourceTypes);

    		// File upload field
    		setMultiPart(true);
            setMaxSize(Bytes.megabytes(5));
    		fileUpload = new FileUploadField("upload",new Model<FileUpload>());
    		fileUpload.setRequired(true);
    		add(fileUpload);
            //add(new UploadProgressBar("progress", ajaxSimpleUploadForm));
    		    		
    	}

        protected void onSubmit(){
			
			try{
				 final FileUpload fupload = fileUpload.getFileUpload();
				 if (fupload==null || fupload.getClientFileName()==null) {
				     // No image was provided
				     error("Please select a CSV file to upload.");
				     return;
				 } else if (fupload.getSize()==0) {
				     error("The file you attempted to upload is empty.");
				     return;
				 } 
				 else if (!fupload.getClientFileName().toLowerCase().endsWith(".csv")) {
				     error("At present only CSV files can be imported.");
				     return;
				 } 
				 else {
					String clientFileName = fupload.getClientFileName();
					info("Uploaded file: " + clientFileName);
					logger.info("Uploaded file: " + clientFileName);
					
					ImportClient client = new ImportClient();
					OpenCdmsWebSession session = (OpenCdmsWebSession) getSession();
					String saml = SamlHelper.getSaml(session.getUser());

					ImportModel model = getModelObject();
					String projectCode = model.getStudy().getIdCode();
					
					String sourceType = model.getSourceType();
					
					String data = convertStreamToString(fupload.getInputStream());
					
					ImportData request = new ImportData(projectCode,clientFileName,data,sourceType,"");
					client.requestImport(request, saml);
					
					String message = "Requested import of '" + clientFileName+"' into '"+sourceType+"'";
						info(message);
						logger.info(message);
					info("An email will be sent when the import has completed");
					}
			}
			catch(Exception ex){
				logger.error("Exception while importing file", ex);
				info("There was a problem importing the file. Please contact openCDMS support.");
			}	
        }   
    }
        
	public void renderHead(IHeaderResponse response) {
		response.renderOnLoadJavascript("selectStudy(\"study\")");
	}

	public  String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {
		    Writer writer = new StringWriter();		
		    char[] buffer = new char[1024];
		    try {
		        Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		        int n;
		        while ((n = reader.read(buffer)) != -1) {
		            writer.write(buffer, 0, n);
		        }
		    } finally {
		        is.close();
		    }
		    return writer.toString();
		} else {       
		    return "";
		}
	}
    
}
