package edu.berkeley.grammarexp.parser;

import java.util.*;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/4/15
 * Time: 5:14 PM
 */
public class ItemLASet {
    HashSet<ItemLA> itemLas;
    private Grammar g;

    public ItemLASet(Grammar g) {
        itemLas = new HashSet<ItemLA>();
        this.g = g;
    }

    public void closure(ItemLA itemLa) {
        LinkedHashSet<ItemLA> pending = new LinkedHashSet<ItemLA>();
        pending.add(itemLa);
        while(!pending.isEmpty()) {
            Iterator<ItemLA> iter = pending.iterator();
            itemLa = iter.next();
            iter.remove();
            boolean added = itemLas.add(itemLa);

            if (added && !itemLa.isDotAtEnd()) {
                int symbol = itemLa.getSymbolUnderDot();
                if (!g.isTerminal(symbol)) {
                    List<Rule> rl = g.getRules(symbol);
                    Set<Integer> first = itemLa.first();
                    for (Integer b: first) {
                        for (Rule r : rl) {
                            ItemLA newItem = new ItemLA(new Item(r), b);
                            if (!itemLas.contains(newItem)) {
                                pending.add(newItem);
                            }
                        }
                    }
                }
            }
        }
    }

}
