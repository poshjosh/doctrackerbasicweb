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

package com.doctracker.basic.web;

import com.bc.config.Config;
import com.bc.config.ConfigService;
import com.bc.jpa.JpaContext;
import java.nio.file.Path;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 11, 2017 7:05:56 AM
 */
public class WebAppImpl implements WebApp {
    
    private final Path workingDir;

    private final ConfigService configService;
    
    private final Config config;

    private final JpaContext jpaContext;
    
    public WebAppImpl(Path workingDir, ConfigService configService, Config config, JpaContext jpaContext) {
        this.workingDir = workingDir;
        this.configService = configService;
        this.config = config;
        this.jpaContext = jpaContext;
    }
    
    @Override
    public void shutdown() {
        if(this.jpaContext.isOpen()) {
            this.jpaContext.close();
        }
    }

    @Override
    public Path getWorkingDir() {
        return workingDir;
    }

    @Override
    public ConfigService getConfigService() {
        return configService;
    }

    @Override
    public Config getConfig() {
        return config;
    }
    
    @Override
    public JpaContext getJpaContext() {
        return jpaContext;
    }
}
