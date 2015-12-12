package edu.berkeley.grammarexp.parser;

import java.util.LinkedList;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 12/12/15
 * Time: 10:32 AM
 */
public class ASTNodeLeaf extends ASTNode {
    private Object value;

    public ASTNodeLeaf(int ID, Object value, boolean isVisible) {
        super(ID, isVisible);
        this.value = value;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public LinkedList<ASTNode> getChildren() {
        throw new RuntimeException("Cannot get children of a leaf node in AST.");
    }

    @Override
    public Object getValue() {
        return value;
    }
}
