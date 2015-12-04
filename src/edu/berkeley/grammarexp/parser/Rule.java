package edu.berkeley.grammarexp.parser;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/2/15
 * Time: 2:00 PM
 */
public class Rule {
    private int lhs;
    private RuleRHS rhs;
    private Grammar g;

    public Rule(int lhs, RuleRHS rhs, Grammar g) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.g = g;
    }

    @Override
    public int hashCode() {
        return lhs + rhs.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Rule)) return false;
        Rule other = (Rule) obj;
        return (lhs == other.lhs) && (rhs.equals(other.rhs));
    }


    @Override
    public String toString() {
        return g.getSymbolFromID(lhs)+ " ::= " + rhs;
    }

    public RuleRHS getRHS() {
        return rhs;
    }

    public int getLHS() {
        return lhs;
    }

    public int getSymbol(int i) {
        return rhs.get(i);
    }
}
