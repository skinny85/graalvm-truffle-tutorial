package com.endoflineblog.truffle.part_11;

public final class FastaCode {
    private static final String ALU = "" +
            "GGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGG" +
            "GAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGTTCGAGA" +
            "CCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAAT" +
            "ACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCA" +
            "GCTACTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACCCGGG" +
            "AGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCC" +
            "AGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAA";

    public static final String FASTA_PROGRAM = "var ALU = '" + ALU + "'; " +
            "function fastaRepeat(n) { " +
            "    var ret = 0, seqi = 0, lenOut = 60; " +
            "                                        " +
            "    while (n > 0) { " +
            "        if (n < lenOut) " +
            "            lenOut = n; " +
            "                        " +
            "        if (seqi + lenOut < ALU.length) { " +
            "            ret = ret + ALU.substring(seqi, seqi + lenOut).length; " +
            "            seqi = seqi + lenOut; " +
            "        } else { " +
            "            var s = ALU.substring(seqi); " +
            "            seqi = lenOut - s.length; " +
            "            ret = ret + (s + ALU.substring(0, seqi)).length; " +
            "        } " +
            "          " +
            "        n = n - lenOut; " +
            "    } " +
            "      " +
            "    return ret; " +
            "}";

    public static final String FASTA_PROGRAM_NO_SUBSTRING = "var ALU1 = '" +  ALU + "'; " +
            "function fastaRepeatNoSubstring(n) { " +
            "    var ret = 0, seqi = 0, lenOut = 60; " +
            "                                        " +
            "    while (n > 0) { " +
            "        if (n < lenOut) " +
            "            lenOut = n; " +
            "                        " +
            "        if (seqi + lenOut < ALU1.length) { " +
            "            ret = ret + lenOut; " +
            "            seqi = seqi + lenOut; " +
            "        } else { " +
            "            var s = ALU1.length - seqi; " +
            "            seqi = seqi + lenOut - ALU1.length; " +
            "            ret = ret + seqi + s; " +
            "        } " +
            "          " +
            "        n = n - lenOut; " +
            "    } " +
            "      " +
            "    return ret; " +
            "}";

    public static final String FASTA_PROGRAM_WITHOUT_LENGTH = "" +
            "function fastaRepeatWithoutLength(n) { " +
            "    var ret = 0, seqi = 0, lenOut = 60; " +
            "                                        " +
            "    while (n > 0) { " +
            "        if (n < lenOut) " +
            "            lenOut = n; " +
            "                        " +
            "        if (seqi + lenOut < 287) { " +
            "            ret = ret + lenOut; " +
            "            seqi = seqi + lenOut; " +
            "        } else { " +
            "            var s = 287 - seqi; " +
            "            seqi = seqi + lenOut - 287; " +
            "            ret = ret + seqi + s; " +
            "        } " +
            "          " +
            "        n = n - lenOut; " +
            "    } " +
            "      " +
            "    return ret; " +
            "}";

    public static int fastaRepeat(int n) {
        var ret = 0;
        var seqi = 0;
        var lenOut = 60;

        while (n > 0) {
            if (n < lenOut)
                lenOut = n;

            if (seqi + lenOut < ALU.length()) {
                ret = ret + ALU.substring(seqi, seqi + lenOut).length();
                seqi = seqi + lenOut;
            } else {
                var s = ALU.substring(seqi);
                seqi = lenOut - s.length();
                ret = ret + (s + ALU.substring(0, seqi)).length();
            }

            n = n - lenOut;
        }

        return ret;
    }

    public static int fastaRepeatNoSubstring(int n) {
        var ret = 0;
        var seqi = 0;
        var lenOut = 60;

        while (n > 0) {
            if (n < lenOut)
                lenOut = n;

            if (seqi + lenOut < ALU.length()) {
                ret = ret + lenOut;
                seqi = seqi + lenOut;
            } else {
                var s = ALU.length() - seqi;
                seqi = seqi + lenOut - ALU.length();
                ret = ret + seqi + s;
            }

            n = n - lenOut;
        }

        return ret;
    }

    public static int fastaRepeatWithoutLength(int n) {
        var ret = 0;
        var seqi = 0;
        var lenOut = 60;

        while (n > 0) {
            if (n < lenOut)
                lenOut = n;

            if (seqi + lenOut < 287) {
                ret = ret + lenOut;
                seqi = seqi + lenOut;
            } else {
                var s = 287 - seqi;
                seqi = seqi + lenOut - 287;
                ret = ret + seqi + s;
            }

            n = n - lenOut;
        }

        return ret;
    }
}