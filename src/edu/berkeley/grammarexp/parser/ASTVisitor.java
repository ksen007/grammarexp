package edu.berkeley.grammarexp.parser;

import java.util.LinkedList;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 1/6/16
 * Time: 11:19 PM
 */
public interface ASTVisitor {
    public void visitInternalNodeBefore(int id, LinkedList<ASTNode> list);
    public void visitInternalNodeAfter(int id, LinkedList<ASTNode> list);
    public void visitLeafNode(int id, Object value);
}
