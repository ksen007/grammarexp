package edu.berkeley.grammarexp.parser;

import java.io.IOException;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/8/15
 * Time: 10:08 AM
 */
public abstract class Scanner {
    public int tokenID;
    public Object tokenValue;
    protected Grammar g;

    public Scanner(Grammar g) {
        this.g = g;
    }
    public abstract int nextToken() throws IOException;
    public abstract void close() throws IOException;
}
