package edu.berkeley.grammarexp.parser;

import java.util.*;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 11/30/15
 * Time: 10:17 PM
 */
public class ItemSet implements Comparable {
    private Map<Item, Integer> items;
    private Grammar g;

    public ItemSet(Grammar g) {
        items = new TreeMap<Item, Integer>();
        this.g = g;
    }

    public void add(Item item) {
        LinkedHashSet<Item> pending = new LinkedHashSet<Item>();
        pending.add(item);
        while(!pending.isEmpty()) {
            Iterator<Item> iter = pending.iterator();
            item = iter.next();
            iter.remove();
            Integer prev = items.put(item, -1);

            if (prev == null && !item.isDotAtEnd()) {
                int symbol = item.getSymbolUnderDot();
                if (!g.isTerminal(symbol)) {
                    List<Rule> rl = g.getRules(symbol);
                    for (Rule r: rl) {
                        Item newItem = new Item(r);
                        if (!items.containsKey(newItem)) {
                            pending.add(newItem);
                        }
                    }
                }
            }
        }
    }

    public Map<Integer,ItemSet> Gotos() {
        TreeMap<Integer,ItemSet> nexts = new TreeMap<Integer, ItemSet>();
        for (Item item: items.keySet()) {
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
        for(Item item: items.keySet()) {
            sb.append(item.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    public Map<Item, Integer> getItems() {
        return items;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof ItemSet)) throw new RuntimeException("Type of "+o+" must be ItemSet.");
        ItemSet other = (ItemSet)o;
        int diff = items.size()-other.items.size();
        if (diff != 0) return diff;

        Iterator<Item> iter1 = items.keySet().iterator();
        Iterator<Item> iter2 = other.items.keySet().iterator();
        while(iter1.hasNext()) {
            Item item1 = iter1.next();
            Item item2 = iter2.next();
            diff = item1.compareTo(item2);
            if (diff != 0) return diff;
        }
        return 0;
    }
}
