/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as 
published by the Free Software Foundation, either version 3 of 
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public 
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/


package org.psygrid.collection.entry.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.tools.SPInterface;
import org.apache.commons.discovery.tools.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.event.PostSectionBuiltEvent;
import org.psygrid.collection.entry.event.PostSectionBuiltListener;
import org.psygrid.collection.entry.event.SectionBuiltEvent;
import org.psygrid.collection.entry.event.SectionBuiltListener;
import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.model.CompositePresModel;
import org.psygrid.collection.entry.model.SectionPresModel;
import org.psygrid.collection.entry.renderer.BasicRenderer;
import org.psygrid.collection.entry.renderer.CompositeRenderer;
import org.psygrid.collection.entry.renderer.Renderer;
import org.psygrid.collection.entry.renderer.RendererHandler;
import org.psygrid.collection.entry.renderer.RendererSPI;
import org.psygrid.data.model.hibernate.CompositeResponse;
import org.psygrid.data.model.hibernate.DocumentInstance;
import org.psygrid.data.model.hibernate.Entry;
import org.psygrid.data.model.hibernate.StandardCode;

import com.jgoodies.forms.builder.DefaultFormBuilder;

/**
 * Responsible for managing the building process. This involves managing the
 * renderer handlers and choosing the correct <code>BuilderSPI</code> to which
 * the building of a specific model object is delegated.<p>
 * 
 * Building in this context refers to the combination of laying out the visual
 * components and rendering them.
 * 
 * @author Ismael Juma (ismael@juma.me.uk)
 * @see RendererHandler
 * @see BuilderSPI
 * @see BuilderHandler#build(DefaultFormBuilder, org.psygrid.data.model.hibernate.Entry, org.psygrid.data.model.hibernate.Entry,
 * org.psygrid.data.model.hibernate.CompositeResponse, boolean, int)
 */
public final class BuilderHandler   {

    private final List<BuilderSPI> builders;

    private static final Log LOG = LogFactory.getLog(BuilderHandler.class);

    private final List<RendererHandler> rendererHandlers;
    
    private final EventListenerList listenerList = new EventListenerList();
    
    private int rendererHandlerIndex = -1;

    private final List<StandardCode> standardCodes;

    private final DocumentInstance docOccurrenceInstance;

    private final List<SectionPresModel> sectionPresModels;
    
    /**
     * Creates an instance of the object.
     * 
     * @param docOccurrenceInstance Document occurrence instance that all
     * elements passed to <code>build()</code> belong to.
     * @param standardCodes The standard codes for the repository that the
     * document belongs to.
     * @param sectionPresModels All the existing section presentation models
     * for <code>docOccurrenceInstance</code>.
     */
    public BuilderHandler(DocumentInstance docOccurrenceInstance,
            List<StandardCode> standardCodes, 
            List<SectionPresModel> sectionPresModels) {
        this.docOccurrenceInstance = docOccurrenceInstance;
        rendererHandlers = new ArrayList<RendererHandler>(2);
        this.standardCodes = standardCodes;
        this.sectionPresModels = sectionPresModels;
        builders = new ArrayList<BuilderSPI>();
        LOG.info("Loading all builders"); //$NON-NLS-1$
        SPInterface spi = new SPInterface(BuilderSPI.class);
        ClassLoaders loaders = new ClassLoaders();
        loaders.put(Thread.currentThread().getContextClassLoader());
        Enumeration<?> spe = Service.providers(spi, loaders);
        while (spe.hasMoreElements()) {
            BuilderSPI builder = (BuilderSPI) spe.nextElement();
            builders.add(builder);
            LOG.debug("\t" + builder.getClass().toString()); //$NON-NLS-1$
        }
    }

