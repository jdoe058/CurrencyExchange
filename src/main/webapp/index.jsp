<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Currency exchange</title>
</head>
<body>
<h1>Currency exchange</h1>
<br/>
<%@include file="WEB-INF/jsp/navigation.jsp" %>
<form action="${pageContext.request.contextPath}/api/exchange">
    <label for="from">
        Base currency code:
        <input type="text" name="from" id="from" placeholder="USD">
    </label><br/>
    <label for="to">
        Target currency code:
        <input type="text" name="to" id="to" placeholder="RUB">
    </label><br/>
    <label for="amount">
        Amount:
        <input type="text" name="amount" id="amount" placeholder="100">
    </label><br/>
    <button type="submit">Calculate</button>
</form>
</body>
</html>