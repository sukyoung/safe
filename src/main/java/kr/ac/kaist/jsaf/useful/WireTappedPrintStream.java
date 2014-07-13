/*******************************************************************************
    Copyright 2008,2009, Oracle and/or its affiliates.
    All rights reserved.


    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.useful;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * Acts like a Printstream, but optionally delays any actual printing of the
 * output, and saves a copy for replay and/or inspection.  Can be used to
 * implement optional logging of output/error for code that uses System.{out,err}
 * (for example, junit).
 *
 * @author chase
 */
public class WireTappedPrintStream extends PrintStream {

    /**
     * The stream to which writes ultimately are directed.
     */
    PrintStream tappee;

    /**
     * Receives writes to this PrintStream.
     */
    ByteArrayOutputStream s;

    /**
     * If true, writes to the wiretapped stream are not released to tappee
     * until flush(true) is called.
     */
    boolean postponePassthrough;

    /**
     * If postponePassthrough, then this is the index of the first postponed
     * byte.
     */
    int postponeStart;

    public static final int DEFAULT_BYTE_LIMIT = 65536;

    /**
     * Creates a wire-tapped PrintStream that does not delay printing.
     *
     * @param tappee the PrintStream to which output should be flushed.
     */
    public static WireTappedPrintStream make(PrintStream tappee) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            return new WireTappedPrintStream(bos, tappee, false, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new WireTappedPrintStream(bos, tappee, false);
        }
    }

    /**
     * Creates a wire-tapped PrintStream delays printing if postponePassthrough
     * is true.
     *
     * @param tappee              the PrintStream to which output should be flushed.
     * @param postponePassthrough do, or don't, postpone actual printing.
     */
    public static WireTappedPrintStream make(PrintStream tappee,
                                             boolean postponePassthrough) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            return new WireTappedPrintStream(bos, tappee, postponePassthrough, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new WireTappedPrintStream(bos, tappee, postponePassthrough);
        } 
    }

    private WireTappedPrintStream(ByteArrayOutputStream s, PrintStream tappee,
                                  boolean postponePassthrough,
                                  String encoding) throws UnsupportedEncodingException {
        super(s, true, encoding);
        this.s = s;
        this.tappee = tappee;
        this.postponePassthrough = postponePassthrough;
    }

    private WireTappedPrintStream(ByteArrayOutputStream s, PrintStream tappee,
                                  boolean postponePassthrough) {
        super(s, true);
        this.s = s;
        this.tappee = tappee;
        this.postponePassthrough = postponePassthrough;
    }

    /**
     * Obtains all output as a string,
     * including both already released and not yet released.
     */
    public String getString() throws UnsupportedEncodingException {
        flush();
        return s.toString("UTF-8");
    }

    /* (non-Javadoc)
     * @see java.io.PrintStream#close()
     */
    @Override
    public void close() {
        super.close();
        tappee.close();
    }

    /**
     * Sets all streams to flushed state, and optionally releases postponed output
     * to the wiretapped stream before flushing.
     *
     * @param releasePostponed if true, released postponed output
     * @throws IOException
     */
    public void flush(boolean releasePostponed) throws IOException {
        super.flush();
        if (releasePostponed && postponePassthrough) {
            byte[] released = s.toByteArray();
            tappee.write(released, postponeStart, released.length - postponeStart);
            postponeStart = released.length;
        }
        tappee.flush();
    }

    /* (non-Javadoc)
     * @see java.io.PrintStream#flush()
     */
    @Override
    public void flush() {
        super.flush();
        tappee.flush();
    }

    /* (non-Javadoc)
     * @see java.io.PrintStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] arg0, int arg1, int arg2) {
        super.write(arg0, arg1, arg2);
        if (!postponePassthrough) tappee.write(arg0, arg1, arg2);
    }

    /* (non-Javadoc)
     * @see java.io.PrintStream#write(int)
     */
    @Override
    public void write(int arg0) {
        super.write(arg0);
        if (!postponePassthrough) tappee.write(arg0);
    }

    /* (non-Javadoc)
     * @see java.io.FilterOutputStream#write(byte[])
     */
    @Override
    public void write(byte[] arg0) throws IOException {
        super.write(arg0);
        if (!postponePassthrough) tappee.write(arg0);
    }
}
