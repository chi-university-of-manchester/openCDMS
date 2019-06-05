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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
				<label for="identifier">Participant Identifier: </label>
				<span class="answer">${report.identifier}</span>
				</p>
				
				<p>
				<spring:bind path="report.formatType">	
				<label for="formatType">Format: </label>
				<select name="formatType" id="formatType">
					<c:forEach items="${formatTypes}" var="format">
						<option value="${format}">${format}</option>
					</c:forEach>
					</select>
				</spring:bind>
				</p>

				<br />
				<br />
				<div>
					<label for="back"></label>
					<input type="submit" name="_target2" value="Back" id="back" />
					<label for="submit"></label>
					<input type="submit" value="Continue" name="_target4" id="submit" />
				</div>
			</fieldset>
		</form>
		
		
<c:import url="footer.jsp" />