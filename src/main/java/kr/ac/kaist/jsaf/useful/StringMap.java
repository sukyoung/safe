/*******************************************************************************
    Copyright 2008,2010, Oracle and/or its affiliates.
    All rights reserved.


    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.useful;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;

public interface StringMap {
 
    String get(String s);

    boolean isEmpty();

    String getCompletely(String s, int limit);

    String getCompletely(String s);

    public int getInt(String s, int ifMissing);

    public long getLong(String s, long ifMissing);

    public boolean getBoolean(String s, boolean ifMissing);

    public String get(String s, String ifMissing);

    static abstract class FromBase implements StringMap {
        public String getCompletely(String s, int limit) {
            if (s == null) return s;
            return Useful.substituteVarsCompletely(s, this, limit);
        }

        public int getInt(String s, int ifMissing) {
            String result = get(s);
            if (result != null) result = getCompletely(result, 1000);
            if (result == null) return ifMissing;
            if (result.length() == 0) return ifMissing;
            int base = 10;
            int underat = result.indexOf('_');
            if (underat != -1) {
                base = Integer.parseInt(result.substring(underat + 1));
                result = result.substring(0, underat);
            }
            return Integer.parseInt(result, base);
        }


        public long getLong(String s, long ifMissing) {
            String result = get(s);
            if (result != null) result = getCompletely(result, 1000);
            if (result == null) return ifMissing;
            if (result.length() == 0) return ifMissing;
            int base = 10;
            int underat = result.indexOf('_');
            if (underat != -1) {
                base = Integer.parseInt(result.substring(underat + 1));
                result = result.substring(0, underat);
            }
            return Long.parseLong(result, base);
        }

        final public boolean getBoolean(String s, boolean ifMissing) {
            String result = get(s);
            if (result != null) result = getCompletely(result, 1000);
            if (result == null) return ifMissing;
            if (result.length() == 0) return true;
            s = result.toLowerCase();
            char c = result.charAt(0);
            if (c == 'y' || c == 't' || c == '1') return true;
            if (c == 'n' || c == 'f' || c == '0') return false;

            throw new Error("Unexpected definition of prop/env " + s + ", got " + result + ", need t/f/y/n/1/0[...]");
        }

        final public String get(String s, String ifMissing) {
            String result = get(s);
            if (result == null) result = ifMissing;
            if (result != null) result = getCompletely(result, 1000);
            if (result == null) {
                throw new Error(
                        "Must supply a definition (as property, environment variable, or repository configuration property) for " +
                        s);
            }
            return result;
        }


        public String getCompletely(String s) {
            return getCompletely(s, 1000);
        }

        abstract public String get(String s);

        abstract public boolean isEmpty();
    }

    static public class FromEnv extends FromBase implements StringMap {
        public String get(String s) {
            s = asEnvOrReflect(s);
            String t = System.getenv(s);
            return t;
        }

        public boolean isEmpty() {
            return false;
        }

        public static String asEnvOrReflect(String s) {
            s = s.toUpperCase();
            s = s.replace('.', '_');
            s = s.replace('-', '_');
            return s;
        }
    }

    static public class FromProps extends FromBase implements StringMap {
        Properties p;

        FromProps(Properties p) {
            this.p = p;
        }

        public String get(String s) {
            return p.getProperty(s);
        }

        public boolean isEmpty() {
            return p == null;
        }
    }

    static public class FromMap extends FromBase implements StringMap {
        Map<String, String> p;

        public FromMap(Map<String, String> p) {
            this.p = p;
        }

        public String get(String s) {
            return p.get(s);
        }

        public boolean isEmpty() {
            return p == null;
        }

    }

    static public class FromSysProps extends FromBase implements StringMap {
        public String get(String s) {
            return System.getProperty(s);
        }

        public boolean isEmpty() {
            return false;
        }
    }

    static public class FromFileProps extends FromProps implements StringMap {
        static Properties fromFile(String filename) {
            Properties p = null;
            try {
                FileInputStream fs = new FileInputStream(filename);
                InputStreamReader ir = new InputStreamReader(fs, Charset.forName("UTF-8"));
                BufferedReader bis = new BufferedReader(ir);
                Properties tmp_p = new Properties();
                tmp_p.load(bis);
                p = tmp_p; // Assign if no exception.
                bis.close(); ir.close(); fs.close();
            }
            catch (IOException ex) {

            }
            return p;
        }

        public FromFileProps(String filename) {
            super(fromFile(filename));
        }

    }

    static public class FromReflection extends FromBase implements StringMap {

        private final Class mapClass;
        private final String optionalPrefix;

        public FromReflection(Class cl) {
            mapClass = cl;
            optionalPrefix = null;
        }

        public FromReflection(Class cl, String optional_prefix) {
            mapClass = cl;
            optionalPrefix = optional_prefix;
        }

        public String get(String s) {
            String result = getOne(s);
            if (result == null && optionalPrefix != null && s.startsWith(optionalPrefix)) {
                result = getOne(s.substring(optionalPrefix.length()));
            }
            return result;

        }

        private String getOne(String s) {
            try {
                s = FromEnv.asEnvOrReflect(s);
                Field f = mapClass.getDeclaredField(s);
                return f.get(null).toString();
            }
            catch (SecurityException e) {
                return null;
            }
            catch (NoSuchFieldException e) {
                return null;
            }
            catch (IllegalArgumentException e) {
                return null;
            }
            catch (IllegalAccessException e) {
                return null;
            }
            catch (NullPointerException e) {
                return null;
            }

        }

        public boolean isEmpty() {
            // TODO Auto-generated method stub
            return false;
        }

    }

    static public class ComposedMaps extends FromBase implements StringMap {
        private final StringMap[] ma;

        public String get(String s) {
            String a = null;
            for (StringMap m : ma) {
                a = m.get(s);
                if (a != null) return a;
            }
            return a;
        }

        public boolean isEmpty() {
            return ma.length == 0;
        }

        public ComposedMaps(StringMap... maps) {
            int ne = 0;
            for (StringMap m : maps) {
                if (!m.isEmpty()) ne++;
            }
            ma = new StringMap[ne];
            ne = 0;
            for (StringMap m : maps) {
                if (!m.isEmpty()) {
                    ma[ne] = m;
                    ne++;
                }
            }
        }
    }
    
    public class FromPair extends FromBase {

        private final  String from;
        private final  String to;
        
        
        public FromPair(String from, String to) {
            this.from = from;
            this.to = to;
        }
        
        @Override
        public String get(String s) {
            return s.equals(from) ? to : null;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }


    }

}
