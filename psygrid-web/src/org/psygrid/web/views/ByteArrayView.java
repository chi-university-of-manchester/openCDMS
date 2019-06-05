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


package org.psygrid.web.views;

import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

/**
 * Spring MVC View implementation to return a binary file as the
 * response, the contents of this binary file being the form of a
 * byte array provided by the model.
 * <p>
 * Slightly modified from code on the Spring forums:
 * http://forum.springframework.org/showthread.php?t=33118
 * 
 * @author Rob Harper
 *
 */
public class ByteArrayView extends AbstractView {

	@Override
	protected void renderMergedOutputModel(Map model,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		byte[] bytes = (byte[]) model.get("fileData");
		String contentType = (String) model.get("contentType");
		String fileName = (String) model.get("fileName");

		// Write content type and also length (determined via byte array).
		response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
		response.setContentType(contentType);
		response.setContentLength(bytes.length);

		/*
		 * The following Pragma and Cache-Control lines are necessary
		 * as the overcome an issue that IE has in some configurations
		 * when the no-cach header is sent and an Office or PDF document is 
		 * downloaded over HTTPS. The two lines override these
		 * headers, allowing IE to proceed. 
		 * More information and fix provided by: http://forum.java.sun.com/thread.jspa?threadID=233446&start=15&tstart=0
		 * MS bug report:  http://support.microsoft.com/default.aspx?scid=kb;en-us;812935
		 */
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "max-age=0");
		//may also require: response.setHeader("Accept-Ranges", bytes.length);
		
		// Flush byte array to servlet output stream.
		ServletOutputStream out = response.getOutputStream();
		out.write(bytes);
		out.flush();
	}

}
