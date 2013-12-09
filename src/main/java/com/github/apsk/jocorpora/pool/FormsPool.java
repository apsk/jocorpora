package com.github.apsk.jocorpora.pool;

import java.util.ArrayList;

public final class FormsPool {
    static final int CHUNK_SIZE = 4194304;

    ArrayList<int[]> chunks;
    int cursor;

    public FormsPool() {
        chunks = new ArrayList<>();
        cursor = 0;
    }

    public int addForms(int[] buffer, int size) {
        int index = cursor << 8;
        int count = size / 2;
        for (int i = 0; i < size; i += 2) {
            int chunkIx = cursor / CHUNK_SIZE;
            int innerIx = cursor % CHUNK_SIZE;
            if (chunkIx >= chunks.size())
                chunks.add(new int[CHUNK_SIZE]);
            chunks.get(chunkIx)[innerIx] = buffer[i];
            chunks.get(chunkIx)[innerIx+1] = buffer[i+1];
            cursor += 2;
        }
        return index | count;
    }

    public long[] getForms(int ref) {
        int index = ref >> 8;
        int count = ref & 255;
        int size = count * 2;
        long[] forms = new long[count];
        for (int i = 0; i < size; i += 2) {
            int chunkIx = (index + i) / CHUNK_SIZE;
            int innerIx = (index + i) % CHUNK_SIZE;
            forms[i>>1] = ((long)chunks.get(chunkIx)[innerIx] << 32)
                    | chunks.get(chunkIx)[innerIx+1];
        }
        return forms;
    }
}
