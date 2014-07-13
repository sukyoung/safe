/*******************************************************************************
    Copyright 2008,2010, Oracle and/or its affiliates.
    All rights reserved.


    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util;

import kr.ac.kaist.jsaf.nodes.*;
import kr.ac.kaist.jsaf.useful.HasAt;

public class UIDObject implements HasAt {

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     *
     * This is mostly for testing purposes, but it also allows
     * a more deterministic replay than Object.hashCode().
     */
    @Override
    public int hashCode() {
        return (int) uid ^ (int) (uid >>> 32);
    }

    /**
     * If none of the nodes override, this provides a somewhat terser toString
     * than the generated formatting.
     */
    @Override
    public String toString() {
        if (this instanceof AbstractNode)
            return ErrorMsgMaker.makeErrorMsg((AbstractNode) this);
        return super.toString();
    }

    public String at() {
        if (this instanceof AbstractNode)
            return NodeUtil.getSpan((AbstractNode) this).toString();
        else if (this instanceof IRAbstractNode)
            return NodeUtil.getSpan((IRAbstractNode) this).toString();
        else if (this instanceof IRId)
            return ((IRId)this).getUniqueName();
        throw new Error("Class " + this.getClass().toString() + " needs to a case in UIDObject.at()");
    }

    public String stringName() {
        if (this instanceof Node)
            return NodeUtil.stringName((Node)this);
        else
            return NodeUtil.stringName((IRNode)this);
    }

    public String toStringVerbose() {
    /*
        Printer p = new Printer();
        StringBuilder sb = new StringBuilder();
        try {
            p.dump(this, sb);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sb.toString();
    */
        return this.stringName();
    }

    public String toStringReadable() {
        /*
        if (this instanceof Node) {
            FortressAstToConcrete fatc = new FortressAstToConcrete(false, false);
            return this.toString()+"\n"+((Node)this).accept( fatc );
        } else {
            return this.toStringVerbose();
        }
        */
        return this.stringName();
    }

    static private Object lock = new Object();
    static private long seedUID = 0x7b546b0e12fd2559L;
    static private long prevUID = seedUID;
    private transient long uid;

    public UIDObject() {
        uid = next();
    }

    public final long getUID() {
        return uid;
    }

    public final void setUID(long uid) {
        this.uid = uid;
    }

    /* LFSR generating 63-bit residues */
    private long next() {
        synchronized (lock)
        {
        long x = prevUID;
        x = x + x;
        if (x < 0)
            x = x ^ 0xb1463923a7c109cdL;
        prevUID = x;
        return x;
        }
    }
}
