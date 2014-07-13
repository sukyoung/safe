/*******************************************************************************
    Copyright 2008,2010, Oracle and/or its affiliates.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.astgen;

import edu.rice.cs.astgen.ASTModel;
import edu.rice.cs.astgen.NodeType;
import edu.rice.cs.astgen.TabPrintWriter;
import edu.rice.cs.astgen.Types.TypeName;

public class DepthFirstVisitorGenerator extends edu.rice.cs.astgen.DepthFirstVisitorGenerator {

    public DepthFirstVisitorGenerator(ASTModel ast) {
        super(ast);
    }

    protected void generateVisitor(NodeType root) {
        String visitorName = root.name() + "DepthFirstVisitor";
        TabPrintWriter writer = options.createJavaSourceInOutDir(visitorName);

        // Class header
        writer.startLine("/** A parametric abstract implementation of a visitor over " + root.name());
        writer.print(" that returns a value.");
        writer.startLine(" ** This visitor implements the visitor interface with methods that");
        writer.startLine(" ** first visit children, and then call forCASEOnly(), passing in");
        writer.startLine(" ** the values of the visits of the children. (CASE is replaced by the case name.)");
        writer.startLine(" ** By default, each of forCASEOnly delegates to a more general case; at the");
        writer.startLine(" ** top of this delegation tree is defaultCase(), which (unless overridden)");
        writer.startLine(" ** throws an exception.");
        writer.startLine(" **/");
        writer.startLine("@SuppressWarnings({\"unused\", \"unchecked\"})");
        writer.startLine("public abstract class " + visitorName + "<RetType>");
        if (options.usePLT) {
            writer.print(" extends " + root.name() + "VisitorLambda<RetType>");
        } else {
            writer.print(" implements " + root.name() + "Visitor<RetType>");
        }
        writer.print(" {");
        writer.indent();

        outputDefaultCaseMethod(writer, root);
        writer.println();

        // Write out forCASEOnly methods
        writer.startLine("/* Methods to handle a node after recursion. */");
        for (NodeType t : ast.descendents(root)) {
            outputForCaseOnly(t, writer, root);
        }
        writer.println();


        // Write implementation of visit methods
        writer.startLine("/** Methods to recur on each child. */");
        for (NodeType t : ast.descendents(root)) {
            if (!t.isAbstract()) {
                outputVisitMethod(t, writer, root);
            }
        }

        writer.println();
        outputRecurMethod(writer, root, "RetType");
        writer.println();

        // Output helpers, if necessary
        for (TypeName t : helpers()) {
            writer.println();
            generateHelper(t, writer, root);
        }
        clearHelpers();

        // output array seeds, if necessary
        for (TypeName t : this.arraySeeds()) {
            writer.println();
            generateArraySeed(t, writer);
        }
        clearArraySeeds();

        writer.unindent();
        writer.startLine("}");
        writer.println();
        writer.close();
    }

    @Override
    protected void outputDefaultCaseMethod(TabPrintWriter writer, NodeType root) {
        super.outputDefaultCaseMethod(writer, root);
    }
}
