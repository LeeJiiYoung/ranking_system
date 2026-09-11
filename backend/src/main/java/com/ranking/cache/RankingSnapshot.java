package com.ranking.cache;

import java.io.Serializable;
import java.util.List;

public record RankingSnapshot(
        List<RankingItem> items,
        long logicalExpireAt   // epoch millis, TTL 대신 값 안에 직접 만료 시각을 저장
) implements Serializable {
    public boolean isExpired() {
        return System.currentTimeMillis() > logicalExpireAt;
    }
}
