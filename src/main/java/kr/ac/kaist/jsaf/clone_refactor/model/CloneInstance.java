/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.model;

import java.util.ArrayList;

import kr.ac.kaist.jsaf.clone_refactor.core.AstTools;
import kr.ac.kaist.jsaf.clone_refactor.core.Serializer;
import kr.ac.kaist.jsaf.clone_refactor.eclipse.AnalysisHandler;
import kr.ac.kaist.jsaf.clone_refactor.eclipse.UserInterface;

import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.JavaScriptUnit;

public class CloneInstance extends AbstractModel {
	private int id;
	private String filename;
	private String canonicalFilename;
	private int startLine;
	private int endLine;
	private ArrayList<ASTNode> mismatches;
	private ASTNode astNode;
	private JavaScriptUnit fullAST;
	private ArrayList<ASTNode> serializedTree;

	protected static ArrayList<CloneInstance> newInstances = new ArrayList<CloneInstance>();
	protected static int cursor = 0;

	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitCloneInstance(this, passAlongArgument);
	}

	public CloneInstance(String filename, int startLine, int endLine) {
		setFilename(filename);
		setStartLine(startLine);
		setEndLine(endLine);
		setCanonicalFilename(filename.substring(
				AnalysisHandler.srcDir.length() + 1,
				filename.length()));
		mismatches = new ArrayList<ASTNode>();

		parse();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}

	public boolean contains(CloneInstance instance) {
		if (filename == null)
			return false;
		return (filename.equals(instance.getFilename())
				&& startLine <= instance.getStartLine() && endLine >= instance
				.getEndLine()) ? true : false;
	}

	public static CloneInstance newInstance() {
		CloneInstance newInstance = (CloneInstance) newInstances.get(cursor);
		cursor = ((cursor + 1) % newInstances.size());
		return newInstance;
	}

	public String getCanonicalFilename() {
		return canonicalFilename;
	}

	public void setCanonicalFilename(String canonicalFilename) {
		this.canonicalFilename = canonicalFilename;
	}

	public void addMismatch(ASTNode node) {
		if (!mismatches.contains(node))
			mismatches.add(node);
	}

	public void addMismatches(ArrayList<ASTNode> nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			ASTNode node = nodes.get(i);
			addMismatch(node);
		}
	}

	public void clearMismatches() {
		mismatches.clear();
	}

	public ArrayList<ASTNode> getMismatches() {
		return mismatches;
	}

	public ASTNode getAstNode() {
		return astNode;
	}

	public void setAstNode(ASTNode astNode) {
		this.astNode = astNode;
	}

	public ArrayList<ASTNode> getSerializedTree() {
		return serializedTree;
	}

	public void setSerializedTree(ArrayList<ASTNode> serializedTree) {
		this.serializedTree = serializedTree;
	}
	
	public void parse() {
		try {
			fullAST = AstTools.parse(this, true);
			ASTNode ast = AstTools.parseToJSAst(this);
			if (ast != null) {
				setAstNode(ast);
				Serializer serializer = new Serializer();
				ast.accept(serializer);
				setSerializedTree(serializer.getSerializedNodes());
			}
		} catch (Exception e) {
			UserInterface.printStackTraceToConsole(e);
		}
	}

	public JavaScriptUnit getFullAST() {
		return fullAST;
	}

	public void setFullAST(JavaScriptUnit fullAST) {
		this.fullAST = fullAST;
	}
}
