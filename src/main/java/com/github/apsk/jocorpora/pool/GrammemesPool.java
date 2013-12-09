package com.github.apsk.jocorpora.pool;

import com.github.apsk.jocorpora.Grammeme;

import java.util.ArrayList;
import java.util.List;

public final class GrammemesPool {
    static final int CHUNK_SIZE = 2097152;

    final ArrayList<byte[]> chunks;
    final ArrayList<Grammeme> grammemes;
    int cursor;

    public GrammemesPool(ArrayList<Grammeme> grammemes) {
        this.grammemes = grammemes;
        chunks = new ArrayList<>();
        chunks.add(new byte[CHUNK_SIZE]);
        cursor = 0;
    }

    public int addGrammemes(List<Grammeme> grammemes) {
        int refPoolId = (cursor << 8) | grammemes.size();
        for (Grammeme grammeme : grammemes) {
            int chunkIx = cursor / CHUNK_SIZE;
            int innerIx = cursor % CHUNK_SIZE;
            if (chunkIx >= chunks.size())
                chunks.add(new byte[CHUNK_SIZE]);
            chunks.get(chunkIx)[innerIx] = grammeme.id;
            cursor++;
        }
        return refPoolId;
    }

    public Grammeme[] getGrammemes(int ref) {
        int index = ref >> 8;
        int count = ref & 255;
        Grammeme[] grammemes = new Grammeme[count];
        for (int i = 0; i < count; ++i) {
            int chunkIx = (index + i) / CHUNK_SIZE;
            int innerIx = (index + i) % CHUNK_SIZE;
            grammemes[i] = this.grammemes.get(chunks.get(chunkIx)[innerIx]);
        }
        return grammemes;
    }
}
