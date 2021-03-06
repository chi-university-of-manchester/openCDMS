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

		<form method="post" action="managementreport.html" class="report">
			<fieldset>
				
					<!-- if a dataset has been selected put in static text field -->
					<p>
						<label for="dataset">Study: </label> <span class="answer">${report.dataset}</span>
					</p>

					<p>	
						<label for="title">Report: </label>
						<select name="title" id="title" class="select-width">
						<c:forEach items="${titles}" var="title">
							<option value="${title}">${fn:split(title, '=')[1]}</option>
						</c:forEach>
						</select>
					</p>

				<br /><br />
				<div>
					<label for="back"></label>
					<input type="submit" name="_target0" value="Back" id="back"/>
					<label for="submit"></label>
					<input type="submit" value="Continue" name="_target2" id="submit"/>
				</div>
			</fieldset>
		</form>

<c:import url="footer.jsp" />