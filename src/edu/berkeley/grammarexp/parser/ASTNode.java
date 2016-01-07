package edu.berkeley.grammarexp.parser;

import java.util.*;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/12/15
 * Time: 10:21 AM
 */
public abstract class ASTNode {
    protected boolean isVisible;
    protected int ID;
    private HashMap<String, Object> attributes;
    boolean toBeVisited = false;

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

    public void visitPostOrder(ASTVisitor visitor) {
        ASTNode root = this;
        Stack<ASTNode> stack = new Stack<ASTNode>();
        Stack<Boolean> stackFlag = new Stack<Boolean>();
        stack.push(root);
        stackFlag.push(false);
        while(!stack.isEmpty()) {
            ASTNode node = stack.pop();
            boolean flag = stackFlag.pop();
            if (node.isLeaf()) {
                visitor.visitLeafNode(node.getID(), node.getValue());
            } else if (!flag){
                stack.push(node);
                stackFlag.push(true);
                visitor.visitInternalNodeBefore(node.getID(), node.getChildren());
                LinkedList<ASTNode> children = node.getChildren();
                Iterator<ASTNode> iter = (Iterator<ASTNode>) children.descendingIterator();
                while(iter.hasNext()) {
                    stack.push(iter.next());
                    stackFlag.push(false);
                }
            } else {
                visitor.visitInternalNodeAfter(node.getID(), node.getChildren());
            }
        }
    }
}
