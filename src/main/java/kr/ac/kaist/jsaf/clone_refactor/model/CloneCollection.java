/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.model;

import java.util.ArrayList;

public class CloneCollection extends AbstractModel {
	public final static CloneCollection instance = new CloneCollection();

	protected static ArrayList<CloneGroup> groups;

	private static IModelVisitor adder = new Adder();
	private static IModelVisitor remover = new Remover();

	private CloneCollection() {
		groups = new ArrayList<CloneGroup>();
	}

	private static class Adder implements IModelVisitor {
		public void visitCloneInstance(CloneInstance instance, Object argument) {
			((CloneGroup) argument).addInstance(instance);
		}

		public void visitCloneGroup(CloneGroup group, Object argument) {
			((CloneCollection) argument).addGroup(group);
		}
	}

	private static class Remover implements IModelVisitor {
		public void visitCloneInstance(CloneInstance instance, Object argument) {
			((CloneGroup) argument).removeInstance(instance);
		}

		public void visitCloneGroup(CloneGroup group, Object argument) {
			((CloneCollection) argument).removeGroup(group);
			group.addListener(NullDeltaListener.getSoleInstance());
		}
	}

	public void addGroup(CloneGroup group) {
		groups.add(group);
		group.parent = this;
		fireAdd(group);
	}

	public void removeGroup(CloneGroup group) {
		groups.remove(group);
		group.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(group);
	}

	public void add(AbstractModel toAdd) {
		toAdd.accept(adder, this);
	}

	public void remove(AbstractModel toRemove) {
		toRemove.accept(remover, this);
	}

	public int size() {
		return groups.size();
	}

	@Override
	public void accept(IModelVisitor visitor, Object passAlongArgument) {

	}

	public ArrayList<CloneGroup> getGroups() {
		return groups;
	}

	public void clearGroups() {
		groups.clear();
	}
	
	public static boolean contains(CloneInstance instance) {
		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).contains(instance))
				return true;
		}
		return false;
	}	
}
