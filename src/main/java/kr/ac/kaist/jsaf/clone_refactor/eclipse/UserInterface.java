/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.eclipse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import kr.ac.kaist.jsaf.clone_refactor.model.CloneInstance;

import org.eclipse.compare.BufferedContent;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BlockTextSelection;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

public class UserInterface {
	private static MessageConsole msgConsole = findConsole("");
	public static MessageConsoleStream out = msgConsole.newMessageStream();

	private static MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	public static void showView(String id) {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().showView(id);
		} catch (PartInitException e) {
			printStackTraceToConsole(e);
		}
	}

	public static void openFileInEditor(CloneInstance instance) {
		File fileToOpen = new File(instance.getFilename());
		if (fileToOpen.exists() && fileToOpen.isFile()) {
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(
					fileToOpen.toURI());
			IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();

			try {
				IEditorPart editorPart = IDE.openEditorOnFileStore(page,
						fileStore);
				goToClone(editorPart, instance);
			} catch (PartInitException e) {
				printStackTraceToConsole(e);
			}
		}
	}

	private static void goToClone(IEditorPart editorPart, CloneInstance instance) {
		if ((editorPart instanceof ITextEditor) && instance.getStartLine() > 0) {
			ITextEditor editor = (ITextEditor) editorPart;
			IDocument document = editor.getDocumentProvider().getDocument(
					editor.getEditorInput());
			if (document != null) {
				BlockTextSelection selection = new BlockTextSelection(document,
						instance.getStartLine() - 1, 0, instance.getEndLine(),
						0, 4);
				editor.selectAndReveal(selection.getOffset(),
						selection.getLength());
			}
		}
	}

	public static void compareClones(CloneInstance left, CloneInstance right) {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		CompareInput compare = new CompareInput(left, right, "Clone Compare");
		CompareUI.openCompareEditorOnPage(compare, page);
	}

	private static class CompareInput extends CompareEditorInput {
		private CloneInstance left, right;
		CompareItem leftItem;
		CompareItem rightItem;

		public CompareInput(CloneInstance left, CloneInstance right,
				String title) {
			super(new CompareConfiguration());
			init(left, right, title);
		}

		private void init(CloneInstance left, CloneInstance right, String title) {
			this.left = left;
			this.right = right;
			setTitle(title);
			getCompareConfiguration().setLeftEditable(true);
			getCompareConfiguration().setRightEditable(true);
			getCompareConfiguration().setLeftLabel(
					left.getCanonicalFilename() + ":" + left.getStartLine()
							+ "-" + left.getEndLine());
			getCompareConfiguration().setRightLabel(
					right.getCanonicalFilename() + ":" + right.getStartLine()
							+ "-" + right.getEndLine());
			getCompareConfiguration().setProperty(CompareConfiguration.IGNORE_WHITESPACE, true);
		}

		protected Object prepareInput(IProgressMonitor pm) {
			leftItem = new CompareItem(left);
			rightItem = new CompareItem(right);

			return new DiffNode(leftItem, rightItem);
		}
	}

	private static class CompareItem extends BufferedContent implements
			ITypedElement {

		private String name;
		private File file;
		private CloneInstance instance;
		FileInputStream input;

		CompareItem(CloneInstance instance) {
			this.name = instance.getFilename();
			file = new File(name);
			this.instance = instance;
		}

		public InputStream getContents() throws CoreException {
			String code = Utility.readCloneToString(instance);
			InputStream is = new ByteArrayInputStream(code.getBytes());
			return is;
		}

		protected InputStream createStream() throws CoreException {
			try {
				input = new FileInputStream(file);
			} catch (FileNotFoundException e) {
			}

			return input;
		}

		public Image getImage() {
			return null;
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return ITypedElement.TEXT_TYPE;
		}
	}
	
	public static void printStackTraceToConsole(Exception e) {		
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		UserInterface.out.println(errors.toString());
	}
}
