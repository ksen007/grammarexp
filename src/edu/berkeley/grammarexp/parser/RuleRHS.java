package edu.berkeley.grammarexp.parser;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 11/30/15
 * Time: 2:42 PM
 */
public class RuleRHS {
    private int[] symbols;
    private int hashCode = -1;
    private int nexti = 0;
    private Grammar g;
    private Precedence precedence = null;

    public RuleRHS(int size, Grammar g) {
        symbols = new int[size];
        this.g = g;
    }

    @Override
    public int hashCode() {
        if (hashCode != -1) {
            return hashCode;
        }
        int ret = 0;
        for(int i=0; i<symbols.length; i++) {
            ret += (symbols[i]<0)?-symbols[i]:symbols[i];
        }
        hashCode = ret;
        return ret;
    }

    public boolean equals(Object o) {
        if (!(o instanceof RuleRHS)) {
            return false;
        }
        RuleRHS other = (RuleRHS)o;
        if (symbols.length != other.symbols.length) return false;
        for (int i=0; i<symbols.length;i++) {
            if (symbols[i] != other.symbols[i])
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String tmp = "";
        for(int i=0; i<symbols.length; i++) {
            int sym = symbols[i];
            Object terminal = null;
            if (g.isTerminalCharacter(sym)) {
                terminal = g.getSymbolFromID(sym);
                tmp+=terminal;
            } else {
                if (tmp.length()>0) {
                    sb.append("\""+tmp+"\"");
                }
                tmp = "";
                if (g.isTerminal(sym)) {
                    terminal = g.getSymbolFromID(sym);
                    sb.append(" " + terminal + " ");
                } else {
                    sb.append(" "+g.getSymbolFromID(sym)+" ");
                }
            }
        }
        if (tmp.length()>0) {
            sb.append("\""+tmp+"\"");
        }
        tmp = "";
        return sb.toString();
    }

    public void addSymbol(int symbol) {
        symbols[nexti] = symbol;
        nexti++;
        if (g.isTerminal(symbol)) {
            precedence = g.getPrecedence(symbol);
        }
    }

    public int length() {
        return symbols.length;
    }

    public int get(int i) {
        return symbols[i];
    }

    public Precedence getPrecedence() {
        return precedence;
    }

    public void setPrecendence(Precedence precedence) {
        this.precedence = precedence;
    }
}
