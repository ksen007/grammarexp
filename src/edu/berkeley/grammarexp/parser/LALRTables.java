package edu.berkeley.grammarexp.parser;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/5/15
 * Time: 2:13 AM
 */
public class LALRTables {
    private final Grammar g;
    public ArrayList<TreeMap<Integer, Integer>> GOTO;
    public ArrayList<TreeMap<Integer, ArrayList<Rule>>> ACTION;

    public LALRTables(Grammar g, ArrayList<TreeMap<Integer, Integer>> GOTO, ArrayList<TreeMap<Integer, ArrayList<Rule>>> ACTION) {
        this.g = g;
        this.GOTO = GOTO;
        this.ACTION = ACTION;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        sb.append("[{\n");
        for (TreeMap<Integer, Integer> edges : GOTO) {
            sb.append("\"I" + i + "\" : {");
            boolean first = true;
            for (Integer symbol : edges.keySet()) {
                if (!first) {
                    sb.append(", ");
                } else {
                    first = false;
                }
                sb.append("\"" + g.getSymbolFromID(symbol) + "\" : \"I" + edges.get(symbol) + "\"");
            }
            sb.append("}\n");
            i++;
        }
        sb.append("},\n");
        sb.append("{\n");
        i = 0;
        for (TreeMap<Integer, ArrayList<Rule>> edges : ACTION) {
            sb.append("\"I" + i + "\" : {");
            boolean first = true;
            for (Integer symbol : edges.keySet()) {
                if (!first) {
                    sb.append(", ");
                } else {
                    first = false;
                }
                ArrayList<Rule> rules = edges.get(symbol);
                sb.append("\"" + g.getSymbolFromID(symbol) + "\" : [");
                for (Rule rule: rules) {
                    sb.append(rule);
                }
                sb.append("]");
            }
            sb.append("}]\n");
            i++;
        }
        sb.append("}\n");
        return sb.toString();

    }
}
