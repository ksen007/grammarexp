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

    public void push(int ID, Object subtree, Integer state) {
        this.add(ID);
        this.add(subtree);
        this.add(state);
    }

    public LinkedList popn(int n) {
        LinkedList sb = new LinkedList();
        int len = size();
        Object top;
        Integer id;

        for(int i=n-1; i>=0; i--) {
            id = (Integer) get(len-3*i-3);
            top = get(len-3*i-2);
            sb.addLast(id);
            sb.addLast(top);
        }
        removeRange(len-3*n, len);
        return sb;
    }

}
