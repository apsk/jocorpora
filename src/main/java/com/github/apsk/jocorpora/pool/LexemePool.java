package com.github.apsk.jocorpora.pool;

import java.util.ArrayList;

public final class LexemePool {
    static final int CHUNK_SIZE = 400000;

    ArrayList<int[]> chunks;

    public LexemePool() {
        chunks = new ArrayList<>();
    }

    public void putLexeme(int lexemeId, int formsRef) {
        int chunkIx = lexemeId / CHUNK_SIZE;
        int innerIx = lexemeId % CHUNK_SIZE;
        if (chunkIx >= chunks.size())
            chunks.add(new int[CHUNK_SIZE]);
        chunks.get(chunkIx)[innerIx] = formsRef;
    }

    public int getLexeme(int lexemeId) {
        int chunkIx = lexemeId / CHUNK_SIZE;
        int innerIx = lexemeId % CHUNK_SIZE;
        return chunks.get(chunkIx)[innerIx];
    }
}
