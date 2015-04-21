/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kr.ac.kaist.jsaf.clone_refactor.eclipse.AnalysisHandler;
import kr.ac.kaist.jsaf.clone_refactor.eclipse.UserInterface;
import kr.ac.kaist.jsaf.clone_refactor.eclipse.Utility;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneInstance;
import kr.ac.kaist.jsaf.clone_refactor.views.CloneView;
import kr.ac.kaist.jsaf.useful.Pair;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.MultiStateTextFileChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditGroup;
import org.eclipse.wst.jsdt.core.dom.AST;
import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.ArrayAccess;
import org.eclipse.wst.jsdt.core.dom.Block;
import org.eclipse.wst.jsdt.core.dom.FunctionDeclaration;
import org.eclipse.wst.jsdt.core.dom.FunctionExpression;
import org.eclipse.wst.jsdt.core.dom.FunctionInvocation;
import org.eclipse.wst.jsdt.core.dom.InfixExpression;
import org.eclipse.wst.jsdt.core.dom.JavaScriptUnit;
import org.eclipse.wst.jsdt.core.dom.ObjectLiteral;
import org.eclipse.wst.jsdt.core.dom.ObjectLiteralField;
import org.eclipse.wst.jsdt.core.dom.ReturnStatement;
import org.eclipse.wst.jsdt.core.dom.SimpleName;
import org.eclipse.wst.jsdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.wst.jsdt.core.dom.rewrite.ListRewrite;

public class PullUpMethodRefactoring extends Refactoring {
	private TextEditGroup editGroup;
	private CompositeChange compositeChange;
	private ArrayList<Pair<Integer, String>> argMap;

	public PullUpMethodRefactoring() {
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor monitor)
			throws CoreException, OperationCanceledException {
		RefactoringStatus status = new RefactoringStatus();
		try {
			monitor.beginTask("Checking preconditions...", 1);
		} finally {
			monitor.done();
		}
		return status;
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor monitor)
			throws CoreException, OperationCanceledException {
		RefactoringStatus status = new RefactoringStatus();
		return status;
	}

	@Override
	public Change createChange(IProgressMonitor monitor) throws CoreException,
			OperationCanceledException {
		long startTime = System.currentTimeMillis();

		ArrayList<CloneInstance> selectedInstances = CloneView
				.getSelectedInstances();

		compositeChange = new CompositeChange("Pull Up Method");
		compositeChange.markAsSynthetic();
		argMap = new ArrayList<Pair<Integer, String>>();
		int selectedIndex = FunctionDeclarationProperty.selectedIndex;
		CloneInstance baseInstance = selectedInstances.get(selectedIndex);
		ASTNode baseNode = baseInstance.getAstNode();
		JavaScriptUnit sourceASTNode = AstTools.parse(baseInstance, true);
		AST sourceAST = sourceASTNode.getAST();

		ASTRewrite rewriter = ASTRewrite.create(sourceAST);
		ListRewrite lrw = null;
		editGroup = new TextEditGroup("");

		for (int i = selectedInstances.size() - 1; i >= 0; i--) {
			CloneInstance instance = selectedInstances.get(i);

			if (instance.getAstNode() instanceof FunctionExpression)
				replaceFunctionExpressionByInvocation(instance);
		}

		if (baseInstance.getAstNode() instanceof FunctionDeclaration)
			updateFunctionInvocation(selectedInstances);

		SimpleName functionName = sourceAST
				.newSimpleName(FunctionDeclarationProperty.functionName);
		FunctionDeclaration function = sourceAST.newFunctionDeclaration();
		function.setName(functionName);

		ReturnStatement returnStatement = sourceAST.newReturnStatement();
		FunctionExpression expression = null;
		if (baseNode instanceof FunctionExpression) {
			expression = (FunctionExpression) ASTNode.copySubtree(sourceAST,
					baseNode);
			returnStatement.setExpression(expression);
		} else if (baseNode instanceof FunctionDeclaration) {
			expression = sourceAST.newFunctionExpression();
			FunctionDeclaration declaration = (FunctionDeclaration) ASTNode
					.copySubtree(sourceAST, baseNode);
			expression.setMethod(declaration);
		}

		addFunctionArguments(baseInstance, expression, sourceAST, function,
				rewriter);

		if (baseNode instanceof FunctionExpression) {
			Block body = sourceAST.newBlock();
			lrw = rewriter.getListRewrite(body, Block.STATEMENTS_PROPERTY);
			lrw.insertFirst(returnStatement, editGroup);
			function.setBody(body);
		} else if (baseNode instanceof FunctionDeclaration) {
			Block body = (Block) ASTNode.copySubtree(sourceAST, expression
					.getMethod().getBody());
			lrw = rewriter.getListRewrite(function,
					FunctionDeclaration.PARAMETERS_PROPERTY);
			List<?> parameters = expression.getMethod().parameters();
			for (int i = 0; i < parameters.size(); i++) {
				ASTNode node = (ASTNode) parameters.get(i);
				lrw.insertLast(node, editGroup);
			}
			function.setBody(body);
		}

		lrw = rewriter.getListRewrite(sourceASTNode,
				JavaScriptUnit.STATEMENTS_PROPERTY);
		lrw.insertFirst(function, editGroup);

		createTextChange(baseInstance, rewriter,
				"Create new function definition", false);

		long stopTime = System.currentTimeMillis();

		UserInterface.out.println("Time for refactoring: "
				+ (stopTime - startTime) + "ms");
		return compositeChange;
	}

