/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.astgen;

import edu.rice.cs.astgen.*;
import edu.rice.cs.astgen.Types.*;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.tuple.OptionUnwrapException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/* Generates JSAst.scala, a Scala representation
 * of the JavaScript ast (JS.ast)
 */
public class ScalaAstGenerator extends CodeGenerator {
    public ScalaAstGenerator(ASTModel ast) {
        super(ast);
    }

    @Override
    public Iterable<Class<? extends CodeGenerator>> dependencies() {
        return new ArrayList<Class<? extends CodeGenerator>>();
    }

    @Override
    public void generateInterfaceMembers(TabPrintWriter writer, NodeInterface i) {
    }

    @Override
    public void generateClassMembers(TabPrintWriter writer, NodeClass c) {
    }

    private List<? extends NodeInterface> getInterfaces() {
        return mkList(ast.interfaces());
    }

    /**
     * Given a NodeInterface, construct its extends clause as a string.
     */
    private String extendsClause(NodeInterface box) {
        List<TypeName> interfaces = box.interfaces();

        if (interfaces.isEmpty()) {
            return "";
        }
        if (interfaces.size() == 1) {
            return "extends " + fieldType(interfaces.get(0));
        }

        StringBuilder buffer = new StringBuilder("extends ");
        boolean first = true;

        for (TypeName name : interfaces) {
            if (first) {
                first = false;
            } else {
                buffer.append(" with ");
            }

            buffer.append(fieldType(name));
        }

        return buffer.toString();
    }

    /**
     * Given a NodeClass, construct its extends clause as a string.
     */
    private String extendsClause(NodeClass box) {
        Option<NodeType> parent = ast.parent(box);

        Iterable<Field> superFields = IterUtil.empty();

        if (parent.isSome() && parent.unwrap() instanceof NodeClass) {
            // Parent has fields that we need to initialize during construction.
            superFields = parent.unwrap().allFields(ast);
        }

        TypeName superName = box.superClass();

        StringBuilder buffer = new StringBuilder("extends " + superName.name());


        // Classes defined outside ASTGen and extended by an ASTGen class
        // are required to have a zeroary constructor.
        buffer.append("(");
        boolean first = true;
        for (Field f : superFields) {
            if (first) {
                first = false;
            } else {
                buffer.append(", ");
            }
            buffer.append(f.getGetterName());
        }
        buffer.append(")");

        List<String> names = new ArrayList<String>();

        // Collect superinterface names.
        for (TypeName name : box.interfaces()) {
            names.add(fieldType(name));
        }

        // Add superinterfaces to extends clause as mixins.
        for (String name : names) {
            buffer.append(" with ");
            buffer.append(name);
        }
        return buffer.toString();
    }

    /**
     * Given a TypeName, return a string representation of the corresponding
     * type in Java.
     */
    private String javaFieldType(TypeName type) {
        return type.accept(new TypeNameVisitor<String>() {

            // A type declared in the AST.  Has 0 type arguments.
            public String forTreeNode(ClassName t) {
                return "kr.ac.kaist.jsaf.nodes." + t.name();
            }

            // A primitive type
            public String forPrimitive(PrimitiveName t) {
                String name = t.name();

                if (name.equals("int")) {
                    return "Int";
                }
                if (name.equals("boolean")) {
                    return "Boolean";
                }
                throw new RuntimeException("Unknown primitive " + name);
            }

            // A String.  Has 0 type arguments.
            public String forString(ClassName t) {
                return "String";
            }

            // An array of primitives.
            public String forPrimitiveArray(PrimitiveArrayName t) {
                throw new RuntimeException("Can't handle primitive array");
            }

            // An array of reference types (non-primitives).
            public String forReferenceArray(ReferenceArrayName t) {
                throw new RuntimeException("Can't handle reference array");
            }

            // A list, set, or other subtype of java.lang.Iterable.
            public String forSequenceClass(SequenceClassName t) {
                return sub("_root_.java.util.List[@type]", "@type", t.elementType().accept(this));
            }

            // An edu.rice.cs.plt.tuple.Option.  Has 1 type argument.
            public String forOptionClass(OptionClassName t) {
                return sub("edu.rice.cs.plt.tuple.Option[@type]", "@type", t.elementType().accept(this));
            }

            // A tuple (see definition in TupleName documentation).
            public String forTupleClass(TupleClassName t) {
                throw new RuntimeException("Can't handle tuple");
            }

            // A type for which none of the other cases apply.
            public String forGeneralClass(ClassName t) {
                StringBuilder name = new StringBuilder();

                // Handle types for which ASTGen provides no hooks,
                // but that we still want to treat specially.
                if (t.className().equals("Map")) {
                    name.append("_root_.java.util.Map");
                } else if (t.className().equals("BigInteger")) {
                    name.append("_root_.java.math.BigInteger");
                } else if (t.className().equals("Double")) {
                    name.append("_root_.java.lang.Double");
                } else if (t.className().equals("Object")) {
                    name.append("_root_.java.lang.Object");
                } else if (t.className().equals("Span")) {
                    name.append("kr.ac.kaist.jsaf.nodes_util.Span");
                } else if (t.className().equals("Integer")) {
                    name.append("_root_.java.lang.Integer");
                } else if (t.className().equals("SpanInfo")) {
                    name.append("kr.ac.kaist.jsaf.nodes_util.SpanInfo");
                } else {
                    name.append("kr.ac.kaist.jsaf.nodes." + t.className());
                }

                // Handle type arguments.
                // Note: This will not work with nested generic types.
                // ASTGen does not provide a facility for recursive deconstruction
                // of nested generic type arguments.
                boolean first = true;
                for (TypeArgumentName arg : t.typeArguments()) {
                    if (first) {
                        name.append("[");
                        first = false;
                    } else {
                        name.append(", ");
                    }
                    if (arg.name().equals("String")) {
                        name.append(arg.name());
                    } else if (arg.name().equals("Integer")) {
                        name.append("_root_.java.lang.Integer");
                    } else if (arg.name().equals("SpanInfo")) {
                        name.append("kr.ac.kaist.jsaf.nodes_util.SpanInfo");
                    } else {
                        name.append("kr.ac.kaist.jsaf.nodes." + arg.name());
                    }
                }
                if (!first) {
                    name.append("]");
                }

                return name.toString();
            }

        });
    }


