/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.wizard;

import java.util.ArrayList;

import kr.ac.kaist.jsaf.clone_refactor.core.FunctionDeclarationProperty;
import kr.ac.kaist.jsaf.clone_refactor.eclipse.Utility;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneInstance;
import kr.ac.kaist.jsaf.clone_refactor.views.CloneView;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.jsdt.core.JavaScriptConventions;
import org.eclipse.wst.jsdt.core.JavaScriptCore;

public class PullUpMethodInputPage extends UserInputWizardPage {
	private String functionName;
	private Text functionNameText, newFunctionText;
	private Label warningText;
	private Combo cloneCombo;

	public PullUpMethodInputPage() {
		super(PullUpMethodInputPage.class.getName());
	}

	@Override
	public void createControl(final Composite parent) {
		Composite composite = createRootComposite(parent);
		setControl(composite);

		createWarningText(composite);
		createFunctionName(composite);
		createCloneCombos(composite);
		createCodeArea(composite);
		createInformationText(composite);
		validate();
	}

	private Composite createRootComposite(final Composite parent) {
		Composite result = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		result.setLayout(gridLayout);
		initializeDialogUnits(result);
		Dialog.applyDialogFont(result);
		return result;
	}

	private void createFunctionName(final Composite composite) {
		Label label = new Label(composite, SWT.NONE);
		label.setText("New function name: ");

		GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		data.horizontalIndent = 1;
		label.setLayoutData(data);

		functionNameText = new Text(composite, SWT.BORDER);
		functionNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		functionNameText.addKeyListener(new KeyAdapter() {
			public void keyReleased(final KeyEvent e) {
				setFunctionName(functionNameText.getText());
				FunctionDeclarationProperty.functionName = functionName;
				validate();
			}
		});
	}

	private void createCloneCombos(final Composite composite) {
		Label label = new Label(composite, SWT.NONE);
		label.setText("Function body based on: ");

		GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		data.horizontalIndent = 1;
		label.setLayoutData(data);

		final ArrayList<CloneInstance> selectedInstances = CloneView
				.getSelectedInstances();
		ArrayList<String> items = new ArrayList<String>();
		for (int i = 0; i < selectedInstances.size(); i++) {
			CloneInstance instance = selectedInstances.get(i);
			items.add(instance.getCanonicalFilename() + ":"
					+ instance.getStartLine() + "-" + instance.getEndLine());
		}

		cloneCombo = new Combo(composite, SWT.READ_ONLY);
		cloneCombo.setItems(items.toArray(new String[items.size()]));
		cloneCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				int selectedIndex = cloneCombo.getSelectionIndex();
				FunctionDeclarationProperty.selectedIndex = selectedIndex;
				newFunctionText.setText(Utility
						.readCloneToString(selectedInstances.get(selectedIndex)));
				newFunctionText.setEditable(false);
				validate();
			}
		});
	}

	private void createCodeArea(final Composite composite) {
		newFunctionText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP
				| SWT.V_SCROLL | SWT.READ_ONLY);
		newFunctionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 2, 1));
	}

	private void createWarningText(final Composite composite) {
		new Label(composite, SWT.NONE);
		warningText = new Label(composite, SWT.NONE);
		warningText.setForeground(new Color(composite.getDisplay(), 255, 0, 0));
		warningText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
	}

	private void createInformationText(final Composite composite) {
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		label.setText("This refactoring creates a new function definition with name being the inputted name and body being the selected code fragment.\nThe duplicated code fragments will be replaced by invocation of the newly created function.");
	}

	private void validate() {
		if (JavaScriptConventions.validateFunctionName(
				functionNameText.getText(), JavaScriptCore.VERSION_1_7,
				JavaScriptCore.VERSION_1_7).getCode() == IStatus.OK) {
			warningText.setText("");
			setPageComplete(cloneCombo.getSelectionIndex() != -1);
		} else {
			if (functionNameText.getText().length() > 0)
				warningText.setText("Invalid function name");
			else
				warningText.setText("");
			setPageComplete(false);
		}
	}

	public String getFunctionName() {
		return functionName;
	}

	private void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
}
