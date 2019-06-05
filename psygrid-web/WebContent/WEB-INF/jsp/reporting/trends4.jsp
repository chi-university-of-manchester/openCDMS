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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
	<head>
		<c:import url="../authorization.jsp" />
		<c:import url="header.jsp" />

		<form method="post" action="trendsreport.html" class="report">
			<fieldset>
				
					<!-- if a dataset has been selected put in static text field -->
					
					<p>
						<label for="dataset">Study: </label><span class="answer">${report.dataset}</span>
					</p>
					
					<p>	
						<label for="title">Report: </label><span class="answer">${report.title}</span>
					</p>
					
					<p>
					<label for="groups">Centres: </label>
					<c:forEach items="${report.groups}" var="group">
						<span class="answer">${fn:split(group, '=')[1]}</span>
						<label for="group"></label>
					</c:forEach>
					</p>

					<p>
					<label for="startDate">Start Date: </label>
					<span class="answer"><fmt:formatDate value="${report.startDate.time}" pattern="MMMM yyyy" /></span>
					</p>
					
					<p>
					<label for="endDate">End Date: </label>
					<span class="answer"><fmt:formatDate value="${report.endDate.time}" pattern="MMMM yyyy" /></span>
					</p>

					<p>
					<spring:bind path="report.summaryType">	
					<label for="summaryType">SummaryType: </label>
					<select name="summaryType" id="summaryType">
						<c:forEach items="${summaryTypes}" var="type">
							<option value="${type}">${type}</option>
						</c:forEach>
					</select><br/>
					<div class="explain">This will determine how the data is summarised. Select 'default' if unsure.</div>
					</spring:bind>
					</p>
					
					<p>
					<spring:bind path="report.showTotals">	
					<label for="showTotals">Show Totals: </label>
					<input type="radio" value="yes" name="showTotals" />Yes<br/>
					<label for="showTotalsNo"></label><input type="radio" value="no"  name="showTotals" checked/>No <br/>
					<div class="explain">This will show the total number of participants for each month on the report</div>
					</spring:bind>
					</p>

				<br />
				<div>
					<label for="back"></label>
					<input type="submit" name="_target3" value="Back" id="back" />
					<label for="submit"></label>
					<input type="submit" value="Continue" name="_target5" id="submit" />
				</div>
			</fieldset>
		</form>
		
		
<c:import url="footer.jsp" />