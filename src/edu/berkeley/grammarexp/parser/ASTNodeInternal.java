package edu.berkeley.grammarexp.parser;

import java.util.LinkedList;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/12/15
 * Time: 10:33 AM
 */
public class ASTNodeInternal extends ASTNode {
    private LinkedList<ASTNode> value;

    public ASTNodeInternal(int ID, boolean isVisible) {
        super(ID, isVisible);
        value = new LinkedList<ASTNode>();
    }

    public void addNode(ASTNode node) {
        value.add(node);
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public LinkedList<ASTNode> getChildren() {
        return value;
    }

    @Override
    public Object getValue() {
        throw new RuntimeException("Cannot get leaf of an internal AST node.");
    }
}
