/*******************************************************************************
    Copyright 2008,2010, Oracle and/or its affiliates.
    All rights reserved.


    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.useful;

import edu.rice.cs.plt.lambda.Lambda;

public abstract class Fn<T, U>  implements Lambda<T, U>, F<T, U>  {
    public abstract U apply(T arg);

    public U value(T arg) {
        return apply(arg);
    }
}
