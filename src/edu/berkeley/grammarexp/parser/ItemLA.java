package edu.berkeley.grammarexp.parser;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/4/15
 * Time: 5:01 PM
 */
public class ItemLA extends Item {
    int la;

    public ItemLA(Item item, int la) {
        super(item.rule, item.dotLocation);
        this.la = la;
    }


    public Set<Integer> first(FirstFollow firstFollow) {
        Grammar g = rule.g;
        Set<Integer> ret = new HashSet<Integer>();
        RuleRHS y = rule.getRHS();

        int i;
        int k = y.length();
        boolean allNullable = true;
        for (i = dotLocation + 1; i < k; i++) {
            int Yi = y.get(i);
            if (g.isTerminal(Yi)) {
                ret.add(Yi);
                allNullable = false;
                break;
            } else {
                ret.addAll(firstFollow.getFirst(Yi));
                if (!firstFollow.getNullable(Yi)) {
                    allNullable = false;
                    break;
                }
            }
        }
        if (allNullable) {
            ret.add(la);
        }
        return ret;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + la;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemLA)) return false;
        ItemLA other = (ItemLA) obj;
        return la == other.la && super.equals(other);
    }

    @Override
    public String toString() {
        return super.toString() + ", " + rule.g.getSymbolFromID(la);
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof ItemLA)) throw new RuntimeException("Type of "+o+" must be of type ItemLA.");
        ItemLA other = (ItemLA) o;
        int diff = super.compareTo(o);
        if (diff == 0) {
            return la - other.la;
        } else {
            return diff;
        }
    }
}
