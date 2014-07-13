/*******************************************************************************
    Copyright 2008,2010, Oracle and/or its affiliates.
    All rights reserved.


    Use is subject to license terms.

    This distribution may include materials developed by third parties.

 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util;

import java.util.*;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.tuple.OptionVisitor;

import kr.ac.kaist.jsaf.nodes.*;
import kr.ac.kaist.jsaf.useful.*;

public class ErrorMsgMaker extends NodeAbstractVisitor<String> {
    public static final ErrorMsgMaker ONLY = new ErrorMsgMaker();

    public static String errorMsg(Object... messages) {
        StringBuilder fullMessage = new StringBuilder();
        for (Object message : messages) {
            if (message instanceof AbstractNode) {
                fullMessage.append(makeErrorMsg((AbstractNode)message));
            }
            else {
                fullMessage.append(message.toString());
            }
        }
        return fullMessage.toString();
    }

    public static String makeErrorMsg(AbstractNode node) {
        return node.accept(ErrorMsgMaker.ONLY);
    }

    private ErrorMsgMaker() {}

    public final List<String> mapSelf(List<? extends AbstractNode> that) {
        LinkedList<String> result = new LinkedList<String>();
        for (AbstractNode elt : that) {
            result.add(elt.accept(this));
        }
        return result;
    }

    private final String acceptIfPresent(Option<? extends Node> possibleNode) {
        return possibleNode.apply(new OptionVisitor<Node, String>() {
            public String forSome(Node n) { return n.accept(ErrorMsgMaker.this); }
            public String forNone() { return ""; }
        });
    }

    public String forAbstractNode(AbstractNode node) {
        return node.getClass().getSimpleName() + " at " + NodeUtil.getSpan(node).begin.at();
    }
}
