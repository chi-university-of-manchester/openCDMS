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


package org.psygrid.collection.entry.renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.tools.SPInterface;
import org.apache.commons.discovery.tools.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psygrid.collection.entry.builder.BuilderHandler;
import org.psygrid.collection.entry.event.EditEvent;
import org.psygrid.collection.entry.event.EditListener;
import org.psygrid.collection.entry.event.PresModelCreatedEvent;
import org.psygrid.collection.entry.event.PresModelCreatedListener;
import org.psygrid.collection.entry.event.RendererCreatedEvent;
import org.psygrid.collection.entry.event.RendererCreatedListener;
import org.psygrid.collection.entry.model.BasicPresModel;
import org.psygrid.collection.entry.model.BasicTextPresModel;
import org.psygrid.collection.entry.model.DatePresModel;
import org.psygrid.collection.entry.model.DerivedPresModel;
import org.psygrid.collection.entry.model.OptionPresModel;
import org.psygrid.collection.entry.model.SectionPresModel;
import org.psygrid.collection.entry.model.StandardPresModel;
import org.psygrid.collection.entry.model.TextPresModel;
import org.psygrid.collection.entry.renderer.RendererData.EditableStatus;
import org.psygrid.data.model.IDateValue;
import org.psygrid.data.model.IOptionValue;
import org.psygrid.data.model.IValue;
import org.psygrid.data.model.hibernate.BasicResponse;
import org.psygrid.data.model.hibernate.CompositeResponse;
import org.psygrid.data.model.hibernate.EditAction;
import org.psygrid.data.model.hibernate.Entry;import org.psygrid.data.model.hibernate.StandardCode;

/**
 * Provides various services related to retrieving a <code>Renderer</code>
 * object. This involves finding the correct <code>RendererSPI</code> to
 * which the creation of the <code>Renderer</code> object is delegated.<p>
 * 
 * @author Ismael Juma
 * @see Renderer
 * @see RendererSPI
 * @see #getRenderer( org.psygrid.data.model.hibernate.Entry , org.psygrid.data.model.hibernate.CompositeResponse, int, String, boolean, EditableStatus)
 */
public class RendererHandler {

	private final List<RendererSPI> rendererSPIs;

	private static final Log log = LogFactory.getLog(RendererHandler.class);

	private final EventListenerList listenerList = new EventListenerList();

	private BuilderHandler builderHandler;

	private final SectionPresModel sectionPresModel;

	private final Map<Entry, List<Renderer>> renderersMap;

	/**
	 * Number of existing renderers. This number could be calculated from
	 * iterating through the elements in <code>renderersMap</code> but to
	 * avoid the performance impact, we update this field whenever an item
	 * is added or removed from renderersMap.
	 */
	private int numExistingRenderers;

	/**
	 * If True then the RendererHandler has been rendered at least once.
	 */
	private boolean rendered = false;
	
	/**
	 * Creates an instance of this object. As a general rule, there should
	 * be one RendererHandler for each SectionPresModel that has to be
	 * rendered.
	 * 
	 * @param sectionPresModel to set the <code>sectionPresModel</code> property
	 * to. Internally, this property is used when any of the methods for the
	 * creation of a presentation model is used.
	 * @param renderersMapCapacity The initial capacity of the <code>renderersMap</code>
	 * property. If a negative number is provided, then the default initial
	 * capacity is used.
	 * 
	 * @see #createBasicPresModel(Object, org.psygrid.data.model.hibernate.BasicResponse, IValue, String)
	 * @see #createBasicTextPresModel(Object, org.psygrid.data.model.hibernate.BasicResponse, IValue, String)
	 * @see #createDatePresModel(Object, org.psygrid.data.model.hibernate.BasicResponse, IDateValue, String)
	 * @see #createOptionPresModel(Object, org.psygrid.data.model.hibernate.BasicResponse, IOptionValue, String)
	 * @see #createPresModel(Object, org.psygrid.data.model.hibernate.BasicResponse, IValue, String)
	 * @see #createTextPresModel(Object, org.psygrid.data.model.hibernate.BasicResponse, IValue, String)
	 */
	public RendererHandler(SectionPresModel sectionPresModel, 
			int renderersMapCapacity) {
		this.sectionPresModel = sectionPresModel;
		rendererSPIs = new ArrayList<RendererSPI>();
		numExistingRenderers = 0;
		if (renderersMapCapacity > -1) {
			renderersMap = new HashMap<Entry, List<Renderer>>(renderersMapCapacity);
		}
		else {
			renderersMap = new HashMap<Entry, List<Renderer>>();
		}

		log.debug("Loading all renderers"); //$NON-NLS-1$
		SPInterface spi = new SPInterface(RendererSPI.class);
		ClassLoaders loaders = new ClassLoaders();
		loaders.put(Thread.currentThread().getContextClassLoader());
		Enumeration<?> spe = Service.providers(spi, loaders);
		while (spe.hasMoreElements()) {
			RendererSPI renderer = (RendererSPI) spe.nextElement();
			rendererSPIs.add(renderer);
			log.debug("\t" + renderer.getClass().toString()); //$NON-NLS-1$
		}
		log.debug("Done"); //$NON-NLS-1$
	}

