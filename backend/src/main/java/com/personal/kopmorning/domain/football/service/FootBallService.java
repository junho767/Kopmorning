package com.personal.kopmorning.domain.football.service;

import com.personal.kopmorning.domain.football.dto.response.TeamResponse;
import com.personal.kopmorning.domain.football.dto.response.TeamsResponse;
import com.personal.kopmorning.domain.football.entity.Team;
import com.personal.kopmorning.domain.football.repository.FootBallRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class FootBallService {
    private final FootBallRepository footBallRepository;
    private final WebClient webClient;

    public FootBallService(FootBallRepository footBallRepository, @Qualifier("fooballWebClient") WebClient webClient) {
        this.footBallRepository = footBallRepository;
        this.webClient = webClient;
    }

    // webClient 비동기 호출 및 data 저장
    public void saveTeams() {
        try {
            TeamsResponse teamsResponse = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/competitions/PL/teams")
                            .build())
                    .retrieve()
                    .bodyToMono(TeamsResponse.class)
                    .block();

            List<Team> teamEntities = Objects.requireNonNull(teamsResponse).getTeams().stream()
                    .map(Team::new)
                    .toList();

            footBallRepository.saveAll(teamEntities);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    // 비동기 호출
    public Mono<TeamsResponse> getTeams(String competition, Long season) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/competitions/" + competition + "/teams")
                        .queryParam("season", season)
                        .build())
                .retrieve()
                .bodyToMono(TeamsResponse.class);
    }
}
