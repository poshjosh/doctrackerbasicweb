/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doctracker.basic.web.servlets;

import com.bc.jpa.dao.BuilderForSelect;
import com.bc.jpa.dao.BuilderForSelectImpl;
import com.bc.jpa.dao.SelectDao;
import com.bc.jpa.search.BaseSearchResults;
import com.bc.jpa.search.SearchResults;
import com.doctracker.basic.web.AttributeNames;
import com.doctracker.basic.web.WebApp;
import com.doctracker.basic.web.pu.SearchResultsTableModel;
import com.doctracker.basic.web.pu.TaskResultModel;
import com.doctracker.basic.web.pu.entities.Appointment;
import com.doctracker.basic.web.pu.entities.Appointment_;
import com.doctracker.basic.web.pu.entities.Doc;
import com.doctracker.basic.web.pu.entities.Doc_;
import com.doctracker.basic.web.pu.entities.Task;
import com.doctracker.basic.web.pu.entities.Task_;
import com.doctracker.basic.web.pu.entities.Taskresponse;
import com.doctracker.basic.web.pu.entities.Taskresponse_;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.table.TableModel;

/**
 * @author Josh
 */
@WebServlet(name = "Search", urlPatterns = {"/search", "/Search"})
public class Search extends BaseServlet {
    
    private transient static final Logger logger = Logger.getLogger(Search.class.getName());
    
    public Search() { }

