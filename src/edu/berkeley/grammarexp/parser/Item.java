package edu.berkeley.grammarexp.parser;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 11/30/15
 * Time: 9:46 PM
 */
public class Item {
    protected Rule rule;
    protected int dotLocation;

    public Item(Rule rule) {
        this.rule = rule;
        dotLocation = 0;
    }

    protected Item(Rule rule, int dotLocation) {
        this.rule = rule;
        this.dotLocation = dotLocation;
    }

    @Override
    public int hashCode() {
        return dotLocation + rule.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Item)) return false;
        Item other = (Item) obj;
        return (dotLocation == other.dotLocation) && (rule.equals(other.rule));
    }

    @Override
    public String toString() {
        return rule+", location = "+dotLocation;
    }

    public int getSymbolUnderDot() {
        return rule.getSymbol(dotLocation);
    }

    public boolean isDotAtEnd() {
        return rule.getRHS().length()==dotLocation;
    }

    public boolean isDotAtStart() {
        return dotLocation == 0;
    }

    public Item advance() {
        return new Item(this.rule, this.dotLocation + 1);
    }
}
