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

    public final Type type;
    public final SideType leftType, rightType;
    public final Grammeme left, right;

    public Restriction(Type type, SideType leftType, Grammeme left, SideType rightType, Grammeme right) {
        this.type = type;
        this.leftType = leftType;
        this.rightType = rightType;
        this.left = left;
        this.right = right;
    }
}
