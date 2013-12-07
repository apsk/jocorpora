package com.github.apsk.jocorpora;
import com.github.apsk.hax.Parser;
import com.github.apsk.j8t.Tuple2;

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
    final GrammemeRefPool grammemeRefPool;
    final List<Restriction> restrictions;
    final ArrayList<Lexeme> lexemes;

    private Dictionary(
        String version,
        String revision,
        ArrayList<Grammeme> grammemes,
        Map<String, Grammeme> grammemeMap,
        GrammemeRefPool grammemeRefPool,
        List<Restriction> restrictions,
        ArrayList<Lexeme> lexemes
    ) {
        this.version = version;
        this.revision = revision;
        this.grammemes = grammemes;
        this.grammemeMap = grammemeMap;
        this.grammemeRefPool = grammemeRefPool;
        this.restrictions = restrictions;
        this.lexemes = lexemes;
    }

    public List<Grammeme> getGrammemes(int ref) {
        return grammemeRefPool.getGrammemes(ref);
    }

    public static Dictionary fromStream(InputStream inputStream) throws XMLStreamException {
        byte[] grammemesCount = new byte[1];
        ArrayList<Grammeme> grammemes = new ArrayList<>();
        Map<String, Grammeme> grammemeMap = new HashMap<>();
        ArrayList<Lexeme> lexemes = new ArrayList<>(400000);
        GrammemeRefPool grammemeRefPool = new GrammemeRefPool(grammemes);

        grammemesCount[0] = 0;

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

        ArrayList<Grammeme> formGrammemePool = new ArrayList<>(32);

        Function<Tuple2<String, List<String>>, Lexeme.Form> mkForm = t -> {
            formGrammemePool.clear();
            for (String grammemeText : t.$2)
                formGrammemePool.add(grammemeMap.get(grammemeText));
            return new Lexeme.Form(t.$1, grammemeRefPool.addGrammemes(formGrammemePool));
        };

        Parser<?> lexemeParser =
            within("lemma", attr("id"),
                manyWithin("l", attr("t"), elemAttr("g", "v")),
                manyWithin("f", attr("t"), elemAttr("g", "v")).until(closing("lemma")))
            .effect(r -> {
                int id = Integer.valueOf(r.$1);
                lexemes.add(new Lexeme(id, mkForm.apply(r.$2),
                    r.$3.stream().map(mkForm).toArray(Lexeme.Form[]::new)));
            });

        Parser<?> linkParser =
            elem("link",
                seq(attr("id"), attr("from"), attr("to"), attr("type")))
            .effect(r -> {
                System.out.println("r.$2     = " + r.$2);
                System.out.println("pI(r.$2) = " + Integer.parseInt(r.$2));
                System.out.println(" ...     = " + lexemes.get(Integer.parseInt(r.$2)));
                lexemes.get(Integer.parseInt(r.$2))
                    .links.add(new Link(
                        Link.Type.fromId(Integer.parseInt(r.$4)),
                        lexemes.get(Integer.parseInt(r.$3))));
            });

        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
        skipTo("dictionary").run(reader);
        Tuple2<String,String> verRev = attributesParser.run(reader);
        System.out.println("Dictionary attributes read.");
        evalManyWithin("grammemes", grammemeParser).run(reader);
        System.out.println(grammemeMap.size() + " grammemes read.");
        List<Restriction> restrictions = manyWithin("restrictions", restrictionParser).run(reader);
        System.out.println(restrictions.size() + " restrictions read.");
        evalManyWithin("lemmata", lexemeParser).run(reader);
        System.out.println(lexemes.size() + " lexemes read.");
        skipTo("links").run(reader);
        // evalManyWithin("links", link).run(reader);

        return new Dictionary(
            verRev.$1, verRev.$2,
            grammemes, grammemeMap, grammemeRefPool,
            restrictions, lexemes
        );
    }
}
