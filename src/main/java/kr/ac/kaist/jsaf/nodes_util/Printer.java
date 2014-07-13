/*******************************************************************************
    Copyright 2008,2009, Oracle and/or its affiliates.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util;

import edu.rice.cs.plt.tuple.Null;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.tuple.Wrapper;
import kr.ac.kaist.jsaf.nodes.*;
import kr.ac.kaist.jsaf.useful.Pair;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class Printer extends NodeReflection {
    boolean firstFieldOnNewLine;

    boolean oneLineVarRef;

    boolean skipEmpty = true;

    int shortListThreshold = 4;

    int oneLinerNesting;

    public final static String tilde = "~";

    public final static String nl = (System.getProperty("line.separator"));

    public Printer() {
        this(true, true, true);
    }

    public Printer(boolean firstFieldOnNewLine, boolean oneLineVarRef,
            boolean skipEmpty) {
        this.firstFieldOnNewLine = firstFieldOnNewLine;
        this.oneLineVarRef = oneLineVarRef;
        this.skipEmpty = skipEmpty;
    }

    Span lastSpan = null;

    static final String indentString = "                                                           "
            + "                                                           "
            + "                                                           "
            + "                                                           "
            + "                                                           "
            + "                                                           "
            + "                                                           "
            + "                                                           "
            + "                                                           ";

    void indent(int i, Appendable w) throws IOException {
        w.append("\n");
        w.append(indentString.substring(0, i));
    }

    public void dumpSpan(Span span, Appendable w) throws IOException {
        if (span == null) {
            return;
        }
        if (lastSpan == null) {
            span.appendTo(w, true, true);
            lastSpan = span;
            return;
        }
        boolean do_file = !(lastSpan.end.getFileName().equals(span.begin
                .getFileName()))
                || !(lastSpan.begin.getFileName().equals(span.begin
                        .getFileName()));

        if (do_file
                || lastSpan.begin.column() != span.begin.column()
                || lastSpan.end.column() != span.end.column()
                || lastSpan.begin.getLine() != span.begin.getLine()
                || lastSpan.end.getLine() != span.end.getLine()) {
            span.appendTo(w, do_file, true);
        }
        lastSpan = span;
    }

    private boolean allAtoms(List l) {
        for (int i = 0; i < l.size(); i++) {
            Object o = l.get(i);
            if (!(o instanceof String || o instanceof Number || o instanceof Boolean)) {
                return false;
            }
        }
        return true;
    }

    public void dump(Object o, Appendable w) throws IOException {
        dump(o, w, 0);
    }

    @SuppressWarnings("unchecked")
    public void dump(Object o, Appendable w, int indent) throws IOException {
        if (o == null) {
            w.append("_");
        } else if (o instanceof String) {
            String s = (String) o;
            // Always quote on output.
            w.append('"');
            w.append(Unprinter.enQuote(s));
            w.append('"');
        } else if (o instanceof Pair) {
            Pair p = (Pair) o;
            Object a = p.getA();
            Object b = p.getB();
            if (firstFieldOnNewLine) {
                w.append("(Pair");
                indent(indent + 1, w);
                dump(a, w, indent + 1);
            } else {
                w.append("(Pair ");
                dump(a, w, indent + 1);
            }
            indent(indent + 1, w);
            dump(b, w, indent + 1);
            w.append(")");
        } else if (o instanceof Double) {
            w.append(Double.toString(((Double)o).doubleValue()));
        } else if (o instanceof Number) {
            w.append(o.toString());
        } else if (o instanceof Boolean) {
            w.append(o.toString());
        } else if (o instanceof List) {
            List l = (List) o;
            w.append("[");
            if (l.size() < shortListThreshold && allAtoms(l)) {
                for (int k = 0; k < l.size(); k++) {

                    if (k > 0) {
                        w.append(" ");
                    }

                    dump(l.get(k), w, indent + 1);
                }
            } else {
                for (int k = 0; k < l.size(); k++) {
                    if (k > 0 || firstFieldOnNewLine) {
                        if (oneLineVarRef && oneLinerNesting > 0) {
                            if (k > 0) {
                                w.append(" ");
                            }
                        } else {
                            indent(indent + 1, w);
                        }
                    }
                    dump(l.get(k), w, indent + 1);
                }
            }
            w.append("]");

        } else if (o instanceof Wrapper) {
            w.append("(");
            w.append("Some");
            Field[] fields = getCachedPrintableFields(Wrapper.class);
            dumpFields(w, indent, o, true, fields, false);
            w.append(")");

        } else if (o instanceof Null) {
            w.append("(");
            w.append("Some");
            w.append(")");

        } else if (o instanceof Modifier){
            if (o instanceof PublicMod) w.append("(PublicMod)");
            else if (o instanceof PrivateMod) w.append("(PrivateMod)");
            else if (o instanceof StaticMod) w.append("(StaticMod)");

        } else if (o instanceof AbstractNode) {
            AbstractNode x = (AbstractNode) o;
            Class cl = x.getClass();
            String clname = cl.getSimpleName();
            int oneLinerNestingInc = (o instanceof Literal ||
                                      o instanceof VarRef) ? 1 : 0;
            oneLinerNesting += oneLinerNestingInc;

            boolean oneLiner = oneLineVarRef
                    && (oneLinerNesting > 0
                            || o instanceof Id || o instanceof Wrapper);
            Field[] fields = getCachedPrintableFields(cl, clname);
            w.append("(");
            w.append(clname);
            dumpSpan(NodeUtil.getSpan(x), w);
            dumpFields(w, indent, x, oneLiner, fields, true);
            w.append(")");
            oneLinerNesting -= oneLinerNestingInc;
        } else if (o instanceof ASTSpanInfo) {
            ASTSpanInfo x = (ASTSpanInfo) o;
            Class cl = x.getClass();
            String clname = cl.getSimpleName();
            Field[] fields = getCachedPrintableFields(cl, clname);
            w.append("(");
            w.append(clname);
            Option<Comment> comment = x.getComment();
            if (comment.isNone()) w.append("()");
            else {
                w.append("(");
                dump(comment.unwrap().getComment(), w);
                w.append(")");
            }
            w.append(")");
        } else if (o instanceof IRAbstractNode) {
            IRAbstractNode x = (IRAbstractNode) o;
            Class cl = x.getClass();
            String clname = cl.getSimpleName();
            int oneLinerNestingInc = (o instanceof IRId) ? 1 : 0;
            oneLinerNesting += oneLinerNestingInc;

            boolean oneLiner = oneLineVarRef
                    && (oneLinerNesting > 0
                            || o instanceof IRId || o instanceof Wrapper);
            Field[] fields = getCachedPrintableFields(cl, clname);
            w.append("(");
            w.append(clname);
            dumpSpan(NodeUtil.getSpan(x), w);
            dumpFields(w, indent, x, oneLiner, fields, true);
            w.append(")");
            oneLinerNesting -= oneLinerNestingInc;
        } else if (o instanceof IRExpr) {
            IRExpr x = (IRExpr) o;
            Class cl = x.getClass();
            String clname = cl.getSimpleName();
            int oneLinerNestingInc = (o instanceof IRId) ? 1 : 0;
            oneLinerNesting += oneLinerNestingInc;

            boolean oneLiner = oneLineVarRef
                    && (oneLinerNesting > 0
                            || o instanceof IRId || o instanceof Wrapper);
            Field[] fields = getCachedPrintableFields(cl, clname);
            w.append("(");
            w.append(clname);
            dumpFields(w, indent, x, oneLiner, fields, true);
            w.append(")");
            oneLinerNesting -= oneLinerNestingInc;
        } else if (o instanceof IROp) {
            IROp x = (IROp) o;
            Class cl = x.getClass();
            String clname = cl.getSimpleName();
            int oneLinerNestingInc = (o instanceof IRId) ? 1 : 0;
            oneLinerNesting += oneLinerNestingInc;

            boolean oneLiner = oneLineVarRef
                    && (oneLinerNesting > 0
                            || o instanceof IRId || o instanceof Wrapper);
            Field[] fields = getCachedPrintableFields(cl, clname);
            w.append("(");
            w.append(clname);
            dumpFields(w, indent, x, oneLiner, fields, true);
            w.append(")");
            oneLinerNesting -= oneLinerNestingInc;
        } else if (o instanceof IRId) {
            IRId x = (IRId) o;
            Class cl = x.getClass();
            String clname = cl.getSimpleName();
            int oneLinerNestingInc = 1;
            oneLinerNesting += oneLinerNestingInc;

            boolean oneLiner = true;
            Field[] fields = getCachedPrintableFields(cl, clname);
            w.append("(");
            w.append(clname);
            dumpFields(w, indent, x, oneLiner, fields, true);
            w.append(")");
            oneLinerNesting -= oneLinerNestingInc;
        } else if (o instanceof IRNumber) {
            IRNumber x = (IRNumber) o;
            Class cl = x.getClass();
            String clname = cl.getSimpleName();
            int oneLinerNestingInc = 1;
            oneLinerNesting += oneLinerNestingInc;

            boolean oneLiner = true;
            Field[] fields = getCachedPrintableFields(cl, clname);
            w.append("(");
            w.append(clname);
            dumpFields(w, indent, x, oneLiner, fields, true);
            w.append(")");
            oneLinerNesting -= oneLinerNestingInc;
        } else if (o instanceof IRSpanInfo) {
            IRSpanInfo x = (IRSpanInfo) o;
            Class cl = x.getClass();
            String clname = cl.getSimpleName();
            int oneLinerNestingInc = (o instanceof VarRef || o instanceof Op) ? 1 : 0;
            oneLinerNesting += oneLinerNestingInc;

            boolean oneLiner = oneLineVarRef
                    && (oneLinerNesting > 0 || o instanceof Op || o instanceof Id);
            Field[] fields = getCachedPrintableFields(cl, clname);
            w.append("(");
            w.append(clname);
            dumpSpan(x.getSpan(), w);
            w.append(")");
            oneLinerNesting -= oneLinerNestingInc;
        } else if (o instanceof ScopeBody) {
            ScopeBody x = (ScopeBody) o;
            Class cl = x.getClass();
            String clname = cl.getSimpleName();
            int oneLinerNestingInc = (o instanceof VarRef || o instanceof Op) ? 1 : 0;
            oneLinerNesting += oneLinerNestingInc;

            boolean oneLiner = oneLineVarRef
                    && (oneLinerNesting > 0 || o instanceof Op || o instanceof Id);
            Field[] fields = getCachedPrintableFields(cl, clname);
            w.append("(");
            w.append(clname);
            dumpFields(w, indent, x, oneLiner, fields, true);
            w.append(")");
            oneLinerNesting -= oneLinerNestingInc;
        } else if (o instanceof IRInfoNode) {
            IRInfoNode x = (IRInfoNode) o;
            Class cl = x.getClass();
            String clname = cl.getSimpleName();
            int oneLinerNestingInc = (o instanceof VarRef || o instanceof Op) ? 1 : 0;
            oneLinerNesting += oneLinerNestingInc;

            boolean oneLiner = oneLineVarRef
                    && (oneLinerNesting > 0 || o instanceof Op || o instanceof Id);
            Field[] fields = getCachedPrintableFields(cl, clname);
            w.append("(");
            w.append(clname);
            dumpFields(w, indent, x, oneLiner, fields, true);
            w.append(")");
            oneLinerNesting -= oneLinerNestingInc;
        } else if ( o instanceof Map ){
            w.append("(Map ");
            java.util.Set<Map.Entry> set = ((Map) o ).entrySet();
            for ( Map.Entry entry : set ){
                w.append("!");
                w.append( entry.getKey().toString() );
                w.append( "=" );
                dump( entry.getValue(), w, indent );
            }
            w.append(")");
        } else {
            w.append("?" + o.getClass().getName());
        }
    }

    /**
     * @param w
     * @param indent
     * @param x
     * @param oneLiner
     * @param fields
     * @throws IOException
     */
    private void dumpFields(Appendable w, int indent, Object x,
            boolean oneLiner, Field[] fields, boolean skipThisEmpty)
            throws IOException {
        for (int j = 0; j < fields.length; j++) {
            Field f = fields[j];

            try {
                Object p = f.get(x);
                if (skipEmpty
                    && skipThisEmpty
                    && (p instanceof List && ((List) p).size() == 0 ||
                        p instanceof Null<?> ||
                                // AHA! A bug, because the default value for refs is a non-zero lexical depth.
                                // p instanceof Integer && ((Integer) p).intValue() == 0 ||
                        p instanceof Boolean && ((Boolean) p).booleanValue() == false)) {
                    /* do nothing */
                } else if (x instanceof Span) {
                    /* do nothing */
                } else {
                    if (oneLiner) {
                        w.append(" ");
                    } else {
                        indent(indent + 1, w);
                    }
                    w.append(f.getName());
                    w.append("=");
                    dump(p, w, indent + 1);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Constructor defaultConstructorFor(Class cl)
            throws NoSuchMethodException {
        return null;
    }
}
