package edu.berkeley.grammarexp.expressions;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 1/7/16
 * Time: 1:15 AM
 */
public class StateList {
    int n;
    State[] s;

    public StateList(int sz) {
        s = new State[sz];
        n = 0;
    }
}
