package edu.berkeley.grammarexp.parser;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/5/15
 * Time: 10:42 AM
 */
public class NonTerminal {
    private Object val;

    public NonTerminal(Object val) {
        this.val = val;
    }

    @Override
    public int hashCode() {
        return val.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NonTerminal)) return false;
        return this.val.equals(((NonTerminal)obj).val);
    }

    @Override
    public String toString() {
        return val.toString();
    }
}
