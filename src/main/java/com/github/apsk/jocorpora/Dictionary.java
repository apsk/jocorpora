package com.github.apsk.jocorpora;

import com.github.apsk.hax.parser.Parser;
import com.github.apsk.j8t.Tuple2;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.github.apsk.hax.HAX.*;
import static com.github.apsk.jocorpora.StringUtil.capitalize;

public class Dictionary {
    public final Map<String, Grammeme> grammemes = new HashMap<>();

    public static Dictionary fromStream(InputStream in) throws XMLStreamException {
        Map<String, Grammeme> grammemes = new HashMap<>();
        Parser<?> grammeme =
            within("grammeme", attr("parent"),
                elemText("name"),
                elemText("alias"),
                elemText("description"))
            .map(r -> grammemes.put(r.$2, new Grammeme(
                r.$2, r.$3, r.$4, grammemes.get(r.$1)
            )));
        Parser<Restriction> restriction =
            within("restr", attr("type"),
                elemAttrAndText("left", "type"),
                elemAttrAndText("right", "type"))
            .map(r -> new Restriction(
                Restriction.Type.valueOf(capitalize(r.$1)),
                Restriction.SideType.valueOf(capitalize(r.$2.$1)),
                grammemes.get(r.$2.$2),
                Restriction.SideType.valueOf(capitalize(r.$3.$1)),
                grammemes.get(r.$3.$2)
            ));
        Function<Tuple2<String, List<String>>, Lexeme.Form> mkForm = t -> new Lexeme.Form(t.$1,
            (Grammeme[]) t.$2.stream().map(grammemes::get).toArray()
        );
        Parser<Lexeme> lexeme =
            within("lemma", attr("id"),
                manyWithin("l", attr("t"), elemAttr("g", "v")),
                manyWithin("f", attr("t"), elemAttr("g", "v")).until(closing("lemma")))
            .map(r -> new Lexeme(
                Integer.valueOf(r.$1),
                mkForm.apply(r.$2),
                (Lexeme.Form[]) r.$3.stream().map(mkForm).toArray()
            ));
        /* Parser<Link> link = open("link").nextR(attrs()).nextL(close("link")).map(r ->
            null
        );*/
        return null;
    }
}
