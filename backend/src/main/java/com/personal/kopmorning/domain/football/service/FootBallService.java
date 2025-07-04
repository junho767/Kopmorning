package com.personal.kopmorning.domain.football.service;

import com.personal.kopmorning.domain.football.dto.PlayerDTO;
import com.personal.kopmorning.domain.football.dto.TeamDTO;
import com.personal.kopmorning.domain.football.dto.response.PlayerDetailResponse;
import com.personal.kopmorning.domain.football.dto.response.PlayerResponse;
import com.personal.kopmorning.domain.football.dto.response.TeamDetailResponse;
import com.personal.kopmorning.domain.football.dto.response.TeamResponse;
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

    public void saveFootBallData() {
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
                    playerEntity.setTeamId(Long.valueOf(team.getTeam_key()));
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

    public List<TeamResponse> getTeams() {
        List<Team> teamList = teamRepository.findAll();

        return teamList.stream()
                .map(TeamResponse::new)
                .toList();
    }

    public TeamDetailResponse getTeamById(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        List<Player> playerList = playerRepository.findByTeamId(teamId);
        List<PlayerResponse> players = playerList.stream()
                .map(PlayerResponse::new)
                .toList();

        return new TeamDetailResponse(team, players);
    }

    public PlayerDetailResponse getPlayer(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        PlayerStat playerStat = playerStatRepository.findByPlayerId(playerId);

        return new PlayerDetailResponse(player, playerStat);
    }
}