    /**
     * Given a TypeName, return a string representation of the corresponding
     * type in Scala.
     */
    private String fieldType(TypeName type) {
        return type.accept(new TypeNameVisitor<String>() {

            // A type declared in the AST.  Has 0 type arguments.
            public String forTreeNode(ClassName t) {
                return "kr.ac.kaist.jsaf.nodes." + t.name();
            }

            // A primitive type
            public String forPrimitive(PrimitiveName t) {
                String name = t.name();

                if (name.equals("int")) {
                    return "Int";
                }
                if (name.equals("boolean")) {
                    return "Boolean";
                }
                throw new RuntimeException("Unknown primitive " + name);
            }

            // A String.  Has 0 type arguments.
            public String forString(ClassName t) {
                return "String";
            }

            // An array of primitives.
            public String forPrimitiveArray(PrimitiveArrayName t) {
                throw new RuntimeException("Can't handle primitive array");
            }

            // An array of reference types (non-primitives).
            public String forReferenceArray(ReferenceArrayName t) {
                throw new RuntimeException("Can't handle reference array");
            }

            // A list, set, or other subtype of java.lang.Iterable.
            public String forSequenceClass(SequenceClassName t) {
                return sub("List[@type]", "@type", t.elementType().accept(this));
            }

            // An edu.rice.cs.plt.tuple.Option.  Has 1 type argument.
            public String forOptionClass(OptionClassName t) {
                return sub("Option[@type]", "@type", t.elementType().accept(this));
            }

            // A tuple (see definition in TupleName documentation).
            public String forTupleClass(TupleClassName t) {
                throw new RuntimeException("Can't handle tuple");
            }

            // A type for which none of the other cases apply.
            public String forGeneralClass(ClassName t) {
                StringBuilder name = new StringBuilder();

                // Handle types for which ASTGen provides no hooks,
                // but that we still want to treat specially.
                if (t.className().equals("Map")) {
                    name.append("Map");
                } else if (t.className().equals("BigInteger")) {
                    name.append("_root_.java.math.BigInteger");
                } else if (t.className().equals("Double")) {
                    name.append("_root_.java.lang.Double");
                } else if (t.className().equals("Object")) {
                    name.append("_root_.java.lang.Object");
                } else if (t.className().equals("Span")) {
                    name.append("kr.ac.kaist.jsaf.nodes_util.Span");
                } else if (t.className().equals("SpanInfo")) {
                    name.append("kr.ac.kaist.jsaf.nodes_util.SpanInfo");
                } else if (t.className().equals("Integer")) {
                    name.append("Int");
                } else {
                    name.append("kr.ac.kaist.jsaf.nodes." + t.className());
                }

                // Handle type arguments.
                // Note: This will not work with nested generic types.
                // ASTGen does not provide a facility for recursive deconstruction
                // of nested generic type arguments.
                boolean first = true;
                for (TypeArgumentName arg : t.typeArguments()) {
                    if (first) {
                        name.append("[");
                        first = false;
                    } else {
                        name.append(", ");
                    }
                    if (arg.name().equals("String")) {
                        name.append(arg.name());
                    } else if (arg.name().equals("Integer")) {
                        name.append("Int");
                    } else if (arg.name().equals("SpanInfo")) {
                        name.append("kr.ac.kaist.jsaf.nodes_util.SpanInfo");
                    } else {
                        name.append("kr.ac.kaist.jsaf.nodes." + arg.name());
                    }
                }
                if (!first) {
                    name.append("]");
                }

                return name.toString();
            }

        });
    }

