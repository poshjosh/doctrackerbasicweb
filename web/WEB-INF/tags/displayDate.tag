<%@tag description="Displays a date in the default format, or as time elapsed" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%@attribute name="date" type="java.util.Date" required="true"%>
<%@attribute name="displayAsTimeElapsed" required="false"%>
<%@attribute name="pattern" required="false"%>
<c:if test="${pattern == null}">
  <c:set var="pattern" value="dd MMM yy HH:mm:ss"/>    
</c:if>

<c:if test="${date != null}">
    <fmt:formatDate value="${date}" pattern="${pattern}" var="ddFormattedDate"/>${ddFormattedDate} 
</c:if>
