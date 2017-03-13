<%-- 
    Document   : login
    Created on : Mar 9, 2017, 12:35:31 AM
    Author     : Josh
--%>
<%@page isErrorPage="true" contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/tlds/dtbweb" prefix="dtbweb"%>
<!DOCTYPE html>
<html>
  <head>
    <%@include file="/WEB-INF/jspf/defaultHeadContents.jspf"%> 
    <title>HQ NAF Task Tracker - Login Page</title>
  </head>
    <body>
        <dtbweb:header headerText="Login Page"/>
        <form id="loginform" method="post" action="${pageContext.servletContext.contextPath}/login">
            <p><label>Username: <input type="text" name="username"/></label></p> 
            <p><label>Password: <input type="password" name="password"/></label></p>
            <p><input style="float:right;" class="button0" type="submit" value="Login"/></p>
        </form>
    </body>
</html>
