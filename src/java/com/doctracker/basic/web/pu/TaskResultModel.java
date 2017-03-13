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

package com.doctracker.basic.web.pu;

import com.bc.config.Config;
import com.doctracker.basic.web.WebApp;
import com.doctracker.basic.web.pu.entities.Appointment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import com.doctracker.basic.web.pu.entities.Doc_;
import com.doctracker.basic.web.pu.entities.Task;
import com.doctracker.basic.web.pu.entities.Task_;
import com.doctracker.basic.web.pu.entities.Taskresponse;
import java.io.Serializable;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 21, 2017 10:47:02 PM
 */
public class TaskResultModel implements ResultModel<Task>, Serializable {
    
    private transient static final Logger logger = Logger.getLogger(TaskResultModel.class.getName());

    private final List<String> columnNames;
    
    private final List<String> columnLabels;
    
    private final int serialColumnIndex;
    
    private final int responseColumnCount;
    
    public TaskResultModel(WebApp webApp, List<String> columnNames) {
        this.columnNames = Collections.unmodifiableList(columnNames);
        this.columnLabels = this.getColumnLabels(webApp, columnNames);
        this.serialColumnIndex = TaskResultModel.this.getSerialColumnIndex(columnNames);
        this.responseColumnCount = 2;
    }
    
    private int getSerialColumnIndex(List<String> colNames) {
        final String serialColumnName = this.getSerialColumnName();
        return serialColumnName == null ? - 1 : colNames.indexOf(serialColumnName);
    }
    
    private List<String> getColumnLabels(WebApp app, List<String> colNames) {
        final List<String> labels = new ArrayList<>();
        final Config config = app.getConfig();
        colNames.stream().forEach((colName) -> {
            final String label = config.getString("columnLabel." + Task.class.getSimpleName() + '.' + colName);
            labels.add(label == null ? colName : label);
        });
        return labels;
    }

    @Override
    public Object get(Task entity, int rowIndex, int columnIndex) {
        final String columnName = this.getColumnName(columnIndex);
        final Object value = this.get(entity, rowIndex, columnName);
        return value;
    }
    
    @Override
    public Object get(Task entity, int rowIndex, String columnName) {
        final Object value;
        if(columnName.equals(this.getSerialColumnName())) {
            value = rowIndex + 1;
        }else if(Task_.taskid.getName().equals(columnName)) {
            value = entity.getTaskid();
        }else if(Doc_.subject.getName().equals(columnName)) {
            value = entity.getDoc().getSubject();
        }else if(Doc_.referencenumber.getName().equals(columnName)) {
            value = entity.getDoc().getReferencenumber();
        }else if(Doc_.datesigned.getName().equals(columnName)) {
            value = entity.getDoc().getDatesigned();
        }else if(Task_.reponsibility.getName().equals(columnName)) {
            value = entity.getReponsibility().getAbbreviation();
        }else if(Task_.description.getName().equals(columnName)) {
            value = entity.getDescription();
        }else if(Task_.timeopened.getName().equals(columnName)) {
            value = entity.getTimeopened();
        }else if("Response 1".equals(columnName)) {
            final Taskresponse res = this.getTaskresponse(entity, columnName);
            value = res == null ? null : res.getResponse();
        }else if("Response 2".equals(columnName)) {
            final Taskresponse res = this.getTaskresponse(entity, columnName);
            value = res == null ? null : res.getResponse();
        }else if("Remarks".equals(columnName)) {
            final Taskresponse res = this.getRemark(entity, columnName);
            value = res == null ? null : res.getResponse();
        }else{
            throw new IllegalArgumentException("Unexpected column name: "+columnName);
        }
        return value;
    }
    
    @Override
    public Object set(Task task, int rowIndex, int columnIndex, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object set(Task task, int rowIndex, String columnName, Object value) {
        throw new UnsupportedOperationException();
    }
    
    public Taskresponse getRemark(Task task, String columnName) {
        return this.getTaskresponse(task, task.getAuthor(), columnName);
    }
    
    public Taskresponse getTaskresponse(Task task, String columnName) {
        return this.getTaskresponse(task, task.getReponsibility(), columnName);
    }
    
    public Taskresponse getTaskresponse(Task task, Appointment appt, String columnName) {
        int index = -1;
        switch(columnName) {
            case "Response 1": 
                index = 1; break;
            case "Response 2": 
            case "Remarks":
                index = 0; break;
            default: throw new UnsupportedOperationException("Unexpected response column name: "+columnName); 
        }
        Taskresponse res = this.getFromEnd(appt, task.getTaskresponseList(), columnName, index);
        return res;
    }
    
    private Taskresponse getFromEnd(Appointment appt, List<Taskresponse> list, String columnName, int index) {
        Taskresponse output;
        if(list == null || list.isEmpty() || list.size() < index) {
            output = null;
        }else{
            List<Taskresponse> filtered = new ArrayList(list.size());
            for(Taskresponse tr : list) {
                if(appt == null || tr.getAuthor().equals(appt)) {
                    filtered.add(tr);
                }
            }
            list = null;
            if(filtered.size() <= index) {
                output = null;
            }else{
                Collections.reverse(filtered);
                output = filtered.get(index);
            }
        }
        return output;
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        final Class value;
        final String columnName = this.getColumnName(columnIndex);
        if(columnName.equals(this.getSerialColumnName())) {
            value = Integer.class;
        }else if(Task_.taskid.getName().equals(columnName)) {
            value = Integer.class;
        }else if(Doc_.subject.getName().equals(columnName)) {
            value = String.class;
        }else if(Doc_.referencenumber.getName().equals(columnName)) {
            value = String.class;
        }else if(Doc_.datesigned.getName().equals(columnName)) {
            value = Date.class;
        }else if(Task_.reponsibility.getName().equals(columnName)) {
            value = String.class;
        }else if(Task_.description.getName().equals(columnName)) {
            value = String.class;
        }else if(Task_.timeopened.getName().equals(columnName)) {
            value = Date.class;
        }else if("Response 1".equals(columnName)) {
            value = String.class;
        }else if("Response 2".equals(columnName)) {
            value = String.class;
        }else if("Remarks".equals(columnName)) {
            value = String.class;
        }else{
            throw new IllegalArgumentException("Unexpected column name: "+columnName);
        }
        return value;
    }
  
    private String getSerialColumnName() {
        return this.serialColumnIndex == -1 ? null : this.columnNames.get(this.serialColumnIndex);
    }

    @Override
    public int getSerialColumnIndex() {
        return this.serialColumnIndex;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames.get(columnIndex);
    }

    @Override
    public String getColumnLabel(int columnIndex) {
        return columnLabels.get(columnIndex);
    }

    @Override
    public Set<String> getColumnNames() {
        return new LinkedHashSet(columnNames);
    }

    @Override
    public Set<String> getColumnLabels() {
        return new LinkedHashSet(columnLabels);
    }
}
