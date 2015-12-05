package edu.berkeley.grammarexp.parser;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/2/15
 * Time: 2:00 PM
 */
public class Rule {
    private int lhs;
    private RuleRHS rhs;
    protected Grammar g;
    private int ID;

    public Rule(int lhs, RuleRHS rhs, int id, Grammar g) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.g = g;
        this.ID = id;
    }

    @Override
    public int hashCode() {
        return ID;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Rule)) return false;
        Rule other = (Rule) obj;
        return (ID == other.ID);
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

    public int getID() {
        return ID;
    }
}
