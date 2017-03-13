<%-- 
    Document   : index
    Created on : Mar 6, 2017, 8:56:42 PM
    Author     : Josh
--%>
<%@page errorPage="error.jsp" contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/tlds/dtbweb" prefix="dtbweb"%>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="/WEB-INF/jspf/defaultHeadContents.jspf"%> 
        <title>HQ NAF Task Tracker - Index Page</title>
    </head>
    <body>
        <dtbweb:header headerText="Index Page"/>
        <p><a href="${pageContext.servletContext.contextPath}/index.jsp">Index Page</a></p>
        <p><a href="${pageContext.servletContext.contextPath}/searchresults.jsp">Search Results Page</a></p>
        <p><a href="${pageContext.servletContext.contextPath}/login.jsp">Login Page</a></p>
        <p><a href="${pageContext.servletContext.contextPath}/error.jsp">Error Page</a></p>
    </body>
</html>
