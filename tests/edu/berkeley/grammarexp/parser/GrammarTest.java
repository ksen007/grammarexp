package edu.berkeley.grammarexp.parser;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 11/30/15
 * Time: 8:10 PM
 */
public class GrammarTest {


    @Test
    public void test1() throws Exception {
        Grammar g = new Grammar();
        int X = g.getNonTerminalID("X");
        int Y = g.getNonTerminalID("Y");
        int Z = g.getNonTerminalID("Z");

        g.addProduction(X, Y);
        g.addProduction(X, "a");

        g.addProduction(Y);
        g.addProduction(Y, "c");

        g.addProduction(Z, "d");
        g.addProduction(Z, X, Y, Z);

        //System.out.println(g);
        FirstFollow ff = new FirstFollow(g);
        ff.computeFirstFollowSets(true);
        String expected = "Start: First = {c, a}  Follow = {}  Nullable = true\n" +
                "X: First = {c, a}  Follow = {d, c, a}  Nullable = true\n" +
                "Y: First = {c}  Follow = {d, c, a}  Nullable = true\n" +
                "Z: First = {d, c, a}  Follow = {}  Nullable = false\n";
        String actual = ff.getFirstFollowAsString();
        assertEquals(expected, actual);
    }

    @Test
    public void test2() throws Exception {
        Grammar g = new Grammar();

        int S = g.getNonTerminalID("S");
        int E = g.getNonTerminalID("E");
        int Ep = g.getNonTerminalID("E'");
        int T = g.getNonTerminalID("T");
        int Tp = g.getNonTerminalID("T'");
        int F = g.getNonTerminalID("F");

        g.addProduction(S, E);

        g.addProduction(E, T, Ep);

        g.addProduction(Ep, "+", T, Ep);
        g.addProduction(Ep, "-", T, Ep);
        g.addProduction(Ep);

        g.addProduction(T, F, Tp);
        g.addProduction(Tp, "/", F, Tp);
        g.addProduction(Tp, "*", F, Tp);
        g.addProduction(Tp);

        g.addProduction(F, "n");
        g.addProduction(F, "i");
        g.addProduction(F, "(", E, ")");

        //System.out.println(g);
        FirstFollow ff = new FirstFollow(g);
        ff.computeFirstFollowSets(true);
        String expected = "Start: First = {n, i, (}  Follow = {}  Nullable = false\n" +
                "S: First = {n, i, (}  Follow = {}  Nullable = false\n" +
                "E: First = {n, i, (}  Follow = {)}  Nullable = false\n" +
                "E': First = {-, +}  Follow = {)}  Nullable = true\n" +
                "T: First = {n, i, (}  Follow = {-, +, )}  Nullable = false\n" +
                "T': First = {/, *}  Follow = {-, +, )}  Nullable = true\n" +
                "F: First = {n, i, (}  Follow = {/, -, +, *, )}  Nullable = false\n";
        String actual = ff.getFirstFollowAsString();
        assertEquals(expected, actual);

    }

