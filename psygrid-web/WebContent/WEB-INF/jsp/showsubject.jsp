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
<html>
	<head>
		<c:import url="authorization.jsp" />
		<link rel="stylesheet" type="text/css" href="/psygrid/styles/eslforms.css" />
      
		<title>Participant Register - Details and participant identifier</title>
	</head>
	<body>
		<h1>Details and participant identifier</h1>
		<c:if test="${!empty message }">
			<p class="message">${message}</p>
		</c:if>
		<form method="post" action=""  class="buttons">
		<c:if test="${'yes' == eslWebEditPrivilege }">
			<input class="submit" name="_target3" type="submit" value="Edit" />
		</c:if><br/>
 		<form/>
 		<br/>

		<table  class="esl" border=0>
			<tr>
				<th colspan="2">${personalDetails.studyNumber}</th>
			</tr>
    		<tr>  
				<td>Title</td>
				<td>${personalDetails.title} </td>
			</tr>
			<tr>  
				<td >First Name</td>
				<td>${personalDetails.firstName}</td>
		    </tr>
	    	<tr>  
				<td>Last Name</td>
   				<td>${personalDetails.lastName }</td>
			</tr>
			<tr class="dateOfBirth">  
				<td>Date of Birth</td>
				<td>${personalDetails.dateOfBirth}</td>
			</tr>
			<tr>  
				<td>Sex</td>
				<td>${personalDetails.sex }</td>
			</tr>
			<tr>  
				<td>Address Line 1 </td>
				<td>${personalDetails.address1 }</td>
			</tr>
			<tr>  
				<td>Address Line 2</td>
				<td>${personalDetails.address2 }</td>
			</tr>
			<tr>  
				<td>Address Line 3</td>
				<td>${personalDetails.address3 }</td>
			</tr>
			<tr>  
				<td>City</td>
				<td>${personalDetails.city }</td>
			</tr>
			<tr>  
				<td>Country</td>
				<td>${personalDetails.country }</td>
			</tr>
			<tr>  
				<td>Region</td>
				<td>${personalDetails.region }</td>
			</tr>  
			<tr>  
				<td>Postcode</td>
				<td>${personalDetails.postCode }</td>
			</tr>  
			<tr>  
				<td>Email Address</td>
				<td>${personalDetails.emailAddress}</td>
			</tr>  
			<tr>  
				<td>Home Phone</td>
				<td>${personalDetails.homePhone }</td>
			</tr>					      
			<tr>  
				<td>Work Phone</td>
				<td>${personalDetails.workPhone }</td>
			</tr>
			<tr>  
				<td>Mobile Phone</td>
				<td>${personalDetails.mobilePhone }</td>
			</tr>
			<tr>  
				<td>NHS Number</td>
				<td>${personalDetails.nhsNumber }</td>
			</tr>
			<tr>  
				<td>Hospital Number</td>
				<td>${personalDetails.hospitalNumber }</td>
			</tr>
			<tr>  
				<td>Centre Number</td>
				<td>${personalDetails.centreNumber }</td>
			</tr>
			<tr>  
				<td>Centre name</td>
				<td>${personalDetails.groupName }</td>
			</tr>
			<tr>  
				<td>Centre code</td>
				<td>${personalDetails.groupCode }</td>
			</tr>
			<tr>  
				<td>Risk Issues</td>
				<td>${personalDetails.riskIssues}</td>
			</tr>
	  	</table> 
		<br/>
		<form method="post" action="" >
		<c:if test="${'yes' == eslWebEditPrivilege }">
			<input class="submit" name="_target2" type="submit" value="Edit" />
		</c:if>
 		<form/>
	</body>
</html>
