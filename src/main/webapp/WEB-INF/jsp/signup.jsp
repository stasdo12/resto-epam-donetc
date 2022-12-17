<%--
  Created by IntelliJ IDEA.
  User: sante
  Date: 17.12.2022
  Time: 11:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<c:set var="title" value="Sign Up" scope="page"/>

<body>

<div class="login-box">
  <p class="login-title">Sign Up</p>

  <form class="login-form" action="${pageContext.request.contextPath}/signup" method="post">
    <label>
    <input name="login" class="login-input" placeholder="Login" required minlength="8" maxlength="20" >
    <input name="password" class="login-input" placeholder="Password" required minlength="8" maxlength="20">
    <input type="submit" class="login-buttom" value="Sign Up">
    </label>
  </form>
  <c:if test="${param.err != null}">
    <p class="login-error"> User with this Login Or Password alredy exists.</p>
  </c:if>
  <p class="login-register">Already have account? <a href="${pageContext.request.contextPath}/login">Log In</a> </p>
</div>
</body>
</html>
