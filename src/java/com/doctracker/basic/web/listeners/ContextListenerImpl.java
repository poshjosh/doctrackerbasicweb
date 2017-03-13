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

import com.bc.config.CompositeConfig;
import com.bc.config.Config;
import com.bc.config.ConfigService;
import com.bc.config.SimpleConfigService;
import com.bc.jpa.JpaContext;
import com.bc.jpa.JpaContextImpl;
import com.doctracker.basic.web.AttributeNames;
import com.doctracker.basic.web.WebApp;
import com.doctracker.basic.web.WebAppImpl;
import com.doctracker.basic.web.pu.entities.Appointment;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Web application lifecycle listener.
 *
 * @author Josh
 */
@WebListener()
public class ContextListenerImpl implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        try{
            
            final ServletContext context = sce.getServletContext();
            
            final Logger logger = Logger.getLogger(this.getClass().getName());
            
            final boolean productionMode = Boolean.parseBoolean(context.getInitParameter(AttributeNames.PRODUCTION_MODE));
            
            logger.log(Level.INFO, "Production mode: {0}", productionMode);
            
            context.setAttribute(AttributeNames.PRODUCTION_MODE, productionMode);
            
            final Path workingDirPath = Paths.get(System.getProperty("user.home"), "dtbweblocal");
            final File dir = workingDirPath.toFile();
            if(!dir.exists()) {
                dir.mkdirs();
            }
            final String workingDir = workingDirPath.toString();
            
            final String defaultPropsFile = "META-INF/properties/appdefaults.properties";
            final String propsFile;
            if(productionMode) {
                propsFile = Paths.get(workingDir, "app.properties").toString();
                File file = new File(propsFile);
                if(!file.exists()) {
                    file.createNewFile();
                }
            }else{
                propsFile = "META-INF/properties/app_devmode.properties";
            }
            
            final ConfigService configService = new SimpleConfigService(
                    defaultPropsFile, propsFile){
                @Override
                public URL getResource(String resourcePath) {
                    try{
                        return context.getResource(resourcePath);
                    }catch(MalformedURLException e) { throw new RuntimeException(e); }
                }
                @Override
                public InputStream getResourceAsStream(String path) {
                    return context.getResourceAsStream(path);
                }
            };

            final Config config = new CompositeConfig(configService);
            
            final String persistenceFile = config.getString("persistenceFile");
            logger.log(Level.INFO, "Persistence file: {0}", persistenceFile);
            
            final URI persistenceURI = context.getResource(persistenceFile).toURI();
            logger.log(Level.INFO, "Persistence URI: {0}", persistenceURI);
           
            final JpaContext jpaContext = new JpaContextImpl(persistenceURI, null);
            
            final WebApp webApp = new WebAppImpl(workingDirPath, configService, config, jpaContext);

            context.setAttribute(AttributeNames.WEB_APP, webApp);
            
            // This will do some java persistence initializations
            //
            jpaContext.getBuilderForSelect(Appointment.class).getResultsAndClose();
            
        }catch(URISyntaxException | IOException e) {
            final String msg = "Unexpected exception during "+this.getClass().getName()+"#contextInitialized";
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) { 
        
        final WebApp webApp = (WebApp)sce.getServletContext().getAttribute(AttributeNames.WEB_APP);
        
        if(webApp != null) {
            webApp.shutdown();
        }
    }
}
