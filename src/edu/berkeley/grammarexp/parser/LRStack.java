package edu.berkeley.grammarexp.parser;

import java.util.ArrayList;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/5/15
 * Time: 10:06 PM
 */
public class LRStack extends ArrayList {
    private Grammar g;
    final public static String LB = "{{";
    final public static String RB = "}}";

    public LRStack(Grammar g) {
        this.g = g;
    }

    public Integer topState() {
        return (Integer)this.get(this.size() - 1);
    }

    public String topSymbol() {
        return (String)this.get(this.size() - 2);
    }

    public void push(Object symbol, Integer state) {
        this.add(symbol);
        this.add(state);
    }

    private Object pop() {
        this.remove(this.size()-1);
        return this.remove(this.size()-1);
    }

    public String popn(int n) {
        StringBuffer sb = new StringBuffer();
        String prev = "";
        String curr = "";
        int len = size();

        for(int i=n-1; i>=0; i--) {
            Object top = get(len-2*i-2);
            if (top instanceof Integer) {
                curr = g.getSymbolFromID((Integer) top).toString();
                prev += curr;
                prev = prev.replace(LB, LB + LB);
                prev = prev.replace(RB, LB + RB);
                sb.append(prev);
                prev = curr;
            } else {
                prev = "";
                sb.append(top);
            }
        }
        removeRange(len-2*n, len);
        return sb.toString();
    }

}
