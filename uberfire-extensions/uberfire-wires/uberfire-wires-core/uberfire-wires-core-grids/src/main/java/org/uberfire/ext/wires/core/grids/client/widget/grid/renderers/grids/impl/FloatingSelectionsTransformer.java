/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl;

import java.util.List;

import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

/**
 * Helper functions to convert SelectedCells into SelectedRanges.
 */
public class FloatingSelectionsTransformer extends DefaultSelectionsTransformer {

    public FloatingSelectionsTransformer( final GridData model,
                                          final List<GridColumn<?>> columns ) {
        super( model,
               columns );
    }

    @Override
    protected int findUiColumnIndex( int modelColumnIndex ) {
        for ( int uiColumnIndex = 0; uiColumnIndex < columns.size(); uiColumnIndex++ ) {
            final GridColumn<?> uiColumn = columns.get( uiColumnIndex );
            if ( uiColumn.getIndex() == modelColumnIndex ) {
                return uiColumnIndex;
            }
        }
        throw new IllegalStateException( "Column was not found!" );
    }

}
