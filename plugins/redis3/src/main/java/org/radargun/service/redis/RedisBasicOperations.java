package org.radargun.service.redis;


import org.radargun.traits.BasicOperations;
import redis.clients.jedis.JedisCluster;

public class RedisBasicOperations implements BasicOperations {

    private Redis3Service redis3Service;

    public RedisBasicOperations(Redis3Service redis3Service) {
        this.redis3Service = redis3Service;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) {
        return new RedisCacheAdapter(redis3Service.getJedisCluster());
    }

    private class RedisCacheAdapter<K, V> implements BasicOperations.Cache<byte[], byte[]> {
        private JedisCluster jedisCluster;

        public RedisCacheAdapter(JedisCluster jedisCluster) {
            this.jedisCluster = jedisCluster;
        }

        @Override
        public byte[] get(byte[] key) {
            return jedisCluster.get(key);
        }

        @Override
        public boolean containsKey(byte[] key) {
            return jedisCluster.exists(key);
        }

        @Override
        public void put(byte[] key, byte[] value) {
            jedisCluster.set(key, value);
        }

        @Override
        public byte[] getAndPut(byte[] key, byte[] value) {
            return jedisCluster.getSet(key, value);
        }

        @Override
        public boolean remove(byte[] key) {
            return jedisCluster.del(key) > 0;
        }

        @Override
        public byte[] getAndRemove(byte[] key) {
            byte[] bytes = jedisCluster.get(key);
            jedisCluster.del(key);
            return bytes;
        }

        @Override
        public void clear() {

        }
    }
}
