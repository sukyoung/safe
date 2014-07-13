/*******************************************************************************
    Copyright 2008,2010, Oracle and/or its affiliates.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
******************************************************************************/

package kr.ac.kaist.jsaf.useful;

import java.io.*;

/*
 * This convenience class provides a simple API for common file actions.
 */
public class Files {
    public static void rm(String name) throws IOException {
        File file = new File(name);
        if (file.exists())
            if (! file.delete())
                throw new IOException();
    }

    public static void mkdir(String name) throws IOException {
        if (! new File(name).mkdir())
            throw new IOException();
    }

    public static void mv(String src, String dest) throws IOException {
        if (! new File(src).renameTo(new File(dest)))
            throw new IOException();
    }

    public static File[] ls(String name) {
        return new File(name).listFiles();
    }

    public static void cp(String src, String dest) throws FileNotFoundException, IOException {
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            input = new FileInputStream(new File(src));
            output = new FileOutputStream(new File(dest));

            for (int next = input.read(); next != -1; next = input.read()) {
                output.write(next);
            }
        } finally {
            try {
                if (input != null) input.close();
            } finally {
                if (output != null) output.close();
            }
        }
    }

    /* Convenience method for creating a BufferedReader from a file name. */
    public static BufferedReader reader(String fileName) throws IOException {
        return new BufferedReader(new FileReader(fileName));
    }

    /* Convenience method for creating a BufferedReader from a file name. */
    public static BufferedWriter writer(String fileName) throws IOException {
        return new BufferedWriter(new FileWriter(fileName));
    }

    public static String windowPathToUnixPath(String filename) {
        //return filename.replaceAll(":", "").replaceAll("\\\\", "/");
        String result = filename.replaceAll("\\\\", "/");
        char drive = result.charAt(0);
        if(Character.isUpperCase(drive)) {
            result = Character.toLowerCase(drive) + result.substring(1);
        }

        return result;
    }
}
