package com.haiphamcoder.storage.shared;

import java.net.InetAddress;
import java.security.SecureRandom;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnowflakeIdGenerator {
    private static SnowflakeIdGenerator instance;

    private static final long EPOCH_DEFAULT = 1735664400000L; // 2025-01-01 00:00:00 GMT+7
    private static final long NODE_ID_BITS = 10L; // 10 bits for node id
    private static final long SEQUENCE_BITS = 12L; // 12 bits for sequence

    // Maximum values for node id and sequence
    private static final long MAX_NODE_ID = (1L << NODE_ID_BITS) - 1;
    private static final long SEQUENCE_MASK = (1L << SEQUENCE_BITS) - 1;

    private static final long NODE_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = NODE_ID_BITS + SEQUENCE_BITS;

    private final long nodeId;
    private final long epoch;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long nodeId, long epoch) {
        if (nodeId > MAX_NODE_ID || nodeId < 0) {
            throw new IllegalArgumentException(
                    String.format("Node id can't be greater than %d or less than 0", MAX_NODE_ID));
        }
        this.nodeId = nodeId;
        this.epoch = epoch;
    }

    public SnowflakeIdGenerator(long nodeId) {
        this(nodeId, EPOCH_DEFAULT);
    }

    public SnowflakeIdGenerator() {
        this.nodeId = createNodeId();
        this.epoch = EPOCH_DEFAULT;
    }

    public static SnowflakeIdGenerator getInstance() {
        if (instance == null) {
            instance = new SnowflakeIdGenerator();
        }
        return instance;
    }

    private long createNodeId() {
        long id;
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String ip = inetAddress.getHostAddress();
            id = ip.hashCode();
        } catch (Exception ex) {
            id = (new SecureRandom().nextInt());
        }
        id = id & MAX_NODE_ID;
        return id;
    }

    public synchronized long generateId() {
        long currentTimestamp = System.currentTimeMillis();

        if (currentTimestamp < lastTimestamp) {
            long offset = lastTimestamp - currentTimestamp;
            if (offset <= 5) {
                sequence = (sequence + 1) & SEQUENCE_MASK;
                if (sequence == 0) {
                    currentTimestamp = waitNextMillis(lastTimestamp);
                }
                log.warn("Clock is moving backwards. Adjusting sequence for {} milliseconds", offset);
            } else {
                throw new IllegalStateException(
                        "Clock moved backwards. Refusing to generate id for " + offset + " milliseconds");
            }
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - epoch) << TIMESTAMP_LEFT_SHIFT) |
                (nodeId << NODE_ID_SHIFT) | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

}
