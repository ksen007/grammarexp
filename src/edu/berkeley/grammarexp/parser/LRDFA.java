package edu.berkeley.grammarexp.parser;

import java.util.*;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/2/15
 * Time: 10:22 PM
 */
class Terminal {
    private String val;

    public Terminal(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}


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

    public int getStateAfterTransition(int state, int symbol) {
        return dfa.get(state).get(symbol);
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

        createKernelItems();
        initKernelLookaheads();
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


    private Item[] kernelItems;
    private int[] kernelItemsState;
    private HashSet<Integer>[] kernelItemsLookaheads;
    private ArrayList<Integer>[] kernelItemsPropagate;

    private void createKernelItems() {
        Rule startRule = g.getStartRule();
        ArrayList<Item> tmp = new ArrayList<Item>();
        int len = 0;
        for(ItemSet items: states) {
            HashMap<Item, Integer> tmp2 = items.getItems();
            for (Item item: tmp2.keySet()) {
                if(!item.isDotAtStart() || item.rule.equals(startRule)) {
                    len++;
                }
            }
        }
        kernelItems = new Item[len];
        kernelItemsState = new int[len];
        kernelItemsLookaheads = new HashSet[len];
        kernelItemsPropagate = new ArrayList[len];

        int i = 0, j = 0;
        for(ItemSet items: states) {
            HashMap<Item, Integer> tmp2 = items.getItems();
            for (Item item: tmp2.keySet()) {
                if(!item.isDotAtStart() || item.rule.equals(startRule)) {
                    kernelItems[i] = item;
                    kernelItemsState[i] = j;
                    tmp2.put(item, i);
                    kernelItemsLookaheads[i] = new HashSet<Integer>();
                    kernelItemsPropagate[i] = new ArrayList<Integer>();
                    if (!item.isDotAtStart()) {
                        kernelItemsLookaheads[i].add(g.getTerminalID(new Terminal("$")));
                    }
                    i++;
                }
            }
            j++;
        }
    }

    private void initKernelLookaheads() {
        int hashTerminal = g.getTerminalID(new Terminal("#"));
        int i = 0;
        for (Item kItem: kernelItems) {
            int state = kernelItemsState[i];
            ItemLASet itemLas = new ItemLASet(g);
            itemLas.closure(new ItemLA(kItem, hashTerminal));
            for (ItemLA itemLA: itemLas.itemLas) {
                if (!itemLA.isDotAtEnd()) {
                    int symbol = itemLA.getSymbolUnderDot();
                    int nextState = getStateAfterTransition(state, symbol);
                    int a = itemLA.la;
                    int kernelIndex = states.get(nextState).getItems().get(itemLA.advance());
                    if (a == hashTerminal) {
                        kernelItemsPropagate[i].add(kernelIndex);
                    } else {
                        kernelItemsLookaheads[kernelIndex].add(a);
                    }
                }
            }
            i++;
        }
    }

}
