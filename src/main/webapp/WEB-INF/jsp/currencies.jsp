<%--
  Created by IntelliJ IDEA.
  User: eugene
  Date: 13.12.2024
  Time: 19:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Currencies</title>
</head>
<body>
<h1>Currencies</h1>
<%@include file="navigation.jsp" %>
<form action="${pageContext.request.contextPath}/currencies" method="post">
    <label for="name">Name:<input type="text" name="name" id="name" placeholder="US Dollar"></label><br>
    <label for="code">Code:<input type="text" name="code" id="code" placeholder="USD"></label><br>
    <label for="sign">Sign:<input type="text" name="sign" id="sign" placeholder="$"></label><br>
    <button type="submit">Add</button>
    <br/>

    <c:if test="${not empty requestScope.errors}">
        <c:forEach var="error" items="${requestScope.errors}">
            <span style="color: darkred">${error.message}</span><br>
        </c:forEach>
    </c:if>
</form>
<a href="${pageContext.request.contextPath}/api/currencies">
    Currencies (${requestScope.currencies.size()}):
</a>
<ul>
    <c:forEach var="currency" items="${requestScope.currencies}">
        <li><a href="/api/currency/${currency.code}">
                ${currency.name} (${currency.sign})
        </a></li>
    </c:forEach>
</ul>
</body>
</html>
