/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.eclipse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;

import kr.ac.kaist.jsaf.clone_refactor.core.MismatchVerifier;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneGroup;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneInstance;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.wst.jsdt.core.dom.ASTNode;

public class Utility {
	public static IFile getIFile(CloneInstance instance) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		Path location = new Path(instance.getFilename());
		return root.getFileForLocation(location);
	}

	public static IFile getIFile(File file) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		Path location = new Path(file.getAbsolutePath());
		return root.getFileForLocation(location);
	}

	public static IDocument getIDocument(IFile file) {
		TextFileDocumentProvider tdp = new TextFileDocumentProvider();
		try {
			tdp.connect(file);
		} catch (CoreException e) {
			UserInterface.printStackTraceToConsole(e);
		}
		return tdp.getDocument(file);
	}

	public static String readCloneToString(CloneInstance instance) {
		FileInputStream input;
		String str = "";
		int lineNumber = 0;

		try {
			input = new FileInputStream(new File(instance.getFilename()));
			CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
			decoder.onMalformedInput(CodingErrorAction.IGNORE);

			InputStreamReader reader = new InputStreamReader(input, decoder);
			BufferedReader bufferedReader = new BufferedReader(reader);
			StringBuilder sb = new StringBuilder();
			String line = bufferedReader.readLine();
			while (line != null) {
				lineNumber++;
				if (lineNumber >= instance.getStartLine()
						&& lineNumber <= instance.getEndLine())
					sb.append(line + "\n");
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
			return sb.toString();
		} catch (Exception e) {
			UserInterface.printStackTraceToConsole(e);
		}

		return str;
	}

	public static boolean isRefactorable(CloneGroup group) {
		ArrayList<CloneInstance> instances = group.getInstances();
		boolean refactorable = true;

		for (int i = 0; i < instances.size() - 1; i++) {
			for (int j = i + 1; j < instances.size(); j++) {
				CloneInstance instance1 = instances.get(i);
				CloneInstance instance2 = instances.get(j);

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
							return false;
						} else {
							try {
								if (!MismatchVerifier.match(node1, node2))
									return false;
							} catch (Exception e) {
								UserInterface.printStackTraceToConsole(e);
								return false;
							}
						}
					}
				} else
					return false;
			}
		}

		return refactorable;
	}
}
