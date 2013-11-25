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
        FULL_CONTRACTED;

        public static Type fromId(int id) {
            switch (id) {
                case 1: return ADJF_ADJS;
                case 2: return ADJF_COMP;
                case 3: return INFN_VERB;
                case 4: return INFN_PRTF;
                case 5: return INFN_GRND;
                case 6: return PRTF_PRTS;
                case 7: return NAME_PATR;
                case 8: return PATR_MASC_PATR_FEMN;
                case 9: return SURN_MASC_SURN_FEMN;
                case 10: return SURN_MASC_SURN_PLUR;
                case 11: return PERF_IMPF;
                case 12: return ADJF_SUPR_ejsh;
                case 13: return PATR_MASC_FORM_PATR_MASC_INFR;
                case 14: return PATR_FEMN_FORM_PATR_FEMN_INFR;
                case 15: return ADJF_eish_SUPR_nai_eish;
                case 16: return ADJF_SUPR_ajsh;
                case 17: return ADJF_aish_SUPR_nai_aish;
                case 18: return ADJF_SUPR_suppl;
                case 19: return ADJF_SUPR_nai;
                case 20: return ADJF_SUPR_slng;
                case 21: return FULL_CONTRACTED;
            }
            throw new IllegalArgumentException(
                "Id " + id + " doesn't correspond to any link type."
            );
        }
    }

    public final Type type;
    public final Lexeme target;

    public Link(Type type, Lexeme target) {
        this.type = type;
        this.target = target;
    }
}
