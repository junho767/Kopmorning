package com.personal.kopmorning.domain.football.controller;

import com.personal.kopmorning.domain.football.dto.response.*;
import com.personal.kopmorning.domain.football.responseCode.FootBallSuccessCode;
import com.personal.kopmorning.domain.football.service.FootBallService;
import com.personal.kopmorning.global.entity.RsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/football")
@RequiredArgsConstructor
public class FootBallController {
    private final FootBallService footBallService;

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
                FootBallSuccessCode.GET_GAME_LIST.getCode(),
                FootBallSuccessCode.GET_GAME_LIST.getMessage(),
                footBallService.getGameList()
        );
    }

    @GetMapping("/ranking/{standard}")
    public RsData<List<RankingResponse>> getRanking(@PathVariable String standard) {
        return new RsData<>(
                FootBallSuccessCode.GET_RANKING_LIST.getCode(),
                FootBallSuccessCode.GET_RANKING_LIST.getMessage(),
                footBallService.getRanking(standard)
        );
    }
}
