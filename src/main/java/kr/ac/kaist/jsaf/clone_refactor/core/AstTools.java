/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import kr.ac.kaist.jsaf.clone_refactor.eclipse.AnalysisHandler;
import kr.ac.kaist.jsaf.clone_refactor.eclipse.UserInterface;
import kr.ac.kaist.jsaf.clone_refactor.eclipse.Utility;
import kr.ac.kaist.jsaf.clone_refactor.model.CloneInstance;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.jsdt.core.dom.AST;
import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.ASTParser;
import org.eclipse.wst.jsdt.core.dom.BooleanLiteral;
import org.eclipse.wst.jsdt.core.dom.JavaScriptUnit;
import org.eclipse.wst.jsdt.core.dom.NumberLiteral;
import org.eclipse.wst.jsdt.core.dom.RegularExpressionLiteral;
import org.eclipse.wst.jsdt.core.dom.StringLiteral;

public class AstTools {
	public static ASTNode parseToJSAst(CloneInstance instance) {
		JavaScriptUnit clone = parse(instance, false);
		Visitor functionVisitor = new Visitor();
		clone.accept(functionVisitor);
		ASTNode extractedFunction = functionVisitor.getTarget();

		JavaScriptUnit source = parse(instance, true);
		Visitor sourceVisitor = new Visitor(extractedFunction);
		source.accept(sourceVisitor);
		return sourceVisitor.getTarget();
	}

	public static JavaScriptUnit parse(CloneInstance instance,
			boolean fullSource) {
		String source = "";
		try {
			if (fullSource)
				source = new String(Files.readAllBytes(Paths.get(instance
						.getFilename())));
			else
				source = Utility.readCloneToString(instance);
		} catch (IOException ioe) {
			UserInterface.printStackTraceToConsole(ioe);
		}
		
		source = source.replaceAll("'", "\"");
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(source.toCharArray());
		parser.setUnitName("/" + instance.getCanonicalFilename());
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setStatementsRecovery(true);

		try {
			return (JavaScriptUnit)parser.createAST(new NullProgressMonitor());
		} catch (IllegalStateException ise) {
			UserInterface.printStackTraceToConsole(ise);
			return null;
		}
	}
	
	public static JavaScriptUnit parse(File file) {
		String source = "";
		try {
			source = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
		} catch (IOException ioe) {
			UserInterface.printStackTraceToConsole(ioe);
		}
		
		source = source.replaceAll("'", "\"");
		
		int length = AnalysisHandler.srcDir.length();
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(source.toCharArray());
		parser.setUnitName("/" + file.getAbsolutePath().substring(length + 1, file.getAbsolutePath().length()));
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setStatementsRecovery(true);

		try {
			return (JavaScriptUnit)parser.createAST(new NullProgressMonitor());
		} catch (IllegalStateException ise) {
			UserInterface.printStackTraceToConsole(ise);
			return null;
		}
	}

	public static boolean isLiteral(ASTNode node) {
		return node instanceof BooleanLiteral || node instanceof NumberLiteral
				|| node instanceof RegularExpressionLiteral
				|| node instanceof StringLiteral;
	}
}
