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
		<script type="text/javascript">
		function enable_alltargets(obj){
			if (document.form1.newtargets.value == "all"){
				obj.disabled=false;
			}
			else {
				obj.disabled=true;
			}
		}
		</script>
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
					<spring:bind path="report.targets">
					<span class="error"><c:out value="${status.errorMessage}"/></span>
					</spring:bind>
					</p>
				
					<p>
					<label for="newtargets">Recruitment targets: </label>
					<input type="radio" value="all" name="newtargets" class="radio" /><span class="checkboxtext">All</span>
					<label for="alltargets" style="width:75px">All Months</label><input type="text" name="alltargets" value="0" />
					<br />
					</p>
						
					<p>
					<label for="newtargets"></label>
					<input type="radio" value="monthly" name="newtargets" class="radio"/><span class="checkboxtext">Per Month</span><br />
					<c:forEach items="${targets}" var="target">
						<label for=""></label>
						<label for="newtargets">${fn:split(target, '=')[0]}</label>
						<input type="text" name="monthlytarget-${fn:split(target, '=')[0]}" value="${fn:split(target, '=')[1]}" /><br/>
					</c:forEach>
					<br />
					</p>
					
				<div>
					<label for="back"></label>
					<input type="submit" name="_target3" value="Back" id="back" />
					<label for="submit"></label>
					<input type="submit" value="Continue" name="_target5" id="submit" />
				</div>
				
			</fieldset>
		</form>
		
		
<c:import url="footer.jsp" />