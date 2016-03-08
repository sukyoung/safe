/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.nodes_util;

import edu.rice.cs.plt.tuple.Option;
import kr.ac.kaist.safe.nodes.*;
import kr.ac.kaist.safe.safe_util.*;
import kr.ac.kaist.safe.useful.JUseful;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.*;

import static kr.ac.kaist.safe.exceptions.SAFEError.error;

/**
 * A collection of methods for converting from a readable AST to an AST data
 * structure. It assumes input generated by the companion class Printer.
 *
 * The Unprinter uses reflection to allocate kr.ac.kaist.safe.nodes and fill in fields. The first
 * element X in an S-sexpression is assumed to be a class name, which is assumed
 * to denote the class kr.ac.kaist.safe.nodes.X.
 *
 * Fields are specified by name, and data types are indicated either by
 * reflected field information, or by syntactic queues in the input (double
 * quotes, square brackets, at signs, and so on). For many types, there is a
 * canonical value for missing information, zero, false, empty string, empty
 * list, absent, and so on, so that the input can be less verbose. In
 * particular, a missing location is supposed to be the same as the previous
 * location.
 *
 * Sample input follows. The first line indicates the beginning of a component,
 * and further (after the "@") supplies location information. The fields of a
 * component include name (a APIName) and defs (a List).
 *
 * <pre>
 * (Component
 *
 * &#064;"../samples/let_fn.fss":7:3 name=(APIName
 * &#064;1:24 names=["samples" "let_fn"]) defs=[ (VarDecl
 * &#064;6:11 init=(Block exprs=[ (LetFn
 * &#064;4:21 body=[ (TightJuxt
 * &#064;5:10~12 exprs=[ (VarRef
 * &#064;5:10 var=(Id name="f")) (IntLiteralExpr
 * &#064;5:12 text="7" val=7 props=["parenthesized"])])] fns=[ (FnDecl
 * &#064;4:21 body=(OpExpr
 * &#064;4:17~21 op=(Opr
 * &#064;4:19 op=(Op name="+")) args=[ (VarRef
 * &#064;4:17 var=(Id name="y")) (IntLiteralExpr
 * &#064;4:21 text="1" val=1)]) contract=(Contract
 * &#064;4:13) name=(Fun name_=(Id
 * &#064;4:10 name="f")) params=[ (Param
 * &#064;4:12 name=(Id
 * &#064;4:11 name="y"))])])]) name=(Id
 * &#064;3:1 name="x") type=(Some val=(VarType
 * &#064;3:5 name=(APIName names=["int"]))))])
 * </pre>
 */
// In the above example, "&#064;" = "@".  A line starting with "@" has special meaning to javadoc.
public class Unprinter extends NodeReflection {

    public Span lastSpan = NodeUtil.makeSpan("Unprinter generated."); // Default value is all empty and zero.
    private final ASTNodeInfo dummyInfo = new ASTNodeInfo(lastSpan, scala.Option.apply((Comment)null));
    private final scala.collection.immutable.List emptyList = scala.collection.JavaConversions.asScalaBuffer(JUseful.list()).toList();

    Lex l;

    /**
     * An unprinter wraps a primitive Lexer.
     */
    public Unprinter(Lex l) {
        this.l = l;
    }

    public String lexAfter(String expected) throws IOException {
        expectPrefix(expected);
        return l.name(false);
    }

