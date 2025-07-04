package com.personal.kopmorning.domain.football.service;

import com.personal.kopmorning.domain.football.dto.PlayerDTO;
import com.personal.kopmorning.domain.football.dto.StandingDTO;
import com.personal.kopmorning.domain.football.dto.TeamDTO;
import com.personal.kopmorning.domain.football.dto.response.PlayerDetailResponse;
import com.personal.kopmorning.domain.football.dto.response.PlayerResponse;
import com.personal.kopmorning.domain.football.dto.response.StandingResponse;
import com.personal.kopmorning.domain.football.dto.response.TeamDetailResponse;
import com.personal.kopmorning.domain.football.dto.response.TeamResponse;
import com.personal.kopmorning.domain.football.entity.Player;
import com.personal.kopmorning.domain.football.entity.PlayerStat;
import com.personal.kopmorning.domain.football.entity.Standing;
import com.personal.kopmorning.domain.football.entity.Team;
import com.personal.kopmorning.domain.football.repository.PlayerRepository;
import com.personal.kopmorning.domain.football.repository.PlayerStatRepository;
import com.personal.kopmorning.domain.football.repository.StandingRepository;
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
    private final StandingRepository standingRepository;
    private final PlayerStatRepository playerStatRepository;

    @Value("${api.token.football}")
    private String apiToken;

    public FootBallService(PlayerRepository playerRepository, TeamRepository teamRepository, @Qualifier("fooballWebClient") WebClient webClient, StandingRepository standingRepository, PlayerStatRepository playerStatRepository) {
        this.webClient = webClient;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.standingRepository = standingRepository;
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

    public void saveStanding() {
        try {
            List<StandingDTO> standingDTO = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("action", "get_standings")
                            .queryParam("league_id", 152)
                            .queryParam("APIkey", apiToken)
                            .build())
                    .retrieve()
                    .bodyToFlux(StandingDTO.class)
                    .collectList()
                    .block();

            List<Standing> standing = standingDTO
                    .stream()
                    .map(Standing::new)
                    .toList();

            standingRepository.saveAll(standing);
        } catch (Exception e) {
            log.error("❗ standings 저장 중 오류 발생", e);
            throw new RuntimeException(e);
        }
    }

    // todo : 홈, 원정에 따른 필터링 방식에 대한 고려
    public StandingResponse getStanding() {
        List<Standing> standing = standingRepository.findAll();
        return new StandingResponse(standing);
    }
}
