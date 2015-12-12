package edu.berkeley.grammarexp.parser;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/12/15
 * Time: 10:21 AM
 */
public abstract class ASTNode {
    protected boolean isVisible;
    protected int ID;
    private HashMap<String, Object> attributes;

    public ASTNode(int ID, boolean isVisible) {
        this.isVisible = isVisible;
        this.ID = ID;
    }

    public abstract boolean isLeaf();
    public abstract LinkedList<ASTNode> getChildren();
    public abstract Object getValue();

    public Object getAttribute(String key) {
        if (attributes == null) return null;
        else return attributes.get(key);
    }

    public void setAttribute(String key, Object value) {
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }
        attributes.put(key, value);
    }

    public int getID() {
        return ID;
    }

    public boolean isVisible() {
        return isVisible;
    }
}
