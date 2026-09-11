package com.ranking.cache;

import java.io.Serializable;

public record RankingItem(
        Long productId,
        String productName,
        long viewCount
) implements Serializable {
}
