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
package com.doctracker.basic.web.filters;

import com.doctracker.basic.web.AttributeNames;
import com.doctracker.basic.web.JspPages;
import com.doctracker.basic.web.User;
import com.doctracker.basic.web.exceptions.LoginException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Josh
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = {'/'+JspPages.SEARCHRESULTS, "/search", "/Search"})
public class LoginFilter extends BaseFilter {
    
    public LoginFilter() { }    

    @Override
    protected void doBeforeProcessing(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        final HttpSession session = ((HttpServletRequest)request).getSession();
        final User user = (User)session.getAttribute(AttributeNames.USER);
        if(!user.isLoggedIn()) {
            throw new LoginException("Login required");
        }
    }
}
