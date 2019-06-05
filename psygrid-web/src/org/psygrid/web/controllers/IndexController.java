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

package org.psygrid.web.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.userdetails.UserDetails;
import org.psygrid.security.attributeauthority.client.AAQueryClient;
import org.psygrid.security.attributeauthority.types.PostProcessLoginResponseType;
import org.psygrid.web.helpers.ExceptionHelper;
import org.psygrid.web.helpers.ModelHelper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Controller for the main index/welcome page of the PsyGrid
 * clinical website.
 * 
 * @author Rob Harper
 *
 */
public class IndexController implements Controller{

	private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy");
	
	private static final int MAX_LOGINS_TO_DISPLAY = 5;
	
	private AAQueryClient aaqc;
	
	public IndexController(AAQueryClient aaqc){
		this.aaqc = aaqc;
	}
	
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
    		throws ServletException, IOException {
		ModelAndView mav = null;
    	try{
        	Map<String, Object> model = ModelHelper.getTemplateModel();
        	UserDetails user = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    		PostProcessLoginResponseType pplrt = aaqc.postProcessLogin(user.getUsername());
    		int nLogins = pplrt.getPreviousLoginAddresses().length;
    		int nLoginsToDisplay = nLogins;
    		if ( nLoginsToDisplay > MAX_LOGINS_TO_DISPLAY ){
    			nLoginsToDisplay = MAX_LOGINS_TO_DISPLAY;
    		}
    		String[][] loginHistory = new String[nLoginsToDisplay][3];
    		int counter = nLoginsToDisplay;
    		for ( int i=nLogins-nLoginsToDisplay, c=nLogins; i<c; i++ ){
    			counter--;
    			loginHistory[counter][0] = formatter.format(pplrt.getPreviousLoginDates(i).getTime());
    			loginHistory[counter][1] = pplrt.getPreviousLoginAddresses(i);
    			if ( pplrt.getAuthenticated(i) ){
    				loginHistory[counter][2] = "Success";
    			}
    			else{
    				loginHistory[counter][2] = "Failure";
    			}
    		}
    		model.put("history", loginHistory);
    		mav = new ModelAndView("index", model);
    	}
    	catch(Exception ex){
    		mav = ExceptionHelper.handleWsException(ex);
    	}
    	return mav;
    }
}
