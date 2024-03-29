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

import java.io.Serializable;
import javax.swing.table.TableModel;

/**
 * @author Chinomso Bassey Ikwuagwu on Mar 7, 2017 2:48:14 PM
 */
public class TableModelBean implements Serializable {

    private TableModel tableModel;
    
    private int rowIndex;
    
    private int columnIndex;

    public TableModelBean() { }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public TableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(TableModel tableModel) {
        this.tableModel = tableModel;
    }

    public int getRowCount() {
        return tableModel.getRowCount();
    }

    public int getColumnCount() {
        return tableModel.getColumnCount();
    }

    public String getColumnName() {
        return tableModel.getColumnName(columnIndex);
    }

    public Class<?> getColumnClass() {
        return tableModel.getColumnClass(columnIndex);
    }

    public boolean isCellEditable() {
        return tableModel.isCellEditable(rowIndex, columnIndex);
    }

    public Object getValueAt() {
        return tableModel.getValueAt(rowIndex, columnIndex);
    }

    public void setValueAt(Object aValue) {
        tableModel.setValueAt(aValue, rowIndex, columnIndex);
    }
}
