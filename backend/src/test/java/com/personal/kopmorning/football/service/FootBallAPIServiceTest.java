package com.personal.kopmorning.football.service;

import com.personal.kopmorning.domain.football.dto.response.StandingResponse;
import com.personal.kopmorning.domain.football.dto.response.TeamDetailResponse;
import com.personal.kopmorning.domain.football.dto.response.TeamResponse;
import com.personal.kopmorning.domain.football.entity.Player;
import com.personal.kopmorning.domain.football.entity.Standing;
import com.personal.kopmorning.domain.football.entity.Team;
import com.personal.kopmorning.domain.football.repository.PlayerRepository;
import com.personal.kopmorning.domain.football.repository.StandingRepository;
import com.personal.kopmorning.domain.football.repository.TeamRepository;
import com.personal.kopmorning.domain.football.service.FootBallService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FootBallAPIServiceTest {
    @InjectMocks
    private FootBallService footBallService;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private StandingRepository standingRepository;

    @Test
    @DisplayName("전체 팀 리스트 반환")
    void getTeamsTest() {
        // given
        List<Team> mockTeams = List.of(
                new Team(
                        1L,
                        "Tottenham Hotspur",
                        "Tottenham",
                        "TOT",
                        1882L,
                        "748 High Road, London",
                        "https://www.tottenhamhotspur.com",
                        "White / Navy Blue",
                        "Tottenham Hotspur Stadium",
                        "https://crests.football-data.org/73.png"
                ),
                new Team(
                        61L,
                        "Manchester City",
                        "Man City",
                        "MCI",
                        1880L,
                        "Etihad Stadium, Manchester",
                        "https://www.mancity.com",
                        "Sky Blue / White",
                        "Etihad Stadium",
                        "https://crests.football-data.org/61.png"
                )
        );
        when(teamRepository.findAll()).thenReturn(mockTeams);

        // when
        List<TeamResponse> result = footBallService.getTeams();

        // then
        assertEquals(2, result.size());
        assertEquals("TOT", result.getFirst().getTla());
        assertEquals("MCI", result.get(1).getTla());
    }

    @Test
    @DisplayName("팀과 소속 선수 리턴")
    void getTeamById() {
        // given
        Long teamId = 52L;
        Team team = new Team(
                1L,
                "Tottenham Hotspur",
                "Tottenham",
                "TOT",
                1882L,
                "748 High Road, London",
                "https://www.tottenhamhotspur.com",
                "White / Navy Blue",
                "Tottenham Hotspur Stadium",
                "https://crests.football-data.org/73.png"
        );
        List<Player> players = List.of(
                new Player(1001L, teamId, "Son", "KR", "30", "Forward"),
                new Player(1002L, teamId, "Maddison", "UK", "26", "Mid")
        );

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(playerRepository.findByTeamId(teamId)).thenReturn(players);

        // when
        TeamDetailResponse result = footBallService.getTeamById(teamId);

        // then
        assertEquals("Tottenham", result.getShortName());
        assertEquals(2, result.getPlayers().size());
        assertEquals("Son", result.getPlayers().getFirst().getPlayer_name());
        assertEquals("Maddison", result.getPlayers().get(1).getPlayer_name());
    }

    @Test
    @DisplayName("전제 순위 반환")
    void getStanding() {
        // given
        Standing standing1 = new Standing();
        standing1.setId(64L);
        standing1.setPosition(1L);
        standing1.setPoints(93L);

        Standing standing2 = new Standing();
        standing2.setId(65L);
        standing2.setPosition(2L);
        standing2.setPoints(92L);

        List<Standing> standings = List.of(standing1, standing2);

        when(standingRepository.findAllByOrderByPositionDesc()).thenReturn(standings);

        // when
        StandingResponse result = footBallService.getStanding();

        // then
        assertEquals(2, result.getStandings().size());
        assertEquals(64L, result.getStandings().get(0).getId());
        assertEquals(65L, result.getStandings().get(1).getId());
        assertEquals(1L, result.getStandings().get(0).getPosition());
        assertEquals(2L, result.getStandings().get(1).getPosition());
    }
}
