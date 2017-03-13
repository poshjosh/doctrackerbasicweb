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

import com.doctracker.basic.web.JspPages;
import com.doctracker.basic.web.exceptions.LoginException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 11, 2017 6:51:39 AM
 */
public abstract class BaseServlet extends HttpServlet {
    
    public abstract void doProcessRequest(
            HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;
    
    public final void processRequest(
            HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, 
                "#processRequest(..) Request URI: {0}", request.getRequestURI());
        
        this.doProcessRequest(request, response);
    }

    public String getSuccessPage(HttpServletRequest request, Object result) {
        return JspPages.SEARCHRESULTS;
    }

    public String getErrorPage(HttpServletRequest request, Throwable error) {
        if(error instanceof LoginException) {
            return JspPages.LOGIN;
        }else if(this instanceof Search) {
            return JspPages.SEARCHRESULTS;
        }else{    
            return JspPages.ERROR;
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        super.getServletInfo();
        return "Short description";
    }// </editor-fold>

}
