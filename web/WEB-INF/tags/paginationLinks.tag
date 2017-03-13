<%@tag trimDirectiveWhitespaces="true" description="Tag for rendering search results paging links" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/tlds/dtbweb" prefix="dtbweb"%>

<%@attribute name="searchResultsBean" required="true" type="com.bc.jpa.search.SearchResults"%>
<%@attribute name="searchServletPath" required="true" description="a full url or path starting with '/' but not ending with '/'. A query of form page=0 etc will be appended to this path for pagination access to further pages in the search results"%>
<%@attribute name="pagePerBatch" required="false"%>

<c:if test="${pagesPerBatch == null}">
  <c:set var="pagesPerBatch" value="${mobile?5:10}"/>    
</c:if>

  <c:if test="${searchResultsBean.pageCount > 1}">
      
    <c:set var="sr_pagesPerBatch" value="${pagesPerBatch}"/>
    <c:set var="sr_pagesRem" value="${searchResultsBean.pageNumber < sr_pagesPerBatch ? searchResultsBean.pageNumber : searchResultsBean.pageNumber % sr_pagesPerBatch}"/>
    <c:set var="sr_pagesBegin" value="${searchResultsBean.pageNumber - sr_pagesRem}"/>
    <c:set var="sr_targetEnd" value="${sr_pagesBegin + sr_pagesPerBatch}"/>
    <c:set var="sr_pagesEnd" value="${sr_targetEnd > searchResultsBean.pageCount ? searchResultsBean.pageCount : sr_targetEnd}"/>
    
<%--@debug p> Pages per batch: ${sr_pagesPerBatch}, Pages remainder: ${sr_pagesRem}, Pages begin: ${sr_pagesBegin}, Target end: ${sr_targetEnd}, Pages end: ${sr_pagesEnd}</p --%>    

    <div>
      <c:if test="${sr_pagesBegin > 0}">
        <a href="${searchServletPath}?page=${sr_pagesBegin-1}">&lt;&lt;</a>    
      </c:if>  
      <c:forEach varStatus="vs" begin="${sr_pagesBegin}" end="${sr_pagesEnd - 1}">
        <c:choose>
          <c:when test="${vs.index == searchResultsBean.pageNumber}">
            <c:set var="sr_pagelinkStyle" value="font-size:1.25em; border:1px solid blue; padding:0 0.25em 0 0.25em;"/>
          </c:when>
          <c:otherwise>
            <c:set var="sr_pagelinkStyle" value="font-size:1em;"/>  
          </c:otherwise>
        </c:choose>    
        <a style="${sr_pagelinkStyle}" href="${searchServletPath}?page=${vs.index}">${vs.index + 1}</a>&nbsp;&nbsp;
      </c:forEach>   
      <c:if test="${sr_pagesEnd < searchResultsBean.pageCount}">
        <a href="${searchServletPath}?page=${sr_pagesEnd}">&gt;&gt;</a>    
      </c:if>  
    </div>
  </c:if>  
