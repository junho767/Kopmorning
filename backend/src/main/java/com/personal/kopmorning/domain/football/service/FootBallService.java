package com.personal.kopmorning.domain.football.service;

import com.personal.kopmorning.domain.football.dto.response.PlayerDTO;
import com.personal.kopmorning.domain.football.dto.response.TeamDTO;
import com.personal.kopmorning.domain.football.entity.Player;
import com.personal.kopmorning.domain.football.entity.PlayerStat;
import com.personal.kopmorning.domain.football.entity.Team;
import com.personal.kopmorning.domain.football.repository.PlayerRepository;
import com.personal.kopmorning.domain.football.repository.PlayerStatRepository;
import com.personal.kopmorning.domain.football.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FootBallService {
    private final WebClient webClient;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final PlayerStatRepository playerStatRepository;

    @Value("${api.token.football}")
    private String apiToken;

    public FootBallService(PlayerRepository playerRepository, TeamRepository teamRepository, @Qualifier("fooballWebClient") WebClient webClient, PlayerStatRepository playerStatRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.webClient = webClient;
        this.playerStatRepository = playerStatRepository;
    }

    // webClient 비동기 호출 및 data 저장
    public void saveTeams() {
        try {
            List<Player> playerList = new ArrayList<>();
            List<PlayerStat> playerStatList = new ArrayList<>();

            List<TeamDTO> teamList = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("action", "get_teams")
                            .queryParam("league_id", "152")
                            .queryParam("APIkey", apiToken)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<TeamDTO>>() {
                    })
                    .block();

            List<Team> teamEntities = Objects.requireNonNull(teamList).stream()
                    .map(Team::new)
                    .collect(Collectors.toList());

            for (TeamDTO team : teamList) {
                for (PlayerDTO playerDTO : team.getPlayers()) {
                    Player playerEntity = new Player(playerDTO);
                    PlayerStat playerStat = new PlayerStat(playerDTO);

                    playerStatList.add(playerStat);
                    playerList.add(playerEntity);
                }
            }

            teamRepository.saveAll(teamEntities);
            playerRepository.saveAll(playerList);
            playerStatRepository.saveAll(playerStatList);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    // 비동기 호출
//    public Mono<TeamsDTO> getTeams(String competition, Long season) {
//        return webClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/competitions/" + competition + "/teams")
//                        .queryParam("season", season)
//                        .build())
//                .retrieve()
//                .bodyToMono(TeamsDTO.class);
//    }

//    // todo : 리펙토링 해야댐 - 선수 정보 저장 하는 방법 및 예외 처리
//    public PlayerResponse getPlayer(Long playerId) {
//        return new PlayerResponse(playerRepository.findById(playerId)
//                .orElseThrow(() -> new RuntimeException("존재하지 않는 선수 입니다.")));
//    }
}
