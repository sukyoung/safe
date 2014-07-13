/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Comparator;
import edu.rice.cs.plt.tuple.Option;

import kr.ac.kaist.jsaf.compiler.Parser;
import kr.ac.kaist.jsaf.nodes_util.Lex;
import kr.ac.kaist.jsaf.nodes_util.NodeUtil;
import kr.ac.kaist.jsaf.nodes.Comment;
import kr.ac.kaist.jsaf.nodes.Program;
import kr.ac.kaist.jsaf.nodes.TreeWalker;
import kr.ac.kaist.jsaf.useful.Pair;
import kr.ac.kaist.jsaf.useful.Useful;
import kr.ac.kaist.jsaf.ProjectProperties;

public class ASTIO {
    private final static String LOG_FILE_NONE="";
    private static String logFileName = "";

    private static volatile FileOutputStream fs = null;
    private static volatile OutputStreamWriter fw = null;
    private static volatile BufferedWriter logFile = null;

    private static long logStart() {
        if (logFileName==LOG_FILE_NONE) return 0;
        return System.nanoTime();
    }

    private static synchronized boolean initLogFile() {
        if (logFile!=null) return false;
        try {
            fs = new FileOutputStream(logFileName, true);
            fw = new OutputStreamWriter(fs, Charset.forName("UTF-8"));
            logFile = new BufferedWriter(fw);
        } catch (FileNotFoundException x) {
            System.err.println("WARNING: log file "+logFileName+
                               " couldn't be opened.\nTurning logging off.");
            logFileName = LOG_FILE_NONE;
            logFile = null;
            return true;
        }
        return false;
    }

    private static void logStop(long start, String eventName, String fileName) {
        if (logFileName==LOG_FILE_NONE) return;
        long nanos = System.nanoTime() - start;
        if (logFile==null && initLogFile()) return;
        String toWrite = eventName+","+fileName+","+nanos+"\n";
        try {
            logFile.write(toWrite);
            logFile.flush();
            logFile.close();
            fw.close();
            fs.close();
        } catch (IOException e) {
            System.err.print("WARNING: could not log event.\n"+toWrite);
        }
    }

    /**
     * @param p
     * @param fout
     * @throws IOException
     */
    public static void writeJavaAst(Object p, String reportedFileName, OutputStream fout)
            throws IOException {
        long t0 = logStart();
        try {
            BufferedWriter utf8fout =
                new BufferedWriter(new OutputStreamWriter(fout, Charset.forName("UTF-8")));
            (new Printer()).dump(p, utf8fout, 0);
            utf8fout.flush();
        } finally {
            logStop(t0,"W",reportedFileName);
        }
    }

    public static void writeJavaAst(Object p, String s)
            throws IOException {
        OutputStream fout = new FileOutputStream(s);
        try { writeJavaAst(p, s, fout); }
        finally { fout.close(); }
    }

    /**
     * @param reportedFileName
     * @param br
     * @throws IOException
     */
    public static Option<Program> readJavaAst(String reportedFileName,
                                              InputStream fin) throws IOException {
        long t0 = logStart();
        BufferedReader br =
            new BufferedReader(new InputStreamReader(fin, Charset.forName("UTF-8")));
        Lex lex = new Lex(br, reportedFileName);
        try {
            Unprinter up = new Unprinter(lex);
            lex.name();
            Program p = (Program) up.readNode(lex.name());
            if (p == null) { return Option.none(); }
            else { return Option.some(p); }
        }
        finally {
            if (!lex.atEOF())
                System.out.println("Parse of " + reportedFileName
                                   + " ended EARLY at line = " + lex.line()
                                   + ",  column = " + lex.column());
            logStop(t0,"R",reportedFileName);
        }
    }

    public static Option<Program> readJavaAst(String fileName) throws IOException {
        InputStream fin = new FileInputStream(fileName);
        try { return readJavaAst(fileName, fin); }
        finally { fin.close(); }
    }

    public static final Comparator<Comment> commentComparator = new Comparator<Comment>() {
        public int compareTo(int arg0, int arg1) {
            return (arg0 < arg1 ? -1 :
                    (arg0 == arg1 ? 0 : 1));
        }

        public int compare(Comment arg0, Comment arg1) {
            SourceLoc begin0 = NodeUtil.getSpan(arg0).getBegin();
            SourceLoc begin1 = NodeUtil.getSpan(arg1).getBegin();
            if (begin0.getLine() == begin1.getLine())
                return compareTo(begin0.column(), begin1.column());
            else
                return compareTo(begin0.getLine(), begin1.getLine());
        }
    };
}
