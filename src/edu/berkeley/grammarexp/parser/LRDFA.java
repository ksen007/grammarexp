package edu.berkeley.grammarexp.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/2/15
 * Time: 10:22 PM
 */
public class LRDFA {
    private HashMap<ItemSet, Integer> itemSetsToStateID;
    private ArrayList<TreeMap<Integer,Integer>> dfa;
    private ArrayList<ItemSet> states;
    private Grammar g;

    public LRDFA(ItemSet I0, Grammar g) {
        this.g = g;
        itemSetsToStateID = new HashMap<ItemSet, Integer>();
        dfa = new ArrayList<TreeMap<Integer, Integer>>();
        states = new ArrayList<ItemSet>();
        Integer i0 = getStateID(I0);
    }

    public Integer getStateID(ItemSet items) {
        Integer ret = itemSetsToStateID.get(items);
        if (ret == null) {
            ret = itemSetsToStateID.size();
            itemSetsToStateID.put(items, ret);
            dfa.add(new TreeMap<Integer, Integer>());
            states.add(items);
        }
        return ret;
    }

    public void build() {
        int i = 0;
        while(i<states.size()) {
            ItemSet Ij = states.get(i);
            Map<Integer, ItemSet> transitions = Ij.Gotos();
            TreeMap<Integer,Integer> edges = dfa.get(i);
            for(Integer symbol: transitions.keySet()) {
                ItemSet Ik = transitions.get(symbol);
                edges.put(symbol, getStateID(Ik));
            }
            i++;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for(ItemSet items: states) {
            sb.append("I"+i+":");
            sb.append(items);
            sb.append("\n");
            i++;
        }
        i = 0;
        for (TreeMap<Integer, Integer> edges:dfa) {
            sb.append("I"+i+": {");
            for(Integer symbol: edges.keySet()) {
                if (g.isTerminal(symbol)) {
                    sb.append(g.getSymbolFromID(symbol) + ": I" + edges.get(symbol) + ",");
                } else {
                    sb.append(g.getSymbolFromID(symbol) + ": I" + edges.get(symbol) + ",");
                }
            }
            sb.append("}\n");
            i++;
        }
        return sb.toString();
    }
}
