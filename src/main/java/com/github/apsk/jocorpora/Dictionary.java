package com.github.apsk.jocorpora;

import com.github.apsk.hax.parser.HAXEventReader;
import com.github.apsk.hax.parser.Parser;
import com.github.apsk.hax.parser.arity.Parser2;
import com.github.apsk.j8t.Tuple2;

import javax.xml.stream.XMLStreamException;
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
    final Map<String, Grammeme> grammemes;
    final List<Restriction> restrictions;
    final ArrayList<Lexeme> lexemes;

    public Dictionary(
        String version,
        String revision,
        Map<String, Grammeme> grammemes,
        List<Restriction> restrictions,
        ArrayList<Lexeme> lexemes
    ) {
        this.version = version;
        this.revision = revision;
        this.grammemes = grammemes;
        this.restrictions = restrictions;
        this.lexemes = lexemes;
    }

    public static Dictionary fromStream(InputStream inputStream) throws XMLStreamException {
        Map<String, Grammeme> grammemes = new HashMap<>();
        ArrayList<Lexeme> lexemes = new ArrayList<>(400000);

        Parser<Tuple2<String,String>> dictAttrs =
            open("dictionary", seq(attr("version"), attr("revision")));

        Parser<?> grammeme =
            within("grammeme", attr("parent"),
                elemText("name"),
                elemText("alias"),
                elemText("description"))
            .effect(r -> grammemes.put(r.$2, new Grammeme(
                r.$2, r.$3, r.$4, grammemes.get(r.$1))));

        Parser<Restriction> restriction =
            within("restr", attr("type"),
                elemAttrAndText("left", "type"),
                elemAttrAndText("right", "type"))
            .map(r -> new Restriction(
                Restriction.Type.valueOf(capitalize(r.$1)),
                Restriction.SideType.valueOf(capitalize(r.$2.$1)),
                grammemes.get(r.$2.$2),
                Restriction.SideType.valueOf(capitalize(r.$3.$1)),
                grammemes.get(r.$3.$2)));

        Function<Tuple2<String, List<String>>, Lexeme.Form> mkForm = t ->
            new Lexeme.Form(t.$1, (Grammeme[]) t.$2.stream().map(grammemes::get).toArray());

        Parser<?> lexeme =
            within("lemma", attr("id"),
                manyWithin("l", attr("t"), elemAttr("g", "v")),
                manyWithin("f", attr("t"), elemAttr("g", "v")).until(closing("lemma")))
            .effect(r -> {
                int id = Integer.valueOf(r.$1);
                lexemes.add(id, new Lexeme(id, mkForm.apply(r.$2),
                    (Lexeme.Form[]) r.$3.stream().map(mkForm).toArray()));
            });

        Parser<?> link =
            elem("link",
                seq(attr("id"), attr("from"), attr("to"), attr("type")))
            .effect(r -> {
                lexemes.get(Integer.parseInt(r.$2))
                    .links.add(new Link(
                        Link.Type.fromId(Integer.parseInt(r.$4)),
                        lexemes.get(Integer.parseInt(r.$3))));
            });

        HAXEventReader reader = new HAXEventReader(inputStream);
        reader.skipTo("dictionary");
        Tuple2<String,String> verRev = dictAttrs.run(reader);
        evalManyWithin("grammemes", grammeme).run(reader);
        List<Restriction> restrictions = manyWithin("restrictions", restriction).run(reader);
        evalManyWithin("lemmata", lexeme).run(reader);
        reader.skipTo("links");
        evalManyWithin("links", link).run(reader);

        return new Dictionary(verRev.$1, verRev.$2, grammemes, restrictions, lexemes);
    }
}
