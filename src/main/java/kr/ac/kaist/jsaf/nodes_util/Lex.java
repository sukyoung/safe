/*******************************************************************************
    Copyright 2008,2010, Oracle and/or its affiliates.
    All rights reserved.


    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util;

import kr.ac.kaist.jsaf.nodes_util.Unprinter;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;

public class Lex {
    BufferedReader reader;
    String name;

    int last = -1;

    int lastread = -1;

    public Lex(BufferedReader r) {
        this(r, "unnamed file");
    }

    public Lex(BufferedReader r, String name) {
        this.reader = r;
        this.name = name;
    }

    int line = 1;

    public int line() {
        return line;
    }

    int column = 0;

    public int column() {
        return column;
    }

    /**
     * Adds line, column, and pushback functionality to a BufferedReader.
     *
     * @return An integer read from a buffer.
     * @throws IOException
     */
    private int read() throws IOException {
        int x = last;
        last = -1;
        if (x == -1) {
            column++;
            x = reader.read();
            if (x == '\r') {
                line++;
                column = 0;
            } else if (x == '\n') {
                // Look back one in case it is a Windows file
                // Do not double-count the line.
                if (lastread != '\r') line++;
                column = 0;
            }
        }
        lastread = x;
        return x;
    }

    /**
     * Undoes previous read (multiple calls only undo a single read).
     */
    private void unread() {
        last = lastread;
    }

    /**
     * Skips over whitespace in the input stream.
     *
     * @throws IOException
     */
    public void white() throws IOException {
        int c = read();
        while (c == -1 || Character.isWhitespace(c)) {
            if (c == -1) {
                throw new EOFException();
            }
            c = read();
        }
        unread();
    }

    /**
     * Returns a string from the input stream.  A String is either:
     * <ul>
     * <li>A single ( or )</li>
     * <li>A double-quoted, encoded string
     * <li>A sequence of non-whitespace, non-(, non-) characters.
     * </ul>
     * <p/>
     * This method is mostly used for reading from OCaml-com.sun.fortress.interpreter.parser-generated ASTs.
     *
     * @throws IOException
     */
    public String string() throws IOException {
        StringBuilder sb = new StringBuilder();
        white();
        int i = read();
        if (i == '(' || i == ')') {
            sb.append((char) i);
            read();
            unread(); // Need to advance the line/column to next.
            return sb.toString();
        }
        if (i == '"') {
            sb.append('"');
            readQuoted(sb);
            sb.append('"');
        } else {
            boolean quoted = false;
            while (i != -1 && (quoted || i != '(' && i != ')' && !Character.isWhitespace(i))) {
                if (quoted) {
                    sb.append((char) i);
                    quoted = false;
                } else if (i == '`') {
                    quoted = true;
                } else {
                    sb.append((char) i);
                }
                i = read();
            }
        }
        unread();
        return sb.toString();
    }

    /**
     * Returns true if the stream is positioned at EOF,
     * or at whitespace leading to EOF.  As a side-effect,
     * skips over any whitespace leading to the next token.
     *
     * @throws IOException
     */
    public boolean atEOF() throws IOException {
        int c = read();
        while (c != -1 && Character.isWhitespace(c)) {
            c = read();
        }
        if (c == -1) return true;
        unread();
        return false;
    }

    /**
     * Returns a name after skipping leading whitespace
     *
     * @return a name
     * @throws IOException
     * @see #name(boolean)
     */
    public String name() throws IOException {
        return name(true);
    }

    /**
     * Returns a name after optionally skipping leading whitespace.
     * <p/>
     * A name is one of
     * <ul>
     * <li> single parentheses (S-expr delimiter)
     * <li> single square bracket (List delimiter)
     * <li> single at-sign (location indicator)
     * <li> single equals sign (follows field name)
     * <li> single comma (separates parts of a location)
     * <li> single colon (separates line number and column number)
     * <li> single tilde (indicates a range of values in a location)
     * <li> a double-quoted escaped string (@see readQuoted)
     * <li> a sequence of underscores, dollar signs, digits, and letters.
     * </ul>
     *
     * @throws IOException
     */
    public String name(boolean skipLeadingWhite) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (skipLeadingWhite) white();
        int c = read();
        if (c == '(' || c == ')' || c == '[' || c == ']' || c == '@' || c == '=' || c == ',' || c == ':' || c == '~' ||
            c == '!') {
            sb.append((char) c);
            read();
            unread(); // Need to advance the line/column to next.
            return sb.toString();
        }
        if (c == '"') {
            sb.append('"');
            readQuoted(sb);
            sb.append('"');
        } else {
            // Allow leading "-"
            if (c == '-') {
                sb.append((char) c);
                c = read();
            }
            while (c != -1 && (c == '_' || c == '$' || Character.isLetterOrDigit(c))) {
                sb.append((char) c);
                c = read();
            }
        }
        unread();
        return sb.toString();
    }

    private final static int NORMAL = 0;
    private final static int SAW_BACKSLASH = 1;
    private final static int SAW_BACKSLASH_TICK = 2;

    /**
     * Assuming that the initial double-quote has been read, reads
     * a quoted string representation and appends it to sb.  Note that
     * it does not translate the representation; it merely reads it,
     * verbatim.
     *
     * @param sb
     * @throws IOException
     */
    private void readQuoted(StringBuilder sb) throws IOException {
        int i;
        i = read();
        int state = NORMAL;
        while (i != -1) {
            char c = (char) i;
            if (state == NORMAL) {
                if (c == '\"') {
                    // Double-quote in NORMAL state ends the string.
                    read(); // Lookahead one to permit unread below.
                    break;
                } else if (c == '\\') {
                    state = SAW_BACKSLASH;
                }
            } else if (state == SAW_BACKSLASH) {
                if (c == 'b' || c == 't' || c == 'n' || c == 'f' || c == 'r' || c == 'v' || c == '\"' || c == '\\') {
                    state = NORMAL;
                } else if (c == '\'') {
                    state = SAW_BACKSLASH_TICK;
                } else if (File.separator.equals("\\")) {
                    state = NORMAL;
                } else {
                    unexpected("Backslash escape " + c + "(hex " + Integer.toHexString(c) + ") ");
                }
            } else if (state == SAW_BACKSLASH_TICK) {
                if (c == '\'') {
                    // Decipher string accumulated in escaped.
                    state = NORMAL;
                }
            }
            sb.append(c);
            i = read();
        }
    }

    /**
     * Skips over whitespace, reads a string, and attempts to convert it to an
     * integer.
     *
     * @return the integer that was read.
     * @throws IOException
     */
    public int integer() throws IOException {
        String s = string();
        if (s.startsWith("-")) return -Integer.parseInt(s.substring(1));
        return Integer.parseInt(s);
    }

    public void unexpected(String got, String wanted) throws IOException {
        throw new IOException(
                "Near line " + line + " and column " + column + " got " + got + ", wanted " + wanted + ", reading " +
                name);
    }

    public void unexpected(String got) throws IOException {
        throw new IOException("Near line " + line + " and column " + column + " got " + got + ", reading " + name);
    }

    public Lex lp() throws IOException {
        expectPrefix("(");
        return this;
    }

    public Lex rp() throws IOException {
        expectPrefix(")");
        return this;
    }

    /**
     * Skips over whitespace, and consumes a white-space-or-(-or-)-terminated
     * string that may contain ( and ) if they are expected.
     *
     * @param s The string that is expected in the input
     * @throws IOException
     */
    public void expect(String s) throws IOException {
        white();
        int i = 0;
        int l = s.length();
        if (l == 0) return; // Empty strings are strings too.
        int c = read();
        while (c != -1 && i < l) {
            if (s.charAt(i) != c) {
                if (Character.isWhitespace(c) || c == '(' || c == ')') break;
                throw new IOException("Expected " + s.substring(0, i) + "[" + s.substring(i, i + 1) + "]" + s.substring(
                        i + 1) + " saw [" + (char) c + "] instead at line " + line() + " and column " + column() +
                                      " of " + name);
            }
            c = read();
            i++;
        }
        if ((c == -1 || Character.isWhitespace(c) || c == '(' || c == ')') && i == l) {
            unread();
        } else if (i != l) {
            throw new IOException("Expected " + s.substring(0, i) + "[" + s.substring(i, i + 1) + "]" + s.substring(
                    i + 1) + " saw [" + (char) c + "] instead at line " + line() + " and column " + column() + " of " +
                                  name);

        } else {
            throw new IOException(
                    "Expected " + s + " followed by whitespace but saw [" + (char) c + "] instead at line " + line() +
                    " and column " + column() + " of " + name);

        }
    }

    /**
     * Skips over whitespace, and consumes a matching string that may contain (
     * and ) if they are expected. The string need not be terminated, and the
     * next read will see the character immediately following.
     *
     * @param s The string that is expected in the input
     * @throws IOException
     */
    public void expectPrefix(String s) throws IOException {
        white();
        int i = 0;
        int l = s.length();
        if (l == 0) return; // Empty strings are strings too.
        int c = read();
        while (c != -1 && !Character.isWhitespace(c) && i < l) {
            if (s.charAt(i) != c) throw new IOException("Expected " + s.substring(0, i) + "[" + s.substring(i, i + 1) +
                                                        "]" + s.substring(i + 1) + " saw [" + (char) c +
                                                        "] instead at line " + line() + " and column " + column() +
                                                        " of " + name);
            c = read();
            i++;
        }
        if (i == l) {
            unread();
        } else /* i < l */ {
            throw new IOException("Expected " + s.substring(0, i) + "[" + s.substring(i, i + 1) + "]" + s.substring(
                    i + 1) + " saw [" + (char) c + "] instead at line " + line() + " and column " + column() + " of " +
                                  name);

        }
    }

    public String conditionallyUnquotedString() throws IOException {
        String s = string();
        if (s.startsWith("\"")) {
            s = Unprinter.deQuote(s);
        }
        return s;
    }

    public boolean boolean_() throws IOException {
        String s = string();
        if (s.startsWith("t")) return true;
        if (s.startsWith("f")) return false;
        if (s.startsWith("T")) return true;
        if (s.startsWith("F")) return false;
        throw new IOException(
                "Expected true or false, saw " + s + "instead at line " + line() + " and column " + column() + " of " +
                name);

    }

}
