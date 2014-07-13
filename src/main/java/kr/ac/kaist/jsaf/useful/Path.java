/*******************************************************************************
    Copyright 2008,2009, Oracle and/or its affiliates.
    All rights reserved.


    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.useful;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Path {

    static String pathSep = File.pathSeparator;

    List<File> dirs;

    public Path(String path) {
        this(stringToFiles(path));
    }

    public static String toDotted(Object o) {
        String s = String.valueOf(o);
        //        s = s.replace(File.separator, "/");
        //        s = s.replace('/', '.');
        return s;
    }

    private static List<File> stringToFiles(String path) {
        List<File> dirs = new ArrayList<File>();
        String p = pathSep;
        if (path.startsWith(":")) {
            p = ":";
            path = path.substring(1);
        } else if (path.startsWith(";")) {
            p = ";";
            path = path.substring(1);
        }

        path = Useful.substituteVars(path);
        StringTokenizer st = new StringTokenizer(path, p);
        while (st.hasMoreTokens()) {
            String e = st.nextToken();
            if (!"".equals(e)) {
                File f = new File(e);
                if (f.isDirectory()) {
                    dirs.add(f);
                }
            }
        }
        return dirs;
    }

    public Path(List<File> dirs) {
        this.dirs = dirs;
    }

    public String toString() {
        return Useful.listInDelimiters("", dirs, "", pathSep);
    }

    public Path prepend(Path other) {
        return new Path(Useful.concat(other.dirs, this.dirs));
    }

    public Path append(Path other) {
        return new Path(Useful.concat(this.dirs, other.dirs));
    }

    public Path prepend(String s) {
        return prepend(new File(s));
    }

    public Path prepend(File f) {
        return new Path(Useful.prepend(f, dirs));
    }

    public File munch(File prefix, String slashedSuffix, String dottedSuffix) {
        File f = new File(prefix, dottedSuffix);
        if (f.isFile()) {
            return f;
        }
        int seploc = slashedSuffix.lastIndexOf('/');

        while (seploc > 0) {
            String dirsuffix = dottedSuffix.substring(0, seploc);
            File dir = new File(prefix, dirsuffix);
            if (dir.isDirectory()) {
                File trial = munch(dir, slashedSuffix.substring(seploc + 1), dottedSuffix.substring(seploc + 1));
                if (trial != null) return trial;
            }
            seploc = dirsuffix.lastIndexOf('.');
        }

        return null;
    }

    public File findFile(String s) throws FileNotFoundException {
        File inappropriateFile = null;
        File unreadableFile = null;
        s = s.replace(File.separator, "/"); // canonicalize
        String s_dotted = s.replace("/", "."); // canonicalize
        if (s.startsWith("/")) {
            File f = new File(s);
            if (f.isFile()) {
                if (f.canRead()) return f;
                else unreadableFile = f;
            } else if (f.exists()) {
                inappropriateFile = f;
            }
        } else {
            for (File d : dirs) {
                File f = munch(d, s, s_dotted);
                if (f != null) return f;
            }
        }
        if (unreadableFile != null) {
            throw new FileNotFoundException(
                    "Readable file " + s + " not found in directories " + dirs + "; the last unreadable match was " +
                    unreadableFile);
        }
        if (inappropriateFile != null) {
            throw new FileNotFoundException(
                    "Normal file " + s + " not found in directories " + dirs + "; the last abnormal match was " +
                    inappropriateFile);
        }
        throw new FileNotFoundException("File " + s + " not found in directories " + dirs);
    }

    public String findDirName(String s, String defaultDir) {
        try {
            return findDir(s).getCanonicalPath();
        }
        catch (IOException ex) {
            return defaultDir;
        }
    }

    public File findDir(String s) throws FileNotFoundException {
        File inappropriateFile = null;
        if (s.startsWith("/") || s.startsWith(File.separator)) {
            File f = new File(s);
            if (f.isDirectory()) {
                return f;
            } else if (f.exists()) {
                inappropriateFile = f;
            }
        } else {
            for (File d : dirs) {
                File f = new File(d, s);
                if (f.isDirectory()) {
                    return f;
                } else if (f.exists()) {
                    inappropriateFile = f;
                }
            }
        }
        if (inappropriateFile != null) {
            throw new FileNotFoundException("Directory " + s + " not found in directories " + dirs +
                                            "; the last non-directory name match was " + inappropriateFile);
        }
        throw new FileNotFoundException("File " + s + " not found in directories " + dirs);
    }

    public int length() {
        return dirs.size();
    }
}
