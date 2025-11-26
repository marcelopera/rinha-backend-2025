package br.com.truta.service;

import br.com.truta.models.PaymentRequest;
import io.quarkus.redis.datasource.RedisDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CacheService {

    @Inject
    RedisDataSource redisDataSource;

    public void add(PaymentRequest req) {
        redisDataSource.value(String.class).set(req.correlationId(), req.amount().toString());
    }

    public java.util.List<String> getAllKeys() {
        return redisDataSource.key().keys("*");
    }

    public String get(String key) {
        return redisDataSource.value(String.class).get(key);
    }

    public String del(String key) {
        return redisDataSource.value(String.class).getdel(key);
    }

}
