<%--
  Created by IntelliJ IDEA.
  User: eugene
  Date: 16.12.2024
  Time: 09:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Exchange rates</title>
</head>
<body>
<h1>Exchange rates</h1>
<%@include file="navigation.jsp" %>
<form action="${pageContext.request.contextPath}/exchangeRates" method="post">
  <label for="baseCurrencyCode">
    Base currency code:
    <input type="text" name="baseCurrencyCode" id="baseCurrencyCode" placeholder="USD">
  </label><br>
  <label for="targetCurrencyCode">
    Target currency code:
    <input type="text" name="targetCurrencyCode" id="targetCurrencyCode" placeholder="RUB">
  </label><br>
  <label for="rate">
    Rate:<input type="text" name="rate" id="rate" placeholder="1.00"></label><br>
  <button type="submit">Add</button>
  <br/>

  <c:if test="${not empty requestScope.errors}">
    <c:forEach var="error" items="${requestScope.errors}">
      <span style="color: darkred">${error.message}</span><br>
    </c:forEach>
  </c:if>
</form>
<a href="${pageContext.request.contextPath}/api/exchangeRates">
  Exchange rates (${requestScope.exchangeRates.size()}):
</a>
<ul>
  <c:forEach var="rate" items="${requestScope.exchangeRates}">
    <li><a href="/api/exchangeRate/${rate.baseCurrency.code}${rate.targetCurrency.code}">
        from ${rate.baseCurrency.name} to ${rate.targetCurrency.name} rate ${rate.rate}
    </a></li>
  </c:forEach>
</ul>
</body>
</html>
