/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.views;

import kr.ac.kaist.jsaf.clone_refactor.model.CloneGroup;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneInstance;

import org.eclipse.jface.viewers.LabelProvider;

public class CloneLabelProvider extends LabelProvider {

	/*
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		if (element instanceof CloneGroup) {
			return "Clone Group " + ((CloneGroup) element).getId();
		} else if (element instanceof CloneInstance) {
			return ((CloneInstance) element).getCanonicalFilename() + ":"
					+ ((CloneInstance) element).getStartLine() + "-"
					+ ((CloneInstance) element).getEndLine();
		} else {
			throw unknownElement(element);
		}
	}

	public void dispose() {
	}

	protected RuntimeException unknownElement(Object element) {
		return new RuntimeException("Unknown type of element in tree of type "
				+ element.getClass().getName());
	}
}
