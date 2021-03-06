package edu.berkeley.grammarexp.parser;

import java.util.*;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/2/15
 * Time: 10:22 PM
 */


public class LALR {
    private LALRTables table;
    private Map<ItemSet, Integer> itemSetsToStateID;
    private ArrayList<TreeMap<Integer, Integer>> GOTO;
    private ArrayList<TreeMap<Integer, ArrayList<Rule>>> ACTION;
    private ArrayList<ItemSet> states;
    private Grammar g;
    private boolean printItemSets = false;

    private LALR(ItemSet I0, Grammar g) {
        this.g = g;
        itemSetsToStateID = new TreeMap<ItemSet, Integer>();
        GOTO = new ArrayList<TreeMap<Integer, Integer>>();
        states = new ArrayList<ItemSet>();
        ACTION = new ArrayList<TreeMap<Integer, ArrayList<Rule>>>();

        Integer i0 = getStateID(I0);
    }

    public void setPrintItemSets(boolean printItemSets) {
        this.printItemSets = printItemSets;
    }

    public int getStateAfterTransition(int state, int symbol) {
        return GOTO.get(state).get(symbol);
    }

    public Integer getStateID(ItemSet items) {
        Integer ret = itemSetsToStateID.get(items);
        if (ret == null) {
            ret = itemSetsToStateID.size();
            itemSetsToStateID.put(items, ret);
            GOTO.add(new TreeMap<Integer, Integer>());
            ACTION.add(new TreeMap<Integer, ArrayList<Rule>>());
            states.add(items);
        }
        return ret;
    }

    public void createGotoTable() {
        int i = 0;
        while (i < states.size()) {
            ItemSet Ij = states.get(i);
            Map<Integer, ItemSet> transitions = Ij.Gotos();
            TreeMap<Integer, Integer> edges = GOTO.get(i);
            for (Integer symbol : transitions.keySet()) {
                ItemSet Ik = transitions.get(symbol);
                edges.put(symbol, getStateID(Ik));
            }
            i++;
        }

    }

    public LALRTables build() {
        createGotoTable();
        createKernelItems();
        initKernelLookaheads();
        computeLookaheads();
        createActionTable();
        return new LALRTables(g, GOTO, ACTION);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        if (printItemSets) {
            for (ItemSet items : states) {
                sb.append("I" + i + ":");
                sb.append(items);
                sb.append("\n");
                i++;
            }
        }
        sb.append(table.toString());
        return sb.toString();
    }


    private Item[] kernelItems;
    private int[] kernelItemsState;
    private HashSet<Integer>[] kernelItemsLookaheads;
    private ArrayList<Integer>[] kernelItemsPropagate;
    private LinkedHashSet<Integer> pending;

    private void createKernelItems() {
        Rule startRule = g.getStartRule();
        ArrayList<Item> tmp = new ArrayList<Item>();
        int len = 0;
        for (ItemSet items : states) {
            Map<Item, Integer> tmp2 = items.getItems();
            for (Item item : tmp2.keySet()) {
                if (!item.isDotAtStart() || item.rule.equals(startRule)) {
                    len++;
                }
            }
        }
        kernelItems = new Item[len];
        kernelItemsState = new int[len];
        kernelItemsLookaheads = new HashSet[len];
        kernelItemsPropagate = new ArrayList[len];
        pending = new LinkedHashSet<Integer>();

        int i = 0, j = 0;
        for (ItemSet items : states) {
            Map<Item, Integer> tmp2 = items.getItems();
            for (Item item : tmp2.keySet()) {
                if (!item.isDotAtStart() || item.rule.equals(startRule)) {
                    kernelItems[i] = item;
                    kernelItemsState[i] = j;
                    tmp2.put(item, i);
                    kernelItemsLookaheads[i] = new HashSet<Integer>();
                    kernelItemsPropagate[i] = new ArrayList<Integer>();
                    if (item.isDotAtStart()) {
                        kernelItemsLookaheads[i].add(g.endToken);
                        pending.add(i);
                    }
                    i++;
                }
            }
            j++;
        }
    }

    private void initKernelLookaheads() {
        int hashTerminal = g.getTerminalID(new Token("#"));
        int i = 0;
        for (Item kItem : kernelItems) {
            int state = kernelItemsState[i];
            ItemLASet itemLas = new ItemLASet(g);
            FirstFollow firstFollow = new FirstFollow(g);
            firstFollow.computeFirstFollowSets(false);
            itemLas.closure(new ItemLA(kItem, hashTerminal), firstFollow);
            for (ItemLA itemLA : itemLas.itemLas) {
                if (!itemLA.isDotAtEnd()) {
                    int symbol = itemLA.getSymbolUnderDot();
                    int nextState = getStateAfterTransition(state, symbol);
                    int a = itemLA.la;
                    int kernelIndex = states.get(nextState).getItems().get(itemLA.advance());
                    if (a == hashTerminal) {
                        kernelItemsPropagate[i].add(kernelIndex);
                    } else {
                        kernelItemsLookaheads[kernelIndex].add(a);
                        pending.add(kernelIndex);
                    }
                }
            }
            i++;
        }

    }

