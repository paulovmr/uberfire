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
package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.BoundingBox;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.HasDOMElementResources;
import org.uberfire.ext.wires.core.grids.client.widget.dom.multiple.HasMultipleDOMElementResources;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.SelectionsTransformer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.DefaultSelectionsTransformer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.FloatingSelectionsTransformer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

/**
 * The base of all GridWidgets.
 */
public class BaseGridWidget extends Group implements GridWidget,
                                                     NodeMouseClickHandler {

    protected final SelectionsTransformer bodyTransformer;
    protected final SelectionsTransformer floatingColumnsTransformer;

    protected final BaseGridRendererHelper rendererHelper;

    //These are final as a reference is held by the ISelectionsTransformers
    protected final List<GridColumn<?>> allColumns = new ArrayList<GridColumn<?>>();
    protected final List<GridColumn<?>> bodyColumns = new ArrayList<GridColumn<?>>();
    protected final List<GridColumn<?>> floatingColumns = new ArrayList<GridColumn<?>>();

    protected GridData model;
    protected GridRenderer renderer;
    protected Group header = null;
    protected Group floatingHeader = null;
    protected Group body = null;
    protected Group floatingBody = null;
    private Group selection = null;
    private boolean isSelected = false;

    public BaseGridWidget( final GridData model,
                           final GridSelectionManager selectionManager,
                           final GridPinnedModeManager pinnedModeManager,
                           final GridRenderer renderer ) {
        this.model = model;
        this.renderer = renderer;
        this.bodyTransformer = new DefaultSelectionsTransformer( model,
                                                                 bodyColumns );
        this.floatingColumnsTransformer = new FloatingSelectionsTransformer( model,
                                                                             floatingColumns );
        this.rendererHelper = new BaseGridRendererHelper( model,
                                                          this );

        //Click handlers
        addNodeMouseClickHandler( new BaseGridWidgetMouseClickHandler( this,
                                                                       selectionManager,
                                                                       renderer ) );
        addNodeMouseClickHandler( new GridCellSelectorMouseClickHandler( this,
                                                                         selectionManager,
                                                                         renderer ) );
        addNodeMouseDoubleClickHandler( new BaseGridWidgetMouseDoubleClickHandler( this,
                                                                                   selectionManager,
                                                                                   pinnedModeManager,
                                                                                   renderer ) );
    }

    @Override
    public GridData getModel() {
        return model;
    }

    @Override
    public GridRenderer getRenderer() {
        return this.renderer;
    }

    @Override
    public void setRenderer( final GridRenderer renderer ) {
        this.renderer = renderer;
    }

    @Override
    public BaseGridRendererHelper getRendererHelper() {
        return rendererHelper;
    }

    @Override
    public Group getBody() {
        return body;
    }

    @Override
    public Group getHeader() {
        return header;
    }

    @Override
    public double getWidth() {
        return rendererHelper.getWidth( model.getColumns() );
    }

    @Override
    public double getHeight() {
        double height = renderer.getHeaderHeight();
        height = height + rendererHelper.getRowOffset( model.getRowCount() );
        return height;
    }

    @Override
    public void select() {
        isSelected = true;
        assertSelectionWidget();
        add( selection );
    }

    @Override
    public void deselect() {
        isSelected = false;
        assertSelectionWidget();
        remove( selection );
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    private void assertSelectionWidget() {
        this.selection = renderer.renderSelector( getWidth(),
                                                  getHeight() );
    }

    /**
     * Intercept the normal Lienzo draw mechanism to calculate and hence draw only the visible
     * columns and rows for the Grid; being those within the bounds of the GridLayer. At the
     * start of this draw method all visible columns are given an opportunity to initialise
     * any resources they require (e.g. DOMElements). At the end of this method all visible
     * columns are given an opportunity to release any unused resources (e.g. DOMElements).
     * If a column is not visible it is given an opportunity to destroy all resources.
     * @param context
     * @param alpha
     */
    @Override
    protected void drawWithoutTransforms( Context2D context,
                                          double alpha,
                                          BoundingBox bb ) {
        body = null;
        header = null;
        floatingBody = null;
        floatingHeader = null;
        if ( ( context.isSelection() ) && ( false == isListening() ) ) {
            return;
        }
        alpha = alpha * getAttributes().getAlpha();

        if ( alpha <= 0 ) {
            return;
        }

        if ( model.getColumns().isEmpty() ) {
            return;
        }

        //Clear existing content
        this.removeAll();
        this.allColumns.clear();
        this.bodyColumns.clear();
        this.floatingColumns.clear();

        //If there's no RenderingInformation the GridWidget is not visible
        final BaseGridRendererHelper.RenderingInformation renderingInformation = rendererHelper.getRenderingInformation();
        if ( renderingInformation == null ) {
            for ( GridColumn<?> column : model.getColumns() ) {
                if ( column.getColumnRenderer() instanceof HasDOMElementResources ) {
                    ( (HasDOMElementResources) column.getColumnRenderer() ).destroyResources();
                }
            }
            return;
        }

        final BaseGridRendererHelper.RenderingBlockInformation bodyBlockInformation = renderingInformation.getBodyBlockInformation();
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final List<GridColumn<?>> allColumns = renderingInformation.getAllColumns();
        final List<GridColumn<?>> bodyColumns = bodyBlockInformation.getColumns();
        final List<GridColumn<?>> floatingColumns = floatingBlockInformation.getColumns();

        this.allColumns.addAll( allColumns );
        this.bodyColumns.addAll( bodyColumns );
        this.floatingColumns.addAll( floatingColumns );

        //Draw if required
        if ( this.bodyColumns.size() > 0 ) {
            drawHeader( renderingInformation,
                        context.isSelection() );

            if ( model.getRowCount() > 0 ) {
                drawBody( renderingInformation,
                          context.isSelection() );
            }

            if ( header != null ) {
                header.moveToTop();
            }
            if ( floatingHeader != null ) {
                floatingHeader.moveToTop();
            }

            //Include selection indicator if required
            if ( isSelected ) {
                select();
            }

        } else {
            if ( !context.isSelection() ) {
                for ( GridColumn<?> column : model.getColumns() ) {
                    if ( column.getColumnRenderer() instanceof HasDOMElementResources ) {
                        ( (HasDOMElementResources) column.getColumnRenderer() ).destroyResources();
                    }
                }
            }
        }

        //Then render to the canvas
        super.drawWithoutTransforms( context,
                                     alpha,
                                     bb );
    }

    @Override
    public Group setVisible( final boolean visible ) {
        if ( !visible ) {
            for ( GridColumn<?> gc : getModel().getColumns() ) {
                if ( gc instanceof HasMultipleDOMElementResources ) {
                    ( (HasMultipleDOMElementResources) gc ).destroyResources();
                }
            }
        }
        return super.setVisible( visible );
    }

    protected void drawHeader( final BaseGridRendererHelper.RenderingInformation renderingInformation,
                               final boolean isSelectionLayer ) {
        final Bounds bounds = renderingInformation.getBounds();
        final List<GridColumn<?>> allColumns = renderingInformation.getAllColumns();
        final BaseGridRendererHelper.RenderingBlockInformation bodyBlockInformation = renderingInformation.getBodyBlockInformation();
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final List<GridColumn<?>> bodyColumns = bodyBlockInformation.getColumns();
        final List<GridColumn<?>> floatingColumns = floatingBlockInformation.getColumns();

        final double headerX = bodyBlockInformation.getX();
        final double headerY = bodyBlockInformation.getHeaderY();
        final double floatingHeaderX = floatingBlockInformation.getX();
        final double floatingHeaderY = floatingBlockInformation.getHeaderY();

        final double vpY = bounds.getY();
        final double vpHeight = bounds.getHeight();

        boolean addFixedHeader = false;
        boolean addFloatingHeader = false;
        if ( isSelected ) {
            if ( getY() < vpY ) {
                //GridWidget is selected and clipped at the top
                if ( getY() + getHeight() > vpY + renderer.getHeaderHeight() ) {
                    //GridWidget is taller than the Header; add fixed header
                    addFixedHeader = true;

                } else {
                    //GridWidget is shorter than the Header; add floating header
                    addFloatingHeader = true;
                }

            } else if ( getY() <= vpY + vpHeight ) {
                //GridWidget is selected and not clipped at the top; add floating header
                addFloatingHeader = true;
            }

        } else if ( getY() + renderer.getHeaderHeight() > vpY && getY() < vpY + vpHeight ) {
            //GridWidget is not selected; add floating header
            addFloatingHeader = true;
        }

        //Add Header, if applicable
        if ( addFixedHeader || addFloatingHeader ) {
            header = renderGridHeaderWidget( allColumns,
                                             bodyColumns,
                                             isSelectionLayer,
                                             renderingInformation );
            header.setX( headerX );
            if ( addFixedHeader ) {
                header.setY( headerY );
            }
            add( header );

            //Draw floating header columns if required
            if ( floatingColumns.size() > 0 ) {
                floatingHeader = renderGridHeaderWidget( floatingColumns,
                                                         floatingColumns,
                                                         isSelectionLayer,
                                                         renderingInformation );
                floatingHeader.setX( floatingHeaderX );
                floatingHeader.setY( floatingHeaderY );
                add( floatingHeader );
            }
        }
    }

    protected void drawBody( final BaseGridRendererHelper.RenderingInformation renderingInformation,
                             final boolean isSelectionLayer ) {
        final BaseGridRendererHelper.RenderingBlockInformation bodyBlockInformation = renderingInformation.getBodyBlockInformation();
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final List<GridColumn<?>> bodyColumns = bodyBlockInformation.getColumns();
        final List<GridColumn<?>> floatingColumns = floatingBlockInformation.getColumns();

        final double bodyX = bodyBlockInformation.getX();
        final double bodyY = bodyBlockInformation.getBodyY();
        final double floatingBodyX = floatingBlockInformation.getX();
        final double floatingBodyY = floatingBlockInformation.getBodyY();

        final int minVisibleRowIndex = renderingInformation.getMinVisibleRowIndex();
        final int maxVisibleRowIndex = renderingInformation.getMaxVisibleRowIndex();

        //Signal columns to attach or detach rendering support
        if ( !isSelectionLayer ) {
            for ( GridColumn<?> column : model.getColumns() ) {
                if ( bodyColumns.contains( column ) || floatingColumns.contains( column ) ) {
                    if ( column.getColumnRenderer() instanceof HasMultipleDOMElementResources ) {
                        ( (HasMultipleDOMElementResources) column.getColumnRenderer() ).initialiseResources();
                    }
                } else if ( column instanceof HasDOMElementResources ) {
                    ( (HasDOMElementResources) column.getColumnRenderer() ).destroyResources();
                }
            }
        }

        body = renderGridBodyWidget( bodyColumns,
                                     bodyBlockInformation.getX(),
                                     minVisibleRowIndex,
                                     maxVisibleRowIndex,
                                     isSelectionLayer,
                                     bodyTransformer,
                                     renderingInformation );
        body.setX( bodyX );
        body.setY( bodyY );
        add( body );

        //Include selected ranges of cells
        if ( !isSelectionLayer ) {
            body.add( renderSelectedRanges( bodyColumns,
                                            bodyBlockInformation.getX(),
                                            minVisibleRowIndex,
                                            maxVisibleRowIndex,
                                            bodyTransformer,
                                            renderingInformation ) );
        }

        //Render floating columns
        if ( floatingColumns.size() > 0 ) {
            floatingBody = renderGridBodyWidget( floatingColumns,
                                                 floatingBlockInformation.getX(),
                                                 minVisibleRowIndex,
                                                 maxVisibleRowIndex,
                                                 isSelectionLayer,
                                                 floatingColumnsTransformer,
                                                 renderingInformation );
            floatingBody.setX( floatingBodyX );
            floatingBody.setY( floatingBodyY );
            add( floatingBody );

            //Include selected ranges of cells
            if ( !isSelectionLayer ) {
                floatingBody.add( renderSelectedRanges( floatingColumns,
                                                        floatingBlockInformation.getX(),
                                                        minVisibleRowIndex,
                                                        maxVisibleRowIndex,
                                                        floatingColumnsTransformer,
                                                        renderingInformation ) );
            }
        }

        //Signal columns to free any unused resources
        if ( !isSelectionLayer ) {
            for ( GridColumn<?> column : bodyColumns ) {
                if ( column.getColumnRenderer() instanceof HasMultipleDOMElementResources ) {
                    ( (HasMultipleDOMElementResources) column.getColumnRenderer() ).freeUnusedResources();
                }
            }
            for ( GridColumn<?> column : floatingColumns ) {
                if ( column.getColumnRenderer() instanceof HasMultipleDOMElementResources ) {
                    ( (HasMultipleDOMElementResources) column.getColumnRenderer() ).freeUnusedResources();
                }
            }
        }
    }

    /**
     * Render the Widget's Header and append to this Group.
     * @param allColumns All columns in the model.
     * @param blockColumns The columns to render for a block.
     * @param isSelectionLayer Is the SelectionLayer being rendered.
     */
    protected Group renderGridHeaderWidget( final List<GridColumn<?>> allColumns,
                                            final List<GridColumn<?>> blockColumns,
                                            final boolean isSelectionLayer,
                                            final BaseGridRendererHelper.RenderingInformation renderingInformation ) {
        final GridHeaderRenderContext context = new GridHeaderRenderContext( allColumns,
                                                                             blockColumns,
                                                                             isSelectionLayer );
        final Group g = renderer.renderHeader( model,
                                               context,
                                               rendererHelper,
                                               renderingInformation );
        return g;
    }

    /**
     * Render the Widget's Body and append to this Group.
     * @param blockColumns The columns to render.
     * @param absoluteColumnOffsetX Absolute offset from Grid's X co-ordinate to render first column in block.
     * @param minVisibleRowIndex The index of the first visible row.
     * @param maxVisibleRowIndex The index of the last visible row.
     * @param isSelectionLayer Is the SelectionLayer being rendered.
     * @param transformer SelectionTransformer in operation.
     */
    protected Group renderGridBodyWidget( final List<GridColumn<?>> blockColumns,
                                          final double absoluteColumnOffsetX,
                                          final int minVisibleRowIndex,
                                          final int maxVisibleRowIndex,
                                          final boolean isSelectionLayer,
                                          final SelectionsTransformer transformer,
                                          final BaseGridRendererHelper.RenderingInformation renderingInformation ) {
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final double floatingX = floatingBlockInformation.getX();
        final double floatingWidth = floatingBlockInformation.getWidth();

        final double clipMinY = getY() + ( header == null ? 0.0 : header.getY() + getRenderer().getHeaderHeight() );
        final double clipMinX = getX() + floatingX + floatingWidth;
        final GridBodyRenderContext context = new GridBodyRenderContext( getX(),
                                                                         getY(),
                                                                         absoluteColumnOffsetX,
                                                                         clipMinY,
                                                                         clipMinX,
                                                                         minVisibleRowIndex,
                                                                         maxVisibleRowIndex,
                                                                         blockColumns,
                                                                         isSelectionLayer,
                                                                         getViewport().getTransform(),
                                                                         renderer,
                                                                         transformer );
        final Group g = renderer.renderBody( model,
                                             context,
                                             rendererHelper,
                                             renderingInformation );
        return g;
    }

    /**
     * Render the selected ranges and append to the Body Group.
     * @param blockColumns The columns to render.
     * @param absoluteColumnOffsetX Absolute offset from Grid's X co-ordinate to render first column in block.
     * @param minVisibleRowIndex The index of the first visible row.
     * @param maxVisibleRowIndex The index of the last visible row.
     * @param transformer SelectionTransformer in operation.
     * @return
     */
    protected Group renderSelectedRanges( final List<GridColumn<?>> blockColumns,
                                          final double absoluteColumnOffsetX,
                                          final int minVisibleRowIndex,
                                          final int maxVisibleRowIndex,
                                          final SelectionsTransformer transformer,
                                          final BaseGridRendererHelper.RenderingInformation renderingInformation ) {
        final BaseGridRendererHelper.RenderingBlockInformation floatingBlockInformation = renderingInformation.getFloatingBlockInformation();
        final double floatingX = floatingBlockInformation.getX();
        final double floatingWidth = floatingBlockInformation.getWidth();

        final double clipMinY = getY() + ( header == null ? 0.0 : header.getY() + getRenderer().getHeaderHeight() );
        final double clipMinX = getX() + floatingX + floatingWidth;
        final GridBodyRenderContext context = new GridBodyRenderContext( getX(),
                                                                         getY(),
                                                                         absoluteColumnOffsetX,
                                                                         clipMinY,
                                                                         clipMinX,
                                                                         minVisibleRowIndex,
                                                                         maxVisibleRowIndex,
                                                                         blockColumns,
                                                                         false,
                                                                         getViewport().getTransform(),
                                                                         renderer,
                                                                         transformer );
        final Group g = renderer.renderSelectedCells( model,
                                                      context,
                                                      rendererHelper );
        return g;
    }

    @Override
    public void onNodeMouseClick( final NodeMouseClickEvent event ) {
        fireEvent( event );
    }

    @Override
    public boolean onGroupingToggle( final double cellX,
                                     final double cellY,
                                     final double cellWidth,
                                     final double cellHeight ) {
        return renderer.onGroupingToggle( cellX,
                                          cellY,
                                          cellWidth,
                                          cellHeight );
    }

}
