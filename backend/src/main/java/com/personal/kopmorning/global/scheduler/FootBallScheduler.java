package com.personal.kopmorning.global.scheduler;

import com.personal.kopmorning.domain.football.service.FootBallService;
import com.personal.kopmorning.global.entity.SchedulerStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class FootBallScheduler {
    private final FootBallService footBallService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String FAIL = "FAIL";
    private static final String RUNNING = "RUNNING";
    private static final String SUCCESS = "SUCCESS";
    private static final long CACHE_TTL_MS = 600000;
    private static final long FIXED_DELAY_MS = 30000;
    private static final String JOB_NAME = "축구 데이터 업데이트";
    private static final String STATUS_KEY = "scheduler:football:lastRun";

    @Scheduled(fixedDelay = FIXED_DELAY_MS)
    public void footBallDataSave() {
        String runId = UUID.randomUUID().toString();
        String startedAt = getCurrentTimeString();
        long startTime = System.currentTimeMillis();
    
        SchedulerStatus status = createStatus(runId, startedAt);
    
        executeFootBallDataUpdateAsync(status, startTime);
    }

    private void executeFootBallDataUpdateAsync(SchedulerStatus status, long startTime) {
        Mono.when(
            footBallService.saveTeamAndPlayer(),
            footBallService.saveStanding(),
            footBallService.saveFixtures(),
            footBallService.saveTopScorer()
        )
        .doOnSuccess(result -> {
            updateStatusSuccess(status, startTime);
            log.info("{} 완료 - 소요시간: {}ms", JOB_NAME, status.getDurationMs());
        })
        .doOnError(error -> {
            updateStatusFail(status, startTime);
            log.error("{} 실패 - 소요시간: {}ms", JOB_NAME, status.getDurationMs(), error);
        })
        .subscribe();
    }

    private SchedulerStatus createStatus(String runId, String startedAt) {
        return SchedulerStatus.builder()
                .jobName(JOB_NAME)
                .status(RUNNING)
                .lastStartedAt(startedAt)
                .runId(runId)
                .build();
    }

    private void updateStatusSuccess(SchedulerStatus status, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        status.setStatus(SUCCESS);
        status.setDurationMs(duration);
        status.setLastFinishedAt(getCurrentTimeString());
        saveStatusToRedis(status);
    }

    private void updateStatusFail(SchedulerStatus status, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        status.setStatus(FAIL);
        status.setDurationMs(duration);
        status.setLastFinishedAt(getCurrentTimeString());
        saveStatusToRedis(status);
    }

    private void saveStatusToRedis(SchedulerStatus status) {
        redisTemplate.opsForValue().set(STATUS_KEY, status, CACHE_TTL_MS, TimeUnit.MILLISECONDS);
    }

    private String getCurrentTimeString() {
        return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
    }

    public Object getCurrentStatus() {
        return redisTemplate.opsForValue().get(STATUS_KEY);
    }
}
