/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.model;

public abstract class AbstractModel {
	protected AbstractModel parent;
	protected IDeltaListener listener = NullDeltaListener.getSoleInstance();

	protected void fireAdd(Object added) {
		listener.add(new DeltaEvent(added));
	}

	protected void fireRemove(Object removed) {
		listener.remove(new DeltaEvent(removed));
	}

	public AbstractModel getParent() {
		return parent;
	}

	/*
	 * The receiver should visit the toVisit object and pass along the argument.
	 */
	public abstract void accept(IModelVisitor visitor, Object passAlongArgument);

	public void addListener(IDeltaListener listener) {
		this.listener = listener;
	}

	public AbstractModel() {
	}

	public void removeListener(IDeltaListener listener) {
		if (this.listener.equals(listener)) {
			this.listener = NullDeltaListener.getSoleInstance();
		}
	}
}
