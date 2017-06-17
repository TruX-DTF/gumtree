package com.github.gumtreediff.actions.model;

import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeContext;

public abstract class Action {

    protected ITree node;

    public Action(ITree node) {
        this.node = node;
    }

    public ITree getNode() {
        return node;
    }

    public void setNode(ITree node) {
        this.node = node;
    }

    protected abstract String getName();

    public abstract String toString();

    public String toString_jihun(TreeContext ctx){
//    	return getName() + " " + node.toPrettyString(ctx);
    	return getName() + " " + node.toTreeString_jihun(ctx);
//    	return getName() + " " + node.toTreeString() + " to " + parent.toShortString() + " at " + pos;
    }
}
