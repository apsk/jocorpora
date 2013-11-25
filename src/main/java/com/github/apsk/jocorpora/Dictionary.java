package com.github.apsk.jocorpora;

import com.github.apsk.hax.Parser;
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
        Parser<?> grammeme = elemAttr("grammeme", "parent")
            .and(elemText("name"))
            .and(elemText("alias"))
            .and(elemText("description"))
            .nextL(close("grammeme"))
            .map(r -> grammemes.put(r.val2, new Grammeme(
                r.val2, r.val3, r.val4, grammemes.get(r.val1)
            )));
        Parser<Restriction> restriction = elemAttr("restr", "type")
            .and(elemAttr("left", "type").and(text()))
            .and(elemAttr("right", "type").and(text()))
            .nextL(close("restr"))
            .map(r -> new Restriction(
                Restriction.Type.valueOf(capitalize(r.val1)),
                Restriction.SideType.valueOf(capitalize(r.val2.val1)),
                grammemes.get(r.val2.val2),
                Restriction.SideType.valueOf(capitalize(r.val3.val1)),
                grammemes.get(r.val3.val2)
            ));
        Function<Tuple2<String, List<String>>, Lexeme.Form> mkForm = t -> new Lexeme.Form(t.val1,
            (Grammeme[]) t.val2.stream().map(grammemes::get).toArray()
        );
        Parser<Lexeme> lexeme = elemAttr("lemma", "id")
            .and(elemAttr("l", "t")
                .and(elemAttr("g", "v").nextL(close("g")).until(closing("l"))))
            .and(elemAttr("f", "t")
                .and(elemAttr("g", "v").nextL(close("g")).until(closing("f")))
                .until(closing("lemma")))
            .map(r -> new Lexeme(
                Integer.valueOf(r.val1),
                mkForm.apply(r.val2),
                (Lexeme.Form[]) r.val3.stream().map(mkForm).toArray()
            ));
        Parser<Link> link = open("link").nextR(attrs()).nextL(close("link")).map(r ->
            null
        );
        return null;
    }
}