    /**
     * Given a list of fields, return the corresponding
     * field declarations in String form.
     */
    private String traitFields(List<Field> fields) {
        StringBuilder buffer = new StringBuilder();
        boolean first = true;
        for (Field field : fields) {
            if (first) {
                buffer.append("\n");
                first = false;
            }
            buffer.append(sub("  def @name:@type\n", "@name", field.getGetterName(), "@type", fieldType(field.type())));
        }
        return buffer.toString();
    }

    /**
     * Given a NodeClass, return its fields in String form.
     */
    private String fields(NodeClass box) {
        return "(" + fieldsNoParens(box, true) + ")";
    }

    /**
     * Given a NodeClass, return its fields in String form
     * (without parentheses).
     */
    private String fieldsNoParens(NodeClass box, boolean firstPass) {
        if (mkList(box.allFields(ast)).isEmpty()) {
            return "";
        } else {
            StringBuilder buffer = new StringBuilder();

            for (Field field : box.allFields(ast)) {
                if (firstPass) {
                    firstPass = false;
                } else {
                    buffer.append(", ");
                }

                buffer.append(sub("@name:@type", "@name", field.getGetterName(), "@type", fieldType(field.type())));
            }
            return buffer.toString();
        }
    }

    /**
     * Return a string including all field declarations, including inherited declarations.
     */
    public String allFields(NodeClass box) {
        return "(" + allFieldsNoParens(box, true) + ")";
    }

    /**
     * Return a string including all field declarations, including inherited declarations,
     * without enclosing parentheses.
     */
    private StringBuilder allFieldsNoParens(NodeClass box, boolean firstPass) {
        StringBuilder buffer = new StringBuilder();

        if (ast.isTopClass(box)) {
            return buffer;
        }

        for (Field field : box.allFields(ast)) {
            if (firstPass) {
                firstPass = false;
            } else {
                buffer.append(", ");
            }

            buffer.append(sub("@name:@type", "@name", field.getGetterName(), "@type", fieldType(field.type())));
        }

        // If the supertype has no definition, there is a bug in JS.ast or ASTGen.
        // If the defined supertype is not a class, there is a bug in JS.ast or ASTGen.
        try {
            buffer.append(fieldsNoParens((NodeClass) ast.typeForName(box.superClass()).unwrap(), firstPass));
        }
        catch (OptionUnwrapException e) {
            throw new RuntimeException("Missing supertype definition in JS.ast: " + box.superClass());
        }
        return buffer;
    }


    /**
     * Given a NodeType, return a string consisting of references to the fields of that type,
     * separated by commas and enclosed in parentheses.
     */
    private String fieldsNoTypes(NodeType box) {
        if (mkList(box.allFields(ast)).isEmpty()) {
            return "";
        } else {
            StringBuilder buffer = new StringBuilder("(");
            boolean first = true;
            for (Field field : box.allFields(ast)) {
                if (first) {
                    first = false;
                } else {
                    buffer.append(", ");
                }

                buffer.append(sub("javaify(@name).asInstanceOf[@type]", "@name", field.getGetterName(), "@type", javaFieldType(field.type())));
            }
            buffer.append(")");
            return buffer.toString();
        }
    }

    /**
     * Given a NodeType, return a string consisting of references to the fields of that type,
     * separated by commas and enclosed in parentheses.
     */
    private String fieldsNames(NodeType box) {
        if (mkList(box.allFields(ast)).isEmpty()) {
            return "";
        } else {
            StringBuilder buffer = new StringBuilder("(");
            boolean first = true;
            for (Field field : box.allFields(ast)) {
                if (first) {
                    first = false;
                } else {
                    buffer.append(", ");
                }

                buffer.append(sub("@name", "@name", field.getGetterName()));
            }
            buffer.append(")");
            return buffer.toString();
        }
    }

