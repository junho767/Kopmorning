package com.personal.kopmorning.football.service;

import com.personal.kopmorning.domain.football.dto.response.PlayerDetailResponse;
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
import com.personal.kopmorning.domain.football.service.FootBallService;
import org.junit.jupiter.api.BeforeEach;
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
                new Team(52L, "Tottenham", "1882", "England", "badge1.png"),
                new Team(61L, "Man City", "1880", "England", "badge2.png")
        );
        when(teamRepository.findAll()).thenReturn(mockTeams);

        // when
        List<TeamResponse> result = footBallService.getTeams();

        // then
        assertEquals(2, result.size());
        assertEquals("Tottenham", result.getFirst().getTeam_name());
        assertEquals("Man City", result.get(1).getTeam_name());
    }

    @Test
    @DisplayName("팀과 소속 선수 리턴")
    void getTeamById() {
        // given
        Long teamId = 52L;
        Team team = new Team(teamId, "Tottenham", "1882", "England", "badge1.png");
        List<Player> players = List.of(
                new Player(1001L, teamId, "Son", "Son", "img1", "KR", "7", "30", "Forward", "1992"),
                new Player(1002L, teamId, "Maddison", "Maddison", "img2", "UK", "10", "26", "Mid", "1996")
        );

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(playerRepository.findByTeamId(teamId)).thenReturn(players);

        // when
        TeamDetailResponse result = footBallService.getTeamById(teamId);

        // then
        assertEquals("Tottenham", result.getTeam_name());
        assertEquals(2, result.getPlayers().size());
        assertEquals("Son", result.getPlayers().getFirst().getPlayer_name());
        assertEquals("Maddison", result.getPlayers().get(1).getPlayer_name());
    }

    @Test
    @DisplayName("전제 순위 반환")
    void getStanding() {
        // given
        Standing standing1 = new Standing();
        standing1.setTeam_name("Arsenal");
        standing1.setOverall_league_position("1");
        standing1.setOverall_league_PTS("85");

        Standing standing2 = new Standing();
        standing2.setTeam_name("Man City");
        standing2.setOverall_league_position("2");
        standing2.setOverall_league_PTS("83");

        List<Standing> standings = List.of(standing1, standing2);

        when(standingRepository.findAll()).thenReturn(standings);

        // when
        StandingResponse result = footBallService.getStanding();

        // then
        assertEquals(2, result.getStandings().size());
        assertEquals("Arsenal", result.getStandings().get(0).getTeam_name());
        assertEquals("Man City", result.getStandings().get(1).getTeam_name());
        assertEquals("1", result.getStandings().get(0).getOverall_league_position());
        assertEquals("2", result.getStandings().get(1).getOverall_league_position());
    }
}
