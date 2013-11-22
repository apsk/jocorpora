package com.github.apsk.jocorpora;

public class Restriction {
    public enum Type {
        Obligatory,
        Maybe,
        Forbidden
    }
    public static enum SideType {
        Lemma,
        Form
    }

    public final SideType leftType, rightType;
    public final Grammeme left, right;

    public Restriction(SideType leftType, Grammeme left, SideType rightType, Grammeme right) {
        this.leftType = leftType;
        this.rightType = rightType;
        this.left = left;
        this.right = right;
    }
}