    /**
     * Given a String (denoting a receiver name) and a NodeType, return a string denoting calls
     * to the getters of that type, separated by commas and enclosed in parentheses.
     */
    private String getterCalls(String receiverName, NodeType box) {
        if (mkList(box.allFields(ast)).isEmpty()) {
            return "()";
        } else {
            StringBuilder buffer = new StringBuilder("(");
            boolean first = true;
            for (Field field : box.allFields(ast)) {
                if (first) {
                    first = false;
                } else {
                    buffer.append(", ");
                }

                buffer.append(sub("scalaify(@receiver.@getter()).asInstanceOf[@type]", "@receiver", receiverName, "@getter", field.getGetterName(), "@type", fieldType(field.type())));
            }
            buffer.append(")");
            return buffer.toString();
        }
    }

    /**
     * Given a String denoting a wrapper function and a NodeType, return a String consisting
     * of a sequence of calls to the wrapper function, passing each field of the given type to
     * the wrapper function in turn. Calls are separated by commas and enclosed in
     * parentheses.
     */
    private String wrappedFieldCalls(String wrapper, NodeType box, boolean isUnit) {
        return wrappedFieldCalls("", wrapper, box, isUnit);
    }


    /**
     * Given a String denoting a receiver, a String denoting a wrapper function, and a NodeType,
     * return a String consisting of a sequence of calls to the wrapper function, passing each
     * field of the given type to the wrapper function in turn. Calls are separated by commas
     * and wrapped in parentheses.
     */
    private String wrappedFieldCalls(String receiver, String wrapper, NodeType box, boolean isUnit) {
        if (mkList(box.allFields(ast)).isEmpty()) {
            return "";
        } else {
            // For convenience, automatically add a dot to end of non-empty receiver.
            if (!receiver.equals("")) {
                receiver = receiver + ".";
            }

            String open, close, middle;
            if (isUnit) {
                open = "";
                close = "";
                middle = "; ";
            } else {
                open = "(";
                close = ")";
                middle = ", ";
            }

            StringBuilder buffer = new StringBuilder(open);
            boolean first = true;

            for (Field field : box.allFields(ast)) {
                if (first) {
                    first = false;
                } else {
                    buffer.append(middle);
                }

                if (isUnit)
                    buffer.append(sub("@wrapper(@receiver@name)", "@wrapper", wrapper, "@receiver", receiver, "@name", field.getGetterName()));
                else
                    buffer.append(sub("@wrapper(@receiver@name).asInstanceOf[@type]", "@wrapper", wrapper, "@receiver", receiver, "@name", field.getGetterName(), "@type", fieldType(field.type())));
            }
            buffer.append(close);
            return buffer.toString();
        }
    }


    /**
     * A nice function for string replacement.
     * sub( "foo @bar @baz", "@bar", "1", "@baz", "2" ) ->
     * "foo 1 2"
     */
    private String sub(String s, String... args) {
        if (args.length == 0) {
            return s;
        }
        return sub(String.format(s.replaceAll(args[0], "%1\\$s"), args[1]), Arrays.asList(args).subList(2, args.length).toArray(new String[0]));
    }

    /**
     * Iterable -> List
     */
    private <T> List<T> mkList(Iterable<T> iter) {
        List<T> list = new ArrayList<T>();
        for (T i : iter) {
            list.add(i);
        }
        return list;
    }

    private <T extends NodeType> Iterable<T> sort(Iterable<T> boxes) {
        List<T> list = mkList(boxes);
        Collections.sort(list, new Comparator<NodeType>() {
            public int compare(NodeType b1, NodeType b2) {
                return b1.name().compareTo(b2.name());
            }
        });
        return list;
    }

    private boolean ignoreClass(String name) {
        if (name.startsWith("_Rewrite")) {
            return false;
        }
        if (name.startsWith("_SyntaxTransformation")) {
            return true;
        }
        if (name.startsWith("_Ellipses")) {
            return true;
        }
        /* TODO: this won't be needed once TemplateGap's are removed from JS.ast */
        if (name.startsWith("TemplateGap")) {
            return true;
        }
        return false;
    }


