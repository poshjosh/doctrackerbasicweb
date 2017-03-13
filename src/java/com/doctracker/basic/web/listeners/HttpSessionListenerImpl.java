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
package com.doctracker.basic.web.listeners;

import com.bc.jpa.search.SearchResults;
import com.doctracker.basic.web.AttributeNames;
import com.doctracker.basic.web.UserImpl;
import com.doctracker.basic.web.WebApp;
import com.doctracker.basic.web.pu.entities.Task;
import com.doctracker.basic.web.servlets.Search;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Web application lifecycle listener.
 *
 * @author Josh
 */
@WebListener()
public class HttpSessionListenerImpl implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        
        final HttpSession session = se.getSession();
        
        final WebApp webApp = (WebApp)session.getServletContext().getAttribute(AttributeNames.WEB_APP);
        
        session.setAttribute(AttributeNames.USER, new UserImpl(webApp, null, "guest", false));
        
        new Thread("Create_initial_searchresults_Thread") {
            @Override
            public void run() {
                try{

                    final Search search = new Search();

                    final SearchResults<Task> searchResults = search.search(webApp, null);

                    search.update(session, searchResults);

                }catch(RuntimeException e) {

                    Logger.getLogger(this.getClass().getName()).log(
                            Level.WARNING, "Unexpected exception", e);
                }        
            }
        }.start();
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        final HttpSession session = se.getSession();
        final Enumeration<String> en = session.getAttributeNames();
        while(en.hasMoreElements()) {
            final String name = en.nextElement();
            final Object value = session.getAttribute(name);
            if(value instanceof AutoCloseable) {
                try{
                    ((AutoCloseable)value).close();
                }catch(Exception e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Error closing: "+name, e);
                }
            }
        }
    }
}
