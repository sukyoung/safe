/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.model;

public class NullDeltaListener implements IDeltaListener {
	protected static NullDeltaListener soleInstance = new NullDeltaListener();

	public static NullDeltaListener getSoleInstance() {
		return soleInstance;
	}

	/*
	 * @see IDeltaListener#add(DeltaEvent)
	 */
	public void add(DeltaEvent event) {
	}

	/*
	 * @see IDeltaListener#remove(DeltaEvent)
	 */
	public void remove(DeltaEvent event) {
	}

}
