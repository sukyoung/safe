/*******************************************************************************
    Copyright 2008,2009, Oracle and/or its affiliates.
    All rights reserved.


    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util;

import kr.ac.kaist.jsaf.useful.Files;

public class SourceLocRats extends SourceLoc {

    final int col;

    int line;

    final int offset;

    final String fileName;

    public SourceLocRats(String f, int l, int c, int offset) {
        this.fileName = Files.windowPathToUnixPath(f);
        this.line = l;
        this.col = c;
        this.offset = offset;
    }

    public SourceLocRats addLines(int l) {
        this.line += l;
        return this;
    }

    @Override
    public int column() {
        return col;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public int getLine() {
        return line;
    }

    public int getOffset() {
        return offset;
    }

}
