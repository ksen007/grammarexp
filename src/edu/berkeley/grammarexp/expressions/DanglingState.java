package edu.berkeley.grammarexp.expressions;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 1/6/16
 * Time: 10:23 PM
 */
public class DanglingState {
    State prev;
    boolean isOut;

    public DanglingState(State prev, boolean isOut) {
        this.prev = prev;
        this.isOut = isOut;
    }

    public void patch(State s) {
        if (isOut) {
            prev.out = s;
        } else {
            prev.out1 = s;
        }
    }
}
