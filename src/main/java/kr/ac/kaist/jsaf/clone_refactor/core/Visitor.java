/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.core;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.wst.jsdt.core.dom.ASTMatcher;
import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.ASTVisitor;
import org.eclipse.wst.jsdt.core.dom.BooleanLiteral;
import org.eclipse.wst.jsdt.core.dom.FunctionDeclaration;
import org.eclipse.wst.jsdt.core.dom.FunctionExpression;
import org.eclipse.wst.jsdt.core.dom.FunctionInvocation;
import org.eclipse.wst.jsdt.core.dom.NumberLiteral;
import org.eclipse.wst.jsdt.core.dom.StringLiteral;

public class Visitor extends ASTVisitor {
	private ASTMatcher matcher = new ASTMatcher();
	private ASTNode target, candidate = null;
	private ArrayList<ASTNode> targets = new ArrayList<ASTNode>();
	private ArrayList<String> functionNames = null;
	private String functionName = "";

	public Visitor() {
	}

	Visitor(ASTNode node) {
		candidate = node;
	}

	public Visitor(String name) {
		functionName = name;
	}

	public Visitor(ArrayList<String> names) {
		functionNames = names;
	}

	public ASTNode getTarget() {
		return target;
	}

	public ArrayList<ASTNode> getTargets() {
		return targets;
	}

	public boolean visit(BooleanLiteral node) {
		if (matcher.safeSubtreeMatch(node, candidate))
			targets.add(node);
		return super.visit(node);
	}

	@Override
	public boolean visit(FunctionDeclaration node) {
		if (target == null) {
			if (candidate == null)
				target = node;
			else if (!functionName.isEmpty()) {
				if (node.getName().getIdentifier().equals(functionName))
					target = node;
			} else {
				String nodeStringStripped = strip(node.toString());
				String candidateStringStripped = strip(candidate.toString());
				if (nodeStringStripped.equals(candidateStringStripped))
					target = node;
			}
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(FunctionExpression node) {
		if (target == null) {
			if (candidate == null)
				target = node;
			else {
				String nodeStringStripped = strip(node.toString());
				String candidateStringStripped = strip(candidate.toString());
				if (nodeStringStripped.equals(candidateStringStripped))
					target = node;
			}
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(FunctionInvocation node) {
		if (functionNames != null) {
			for (int i = 0; i < functionNames.size(); i++) {
				String name = functionNames.get(i);

				if (node.getName() != null
						&& node.getName().getIdentifier() != null)
					if (node.getName().getIdentifier().equals(name))
						targets.add(node);
			}
		}
		return super.visit(node);
	}

	public boolean visit(NumberLiteral node) {
		if (matcher.safeSubtreeMatch(node, candidate))
			targets.add(node);
		return super.visit(node);
	}

	public boolean visit(StringLiteral node) {
		if (matcher.safeSubtreeMatch(node, candidate))
			targets.add(node);
		return super.visit(node);
	}

	private String strip(String str) {
		Pattern pattern = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
		return pattern.matcher(str).replaceAll("").replaceAll("\\s+", "");
	}
}
