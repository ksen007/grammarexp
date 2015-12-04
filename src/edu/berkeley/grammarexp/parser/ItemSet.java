package edu.berkeley.grammarexp.parser;

import java.util.*;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 11/30/15
 * Time: 10:17 PM
 */
public class ItemSet {
    private HashSet<Item> items;
    private Grammar g;
    private int id;

    public ItemSet(Grammar g) {
        items = new HashSet<Item>();
        this.g = g;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void add(Item item) {
        LinkedHashSet<Item> pending = new LinkedHashSet<Item>();
        pending.add(item);
        while(!pending.isEmpty()) {
            Iterator<Item> iter = pending.iterator();
            item = iter.next();
            iter.remove();
            boolean added = items.add(item);

            if (added && !item.isDotAtEnd()) {
                int symbol = item.getSymbolUnderDot();
                if (!g.isTerminal(symbol)) {
                    List<Rule> rl = g.getRules(symbol);
                    for (Rule r: rl) {
                        Item newItem = new Item(r);
                        if (!items.contains(newItem)) {
                            pending.add(newItem);
                        }
                    }
                }
            }
        }
    }

    public Map<Integer,ItemSet> Gotos() {
        TreeMap<Integer,ItemSet> nexts = new TreeMap<Integer, ItemSet>();
        for (Item item: items) {
            if (!item.isDotAtEnd()) {
                int symbol = item.getSymbolUnderDot();
                ItemSet current = nexts.get(symbol);
                if (current == null) {
                    current = new ItemSet(g);
                    nexts.put(symbol, current);
                }
                current.add(item.advance());
            }
        }
        return nexts;
    }

    @Override
    public int hashCode() {
        return items.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemSet)) return false;
        ItemSet other = (ItemSet) obj;
        return items.equals(other.items);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Item item: items) {
            sb.append(item.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
