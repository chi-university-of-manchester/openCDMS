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
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
	<head>
		<c:import url="../authorization.jsp" />
		<c:import url="header.jsp" />

		<form method="post" action="recordreport.html" class="report">
			<fieldset>
				
				<spring:hasBindErrors name="report">
				    <b class="error">Please fix all errors!</b>
				</spring:hasBindErrors>
			
				<!-- if a dataset has been selected put in static text field -->
				<p>
					<label for="dataset">Study: </label><span class="answer">${report.dataset}</span>
				</p>
				
				<p>	
					<label for="title">Report: </label><span class="answer">${report.title}</span>
				</p>
				
				<p>
					<spring:bind path="report.identifier">	
					<label for="identifier">Participant Identifier: </label>
					<select name="identifier">
					<c:forEach items="${identifiers}" var="identifier">
						<option value="${identifier}">${identifier}</option>
					</c:forEach>
					</select>
    				</spring:bind>
				</p>

				<br />
				<br />
				<div>
					<label for="back"></label>
					<input type="submit" name="_target1" value="Back" id="back" />
					<label for="submit"></label>
					<input type="submit" value="Continue" name="_target3" id="submit" />
				</div>
			</fieldset>
		</form>
		
		
<c:import url="footer.jsp" />