package edu.berkeley.grammarexp.parser;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/5/15
 * Time: 2:13 AM
 */
public class Pair<F,S> {
    public F first;
    public S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }
}
