package org.radargun.service.redis;

import org.radargun.stages.cache.generators.KeyGenerator;

public class Redis3ByteArrayKeyGenerator implements KeyGenerator {

    @Override
    public Object generateKey(long keyIndex) {
        return Long.toHexString(keyIndex).getBytes();
    }

}