	/**
	 * Convenience constructor that calls 
	 * {@link #RendererHandler(SectionPresModel, int)} and passes 
	 * <code>sectionPresModel</code> and -1 to it.
	 * 
	 * @param sectionPresModel SectionPresModel to be passed to
	 * <code>RendererHandler(SectionPresModel, int)</code>.
	 */
	public RendererHandler(SectionPresModel sectionPresModel) {
		this(sectionPresModel, -1);
	}

	/**
	 * Adds an <code>EditListener</code> to this renderer handler.
	 * 
	 * @param listener to be notified of any <code>EditEvent</code> fired by
	 * this renderer handler.
	 * 
	 * @see EditListener
	 * @see EditEvent
	 */
	public void addEditListener(EditListener listener) {
		listenerList.add(EditListener.class, listener);
	}

	/**
	 * Removes an <code>EditListener</code> from this renderer handler.
	 * 
	 * @param listener to be removed from this renderer handler.
	 * 
	 * @see EditListener
	 * @see EditEvent
	 */
	public void removeEditListener(EditListener listener) {
		listenerList.remove(EditListener.class, listener);
	}

	/**
	 * Notifies all <code>EditListener</code>s that registered interest in
	 * this event and passes <code>event</code> to them.
	 * 
	 * @param event <code>EditEvent</code> to be passed to listeners.
	 */
	public final void fireEditEvent(EditEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == EditListener.class) {
				((EditListener) listeners[i + 1]).editOccurred(event);
			}
		}
	}

	/**
	 * Adds an <code>PresModelCreatedListener</code> to this renderer handler.
	 * 
	 * @param listener to be notified of any <code>PresModelCreatedEvent</code>
	 * fired by this renderer handler.
	 * 
	 * @see PresModelCreatedListener
	 * @see PresModelCreatedEvent
	 */
	public void addPresModelCreatedListener(PresModelCreatedListener listener) {
		listenerList.add(PresModelCreatedListener.class, listener);
	}

	/**
	 * Creates an instance of <code>BasicPresModel</code> using the parameters
	 * received, fires a <code>PresModelCreatedEvent</code> and returns the
	 * created BasicPresModel instance.
	 * 
	 * @param source The source to be used in PresModelCreatedEvent.
	 * @param response The IBasicResponse to be used in the creation of 
	 * BasicPresModel.
	 * @param value The IValue to be used in the creation of BasicPresModel.
	 * @param validationPrefix The validationPrefix to be used in the creation 
	 * of BasicPresModel.
	 * @return an instance of <code>BasicPresModel</code>.
	 */
	public BasicPresModel createBasicPresModel(Object source,
			BasicResponse response, IValue value,
			String validationPrefix) {
		BasicPresModel model = new BasicPresModel(response, value, 
				sectionPresModel, validationPrefix, builderHandler.getDocOccurrenceInstance());
		processPresModel(source, model);
		return model;
	}

	/**
	 * Creates an instance of <code>BasicTextPresModel</code> using the parameters
	 * received, fires a <code>PresModelCreatedEvent</code> and returns the
	 * created BasicTextPresModel instance.
	 * 
	 * @param source The source to be used in PresModelCreatedEvent.
	 * @param response The IBasicResponse to be used in the creation of 
	 * BasicTextPresModel.
	 * @param value The IValue to be used in the creation of BasicPresModel.
	 * @param validationPrefix The validationPrefix to be used in the creation 
	 * of BasicTextPresModel.
	 * @return an instance of <code>BasicTextPresModel</code>.
	 */
	public BasicTextPresModel createBasicTextPresModel(Object source,
			BasicResponse response, IValue value,
			String validationPrefix) {
		BasicTextPresModel model = new BasicTextPresModel(response, value, 
				sectionPresModel, validationPrefix, builderHandler.getDocOccurrenceInstance());
		processPresModel(source, model);
		return model;
	}

	/**
	 * Creates an instance of <code>StandardPresModel</code> using the parameters
	 * received, fires a <code>PresModelCreatedEvent</code> and returns the
	 * created StandardPresModel instance.
	 * 
	 * @param source The source to be used in PresModelCreatedEvent.
	 * @param response The IBasicResponse to be used in the creation of 
	 * StandardPresModel.
	 * @param value The IValue to be used in the creation of StandardPresModel.
	 * @param validationPrefix The validationPrefix to be used in the creation 
	 * of StandardPresModel.
	 * @return an instance of <code>StandardPresModel</code>.
	 */
	public StandardPresModel createPresModel(Object source, 
			BasicResponse response, IValue value, String validationPrefix) {
		StandardPresModel model = new StandardPresModel(response, value,
				sectionPresModel, validationPrefix, builderHandler.getDocOccurrenceInstance());
		processPresModel(source, model);
		return model;
	}

	/**
	 * Creates an instance of <code>TextPresModel</code> using the parameters
	 * received, fires a <code>PresModelCreatedEvent</code> and returns the
	 * created TextPresModel instance.
	 * 
	 * @param source The source to be used in PresModelCreatedEvent.
	 * @param response The IBasicResponse to be used in the creation of 
	 * TextPresModel.
	 * @param value The IValue to be used in the creation of TextPresModel.
	 * @param validationPrefix The validationPrefix to be used in the creation 
	 * of TextPresModel.
	 * @return an instance of <code>TextPresModel</code>.
	 */
	public TextPresModel createTextPresModel(Object source, 
			BasicResponse response, IValue value, String validationPrefix) {
		TextPresModel model = new TextPresModel(response, value, 
				sectionPresModel, validationPrefix, builderHandler.getDocOccurrenceInstance());
		processPresModel(source, model);
		return model;
	}

	/**
	 * Creates an instance of <code>DerivedPresModel</code> using the parameters
	 * received, fires a <code>PresModelCreatedEvent</code> and returns the
	 * created DerivedPresModel instance.
	 * 
	 * @param source The source to be used in PresModelCreatedEvent.
	 * @param response The IBasicResponse to be used in the creation of 
	 * DerivedPresModel.
	 * @param value The IValue to be used in the creation of DerivedPresModel.
	 * @param validationPrefix The validationPrefix to be used in the creation 
	 * of DerivedPresModel.
	 * @return an instance of <code>DerivedPresModel</code>.
	 */
	public DerivedPresModel createDerivedPresModel(Object source, 
			BasicResponse response, IValue value, String validationPrefix) {
		DerivedPresModel model = new DerivedPresModel(response, value, 
				sectionPresModel, validationPrefix, builderHandler.getDocOccurrenceInstance());
		processPresModel(source, model);
		return model;
	}

	/**
	 * Creates an instance of <code>DatePresModel</code> using the parameters
	 * received, fires a <code>PresModelCreatedEvent</code> and returns the
	 * created DatePresModel instance.
	 * 
	 * @param source The source to be used in PresModelCreatedEvent.
	 * @param response The IBasicResponse to be used in the creation of 
	 * DatePresModel.
	 * @param value The IValue to be used in the creation of DatePresModel.
	 * @param validationPrefix The validationPrefix to be used in the creation 
	 * of DatePresModel.
	 * @return an instance of <code>DatePresModel</code>.
	 */
	public DatePresModel createDatePresModel(Object source, 
			BasicResponse response, IDateValue value, String validationPrefix) {
		DatePresModel model = new DatePresModel(response, value, 
				sectionPresModel, validationPrefix, builderHandler.getDocOccurrenceInstance());
		processPresModel(source, model);
		return model;
	}
	
	private void processPresModel(Object source, BasicPresModel model) {
		firePresModelCreatedEvent(new PresModelCreatedEvent(source, model));
	}

	/**
	 * Creates an instance of <code>OptionPresModel</code> using the parameters
	 * received, fires a <code>PresModelCreatedEvent</code> and returns the
	 * created OptionPresModel instance.
	 * 
	 * @param source The source to be used in PresModelCreatedEvent.
	 * @param response The IBasicResponse to be used in the creation of 
	 * OptionPresModel.
	 * @param value The IValue to be used in the creation of OptionPresModel.
	 * @param validationPrefix The validationPrefix to be used in the creation 
	 * of OptionPresModel.
	 * @return an instance of <code>OptionPresModel</code>.
	 */
	public OptionPresModel createOptionPresModel(Object source, 
			BasicResponse response, IOptionValue value,
			String validationPrefix) {
		OptionPresModel model = new OptionPresModel(response, value, 
				sectionPresModel, validationPrefix, builderHandler.getDocOccurrenceInstance());
		processPresModel(source, model);
		return model;
	}

	/**
	 * Removes a <code>PresModelCreatedListener</code> from this renderer handler.
	 * 
	 * @param listener to be removes from this renderer handler.
	 * 
	 * @see PresModelCreatedListener
	 * @see PresModelCreatedEvent
	 */
	public void removePresModelCreatedListener(
			PresModelCreatedListener listener) {
		listenerList.remove(PresModelCreatedListener.class, listener);
	}

	protected void firePresModelCreatedEvent(PresModelCreatedEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PresModelCreatedListener.class) {
				((PresModelCreatedListener) listeners[i + 1]).modelCreated(event);
			}
		}
	}

	/**
	 * Adds a <code>RendererCreatedListener</code> to this renderer handler.
	 * 
	 * @param listener to be notified of any <code>RendererCreatedEvent</code>
	 * fired by this renderer handler.
	 * 
	 * @see RendererCreatedListener
	 * @see RendererCreatedEvent
	 */
	public void addRendererCreatedListener(RendererCreatedListener listener) {
		listenerList.add(RendererCreatedListener.class, listener);
	}

	/**
	 * Removes a <code>RendererCreatedListener</code> from this renderer handler.
	 * 
	 * @param listener to be removed from this renderer handler.
	 * 
	 * @see RendererCreatedListener
	 * @see RendererCreatedEvent
	 */
	public void removeRendererCreatedListener(RendererCreatedListener listener) {
		listenerList.remove(RendererCreatedListener.class, listener);
	}

	protected void fireRendererCreatedEvent(RendererCreatedEvent event) {
		Object[] listeners = listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == RendererCreatedListener.class) {
				((RendererCreatedListener) listeners[i + 1]).rendererCreated(event);
			}
		}
	}

	/**
	 * Returns an unmodifiable map of existing renderers. The map contains
	 * all the entries for which renderers were created and not subsequentely 
	 * removed by this renderer handler. These entries are inserted as keys to
	 * the Map. Each <code>IEntry</code> in turn is associated with a list
	 * of <code>Renderer</code> objects. If the <code>IEntry</code> is of
	 * type <code>IBasicEntry</code>, it is safe to assume that this list will
	 * contain a single <code>Renderer</code> object.
	 * 
	 * The index in the list of the renderer objects will match their rowIndex.
	 * As a result, it is possible for the list of renderers to contain null
	 * values up to this index, depending on when this method is called. It
	 * should not happen under normal 
	 * 
	 * Note that the renderer handler relies on the <code>RendererSPI</code>
	 * implementations to add the renderers they create to this map. If they
	 * fail to honour the contract, then <code>renderersMap</code> will not
	 * contain all renderers created.
	 * 
	 * @return an unmodifiable map of the existing renderers.
	 */
	public Map<Entry, List<Renderer>> getRenderersMap(){
		return Collections.unmodifiableMap(renderersMap);
	}

	/**
	 * Convenience method that gets the list of renderers from 
	 * <code>renderersMap</code> associated with the given key.
	 * 
	 * @param entry The key whose associated renderers are to be returned.
	 * @return The list of renderers associated with the given key in 
	 * <code>renderersMap</code> or an empty list if no renderers are associated
	 * with the given key.
	 * 
	 * @see #getRenderersMap()
	 */
	public List<Renderer> getExistingRenderers(Entry entry) {
		List<Renderer> renderers = renderersMap.get(entry);
		if (renderers == null) {
			return new ArrayList<Renderer>(0);
		}
		return renderers;
	}

	/**
	 * @return the number of renderers that have been created by this renderer
	 * handler.
	 */
	public int getNumExistingRenderers() {
		return numExistingRenderers;
	}

	/**
	 * Convenience method that gets the <code>Renderer</code> in 
	 * <code>renderersMap</code> associated with the given key and in
	 * <code>rowIndex</code>.
	 * 
	 * @param entry The key that is associated with the renderer required.
	 * @param rowIndex The position of the entry in its parent. This should
	 * always be 0 for ICompositeEntry and it should be 0 for any IBasicEntry
	 * that does not have a ICompositeEntry as a parent.
	 * @return The <code>Renderer</code>  associated with the given key and
	 * in the provided <code>rowIndex</code> or null if <code>entry</code>
	 * has no associated renderers in <code>renderersMap</code>.
	 * @throws IndexOutOfBoundsException if <code>rowIndex</code> is not within
	 * the range expected.
	 * 
	 * @see #getRenderersMap()
	 */
	public Renderer getExistingRenderer(Entry entry, int rowIndex) {
		List<Renderer> renderers = renderersMap.get(entry);
		if (renderers == null) {
			return null;
		}
		return renderers.get(rowIndex);
	}

	/**
	 * Adds <code>renderer</code> into <code>renderersMap</code> as an element
	 * of the list associated with the key <code>entry</code>.<p>
	 * 
	 * If <code>entry</code> is not a key in <code>renderersMap</code>, a new 
	 * map entry is created with it as the key. In this case, a list of 
	 * renderers is also created and added to the map as the value of the key.
	 * In addition, <code>renderer</code> will be added to this list in the
	 * position denoted by <code>rowIndex</code>. The list will be padded
	 * with null values if required to achieve this.
	 * 
	 * @param entry The key that <code>renderer</code> should be associated with
	 * in <code>renderersMap</code>.
	 * @param rowIndex The position in the list of renderers that <code>renderer</code>
	 * should be inserted.
	 * @param renderer The Renderer to be added to <code>renderersMap</code>.
	 * 
	 * @throws IllegalArgumentException if <code>entry</code> is null,
	 * <code>renderer</code> is null or <code>rowIndex</code> is < 0.
	 */
	public void putRenderer(Entry entry, int rowIndex, Renderer renderer) {
		checkEntryForNull(entry);
		checkRowIndex(rowIndex);
		checkRendererForNull(renderer);

		List<Renderer> renderers = renderersMap.get(entry);
		if (renderers == null) {
			renderers = new ArrayList<Renderer>(rowIndex + 1);
			renderersMap.put(entry, renderers);
		}
		int size = renderers.size();

		/* Add a new renderer to the list */
		if (rowIndex > size - 1) {
			/* In the unlikely event that we want to add a Renderer with a
			 * rowIndex > 0 in an unordered fashion we must first add nulls in
			 * the List up to the point required */
			for (int i = size; i < rowIndex - 1; ++i) {
				renderers.add(null);
			}
			renderers.add(renderer);
			++numExistingRenderers;
		}
		/* Replace an existing renderer in the list or insert a renderer
		 * into a spot previously occupied by a null value (used as padding) */
		else {
			if (renderers.get(rowIndex) == null) {
				++numExistingRenderers;
			}
			renderers.set(rowIndex, renderer);
		}
	}

	private void checkEntryForNull(Entry entry) {
		if (entry == null) {
			throw new IllegalArgumentException("entry cannot be null"); //$NON-NLS-1$
		}
	}

	private void checkRowIndex(int rowIndex) {
		if (rowIndex < 0) {
			throw new IllegalArgumentException("rowIndex must be bigger than 0"); //$NON-NLS-1$
		}
	}

	private void checkRendererForNull(Renderer renderer) {
		if (renderer == null) {
			throw new IllegalArgumentException("renderer cannot be null"); //$NON-NLS-1$
		}
	}

	/**
	 * Removes a <code>Renderer</code> associated with <code>entry</code> in
	 * <code>renderersMap</code>. The actual renderer removed from the list
	 * of renderers associated with <code>entry</code> is determined by 
	 * <code>rowIndex</code>.
	 * 
	 * @param entry that the Renderer to be removed is associated with.
	 * @param rowIndex The index used to determine which of the list of Renderers
	 * should be removed.
	 * @return The <code>Renderer</code> removed.
	 * @throws IllegalArgumentException if <code>entry</code> is not a key
	 * in <code>renderersMap</code>, if <code>rowIndex</code> is negative or 
	 * higher than the permitted value or if <code>entry</code> is null.
	 */
	public Renderer removeRenderer(Entry entry, int rowIndex) {
		checkEntryForNull(entry);
		checkRowIndex(rowIndex);

		List<Renderer> renderers = renderersMap.get(entry);
		if (renderers == null) {
			throw new IllegalArgumentException("No list of renderers exists for the given entry, [" + entry + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (renderers.size() < rowIndex) {
			throw new IllegalArgumentException("The rowIndex is too high, " + //$NON-NLS-1$
					"received: " + rowIndex + ", maximum allowed: " +  //$NON-NLS-1$ //$NON-NLS-2$
					(renderers.size() - 1));
		}
		--numExistingRenderers;
		return renderers.remove(rowIndex);
	}

	/**
	 * Removes the map entry in <code>renderersMap</code> that has <code>entry</code>
	 * as a key and returns the renderers associated with this key. If
	 * <code>entry</code> is not a valid key in <code>renderersMap</code>, then
	 * null is returned.
	 * 
	 * @param entry The key for the map entry to be removed.
	 * @return the list of renderers removed or null if <code>entry</code>
	 * is not a key in <code>renderersMap</code>.
	 * @throws IllegalArgumentException if <code>entry</code> is null.
	 */
	public List<Renderer> removeRenderers(Entry entry) {
		checkEntryForNull(entry);
		List<Renderer> renderersRemoved = renderersMap.remove(entry);
		numExistingRenderers = numExistingRenderers - renderersRemoved.size();
		return renderersRemoved;
	}

	/**
	 * Convenience method for 
	 * {@link #getRenderer( org.psygrid.data.model.hibernate.Entry , org.psygrid.data.model.hibernate.CompositeResponse, int, String, boolean)}.
	 * It simply calls the referenced method and passes 0, <code>null</code>
	 * and <code>false</code> as the three last parameters.
	 * 
	 * @see #getRenderer( org.psygrid.data.model.hibernate.Entry , org.psygrid.data.model.hibernate.CompositeResponse, int, String, boolean)
	 * @see #getRenderer( org.psygrid.data.model.hibernate.Entry , org.psygrid.data.model.hibernate.CompositeResponse, int, String, boolean, EditableStatus)
	 */
	public Renderer getRenderer(Entry model,
			CompositeResponse modelParentResponse) {
		return getRenderer(model, modelParentResponse, 0, null, false);
	}

	/**
	 * Convenience method for 
	 * {@link #getRenderer( org.psygrid.data.model.hibernate.Entry , org.psygrid.data.model.hibernate.CompositeResponse, int, String, boolean, EditableStatus)}.
	 * It simply calls the referenced method and passes 
	 * <code>EditableStatus.DEFAULT</code> as the last parameter.
	 * 
	 * @see #getRenderer( org.psygrid.data.model.hibernate.Entry , org.psygrid.data.model.hibernate.CompositeResponse, int, String, boolean, EditableStatus)
	 */
	public Renderer getRenderer(Entry model,
			CompositeResponse modelParentResponse, int rowIndex,
			String validationPrefix, boolean copy) {
		EditableStatus status = EditableStatus.DEFAULT;
		if (EditAction.READONLY.equals(model.getEditingPermitted())
				|| EditAction.DENY.equals(model.getEditingPermitted())) {
			status = EditableStatus.FALSE;
		}

		return getRenderer(model, modelParentResponse, rowIndex, validationPrefix, copy,
				status);
	}

	/**
	 * Finds the <code>RendererSPI</code> that can handle <code>model</code>
	 * and its parent and delegates the creation of the <code>Renderer</code>
	 * object to the <code>RendererSPI</code>. This is explained in more detail
	 * in {@link RendererSPI}.
	 * 
	 * @param model The model to retrieve the Renderer of.
	 * @param modelParentResponse The response of the parent of the model or null
	 * if the model has no parent or if the parent has no response associated
	 * with it. 
	 * @param rowIndex The row index the element belongs to. This is 0 if the
	 * element has no parent. If the element has a parent, then it should specify
	 * in what position it belongs in the parent.
	 * @param validationPrefix Text to be shown before any validation error
	 * message or null if no text should be shown. This is useful when a 
	 * validation icon is shared between many responses (e.g. in a composite).
	 * @param copy True if the element is to be built outside the main frame.
	 * This is useful in cases like the EditDialog where certain restrictions
	 * should not be applied. This is explained in more detail in 
	 * <code>RendererSPI</code>.
	 * @param editable Specifies the editable status of the visual entries. See
	 * {@link RendererData.EditableStatus}.
	 * @return Renderer instance containing the visual components that represent
	 * <code>model</code>.
	 * 
	 * @see RendererSPI
	 * @see Renderer
	 * @see RendererData.EditableStatus
	 */
	public Renderer getRenderer(Entry model,
			CompositeResponse modelParentResponse, int rowIndex,
			String validationPrefix, boolean copy, EditableStatus editable) {
		Entry modelParent = null;
		if (modelParentResponse != null) {
			modelParent = modelParentResponse.getEntry();
		}
		RendererSPI rspi = getRendererSPI(model, modelParent);
		if (rspi == null) {
			if (log.isWarnEnabled()) {
				log.warn("No Renderer found for entry: [name: " + model.getName() +  //$NON-NLS-1$
						"], [displayText: " + model.getDisplayText() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			return null;
		}
		if (EditAction.READONLY.equals(model.getEditingPermitted())
				|| EditAction.DENY.equals(model.getEditingPermitted())) {
			editable = EditableStatus.FALSE;
		}
		Renderer renderer = rspi.getRenderer(new RendererData(this, model,
				modelParentResponse, rowIndex, validationPrefix, copy, editable));
		return renderer;
	}

	final RendererSPI getRendererSPI(Entry model, Entry parent) {
		for (Iterator<RendererSPI> i = rendererSPIs.iterator(); i.hasNext();) {
			RendererSPI rspi = i.next();
			if (rspi.canHandle(model, parent)) {
				if (log.isDebugEnabled()) {
					log.debug("Getting renderer " + rspi.getClass()); //$NON-NLS-1$
				}
				return rspi;
			}
		}
		return null;
	}

	/**
	 * Sets the builderHandler property to <code>builderHandler</code>.
	 * @param builderHandler The value to set the builderHandler property to.
	 */
	public final void setBuilderHandler(BuilderHandler builderHandler) {
		this.builderHandler = builderHandler;
	}

	/**
	 * @return the value of the <code>builderHandler</code> property.
	 */
	public final BuilderHandler getBuilderHandler() {
		return builderHandler;
	}

	List<StandardCode> getStdCodes()   {
		return builderHandler.getStandardCodes();
	}

	/**
	 * @return the value of the <code>sectionPresModel</code> property.
	 */
	public final SectionPresModel getSectionPresModel() {
		return sectionPresModel;
	}

	public boolean isRendered() {
		return rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}
}
