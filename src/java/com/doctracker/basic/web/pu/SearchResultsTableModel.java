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

import com.bc.jpa.search.SearchResults;
import com.doctracker.basic.web.WebApp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Feb 14, 2017 8:13:17 PM
 */
public class SearchResultsTableModel<T> extends EntityTableModel {
    
    private transient final Logger logger = Logger.getLogger(SearchResultsTableModel.class.getName());
    
    private final int serialColumnIndex;
    
    private final SearchResults<T> searchResults;

    public SearchResultsTableModel(WebApp app, SearchResults<T> searchResults, ResultModel<T> resultModel) {
        this(app, searchResults, resultModel, 0, searchResults.getPageCount());
    }
    
    public SearchResultsTableModel(WebApp app, SearchResults<T> searchResults, 
            ResultModel<T> resultModel, int pageNum) {
        this(app, searchResults, resultModel, pageNum, 1);
    }
    
    public SearchResultsTableModel(WebApp app, SearchResults<T> searchResults, 
            ResultModel<T> resultModel, int firstPage, int numberOfPages) {
        super(app, getResultsToDisplay(searchResults, firstPage, numberOfPages), resultModel);
        this.serialColumnIndex = resultModel.getSerialColumnIndex();
        this.searchResults = searchResults;
        if(logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "First page: {0}, display pages: {1}, total pages: {2}, display rows: {3}, total rows: {4}", 
                    new Object[]{firstPage, numberOfPages, searchResults.getPageCount(), this.getResultsToDisplay().size(), searchResults.getSize()});
        }
    }
    
    private static <T> List<T> getResultsToDisplay(SearchResults<T> searchResults, int firstPage, int numberOfPages) {
        final int END = firstPage + numberOfPages;
        final List<T> list = new ArrayList(searchResults.getPageSize() * numberOfPages);
        for(int pageNum=firstPage; pageNum < END; pageNum++) {
            list.addAll(searchResults.getPage(pageNum));
        }
        return Collections.unmodifiableList(list);
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try{
            final Object value;
            if(columnIndex == this.serialColumnIndex) {
                value = (this.searchResults.getPageNumber() * this.searchResults.getPageSize()) + (rowIndex + 1);
            }else{
                value = super.getValueAt(rowIndex, columnIndex);
            }
            return value;
        }catch(RuntimeException e) {
            log(e, "Error accessing value at ["+rowIndex+':'+columnIndex+']');
            return "Error";
        }
    }
}
