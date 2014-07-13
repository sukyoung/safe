/*******************************************************************************
    Copyright 2008,2009, Oracle and/or its affiliates.
    All rights reserved.


    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.useful;

import java.io.Serializable;

public interface HasAt extends Serializable {
    static class FromString implements HasAt {
        public FromString(String s) {
            this.s = s;
        }

        String s;

        public String at() {
            return s;
        }

        public String stringName() {
            return s;
        }
    }

    public String at();

    public String stringName();
}
