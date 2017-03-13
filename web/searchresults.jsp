<%-- 
    Document   : searchresults
    Created on : Mar 6, 2017, 11:06:14 PM
    Author     : Josh
--%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="/WEB-INF/tlds/dtbweb" prefix="dtbweb"%>
<%@page errorPage="error.jsp" contentType="text/html" pageEncoding="UTF-8"%>

<c:choose>
  <c:when test="${SearchResults == null && empty SearchResults.pages}">
    <c:set var="srSearchSize" value="0"/>      
  </c:when>  
  <c:otherwise>
    <c:set var="srSearchSize" value="${fn:length(SearchResults.pages)}"/>        
  </c:otherwise>
</c:choose>

<!DOCTYPE html>
<html>
  <head>
    <%@include file="/WEB-INF/jspf/defaultHeadContents.jspf"%> 
    <title>HQ NAF Task Tracker - Search Results</title>
  </head>
  <body>
    <dtbweb:header/>
    <c:set var="searchPath" value="${pageContext.servletContext.contextPath}/search"/>
    
    <jsp:useBean id="SelectorBean" class="com.doctracker.basic.web.beans.SelectorBean" scope="application"/>
    <jsp:setProperty name="SelectorBean" property="request" value="<%=request%>"/>
    <jsp:setProperty name="SelectorBean" property="tableName" value="appointment"/>
    <jsp:setProperty name="SelectorBean" property="limit" value="100"/>

    <form id="searchform" method="post" action="${searchPath}">
    <table id="searchtable">
      <tr>
        <td><input type="text" name="query"/></td> 
        <td colspan="2"><input class="button0" type="submit" value="Search"/></td>
      </tr>
      <tr>
        <td>
            <select name="reponsibility" size="1">
<%-- We use empty string here as when we left out the value altogether it used the contents of the option tag --%>        
                <option value="" disabled selected>Select Appointment</option>  
                <c:forEach var="Appointment" items="${SelectorBean.resultList}">
                  <option value="${Appointment.appointment}">${Appointment.abbreviation}</option>
                </c:forEach>
            </select>
        </td>
        <td><input type="checkbox" name="closed" value="true"/></td>
        <td><span class="smallerLighter">Closed</span></td>  
      </tr>    
    </table>
    </form> 
                
    <div id="main_container">
    <c:choose>
      <c:when test="${srSearchSize != 0}">
          
        <p><tt><dtbweb:paginationMessage searchResultsBean="${SearchResults}" searchServletPath="${searchPath}"/></tt></p> 

        <jsp:useBean id="CurrentPage" class="com.doctracker.basic.web.beans.TableModelBean" scope="session"/>
        <jsp:setProperty name="CurrentPage" property="tableModel" value="${SearchResultsPageTableModel}"/>
        
        <c:choose>
            <c:when test="${mobile}"><c:set var="srDatePattern" value="dd MMM yy"/></c:when>
            <c:otherwise><c:set var="srDatePattern" value="dd MMM yy HH:mm"/></c:otherwise>
        </c:choose>
        
        <table id="searchresultstable">
          <thead>
            <tr>  
            <c:forEach begin="0" end="${CurrentPage.columnCount-1}" varStatus="vsCol">
              <jsp:setProperty name="CurrentPage" property="columnIndex" value="${vsCol.index}"/>
              <th>${CurrentPage.columnName}</th>      
            </c:forEach>
            </tr> 
          </thead>
          <tbody>
            <c:forEach begin="0" end="${CurrentPage.rowCount-1}" varStatus="vsRow">
            <jsp:setProperty name="CurrentPage" property="rowIndex" value="${vsRow.index}"/>    
            <tr>
              <c:forEach begin="0" end="${CurrentPage.columnCount-1}" varStatus="vsCol">
                <jsp:setProperty name="CurrentPage" property="columnIndex" value="${vsCol.index}"/>  
                <td>
                    <c:choose>
                        <c:when test="${CurrentPage.columnClass.name == 'java.util.Date'}">
                            <dtbweb:displayDate date="${CurrentPage.valueAt}" pattern="${srDatePattern}" displayAsTimeElapsed="false"/>    
                        </c:when>
                        <c:otherwise>
                            ${CurrentPage.valueAt}<c:if test="${vsCol.index == 0}">.</c:if>
                        </c:otherwise>
                    </c:choose>
                </td>
              </c:forEach>
            </tr>
            </c:forEach>
          </tbody>
        </table>
        
        <b><dtbweb:paginationLinks pagePerBatch="10" searchResultsBean="${SearchResults}" searchServletPath="${searchPath}"/></b> 

      </c:when>    
      <c:otherwise>
          
        <p>${srSearchSize} search results</p>
        
      </c:otherwise>
    </c:choose>  
    </div>            
                
  </body>
</html>
