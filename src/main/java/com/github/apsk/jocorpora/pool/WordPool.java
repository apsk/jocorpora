package com.github.apsk.jocorpora.pool;

import com.github.apsk.jocorpora.Letter;

import java.util.ArrayList;

public final class WordPool {
    static final int CHUNK_SIZE = 8388608;

    ArrayList<byte[]> chunks;
    int cursor;

    public WordPool() {
        chunks = new ArrayList<>();
        cursor = 0;
    }

    public int addWord(String word) {
        int index = cursor << 8;
        int count = word.length();
        for (int i = 0; i < count; ++i) {
            int chunkIx = cursor / CHUNK_SIZE;
            int innerIx = cursor % CHUNK_SIZE;
            if (chunkIx >= chunks.size())
                chunks.add(new byte[CHUNK_SIZE]);
            chunks.get(chunkIx)[innerIx] = Letter.encode(word.charAt(i));
            cursor++;
        }
        return index | count;
    }

    public String getWord(int ref) {
        int index = ref >> 8;
        int count = ref & 255;
        StringBuilder stringBuilder = new StringBuilder(count);
        for (int i = 0; i < count; ++i) {
            int chunkIx = (index + i) / CHUNK_SIZE;
            int innerIx = (index + i) % CHUNK_SIZE;
            stringBuilder.append(Letter.decode(chunks.get(chunkIx)[innerIx]));
        }
        return stringBuilder.toString();
    }
}
