package com.github.apsk.jocorpora;

import java.util.ArrayList;
import java.util.List;

public class Lexeme {
    public static class Form {
        public final String text;
        public final int grammemeRef;

        public Form(String text, int grammemeRef) {
            this.text = text;
            this.grammemeRef = grammemeRef;
        }
    }

    public final int id;
    public final Form lemma;
    public final Form[] forms;
    public List<Link> links;

    public Lexeme(int id, Form lemma, Form[] forms) {
        this.id = id;
        this.lemma = lemma;
        this.forms = forms;
        this.links = new ArrayList<>();
    }
}