    /**
     * Finds the BuilderSPI that can handle <code>model</code> and 
     * <code>modelParent</code> and delegates the actual building to the
     * <code>BuilderSPI</code>. This is explained in more detail in 
     * {@link BuilderSPI}.
     * 
     * @param builder The builder to add components to.
     * @param model The model that is to be rendered.
     * @param parent The parent of the model that is to be rendered or null if
     * it has no parent.
     * @param parentResponse The response of the parent of the model or null if
     * the model has no parent or if the parent has no response associated with
     * it. 
     * @param copy True if the element is to be built outside the main frame.
     * This is useful in cases like the EditDialog where certain restrictions
     * should not be applied. This is explained in more detail in 
     * <code>RendererSPI</code>.
     * @param rowIndex The row index the element belongs to. This is 0 if the
     * element has no parent. If the element has a parent, then it should specify
     * in what position it belongs in the parent.
     * 
     * @see BuilderSPI
     * @see RendererSPI
     */
    public final void build(DefaultFormBuilder builder, Entry model,
            Entry parent, CompositeResponse parentResponse, boolean copy,
            int rowIndex) {
        BuilderData data = new BuilderData(getCurrentRendererHandler(), 
                builder,model, parentResponse, copy, rowIndex);
        for (BuilderSPI bspi : builders) {
            if (bspi.canHandle(model, parent)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Building with " + bspi.getClass()); //$NON-NLS-1$
                }
                bspi.build(data);
                return;
            }
        }
    }
    
    /**
     * Adds <code>rendererHandler</code> to the end of the list of renderer
     * handlers.
     * 
     * @param rendererHandler to add to the list of renderer handlers.
     */
    public final void addRendererHandler(RendererHandler rendererHandler) {
        rendererHandlers.add(rendererHandler);
        rendererHandler.setBuilderHandler(this);
        rendererHandlerIndex++;
    }
    
    /**
     * Returns the renderer handler for the given index.
     * @param index A number higher than 0 and smaller than the number of
     * renderer handlers.
     */
    public final RendererHandler getRendererHandler(int index) {
        return rendererHandlers.get(index);
    }
    
    /**
     * @return an unmodifiable list containing all renderer handlers in this
     * object.
     */
    public List<RendererHandler> getRendererHandlers()  {
        return Collections.unmodifiableList(rendererHandlers);
    }
    
    /**
     * @return the index of the currently active renderer.
     */
    public final int getRendererHandlerIndex() {
        return rendererHandlerIndex;
    }
    
    /**
     * @return the currently active renderer.
     */
    public final RendererHandler getCurrentRendererHandler() {
        if (rendererHandlerIndex == -1) {
            return null;
        }
        return rendererHandlers.get(rendererHandlerIndex);
    }
    
    public final RendererHandler getRendererHandler(SectionPresModel presModel) {
        for (RendererHandler rendererHandler : rendererHandlers) {
            if (rendererHandler.getSectionPresModel().equals(presModel)) {
                return rendererHandler;
            }
        }
        return null;
    }
    
    /**
     * Adds <code>listener</code> to a list of listeners that are invoked when 
     * the building of a section has reached the end.
     */
    public final void addSectionBuiltListener(SectionBuiltListener listener) {
        listenerList.add(SectionBuiltListener.class, listener);
    }
    
    /**
     * Removes <code>listener</code> from the list of listeners that are
     * invoked when the building of a section has reached the end.
     */
    public final void removeSectionBuiltListener(SectionBuiltListener listener) {
        listenerList.remove(SectionBuiltListener.class, listener);
    }
    
    /**
     * Adds <code>listener</code> to a list of listeners that are invoked after
     * all <code>SectionBuiltListener</code>s are invoked.
     */
    public final void addPostSectionBuiltListener(PostSectionBuiltListener listener) {
        listenerList.add(PostSectionBuiltListener.class, listener);
    }
    
    /**
     * Removes <code>listener</code> from the list of listeners that are
     * invoked after all <code>SectionBuiltListener</code>s are invoked.
     */
    public final void removePostSectionBuiltListener(PostSectionBuiltListener listener) {
        listenerList.remove(PostSectionBuiltListener.class, listener);
    }
    
    /**
     * Invokes all listeners that have registered for the SectionBuiltEvent
     * and passes <code>event</code> to them.
     */
    public final void fireSectionBuiltEvent(SectionBuiltEvent event) {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == SectionBuiltListener.class) {
                ((SectionBuiltListener) listeners[i + 1]).sectionBuilt(event);
            }
        }
    }
   
    /**
     * Invokes all listeners that have registered for the PostSectionBuiltEvent
     * and passes <code>event</code> to them.
    */
    public final void firePostSectionBuiltEvent(PostSectionBuiltEvent event) {
        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == PostSectionBuiltListener.class) {
                ((PostSectionBuiltListener) listeners[i + 1]).postSectionBuilt(event);
            }
        }
    }
    
    /**
     * @return whether there is at least one renderer handler after
     * the currently active one.
     */
    public final boolean hasNextRendererHandler() {
        if (rendererHandlerIndex < rendererHandlers.size() - 1) {
            return true;
        }
        return false;
    }
    
    /**
     * @return whether there is at least one renderer handler before the currently
     * active one.
     */
    public final boolean hasPreviousRendererHandler() {
        if (rendererHandlerIndex > 0) {
            return true;
        }
        return false;
    }
    
    /**
     * Changes the currently selected renderer handler to be the one after the
     * currently selected one.
     * @throws IllegalStateException if there is no renderer handler after the
     * currently selected one.
     */
    public final void nextRendererHandler() {
        if (rendererHandlerIndex >= rendererHandlers.size() - 1) {
            throw new IllegalStateException("There is no next rendererHandler"); //$NON-NLS-1$
        }
        ++rendererHandlerIndex;
    }

    /**
     * Changes the currently selected renderer handler to be the one before the
     * currently selected one.
     * @throws IllegalStateException if there is no renderer handler before the
     * currently selected one.
     */
    public final void previousRendererHandler() {
        if (rendererHandlerIndex == 0) {
            throw new IllegalStateException("There is no previous " + //$NON-NLS-1$
                    "rendererHandler"); //$NON-NLS-1$
        }
        --rendererHandlerIndex;
    }
    
    /**
     * Returns a list of basic presentation models for the given entry. This
     * list may contain 0 items in case no basic presentation model was found
     * for the given entry. This is just a convenience method that calls 
     * {@link #getRenderers(org.psygrid.data.model.hibernate.Entry)} and retrieves any basic presentation model
     * from the renderers returned.
     * 
     * @param entry of which we want to retrieve presentation models.
     * @return a list containing 0 or more presentation models for the given
     * entry.
     * @see #getRenderers(org.psygrid.data.model.hibernate.Entry)
     */
    public final List<BasicPresModel> getBasicPresModels(Entry entry) {
        List<Renderer> renderers = getRenderers(entry);
        List<BasicPresModel> presModels = new ArrayList<BasicPresModel>(renderers.size());
        for (Renderer renderer : renderers) {
            if (renderer != null && renderer instanceof BasicRenderer) {
                BasicRenderer<?> basicRenderer = BasicRenderer.class.cast(renderer);
                presModels.add(basicRenderer.getPresModel());
            }
        }
        return presModels;
    }
    
    /**
     * Returns a list of composite presentation models for the given entry. This
     * list may contain 0 items in case no composite presentation model was found
     * for the given entry. This is just a convenience method that calls 
     * {@link #getRenderers(org.psygrid.data.model.hibernate.Entry)} and retrieves any composite presentation model
     * from the renderers returned.
     * 
     * @param entry of which we want to retrieve presentation models.
     * @return a list containing 0 or more presentation models for the given
     * entry.
     * @see #getRenderers(org.psygrid.data.model.hibernate.Entry)
     */
    public final CompositePresModel getCompositePresModel(Entry entry,
            int rowIndex) {
        Renderer renderer = getRenderer(entry, rowIndex);
        if (renderer != null && renderer instanceof CompositeRenderer) {
            CompositeRenderer<?> compositeRenderer = CompositeRenderer.class.cast(renderer);
            return compositeRenderer.getPresModel();
        }

        return null;
    }
    
    /**
     * Returns a list of renderers for the given entry. This method will return
     * an empty list in case it cannot find any renderers for the given entry.
     * For basic entries, it will never return a list containing more than one
     * renderer.<p>
     * 
     * The method tries to find the renderers for the entry in the currently
     * active renderer handler first. If it finds them here, it simply returns
     * them. Otherwise, it proceeds to look in other renderer handlers. Note
     * that it will return the renderers as soon as it finds them in any of
     * the other renderer handlers.
     * 
     * @param entry of which we want to find renderers.
     * @return a list of renderers for <code>entry</code> containing 0 or more
     * items.
     */
    public final List<Renderer> getRenderers(Entry  entry) {
        List<Renderer> renderers = 
            getCurrentRendererHandler().getExistingRenderers(entry);
        
        if (renderers.size() > 0) {
            return renderers;
        }
        
        for (RendererHandler rendererHandler : rendererHandlers) {
            renderers = rendererHandler.getExistingRenderers(entry);
            
            if (renderers.size() > 0) {
                return renderers;
            }
        }
        return renderers;
    }
    
    /**
     * Returns the renderer for the given entry and rowIndex or null if none
     * can be found.<p>
     * 
     * The method tries to find the renderer in the currently
     * active renderer handler first. If it finds it here, it simply returns
     * it. Otherwise, it proceeds to look in other renderer handlers. Note
     * that it will return a renderer as soon as it finds it in any of
     * the other renderer handlers.
     * 
     * @param entry of which we want to find the renderer.
     * @param rowIndex The index of the entry in its parent.
     * @return a Renderer for <code>entry</code> in <code>rowIndex</code> or
     * null.
     */
    public final Renderer getRenderer(Entry  entry, int rowIndex) {
        Renderer renderer = 
            getCurrentRendererHandler().getExistingRenderer(entry, rowIndex);
        
        if (renderer != null) {
            return renderer;
        }
        for (RendererHandler rendererHandler : rendererHandlers) {
            renderer = rendererHandler.getExistingRenderer(entry, rowIndex);
            
            if (renderer != null) {
                return renderer;
            }
        }
        return null;
    }
    
    /**
     * @return an unmodifiable list of the standard codes contained in this
     * object.
     */
    public final List<StandardCode> getStandardCodes()   {
        return Collections.unmodifiableList(standardCodes);
    }

    /**
     * @return the document occurrence instance that all models built by this
     * object should belong to.
     */
    public final  DocumentInstance  getDocOccurrenceInstance() {
        return docOccurrenceInstance;
    }
    
    /**
     * Add <code>sectionPresModel</code> to the list of section presentation
     * models in the position indicated by <code>index</code>.
     * @param index Position to insert <code>sectionPresModel</code> in.
     * @param sectionPresModel Section presentation model to insert.
     */
    public final void addSectionPresModel(int index, 
            SectionPresModel sectionPresModel) {
        sectionPresModels.add(index, sectionPresModel);
    }
    
    public final void removeSectionPresModel(
    		SectionPresModel sectionPresModel) {
    	Iterator<RendererHandler> it = rendererHandlers.iterator();
    	int removedIndex = 0;
    	while ( it.hasNext() ){
    		RendererHandler rh = it.next();
    		if ( sectionPresModel == rh.getSectionPresModel() ){
    			it.remove();
    			break;
    		}
    		removedIndex++;
    	}
    	if ( removedIndex == rendererHandlers.size() ){
    		rendererHandlerIndex--;
    	}
    	sectionPresModels.remove(sectionPresModel);
    }
    
    /**
     * @return an unmodifiable list of the section presentation models for
     * docOccurrenceInstance.
     */
    public final List<SectionPresModel> getSectionPresModels()    {
        return Collections.unmodifiableList(sectionPresModels);
    }

    /**
     * Add <code>rendererHandler</code> to the list of renderer handlers in the
     * position indicated by <code>index</code>.
     * @param index Position to insert <code>rendererHandler</code>.
     * @param rendererHandler Renderer handler to insert.
     */
    public void addRendererHandler(int index, RendererHandler rendererHandler) {
        rendererHandlers.add(index, rendererHandler);
        rendererHandler.setBuilderHandler(this);
        if ( index <= rendererHandlerIndex ){
        	//if we are inserting a renderer handler before the current one then
        	//make sure the index is incremented, so that it still points to the
        	//current renderer handler
        	++rendererHandlerIndex;
        }
    }
    
    public void rewind(){
    	rendererHandlerIndex = 0;
    }
}