	@Override
	public String getName() {
		return "Pull Up Method";
	}

	private void addFunctionArguments(CloneInstance baseInstance,
			FunctionExpression expression, AST sourceAST,
			FunctionDeclaration function, ASTRewrite rewriter) {
		ArrayList<ASTNode> baseMismatches = baseInstance.getMismatches();

		for (int i = 0; i < baseMismatches.size(); i++) {
			ASTNode node = baseMismatches.get(i);
			Visitor visitor = new Visitor(node);
			expression.accept(visitor);

			ArrayList<ASTNode> targets = visitor.getTargets();

			for (int j = 0; j < targets.size(); j++) {
				ASTNode target = targets.get(j);

				if (AstTools.isLiteral(target)) {
					int argCount = computeArgCount(argMap, target.toString());

					Pair<Integer, String> argPair = new Pair<Integer, String>(
							argCount, target.toString());

					boolean containsMapping = argMap.contains(argPair);

					SimpleName simpleName = sourceAST.newSimpleName("arg"
							+ argCount);

					ListRewrite lrw = rewriter.getListRewrite(function,
							FunctionDeclaration.PARAMETERS_PROPERTY);

					if (!containsMapping)
						lrw.insertLast(simpleName, editGroup);

					ASTNode parent = target.getParent();
					if (parent instanceof ArrayAccess) {
						rewriter.replace(target, simpleName, editGroup);
						if (!containsMapping)
							argMap.add(argPair);
					} else if (parent instanceof FunctionInvocation) {
						lrw = rewriter.getListRewrite(parent,
								FunctionInvocation.ARGUMENTS_PROPERTY);
						lrw.replace(target, simpleName, editGroup);
						if (!containsMapping)
							argMap.add(argPair);
					} else if (parent instanceof InfixExpression) {
						rewriter.replace(target, simpleName, editGroup);
						if (!containsMapping)
							argMap.add(argPair);
					}
				}
			}
		}
	}

	private void replaceFunctionExpressionByInvocation(CloneInstance instance) {
		ArrayList<ASTNode> mismatches = instance.getMismatches();

		ASTNode instanceASTNode = instance.getAstNode();
		AST ast = instanceASTNode.getAST();
		ASTRewrite cloneRewriter = ASTRewrite.create(ast);
		FunctionInvocation invocation = ast.newFunctionInvocation();
		invocation.setName(ast
				.newSimpleName(FunctionDeclarationProperty.functionName));
		ListRewrite lrw = cloneRewriter.getListRewrite(invocation,
				FunctionInvocation.ARGUMENTS_PROPERTY);
		ArrayList<String> args = new ArrayList<String>();

		for (int j = 0; j < mismatches.size(); j++) {
			ASTNode node = mismatches.get(j);
			if (!args.contains(node.toString())) {
				lrw.insertLast(node, editGroup);
			}
			args.add(node.toString());
		}

		ASTNode parent = instanceASTNode.getParent();
		if (parent instanceof ObjectLiteralField) {
			ASTNode objLiteral = parent.getParent();
			AST objLiteralAST = objLiteral.getAST();
			ObjectLiteralField field = objLiteralAST.newObjectLiteralField();
			SimpleName fieldName = objLiteralAST
					.newSimpleName(((SimpleName) ((ObjectLiteralField) parent)
							.getFieldName()).getIdentifier());
			field.setFieldName(fieldName);
			field.setInitializer(invocation);
			ListRewrite flrw = cloneRewriter.getListRewrite(objLiteral,
					ObjectLiteral.FIELDS_PROPERTY);
			flrw.replace(parent, field, editGroup);
		} else
			cloneRewriter.replace(instanceASTNode, invocation, editGroup);
		createTextChange(instance, cloneRewriter,
				"Replace function expression by function invocation", true);
	}

