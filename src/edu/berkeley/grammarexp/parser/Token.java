package edu.berkeley.grammarexp.parser;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/5/15
 * Time: 10:40 AM
 */
public class Token {
    private Object val;

    public Token(Object val) {
        this.val = val;
    }

    @Override
    public int hashCode() {
        return val.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Token)) return false;
        return this.val.equals(((Token)obj).val);
    }

    @Override
    public String toString() {
        return val.toString();
    }
}
