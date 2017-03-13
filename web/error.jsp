<%-- 
    Document   : error
    Created on : Mar 6, 2017, 11:06:31 PM
    Author     : Josh
--%>
<%@page isErrorPage="true" contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/tlds/dtbweb" prefix="dtbweb"%>
<!DOCTYPE html>
<html>
    <head>
        <%@include file="/WEB-INF/jspf/defaultHeadContents.jspf"%> 
        <title>HQ NAF Task Tracker - Error Page</title>
    </head>
    <body>
        
        <dtbweb:header headerText="Error Page"/>
        
        <c:out default="An unexpected error occured" 
               value="${userMessage == null ? pageContext.errorData.throwable.localizedMessage : userMessage}"/>
        
        <p><small><tt>
            Requested resource: ${pageContext.errorData.requestURI}<br/>
            Status Code: ${pageContext.errorData.statusCode}
        </tt></small></p>
        <c:if test="${productionMode != null && !productionMode}">
            <c:forEach var="stackTrace" items="${pageContext.exception.stackTrace}">
              ${stackTrace}<br/>    
            </c:forEach>
        </c:if>
    </body>
</html>
