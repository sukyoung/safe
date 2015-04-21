/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.views;

import java.util.Iterator;

import kr.ac.kaist.jsaf.clone_refactor.model.AbstractModel;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneCollection;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneGroup;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneInstance;
import kr.ac.kaist.jsaf.clone_refactor.model.DeltaEvent;
import kr.ac.kaist.jsaf.clone_refactor.model.IDeltaListener;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

public class CloneContentProvider implements ITreeContentProvider,
		IDeltaListener {
	private static Object[] EMPTY_ARRAY = new Object[0];
	protected TreeViewer viewer;

	/*
	 * @see IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/*
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	/**
	 * Notifies this content provider that the given viewer's input has been
	 * switched to a different element.
	 * <p>
	 * A typical use for this method is registering the content provider as a
	 * listener to changes on the new input (using model-specific means), and
	 * deregistering the viewer from the old input. In response to these change
	 * notifications, the content provider propagates the changes to the viewer.
	 * </p>
	 *
	 * @param viewer
	 *            the viewer
	 * @param oldInput
	 *            the old input element, or <code>null</code> if the viewer did
	 *            not previously have an input
	 * @param newInput
	 *            the new input element, or <code>null</code> if the viewer does
	 *            not have an input
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer) viewer;
		if (oldInput != null) {
			removeListenerFrom((CloneCollection) oldInput);
		}
		if (newInput != null) {
			addListenerTo((CloneCollection) newInput);
		}
	}

	/**
	 * Because the domain model does not have a richer listener model,
	 * recursively remove this listener from each child box of the given box.
	 */
	protected void removeListenerFrom(CloneCollection collection) {
		collection.removeListener(this);
		for (Iterator<CloneGroup> iterator = collection.getGroups().iterator(); iterator
				.hasNext();) {
			CloneGroup aBox = (CloneGroup) iterator.next();
			removeListenerFrom(aBox);
		}
	}
	
	protected void removeListenerFrom(CloneGroup group) {
		group.removeListener(this);
		for (Iterator<CloneInstance> iterator = group.getInstances().iterator(); iterator
				.hasNext();) {
			CloneInstance aBox = (CloneInstance) iterator.next();
			removeListenerFrom(aBox);
		}
	}

	protected void removeListenerFrom(CloneInstance instance) {
		instance.removeListener(this);
	}
	
	/**
	 * Because the domain model does not have a richer listener model,
	 * recursively add this listener to each child box of the given box.
	 */
	protected void addListenerTo(CloneCollection collection) {
		collection.addListener(this);
		for (Iterator<CloneGroup> iterator = collection.getGroups().iterator(); iterator
				.hasNext();) {
			CloneGroup aBox = (CloneGroup) iterator.next();
			addListenerTo(aBox);
		}
	}
	
	protected void addListenerTo(CloneGroup group) {
		group.addListener(this);
		for (Iterator<CloneInstance> iterator = group.getInstances().iterator(); iterator
				.hasNext();) {
			CloneInstance aBox = (CloneInstance) iterator.next();
			addListenerTo(aBox);
		}
	}

	protected void addListenerTo(CloneInstance instance) {
		instance.addListener(this);
	}
	
	/*
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof CloneCollection) {
			return ((CloneCollection) parentElement).getGroups().toArray();
		} else if (parentElement instanceof CloneGroup) {
			return ((CloneGroup) parentElement).getInstances().toArray();
		}
		return EMPTY_ARRAY;
	}

	/*
	 * @see ITreeContentProvider#getParent(Object)
	 */
	public Object getParent(Object element) {
		if (element instanceof CloneGroup) {
			return ((CloneGroup) element).getParent();
		} else if (element instanceof CloneInstance) {
			return ((CloneInstance) element).getParent();
		}
		return null;
	}

	/*
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	/*
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/*
	 * @see IDeltaListener#add(DeltaEvent)
	 */
	public void add(DeltaEvent event) {
		Object collection = ((AbstractModel) event.receiver()).getParent();
		viewer.refresh(collection, false);
	}

	/*
	 * @see IDeltaListener#remove(DeltaEvent)
	 */
	public void remove(DeltaEvent event) {
		add(event);
	}

}