    /**
     * Given a PrintWriter, write out all Scala declarations for JS.ast.
     */
    private void generateBody(PrintWriter writer) {

        // Generate extractor objects
        for (NodeInterface box : sort(getInterfaces())) {
            writer.println(sub("object S@name {", "@name", box.name()));
            writer.println(sub("   def unapply(node:kr.ac.kaist.jsaf.nodes.@name) = ", "@name", box.name()));
            writer.println(sub("      Some(@getterCalls)", "@getterCalls", getterCalls("node", box)));
            writer.println(sub("}"));
        }

        for (NodeClass c : sort(ast.classes())) {
            if (ignoreClass(c.name())) {
                continue;
            }

            writer.println(sub("object S@name {", "@name", c.name()));
            writer.println(sub("   def unapply(node:kr.ac.kaist.jsaf.nodes.@name) = ", "@name", c.name()));
            writer.println(sub("      Some(@getterCalls)", "@getterCalls", getterCalls("node", c)));

            // For concrete classes, generate a simulated constructor with an apply method.
            if (!c.isAbstract()) {
                writer.println(sub("   def apply@fields = ", "@fields", fields(c)));
                writer.println(sub("      new kr.ac.kaist.jsaf.nodes.@name@fieldsNoTypes", "@name", c.name(), "@fieldsNoTypes", fieldsNoTypes(c)));
            }
            writer.println(sub("}"));
        }
        // Generate walker.
        writer.println();
        writer.println("trait Walker {");
        writer.println("   def apply(node:Any):Any = walk(node)");
        writer.println("   def walk(node:Any):Any = {");
        writer.println("       node match {");
        for (NodeClass c : sort(ast.classes())) {
            if (ignoreClass(c.name())) {
                continue;
            }
            if (c.isAbstract()) {
                continue;
            }

            writer.println(sub("         case S@name@fieldsNoTypes =>", "@name", c.name(), "@fieldsNoTypes", fieldsNames(c)));
            writer.println(sub("             S@name@fieldsNoTypes", "@name", c.name(), "@fieldsNoTypes", wrappedFieldCalls("walk", c, false)));
        }
        writer.println("         case xs:List[_] => xs.map(walk _)");
        writer.println("         case xs:Option[_] => xs.map(walk _)");
        writer.println("         case _ => node");
        writer.println("      }");
        writer.println("   }");

        // Generate walker producing Unit.
        writer.println("   def walkUnit(node:Any):Unit = {");
        writer.println("       node match {");
        for (NodeClass c : sort(ast.classes())) {
            if (ignoreClass(c.name())) {
                continue;
            }
            if (c.isAbstract()) {
                continue;
            }

            writer.println(sub("         case S@name@fieldsNoTypes =>", "@name", c.name(), "@fieldsNoTypes", fieldsNames(c)));
            writer.println(sub("             @fieldsNoTypes", "@fieldsNoTypes", wrappedFieldCalls("walkUnit", c, true)));
        }
        writer.println("         case xs:List[_] => xs.foreach(walkUnit _)");
        writer.println("         case xs:Option[_] => xs.foreach(walkUnit _)");
        writer.println("         case _:Span => ");
        writer.println("         case _ => ");
        writer.println("      }");
        writer.println("   }");

        writer.println("}");
    }

    /**
     * Given a String denoting a file name, and a preamble (e.g., a copyright notice)
     * write out the contents of the preamble along with all Scala code to that file.
     */
    private void generateFile(String file, String preamble) {
        FileWriter out = null;
        PrintWriter writer = null;
        try {
            out = options.createFileInOutDir(file);

            writer = new PrintWriter(out);

            writer.println(copyright());

            writer.println(preamble);

            writer.println();

            generateBody(writer);

            writer.println();

            writer.close();

            out.close();

        }
        catch (IOException ie) {
            ie.printStackTrace();
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (writer != null) {
                    writer.close();
                }
            }
            catch (IOException ie) {
                ie.printStackTrace();
            }
        }
    }

    private void generateScalaFile() {
        generateFile("JSAst.scala",
                     "package kr.ac.kaist.jsaf.scala_src.nodes\n" +
                     "import kr.ac.kaist.jsaf.scala_src.useful._\n" +
                     "import kr.ac.kaist.jsaf.nodes_util._\n" +
                     "import kr.ac.kaist.jsaf.useful.HasAt\n" +
                     "import _root_.scala.collection.mutable.ListBuffer\n" +
                     "import _root_.java.math.BigInteger\n" +
                     "import _root_.java.lang.Double\n" +
                     "import kr.ac.kaist.jsaf.scala_src.useful.ASTGenHelper._\n\n" +
                     "object JSAst {}\n");
    }

    public void generateAdditionalCode() {
        generateScalaFile();
    }

    private String copyright() {
        StringWriter string = new StringWriter();
        PrintWriter writer = new PrintWriter(string);
        writer.println("/* THIS FILE WAS AUTOMATICALLY GENERATED BY");
        writer.println(sub("   @class FROM JS.ast */", "@class", this.getClass().getName()));
        return string.toString();
    }
}
