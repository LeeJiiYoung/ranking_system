package com.ranking.ranking;

import com.ranking.ranking.dto.RankingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class RankingController {

	private final RankingService rankingService;

	@GetMapping("/rankings")
	public List<RankingResponse> getRankings(@RequestParam(defaultValue = "10") int limit) {

        return List.of();
    }

}
