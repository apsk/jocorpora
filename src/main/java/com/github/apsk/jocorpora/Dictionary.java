package com.github.apsk.jocorpora;
import com.github.apsk.hax.Parser;
import com.github.apsk.j8t.Tuple2;
import com.github.apsk.jocorpora.pool.FormsPool;
import com.github.apsk.jocorpora.pool.GrammemesPool;
import com.github.apsk.jocorpora.pool.LexemePool;
import com.github.apsk.jocorpora.pool.WordPool;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.github.apsk.hax.HAX.*;
import static com.github.apsk.jocorpora.StringUtil.capitalize;

public class Dictionary {
    final String version;
    final String revision;
    final ArrayList<Grammeme> grammemes;
    final Map<String, Grammeme> grammemeMap;
    final GrammemesPool grammemesPool;
    final WordPool wordPool;
    final FormsPool formsPool;
    final LexemePool lexemePool;
    final List<Restriction> restrictions;

    private Dictionary(
        String version,
        String revision,
        ArrayList<Grammeme> grammemes,
        Map<String, Grammeme> grammemeMap,
        GrammemesPool grammemesPool,
        WordPool wordPool,
        FormsPool formsPool,
        LexemePool lexemePool,
        List<Restriction> restrictions
    ) {
        this.version = version;
        this.revision = revision;
        this.grammemes = grammemes;
        this.grammemeMap = grammemeMap;
        this.grammemesPool = grammemesPool;
        this.wordPool = wordPool;
        this.formsPool = formsPool;
        this.lexemePool = lexemePool;
        this.restrictions = restrictions;
    }

    public Grammeme[] getGrammemes(int ref) {
        return grammemesPool.getGrammemes(ref);
    }

    public String getWord(int ref) {
        return wordPool.getWord(ref);
    }

    public int getLexeme(int id) {
        return lexemePool.getLexeme(id);
    }

    public long[] getForms(int ref) {
        return formsPool.getForms(ref);
    }

    public static Dictionary fromStream(InputStream inputStream) throws XMLStreamException {
        byte[] grammemesCount = new byte[1];
        grammemesCount[0] = 0;

        ArrayList<Grammeme> grammemes = new ArrayList<>();
        Map<String, Grammeme> grammemeMap = new HashMap<>();
        GrammemesPool grammemesPool = new GrammemesPool(grammemes);
        WordPool wordPool = new WordPool();
        FormsPool formsPool = new FormsPool();
        LexemePool lexemePool = new LexemePool();

        Parser<Tuple2<String,String>> attributesParser =
            open("dictionary", seq(attr("version"), attr("revision")));

        Parser<?> grammemeParser =
            within("grammeme", attr("parent"),
                elemText("name"),
                elemText("alias"),
                elemText("description"))
            .effect(r -> {
                Grammeme grammeme = new Grammeme(grammemesCount[0]++,
                    r.$2, r.$3, r.$4, grammemeMap.get(r.$1));
                grammemes.add(grammeme);
                grammemeMap.put(r.$2, grammeme);
            });

        Parser<Restriction> restrictionParser =
            within("restr", attr("type"),
                elemAttrAndText("left", "type"),
                elemAttrAndText("right", "type"))
            .map(r -> new Restriction(
                Restriction.Type.valueOf(capitalize(r.$1)),
                Restriction.SideType.valueOf(capitalize(r.$2.$1)),
                grammemeMap.get(r.$2.$2),
                Restriction.SideType.valueOf(capitalize(r.$3.$1)),
                grammemeMap.get(r.$3.$2)));

        ArrayList<Grammeme> formGrammemesPool = new ArrayList<>(32);
        int[] lexemeFormsPool = new int[128]; // each form is 2 ints: formRef x grammemesRef

        Parser<?> lexemeParser =
            within("lemma", attr("id"),
                manyWithin("l", attr("t"), elemAttr("g", "v")),
                manyWithin("f", attr("t"), elemAttr("g", "v")).until(closing("lemma")))
            .effect(r -> {
                formGrammemesPool.clear();
                for (String grammemeText : r.$2.$2)
                    formGrammemesPool.add(grammemeMap.get(grammemeText));
                lexemeFormsPool[0] = wordPool.addWord(r.$2.$1);
                lexemeFormsPool[1] = grammemesPool.addGrammemes(formGrammemesPool);
                int ix = 2;
                for (Tuple2<String,List<String>> form : r.$3) {
                    formGrammemesPool.clear();
                    for (String grammemeText : form.$2)
                        formGrammemesPool.add(grammemeMap.get(grammemeText));
                    lexemeFormsPool[ix] = wordPool.addWord(form.$1);
                    lexemeFormsPool[ix+1] = grammemesPool.addGrammemes(formGrammemesPool);
                    ix += 2;
                }
                lexemePool.putLexeme(Integer.valueOf(r.$1),
                    formsPool.addForms(lexemeFormsPool, ix));
            });

        /*
        Parser<?> linkParser =
            elem("link",
                seq(attr("id"), attr("from"), attr("to"), attr("type")))
            .effect(r -> {
                lexemes.get(Integer.parseInt(r.$2))
                    .links.add(new Link(
                        Link.Type.fromId(Integer.parseInt(r.$4)),
                        lexemes.get(Integer.parseInt(r.$3))));
            });
        */

        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
        skipTo("dictionary").run(reader);
        Tuple2<String,String> verRev = attributesParser.run(reader);
        System.out.println("Dictionary attributes read.");
        evalManyWithin("grammemes", grammemeParser).run(reader);
        System.out.println(grammemeMap.size() + " grammemes read.");
        List<Restriction> restrictions = manyWithin("restrictions", restrictionParser).run(reader);
        System.out.println(restrictions.size() + " restrictions read.");
        evalManyWithin("lemmata", lexemeParser).run(reader);
        // System.out.println(lexemePool.size() + " lexemes read.");
        skipTo("links").run(reader);
        // evalManyWithin("links", link).run(reader);

        return new Dictionary(
            verRev.$1, verRev.$2,
            grammemes, grammemeMap, grammemesPool,
            wordPool, formsPool, lexemePool,
            restrictions
        );
    }
}
