package edu.berkeley.grammarexp.parser;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/5/15
 * Time: 10:06 PM
 */
public class LRStack extends ArrayList {

    public Integer topState() {
        return (Integer)this.get(this.size() - 1);
    }

    public void push(Object subtree, Integer state) {
        this.add(subtree);
        this.add(state);
    }

    public LinkedList popn(int n) {
        LinkedList sb = new LinkedList();
        int len = size();
        Object top;

        for(int i=n-1; i>=0; i--) {
            top = get(len-2*i-2);
            sb.addLast(top);
        }
        removeRange(len-2*n, len);
        return sb;
    }

}
