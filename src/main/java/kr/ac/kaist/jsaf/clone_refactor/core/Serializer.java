/*******************************************************************************
    Copyright (c) 2015, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.clone_refactor.core;

import java.util.ArrayList;

import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.ASTVisitor;
import org.eclipse.wst.jsdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.wst.jsdt.core.dom.ArrayAccess;
import org.eclipse.wst.jsdt.core.dom.ArrayCreation;
import org.eclipse.wst.jsdt.core.dom.ArrayInitializer;
import org.eclipse.wst.jsdt.core.dom.ArrayType;
import org.eclipse.wst.jsdt.core.dom.Assignment;
import org.eclipse.wst.jsdt.core.dom.Block;
import org.eclipse.wst.jsdt.core.dom.BlockComment;
import org.eclipse.wst.jsdt.core.dom.BooleanLiteral;
import org.eclipse.wst.jsdt.core.dom.BreakStatement;
import org.eclipse.wst.jsdt.core.dom.CatchClause;
import org.eclipse.wst.jsdt.core.dom.CharacterLiteral;
import org.eclipse.wst.jsdt.core.dom.ClassInstanceCreation;
import org.eclipse.wst.jsdt.core.dom.ConditionalExpression;
import org.eclipse.wst.jsdt.core.dom.ConstructorInvocation;
import org.eclipse.wst.jsdt.core.dom.ContinueStatement;
import org.eclipse.wst.jsdt.core.dom.DoStatement;
import org.eclipse.wst.jsdt.core.dom.EmptyStatement;
import org.eclipse.wst.jsdt.core.dom.EnhancedForStatement;
import org.eclipse.wst.jsdt.core.dom.ExpressionStatement;
import org.eclipse.wst.jsdt.core.dom.FieldAccess;
import org.eclipse.wst.jsdt.core.dom.FieldDeclaration;
import org.eclipse.wst.jsdt.core.dom.ForStatement;
import org.eclipse.wst.jsdt.core.dom.FunctionDeclaration;
import org.eclipse.wst.jsdt.core.dom.FunctionInvocation;
import org.eclipse.wst.jsdt.core.dom.FunctionRef;
import org.eclipse.wst.jsdt.core.dom.FunctionRefParameter;
import org.eclipse.wst.jsdt.core.dom.IfStatement;
import org.eclipse.wst.jsdt.core.dom.ImportDeclaration;
import org.eclipse.wst.jsdt.core.dom.InfixExpression;
import org.eclipse.wst.jsdt.core.dom.Initializer;
import org.eclipse.wst.jsdt.core.dom.InstanceofExpression;
import org.eclipse.wst.jsdt.core.dom.JSdoc;
import org.eclipse.wst.jsdt.core.dom.JavaScriptUnit;
import org.eclipse.wst.jsdt.core.dom.LabeledStatement;
import org.eclipse.wst.jsdt.core.dom.LineComment;
import org.eclipse.wst.jsdt.core.dom.MemberRef;
import org.eclipse.wst.jsdt.core.dom.NullLiteral;
import org.eclipse.wst.jsdt.core.dom.NumberLiteral;
import org.eclipse.wst.jsdt.core.dom.PackageDeclaration;
import org.eclipse.wst.jsdt.core.dom.ParenthesizedExpression;
import org.eclipse.wst.jsdt.core.dom.PostfixExpression;
import org.eclipse.wst.jsdt.core.dom.PrefixExpression;
import org.eclipse.wst.jsdt.core.dom.PrimitiveType;
import org.eclipse.wst.jsdt.core.dom.QualifiedName;
import org.eclipse.wst.jsdt.core.dom.QualifiedType;
import org.eclipse.wst.jsdt.core.dom.ReturnStatement;
import org.eclipse.wst.jsdt.core.dom.SimpleName;
import org.eclipse.wst.jsdt.core.dom.SimpleType;
import org.eclipse.wst.jsdt.core.dom.SingleVariableDeclaration;
import org.eclipse.wst.jsdt.core.dom.StringLiteral;
import org.eclipse.wst.jsdt.core.dom.SuperConstructorInvocation;
import org.eclipse.wst.jsdt.core.dom.SuperFieldAccess;
import org.eclipse.wst.jsdt.core.dom.SuperMethodInvocation;
import org.eclipse.wst.jsdt.core.dom.SwitchCase;
import org.eclipse.wst.jsdt.core.dom.SwitchStatement;
import org.eclipse.wst.jsdt.core.dom.TagElement;
import org.eclipse.wst.jsdt.core.dom.TextElement;
import org.eclipse.wst.jsdt.core.dom.ThisExpression;
import org.eclipse.wst.jsdt.core.dom.ThrowStatement;
import org.eclipse.wst.jsdt.core.dom.TryStatement;
import org.eclipse.wst.jsdt.core.dom.TypeDeclaration;
import org.eclipse.wst.jsdt.core.dom.TypeDeclarationStatement;
import org.eclipse.wst.jsdt.core.dom.TypeLiteral;
import org.eclipse.wst.jsdt.core.dom.VariableDeclarationExpression;
import org.eclipse.wst.jsdt.core.dom.VariableDeclarationFragment;
import org.eclipse.wst.jsdt.core.dom.VariableDeclarationStatement;
import org.eclipse.wst.jsdt.core.dom.WhileStatement;

public class Serializer extends ASTVisitor {
	private ArrayList<ASTNode> serializedNodes = new ArrayList<ASTNode>();
	
	@Override
	public boolean visit(AnonymousClassDeclaration node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ArrayAccess node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ArrayCreation node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ArrayInitializer node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ArrayType node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(Assignment node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(Block node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(BooleanLiteral node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(BreakStatement node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(CatchClause node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(CharacterLiteral node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ClassInstanceCreation node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(JavaScriptUnit node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ConditionalExpression node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ConstructorInvocation node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ContinueStatement node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(DoStatement node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(EmptyStatement node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ExpressionStatement node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(FieldAccess node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ForStatement node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(IfStatement node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ImportDeclaration node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(InfixExpression node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(Initializer node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(JSdoc node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(LabeledStatement node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(FunctionDeclaration node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(FunctionInvocation node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(NullLiteral node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(NumberLiteral node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(PackageDeclaration node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ParenthesizedExpression node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(PostfixExpression node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(PrefixExpression node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(PrimitiveType node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(QualifiedName node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ReturnStatement node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(SimpleName node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(SimpleType node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(SingleVariableDeclaration node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	public boolean visit(StringLiteral node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(SuperConstructorInvocation node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(SuperFieldAccess node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(SuperMethodInvocation node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(SwitchCase node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(SwitchStatement node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ThisExpression node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ThrowStatement node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(TryStatement node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(TypeDeclarationStatement node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(TypeLiteral node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(VariableDeclarationExpression node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(VariableDeclarationFragment node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(VariableDeclarationStatement node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(WhileStatement node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(InstanceofExpression node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(LineComment node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(BlockComment node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(TagElement node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(TextElement node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(MemberRef node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(FunctionRef node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(FunctionRefParameter node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(EnhancedForStatement node) {
		serializedNodes.add(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(QualifiedType node) {
		serializedNodes.add(node);
		return super.visit(node);
	}

	public ArrayList<ASTNode> getSerializedNodes() {
		return serializedNodes;
	}
}
