/*******************************************************************************
    Copyright 2008,2010, Oracle and/or its affiliates.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util;

import kr.ac.kaist.jsaf.nodes.ASTNode;
import kr.ac.kaist.jsaf.nodes.AbstractNode;
import kr.ac.kaist.jsaf.nodes.Node;
import kr.ac.kaist.jsaf.nodes.ScopeBody;
import kr.ac.kaist.jsaf.nodes_util.SpanInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static kr.ac.kaist.jsaf.exceptions.JSAFError.error;

@SuppressWarnings(value={"unchecked"})
abstract public class NodeReflection {
    // For minor efficiency, cache the fields, classes, and constructors.
    // Note that reflection is filtered to exclude certain fields, and to
    // sort them into a particular order.

    public static final String NODES_PACKAGE_PREFIX = "kr.ac.kaist.jsaf.nodes.";
    public static final Object[] args0 = new Object[0];

    private HashMap<String, HashMap<String, Field>> shortClassNameToFieldNameToField = new HashMap<String, HashMap<String, Field>>();

    private HashMap<String, Class> classMap = new HashMap<String, Class>();

    private HashMap<String, Constructor> constructorMap = new HashMap<String, Constructor>();
    private HashMap<String, Constructor> constructorMapZero = new HashMap<String, Constructor>();

    // What happened here is that various forms of node reflection got merged
    // into a
    // common superclass, and the needs of printing and unprintig were not
    // exactly aligned.
    // private HashMap<String, Field[]> fullClassNameToFieldArray = new
    // HashMap<String, Field[]>();
    private HashMap<String, Field[]> shortClassNameToFieldArray = new HashMap<String, Field[]>();

    private Field infoField;

    protected NodeReflection() {
        try {
            infoField = AbstractNode.class.getDeclaredField("_info");
            infoField.setAccessible(true);
        } catch (SecurityException e) {
            error(e.getMessage());
        } catch (NoSuchFieldException e) {
            error(e.getMessage());
        }
    }

    protected Field fieldFor(String class_name, String field_name) {
        return shortClassNameToFieldNameToField.get(class_name).get(field_name);
    }

    protected Constructor constructorFor(String class_name) {
        return constructorMap.get(class_name);
    }

    protected Constructor constructorZeroFor(String class_name) {
        return constructorMapZero.get(class_name);
    }

    /**
     * Makes a new node given a span and string name or class of the node to
     * be created.  At least one of s and c must be non-null; if both are
     * non-null, s is used.  THE NODE RETURNED BY THIS METHOD IS IMPROPERLY
     * INITIALIZED; IT WILL HAVE null FINAL FIELDS, WHICH MUST BE FIXED
     * USING REFLECTION.
     *
     * This method preferentially uses the zero-arg constructor, if one is
     * found, otherwise it use the one-span-arg constructor and assigns the
     * field.  This is intended to ease the transition to the
     * ASTgen-generated nodes.
     *
     * @param s
     * @param c
     * @param span
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IllegalArgumentException
     */
    protected Node makeNodeFromSpan(String s, Class c, Span span)
        throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        // try {
        Constructor con = s != null ? constructorZeroFor(s) : constructorZeroFor(c);
        if (con != null) {
            Node node = (Node)con.newInstance(args0);
            if ( node instanceof ASTNode ) {
                infoField.set((ASTNode)node, NodeFactory.makeSpanInfo(span));
            }
            return node;
        }
        con = s != null ? constructorFor(s) : constructorFor(c);
        Object[] args = new Object[1];
        args[0] = span;
        return (Node) con.newInstance(args);
    }


    protected Constructor constructorFor(Class cls) {
        String sn = cls.getSimpleName();
        Constructor c = constructorMap.get(sn);
        if (c == null) {
            classFor(sn);
            c = constructorMap.get(sn);
        }
        return c;
    }

    protected Constructor constructorZeroFor(Class cls) {
        String sn = cls.getSimpleName();
        Constructor c = constructorMapZero.get(sn);
        if (c == null) {
            c = constructorFor(cls); // This will force init, if possible
            c = constructorMapZero.get(sn);
        }
        return c;
    }

    protected Field[] fieldArrayFor(String class_name) {
        return shortClassNameToFieldArray.get(class_name);
    }

    abstract protected Constructor defaultConstructorFor(Class cl)
    throws NoSuchMethodException;

    static Class[] zeroArg = {  };
    protected Constructor defaultConstructorZeroFor(Class cl) {

        try {
            return cl.getDeclaredConstructor(zeroArg);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {

        }
        return null;
    }


    protected Class classFor(String class_name) {
        String full_class_name = NODES_PACKAGE_PREFIX + class_name;
        Class cl = classMap.get(class_name);
        if (cl == null) {
            try {
                cl = Class.forName(full_class_name);
                // TODO Ought to make all leaf classes final, and check for
                // that.
                Field[] fields = NodeReflection.getPrintableFields(cl);
                HashMap<String, Field> h = new HashMap<String, Field>();
                for (int i = 0; i < fields.length; i++) {
                    Field f = fields[i];
                    h.put(f.getName(), f);
                }
                // fullClassNameToFieldArray.put(full_class_name, fields);
                shortClassNameToFieldArray.put(class_name, fields);
                shortClassNameToFieldNameToField.put(class_name, h);
                classMap.put(class_name, cl);
                Constructor c = defaultConstructorFor(cl);
                if (c != null) {
                    c.setAccessible(true);
                    constructorMap.put(class_name, c);
                }
                Constructor c0 = defaultConstructorZeroFor(cl);
                if (c0 != null) {
                    c0.setAccessible(true);
                    constructorMapZero.put(class_name, c0);
                }
                if (c == null && c0 == null)
                    error("Could not find an appropriate constructor for " + full_class_name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                error("Error reading node type " + class_name);
            } catch (SecurityException e) {
                e.printStackTrace();
                error("Error reading node type " + class_name);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                error("Error reading node type " + class_name
                        + ", missing constructor (Span)");
            } catch (NoSuchFieldException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return cl;
    }

    protected static Field[] getPrintableFields(Class cl)
    throws SecurityException, NoSuchFieldException {
        Field[] fields;
        ArrayList<Field> fal = new ArrayList<Field>();
        Class icl = cl;
        while (icl != SpanInfo.class &&
               icl != Object.class) {
            fields = icl.getDeclaredFields();
            handleFields(fields, fal);
            icl = icl.getSuperclass();
        }
        Field[] ifields = new Field[fal.size()];
        ifields = fal.toArray(ifields);
        Arrays.sort(ifields, fieldComparator);
        return ifields;
    }

    private static void handleFields(Field[] fields, ArrayList<Field> fal) {
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if ((f.getModifiers() &
                 (java.lang.reflect.Modifier.STATIC |
                  java.lang.reflect.Modifier.TRANSIENT)) == 0
                && ! f.getName().equals("_hashCode")
                && ! f.getName().equals("_hasHashCode") ) {
                // && (fields[i].getModifiers() & java.lang.reflect.Modifier.PRIVATE) == 0
                f.setAccessible(true);
                fal.add(f);
            }
        }
    }

    protected static final Comparator<Field> fieldComparator = new Comparator<Field>() {

        public int compare(Field arg0, Field arg1) {
            Class c0 = arg0.getType();
            Class c1 = arg1.getType();
            if (c0 == List.class && c1 != List.class) {
                return 1;
            }
            if (c0 != List.class && c1 == List.class) {
                return -1;
            }
            String s0 = arg0.getName();
            String s1 = arg1.getName();
            return s0.compareTo(s1);
        }

    };

    final protected Field[] getCachedPrintableFields(Class cl) {
        return getCachedPrintableFields(cl, cl.getSimpleName());
    }

    final protected Field[] getCachedPrintableFields(Class cl, String clname) {
        Field[] fields = shortClassNameToFieldArray.get(clname);
        if (fields == null) {
            try {
                fields = getPrintableFields(cl);
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            synchronized (shortClassNameToFieldNameToField) {
                shortClassNameToFieldArray.put(clname, fields);
            }
        }
        return fields;
    }

}
