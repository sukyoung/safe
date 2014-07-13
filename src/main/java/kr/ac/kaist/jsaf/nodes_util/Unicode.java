/*******************************************************************************
    Copyright 2008, Oracle and/or its affiliates.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util;

import java.util.HashMap;

public class Unicode {
    public static boolean charactersOverlap(String s1, String s2) {
        for (int i = 0; i < s1.length(); i++) {
            if (s2.indexOf(s1.charAt(i)) >= 0) {
                return true;
            }
        }
        return false;
    }

    public static String byNameLC(String s) {
        return unicodeByName.get(s.toLowerCase());
    }

    public static int numberToValue(String s) {
        Integer I = numbers.get(s.toUpperCase());
        if (I == null) {
            return -1;
        }
        return I.intValue();
    }

    private static HashMap<String, String> unicodeByName = new HashMap<String, String>();

    private static HashMap<String, Integer> numbers = new HashMap<String, Integer>();
    static {
        HashMap<String, Integer> m = numbers;
        m.put("ZERO", Integer.valueOf(0));
        m.put("ONE", Integer.valueOf(1));
        m.put("TWO", Integer.valueOf(2));
        m.put("THREE", Integer.valueOf(3));
        m.put("FOUR", Integer.valueOf(4));
        m.put("FIVE", Integer.valueOf(5));
        m.put("SIX", Integer.valueOf(6));
        m.put("SEVEN", Integer.valueOf(7));
        m.put("EIGHT", Integer.valueOf(8));
        m.put("NINE", Integer.valueOf(9));
        m.put("TEN", Integer.valueOf(10));
        m.put("ELEVEN", Integer.valueOf(11));
        m.put("TWELVE", Integer.valueOf(12));
        m.put("THIRTEEN", Integer.valueOf(13));
        m.put("FOURTEEN", Integer.valueOf(14));
        m.put("FIFTEEN", Integer.valueOf(15));
        m.put("SIXTEEN", Integer.valueOf(16));
    }
    static {
        HashMap<String, String> m = unicodeByName;
        m.put("alpha", "\u03b1");
        m.put("beta", "\u03b2");
        m.put("gamma", "\u03b3");
        m.put("delta", "\u03b4");
        m.put("epsilon", "\u03b5");
        m.put("zeta", "\u03b6");
        m.put("eta", "\u03b7");
        m.put("theta", "\u03b8");
        m.put("iota", "\u03b9");
        m.put("kappa", "\u03ba");
        m.put("lambda", "\u03bb");
        m.put("lamda", "\u03bb");
        m.put("mu", "\u03bc");
        m.put("nu", "\u03bd");
        m.put("xi", "\u03be");
        m.put("omicron", "\u03bf");
        m.put("pi", "\u03c0");
        m.put("rho", "\u03c1");
        m.put("final sigma", "\u03c2");
        m.put("sigma", "\u03c3");
        m.put("tau", "\u03c4");
        m.put("upsilon", "\u03c5");
        m.put("phi", "\u03c6");
        m.put("chi", "\u03c7");
        m.put("psi", "\u03c8");
        m.put("omega", "\u03c9");
    }
}