	private void updateFunctionInvocation(
			ArrayList<CloneInstance> selectedInstances) {
		ArrayList<String> functionNames = new ArrayList<String>();

		for (int i = 0; i < selectedInstances.size(); i++) {
			CloneInstance instance = selectedInstances.get(i);
			FunctionDeclaration declaration = (FunctionDeclaration) (instance
					.getAstNode());
			String name = declaration.getName().getIdentifier();

			if (!functionNames.contains(name))
				functionNames.add(name);
		}

		File root = new File(AnalysisHandler.srcDir);
		ArrayList<File> fileList = new ArrayList<File>();
		ArrayList<Pair<File, ASTNode>> invocations = new ArrayList<Pair<File, ASTNode>>();
		findSourceFiles(root, fileList);

		for (int i = 0; i < fileList.size(); i++) {
			File file = fileList.get(i);
			try {
				ASTNode source = AstTools.parse(file);
				Visitor invocationVisitor = new Visitor(functionNames);
				source.accept(invocationVisitor);
				ArrayList<ASTNode> targets = invocationVisitor.getTargets();
				if (!targets.isEmpty()) {
					for (int j = 0; j < targets.size(); j++) {
						invocations.add(new Pair<File, ASTNode>(file, targets
								.get(j)));
					}
				}
			} catch (Exception e) {
				UserInterface.printStackTraceToConsole(e);
			}
		}

		for (int i = 0; i < invocations.size(); i++) {
			FunctionInvocation invocation = (FunctionInvocation) invocations
					.get(i).getB();
			AST ast = invocation.getAST();
			ASTRewrite invocationRewriter = ASTRewrite.create(ast);
			FunctionInvocation newInvocation = ast.newFunctionInvocation();
			newInvocation.setName(ast
					.newSimpleName(FunctionDeclarationProperty.functionName));
			ListRewrite lrw = invocationRewriter.getListRewrite(newInvocation,
					FunctionInvocation.ARGUMENTS_PROPERTY);
			List<?> arguments = invocation.arguments();
			for (int j = 0; j < arguments.size(); j++) {
				lrw.insertLast((ASTNode) arguments.get(j), editGroup);
			}

			for (int j = 0; j < argMap.size(); j++) {
				SimpleName arg = ast.newSimpleName(argMap.get(j).getB());
				lrw.insertLast(arg, editGroup);
			}

			invocationRewriter.replace(invocation, newInvocation, editGroup);
			createTextChange(invocations.get(i).getA(), invocation,
					invocationRewriter, "Update function invocation");
		}
	}

	private void createTextChange(CloneInstance instance, ASTRewrite rewriter,
			String message, boolean showLineNumber) {
		IFile file = Utility.getIFile(instance);
		TextFileChange change = createTextFileChange(file, rewriter);

		JavaScriptUnit instanceAST = AstTools.parse(instance, true);
		String changeName = instance.getCanonicalFilename();
		if (showLineNumber) {
			ASTNode node = instance.getAstNode();
			ASTNode parent = node.getParent();
			int startPosition = -1;

			if (parent instanceof ObjectLiteralField)
				startPosition = ((ObjectLiteralField) parent).getFieldName()
						.getStartPosition();
			else
				startPosition = node.getStartPosition();
			int startLine = instanceAST.getLineNumber(startPosition);

			changeName += ":" + startLine;
		}
		changeName += " (" + message + ")";

		addMultiStateChange(changeName, file, change);
	}

	private void createTextChange(File source, ASTNode node,
			ASTRewrite rewriter, String message) {
		IFile file = Utility.getIFile(source);
		TextFileChange change = createTextFileChange(file, rewriter);

		JavaScriptUnit instanceAST = AstTools.parse(source);
		int length = AnalysisHandler.srcDir.length();
		String changeName = source.getAbsolutePath().substring(length + 1,
				source.getAbsolutePath().length());

		int startPosition = node.getStartPosition();
		int startLine = instanceAST.getLineNumber(startPosition);

		changeName += ":" + startLine;
		changeName += " (" + message + ")";

		addMultiStateChange(changeName, file, change);
	}

	private TextFileChange createTextFileChange(IFile file, ASTRewrite rewriter) {
		IDocument document = Utility.getIDocument(file);
		TextEdit edits = rewriter.rewriteAST(document, null);
		editGroup.addTextEdit(edits);
		TextFileChange change = new TextFileChange("", file);
		change.setEdit(edits);
		return change;
	}
	
	private void addMultiStateChange(String changeName, IFile file, TextFileChange change) {
		MultiStateTextFileChange multiChange = new MultiStateTextFileChange(
				changeName, file);
		multiChange.addChange(change);
		compositeChange.add(multiChange);
	}
	
	private int computeArgCount(ArrayList<Pair<Integer, String>> argMap,
			String target) {
		for (int i = 0; i < argMap.size(); i++) {
			Pair<Integer, String> arg = argMap.get(i);
			if (arg.getB().equals(target))
				return arg.getA();
		}
		return argMap.size() + 1;
	}

	private void findSourceFiles(File dir, ArrayList<File> fileList) {
		File[] files = dir.listFiles();

		for (File file : files) {
			if (file.isDirectory())
				findSourceFiles(file, fileList);
			else if (file.getName().endsWith(".js"))
				fileList.add(file);
		}
	}
}
