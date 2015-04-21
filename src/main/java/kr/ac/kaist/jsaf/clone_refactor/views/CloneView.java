/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.views;

import java.util.ArrayList;
import java.util.Iterator;

import kr.ac.kaist.jsaf.clone_refactor.eclipse.UserInterface;
import kr.ac.kaist.jsaf.clone_refactor.model.AbstractModel;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneCollection;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneGroup;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneInstance;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IServiceLocator;

public class CloneView extends ViewPart {
	protected static TreeViewer treeViewer;
	protected Text text;
	protected CloneLabelProvider labelProvider;
	protected Action diffAction, pullupAction;
	protected static CloneCollection root;
	private static ArrayList<CloneInstance> selectedInstances = new ArrayList<CloneInstance>();
	private int selectedGroups = 0;

	/**
	 * The constructor.
	 */
	public CloneView() {
	}

	/*
	 * @see IWorkbenchPart#createPartControl(Composite)
	 */
	public void createPartControl(Composite parent) {
		/*
		 * Create a grid layout object so the text and treeviewer are layed out
		 * the way I want.
		 */
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 2;
		parent.setLayout(layout);

		/*
		 * Create a "label" to display information in. I'm using a text field
		 * instead of a lable so you can copy-paste out of it.
		 */
		text = new Text(parent, SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER);
		// layout the text field above the treeviewer
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		text.setLayoutData(layoutData);

		// Create the tree viewer as a child of the composite parent
		treeViewer = new TreeViewer(parent);
		treeViewer.setContentProvider(new CloneContentProvider());
		labelProvider = new CloneLabelProvider();
		treeViewer.setLabelProvider(labelProvider);

		treeViewer.setUseHashlookup(true);

		// layout the tree viewer below the text field
		layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		treeViewer.getControl().setLayoutData(layoutData);

		createActions();
		createMenus();
		hookListeners();

		treeViewer.setInput(CloneCollection.instance);
		treeViewer.expandAll();
	}

	private void createActions() {
		diffAction = new Action("Compare") {
			public void run() {
				if (CloneCollection.instance.size() == 0)
					MessageDialog.openWarning(null, "",
							"Please run clone detection first");
				else if (selectedGroups != 0)
					MessageDialog.openWarning(null, "",
							"Please select only code fragments");
				else if (getSelectedInstances().size() != 2)
					MessageDialog.openWarning(null, "",
							"Please select only two code fragments");
				else if (getSelectedInstances().get(0).getId() != getSelectedInstances()
						.get(1).getId())
					MessageDialog
							.openWarning(null, "",
									"Please select only code fragments from the same group");
				else
					UserInterface.compareClones(getSelectedInstances().get(0),
							getSelectedInstances().get(1));
			}
		};

		pullupAction = new Action("Pull Up Method") {
			public void run() {
				if (CloneCollection.instance.size() == 0)
					MessageDialog.openWarning(null, "",
							"Please run clone detection first");
				else if (selectedGroups != 0)
					MessageDialog.openWarning(null, "",
							"Please select only code fragments");
				else if (getSelectedInstances().size() < 2)
					MessageDialog.openWarning(null, "",
							"Please select at least two code fragments");
				else {
					boolean sameGroup = true;
					for (int i = 0; i < getSelectedInstances().size() - 1; i++) {
						for (int j = 1; j < getSelectedInstances().size(); j++) {
							if (i != j
									&& getSelectedInstances().get(i).getId() != getSelectedInstances()
											.get(j).getId())
								sameGroup = false;
						}
					}
					if (!sameGroup)
						MessageDialog
								.openWarning(null, "",
										"Please select code fragments from only the same group");
					else {
						IServiceLocator serviceLocator = PlatformUI.getWorkbench();
						ICommandService commandService = (ICommandService) serviceLocator
								.getService(ICommandService.class);

						Command command = commandService
								.getCommand("kr.ac.kaist.jsaf.clone_refactor.commands.pullupCommand");
						try {
							command.executeWithChecks(new ExecutionEvent());
						} catch (ExecutionException ee) {
							UserInterface.printStackTraceToConsole(ee);
						} catch (NotDefinedException nde) {
							UserInterface.printStackTraceToConsole(nde);
						} catch (NotEnabledException nee) {
							UserInterface.printStackTraceToConsole(nee);
						} catch (NotHandledException nhe) {
							UserInterface.printStackTraceToConsole(nhe);
						}
					}
				}
			}
		};
	}

	private void createMenus() {
		IMenuManager rootMenuManager = getViewSite().getActionBars()
				.getMenuManager();
		rootMenuManager.setRemoveAllWhenShown(true);
		rootMenuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillMenu(mgr);
			}
		});
		fillMenu(rootMenuManager);
	}

	protected void fillMenu(IMenuManager rootMenuManager) {
		rootMenuManager.add(diffAction);
		rootMenuManager.add(pullupAction);
	}

	protected void hookListeners() {
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				clearSelectedInstances();
				selectedGroups = 0;

				// if the selection is empty clear the label
				if (event.getSelection().isEmpty()) {
					text.setText("");

					return;
				}
				if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) event
							.getSelection();
					StringBuffer toShow = new StringBuffer();
					for (Iterator iterator = selection.iterator(); iterator
							.hasNext();) {
						Object domain = (AbstractModel) iterator.next();
						if (domain instanceof CloneInstance) {
							String value = labelProvider.getText(domain);
							toShow.append(value);
							toShow.append(", ");
							getSelectedInstances().add((CloneInstance) domain);
						} else if (domain instanceof CloneGroup) {
							selectedGroups++;
						}
					}
					// remove the trailing comma space pair
					if (toShow.length() > 0) {
						toShow.setLength(toShow.length() - 2);
					}
					text.setText(toShow.toString());
				}
			}
		});

		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) event
							.getSelection();
					for (Iterator iterator = selection.iterator(); iterator
							.hasNext();) {
						Object domain = (AbstractModel) iterator.next();
						if (domain instanceof CloneInstance) {
							UserInterface
									.openFileInEditor((CloneInstance) domain);
						} else if (domain instanceof CloneGroup) {
							if (treeViewer.getExpandedState(domain))
								treeViewer.collapseToLevel(domain,
										AbstractTreeViewer.ALL_LEVELS);
							else
								treeViewer.expandToLevel(domain,
										AbstractTreeViewer.ALL_LEVELS);
						}
					}
				}
			}
		});
	}

	/**
	 * Remove the selected domain object(s). If multiple objects are selected
	 * remove all of them.
	 * 
	 * If nothing is selected do nothing.
	 */
	protected void removeSelected() {
		if (treeViewer.getSelection().isEmpty()) {
			return;
		}
		IStructuredSelection selection = (IStructuredSelection) treeViewer
				.getSelection();
		/*
		 * Tell the tree to not redraw until we finish removing all the selected
		 * children.
		 */
		treeViewer.getTree().setRedraw(false);
		for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
			AbstractModel model = (AbstractModel) iterator.next();
			CloneCollection parent = (CloneCollection) model.getParent();
			parent.remove(model);
		}
		treeViewer.getTree().setRedraw(true);
	}

	/*
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
	}

	public static void refresh() {
		if (CloneCollection.instance != null) {
			treeViewer.setInput(CloneCollection.instance);
		}
	}

	public static ArrayList<CloneInstance> getSelectedInstances() {
		return selectedInstances;
	}

	public static void clearSelectedInstances() {
		selectedInstances.clear();
	}
}
