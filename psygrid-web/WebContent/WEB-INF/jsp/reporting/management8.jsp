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
					<label for="dataset">Study: </label><span class="answer">${report.dataset}</span>
				</p>
				
				<p>	
					<label for="title">Report: </label><span class="answer">${report.title}</span>
				</p>
				
				<p>
				<c:if test="${!empty report.groups}">
				<label for="groups">Centres: </label>
				<c:forEach items="${report.groups}" var="group">
					<span class="answer">${fn:split(group, '=')[1]}</span>
					<label for="group"></label>
				</c:forEach>
				</c:if>
				</p>
			    
				<c:if test="${!empty report.startDate}">
				<p>
				<label for="startDate">Start Date: </label>
				<span class="answer">${report.formattedStartDate}</span>
				</p>
				</c:if>
				
				<c:if test="${!empty report.endDate}">
				<p>
				<label for="endDate">End Date: </label>
				<span class="answer">${report.formattedEndDate}</span>
				</p>
				</c:if>

				<p>
				<c:if test="${!empty report.targets}">
				<label for="targets">Recruitment Targets: </label>
				<c:forEach items="${report.targets}" var="target">
					<span class="answer">${fn:split(target, '=')[0]}  :   ${fn:split(target, '=')[1]}</span>
					<label for="target"></label>
				</c:forEach>
				</c:if>
				</p>
									
				<c:if test="${!empty report.document}">
				<p>
				<label for="document">Document: </label>
				<span class="answer">${report.document.name}</span>
				</p>
				</c:if>
				
				<c:if test="${!empty report.docOcc}">
				<p>
				<label for="document">Occurrence: </label>
				<span class="answer">${report.docOcc.name}</span>
				</p>
				</c:if>
				
				<p>
				<c:if test="${!empty report.entries}">
				<label for="entries">Entries: </label>
				<c:forEach items="${report.entries}" var="entry">
					<span class="answer">${entry.displayText}</span>
					<label for="entry"></label>
				</c:forEach>
				</c:if>
				</p>
				
				<p>
				<label for="statistics">Statistics: </label><br/>
				<spring:bind path="report.statistics">	
		            <c:if test="${status.error}">
		                <c:forEach items="${status.errorMessages}" var="error">
		                    <span class="error"><c:out value="${error}"/></span>
		                </c:forEach>
		            </c:if>
					<c:forEach items="${statTypes}" var="stat">
						<div>
    					<input type="checkbox" class="checkbox" name="${status.expression}" id="stat_${stat}" value="${stat}" />
    					<label for="stat_${stat}" class="checkboxlabel1">${stat}</label>
						</div>
					</c:forEach>
				</spring:bind>
				</p>

				<br /><br />
				<div>
					<label for="back"></label>
					<input type="submit" name="_target7" value="Back" id="back" />
					<label for="submit"> </label>
					<input type="submit" value="Continue" name="_target9" id="submit" />
				</div>
			</fieldset>
		</form>
		
		
<c:import url="footer.jsp" />