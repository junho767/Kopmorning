package com.personal.kopmorning.domain.football.service;

import com.personal.kopmorning.domain.football.dto.MatchDTO;
import com.personal.kopmorning.domain.football.dto.RankingDTO;
import com.personal.kopmorning.domain.football.dto.StandingDTO;
import com.personal.kopmorning.domain.football.dto.TeamDTO;
import com.personal.kopmorning.domain.football.dto.response.GameResponse;
import com.personal.kopmorning.domain.football.dto.response.PlayerResponse;
import com.personal.kopmorning.domain.football.dto.response.RankingResponse;
import com.personal.kopmorning.domain.football.dto.response.StandingResponse;
import com.personal.kopmorning.domain.football.dto.response.TeamDetailResponse;
import com.personal.kopmorning.domain.football.dto.response.TeamResponse;
import com.personal.kopmorning.domain.football.entity.Coach;
import com.personal.kopmorning.domain.football.entity.Game;
import com.personal.kopmorning.domain.football.entity.Player;
import com.personal.kopmorning.domain.football.entity.Ranking;
import com.personal.kopmorning.domain.football.entity.Standing;
import com.personal.kopmorning.domain.football.entity.Team;
import com.personal.kopmorning.domain.football.repository.CoachRepository;
import com.personal.kopmorning.domain.football.repository.GameRepository;
import com.personal.kopmorning.domain.football.repository.PlayerRepository;
import com.personal.kopmorning.domain.football.repository.RankingRepository;
import com.personal.kopmorning.domain.football.repository.StandingRepository;
import com.personal.kopmorning.domain.football.repository.TeamRepository;
import com.personal.kopmorning.domain.football.responseCode.FootBallErrorCode;
import com.personal.kopmorning.global.exception.FootBall.FootBallException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FootBallService {
    private final WebClient webClient;
    private final TeamRepository teamRepository;
    private final GameRepository gameRepository;
    private final CoachRepository coachRepository;
    private final PlayerRepository playerRepository;
    private final RankingRepository rankingRepository;
    private final StandingRepository standingRepository;

    private static final String GOALS_PARAM = "goals";
    private static final String OVERALL_PARAM = "overall";

    private static final String GAME_REQUEST_PATH = "teams/64/matches";
    private static final String TEAMS_REQUEST_PATH = "competitions/PL/teams";
    private static final String RANKING_REQUEST_PATH = "competitions/PL/scorers";
    private static final String STANDING_REQUEST_PATH = "competitions/PL/standings";


    public FootBallService(
            PlayerRepository playerRepository,
            TeamRepository teamRepository,
            GameRepository gameRepository,
            CoachRepository coachRepository,
            StandingRepository standingRepository,
            @Qualifier("footballWebClient") WebClient webClient, RankingRepository rankingRepository
    ) {
        this.webClient = webClient;
        this.teamRepository = teamRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.coachRepository = coachRepository;
        this.standingRepository = standingRepository;
        this.rankingRepository = rankingRepository;
    }


    // todo : 대회 정보, 팀 별 국가 데이터
    @Retry(name = "footballApi", fallbackMethod = "fallbackOpenAPI")
    @CircuitBreaker(name = "footballApi", fallbackMethod = "fallbackOpenAPI")
    public void saveTeamAndPlayer() {
        try {
            List<Player> playerList = new ArrayList<>();
            List<Coach> coachList = new ArrayList<>();

            TeamDTO teamDTO = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(TEAMS_REQUEST_PATH)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<TeamDTO>() {
                    })
                    .block();

            List<Team> teamEntities = Objects.requireNonNull(teamDTO.getTeams()).stream()
                    .map(Team::new)
                    .collect(Collectors.toList());

            for (TeamDTO.Team team : teamDTO.getTeams()) {
                coachList.add(new Coach(team.coach()));

                for (TeamDTO.Player player : team.squad()) {
                    Player playerEntity = new Player(player);
                    playerEntity.setTeamId(team.id());
                    playerList.add(playerEntity);
                }
            }

            teamRepository.saveAll(teamEntities);
            playerRepository.saveAll(playerList);
            coachRepository.saveAll(coachList);
        } catch (Exception e) {
            log.error("❗ standings 저장 중 오류 발생", e);
            throw new FootBallException(
                    FootBallErrorCode.PLAYER_API_ERROR.getCode(),
                    FootBallErrorCode.PLAYER_API_ERROR.getMessage(),
                    FootBallErrorCode.PLAYER_API_ERROR.getHttpStatus()
            );
        }
    }


    @Retry(name = "footballApi", fallbackMethod = "fallbackOpenAPI")
    @CircuitBreaker(name = "footballApi", fallbackMethod = "fallbackOpenAPI")
    public void saveStanding() {
        try {
            StandingDTO standingDTO = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(STANDING_REQUEST_PATH)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<StandingDTO>() {
                    })
                    .block();

            List<StandingDTO.Table> tables = standingDTO.getStandings().getFirst().table();

            List<Standing> standing = tables
                    .stream()
                    .map(Standing::new)
                    .toList();

            standingRepository.saveAll(standing);
        } catch (Exception e) {
            log.error("❗ standings 저장 중 오류 발생", e);
            throw new FootBallException(
                    FootBallErrorCode.STANDING_API_ERROR.getCode(),
                    FootBallErrorCode.STANDING_API_ERROR.getMessage(),
                    FootBallErrorCode.STANDING_API_ERROR.getHttpStatus()
            );
        }
    }

    @Retry(name = "footballApi", fallbackMethod = "fallbackOpenAPI")
    @CircuitBreaker(name = "footballApi", fallbackMethod = "fallbackOpenAPI")
    public void saveFixtures() {
        try {
            MatchDTO matchDTO = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(GAME_REQUEST_PATH)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<MatchDTO>() {})
                    .block();

            List<Game> gameList = matchDTO.getMatches().stream()
                    .map(Game::new)
                    .toList();

            gameRepository.saveAll(gameList);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new FootBallException(
                    FootBallErrorCode.FIXTURES_API_ERROR.getCode(),
                    FootBallErrorCode.FIXTURES_API_ERROR.getMessage(),
                    FootBallErrorCode.FIXTURES_API_ERROR.getHttpStatus()
            );
        }
    }

    @Retry(name = "footballApi", fallbackMethod = "fallbackOpenAPI")
    @CircuitBreaker(name = "footballApi", fallbackMethod = "fallbackOpenAPI")
    public void saveTopScorer() {
        try {
            RankingDTO rankingDTO = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(RANKING_REQUEST_PATH)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<RankingDTO>() {})
                    .block();

            List<Ranking> ranking = rankingDTO.getScorers().stream()
                    .map(Ranking::new)
                    .toList();

            rankingRepository.saveAll(ranking);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new FootBallException(
                    FootBallErrorCode.TOP_SCORER_API_ERROR.getCode(),
                    FootBallErrorCode.TOP_SCORER_API_ERROR.getMessage(),
                    FootBallErrorCode.TOP_SCORER_API_ERROR.getHttpStatus()
            );
        }
    }

    public void fallbackOpenAPI(Throwable t) {
        log.error("🛑 Fallback 호출 - saveStanding() 실패", t);
    }

    public List<TeamResponse> getTeams() {
        List<Team> teamList = teamRepository.findAll();

        return teamList.stream()
                .map(TeamResponse::new)
                .toList();
    }

    public TeamDetailResponse getTeamById(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new FootBallException(
                                FootBallErrorCode.TEAM_NOT_FOUND.getCode(),
                                FootBallErrorCode.TEAM_NOT_FOUND.getMessage(),
                                FootBallErrorCode.TEAM_NOT_FOUND.getHttpStatus()
                        )
                );

        List<Player> playerList = playerRepository.findByTeamId(teamId);
        List<PlayerResponse> players = playerList.stream()
                .map(PlayerResponse::new)
                .toList();

        return new TeamDetailResponse(team, players);
    }

    @Cacheable(value = "standingCache", key = "'standing'")
    public StandingResponse getStanding() {
        log.info("⛔ 캐시에 없음 → DB 조회 → 캐시에 저장");
        List<Standing> standing = standingRepository.findAllByOrderByPositionDesc();
        return new StandingResponse(standing);
    }

    public List<GameResponse> getGameList() {
        List<Game> gameList = gameRepository.findAll();
        return gameList.stream().map(GameResponse::new).toList();
    }

    @Cacheable(value = "rankingCache", key = "'ranking:' + #standard")
    public List<RankingResponse> getRanking(String standard) {
        log.info("⛔ 캐시에 없음 → DB 조회 → 캐시에 저장");
        List<Ranking> ranking;

        if (standard.equals(GOALS_PARAM)) {
            ranking = rankingRepository.findAllByOrderByGoalsDesc();
        } else if (standard.equals(OVERALL_PARAM)) {
            ranking = rankingRepository.findAllOrderByGoalPlusAssistNative();
        } else {
            throw new FootBallException(
                    FootBallErrorCode.RANKING_NOT_FOUND.getCode(),
                    FootBallErrorCode.RANKING_NOT_FOUND.getMessage(),
                    FootBallErrorCode.RANKING_NOT_FOUND.getHttpStatus()
            );
        }

        AtomicInteger rankCounter = new AtomicInteger(1);

        return ranking.stream()
                .map(r -> {
                    Player player = playerRepository.findById(r.getPlayerId()).orElseThrow(() -> new FootBallException(
                                    FootBallErrorCode.PLAYER_NOT_FOUND.getCode(),
                                    FootBallErrorCode.PLAYER_NOT_FOUND.getMessage(),
                                    FootBallErrorCode.PLAYER_NOT_FOUND.getHttpStatus()
                            )
                    );
                    Team team = teamRepository.findById(r.getTeamId()).orElseThrow(() -> new FootBallException(
                                    FootBallErrorCode.TEAM_NOT_FOUND.getCode(),
                                    FootBallErrorCode.TEAM_NOT_FOUND.getMessage(),
                                    FootBallErrorCode.TEAM_NOT_FOUND.getHttpStatus()
                            )
                    );
                    RankingResponse response = new RankingResponse(r, player, team);
                    response.setRank(rankCounter.getAndIncrement());
                    return response;
                })
                .collect(Collectors.toList());
    }
}