    @Override
    public void doProcessRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try{  
        
            final SearchResults searchResults = this.execute(request, response);

            final String successPage = this.getSuccessPage(request, searchResults);

            request.getRequestDispatcher(successPage).forward(request, response);
        
        }catch(ServletException | IOException e) {

            final String message = e.getLocalizedMessage();

            request.setAttribute(AttributeNames.USER_MESSAGE, message);

            final String errorPage = this.getErrorPage(request, e);

            request.getRequestDispatcher(errorPage).forward(request, response);

            logger.log(Level.WARNING, "Unexpected Exception", e);
        }
    }
    
    protected SearchResults execute(HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("text/html;charset=UTF-8");

        final String pageStr = request.getParameter("page");

        final WebApp webApp = (WebApp)request.getServletContext().getAttribute(AttributeNames.WEB_APP);
        
        final HttpSession session = request.getSession();

        final SearchResults previousSearchResults = (SearchResults)session.getAttribute(AttributeNames.SEARCH_RESULTS);

        final SearchResults searchResults = pageStr != null ? 
                previousSearchResults : this.search(webApp, request);

        final int pageNumber = pageStr == null ? 0 : Integer.parseInt(pageStr);
        
        final int numberOfPages = searchResults.getSize() == 0 ? 0 : 1;

        this.update(session, searchResults, pageNumber, numberOfPages);
        
        return searchResults;
    }
    
    public SearchResults<Task> search(WebApp app, HttpServletRequest request) {
        
        logger.finer("#search(..) Creating search");
        
        final SelectDao<Task> dao = this.buildSelector(app, Task.class, request);
        final BaseSearchResults searchResults = new BaseSearchResults(dao, 20, true);
        
        logger.finer("#search(..) Done creating search");
        
        return searchResults;
    }

    public void update(HttpSession session, SearchResults searchResults) {
        final int numberOfPages = searchResults.getSize() == 0 ? 0 : 1;
        this.update(session, searchResults, 0, numberOfPages);
    }
    
    public void update(HttpSession session, SearchResults searchResults, int pageNumber, int numberOfPages) {
        
        logger.finer("#update(..) Updating searchresults session");
        
        final WebApp webApp = (WebApp)session.getServletContext().getAttribute(AttributeNames.WEB_APP);
        
        final SearchResults previousSearchResults = (SearchResults)session.getAttribute(AttributeNames.SEARCH_RESULTS);

        new Thread("Close_previous_searchresults_Thread") {
            @Override
            public void run() {
                if(!searchResults.equals(previousSearchResults) && previousSearchResults instanceof AutoCloseable) {
                    try{
                        ((AutoCloseable)previousSearchResults).close();
                    }catch(Exception e) {
                        logger.log(Level.WARNING, "Exception closing previous search results", e);
                    }
                }
            }
        }.start();

        logger.log(Level.FINE, "Search results size: {0}", searchResults.getSize());

        session.setAttribute(AttributeNames.SEARCH_RESULTS, searchResults);

        logger.log(Level.FINE, "Page offset: {0}", pageNumber);

        TaskResultModel resultModel = new TaskResultModel(webApp, 
                Arrays.asList(
                        webApp.getConfig().getString("serialColumnName"), 
//                        Task_.taskid.getName(), 
                        Doc_.subject.getName(), 
//                        Doc_.referencenumber.getName(),
                        Doc_.datesigned.getName(), Task_.reponsibility.getName(),
                        Task_.description.getName(), Task_.timeopened.getName(),
                        "Response 1", "Response 2", "Remarks"
                )
            ); 
        
        TableModel tableModel;
        if(searchResults.getPageCount() > pageNumber) {
            searchResults.setPageNumber(pageNumber);
            tableModel = new SearchResultsTableModel(webApp, searchResults, resultModel, pageNumber, numberOfPages);
        }else{
            tableModel = new SearchResultsTableModel(webApp, searchResults, resultModel);
        }

        session.setAttribute(AttributeNames.SEARCH_RESULTS_PAGE_TABLE_MODEL, tableModel);

        if(pageNumber < searchResults.getPageCount() - 1) {
            new Thread("Premptively_Load_SearchPage_"+(pageNumber+1)+"_Thread") {
                @Override
                public void run() {
                    try{
                        searchResults.getPage(pageNumber + 1);
                    }catch(RuntimeException e) {
                        Logger.getLogger(this.getClass().getName()).log(
                                Level.WARNING, "Unexpected exception", e);
                    }
                }
            }.start();
        }
        
        logger.finer("#update(..) Done updating searchresults session");
    }

    public <T> SelectDao<T> buildSelector(WebApp app, Class<T> resultType, HttpServletRequest request) {
        
        final String query = request == null ? null : request.getParameter("query");
        final Date deadlineFrom = null;
        final Date deadlineTo = null;
        final String who = request == null ? null : request.getParameter(Task_.reponsibility.getName());
        final Appointment appointment;
        if(this.isNullOrEmpty(who)) {
            appointment = null;
        }else{
            appointment = app.getJpaContext().getBuilderForSelect(Appointment.class)
                    .where(Appointment.class, Appointment_.appointment.getName(), who)
                    .getSingleResultAndClose();
        }
        final Boolean opened = null;
        final String sval = request == null ? null : request.getParameter("closed");
        final Boolean closed;
        if(!this.isNullOrEmpty(sval)) {
            closed = Boolean.parseBoolean(sval);
        }else{
            closed = null;
        }
        final Date from = null;
        final Date to = null;
        
        final boolean hasQuery = query != null && !query.isEmpty();
        final boolean joinDoc = hasQuery;
        final boolean joinTr = hasQuery || deadlineFrom != null || deadlineTo != null;
        
        final String q = !hasQuery ? null : '%'+query+'%';
        
        final BuilderForSelect<T> dao = new BuilderForSelectImpl(
                app.getJpaContext().getEntityManager(resultType), resultType);
        
        final CriteriaBuilder cb = dao.getCriteriaBuilder();
        
        final CriteriaQuery cq = dao.getCriteriaQuery();
        
        cq.distinct(true);
        
        final List<Predicate> likes = !hasQuery ? null : new ArrayList();
        
        final Root task = cq.from(Task.class); 
        if(likes != null) {
            likes.add(cb.like(task.get(Task_.description), q));
        }
        
        final Join<Task, Doc> taskDoc = !joinDoc ? null : task.join(Task_.doc); 
        if(likes != null && taskDoc != null) {
            likes.add(cb.like(taskDoc.get(Doc_.subject), q));
            likes.add(cb.like(taskDoc.get(Doc_.referencenumber), q));
        }
        
        // If you don't specify lef join here then some searches will return incorrect results
        final Join<Task, Taskresponse> taskTr = !joinTr ? null : task.join(Task_.taskresponseList, JoinType.LEFT);
        if(likes != null && taskTr != null) {
            likes.add(cb.like(taskTr.get(Taskresponse_.response), q));  
        }

        final List<Predicate> where = new ArrayList<>();
        
        if(likes != null) {
            where.add(cb.or(likes.toArray(new Predicate[0])));
        }
        
        if(appointment != null) {
            where.add(cb.equal(task.get(Task_.reponsibility), appointment));
        }
        if(opened != null) {
            if(opened) {
                where.add(cb.isNotNull(task.get(Task_.timeopened)));
            }else{
                where.add(cb.isNull(task.get(Task_.timeopened)));
            }
        }
        if(closed != null) {
            if(closed) {
                where.add(cb.isNotNull(task.get(Task_.timeclosed)));
            }else{
                where.add(cb.isNull(task.get(Task_.timeclosed)));
            }
        }
        if(from != null) {
            where.add(cb.greaterThanOrEqualTo(task.get(Task_.timeopened), from));
        }        
        if(to != null) {
            where.add(cb.lessThan(task.get(Task_.timeopened), to));
        } 
        
        if(deadlineFrom != null || deadlineTo != null) {
            where.add(cb.isNotNull(taskTr.get(Taskresponse_.deadline)));
        }
        
        if(deadlineFrom != null) {
            final Subquery<Integer> subquery = cq.subquery(Integer.class);
            final Root<Taskresponse> subqueryRoot = subquery.from(Taskresponse.class);
            subquery.select(subqueryRoot.get(Taskresponse_.taskresponseid));
            subquery.where(cb.equal(task, subqueryRoot.get(Taskresponse_.task)));
//            subquery.groupBy(subqueryRoot.get(Taskresponse_.taskresponseid)); 
            subquery.having(cb.greaterThanOrEqualTo(cb.greatest(subqueryRoot.<Date>get(Taskresponse_.deadline)), deadlineFrom));
            where.add(cb.exists(subquery));
        }        
        
        if(deadlineTo != null) {
            final Subquery<Integer> subquery = cq.subquery(Integer.class);
            final Root<Taskresponse> subqueryRoot = subquery.from(Taskresponse.class);
            subquery.select(subqueryRoot.get(Taskresponse_.taskresponseid));
            subquery.where(cb.equal(task, subqueryRoot.get(Taskresponse_.task)));
//            subquery.groupBy(subqueryRoot.get(Taskresponse_.taskresponseid)); 
            subquery.having(cb.lessThan(cb.greatest(subqueryRoot.<Date>get(Taskresponse_.deadline)), deadlineTo));
            where.add(cb.exists(subquery));
        } 

        if(!where.isEmpty()) {
            cq.where( cb.and(where.toArray(new Predicate[0])) );
        }

        cq.orderBy(cb.desc(task.get(Task_.taskid))); 

        return dao;
    }    
    private boolean isNullOrEmpty(Object text) {
        return text == null || "".equals(text);
    }
}