    private void computeLookaheads() {
        while (!pending.isEmpty()) {
            Iterator<Integer> iter = pending.iterator();
            int i = iter.next();
            iter.remove();
            HashSet<Integer> lafrom = kernelItemsLookaheads[i];
            for (Integer j : kernelItemsPropagate[i]) {
                HashSet<Integer> la = kernelItemsLookaheads[j];
                int oldSize = la.size();
                la.addAll(lafrom);
                if (la.size() > oldSize) {
                    pending.add(j);
                }
            }
        }
    }

    private void reportShiftReduceConflict(int state, int symbol, ArrayList<Rule> rules) {
        ItemSet is = states.get(state);
        System.err.println("At state " + state);
        System.err.println("Shift-reduce conflict detected: Symbol " + g.getSymbolFromID(symbol));

        System.err.println("State");
        Map<Item, Integer> sItems = is.getItems();
        for (Item sItem : sItems.keySet()) {
            if (!sItem.isDotAtEnd() && sItem.getSymbolUnderDot() == symbol) {
                System.err.println("  Rule " + sItem);
            }
        }
        System.err.println("Rule");
        for (Rule tr : rules) {
            System.err.println("  Rule " + tr);
        }

    }


    private void reportReduceReduceConflict(int state, int symbol, ArrayList<Rule> rules) {
        if (rules.size() > 1) {
            System.err.println("At state " + state);
            System.err.println("Reduce-reduce conflict detected: Symbol " + g.getSymbolFromID(symbol));
            for (Rule tr : rules) {
                System.err.println("  Rule " + tr);
            }
        }
    }

    private void createActionTable() {
        int i = 0;
        for (Item item : kernelItems) {
            if (item.isDotAtEnd()) {
                HashSet<Integer> lookaheads = kernelItemsLookaheads[i];
                int state = kernelItemsState[i];
                for (Integer symbol : lookaheads) {
                    TreeMap<Integer, ArrayList<Rule>> actions = ACTION.get(state);
                    ArrayList<Rule> rules = actions.get(symbol);
                    if (rules == null) {
                        rules = new ArrayList<Rule>();
                        actions.put(symbol, rules);
                    }
                    rules.add(item.rule);
                    Integer next = GOTO.get(state).get(symbol);
                    if (next != null) {
                        Rule reduce = rules.get(0);
                        Precedence rulePrecedence = reduce.getPrecedence();
                        Precedence symPrecedence = g.getPrecedence(symbol);
                        if (rulePrecedence != null && symPrecedence != null) {
                            if (rulePrecedence.precedence > symPrecedence.precedence) {
                                System.out.println("Resolving to reduce");
                                GOTO.get(state).remove(symbol);
                            } else if (rulePrecedence.precedence == symPrecedence.precedence && symPrecedence.isAssociative && !symPrecedence.rightAssociative) {
                                System.out.println("Resolving to reduce");
                                GOTO.get(state).remove(symbol);
                            } else if (rulePrecedence.precedence == symPrecedence.precedence && !symPrecedence.isAssociative) {
                                reportShiftReduceConflict(state, symbol, rules);
                                System.err.println("Cannot resolve shift-reduce conflict due to non-associativity of " + g.getSymbolFromID(symbol));
                                throw new RuntimeException("Cannot resolve shift-reduce conflict due to non-associativity of " + g.getSymbolFromID(symbol));
                            } else {
                                System.out.println("Resolving to shift");
                            }
                        } else if (rulePrecedence != null && rulePrecedence.precedence > 0) {
                            System.out.println("Resolving to reduce");
                            GOTO.get(state).remove(symbol);
                        } else if (symPrecedence != null) {
                            if (symPrecedence.precedence == 0 && symPrecedence.isAssociative && !symPrecedence.rightAssociative) {
                                System.out.println("Resolving to reduce");
                                GOTO.get(state).remove(symbol);
                            } else if (symPrecedence.precedence == 0 && !symPrecedence.isAssociative) {
                                reportShiftReduceConflict(state, symbol, rules);
                                System.err.println("Cannot resolve shift-reduce conflict due to non-associativity of " + g.getSymbolFromID(symbol));
                                throw new RuntimeException("Cannot resolve shift-reduce conflict due to non-associativity of " + g.getSymbolFromID(symbol));
                            } else if (symPrecedence.precedence > 0) {
                                System.out.println("Resolving to shift");
                            }
                        } else {
                            reportShiftReduceConflict(state, symbol, rules);
                        }
                    }
                    reportReduceReduceConflict(state, symbol, rules);
                }
            }
            i++;
        }
    }

    private void printLookAheads() {
        int j = 0;
        for (Item item : kernelItems) {
            System.out.print(j + ": I" + kernelItemsState[j] + ": ");
            System.out.print(item);
            System.out.print(" lookaheads = {");
            for (Integer la : kernelItemsLookaheads[j]) {
                System.out.print(g.getSymbolFromID(la) + ", ");
            }
            System.out.print("} propagate = {");
            for (Integer ki : kernelItemsPropagate[j]) {
                System.out.print(ki + ", ");
            }
            System.out.println("}");
            j++;
        }

    }

    public static LALR generateLALRTables(Grammar g) {
        ItemSet I0 = new ItemSet(g);
        I0.add(new Item(g.getStartRule()));
        LALR ret = new LALR(I0, g);
        ret.table = ret.build();
        return ret;
    }

    public LALRTables getLALRTables() {
        return table;
    }
}
