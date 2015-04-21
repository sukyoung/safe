/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.model;

import java.util.ArrayList;

public class CloneGroup extends AbstractModel {
	public ArrayList<CloneInstance> instances;

	private static IModelVisitor adder = new Adder();
	private static IModelVisitor remover = new Remover();

	private Integer id;

	public CloneGroup() {
		instances = new ArrayList<CloneInstance>();
	}

	private static class Adder implements IModelVisitor {
		public void visitCloneInstance(CloneInstance instance, Object argument) {
			((CloneGroup) argument).addInstance(instance);
		}

		public void visitCloneGroup(CloneGroup group, Object argument) {
		}
	}

	private static class Remover implements IModelVisitor {
		public void visitCloneInstance(CloneInstance instance, Object argument) {
			((CloneGroup) argument).removeInstance(instance);
		}

		public void visitCloneGroup(CloneGroup group, Object argument) {
		}
	}

	protected void removeInstance(CloneInstance instance) {
		instances.remove(instance);
		instance.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(instance);
	}

	protected void addInstance(CloneInstance instance) {
		instances.add(instance);
		instance.parent = this;
		fireAdd(instance);
	}

	public ArrayList<CloneInstance> getInstances() {
		return instances;
	}

	public void add(AbstractModel toAdd) {
		toAdd.accept(adder, this);
	}

	public void remove(AbstractModel toRemove) {
		toRemove.accept(remover, this);
	}

	/**
	 * Answer the total number of items the receiver contains.
	 */
	public int size() {
		return instances.size();
	}

	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitCloneGroup(this, passAlongArgument);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean contains(CloneInstance instance) {
		for (int i = 0; i < instances.size(); i++) {
			CloneInstance ci = instances.get(i);

			if (ci.contains(instance))
				return true;
		}

		return false;
	}
}