    /**
     * Reads the location-describing information that follows an at-sign (@)
     * following the class name in an S-expression. The most general location is
     * <br>
     * "startfile":startline:startcolumn~"endfile":endline:endcolumn <br>
     * The full list of accepted location forms appears below. Missing
     * information is inferred; missing file is copied from the previous
     * location's ending file, missing endpoint is copied from the start point,
     * and missing line is copied from the same line.
     * <p>
     * "f1":1:2~"f2":3:4<br>
     * "f3":5:6~7:8<br>
     * "f4":9:10~11 (range of columns)<br>
     * "f5":12:13<br>
     * 14:15~16:17<br>
     * 18:19~20 (range of columns)<br>
     * 21:22<br>
     *
     * @throws IOException
     */
    public String readSpan() throws IOException {
        String fname = lastSpan.begin().fileName();
        String next = l.name(false);
        if (next.startsWith("\"") && next.endsWith("\"")) {
            fname = deQuote(next).intern();
            next = lexAfter(":");
        }
        int line = Integer.parseInt(next,10);
        int column = Integer.parseInt(lexAfter(":"),10);
        SourceLoc beginning = new SourceLoc(fname, line, column, 0);

        next = l.name(false);

        SourceLoc ending = beginning;
        if (Printer.tilde.equals(next)) {
            next = l.name(false);
            boolean sawFile = false;
            if (next.startsWith("\"") && next.endsWith("\"")) {
                fname = deQuote(next).intern();
                next = lexAfter(":");
                sawFile = true;
            }
            int lineOrCol = Integer.parseInt(next);
            next = l.name(false);
            if (":".equals(next)) {
                line = lineOrCol;
                column = Integer.parseInt(l.name(false),10);
                next = l.name(false);
            } else if (sawFile) {
                error("Saw f:l:c~f:l with no following colon");
            } else {
                // line unchanged
                column = lineOrCol;
            }
            ending = new SourceLoc(fname, line, column, 0);
        }
        lastSpan = new Span(beginning, ending);
        if (next.length() == 0) {
            next = l.name();
        } else if (!(")".equals(next))) {
            error("Did we expect this?");
        }
        // System.out.println("Returning "+lastSpan+" and \""+next+"\"");
        return next;
    }

    static Class[] oneSpanArg = { Span.class };

