package com.endoflineblog.truffle.part_11;

public final class FastaCode {
    public static final String FASTA_PROGRAM = "" +
            "var ALU = " +
            "    'GGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGG' + " +
            "    'GAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGTTCGAGA' + " +
            "    'CCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAAT' + " +
            "    'ACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCA' + " +
            "    'GCTACTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACCCGGG' + " +
            "    'AGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCC' + " +
            "    'AGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAA';         " +
            "                                                   " +
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

    static final String ALU = "" +
            "GGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGG" +
            "GAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGTTCGAGA" +
            "CCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAAT" +
            "ACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCA" +
            "GCTACTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACCCGGG" +
            "AGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCC" +
            "AGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAA";

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
}
