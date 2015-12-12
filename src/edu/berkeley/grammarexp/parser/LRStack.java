package edu.berkeley.grammarexp.parser;

import java.util.ArrayList;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/5/15
 * Time: 10:06 PM
 */
public class LRStack extends ArrayList {

    public Integer topState() {
        return (Integer)this.get(this.size() - 1);
    }

    public void push(ASTNode node, Integer state) {
        this.add(node);
        this.add(state);
    }

    public ASTNodeInternal popn(int n, int ID, boolean isVisible) {
        ASTNodeInternal ret = new ASTNodeInternal(ID, isVisible);
        int len = size();
        ASTNode top;

        for(int i=n-1; i>=0; i--) {
            top = (ASTNode) get(len-2*i-2);
            ret.addNode(top);
        }
        removeRange(len-2*n, len);
        return ret;
    }

}
