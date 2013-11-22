package com.github.apsk.jocorpora;

public class Link {
    public enum Type {
        ADJF_ADJS,
        ADJF_COMP,
        INFN_VERB,
        INFN_PRTF,
        INFN_GRND,
        PRTF_PRTS,
        NAME_PATR,
        PATR_MASC_PATR_FEMN,
        SURN_MASC_SURN_FEMN,
        SURN_MASC_SURN_PLUR,
        PERF_IMPF,
        ADJF_SUPR_ejsh,
        PATR_MASC_FORM_PATR_MASC_INFR,
        PATR_FEMN_FORM_PATR_FEMN_INFR,
        ADJF_eish_SUPR_nai_eish,
        ADJF_SUPR_ajsh,
        ADJF_aish_SUPR_nai_aish,
        ADJF_SUPR_suppl,
        ADJF_SUPR_nai,
        ADJF_SUPR_slng,
        FULL_CONTRACTED
    }

    public final Type type;
    public final Lexeme target;

    public Link(Type type, Lexeme target) {
        this.type = type;
        this.target = target;
    }
}
