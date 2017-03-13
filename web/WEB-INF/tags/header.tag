<%-- 
    Document   : header
    Created on : Mar 9, 2017, 11:29:28 AM
    Author     : Josh
--%>
<%@tag description="put the tag description here" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@attribute name="headerText" required="false" description="The text to be displayed in the header"%>

<c:if test="${userMessage != null && userMessage != ''}"><div class="userMessage">${userMessage}</div></c:if>
<c:if test="${headerText == null}"><c:set var="headerText" value="&nbsp;"/></c:if>

<table id="headertable">
    <tr>
<%-- (58,65), (40,45) --%>        
        <td style="width:50px">
            <a href="${pageContext.servletContext.contextPath}/tracker">
                <img id="logo" alt="NAF Logo" width="50" height="53" 
                     src="${pageContext.servletContext.contextPath}/images/naflogo.jpg"/>
            </a>
        </td>
        <td>
            <div class="header0">
                <div style="width:100%; text-align:center">HQ NAF Task Tracker</div>
                <div>
                    ${headerText}
                    <small style="float:right;">
                        ${User.name} |
                        <c:choose>
                            <c:when test="${User.loggedIn}">
                                <a href="${pageContext.servletContext.contextPath}/logout">Logout</a>    
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.servletContext.contextPath}/login.jsp">Login</a>    
                            </c:otherwise>
                        </c:choose>
                    </small>
                </div>
            </div>
        </td>
    </tr>
</table>
