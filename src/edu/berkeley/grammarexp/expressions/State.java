package edu.berkeley.grammarexp.expressions;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 1/6/16
 * Time: 9:28 PM
 */
public class State {
    int c;
    State out;
    State out1;
    boolean isMatch;
    boolean isSplit;
    static int n = 0;
    int lastlist;

    public State(int c, State out, State out1, boolean isMatch, boolean isSplit) {
        this.c = c;
        this.out = out;
        this.out1 = out1;
        this.isMatch = isMatch;
        this.isSplit = isSplit;
        n++;
    }
}
