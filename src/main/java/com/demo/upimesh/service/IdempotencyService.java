package com.demo.upimesh.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory idempotency cache. In production this would be Redis with SETNX +
 * TTL — exactly the same semantics, just distributed across instances.
 *
 * The contract:
 *   - claim(hash) returns true on first call, false on every call after that
 *     (within the TTL window)
 *   - the operation is atomic — even if 100 threads call claim(hash) at the
 *     same instant, exactly one returns true
 *
 * This is what kills the "three bridges deliver simultaneously" problem.
 * ConcurrentHashMap.putIfAbsent is the JVM-local equivalent of Redis SETNX.
 */
@Service
public class IdempotencyService {

    private final Map<String, Instant> seen = new ConcurrentHashMap<>();

    @Value("${upi.mesh.idempotency-ttl-seconds:86400}")
    private long ttlSeconds;

    /**
     * Try to claim a hash. Returns true if this caller is the first; false if
     * someone else already claimed it (i.e. the packet is a duplicate).
     */
    public boolean claim(String packetHash) {
        Instant now = Instant.now();
        Instant prev = seen.putIfAbsent(packetHash, now);
        return prev == null;
    }

    public int size() {
        return seen.size();
    }

    /** Periodically evict entries past their TTL so the map doesn't grow forever. */
    @Scheduled(fixedDelay = 60_000)
    public void evictExpired() {
        Instant cutoff = Instant.now().minusSeconds(ttlSeconds);
        seen.entrySet().removeIf(e -> e.getValue().isBefore(cutoff));
    }

    /** Test/demo helper. */
    public void clear() {
        seen.clear();
    }
}
