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

import com.doctracker.basic.web.AttributeNames;
import com.doctracker.basic.web.User;
import com.doctracker.basic.web.UserImpl;
import com.doctracker.basic.web.WebApp;
import com.doctracker.basic.web.exceptions.LoginException;
import com.doctracker.basic.web.pu.entities.Appointment;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Josh
 */
@WebServlet(name = "Login", urlPatterns = {"/login", "/Login"})
public class Login extends BaseServlet {

    private transient static final Logger logger = Logger.getLogger(Login.class.getName());

    @Override
    public void doProcessRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
        response.setContentType("text/html;charset=UTF-8");
        
        final WebApp webApp = (WebApp)request.getServletContext().getAttribute(AttributeNames.WEB_APP);
        
        final HttpSession session = request.getSession();
        
        final String username = request.getParameter("username");
        if(username == null || username.isEmpty()) {
            throw new LoginException("Please enter a username");
        }
        
        final String password = request.getParameter("password");
        if(password == null || password.isEmpty()) {
            throw new LoginException("Please enter a password");
        }
        
        final String expectedPass = request.getServletContext().getInitParameter(username);
        if(expectedPass == null || !expectedPass.equals(password)) {
            throw new LoginException("You entered invalid login details");
        }

        final Appointment CAS = webApp.getJpaContext().getEntityManager(Appointment.class).find(Appointment.class, 1);
        
        final User user = new UserImpl(webApp, CAS, username, true);

        session.setAttribute(AttributeNames.USER, user);
        
        request.getRequestDispatcher(this.getSuccessPage(request, user)).forward(request, response);

        request.setAttribute(AttributeNames.USER_MESSAGE, "Login Successful");
    }
}
