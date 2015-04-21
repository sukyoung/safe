/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.wizard;

import kr.ac.kaist.jsaf.clone_refactor.eclipse.TaskManager;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class PullUpMethodWizard extends RefactoringWizard {	
	public PullUpMethodWizard(Refactoring refactoring) {
		super(refactoring, WIZARD_BASED_USER_INTERFACE);
	}

	@Override
	protected void addUserInputPages() {
		setDefaultPageTitle(getRefactoring().getName());
		addPage(new PullUpMethodInputPage());
	}
		
	@Override
	public boolean performFinish() {
		super.performFinish();
		TaskManager.performFinish();
		return true;
	}
}
