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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
	<head>
		<c:import url="authorization.jsp" />
		<title>Participant Register - ${title}</title>
		
        <link rel="stylesheet" type="text/css" href="/psygrid/styles/eslforms.css" />
       <link rel="stylesheet" type="text/css" href="/psygrid/styles/calendar-blue.css" />
		<script type="text/javascript" src="/psygrid/scripts/calendar.js"></script>
		<script type="text/javascript" src="/psygrid/scripts/calendar-en.js"></script>
		<script type="text/javascript" src="/psygrid/scripts/calendar-setup.js"></script>
		
		<script type="text/javascript">
			function clear(elementId){
				document.getElementById(elementId).value='';
			}
		</script>
		        
        <script type="text/javascript">
			function CheckMaxLength(Object, MaxLen)
			{
				if(Object.value.length > MaxLen)
			  	{     
			    	Object.value = Object.value.substring(0, MaxLen);
			  	}
			}
		</script>
        
	</head>
	<body>
		<h1>${heading}</h1>
 		<p>${description}</p>
 		<c:if test="${ !empty personalDetails.studyNumber}">
 			<h2>${personalDetails.studyNumber}</h2>
		</c:if>
 		
 		<spring:bind path="personalDetails">
        	<c:forEach items="${status.errorMessages}" var="error">
        		<p class="error">${error}</p>
        	</c:forEach>
		</spring:bind>
 		
		<form method="post" action="" id="horizontalForm">
		
			<!-- 
			Had to remove the buttons from the top of the form as
			it broke the layout in IE...
			-->
			
			<fieldset class="buttons">
				<span>
				<c:if test="${'yes' == treatmentarm}">
					<input class="submit" name="submit" type="submit" value="Find treatment arm" />
				</c:if>
				<c:if test="${'yes' == studycode}">
					<input class="submit" name="_target1" type="submit" value="Search" />
				</c:if>
				<c:if test="${'yes' == editdetails}">
					<input class="submit" name="_finish" type="submit" value="Update details" />
				</c:if>
				</span>
			</fieldset>
			
			<!--  Resusable Form Details -->
			<fieldset>
				<legend>
					Personal Information
				</legend>
				<spring:bind path="personalDetails.title">
					<label for="${status.expression}">
						Title 
						<input class="short" name="${status.expression}" id="${status.expression}" type="text" value="${status.value}" />
					</label>
				</spring:bind>
				
				<spring:bind path="personalDetails.firstName">
					<label for="${status.expression}">
						First name
						<input class="medium" name="${status.expression}" id="${status.expression}" type="text" value="${status.value}" />
					</label>
				</spring:bind>
				
				<spring:bind path="personalDetails.lastName">
					<label for="${status.expression}">
						Last name
						<input class="medium" name="${status.expression}" id="${status.expression}" type="text" value="${status.value}" />
					</label>
				</spring:bind>
				
				<spring:bind path="personalDetails.sex">
					<label for="${status.expression}">
						Sex
						<select name="${status.expression}" id="${status.expression}">
							<option></option>
							<option ${status.value == "Male" ? 'selected' : ''}>Male</option>
							<option ${status.value == "Female" ? 'selected' : ''}>Female</option>
						</select>
					</label>
				</spring:bind>
			</fieldset>
			
			<fieldset>
				<legend> 
				Date of Birth 
				</legend>
				<spring:bind path="personalDetails.dateOfBirth">
				<label for="${status.expression}"  style="margin-top: 5px;" >Date of birth </label>
					<input type="text" name="${status.expression}" id="${status.expression}" value="<c:if test="${!empty personalDetails.dateOfBirth}">${personalDetails.dateOfBirth}</c:if>" style="display: inline;"  READONLY></input><img src="/psygrid/images/calendarIcon.png" alt="Select a date of birth" id="f_trigger_c" style="cursor: pointer;" width="28px" height="21px" />
					&nbsp; <span class="clear-link" ><a href="javascript:clear('${status.expression}')" >Clear</a></span>				
					<br />
					<span class="error"><c:out value="${status.errorMessage}"/></span>
					
				<script type="text/javascript">
					  Calendar.setup(
					    {
					      inputField  : "${status.expression}",
					      <c:if test="${!empty personalDetails.dateOfBirth}">date : "${personalDetails.dateOfBirth}",</c:if>
					      ifFormat    : "%d-%m-%Y",
					      button      :    "f_trigger_c",
					      align       :    "Br",
					      singleClick :    true
					    }
					  );
				</script>
				</spring:bind>
			</fieldset>
			
			<fieldset>
				<legend>
					Address
				</legend>
				
				<spring:bind path="personalDetails.address1">
				<label for="${status.expression}">
					Address Line 1 
					<input name="${status.expression}" id="${status.expression}"  type="text" value="${status.value}" />
				</label>
				</spring:bind>
				
				<spring:bind path="personalDetails.address2">
				<label for="${status.expression}">
					Address Line 2
					<input name="${status.expression}" id="${status.expression}"  type="text" value="${status.value}" />
				</label>
				</spring:bind>
				
				<spring:bind path="personalDetails.address3">
				<label for="${status.expression}">
					Address Line 3
					<input name="${status.expression}" id="${status.expression}"  type="text" value="${status.value}" />
				</label>
				</spring:bind>
				
				<spring:bind path="personalDetails.city">
				<label for="${status.expression}">
					City
					<input name="${status.expression}" id="${status.expression}"  type="text" value="${status.value}" />
				</label>
				</spring:bind>
				
				<spring:bind path="personalDetails.region">
				<label for="${status.expression}">
					Region
					<input name="${status.expression}" id="${status.expression}"  type="text" value="${status.value}" />
				</label>
				</spring:bind>
				
				<spring:bind path="personalDetails.country">
				<label for="${status.expression}">
					Country
					<input name="${status.expression}" id="${status.expression}" type="text" value="${status.value}" />
				</label>
				</spring:bind>
				
				<spring:bind path="personalDetails.postCode">
				<label for="${status.expression}">
					Postcode
					<input name="${status.expression}" id="${status.expression}" type="text" value="${status.value}" />
				</label>
				</spring:bind>
			</fieldset>
			
			<fieldset>
				<legend> 
					Email
				</legend>
				
				<spring:bind path="personalDetails.emailAddress">
				<label for="${status.expression}">
					Email Address
					<input name="${status.expression}" id="${status.expression}" type="text" value="${status.value}" />
				</label>
				</spring:bind>
				
			</fieldset>
			
			<fieldset>
				<legend>
					Phone Numbers
				</legend>
				
				<spring:bind path="personalDetails.homePhone">
				<label for="${status.expression}">
					Home Phone
					<input name="${status.expression}" id="${status.expression}" type="text" value="${status.value}" />
				</label>
				</spring:bind>
				
				<spring:bind path="personalDetails.workPhone">
				<label for="${status.expression}">
					Work Phone
					<input name="${status.expression}" id="${status.expression}" type="text" value="${status.value}" />
				</label>
				</spring:bind>
				
				<spring:bind path="personalDetails.mobilePhone">
				<label for="${status.expression}">
					Mobile Phone
					<input name="${status.expression}" id="${status.expression}" type="text" value="${status.value}" />
				</label>
				</spring:bind>
				
			</fieldset>
				
			<fieldset>
				<legend>
					Health Details
				</legend>
				
				<spring:bind path="personalDetails.nhsNumber">
				<label for="${status.expression}">
					NHS Number
					<input name="${status.expression}" id="${status.expression}" type="text" value="${status.value}" />
				</label>
				</spring:bind>
				
				<spring:bind path="personalDetails.hospitalNumber">
				<label for="${status.expression}">
						Hospital Number
					<input name="${status.expression}" id="${status.expression}" type="text" value="${status.value}" />
				</label>
				</spring:bind>
				
				<spring:bind path="personalDetails.centreNumber">
				<label for="${status.expression}">
						Centre Number
					<input name="${status.expression}" id="${status.expression}" type="text" value="${status.value}" />
				</label>
				</spring:bind>
				
			</fieldset>
			
			<fieldset>
				<legend>
					Risk Issues 
				</legend>
				<spring:bind path="personalDetails.riskIssues">
				<!-- Note textarea only inside a span to fix the "IE inherited margin bug"; 
				see http://jaspan.com/ie-inherited-margin-bug-form-elements-and-haslayout -->
				<span>
				<textarea name="${status.expression}" id="${status.expression}" cols="60" rows="8" onkeyup="CheckMaxLength(this, 4096);" >${status.value}</textarea>	
				</span>
				</spring:bind>
			</fieldset>
			
			<fieldset class="buttons">
				<span>
				<c:if test="${'yes' == treatmentarm}">
					<input class="submit" name="submit" type="submit" value="Find treatment arm" />
				</c:if>
				<c:if test="${'yes' == studycode}">
					<input class="submit" name="_target1" type="submit" value="Search" />
				</c:if>
				<c:if test="${'yes' == editdetails}">
					<input class="submit" name="_finish" type="submit" value="Update details" />
				</c:if>
				</span>
			</fieldset>
		</form>
	</body>
</html>
