package com.personal.kopmorning.domain.football.service;

import com.personal.kopmorning.domain.football.dto.response.PlayerResponse;
import com.personal.kopmorning.domain.football.dto.response.TeamResponse;
import com.personal.kopmorning.domain.football.dto.response.TeamsResponse;
import com.personal.kopmorning.domain.football.entity.Player;
import com.personal.kopmorning.domain.football.entity.Team;
import com.personal.kopmorning.domain.football.repository.PlayerRepository;
import com.personal.kopmorning.domain.football.repository.TeamRepository;
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
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final WebClient webClient;

    public FootBallService(PlayerRepository playerRepository, TeamRepository teamRepository, @Qualifier("fooballWebClient") WebClient webClient) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
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

            teamRepository.saveAll(teamEntities);
            savePlayers(teamsResponse);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void savePlayers(TeamsResponse teamsResponse) {
        try {
            for (TeamResponse team : teamsResponse.getTeams()) {
                TeamResponse teamResponse = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/teams/" + team.getId())
                                .build())
                        .retrieve()
                        .bodyToMono(TeamResponse.class)
                        .block();
                if (teamResponse.getSquad() != null) {
                    for (PlayerResponse playerResponse : teamResponse.getSquad()) {
                        Player player = new Player(playerResponse);
                        playerRepository.save(player);
                    }
                }

                // 각 호출 후 6초 대기
                Thread.sleep(6000);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
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
