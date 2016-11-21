<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %>
<!DOCTYPE HTML>

<html>
<head>
<title>Courier listing</title>
</head>
<body>

<table>
	<tr>
		<th>Id</th>
		<th>Name</th>
		<th>Is On</th>
		<th>State</th>
		<th>Lattitude</th>
		<th>Longtitude</th>
	</tr>

	<c:forEach var="courier" items="${requestScope.list}">
		<tr>
			<td>${courier.id}</td>
			<td>${courier.name}</td>
			<td>${courier.on}</td>
			<td>${courier.state}</td>
			<td>${courier.lat}</td>
			<td>${courier.lng}</td>
		</tr>
	</c:forEach>
</table>

</body>
</html>