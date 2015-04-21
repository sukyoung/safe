/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.eclipse;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

public class ConfigurationDialog extends Dialog {
	private String srcDir;
	private String minTokens;
	private Label srcDirLabel;
	private Scale minTokensScale;
	private Label unit;

	public ConfigurationDialog(Shell parent) {
		super(parent);
		super.setBlockOnOpen(true);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Clone Analysis");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(3, false);
		container.setLayout(layout);

		createSrcDir(container);
		createMinTokens(container);

		return container;
	}

	private void createSrcDir(final Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setText("Project directory: ");

		srcDirLabel = new Label(container, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		data.widthHint = 320;
		srcDirLabel.setLayoutData(data);

		Button browse = new Button(container, SWT.PUSH);
		browse.setText("Browse");
		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				ContainerSelectionDialog dialog = new ContainerSelectionDialog(
						getShell(), root, false, "Select project directory");
				if (dialog.open() == Window.OK) {
					Object[] result = dialog.getResult();
					if (result.length == 1) {
						srcDirLabel.setText(((Path) result[0]).toString()
								.substring(1));
						validate();
					}
				}
			}
		});

	}

	private void createMinTokens(Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setText("Minimum size of clones: ");

		GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		data.horizontalIndent = 1;
		label.setLayoutData(data);

		minTokensScale = new Scale(container, SWT.HORIZONTAL);
		minTokensScale.setMinimum(10);
		minTokensScale.setMaximum(100);
		minTokensScale.setSelection(50);
		// minTokensScale.setIncrement(5);
		// minTokensScale.setPageIncrement(10);
		minTokensScale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		minTokensScale.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				minTokens = "" + minTokensScale.getSelection();
				unit.setText(minTokens + " tokens");
			}
		});

		unit = new Label(container, SWT.NONE);
		unit.setText(minTokensScale.getSelection() + " tokens ");
		unit.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1,
				1));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Analyze", true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		validate();
	}

	@Override
	protected void okPressed() {
		srcDir = ResourcesPlugin.getWorkspace().getRoot().getLocation()
				.toString()
				+ "/" + srcDirLabel.getText();
		minTokens = "" + minTokensScale.getSelection();
		super.okPressed();
	}

	public String getSrcDir() {
		return srcDir;
	}

	public String getMinTokens() {
		return minTokens;
	}

	private void validate() {
		if (srcDirLabel.getText().isEmpty())
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		else
			getButton(IDialogConstants.OK_ID).setEnabled(true);
	}
}
