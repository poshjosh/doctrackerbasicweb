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

package com.doctracker.basic.web.beans;

import com.bc.jpa.EntityController;
import com.bc.jpa.JpaContext;
import com.bc.jpa.JpaMetaData;
import com.doctracker.basic.web.AttributeNames;
import com.doctracker.basic.web.WebApp;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 11, 2017 9:04:32 AM
 */
public class SelectorBean<E> implements Serializable {

    private boolean addParametersFromRequest;
    
    private int offset = -1;
    
    private int limit = -1;
    
    private Class<E> entityClass;
    
    private String columnName;
    
    private Object columnValue;
    
    private Map params;
    
    private JpaContext jpaContext;
    
    public void setRequest(HttpServletRequest request) {
        
        final WebApp webApp = (WebApp)request.getServletContext().getAttribute(AttributeNames.WEB_APP);
        
        jpaContext = Objects.requireNonNull(webApp.getJpaContext());

        params = new HashMap();

        if(this.isAddParametersFromRequest()) {
            
            Map map = request.getParameterMap();
            
            if(!map.isEmpty()) {
                params.putAll(map);
            }
            if(columnName != null) {
                params.put(columnName, columnValue);
            }
        }else{
            if(columnName != null) {
                params = Collections.singletonMap(columnName, columnValue);
            }
        }
    }
    
    public E getSingleResult() {
        return jpaContext.getBuilderForSelect(entityClass).where(entityClass, params).getSingleResultAndClose();
    }
    
    public List<E> getResultList() {
        return jpaContext.getBuilderForSelect(entityClass).where(entityClass, params).getResultsAndClose(offset, limit);
    }
    
    public List<Map<String, ?>> getResultListMappings() {
        EntityController<E, ?> ec = jpaContext.getEntityController(this.getEntityClass());
        return ec.toMapList(this.getResultList(), -1);
    }
    
    public String getTableName() {
        return jpaContext.getMetaData().getTableName(entityClass);
    }

    public void setTableName(String table) {
        final JpaMetaData metaData = jpaContext.getMetaData();
        this.entityClass = Objects.requireNonNull(metaData.findEntityClass(table));
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<E> entityClass) {
        this.entityClass = entityClass;
    }
    
    public boolean isAddParametersFromRequest() {
        return addParametersFromRequest;
    }

    public void setAddParametersFromRequest(boolean addParametersFromRequest) {
        this.addParametersFromRequest = addParametersFromRequest;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Object getColumnValue() {
        return columnValue;
    }

    public void setColumnValue(Object columnValue) {
        this.columnValue = columnValue;
    }
}