    @SuppressWarnings("unchecked")
    @Override
    protected Constructor defaultConstructorFor(Class cl) {
        try {
            return cl.getDeclaredConstructor(oneSpanArg);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {

        }
        return null;
    }

    /**
     * Expects a String representation of an AST, beginning with left
     * parentheses and name of the AST node being read.
     *
     * @return The AST whose represenation appears on the input, including all
     *         of its substructure.
     * @throws IOException
     */
    public Node read() throws IOException {
        l.lp();
        return readNode(l.name());
    }

    /**
     * Reads the remainder of the S expression for the class whose name is
     * passed in as a parameter. Expect that leading "(" and class name have
     * both already been read from the stream.
     *
     * @return The AST whose represenation appears on the input, including all
     *         of its substructure.
     * @throws IOException
     */
    public Node readNode(String class_name) throws IOException {
        classFor(class_name); // Loads other tables as a side-effect

        Node node = null;
        String next = l.name();

        // Next token is either a field, or a span.
        if ("@".equals(next)) {
            next = readSpan();
        }

        // Begins by constructing an empty node.
        // To be readable, a node class must supply a constructor for
        // a single Span argument.
        try {
            node = makeNodeFromSpan(class_name, null, lastSpan);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            error("Error reading node type " + class_name + ", wrong invocation target");
        } catch (InstantiationException e) {
            e.printStackTrace();
            error("Error reading node type " + class_name + ", wrong instantiation");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            error("Error reading node type " + class_name + ", illegal access");
        }
        // Iteratively read field names and values, and assign them into
        // the (increasingly less-) empty node.
        try {
            while (!")".equals(next)) {
                Field f = fieldFor(class_name, next);
                expectPrefix("=");
                // Try to figure out, based on reflected type, what we are
                // reading.
                // Fail if the syntax doesn't match.

                // There is some, not too much, consistency checking
                // between Field type and input syntax.
                if (f.getType() == List.class || f.getType() == scala.collection.immutable.List.class) {
                    expectPrefix("[");
                    // This is an actual hole. Might want to add a
                    // structure-verification
                    // frob to any methods containing List or Pair.
                    f.set(node, readList());
                } else if (f.getType() == Map.class){
                    expectPrefix("(Map");
                    f.set(node, readMap());
                    /*
                } else if (f.getType() == Pair.class) {
                    expectPrefix("(Pair");
                    // This is an actual hole. Might want to add a
                    // structure-verification
                    // frob to any methods containing List or Pair.
                    f.set(node, readPair());
                    */
                } else if (f.getType() == String.class) {
                    f.set(node, deQuote(l.name()).intern()); // Lexer returns an
                                                     // escape-containing
                                                     // string, deQuote converts to Unicode.
                } else if (f.getType() == Integer.class) {
                    f.set(node, readInt(l.name()));
                } else if (f.getType() == Boolean.TYPE) {
                    f.setBoolean(node, readBoolean(l.name()));
                } else if (f.getType() == Double.TYPE) {
                    f.setDouble(node, readDouble(l.string()));
                } else if (f.getType() == BigInteger.class) {
                    f.set(node, readBigInteger(l.name()));
                } else if (f.getType() == Double.class) {
                    f.set(node, readDouble(l.name()+"."+lexAfter(".")));
                } else if (Option.class.isAssignableFrom(f.getType()) ||
                           scala.Option.class.isAssignableFrom(f.getType())) {
                    f.set(node, readOption());
                } else if (ASTNodeInfo.class.isAssignableFrom(f.getType())) {
                    f.set(node, readASTNodeInfo(NodeUtil.getSpan((ASTNode)node)));
                } else if (ASTNode.class.isAssignableFrom(f.getType())
                           || ScopeBody.class.isAssignableFrom(f.getType())
                           || IRNodeInfo.class.isAssignableFrom(f.getType())) {
                    expectPrefix("(");
                    f.set(node, readNode(l.name()));
                }
                next = l.name();
            }

            // Check for unassigned fields, pick sensible defaults.
            for (Field f : fieldArrayFor(class_name)) {
                Class<? extends Object> fcl = f.getType();
                // Happily, primitives all wake up with good default values.
                if (!fcl.isPrimitive() && f.get(node) == null) {
                    if (fcl == List.class || fcl == scala.collection.immutable.List.class) {
                        // empty list
                        f.set(node, emptyList);
                    } else if (fcl == String.class) {
                        // empty string
                        f.set(node, "");
                    } else if (fcl == scala.Option.class) {
                        // missing option
                        f.set(node, scala.Option.apply(null));
                    } else if (fcl == ASTNodeInfo.class) {
                        // missing option
                        f.set(node, dummyInfo);
                    } else {
                        error("Unexpected missing data, field "
                                + f.getName() + " of class " + class_name);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            error("Error reading node type " + class_name + ", illegal argument");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            error("Error reading node type " + class_name + ", illegal access");
        } catch (IOException e) {
            e.printStackTrace();
            error("Error reading node type " + class_name + ", illegal IO");
        }
        return node;
    }

    public void expectPrefix(String string) throws IOException {
        l.expectPrefix(string);
    }

    private final static int NORMAL = 0;
    private final static int SAW_BACKSLASH = 1;
    private final static int SAW_BACKSLASH_TICK = 2;

    /**
     * Given a quoted string, return the Unicode string encoded within. The
     * input should be quoted, but the quotes do not appear in the resulting
     * Unicode.
     */
    public static String deQuote(CharSequence s) {
        int l = s.length();
        if (s.charAt(0) != '\"') {
            error("Malformed input, missing initial \"");
        }
        if (s.charAt(l - 1) != '\"') {
            error("Malformed input, missing final \"");
        }
        StringBuilder sb = new StringBuilder(l - 2);
        StringBuilder escaped = null;
        int state = NORMAL;
        for (int i = 1; i < l - 1; i++) {
            char c = s.charAt(i);
            if (state == NORMAL) {
                if (c == '\"') {
                    error(
                            "Malformed input, unescaped \" seen at position "
                                    + i);
                } else if (c == '\\') {
                    state = SAW_BACKSLASH;
                } else {
                    sb.append(c);
                }
            } else if (state == SAW_BACKSLASH) {
                if (c == 'b') {
                    sb.append('\b');
                    state = NORMAL;
                } else if (c == 't') {
                    sb.append('\t');
                    state = NORMAL;
                } else if (c == 'n') {
                    sb.append('\n');
                    state = NORMAL;
                } else if (c == 'f') {
                    sb.append('\f');
                    state = NORMAL;
                } else if (c == 'r') {
                    sb.append('\r');
                    state = NORMAL;
                } else if (c == 'v') {
                    sb.append('\u000b');
                    state = NORMAL;
                } else if (c == '\"') {
                    sb.append('\"');
                    state = NORMAL;
                } else if (c == '\\') {
                    sb.append("\\\\");
                    state = NORMAL;
                } else if (c == '\'') {
                    state = SAW_BACKSLASH_TICK;
                    escaped = new StringBuilder();
                } else if (File.separator.equals("\\")) {
                    sb.append('\\');
                    state = NORMAL;
                } else {
                    error(
                            "Malformed input, unexpected backslash escape " + c
                                    + "(hex " + Integer.toHexString(c)
                                    + ") at index " + i);
                }
            } else if (state == SAW_BACKSLASH_TICK) {
                if (c == '\'') {
                    // Decipher string accumulated in escaped.
                    state = NORMAL;
                    if (escaped.length() == 0) {
                        sb.append('\'');
                    } else if (Character.isDigit(escaped.charAt(0))) {
                        int fromHex;
                        try {
                            fromHex = Integer.parseInt(escaped.toString(), 16);
                            if (fromHex < 0 || fromHex > 0xFFFF) {
                                error("Unicode " + escaped
                                        + " too large for Java-hosted tool");
                            }
                            sb.append((char) fromHex);
                        } catch (NumberFormatException ex) {
                            error("Malformed hex encoding " + escaped);
                        }
                    } else {
                        translateUnicode(escaped.toString(), sb);
                    }
                } else {
                    escaped.append(c);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Appends to sb the Unicode characters specified by name in escaped.
     * Incomplete implementation of the Fortress Unicode escaping spec.
     *
     * @param escaped
     * @param sb
     */
    private static void translateUnicode(String escaped, StringBuilder sb) {
        // TODO Need to implement full generality of Unicode name encoding.
        StringTokenizer st = new StringTokenizer(escaped, "&", false);
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            String mapped = Unicode.byNameLC(tok);
            if (Character.isUpperCase(tok.charAt(0))) {
                mapped = mapped.toUpperCase();
            }
            sb.append(mapped);
        }
    }

    public static String enQuote(CharSequence s) {
        StringBuilder sb = new StringBuilder(s.length() + 2);
        int l = s.length();
        for (int i = 0; i < l; i++) {
            char c = s.charAt(i);
            if (needsBackslash(c)) {
                sb.append('\\');
                sb.append(afterBackslash(c).charValue());
            } else if (needsUnicoding(c)) {
                // At least two characters following \',
                // and also beginning with 0-9.
                sb.append('\\');
                sb.append('\'');
                String hex = Integer.toHexString(c);
                if (hex.length() < 2 || hex.charAt(0) > '9') {
                    sb.append('0');
                }
                sb.append(hex);
                sb.append('\'');
            } else {
                sb.append(c);
            }

        }
        return sb.toString();
    }

    private static boolean needsUnicoding(char c) {
        return c < ' ' || c > '~';
    }

    private static boolean needsBackslash(char c) {
        return c == '\b' || c == '\t' || c == '\n' || c == '\f' || c == '\r' || c == '\u000b'
                || c == '\"' || c == '\\';
    }

    private static Character afterBackslash(char c) {
        switch (c) {
        case '\b':
            return Character.valueOf('b');
        case '\t':
            return Character.valueOf('t');
        case '\n':
            return Character.valueOf('n');
        case '\f':
            return Character.valueOf('f');
        case '\r':
            return Character.valueOf('r');
        case '\u000b':
            return Character.valueOf('v');
        case '\"':
            return Character.valueOf('\"');
        case '\'':
            return Character.valueOf('\'');
        case '\\':
            return Character.valueOf('\\');
        default:
            error("Invalid input, character value 0x" + Integer.toHexString(c));
            return Character.valueOf(' ');
        }
    }

    /*
    private Pair<Object, Object> readPair() throws IOException {
        Object x, y;
//        if ("(".equals(a)) {
//            x = readThing();
//            expectPrefix("(");
//            y = readThing();
//        } else if ("[".equals(a)) {
//            x = readList();
//            expectPrefix("[");
//            y = readList();
//        } else if (a.startsWith("\"")) {
//            x = deQuote(a).intern(); // Internal form is quoted
//            y = deQuote(l.name()).intern(); // Internal form is quoted
//        } else {
//            return error("Pair of unknown stuff beginning " + a);
//        }
        x = readElement();
        y = readElement();
        expectPrefix(")");
        return new Pair<Object, Object>(x, y);
    }
    */

    public ASTNodeInfo readASTNodeInfo(Span span) throws IOException {
        expectPrefix("(");
        String s = l.name();
        ASTNodeInfo info;
        if ( "ASTNodeInfo".equals(s) ) {
            expectPrefix("(");
            s = l.name(false);
            if (")".equals(s))
                info = NodeFactory.makeASTNodeInfo(span);
            else {
                info = NodeFactory.makeASTNodeInfo(span, deQuote(s).intern());
                s = l.name(false);
            }
            expectPrefix(")");
        } else {
            error(s + " is not a valid subclass of ASTNodeInfo.");
            info = NodeFactory.makeASTNodeInfo(span);
        }
        return info;
    }

    public Map<String,Object> readMap() throws IOException {
        Map<String,Object> map = new HashMap<String,Object>();
        String s = l.name();
        while (true) {
            if ("!".equals(s)) {
                String name = readIdentifier();
                expectPrefix("=");
                Object obj = readElement();
                map.put( name, obj );
            } else if ( ")".equals(s) ){
                return map;
            }
            s = l.name();
        }
    }

    private Object readElement() throws IOException {
        String a = l.name();
         if ("(".equals(a)) {
            return readThing();
        } else if ("[".equals(a)) {
            return readList();
        } else if (a.startsWith("\"")) {
            return deQuote(a).intern(); // Internal form is quoted
        } else {
            error("Pair of unknown stuff beginning " + a);
            return null;
        }
    }

    Integer readInt(String s) throws IOException {
        return new Integer(Integer.parseInt(s, 10));

    }

    private String readIdentifier() throws IOException {
        return l.name();
    }

    double readDouble(String s) throws IOException {
        if (s.endsWith("e") || s.endsWith("E")) {
            return Double.parseDouble(s+l.string());
        }
        else return Double.parseDouble(s);
    }

    boolean readBoolean(String s) throws IOException {
        return Boolean.parseBoolean(s);
    }

    BigInteger readBigInteger(String s) throws IOException {
        return new BigInteger(s);
    }

    public scala.collection.immutable.List<Object> readList() throws IOException {
        String s = l.name();
        ArrayList<Object> a = new ArrayList<Object>();
        Object x;
        while (true) {
            if ("(".equals(s)) {
                x = readThing();
            } else if ("[".equals(s)) {
                x = readList();
            } else if (s.startsWith("\"")) {
                x = deQuote(s).intern(); // Intermediate form is quoted.
            } else if (s.startsWith("]")) {
                return scala.collection.JavaConversions.asScalaBuffer(JUseful.immutableTrimmedList(a)).toList();
            } else {
                error("List of unknown element beginning " + s);
                return emptyList;
            }
            a.add(x);
            s = l.name();
        }
    }

    private Object readThing() throws IOException {
        Object x;
        String s2 = l.name();
        /*
        if ("Pair".equals(s2)) {
            x = readPair();
        */
        if ("Some".equals(s2)) {
            x = readOptionTail();
        } else if ("Map".equals(s2)) {
            x = readMap();
        } else {
            x = readNode(s2);
        }
        return x;
    }

    public scala.Option<Object> readOption() throws IOException {
        // Reading options is slightly different because
        // there is no detailed reflection information for
        // generics in Java. This code simply chooses to
        // believes the types in the input.
        expectPrefix("(Some");
        return readOptionTail();
    }

    private scala.Option<Object> readOptionTail() throws IOException {
        String s = l.name();
        if (")".equals(s)) { return scala.Option.apply(null); }
        else if (!"x".equals(s)) {
            error("Expected 'x' saw '" + s + "'");
            return scala.Option.apply(null);
        }
        expectPrefix("=");
        Object x;
        s = l.name();
        if ("(".equals(s)) {
            x = readThing();
        } else if ("[".equals(s)) {
            x = readList();
        } else if (s.startsWith("\"")) {
            x = deQuote(s).intern(); // Internal form is quoted. deQuoteQuoted(s);
        } else {
            x = null;
        }
        expectPrefix(")");
        return scala.Option.apply(x);
    }

}
