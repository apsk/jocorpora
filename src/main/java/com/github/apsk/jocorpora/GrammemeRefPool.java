package com.github.apsk.jocorpora;

import java.util.ArrayList;
import java.util.List;

public class GrammemeRefPool {
    static final int CHUNK_SIZE = 2097152;

    final ArrayList<byte[]> chunks;
    final ArrayList<Grammeme> grammemes;
    int cursor;

    public GrammemeRefPool(ArrayList<Grammeme> grammemes) {
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

    public List<Grammeme> getGrammemes(int ref) {
        int index = ref >> 8;
        int count = ref & 255;
        List<Grammeme> grammemes = new ArrayList<>(count);
        for (int i = 0; i < count; ++i) {
            int chunkIx = (index + i) / CHUNK_SIZE;
            int innerIx = (index + i) % CHUNK_SIZE;
            grammemes.add(this.grammemes.get(chunks.get(chunkIx)[innerIx]));
        }
        return grammemes;
    }

    public int getGrammemesCount(int ref) {
        return ref & 255;
    }

    public Grammeme getGrammeme(int ref, int index) {
        return this.grammemes.get((ref >> 8) + index);
    }
}
