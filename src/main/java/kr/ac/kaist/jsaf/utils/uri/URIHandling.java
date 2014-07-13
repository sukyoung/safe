/*******************************************************************************
 Copyright (c) 2013, KAIST.
 All rights reserved.

 Use is subject to license terms.

 This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.utils.uri;

public class URIHandling {
    public static String encodeURIString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'();/?:@&=+$,#";
    public static String encodeURIComponentString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'()";
    public static String decodeURIString = ";/?:@&=+$,#";
    public static String decodeURIComponentString = "";

    /* Convert one UCS-4 char and write it into a UTF-8 buffer, which must be
     * at least 6 bytes long.  Return the number of UTF-8 bytes of data written.
     */
    private static int oneUcs4ToUtf8Char(byte[] utf8Buffer, int ucs4Char) {
        int utf8Length = 1;

        if ((ucs4Char & ~0x7F) == 0)
            utf8Buffer[0] = (byte)ucs4Char;
        else {
            int i;
            int a = ucs4Char >>> 11;
            utf8Length = 2;
            while (a != 0) {
                a >>>= 5;
                utf8Length++;
            }
            i = utf8Length;
            while (--i > 0) {
                utf8Buffer[i] = (byte)((ucs4Char & 0x3F) | 0x80);
                ucs4Char >>>= 6;
            }
            utf8Buffer[0] = (byte)(0x100 - (1 << (8-utf8Length)) + ucs4Char);
        }
        return utf8Length;
    }

    private static char toHexChar(int i) {
        return (char)((i < 10) ? i + '0' : i - 10 + 'A');
    }

    public static String encode(String string, String unescapedSet) {
        byte[] Octets = null;
        // 1. Let strLen be the number of characters in string.
        int strLen = string.length();
        // 2. Let R be the empty String.
        StringBuffer R = null;

        // 3. Letkbe0.
        // 4. Repeat
        //    a. If k equals strLen, return R.
        for (int k=0; k < strLen; k++) {
            // b. Let C be the character at position k within string.
            char C = string.charAt(k);

            if (unescapedSet.indexOf(C) >= 0) {
                // c. If C is in unescapedSet, then
                if (R != null) {
                    // i. Let S be a String containing only the character C.
                    // ii. Let R be a new String value computed by concatenating the previous value of R and S.
                    R.append(C);
                }
            } else { // d. Else, C is not in unescapedSet
                if (R == null) {
                    R = new StringBuffer(strLen + 3);
                    R.append(string);
                    R.setLength(k);
                    Octets = new byte[6];
                }

                // i. If the code unit value of C is not less than 0xDC00 and not greater than 0xDFFF,
                if (0xDC00 <= C && C <= 0xDFFF) {
                    return null; // throw a URIError exception
                }
                int V;

                // ii. If the code unit value of C is less than 0xD800 or greater than 0xDBFF, then
                if (C < 0xD800 || 0xDBFF < C) {
                    // 1. Let V be the code unit value of C.
                    V = C;
                } else { // iii. Else,
                    // 1. Increase k by 1.
                    k++;

                    // 2. If k equals strLen, throw a URIError exception.
                    if (k == strLen) {
                        return null; // throw a URIError exception
                    }
                    // 3. Let kChar be the code unit value of the character at position k within string.
                    char kChar = string.charAt(k);
                    // 4. If kChar is less than 0xDC00 or greater than 0xDFFF,
                    if (!(0xDC00 <= kChar && kChar <= 0xDFFF)) {
                        return null; // throw a URIError exception
                    }
                    // 5. Let V be (((the code unit value of C) – 0xD800) X 0x400 + (kChar – 0xDC00) + 0x10000).
                    V = ((C - 0xD800) << 10) + (kChar - 0xDC00) + 0x10000;
                }
                // iv. Let Octets be the array of octets resulting by applying the UTF-8 transformation to V, and let L be the array size.
                int L = oneUcs4ToUtf8Char(Octets, V);
                // v. Let j be 0.
                // vi. Repeat, while j < L
                for (int j = 0; j < L; j++) {
                    // 1. Let jOctet be the value at position j within Octets.
                    int jOctet = 0xff & Octets[j];
                    // 2. Let S be a String containing three characters "%XY" where XY are two uppercase hexadecimal digits encoding the value of jOctet.
                    // 3. Let R be a new String value computed by concatenating the previous value of R and S.
                    R.append('%');
                    R.append(toHexChar(jOctet >>> 4));
                    R.append(toHexChar(jOctet & 0xf));
                }
            }
        }
        return (R == null) ? string : R.toString();
    }

    private static int unHex(char c) {
        if ('A' <= c && c <= 'F') {
            return c - 'A' + 10;
        } else if ('a' <= c && c <= 'f') {
            return c - 'a' + 10;
        } else if ('0' <= c && c <= '9') {
            return c - '0';
        } else {
            return -1;
        }
    }

    private static int unHex(char c1, char c2) {
        int i1 = unHex(c1);
        int i2 = unHex(c2);
        if (i1 >= 0 && i2 >= 0) {
            return (i1 << 4) | i2;
        }
        return -1;
    }

    public static String decode(String str, String reservedSet) {
        int bufTop = 0;
        // 1. Let strLen be the number of characters in string.
        int strLen = str.length();
        // 2. Let R be the empty String.
        char[] R = null;

        // 3. Let k be 0.
        // 4. Repeat  a. If k equals strLen, return R.
        for (int k = 0; k < strLen;) {
            // b. Let C be the character at position k within string.
            char C = str.charAt(k);

            // c. If C is not '%', then
            if (C != '%') {
                if (R != null) {
                    // i. Let S be the String containing only the character C.
                    R[bufTop++] = C;
                }
                ++k;
            } else { // d. Else, C is '%'
                if (R == null) {
                    // decode always compress so result can not be bigger then strLen
                    R = new char[strLen];
                    str.getChars(0, k, R, 0);
                    bufTop = k;
                }
                // i. Let start be k.
                int start = k;
                // ii. If k + 2 is greater than or equal to strLen,
                if (k + 3 > strLen)
                    return null;  // throw a URIError exception.

                // iv. Let B be the 8-bit value represented by the two hexadecimal digits at position (k + 1) and (k + 2).
                int B = unHex(str.charAt(k + 1), str.charAt(k + 2));
                // iii. If the characters at position (k+1) and (k + 2) within string do not represent hexadecimal digits,
                if (B < 0) return null; // throw a URIError exception.

                // v. Increment k by 2.
                k += 3;
                // vi. If the most significant bit in B is 0, then
                if ((B & 0x80) == 0) {
                    // 1. Let C be the character with code unit value B.
                    C = (char)B;

                } else { // Else, the most significant bit in B is 1
                    // Decode UTF-8 sequence into Octets and encode it into UTF-16
                    int n, Octets, minUcs4Char;

                    // TODO 1. Let n be the smallest non-negative number such that (B << n) & 0x80 is equal to 0.
                    // TODO 2. If n equals 1 or n is greater than 4,
                    if ((B & 0xC0) == 0x80) {
                        // First  UTF-8 should be ouside 0x80..0xBF
                        return null; // throw a URIError exception.
                    } else if ((B & 0x20) == 0) {
                        n = 1; Octets = B & 0x1F;
                        minUcs4Char = 0x80;
                    } else if ((B & 0x10) == 0) {
                        n = 2; Octets = B & 0x0F;
                        minUcs4Char = 0x800;
                    } else if ((B & 0x08) == 0) {
                        n = 3; Octets = B & 0x07;
                        minUcs4Char = 0x10000;
                    } else if ((B & 0x04) == 0) {
                        n = 4; Octets = B & 0x03;
                        minUcs4Char = 0x200000;
                    } else if ((B & 0x02) == 0) {
                        n = 5; Octets = B & 0x01;
                        minUcs4Char = 0x4000000;
                    } else {
                        // First UTF-8 can not be 0xFF or 0xFE
                        return null;
                    }

                    // TODO 3. Let Octets be an array of 8-bit integers of size n.
                    // TODO 4. Put B into Octets at position 0.

                    // 5. If k + (3 X (n – 1)) is greater than or equal to strLen,
                    if (k + 3 * n > strLen)
                        return null; // throw a URIError exception.

                    // 6. Let j be 1.
                    // 7. Repeat, while j < n
                    for (int j = 0; j != n; j++) {
                        // TODO a Increment k by 1.

                        // b If the character at position k is not '%',
                        if (str.charAt(k) != '%')
                            return null; // throw a URIError exception.

                        // d Let B be the 8-bit value represented by the two hexadecimal digits at position (k + 1) and (k + 2).
                        B = unHex(str.charAt(k + 1), str.charAt(k + 2));
                        // c If the characters at position (k +1) and (k + 2) within string do not represent hexadecimal digits,
                        if (B < 0 || (B & 0xC0) != 0x80)
                            return null; // throw a URIError exception.

                        // TODO e If the two most significant bits in B are not 10, throw a URIError exception.
                        // TODO f Increment k by 2.

                        // g Put B into Octets at position j.
                        Octets = (Octets << 6) | (B & 0x3F);
                        k += 3;
                    }

                    // Check for overlongs and other should-not-present codes
                    if (Octets < minUcs4Char
                            || Octets == 0xFFFE || Octets == 0xFFFF)
                    {
                        Octets = 0xFFFD;
                    }
                    // 10. Else, V is ≥ 0x10000
                    if (Octets >= 0x10000) {
                        // a Let L be (((V – 0x10000) & 0x3FF) + 0xDC00).
                        Octets -= 0x10000;

                        // 8. Let V be the value obtained by applying the UTF-8 transformation to Octets, that is, from an array of octets into a 21-bit value.
                        // If Octets does not contain a valid UTF-8 encoding of a Unicode code point
                        if (Octets > 0xFFFFF)
                            return null;  // throw an URIError exception.

                        // b Let H be ((((V – 0x10000) >> 10) & 0x3FF) + 0xD800).
                        char H = (char)((Octets >>> 10) + 0xD800);

                        // c Let S be the String containing the two characters with code unit values H and L.
                        C = (char)((Octets & 0x3FF) + 0xDC00);

                        R[bufTop++] = H;
                    } else { // 9. If V is less than 0x10000, then
                        // a Let C be the character with code unit value V.
                        C = (char)Octets;
                    }
                }

                // e. Let R be a new String value computed by concatenating the previous value of R and S.
                // b If C is not in reservedSet, then
                //     i. Let S be the String containing only the character C.
                // c Else, C is in reservedSet
                //     i. Let S be the substring of string from position start to position k included.
                if (reservedSet.indexOf(C) >= 0) {
                    for (int x = start; x != k; x++) {
                        R[bufTop++] = str.charAt(x);
                    }
                } else {
                    R[bufTop++] = C;
                }
            }
        }
        return (R == null) ? str : new String(R, 0, bufTop);
    }
}
