/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.eclipse;

import java.util.ArrayList;

import kr.ac.kaist.jsaf.clone_refactor.core.NodeMatcher;
import kr.ac.kaist.jsaf.clone_refactor.core.PullUpMethodRefactoring;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneInstance;
import kr.ac.kaist.jsaf.clone_refactor.views.CloneView;
import kr.ac.kaist.jsaf.clone_refactor.wizard.PullUpMethodWizard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.wst.jsdt.core.dom.ASTNode;

public class PullUpMethodHandler extends AbstractHandler {
	public PullUpMethodHandler() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		findMismatches();
		startWizard(new PullUpMethodWizard(createPullUpMethodRefactoring()),
				HandlerUtil.getActiveShell(event), "Pull Up Method");
		return null;
	}

	private PullUpMethodRefactoring createPullUpMethodRefactoring() {
		PullUpMethodRefactoring refactoring = new PullUpMethodRefactoring();
		return refactoring;
	}

	private void startWizard(RefactoringWizard wizard, Shell parent,
			String dialogTitle) {
		try {
			RefactoringWizardOpenOperation operation = new RefactoringWizardOpenOperation(
					wizard);
			operation.run(parent, dialogTitle);
		} catch (InterruptedException ie) {
			UserInterface.printStackTraceToConsole(ie);
		}
	}

	private void findMismatches() {
		ArrayList<CloneInstance> selectedInstances = CloneView
				.getSelectedInstances();
		for (int i = 0; i < selectedInstances.size(); i++)
			selectedInstances.get(i).clearMismatches();

		for (int i = 0; i < selectedInstances.size() - 1; i++) {
			for (int j = i + 1; j < selectedInstances.size(); j++) {
				CloneInstance instance1 = selectedInstances.get(i);
				CloneInstance instance2 = selectedInstances.get(j);

				ArrayList<ASTNode> serializedTree1 = instance1
						.getSerializedTree();
				ArrayList<ASTNode> serializedTree2 = instance2
						.getSerializedTree();

				if (serializedTree1 != null && serializedTree2 != null) {
					int length = serializedTree1.size() < serializedTree2
							.size() ? serializedTree1.size() : serializedTree2
							.size();
					for (int k = 0; k < length; k++) {
						ASTNode node1 = serializedTree1.get(k);
						ASTNode node2 = serializedTree2.get(k);
						if (node1.getNodeType() != node2.getNodeType()) {
							instance1.addMismatch(node1);
							instance2.addMismatch(node2);
						} else {
							if (!NodeMatcher.match(node1, node2)) {
								instance1.addMismatch(node1);
								instance2.addMismatch(node2);
							}
						}
					}
				}
			}

		}
	}
}
