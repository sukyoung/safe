/*******************************************************************************
    Copyright 2008,2009, Oracle and/or its affiliates.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.astgen;

import edu.rice.cs.astgen.ASTModel;
import edu.rice.cs.astgen.NodeType;
import edu.rice.cs.astgen.TabPrintWriter;

public class DepthFirstVoidVisitorGenerator extends edu.rice.cs.astgen.DepthFirstVoidVisitorGenerator {

    public DepthFirstVoidVisitorGenerator(ASTModel ast) {
        super(ast);
    }

    protected void outputDelegatingForCase(NodeType t, TabPrintWriter writer, NodeType root, String retType, String suff, String defaultMethod) {
        super.outputDelegatingForCase(t, writer, root, retType, suff, defaultMethod);
    }

    protected void outputDefaultCaseVoidMethod(TabPrintWriter writer, NodeType root) {
        super.outputDefaultCaseVoidMethod(writer, root);
        writer.println();
    }

    protected void outputVisitMethod(NodeType t, TabPrintWriter writer, NodeType root) {
        super.outputVisitMethod(t, writer, root);
    }
}
