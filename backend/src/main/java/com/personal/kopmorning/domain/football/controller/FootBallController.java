package com.personal.kopmorning.domain.football.controller;

import com.personal.kopmorning.domain.football.dto.response.GameResponse;
import com.personal.kopmorning.domain.football.dto.response.PlayerDetailResponse;
import com.personal.kopmorning.domain.football.dto.response.StandingResponse;
import com.personal.kopmorning.domain.football.dto.response.TeamDetailResponse;
import com.personal.kopmorning.domain.football.dto.response.TeamResponse;
import com.personal.kopmorning.domain.football.responseCode.FootBallSuccessCode;
import com.personal.kopmorning.domain.football.service.FootBallService;
import com.personal.kopmorning.global.entity.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/football")
@RequiredArgsConstructor
public class FootBallController {
    private final FootBallService footBallService;

    @PostMapping("/save")
    public RsData<?> save() {
        footBallService.saveFootBallData();
        return new RsData<>(
                FootBallSuccessCode.SAVE_INFO.getCode(),
                FootBallSuccessCode.SAVE_INFO.getMessage()
        );
    }

    @PostMapping("/save/standing")
    public RsData<?> saveStanding() {
        footBallService.saveStanding();
        footBallService.saveFixtures();
        return new RsData<>(
                FootBallSuccessCode.SAVE_STANDING.getCode(),
                FootBallSuccessCode.SAVE_STANDING.getMessage()
        );
    }

    @GetMapping("/team")
    public RsData<List<TeamResponse>> getTeams() {
        return new RsData<>(
                FootBallSuccessCode.GET_TEAM_LIST.getCode(),
                FootBallSuccessCode.GET_TEAM_LIST.getMessage(),
                footBallService.getTeams()
        );
    }

    @GetMapping("/team/{team_id}")
    public RsData<TeamDetailResponse> getTeam(@PathVariable Long team_id) {
        return new RsData<>(
                FootBallSuccessCode.GET_TEAM_ONE.getCode(),
                FootBallSuccessCode.GET_TEAM_ONE.getMessage(),
                footBallService.getTeamById(team_id)
        );
    }

    @GetMapping("/player/{player_id}")
    public RsData<PlayerDetailResponse> getPlayers(@PathVariable Long player_id) {
        return new RsData<>(
                FootBallSuccessCode.GET_PLAYER_INFO.getCode(),
                FootBallSuccessCode.GET_PLAYER_INFO.getMessage(),
                footBallService.getPlayer(player_id)
        );
    }

    @GetMapping("/standing")
    public RsData<StandingResponse> getStanding() {
        return new RsData<>(
                FootBallSuccessCode.GET_STANDING.getCode(),
                FootBallSuccessCode.GET_STANDING.getMessage(),
                footBallService.getStanding()
        );
    }

    @GetMapping("/matches")
    public RsData<List<GameResponse>> getMatches() {
        return new RsData<>(
                "200",
                "성공",
                footBallService.getGameList()
        );
    }
}
