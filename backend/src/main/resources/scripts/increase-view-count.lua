-- 상품 조회수(랭킹 ZSET 점수)를 원자적으로 증가시킨다 (중복 조회는 무시)
-- KEYS[1]: 랭킹 ZSET 키 (ex: "ranking:view:realtime")
-- KEYS[2]: 중복 조회 체크 키 (ex: "viewed:1:127.0.0.1")
-- ARGV[1]: productId (member)
-- ARGV[2]: 증가시킬 점수 (기본 1)
-- ARGV[3]: 중복 방지 TTL(초)

if redis.call('EXISTS', KEYS[2]) == 1 then --call('EXISTS', 127.0.0.1)
    return redis.call('ZSCORE', KEYS[1], ARGV[1]) --현재 스코어
end

redis.call('SET', KEYS[2], 1, 'EX', ARGV[3]) --call('SET', 127.0.0.0, 1, 'EX', 60초)
return redis.call('ZINCRBY', KEYS[1], ARGV[2], ARGV[1])
