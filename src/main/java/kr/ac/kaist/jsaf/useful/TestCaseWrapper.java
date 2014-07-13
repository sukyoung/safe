/*******************************************************************************
    Copyright 2008,2009, Oracle and/or its affiliates.
    All rights reserved.


    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.useful;

import junit.framework.*;

import java.io.IOException;
import java.io.PrintStream;

public abstract class TestCaseWrapper extends TestCase implements TestListener {

    private final static boolean failsOnly = true;
    private boolean anyFail;

    PrintStream oldOut;
    PrintStream oldErr;
    WireTappedPrintStream wt_err;
    WireTappedPrintStream wt_out;

    public void run(TestResult result) {
        result.addListener(this);

        anyFail = false;
        oldOut = System.out;
        oldErr = System.err;
        wt_err = WireTappedPrintStream.make(System.err, failsOnly);
        wt_out = WireTappedPrintStream.make(System.out, failsOnly);
        System.setErr(wt_err);
        System.setOut(wt_out);

        super.run(result);

        System.setErr(oldErr);
        System.setOut(oldOut);
        System.out.println("  " + this.toString() + (anyFail ? " FAIL" : " OK"));
        try {
            wt_err.flush(anyFail);
            wt_out.flush(anyFail);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        result.removeListener(this);
    }

    public TestCaseWrapper() {
        super();
    }

    public TestCaseWrapper(String arg0) {
        super(arg0);
    }

    /* (non-Javadoc)
    * @see junit.framework.TestListener#addError(junit.framework.Test, java.lang.Throwable)
    */
    public void addError(Test test, Throwable t) {
        anyFail = true;
    }

    /* (non-Javadoc)
     * @see junit.framework.TestListener#addFailure(junit.framework.Test, junit.framework.AssertionFailedError)
     */
    public void addFailure(Test test, AssertionFailedError t) {
        anyFail = true;
    }

    /* (non-Javadoc)
     * @see junit.framework.TestListener#endTest(junit.framework.Test)
     */
    public void endTest(Test test) {
    }

    /* (non-Javadoc)
    * @see junit.framework.TestListener#startTest(junit.framework.Test)
    */
    public void startTest(Test test) {
    }
}
