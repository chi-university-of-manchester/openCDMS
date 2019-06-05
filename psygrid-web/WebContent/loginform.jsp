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
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core' %>
<form method="post" action="<c:url value='j_acegi_security_check'/>" class="login">
	<fieldset>
		<label for="username">Username: </label>
		<input type="text" name="j_username" id="username" title="username" /><br/>
		<label for="password">Password: </label>
		<input type="password" name="j_password" id="password" title="password" /><br />
		<label for="submit"></label>
		<input value="Login" type="submit" id="submit" />
	</fieldset>
</form>