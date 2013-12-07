package com.github.apsk.jocorpora;

public class Grammeme {
    public final byte id;
    public final String name;
    public final String alias;
    public final String description;
    public final Grammeme parent;

    public Grammeme(byte id, String name, String alias, String description, Grammeme parent) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.description = description;
        this.parent = parent;
    }
}
