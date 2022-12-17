<%--
  Created by IntelliJ IDEA.
  User: sante
  Date: 17.12.2022
  Time: 11:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>

<c:set var="title" value="Log In" scope="page"/>

<%@include file="../jspf/head.jspf"%>

<body>
<div class="login-box">
<p class="login-title">Log In</p>\
<form class="login-form" action="${pageContext.request.contextPath}/login" method="post">
    <label>
        <input placeholder="Login" type="text" class="login-input" name="login"/>
    <input placeholder="Password" type="text" class="login-input" name="password"/>
    <input type="submit" class="login-button" value="Log in!"/>
    </label>
</form>

<c:if test="${param.err !=null}">
    <p class="login-error">Wrong Login or Password, Try again....</p>
</c:if>
<p class="login-register">Don't have a account? <a href="${pageContext.request.contextPath}/signup"> Sign Up</a> </p>
</div>
</body>
</html>