<%--
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
--%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
	
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>openCDMS - <decorator:title default="Clinical Portal" /></title>
        <link rel="stylesheet" type="text/css" href="/psygrid/styles/default.css" />
         <link rel="shortcut icon" type="image/x-icon" href="/psygrid/images/favicon.ico" />
 
        <decorator:head />
	</head>
	
	<body>
		<div id="main">
			<div id="login">
				<div id="banner">        		
	        	</div>
	        	<div id="login-form">
	        		<decorator:body />
	        	</div>
	        </div>
    		<div id="cleardiv"></div>
	    	<div id="footer">
	    		<div id="help">
	    			<p><a target="_blank" href="http://www.psygrid.org/psygrid/documents/help/ch06.html">Help</a></p>
	    		</div>
	    		<div id="copyright">
		    		<ul>
		    			<li>&copy; Copyright 2008 University of Manchester</li>
		    		</ul>
		    	</div>
	    	</div>
	    </div>
	</body>
</html>
