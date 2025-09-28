package dev.juntralala.oauth.scheduler;

import dev.juntralala.oauth.repository.AuthorizationCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class CleanAuthorizationCodeScheduler {

    private final AuthorizationCodeRepository authorizationCodeRepository;

    public CleanAuthorizationCodeScheduler(
            AuthorizationCodeRepository authorizationCodeRepository
    ) {
        this.authorizationCodeRepository = authorizationCodeRepository;
    }

    @Scheduled(fixedDelay = 10_000)
    public void clean() {
        int deletedCount = authorizationCodeRepository.deleteByExpiresAtBefore(Instant.now());
        if (log.isInfoEnabled()) {
            log.info("deleted authorization code: {}", deletedCount);
        }
    }
}