    @Test
    public void testLR0() throws Exception {
        Grammar g = new Grammar();
        int N = g.getNonTerminalID("N");
        int V = g.getNonTerminalID("V");
        int E = g.getNonTerminalID("E");

        g.addProduction(N, V, "=", E);
        g.addProduction(N, E);
        g.addProduction(E, V);
        g.addProduction(V, "x");
        g.addProduction(V, "*", E);

        String expected = "I0:Start ::=  N , location = 0\n" +
                "N ::=  V \"=\" E , location = 0\n" +
                "N ::=  E , location = 0\n" +
                "E ::=  V , location = 0\n" +
                "V ::= \"x\", location = 0\n" +
                "V ::= \"*\" E , location = 0\n" +
                "\n" +
                "I1:V ::= \"x\", location = 1\n" +
                "\n" +
                "I2:E ::=  V , location = 0\n" +
                "V ::= \"x\", location = 0\n" +
                "V ::= \"*\" E , location = 0\n" +
                "V ::= \"*\" E , location = 1\n" +
                "\n" +
                "I3:Start ::=  N , location = 1\n" +
                "\n" +
                "I4:N ::=  V \"=\" E , location = 1\n" +
                "E ::=  V , location = 1\n" +
                "\n" +
                "I5:N ::=  E , location = 1\n" +
                "\n" +
                "I6:E ::=  V , location = 1\n" +
                "\n" +
                "I7:V ::= \"*\" E , location = 2\n" +
                "\n" +
                "I8:N ::=  V \"=\" E , location = 2\n" +
                "E ::=  V , location = 0\n" +
                "V ::= \"x\", location = 0\n" +
                "V ::= \"*\" E , location = 0\n" +
                "\n" +
                "I9:N ::=  V \"=\" E , location = 3\n" +
                "\n" +
                "[{\n" +
                "\"I0\" : {\"x\" : \"I1\", \"*\" : \"I2\", \"N\" : \"I3\", \"V\" : \"I4\", \"E\" : \"I5\"}\n" +
                "\"I1\" : {}\n" +
                "\"I2\" : {\"x\" : \"I1\", \"*\" : \"I2\", \"V\" : \"I6\", \"E\" : \"I7\"}\n" +
                "\"I3\" : {}\n" +
                "\"I4\" : {\"=\" : \"I8\"}\n" +
                "\"I5\" : {}\n" +
                "\"I6\" : {}\n" +
                "\"I7\" : {}\n" +
                "\"I8\" : {\"x\" : \"I1\", \"*\" : \"I2\", \"V\" : \"I6\", \"E\" : \"I9\"}\n" +
                "\"I9\" : {}\n" +
                "},\n" +
                "{\n" +
                "\"I0\" : {}]\n" +
                "\"I1\" : {\"=\" : [V ::= \"x\"], \"$\" : [V ::= \"x\"]}]\n" +
                "\"I2\" : {}]\n" +
                "\"I3\" : {\"$\" : [Start ::=  N ]}]\n" +
                "\"I4\" : {\"$\" : [E ::=  V ]}]\n" +
                "\"I5\" : {\"$\" : [N ::=  E ]}]\n" +
                "\"I6\" : {\"=\" : [E ::=  V ], \"$\" : [E ::=  V ]}]\n" +
                "\"I7\" : {\"=\" : [V ::= \"*\" E ], \"$\" : [V ::= \"*\" E ]}]\n" +
                "\"I8\" : {}]\n" +
                "\"I9\" : {\"$\" : [N ::=  V \"=\" E ]}]\n" +
                "}\n";
        String actual = LALR.generateLALRTables(g).toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testLR0_2() throws Exception {
        Grammar g = new Grammar();
        int S = g.getNonTerminalID("S");
        int L = g.getNonTerminalID("L");

        g.addProduction(S, "(", L, ")");
        g.addProduction(S, "x");
        g.addProduction(L, S);
        g.addProduction(L, L, ",", S);

        String expected = "I0:Start ::=  S , location = 0\n" +
                "S ::= \"(\" L \")\", location = 0\n" +
                "S ::= \"x\", location = 0\n" +
                "\n" +
                "I1:S ::= \"x\", location = 1\n" +
                "\n" +
                "I2:S ::= \"(\" L \")\", location = 0\n" +
                "S ::= \"(\" L \")\", location = 1\n" +
                "S ::= \"x\", location = 0\n" +
                "L ::=  S , location = 0\n" +
                "L ::=  L \",\" S , location = 0\n" +
                "\n" +
                "I3:Start ::=  S , location = 1\n" +
                "\n" +
                "I4:L ::=  S , location = 1\n" +
                "\n" +
                "I5:S ::= \"(\" L \")\", location = 2\n" +
                "L ::=  L \",\" S , location = 1\n" +
                "\n" +
                "I6:S ::= \"(\" L \")\", location = 0\n" +
                "S ::= \"x\", location = 0\n" +
                "L ::=  L \",\" S , location = 2\n" +
                "\n" +
                "I7:S ::= \"(\" L \")\", location = 3\n" +
                "\n" +
                "I8:L ::=  L \",\" S , location = 3\n" +
                "\n" +
                "[{\n" +
                "\"I0\" : {\"x\" : \"I1\", \"(\" : \"I2\", \"S\" : \"I3\"}\n" +
                "\"I1\" : {}\n" +
                "\"I2\" : {\"x\" : \"I1\", \"(\" : \"I2\", \"S\" : \"I4\", \"L\" : \"I5\"}\n" +
                "\"I3\" : {}\n" +
                "\"I4\" : {}\n" +
                "\"I5\" : {\",\" : \"I6\", \")\" : \"I7\"}\n" +
                "\"I6\" : {\"x\" : \"I1\", \"(\" : \"I2\", \"S\" : \"I8\"}\n" +
                "\"I7\" : {}\n" +
                "\"I8\" : {}\n" +
                "},\n" +
                "{\n" +
                "\"I0\" : {}]\n" +
                "\"I1\" : {\",\" : [S ::= \"x\"], \")\" : [S ::= \"x\"], \"$\" : [S ::= \"x\"]}]\n" +
                "\"I2\" : {}]\n" +
                "\"I3\" : {\"$\" : [Start ::=  S ]}]\n" +
                "\"I4\" : {\",\" : [L ::=  S ], \")\" : [L ::=  S ]}]\n" +
                "\"I5\" : {}]\n" +
                "\"I6\" : {}]\n" +
                "\"I7\" : {\",\" : [S ::= \"(\" L \")\"], \")\" : [S ::= \"(\" L \")\"], \"$\" : [S ::= \"(\" L \")\"]}]\n" +
                "\"I8\" : {\",\" : [L ::=  L \",\" S ], \")\" : [L ::=  L \",\" S ]}]\n" +
                "}\n";
        String actual = LALR.generateLALRTables(g).toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testLALR1() throws Exception {
        Grammar g = new Grammar();
        int S = g.getNonTerminalID("S");
        int L = g.getNonTerminalID("L");
        int R = g.getNonTerminalID("R");

        g.addProduction(S, L, "=", R);
        g.addProduction(S, R);
        g.addProduction(L, "*", R);
        g.addProduction(L, "i");
        g.addProduction(R, L);

        String expected = "I0:Start ::=  S , location = 0\n" +
                "S ::=  L \"=\" R , location = 0\n" +
                "S ::=  R , location = 0\n" +
                "L ::= \"*\" R , location = 0\n" +
                "L ::= \"i\", location = 0\n" +
                "R ::=  L , location = 0\n" +
                "\n" +
                "I1:L ::= \"i\", location = 1\n" +
                "\n" +
                "I2:L ::= \"*\" R , location = 0\n" +
                "L ::= \"*\" R , location = 1\n" +
                "L ::= \"i\", location = 0\n" +
                "R ::=  L , location = 0\n" +
                "\n" +
                "I3:Start ::=  S , location = 1\n" +
                "\n" +
                "I4:S ::=  L \"=\" R , location = 1\n" +
                "R ::=  L , location = 1\n" +
                "\n" +
                "I5:S ::=  R , location = 1\n" +
                "\n" +
                "I6:R ::=  L , location = 1\n" +
                "\n" +
                "I7:L ::= \"*\" R , location = 2\n" +
                "\n" +
                "I8:S ::=  L \"=\" R , location = 2\n" +
                "L ::= \"*\" R , location = 0\n" +
                "L ::= \"i\", location = 0\n" +
                "R ::=  L , location = 0\n" +
                "\n" +
                "I9:S ::=  L \"=\" R , location = 3\n" +
                "\n" +
                "[{\n" +
                "\"I0\" : {\"i\" : \"I1\", \"*\" : \"I2\", \"S\" : \"I3\", \"L\" : \"I4\", \"R\" : \"I5\"}\n" +
                "\"I1\" : {}\n" +
                "\"I2\" : {\"i\" : \"I1\", \"*\" : \"I2\", \"L\" : \"I6\", \"R\" : \"I7\"}\n" +
                "\"I3\" : {}\n" +
                "\"I4\" : {\"=\" : \"I8\"}\n" +
                "\"I5\" : {}\n" +
                "\"I6\" : {}\n" +
                "\"I7\" : {}\n" +
                "\"I8\" : {\"i\" : \"I1\", \"*\" : \"I2\", \"L\" : \"I6\", \"R\" : \"I9\"}\n" +
                "\"I9\" : {}\n" +
                "},\n" +
                "{\n" +
                "\"I0\" : {}]\n" +
                "\"I1\" : {\"=\" : [L ::= \"i\"], \"$\" : [L ::= \"i\"]}]\n" +
                "\"I2\" : {}]\n" +
                "\"I3\" : {\"$\" : [Start ::=  S ]}]\n" +
                "\"I4\" : {\"$\" : [R ::=  L ]}]\n" +
                "\"I5\" : {\"$\" : [S ::=  R ]}]\n" +
                "\"I6\" : {\"=\" : [R ::=  L ], \"$\" : [R ::=  L ]}]\n" +
                "\"I7\" : {\"=\" : [L ::= \"*\" R ], \"$\" : [L ::= \"*\" R ]}]\n" +
                "\"I8\" : {}]\n" +
                "\"I9\" : {\"$\" : [S ::=  L \"=\" R ]}]\n" +
                "}\n";
        String actual = LALR.generateLALRTables(g).toString();
        assertEquals(expected, actual);
    }

    @Test
    public void testLALR2() throws Exception {
        Grammar g = new Grammar();

        int S = g.getNonTerminalID("S");
        int V = g.getNonTerminalID("V");
        int E = g.getNonTerminalID("E");

        g.addProduction(S, V, "=", E);
        g.addProduction(S, E);
        g.addProduction(E, V);
        g.addProduction(V, "x");
        g.addProduction(V, "*", E);
        String expected = "I0:Start ::=  S , location = 0\n" +
                "S ::=  V \"=\" E , location = 0\n" +
                "S ::=  E , location = 0\n" +
                "E ::=  V , location = 0\n" +
                "V ::= \"x\", location = 0\n" +
                "V ::= \"*\" E , location = 0\n" +
                "\n" +
                "I1:V ::= \"x\", location = 1\n" +
                "\n" +
                "I2:E ::=  V , location = 0\n" +
                "V ::= \"x\", location = 0\n" +
                "V ::= \"*\" E , location = 0\n" +
                "V ::= \"*\" E , location = 1\n" +
                "\n" +
                "I3:Start ::=  S , location = 1\n" +
                "\n" +
                "I4:S ::=  V \"=\" E , location = 1\n" +
                "E ::=  V , location = 1\n" +
                "\n" +
                "I5:S ::=  E , location = 1\n" +
                "\n" +
                "I6:E ::=  V , location = 1\n" +
                "\n" +
                "I7:V ::= \"*\" E , location = 2\n" +
                "\n" +
                "I8:S ::=  V \"=\" E , location = 2\n" +
                "E ::=  V , location = 0\n" +
                "V ::= \"x\", location = 0\n" +
                "V ::= \"*\" E , location = 0\n" +
                "\n" +
                "I9:S ::=  V \"=\" E , location = 3\n" +
                "\n" +
                "[{\n" +
                "\"I0\" : {\"x\" : \"I1\", \"*\" : \"I2\", \"S\" : \"I3\", \"V\" : \"I4\", \"E\" : \"I5\"}\n" +
                "\"I1\" : {}\n" +
                "\"I2\" : {\"x\" : \"I1\", \"*\" : \"I2\", \"V\" : \"I6\", \"E\" : \"I7\"}\n" +
                "\"I3\" : {}\n" +
                "\"I4\" : {\"=\" : \"I8\"}\n" +
                "\"I5\" : {}\n" +
                "\"I6\" : {}\n" +
                "\"I7\" : {}\n" +
                "\"I8\" : {\"x\" : \"I1\", \"*\" : \"I2\", \"V\" : \"I6\", \"E\" : \"I9\"}\n" +
                "\"I9\" : {}\n" +
                "},\n" +
                "{\n" +
                "\"I0\" : {}]\n" +
                "\"I1\" : {\"=\" : [V ::= \"x\"], \"$\" : [V ::= \"x\"]}]\n" +
                "\"I2\" : {}]\n" +
                "\"I3\" : {\"$\" : [Start ::=  S ]}]\n" +
                "\"I4\" : {\"$\" : [E ::=  V ]}]\n" +
                "\"I5\" : {\"$\" : [S ::=  E ]}]\n" +
                "\"I6\" : {\"=\" : [E ::=  V ], \"$\" : [E ::=  V ]}]\n" +
                "\"I7\" : {\"=\" : [V ::= \"*\" E ], \"$\" : [V ::= \"*\" E ]}]\n" +
                "\"I8\" : {}]\n" +
                "\"I9\" : {\"$\" : [S ::=  V \"=\" E ]}]\n" +
                "}\n";
        String actual = LALR.generateLALRTables(g).toString();
        assertEquals(expected, actual);
        //System.out.println(actual);
    }
}