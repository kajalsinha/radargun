package org.radargun.service.redis;


import org.radargun.Service;
import org.radargun.config.Property;
import org.radargun.logging.Log;
import org.radargun.logging.LogFactory;
import org.radargun.traits.Lifecycle;
import org.radargun.traits.ProvidesTrait;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service(doc = "Redis 3 clustered service")
public class Redis3Service implements Lifecycle {

    protected final Log log = LogFactory.getLog(getClass());

    @Property(name = "addresses", doc = "Addresses of clustered Redis instances")
    public String addresses;

    @Property(name = "redisConnector", doc = "Connector for redis e")
    public String redisConnector;

    private boolean running;

    private JedisCluster jedisCluster;

    @ProvidesTrait
    public RedisBasicOperations getBasicOperations() {
        return new RedisBasicOperations(this);
    }

    @ProvidesTrait
    public Lifecycle getLifecycle() {
        return this;
    }

    @Override
    public void start() {
        jedisCluster = new JedisCluster(loadAddresses());
        running = true;
    }

    private Set<HostAndPort> loadAddresses() {
        HashSet<HostAndPort> result = new HashSet<>();
        String[] stringAddresses = addresses.split(",");
        for (String address : stringAddresses) {
            String[] tokens = address.split(":");

            if (tokens.length != 2) {
                throw new RuntimeException("Address " + address +  " does not match format host:port");
            }

            String host = tokens[0].trim();
            Integer port;
            try {
                port = Integer.parseInt(tokens[1].trim());
            } catch (NumberFormatException nfe) {
                throw new RuntimeException("Port in address " + address +  " is not numeric");
            }

            result.add(new HostAndPort(host, port));
        }

        return result;
    }

    @Override
    public void stop() {
        try {
            jedisCluster.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }
}
