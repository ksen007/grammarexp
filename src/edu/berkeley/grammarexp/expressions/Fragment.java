package edu.berkeley.grammarexp.expressions;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 1/6/16
 * Time: 9:29 PM
 */

// https://swtch.com/~rsc/regexp/regexp1.html

public class Fragment {
    State start;
    List<DanglingState> out;

    public Fragment(State start, List<DanglingState> out) {
        this.start = start;
        this.out = out;
    }

    public static Fragment literal(int c) {
        State s = new State(c, null, null, false, false);
        List<DanglingState> l = new LinkedList<DanglingState>();
        l.add(new DanglingState(s, true));
        return new Fragment(s, l);
    }

    public static Fragment concatenate(Fragment e1, Fragment e2) {
        for(DanglingState ds: e1.out) {
            ds.patch(e2.start);
        }
        return new Fragment(e1.start, e2.out);
    }

    public static Fragment alternate(Fragment e1, Fragment e2) {
        State s = new State(0, e1.start, e2.start, false, true);
        List<DanglingState> l = new LinkedList<DanglingState>();
        l.addAll(e1.out);
        l.addAll(e2.out);
        return new Fragment(s, l);
    }

    public static Fragment star(Fragment e) {
        State s = new State(0, e.start, null, false, true);
        for(DanglingState ds: e.out) {
            ds.patch(s);
        }
        List<DanglingState> l = new LinkedList<DanglingState>();
        l.add(new DanglingState(s, false));
        return new Fragment(s, l);
    }

    public static Fragment question(Fragment e) {
        State s = new State(0, e.start, null, false, true);
        List<DanglingState> l = new LinkedList<DanglingState>();
        l.addAll(e.out);
        l.add(new DanglingState(s, false));
        return new Fragment(s, l);
    }

    public static Fragment plus(Fragment e) {
        State s = new State(0, e.start, null, false, true);
        for(DanglingState ds: e.out) {
            ds.patch(s);
        }
        List<DanglingState> l = new LinkedList<DanglingState>();
        l.add(new DanglingState(s, false));
        return new Fragment(e.start, l);
    }

}
