/*******************************************************************************
    Copyright 2008,2009, Oracle and/or its affiliates.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util;

import java.io.File;
import java.io.Serializable;
import kr.ac.kaist.jsaf.useful.HasAt;
import kr.ac.kaist.jsaf.useful.MagicNumbers;

// /
// / type 'a node = 'a Node.node
// /
abstract public class SourceLoc implements Serializable, HasAt {
    public abstract int column();

    public abstract SourceLoc addLines(int line);

    @Override
    public final int hashCode() {
        return getLine() * MagicNumbers.S + column() * MagicNumbers.o
                + getFileName().hashCode() + MagicNumbers.u;
    }

    @Override
    public final boolean equals(Object o) {
        if (o instanceof SourceLoc) {
            SourceLoc sl = (SourceLoc) o;
            return getLine() == sl.getLine() && column() == sl.column()
                    && getFileName().equals(sl.getFileName());
        }
        return false;
    }

    public final String at() {
        return getFileName() + ":" + getLine() + "." + column();
    }

    public final String stringName() {
        return "";
    }

    /**
     * @return Returns the fileName. (contains full path "/c/safe_private/test.js")
     */
    abstract public String getFileName();

    /**
     * @return Returns the fileName. (contains only filename part "test.js")
     */
    public String getFileNameOnly() {
        String fileName = getFileName();
        int index = fileName.lastIndexOf('/' /*File.separatorChar*/);
        if (index != -1) return fileName.substring(index + 1);
        else return fileName;
    }

    /**
     * @return Returns the line.
     */
    abstract public int getLine();

    /**
     * @return Returns the offset.
     */
    public abstract int getOffset();
}
