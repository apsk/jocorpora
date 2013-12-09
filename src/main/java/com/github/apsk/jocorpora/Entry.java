package com.github.apsk.jocorpora;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public final class Entry {
    public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
        Scanner s = new Scanner(System.in);
        System.out.println("Press enter to start parsing...");
        s.nextLine();
        long startTime = System.currentTimeMillis();
        InputStream in = new FileInputStream("/home/apsk/ling/dict.opcorpora.xml");
        Dictionary dict = Dictionary.fromStream(in);
        System.out.println("Done!");
        //for (int i = 0; i < 10; ++i) {
        System.out.println("-------------------------------------------------");
        int lexeme = dict.getLexeme(1);
        long[] forms = dict.getForms(lexeme);
        int lemma = (int)(forms[0] >> 32);
        System.out.print(dict.getWord(lemma) + ": ");
        for (Grammeme grammeme : dict.getGrammemes((int)forms[0])) {
            System.out.print(grammeme.name + " ");
        }
            /*
            System.out.println();
            for (Lexeme.Form form : lexeme.forms) {
                System.out.print("form " + dict.getWord(form.wordRef) + ": ");
                for (Grammeme grammeme : dict.getGrammemes(form.grammemesRef)) {
                    System.out.print(grammeme.name + " ");
                }
                System.out.println();
            }*/
        //}
        System.out.println("Time elapsed: " + (System.currentTimeMillis() - startTime) / 1000);
        for (;;) {
            if (s.nextLine().equals(":q")) {
                System.out.println("terminationg...");
                System.out.println(dict.toString());
                return;
            }
        }
    }
}
